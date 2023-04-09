package client.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalUtilsTest {
    private LocalUtils localUtils;

    @BeforeEach
    public void setUp() {
        localUtils = new LocalUtils();
    }

    @Test
    public void testSetPath() throws IOException {
        String path = "       test";
        LocalUtils localUtils = new LocalUtils();
        localUtils.setPath(path);
        File file = new File("data/test@saved_boards.txt");
        assertTrue(file.exists());

        assertEquals(path, localUtils.getPath());
    }

    @Test
    public void testFetch() throws IOException {
        localUtils.setPath("data/test");
        localUtils.write();
        localUtils.add(123L);
        localUtils.fetch();

        Set<Long> expectedBoards = new TreeSet<>();
        expectedBoards.add(123L);

        assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testFetchThrowIoException() throws IOException {
        localUtils.setPath("data/test");
        localUtils.write();
        localUtils.add(123L);
        localUtils.inited = false;
        assertThrows(IOException.class, () -> {
            localUtils.fetch();
        });
    }

    @Test
    public void testAdd() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);

        Set<Long> expectedBoards = new TreeSet<>();
        expectedBoards.add(123L);

        assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testAdd_duplicate() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);
        localUtils.add(123L);

        Set<Long> expectedBoards = new TreeSet<>();
        expectedBoards.add(123L);

        assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testRemove() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);
        localUtils.remove(123L);

        Set<Long> expectedBoards = new TreeSet<>();

        assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testRemove_notAdded() throws IOException {
        localUtils.setPath("data/test");
        localUtils.remove(123L);

        Set<Long> expectedBoards = new TreeSet<>();

        assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testIsAdded() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);

        assertTrue(localUtils.isAdded(123L));
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

        assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testReset() throws IOException {
        localUtils.setPath("data/test");
        localUtils.add(123L);
        localUtils.reset();

        Set<Long> expectedBoards = new TreeSet<>();

        assertEquals(expectedBoards, localUtils.getBoards());
    }

    @Test
    public void testReadCustomization() {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter("data/customization.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("123:color:red");
            printWriter.println("456:background:black");
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocalUtils.readCustomization();
        Map<Long, String> customizationData = CustomizationUtils.customizationData;
        assertTrue(customizationData.containsKey(123L));
        assertEquals(customizationData.get(123L), "color:red");
        assertTrue(customizationData.containsKey(456L));
        assertEquals(customizationData.get(456L), "background:black");
        File file = new File("data/customization.txt");
        file.delete();
    }

    @Test
    public void testWriteCustomization() throws IOException {
        // create a temporary file for testing
        File file = new File("data/customization.txt");
        String filePath = file.getAbsolutePath();
        // set the customization data
        Map<Long, String> customizationData = new HashMap<>();
        customizationData.put(1L, "value1");
        customizationData.put(2L, "value2");

        // write the customization data to the file
        CustomizationUtils.customizationData = customizationData;
        LocalUtils.writeCustomization();

        // read the contents of the file
        String contents = new String(Files.readAllBytes(Paths.get(filePath)));

        // expected contents of the file
        String expectedContents = "1:value1\n2:value2\n";

        // compare the actual and expected contents of the file
        assertEquals(expectedContents, contents);
    }
}
