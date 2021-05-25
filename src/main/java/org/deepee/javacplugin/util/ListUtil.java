package org.deepee.javacplugin.util;

import com.sun.tools.javac.util.List;

public class ListUtil {
    public static <T> List<T> emptyJavacList() {
        return List.nil();
    }

    @SafeVarargs
    public static <T> List<T> javacList(T... item) {
        return List.from(item);
    }

    public static <T> List<T> javacList(java.util.List<T> item) {
        return List.from(item);
    }
}
