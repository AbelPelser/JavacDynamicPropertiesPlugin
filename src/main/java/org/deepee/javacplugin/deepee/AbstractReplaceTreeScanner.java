package org.deepee.javacplugin.deepee;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Flags;
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

import static org.deepee.javacplugin.util.ListUtil.javacList;

public class AbstractReplaceTreeScanner extends AbstractCustomTreeScanner {

    public AbstractReplaceTreeScanner(Context context) {
        super(context);
    }

    protected JCTree getParentNode() {
        return (JCTree) getParentPath().getLeaf();
    }

    private TreePath getParentPath() {
        return getCurrentPath().getParentPath();
    }

    protected JCTree.JCLiteral convertNameToStringParam(Name name) {
        return factory.Literal(name.toString());
    }

    protected JCTree.JCExpression createCastIfPossible(JCTree.JCExpression expr, Type type) {
        if (type == null || type instanceof Type.ErrorType) {
            p("Not casting, could not determine required type!");
            return expr;
        }
        return factory.TypeCast(type, expr);
    }

    private Type findReturnTypeOfEnclosingMethod(TreePath treePath) {
        while (!(treePath.getLeaf() instanceof JCTree.JCMethodDecl)) {
            treePath = treePath.getParentPath();
        }
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) treePath.getLeaf();
        return methodDecl.restype.type;
    }

    protected Type getCastTypeForNode(JCTree tree) {
        if (tree instanceof JCTree.JCFieldAccess) {
            JCTree parent = getParentNode();
            if (parent instanceof JCTree.JCReturn) {
                return findReturnTypeOfEnclosingMethod(getParentPath());
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
                .map(s -> findTypeOfPassedParameter(s, paramIndex))
                .orElse(null);
    }

    private boolean paramIsVararg(Symbol.MethodSymbol sym, List<Type> argTypes, int paramIndex) {
        if ((sym.flags_field & Flags.VARARGS) != 0) {
            return paramIndex >= argTypes.size() - 1;
        }
        return false;
    }

    private Type findTypeOfPassedParameter(Symbol.MethodSymbol sym, int paramIndex) {
        Type.MethodType methodType = (Type.MethodType) sym.type;
        if (paramIsVararg(sym, methodType.argtypes, paramIndex)) {
            return getVarArgsType(methodType.argtypes);
        } else if (paramIndex < methodType.argtypes.size()) {
            return methodType.argtypes.get(paramIndex);
        }
        p("Could not find type of parameter #" + paramIndex + " in method " + sym);
        return null;
    }

    private Type getVarArgsType(List<Type> argTypes) {
        Type varArgsType = argTypes.last();
        if (varArgsType instanceof Type.ArrayType) {
            return ((Type.ArrayType) varArgsType).elemtype;
        }
        p("Attempted to find varArgType in parameter list " + argTypes + " but couldn't");
        return null;
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

    protected void replacementCurrentNodeInParent(JCTree originalNode, JCTree.JCExpression replacementNode) {
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

    protected Void visitReplacementNode(JCTree.JCExpression replacementNode, Void unused) {
        if (replacementNode instanceof TypeCastTree) {
            p("Calling visitTypeCast");
            return super.visitTypeCast((TypeCastTree) replacementNode, unused);
        }
        p("Calling visitMethodInvocation");
        return super.visitMethodInvocation((MethodInvocationTree) replacementNode, unused);
    }
}
