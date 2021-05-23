package org.banana.javacplugin.myplugin;

import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.util.Context;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReplaceIllegalMemberSelectTreeScanner extends AbstractAddMonkeyGetSetTreeScanner {
    private JavacTypes javacTypes;
    private Attr attr;
    private JavacElements javacElements;
    private JavacProcessingEnvironment processingEnvironment;
    private Symtab symtab;
    private TreeCopier<Void> treeCopier;
    private Env<AttrContext> env;


    public ReplaceIllegalMemberSelectTreeScanner(Context context) {
        super(context);
        javacTypes = JavacTypes.instance(context);
        attr = Attr.instance(context);
        processingEnvironment = JavacProcessingEnvironment.instance(context);
        javacElements = JavacElements.instance(context);
        symtab = Symtab.instance(context);
        treeCopier = new TreeCopier<>(factory);
//        Iterator<Env<AttrContext>> iter = Todo.instance(context).iterator();
//        if (iter.hasNext()) {
//            env = Todo.instance(context).iterator().next();
//        }
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, List<JCTree> unused) {
        p(memberSelectTree);
        if (memberSelectTree instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) memberSelectTree;
        }
        return super.visitMemberSelect(memberSelectTree, unused);
    }


    @Override
    public Void visitMemberReference(MemberReferenceTree memberReferenceTree, List<JCTree> unused) {
        // TODO: Method references (Class::method)
        return super.visitMemberReference(memberReferenceTree, unused);
    }
}
