package com.example.printf;

public class Example {

    public static void main(String... args) {
        final String constant = "%d\t%d\n";
        System.out.printf(constant, 14, (byte) 1, new Byte((byte) 1), "s");
    }
    
}
