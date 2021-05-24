package org.banana.javacplugin.myplugin;

import com.sun.source.tree.MemberSelectTree;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public class ReplaceMemberSelectTreeScanner extends AbstractReplaceTreeScanner {
    public ReplaceMemberSelectTreeScanner(Context context) {
        super(context);
    }

    private JCTree.JCMethodInvocation createGetInvocation(JCTree.JCFieldAccess fieldAccess) {
        return createMethodInvocation(
                createIdent(GET_METHOD),
                fieldAccess.selected,
                convertNameToStringParam(fieldAccess.name)
        );
    }

    private JCTree.JCExpression createReplacementReadNode(JCTree.JCFieldAccess fieldAccess) {
        return createCastIfPossible(createGetInvocation(fieldAccess), getCastTypeForNode(fieldAccess));
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
}
