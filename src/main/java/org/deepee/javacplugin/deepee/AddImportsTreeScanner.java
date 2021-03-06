package org.deepee.javacplugin.deepee;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import static org.deepee.javacplugin.util.ListUtil.javacList;

public class AddImportsTreeScanner extends AbstractCustomTreeScanner {
    public AddImportsTreeScanner(Context context) {
        super(context);
    }

    private List<JCTree> getImports() {
        return javacList(
                createImport(JAVA_LANG_REFLECT, FIELD),
                createImport(JAVA_LANG_REFLECT, MODIFIER),
                createImport(JAVA_UTIL, MAP),
                createImport(JAVA_UTIL, HASH_MAP),
                createImport(JAVA_UTIL, COLLECTIONS)
        );
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnitTree, Void unused) {
        if (compilationUnitTree instanceof JCTree.JCCompilationUnit) {
            JCTree.JCCompilationUnit jcCompilationUnit = (JCTree.JCCompilationUnit) compilationUnitTree;
            List<JCTree> defs = jcCompilationUnit.defs;
            List<JCTree> imports = getImports();
            // Ensure the package statement remains first
            if (defs.isEmpty() || defs.head instanceof JCTree.JCImport || defs.head instanceof JCTree.JCClassDecl) {
                jcCompilationUnit.defs = defs.prependList(imports);
            } else {
                jcCompilationUnit.defs = defs.tail.prependList(imports).prepend(defs.head);
            }
        }
        return super.visitCompilationUnit(compilationUnitTree, unused);
    }
}
