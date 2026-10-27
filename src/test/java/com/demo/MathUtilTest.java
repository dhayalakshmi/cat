package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MathUtilTest {
    @Test void testFactorial() {
        assertEquals(1, MathUtil.factorial(0));
        assertEquals(120, MathUtil.factorial(5));
        assertThrows(IllegalArgumentException.class, () -> MathUtil.factorial(-1));
    }

    @Test void testGreatest() {
        assertEquals(9, MathUtil.greatest(3, 9, 5));
        assertEquals(9, MathUtil.greatest(9, 3, 5));
        assertEquals(9, MathUtil.greatest(3, 5, 9));
    }
}
