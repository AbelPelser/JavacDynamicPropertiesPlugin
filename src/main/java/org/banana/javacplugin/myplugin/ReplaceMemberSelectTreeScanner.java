package org.banana.javacplugin.myplugin;

import com.sun.source.tree.MemberSelectTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

import java.util.List;
import java.util.Optional;

public class ReplaceMemberSelectTreeScanner extends AbstractBananaTreeScanner {
    private final Enter enter;

    public ReplaceMemberSelectTreeScanner(Context context) {
        super(context);
        enter = Enter.instance(context);
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

    private JCTree.JCTypeCast createReplacementNode(JCTree.JCFieldAccess fieldAccess) {
        JCTree.JCExpression replacement = createMethodInvocation(createIdent(GET_METHOD), fieldAccess.selected, factory.Literal(fieldAccess.name.toString()));
        Type desiredType = getParentNode().type;
        if (desiredType == null) {
            throw new IllegalStateException("Could not determine required type!");
        }
        JCTree.JCTypeCast replacementNode = factory.TypeCast(desiredType, replacement);
//        replacementNode.accept(enter);
        return replacementNode;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, List<JCTree> unused) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) memberSelectTree;

        if (fieldAccess.type instanceof Type.ErrorType) {
            p("Found error in visitMemberSelect: " + memberSelectTree);
            JCTree.JCTypeCast replacementNode = createReplacementNode(fieldAccess);
            unused.add(replacementNode);
            p("Replacement node " + replacementNode);
            ((JCTree.JCVariableDecl) getParentNode()).init = replacementNode;
            log.nerrors--;
        }
//        dumpMemberSelect(fieldAccess);
//        p("\n\n");
        return super.visitMemberSelect(memberSelectTree, unused);
    }
}
