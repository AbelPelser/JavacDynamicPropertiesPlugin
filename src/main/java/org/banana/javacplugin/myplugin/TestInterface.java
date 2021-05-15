package org.banana.javacplugin.myplugin;

import static java.util.Collections.synchronizedMap;

import java.util.HashMap;
import java.util.Map;

public interface TestInterface {
    Map<String, Object> __OBJECTS_MAP = synchronizedMap(new HashMap<>());
}
