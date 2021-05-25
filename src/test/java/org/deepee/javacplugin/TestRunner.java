package org.deepee.javacplugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.SneakyThrows;

public class TestRunner {

    @SneakyThrows
    public Object runMethodInClass(byte[] byteCode, String qualifiedClassName, String methodName,
        Class<?>[] argumentTypes, Object... args) {
        ClassLoader classLoader = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) {
                return defineClass(name, byteCode, 0, byteCode.length);
            }
        };
        Class<?> clazz = classLoader.loadClass(qualifiedClassName);
        Method method = clazz.getMethod(methodName, argumentTypes);
        try {
            return method.invoke(null, args);
        } catch (InvocationTargetException exc) {
            throw exc.getCause();
        }
    }
}