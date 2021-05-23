package org.banana.javacplugin;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;

public class TestCompiler {
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    private static List<String> getJavacArguments() {
        return new ArrayList<>(
            asList("-classpath", System.getProperty("java.class.path"), "-Xplugin:" + JavacPlugin.NAME));
    }

    private SimpleFileManager getFileManager() {
        return new SimpleFileManager(COMPILER.getStandardFileManager(null, null, null));
    }

    private List<SimpleSourceFile> getCompilationUnits(String qualifiedClassName, String testSource) {
        return singletonList(new SimpleSourceFile(qualifiedClassName, testSource));
    }

    private CompilationTask getCompilerTask(SimpleFileManager fileManager, StringWriter output, List<SimpleSourceFile> compilationUnits) {
        return COMPILER.getTask(output, fileManager, null, getJavacArguments(), null, compilationUnits);
    }

    public byte[] compile(String qualifiedClassName, String testSource) {
        SimpleFileManager fileManager = getFileManager();
        List<SimpleSourceFile> compilationUnits = getCompilationUnits(qualifiedClassName, testSource);
        StringWriter output = new StringWriter();
        CompilationTask task = getCompilerTask(fileManager, output, compilationUnits);
        task.call();
        System.err.print(output);
        return fileManager.getCompiled().get(0).getCompiledBinaries();
    }
}