package com.example.printf;

public class Example {

    public static void main(String... args) {
        System.out.printf("%", 1);
        System.out.printf("%f", 1);
        final String constant = "%d\t%d\t%d\n";
        System.out.printf(constant, 14, 14);
    }
    
}
