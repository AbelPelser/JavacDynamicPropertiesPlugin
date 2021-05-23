package org.banana.javacplugin;

import org.junit.Test;

public class JavacPluginIntegrationTest {
    private static final String PACKAGE_NAME = "org.banana.javacplugin";
    private static final String CLASS_NAME = "Test";
    private static final String CLASS_TEMPLATE =
            "package org.banana.javacplugin;\n" +
                    "\n" +
                    "public class " + CLASS_NAME +  " {\n" +
                    "    public static int test(String i) {\n" +
                    "        i.haha = 6;\n" +
                    "        int x = i.haha;\n" +
                    "        return x;\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void main(String[] args) {\n" +
                    "        System.out.println(\"Hello world!\");\n" +
                    "    }\n" +
                    "}";

    private final TestCompiler compiler = new TestCompiler();
    private final TestRunner runner = new TestRunner();

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotCompile() {
        compileAndRun();
    }

    private Object compileAndRun() {
        String qualifiedClassName = PACKAGE_NAME + "." + CLASS_NAME;
        byte[] byteCode = compiler.compile(qualifiedClassName, CLASS_TEMPLATE);
        return runner.runMethodInClass(byteCode, qualifiedClassName, "test", new Class[]{String.class}, "A");
    }
}