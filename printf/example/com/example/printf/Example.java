package com.example.printf;

public class Example {

    private static int a = 2, b = 3, c = 4;
    private static Integer boxed = null;
    
    public static void main(String... args) {
        final String constant = "%d\t%d\t%d\n";
        System.out.printf(constant, 14, 14);
        System.out.printf(constant, 14, 14, 14);
        System.out.printf(constant, "foo", "bar");
        System.out.printf(constant, a, b, c);
        System.out.printf(constant, boxed, boxed, boxed);
        System.out.printf(constant, null, null, null); // TODO cover null constants
    }
    
}
