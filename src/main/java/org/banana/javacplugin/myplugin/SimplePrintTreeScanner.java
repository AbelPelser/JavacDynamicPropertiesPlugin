package org.banana.javacplugin.myplugin;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;

public class SimplePrintTreeScanner extends TreeScanner<Void, Void> {

    private final Log log;

    public SimplePrintTreeScanner(Context context) {
        log = Log.instance(context);

    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnitTree, Void unused) {
        log.printRawLines(compilationUnitTree.toString());
        return super.visitCompilationUnit(compilationUnitTree, unused);
    }
}
