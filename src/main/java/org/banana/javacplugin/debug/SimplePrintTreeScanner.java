package org.banana.javacplugin.debug;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;

import java.util.List;

public class SimplePrintTreeScanner extends TreeScanner<Void, List<JCTree>> {

    private final Log log;

    public SimplePrintTreeScanner(Context context) {
        log = Log.instance(context);
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnitTree, List<JCTree> unused) {
        log.printRawLines(compilationUnitTree.toString());
        return super.visitCompilationUnit(compilationUnitTree, unused);
    }
}
