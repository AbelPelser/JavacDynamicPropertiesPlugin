package org.banana.javacplugin.util;

import com.sun.tools.javac.util.List;

public class ListUtil {
    public static <T> List<T> emptyJavacList() {
        return List.nil();
    }

    @SafeVarargs
    public static <T> List<T> javacList(T... item) {
        return List.from(item);
    }
}
