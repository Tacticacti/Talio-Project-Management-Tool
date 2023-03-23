package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Board;
import commons.BoardList;
import commons.Card;
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

    @Test
    public void testPropagateIDs() {
        Board b1 = databaseUtils.mockSimpleBoard();
        databaseUtils.PopagateIDs(b1);

        for(BoardList bl : b1.getLists()) {
            assertEquals(b1.getId(), bl.boardId);
            for(Card c : bl.getCards()) {
                assertEquals(b1.getId(), c.boardId);
                assertEquals(bl.getId(), c.listId);
            }
        }
    }

}
