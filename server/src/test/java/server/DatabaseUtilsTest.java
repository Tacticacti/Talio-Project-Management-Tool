package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import commons.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Board;

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

    @Test
    public void updateCardEmptyName() {
        Card c1 = new Card("name");
        Card c2 = new Card("     ");
        databaseUtils.updateCard(c1, c2);
        assertEquals("name", c1.getTitle());
    }
}
