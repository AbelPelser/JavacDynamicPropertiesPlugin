package org.deepee.javacplugin;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class EnvironmentUtil {
    @SneakyThrows
    public static void replaceEnvironment(Map<String, String> newEnvironment) {
        Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
        Field unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment");
        unmodifiableMapField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(unmodifiableMapField, unmodifiableMapField.getModifiers() & ~Modifier.FINAL);
        unmodifiableMapField.set(null, newEnvironment);
    }
}
