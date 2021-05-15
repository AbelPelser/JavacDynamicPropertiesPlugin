package org.banana.javacplugin.myplugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.util.Context;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class MyJavacTaskListener implements TaskListener {
    private final Context context;

    @Override
    public void started(TaskEvent e) {
    }

    @Override
    public void finished(TaskEvent e) {
        CompilationUnitTree compilationUnit = e.getCompilationUnit();
        if (e.getKind() == TaskEvent.Kind.PARSE) {
            /*
            compilationUnit.accept(new AddImportsTreeScanner(context), null);
            compilationUnit.accept(new AddMonkeyGetTreeScanner(context), null);
            compilationUnit.accept(new AddMonkeySetTreeScanner(context), null);
            compilationUnit.accept(new AddEnvironmentSetupTreeScanner(context), null);
            compilationUnit.accept(new SimplePrintTreeScanner(context), null);*/
            compilationUnit.accept(new PrintTreeScanner(context), null);
        } else if (e.getKind() == TaskEvent.Kind.ANALYZE) {
//            compilationUnit.accept(new SimplePrintTreeScanner(context), null);
        }
    }
}
