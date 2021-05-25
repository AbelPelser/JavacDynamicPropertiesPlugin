package org.deepee.javacplugin.deepee;

import com.sun.source.tree.AssignmentTree;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public class ReplaceAssignmentTreeScanner extends AbstractReplaceTreeScanner {
    public ReplaceAssignmentTreeScanner(Context context) {
        super(context);
    }

    private JCTree.JCMethodInvocation createSetInvocation(JCTree.JCFieldAccess fieldAccess, JCTree.JCExpression value) {
        return createMethodInvocation(
                createIdent(SET_PROPERTY_METHOD),
                fieldAccess.selected,
                convertNameToStringParam(fieldAccess.name),
                value
        );
    }

    private Type getCastTypeForAssign(JCTree.JCAssign jcAssign) {
        Type rhsType = jcAssign.rhs.type;
        if (rhsType == null || rhsType instanceof Type.ErrorType) {
            return getCastTypeFromOuterContext(jcAssign);
        }
        return rhsType;
    }

    private JCTree.JCExpression createReplacementWriteNode(JCTree.JCAssign assign) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) assign.lhs;
        JCTree.JCExpression setInvocation = createSetInvocation(fieldAccess, assign.rhs);
        return createCastIfPossible(setInvocation, getCastTypeForAssign(assign));
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
