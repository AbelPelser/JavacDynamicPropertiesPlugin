package org.banana.javacplugin.myplugin;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import static org.banana.javacplugin.util.TreeMakerUtil.javacList;

public class ReplaceIllegalMemberSelectTreeScanner extends AbstractAddMonkeyGetSetTreeScanner {
    public ReplaceIllegalMemberSelectTreeScanner(Context context) {
        super(context);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void unused) {

        return super.visitMemberSelect(memberSelectTree, unused);
    }

    @Override
    public Void visitMemberReference(MemberReferenceTree memberReferenceTree, Void unused) {
        return super.visitMemberReference(memberReferenceTree, unused);
    }
}
