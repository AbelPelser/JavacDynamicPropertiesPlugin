package org.deepee.javacplugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

import static org.deepee.javacplugin.EnvironmentUtil.replaceEnvironment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeepeeJavacPluginIntegrationTest {
    private static final String PACKAGE_NAME = "org.deepee.javacplugin";
    private static final String CLASS_NAME = "Test";
    private static final String CLASS_TEMPLATE = "" +
            "package " + PACKAGE_NAME + ";\n" +
            "\n" +
            "public class " + CLASS_NAME + " {\n" +
            "%s" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"Hello world!\");\n" +
            "    }\n" +
            "}";
    private static final String VARARGS_METHOD = "" +
            "    public static int testVarArgs(int firstParam, int... varArgParams) {\n" +
            "        return varArgParams.length;\n" +
            "    }\n";
    private static final String TEST_METHOD_TEMPLATE = "    public static int test(String i) {\n%s\n}";

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
    public void shouldRunReadAndWrite() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = 6;\n" +
                "        int x = i.haha;\n" +
                "        return x;"
        ), 6);
    }

    @Test
    public void shouldRunOverwrite() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = 6;\n" +
                "        i.haha = 8;\n" +
                "        int x = i.haha;\n" +
                "        return x;"
        ), 8);
    }

    @Test
    public void shouldRunWriteComplexObject() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = new IllegalStateException(\"Test\");\n" +
                "        return 0;"
        ), 0);
    }

    @Test
    public void shouldRunAndNotModifyKnownProperties() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        System.out.println(12345);\n" +
                "        return 0;"
        ), 0);
    }

    @Test
    public void shouldThrowClassCastExceptionOnRead() {
        compileAndThrowClassCastException(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = true;\n" +
                "        int x = i.haha;\n" +
                "        return x;"
        ), Boolean.class.getName(), Integer.class.getName());
    }

    @Test
    public void shouldThrowClassCastExceptionOnReadInReturn() {
        compileAndThrowClassCastException(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = true;\n" +
                "        return i.haha;"
        ), Boolean.class.getName(), Integer.class.getName());
    }

    @Test
    public void shouldRunReturn() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = 6;\n" +
                "        return i.haha;"
        ), 6);
    }

    @Test
    public void shouldRunReadAndWriteInBlock() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        {\n" +
                "            i.haha = 6;\n" +
                "            int x = i.haha;\n" +
                "            return x;\n" +
                "        }"
        ), 6);
    }

    @Test
    public void shouldRunReturnInBlock() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        {\n" +
                "            i.haha = 6;\n" +
                "            return i.haha;\n" +
                "        }"
        ), 6);
    }

    @Test
    public void shouldRunChainedProperties() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = 6;\n" +
                "        i.hihi = i.haha;\n" +
                "        int x = i.hihi;\n" +
                "        return x;"
        ), 6);
    }

    @Test
    public void shouldRunChainedPropertiesReturn() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = 6;\n" +
                "        i.hihi = i.haha;\n" +
                "        return i.hihi;"
        ), 6);
    }

    @Test
    public void shouldRunPassingParams() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        if (i.equals(\"fifi\")) return 0;\n" +
                "        i.haha = \"fifi\";\n" +
                "        return test(i.haha);\n"
        ), 0);
    }

    @Test
    public void shouldRunNestedProperties() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.hihi = 7;\n" +
                "        i.hihi.haha = 6;\n" +
                "        int x = i.hihi.haha;\n" +
                "        return x;"
        ), 6);
    }

    @Test
    public void shouldRunNestedPropertiesReturn() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.hihi = 7;\n" +
                "        i.hihi.haha = 6;\n" +
                "        return i.hihi.haha;"
        ), 6);
    }

    @Test
    public void shouldRunVarargs() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = 67;\n" +
                "        return testVarArgs(6, i.haha);"
        ) + VARARGS_METHOD, 1);
    }

    @Test
    public void shouldRunNonVarargInMethodThatTakesVarargs() {
        compileAndRunSuccessfully(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.haha = 67;\n" +
                "        return testVarArgs(i.haha);"
        ) + VARARGS_METHOD, 0);
    }

    @Test
    public void shouldThrowForUnsetProperties() {
        compileAndThrowReadUnknownProperty(String.format(TEST_METHOD_TEMPLATE, "" +
                "return i.hihi;"), "hihi");
    }

    @Test
    public void shouldThrowForUnsetPropertiesInBlock() {
        compileAndThrowReadUnknownProperty(String.format(TEST_METHOD_TEMPLATE, "" +
                "{return i.hihi;}"), "hihi");
    }

    @Test
    public void shouldThrowForUnsetNestedPropertiesInReturn() {
        compileAndThrowReadUnknownProperty(String.format(TEST_METHOD_TEMPLATE, "" +
                "return i.hihi.haha;"), "hihi");
    }

    @Test
    public void shouldThrowForUnsetNestedProperties() {
        compileAndThrowReadUnknownProperty(String.format(TEST_METHOD_TEMPLATE, "" +
                "        i.hoho.haha = 6;\n" +
                "        return 0;"
        ), "hoho");
    }

    private void compileAndThrowClassCastException(String code, String fromClass, String toClass) {
        ClassCastException thrown = assertThrows(ClassCastException.class, () -> compileAndRun(code));
        assertEquals(thrown.getMessage(), String.format("%s cannot be cast to %s", fromClass, toClass));
    }

    private void compileAndThrowReadUnknownProperty(String code, String unknownPropertyName) {
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> compileAndRun(code));
        assertEquals(thrown.getMessage(), String.format("Attempting to read unknown property %s!", unknownPropertyName));
    }

    private void compileAndRunSuccessfully(String code, int expected) {
        assertEquals(expected, compileAndRun(code));
    }

    private int compileAndRun(String code) {
        String qualifiedClassName = PACKAGE_NAME + "." + CLASS_NAME;
        code = String.format(CLASS_TEMPLATE, code);
        byte[] byteCode = compiler.compile(qualifiedClassName, code);
        return (int) runner.runMethodInClass(byteCode, qualifiedClassName, "test", new Class[]{String.class}, "A");
    }
}