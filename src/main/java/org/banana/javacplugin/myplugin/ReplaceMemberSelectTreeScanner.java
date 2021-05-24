package org.banana.javacplugin.myplugin;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.banana.javacplugin.util.ListUtil.javacList;

public class ReplaceMemberSelectTreeScanner extends AbstractBananaTreeScanner {
    public ReplaceMemberSelectTreeScanner(Context context) {
        super(context);
    }

    private JCTree getParentNode() {
        return (JCTree) getParentPath().getLeaf();
    }

    private TreePath getParentPath() {
        return getCurrentPath().getParentPath();
    }

    private JCTree.JCLiteral convertNameToStringParam(Name name) {
        return factory.Literal(name.toString());
    }

    private JCTree.JCMethodInvocation createGetInvocation(JCTree.JCFieldAccess fieldAccess) {
        return createMethodInvocation(
                createIdent(GET_METHOD),
                fieldAccess.selected,
                convertNameToStringParam(fieldAccess.name)
        );
    }

    private JCTree.JCExpression createCastIfPossible(JCTree.JCExpression expr, Type type) {
        if (type == null || type instanceof Type.ErrorType) {
            p("Not casting, could not determine required type!");
            return expr;
        }
        return factory.TypeCast(type, expr);
    }

    private Type convertTypeToReturnType(Type type) {
        if (type instanceof Type.MethodType) {
            return ((Type.MethodType) type).restype;
        }
        return type;
    }

    private Type findFirstTypeInHierarchy(TreePath tree) {
        Type type;
        do {
            type = ((JCTree) tree.getLeaf()).type;
            tree = tree.getParentPath();
        } while (type == null || type instanceof Type.ErrorType);
        type = convertTypeToReturnType(type);
        p("Returning type " + type + " of " + tree.getLeaf());
        return type;
    }

    private Type getCastTypeForNode(JCTree tree) {
        if (tree instanceof JCTree.JCFieldAccess) {
            JCTree parent = getParentNode();
            if (parent instanceof JCTree.JCReturn) {
                return findFirstTypeInHierarchy(getParentPath());
            } else if (parent instanceof JCTree.JCMethodInvocation) {
                return findTypeOfPassedParameter((JCTree.JCMethodInvocation) parent, tree);
            }
            return parent.type;
        } else if (tree instanceof JCTree.JCAssign) {
            return ((JCTree.JCAssign) tree).rhs.type;
        }
        return null;
    }

    // We are replacing an expression of a passed parameter, and need to cast appropriately
    private Type findTypeOfPassedParameter(JCTree.JCMethodInvocation parent, JCTree param) {
        int paramIndex = parent.args.indexOf(param);
        if (paramIndex < 0) {
            throw new IllegalStateException("Param " + param + " not found in parameter list of " + parent);
        }
        return Optional.of(parent.meth)
                .map(p -> (JCTree.JCIdent) p)
                .map(i -> i.sym)
                .map(s -> (Symbol.MethodSymbol) s)
                .map(s -> s.type)
                .map(t -> (Type.MethodType) t)
                .map(t -> t.argtypes)
                .map(a -> a.get(paramIndex))
                .orElse(null);
    }

    private JCTree.JCExpression createReplacementReadNode(JCTree.JCFieldAccess fieldAccess) {
        return createCastIfPossible(createGetInvocation(fieldAccess), getCastTypeForNode(fieldAccess));
    }

    private boolean replaceFieldListInParent(JCTree parent, Field parentField, JCTree originalNode, JCTree.JCExpression replacementNode) throws IllegalAccessException {
        boolean changed = false;
        java.util.List<Object> newFieldList = new ArrayList<>();
        for (Object o : (List<?>) parentField.get(parent)) {
            if (o == originalNode) {
                newFieldList.add(replacementNode);
                changed = true;
            } else {
                newFieldList.add(o);
            }
        }
        if (changed) {
            parentField.set(parent, javacList(newFieldList));
        }
        return changed;
    }

