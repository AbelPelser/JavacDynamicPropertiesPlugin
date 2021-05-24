package org.banana.javacplugin;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.util.Context;
import org.banana.javacplugin.myplugin.MyJavacTaskListener;

public class JavacPlugin implements Plugin {

    public static final String NAME = "BananaPlugin";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void init(JavacTask task, String... args) {
        BasicJavacTask basicJavacTask = (BasicJavacTask) task;
        Context context = basicJavacTask.getContext();
        task.addTaskListener(new MyJavacTaskListener(context));
    }
}