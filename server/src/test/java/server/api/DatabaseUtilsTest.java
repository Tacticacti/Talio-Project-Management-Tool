package server.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Board;
import server.DatabaseUtils;

public class DatabaseUtilsTest {

    private DatabaseUtils databaseUtils;

    @BeforeEach
    public void setup() {
        databaseUtils = new DatabaseUtils();
    }

    @Test
    public void testMockBoard() {
        Board b1 = databaseUtils.mockSimpleBoard();
        assertNotNull(b1);
    }
}
