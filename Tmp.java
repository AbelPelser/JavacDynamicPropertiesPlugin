package org.deepee.javacplugin;

public class Tmp {
    private static int test(String i) {
        i.haha = 6;
        int x = i.haha;
        return x;
//        i.x = 6;
//        i.doSomething();
//        i.t = i.doAnotherThing();
//        boolean b = Optional.of(i)
//                .map(Tmp::testRef)
//                .orElse(true);
    }


    public static void main(String[] args) {
        System.out.println("Hello world! " + test("sss"));
    }
}