    private boolean replaceFieldValueInParent(JCTree parent, Field parentField, JCTree originalNode, JCTree.JCExpression replacementNode) throws IllegalAccessException {
        if (parentField.get(parent) == originalNode) {
            parentField.set(parent, replacementNode);
            return true;
        }
        return false;
    }

    private boolean replaceFieldInParent(JCTree parent, Field parentField, JCTree originalNode, JCTree.JCExpression replacementNode) {
        try {
            if (List.class.isAssignableFrom(parentField.getType())) {
                return replaceFieldListInParent(parent, parentField, originalNode, replacementNode);
            }
            return replaceFieldValueInParent(parent, parentField, originalNode, replacementNode);
        } catch (IllegalAccessException ignore) {
            return false;
        }
    }

    private void replacementCurrentNodeInParent(JCTree originalNode, JCTree.JCExpression replacementNode) {
        JCTree parent = getParentNode();
        Field[] fields = parent.getClass().getDeclaredFields();
        long nChanged = Arrays.stream(fields)
                .map(field -> replaceFieldInParent(parent, field, originalNode, replacementNode))
                .filter(b -> b)
                .count();
        if (nChanged != 1) {
            if (nChanged == 0) {
                p("Could not replace node in parent!");
            } else {
                p("Replaced multiple nodes in parent!");
            }
            p("Parent: " + parent);
            p("Original: " + originalNode);
            p("Intended replacement: " + replacementNode);
        }
        replaceCurrentPath(replacementNode);
    }

    private void replaceCurrentPath(JCTree.JCExpression newCurrentNode) {
        TreePath parentPath = getCurrentPath().getParentPath();
        setPath(new TreePath(parentPath, newCurrentNode));
    }

    private Void visitReplacementNode(JCTree.JCExpression replacementNode, Void unused) {
        if (replacementNode instanceof TypeCastTree) {
            p("Calling visitTypeCast");
            return super.visitTypeCast((TypeCastTree) replacementNode, unused);
        }
        p("Calling visitMethodInvocation");
        return super.visitMethodInvocation((MethodInvocationTree) replacementNode, unused);
    }

    @Override
    // Replace member select READs
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void unused) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) memberSelectTree;

        if (fieldAccess.type instanceof Type.ErrorType) {
            p("\nFound error in visitMemberSelect: " + memberSelectTree + " (parent " + getParentNode() + ")");
            JCTree.JCExpression replacementNode = createReplacementReadNode(fieldAccess);
            p("Replacement node = " + replacementNode);
            replacementCurrentNodeInParent(fieldAccess, replacementNode);
            log.nerrors--;
            return visitReplacementNode(replacementNode, unused);
        }
        return super.visitMemberSelect(memberSelectTree, unused);
    }

    private JCTree.JCMethodInvocation createSetInvocation(JCTree.JCFieldAccess fieldAccess, JCTree.JCExpression value) {
        return createMethodInvocation(
                createIdent(SET_METHOD),
                fieldAccess.selected,
                convertNameToStringParam(fieldAccess.name),
                value
        );
    }

    private JCTree.JCExpression createReplacementWriteNode(JCTree.JCAssign assign) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) assign.lhs;
        JCTree.JCExpression setInvocation = createSetInvocation(fieldAccess, assign.rhs);
        return createCastIfPossible(setInvocation, getCastTypeForNode(assign));
    }

    @Override
    // Replace member select WRITEs
    public Void visitAssignment(AssignmentTree assignmentTree, Void unused) {
        JCTree.JCAssign assign = (JCTree.JCAssign) assignmentTree;
        if (assign.lhs instanceof JCTree.JCFieldAccess && assign.type instanceof Type.ErrorType) {
            p("\nFound error in visitAssignment: " + assign + " (parent " + getParentNode() + ")");
            JCTree.JCExpression replacementNode = createReplacementWriteNode(assign);
            p("Replacement node = " + replacementNode);
            replacementCurrentNodeInParent(assign, replacementNode);
            log.nerrors--;
            return visitReplacementNode(replacementNode, unused);
        }
        return super.visitAssignment(assignmentTree, unused);
    }
}
