package org.banana.javacplugin.myplugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.util.Context;


public class MyJavacTaskListener implements TaskListener {

    private final Context context;

    public MyJavacTaskListener(Context context) {
        this.context = context;
    }

    @Override
    public void started(TaskEvent e) {
    }

    @Override
    public void finished(TaskEvent e) {
        if (e.getKind() != TaskEvent.Kind.PARSE) {
            return;
        }
        CompilationUnitTree compilationUnit = e.getCompilationUnit();
        compilationUnit.accept(new AddImportsTreeScanner(context), null);
        compilationUnit.accept(new AddMonkeyGetTreeScanner(context), null);
        compilationUnit.accept(new AddMonkeySetTreeScanner(context), null);
        compilationUnit.accept(new AddEnvironmentSetupTreeScanner(context), null);
//		compilationUnit.accept(new PrintTreeScanner(context), null);
        compilationUnit.accept(new SimplePrintTreeScanner(context), null);
    }
}
