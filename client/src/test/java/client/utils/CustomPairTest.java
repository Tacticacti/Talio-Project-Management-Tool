package client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomPairTest {

    @Test
    public void testGetFirst() {
        String first = "Hello";
        Integer second = 123;

        CustomPair<String, Integer> pair = new CustomPair<>(first, second);
        assertEquals(first, pair.getFirst());
    }

    @Test
    public void testGetSecond() {
        String first = "Hello";
        Integer second = 123;

        CustomPair<String, Integer> pair = new CustomPair<>(first, second);
        assertEquals(second, pair.getSecond());
    }

    @Test
    public void testSetFirst() {
        String first = "Hello";
        Integer second = 123;

        CustomPair<String, Integer> pair = new CustomPair<>(first, second);
        String newFirst = "Hi";

        pair.setFirst(newFirst);
        assertEquals(newFirst, pair.getFirst());
    }

    @Test
    public void testSetSecond() {
        String first = "Hello";
        Integer second = 123;

        CustomPair<String, Integer> pair = new CustomPair<>(first, second);
        Integer newSecond = 456;

        pair.setSecond(newSecond);
        assertEquals(newSecond, pair.getSecond());
    }
}
