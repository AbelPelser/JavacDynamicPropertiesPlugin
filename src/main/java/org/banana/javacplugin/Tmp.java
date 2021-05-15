package org.banana.javacplugin;


public class Tmp {

//    private static void test(Integer i) {
//        int x = i.haha;
//        i.x = 6;
//    }

//    private static Map<Object, Object> __monkeyMap1238533194;
//
//    private static void __setupEnvironment1238533194() {
//        try {
//            Map<String, String> oldMap = System.getenv();
//            Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
//            Field unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment");
//            unmodifiableMapField.setAccessible(true);
//            Field modifiersField = Field.class.getDeclaredField("modifiers");
//            modifiersField.setAccessible(true);
//            modifiersField.setInt(unmodifiableMapField, unmodifiableMapField.getModifiers() & ~Modifier.FINAL);
//            __monkeyMap1238533194 = synchronizedMap(new HashMap<Object, Object>(oldMap));
//            unmodifiableMapField.set(null, __monkeyMap1238533194);
//        } catch (Exception ignore) {
//        }
//    }


    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}