package org.banana.javacplugin.myplugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Check;
import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import org.banana.javacplugin.debug.SimplePrintTreeScanner;

import java.util.ArrayList;
import java.util.List;

import static org.banana.javacplugin.util.ListUtil.javacList;


public class MyJavacTaskListener implements TaskListener {
    private final Context context;
    private final Log log;
    private final Attr attr;
    private final Todo todo;
    private final Check chk;
    private final JavaCompiler javaCompiler;

    private final ReplaceMemberSelectTreeScanner replaceMemberSelectTreeScanner;
    private final SimplePrintTreeScanner simplePrintTreeScanner;

    public MyJavacTaskListener(Context context) {
        this.context = context;
        log = Log.instance(context);
        attr = Attr.instance(context);
        todo = Todo.instance(context);
        chk = Check.instance(context);
        javaCompiler = JavaCompiler.instance(context);
        replaceMemberSelectTreeScanner = new ReplaceMemberSelectTreeScanner(context);
        simplePrintTreeScanner = new SimplePrintTreeScanner(context);
    }

    private void runTree(CompilationUnitTree compilationUnit, TreeScanner<?, ?> treeScanner) {
//        compilationUnit.accept(treeScanner, null);
        treeScanner.scan(compilationUnit, null);
    }

    @Override
    public void started(TaskEvent e) {
        log.printRawLines("STARTED " + e.getKind());
    }

    private void addMonkeyPatchCode(JCTree.JCCompilationUnit jcCompilationUnit) {
        runTree(jcCompilationUnit, new AddMonkeyGetTreeScanner(context));
        runTree(jcCompilationUnit, new AddMonkeySetTreeScanner(context));
        runTree(jcCompilationUnit, new AddEnvironmentSetupTreeScanner(context));
        runTree(jcCompilationUnit, new AddImportsTreeScanner(context));
    }

    @Override
    public void finished(TaskEvent e) {
        JCTree.JCCompilationUnit jcCompilationUnit = (JCTree.JCCompilationUnit) e.getCompilationUnit();
        log.printRawLines("FINISHED " + e.getKind());
        if (e.getKind() == TaskEvent.Kind.PARSE) {
            log.printRawLines("PARSE finished, adding monkey patch code!");
            addMonkeyPatchCode(jcCompilationUnit);
            List<JCTree.JCCompilationUnit> results = javaCompiler.enterTrees(javacList(jcCompilationUnit));
            attr.attrib(todo.remove());
            JCTree.JCCompilationUnit result = results.stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No results obtained from enterTrees"));
            runTree(result, replaceMemberSelectTreeScanner);
            chk.compiled.clear();
        }
    }
}
