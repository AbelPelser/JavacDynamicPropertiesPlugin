package org.banana.javacplugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

import static org.banana.javacplugin.EnvironmentUtil.replaceEnvironment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JavacPluginIntegrationTest {
    private static final String PACKAGE_NAME = "org.banana.javacplugin";
    private static final String CLASS_NAME = "Test";
    private static final String CLASS_TEMPLATE =
            "package org.banana.javacplugin;\n" +
                    "\n" +
                    "public class " + CLASS_NAME + " {\n" +
                    "    public static int testVarArgs(int firstParam, int... varArgParams) {\n" +
                    "        return varArgParams.length;\n" +
                    "    }\n" +
                    "    public static int test(String i) {\n%s\n}" +
                    "    public static void main(String[] args) {\n" +
                    "        System.out.println(\"Hello world!\");\n" +
                    "    }\n" +
                    "}";

    private final TestCompiler compiler = new TestCompiler();
    private final TestRunner runner = new TestRunner();
    private static Map<String, String> originalEnvironment;

    @BeforeAll
    public static void storeOriginalEnvironment() {
        originalEnvironment = System.getenv();
    }

    @BeforeEach
    public void resetEnvironment() {
        replaceEnvironment(originalEnvironment);
    }

    @Test
    public void shouldCompileBasicProperties() {
        compileAndRun(String.format(CLASS_TEMPLATE, "" +
                "        i.haha = 6;\n" +
                "        int x = i.haha;\n" +
                "        return x;"
        ));
    }

    @Test
    public void shouldCompileChainedProperties() {
        compileAndRun(String.format(CLASS_TEMPLATE, "" +
                "        i.haha = 6;\n" +
                "        i.hihi = i.haha;\n" +
                "        int x = i.hihi;\n" +
                "        return x;"
        ));
    }

    @Test
    public void shouldCompilePassingParams() {
        compileAndRun(String.format(CLASS_TEMPLATE, "" +
                "        if (i.equals(\"fifi\")) return 0;\n" +
                "        i.haha = \"fifi\";\n" +
                "        return test(i.haha);\n"
        ));
    }

    @Test
    public void shouldCompileNestedProperties() {
        compileAndRun(String.format(CLASS_TEMPLATE, "" +
                "        i.hihi = 7;\n" +
                "        i.hihi.haha = 6;\n" +
                "        int x = i.hihi.haha;\n" +
                "        return x;"
        ));
    }

    @Test
    public void shouldCompileVarargs() {
        compileAndRun(String.format(CLASS_TEMPLATE, "" +
                "        i.haha = 67;\n" +
                "        return testVarArgs(6, i.haha);\n"
        ));
    }

    @Test
    public void shouldCompileNonVarargInMethodThatTakesVarargs() {
        compileAndRun(String.format(CLASS_TEMPLATE, "" +
                "        i.haha = 67;\n" +
                "        return testVarArgs(i.haha);\n"
        ));
    }

    @Test
    public void shouldThrowForUnsetProperties() {
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class, () ->
                        compileAndRun(String.format(CLASS_TEMPLATE, "        return i.hihi;"))
        );
        assertEquals(thrown.getMessage(), "Attempting to read unknown property hihi!");
    }

    @Test
    public void shouldThrowForUnsetPropertiesInBlock() {
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class, () ->
                        compileAndRun(String.format(CLASS_TEMPLATE, "        {return i.hihi;}"))
        );
        assertEquals(thrown.getMessage(), "Attempting to read unknown property hihi!");
    }

     @Test
    public void shouldThrowForUnsetNestedPropertiesInReturn() {
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class, () ->
                        compileAndRun(String.format(CLASS_TEMPLATE, "        return i.hihi.haha;"))
        );
        assertEquals(thrown.getMessage(), "Attempting to read unknown property hihi!");
    }

    @Test
    public void shouldThrowForUnsetNestedProperties() {
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class, () ->
                        compileAndRun(String.format(CLASS_TEMPLATE, "" +
                                "        i.hihi.haha = 6;\n" +
                                "        int x = i.hihi.haha;\n" +
                                "        return x;"
                        ))
        );
        assertEquals(thrown.getMessage(), "Attempting to read unknown property hihi!");
    }

    private Object compileAndRun(String code) {
        String qualifiedClassName = PACKAGE_NAME + "." + CLASS_NAME;
        byte[] byteCode = compiler.compile(qualifiedClassName, code);
        return runner.runMethodInClass(byteCode, qualifiedClassName, "test", new Class[]{String.class}, "A");
    }
}