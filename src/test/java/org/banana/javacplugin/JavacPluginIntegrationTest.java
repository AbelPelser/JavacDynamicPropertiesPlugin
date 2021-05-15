package org.banana.javacplugin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JavacPluginIntegrationTest {

    private static final String CLASS_TEMPLATE =
            "package org.banana.javacplugin;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "public class Test {\n" +
                    "    private static Map test() {\n" +
                    "        Map<String, String> oldMap = System.getenv();\n" +
                    "        return oldMap;\n" +
                    "    }\n" +
                    "    public static %1$s service(%1$s i) {\n" +
                    "        return i + test().size() * 0;\n" +
                    "    }\n" +
                    "}\n";

    private final TestCompiler compiler = new TestCompiler();
    private final TestRunner runner = new TestRunner();

    @Test(expected = IllegalArgumentException.class)
    public void givenInt_whenNegative_thenThrowsException() {
        compileAndRun(double.class, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenInt_whenZero_thenThrowsException() {
        compileAndRun(int.class, 0);
    }

    @Test
    public void givenInt_whenPositive_thenSuccess() {
        assertEquals(1, compileAndRun(int.class, 1));
    }

    private Object compileAndRun(Class<?> argumentType, Object argument) {
        String qualifiedClassName = "org.banana.javacplugin.Test";
        byte[] byteCode = compiler.compile(qualifiedClassName, String.format(CLASS_TEMPLATE, argumentType.getName()));
        return runner.runMethodInClass(byteCode, qualifiedClassName, "service", new Class[]{argumentType}, argument);
    }
}