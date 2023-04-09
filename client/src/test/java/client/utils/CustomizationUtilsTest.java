package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomizationUtilsTest {
    private Long boardID;
    private Map<Long, String> customizationData;

    @BeforeEach
    void setUp() {
        boardID = 1L;
        customizationData = CustomizationUtils.customizationData;
    }


    @Test
    void testAddDefaultCustomization() {
        CustomizationUtils.addDefaultCustomization(boardID);
        String expected = "white,#403e3e,white,black,false,white,black";
        String actual = customizationData.get(boardID);
        assertEquals(expected, actual);
    }

    @Test
    void testGetCustomizationField() {
        String expected = "black";
        customizationData.put(boardID, "red,orange,yellow,black,false,green,blue");

        String actual = CustomizationUtils.getCustomizationField(boardID, 3);

        assertEquals(expected, actual);
    }
}
