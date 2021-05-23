package org.banana.javacplugin.util;


import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Tmp {
    private static Map<Object, Object> __monkeyMap;

    static {
        __setupEnvironment();
    }

    private static void __setupEnvironment() {
        try {
            Map<String, String> oldMap = System.getenv();
            Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
            Field unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment");
            unmodifiableMapField.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(unmodifiableMapField, unmodifiableMapField.getModifiers() & ~Modifier.FINAL);
            __monkeyMap = Collections.synchronizedMap(new HashMap<>(oldMap));
            unmodifiableMapField.set(null, __monkeyMap);
        } catch (Exception ignore) {
        }
    }

    private static <T> T __setMonkeyPatchProperty(Object object, String propName, T value) {
        int id = object.hashCode();
        if (!__monkeyMap.containsKey(id)) {
            __monkeyMap.put(id, new HashMap<String, Object>());
        }
        Map<String, Object> propMap = (Map<String, Object>) __monkeyMap.get(id);
        propMap.put(propName, value);
        return value;
    }

    private static <T> T __getMonkeyPatchProperty(Object object, String propName) {
        int id = object.hashCode();
        if (__monkeyMap.containsKey(id)) {
            Map<String, Object> propMap = (Map<String, Object>) __monkeyMap.get(id);
            if (propMap.containsKey(propName)) {
                return (T) propMap.get(propName);
            }
        }
        throw new IllegalStateException("Attempting to read unknown property " + (propName + "!"));
    }

    private static <T> T __getProperty(Object object, String propName) {
        try {
            Field field = object.getClass().getDeclaredField(propName);
            return (T) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return __getMonkeyPatchProperty(object, propName);
        }
    }

    private static void test(Map<Object, String> inputParam) {
        System.out.println(inputParam.size());
    }

    private static int test2(PrintStream y) {
        y.println("Hi");
        return 2;
    }

    public static void main(String[] args) {
        String s = "a";
        __setMonkeyPatchProperty(s, "propName", 3);
        //System.out.println(__getProperty(s, "propName"));
        test2(__getProperty(System.class, "out"));
        System.out.println("Hello world!");
    }
}