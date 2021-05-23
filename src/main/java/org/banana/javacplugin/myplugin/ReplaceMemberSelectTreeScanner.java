package org.banana.javacplugin.myplugin;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class ReplaceMemberSelectTreeScanner extends AbstractBananaTreeScanner {

    public ReplaceMemberSelectTreeScanner(Context context) {
        super(context);
    }

    private void printSym(Symbol sym) {
        p("sym: " + sym);
        p("sym.completer: " + sym.completer);
        if (sym.completer != null) {
            p("sym.completer.className: " + sym.completer.getClass().getName());
        }
        p("sym.flags_field: " + sym.flags_field);
        try {
            p("sym.kind: " + sym.kind);
        } catch (NoSuchFieldError ignored) {
        }
        p("sym.name: " + sym.name);
        p("sym.owner: " + sym.owner);
        Type erField = sym.erasure_field;
        p("sym.erasure_field: " + erField);
        if (erField != null) {
            p("sym.erasure_field.tsym: " + erField.tsym);
        }
    }

    private void printExpression(JCTree.JCExpression selected) {
        p("selected: " + selected);
        p("selected.type: " + selected.type);
        if (selected.type != null) {
            p("selected.type.tsym: " + selected.type.tsym);
        }
        p("selected.pos: " + selected.pos);
    }

    private void printType(Type type) {
        p("type: " + type);
        p("type.class: " + type.getClass().getName());
        p("type.tsym: " + type.tsym);
    }

    private void printName(Name name) {
        p("name: " + name);
        p("name.table: " + name.table);
        if (name.table != null) {
            p("name.table.names: " + name.table.names);
        }
    }

    private void dumpMemberSelect(JCTree.JCFieldAccess fieldAccess) {
        Optional.ofNullable(fieldAccess.sym).ifPresent(this::printSym);
        Optional.ofNullable(fieldAccess.selected).ifPresent(this::printExpression);
        Optional.ofNullable(fieldAccess.type).ifPresent(this::printType);
        Optional.ofNullable(fieldAccess.name).ifPresent(this::printName);
        p("\n\n");
    }

    private JCTree getParentNode() {
        return (JCTree) getCurrentPath().getParentPath().getLeaf();
    }

    private JCTree.JCTypeCast createReplacementReadNode(JCTree.JCFieldAccess fieldAccess) {
        JCTree.JCExpression replacement = createMethodInvocation(createIdent(GET_METHOD), fieldAccess.selected, factory.Literal(fieldAccess.name.toString()));
        Type desiredType = getParentNode().type;
        if (desiredType == null) {
            throw new IllegalStateException("Could not determine required type!");
        }
        return factory.TypeCast(desiredType, replacement);
    }

    private void replaceFieldInParent(JCTree parent, Field parentField, JCTree originalNode, JCTree.JCTypeCast replacementNode) {
        try {
            if (parentField.get(parent) == originalNode) {
                parentField.set(parent, replacementNode);
            }
        } catch (IllegalAccessException ignore) {
            // Expected to happen, it's fine
        }
    }

    private void replaceFieldsInParent(JCTree parent, JCTree originalNode, JCTree.JCTypeCast replacementNode) {
        Arrays.stream(parent.getClass().getDeclaredFields())
                .forEach(field -> replaceFieldInParent(parent, field, originalNode, replacementNode));
    }

    @Override
    // Replace member select READs
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void unused) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) memberSelectTree;

        if (fieldAccess.type instanceof Type.ErrorType) {
            p("Found error in visitMemberSelect: " + memberSelectTree);
            JCTree.JCTypeCast replacementNode = createReplacementReadNode(fieldAccess);
            p("Replacement node " + replacementNode);
            replaceFieldsInParent(getParentNode(), fieldAccess, replacementNode);
            log.nerrors--;
        }
//        dumpMemberSelect(fieldAccess);
//        p("\n\n");
        return super.visitMemberSelect(memberSelectTree, unused);
    }

    @Override
    // Replace member select WRITEs
    public Void visitAssignment(AssignmentTree assignmentTree, Void unused) {
        p("Found assignment " + assignmentTree);
        // i.haha = 6;
        // (int) __setMonkeyPatchProperty(i, "haha", 6);
//        return super.visitAssignment(assignmentTree, unused);
        return null;
    }
}
