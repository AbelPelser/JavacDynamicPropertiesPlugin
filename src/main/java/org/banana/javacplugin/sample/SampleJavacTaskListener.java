package org.banana.javacplugin.sample;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.util.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleJavacTaskListener implements TaskListener {

    private final Context context;

    @Override
    public void started(TaskEvent e) {
    }

    @Override
    public void finished(TaskEvent e) {
        if (e.getKind() != TaskEvent.Kind.PARSE) {
            return;
        }
        e.getCompilationUnit().accept(new SampleTreeScanner(context), null);
    }
}
