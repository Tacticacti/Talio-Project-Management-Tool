package client.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class LocalUtilsTest {
    private LocalUtils localUtils;

    @BeforeEach
    public void setUp() {
        localUtils = new LocalUtils();
    }

    @Test
    public void testFetch() throws IOException {
        localUtils.setPath("data/test");
        localUtils.write();
        localUtils.add(123L);

        localUtils.fetch();

        Set<Long> expectedBoards = new TreeSet<>();
        expectedBoards.add(123L);

        Assertions.assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testAdd() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);

        Set<Long> expectedBoards = new TreeSet<>();
        expectedBoards.add(123L);

        Assertions.assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testAdd_duplicate() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);
        localUtils.add(123L);

        Set<Long> expectedBoards = new TreeSet<>();
        expectedBoards.add(123L);

        Assertions.assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testRemove() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);
        localUtils.remove(123L);

        Set<Long> expectedBoards = new TreeSet<>();

        Assertions.assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testRemove_notAdded() throws IOException {
        localUtils.setPath("data/test");
        localUtils.remove(123L);

        Set<Long> expectedBoards = new TreeSet<>();

        Assertions.assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testIsAdded() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);

        Assertions.assertTrue(localUtils.isAdded(123L));
    }

    @Test
    public void testIsAdded_notAdded() throws IOException {
        localUtils.setPath("data/test");

        Assertions.assertFalse(localUtils.isAdded(123L));
    }

    @Test
    public void testWrite() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);
        localUtils.write();

        Set<Long> expectedBoards = new TreeSet<>();
        expectedBoards.add(123L);

        Assertions.assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testReset() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);
        localUtils.reset();

        Set<Long> expectedBoards = new TreeSet<>();

        Assertions.assertEquals(expectedBoards, localUtils.getBoards());
    }
}
