package com.demo;

public class MathUtil {

    public static long factorial(int n) {
        if (n < 0 || n > 20) {
            throw new IllegalArgumentException("n must be between 0 and 20");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    public static int greatest(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }
}
