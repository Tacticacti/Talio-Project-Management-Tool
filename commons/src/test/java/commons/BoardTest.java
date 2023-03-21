package commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class BoardTest {

    private Board board;
    private BoardList list1, list2;
    private Card card1;

    // initialize the Board object before each test
    @BeforeEach
    public void setup() {
        board = new Board("Board 1");
        list1 = new BoardList("List 1");
        list2 = new BoardList("List 2");
        card1 = new Card("card 1");
    }

    // test the constructor
    @Test
    public void testConstructor() {
        Board newBoard = new Board("New Board");
        Assertions.assertEquals("New Board", newBoard.getName(), "Constructor should set the board name");
        Assertions.assertNotNull(newBoard.getLists(), "Constructor should initialize the lists variable");
        Assertions.assertEquals(0, newBoard.getLists().size(), "Constructor should initialize an empty lists variable");
    }

    // test the getId() and setId() methods
    @Test
    public void testId() {
        board.setId(123L);
        Assertions.assertEquals(123L, board.getId(), "setId() should set the id");
    }

    // test the getName() and setName() methods
    @Test
    public void testName() {
        board.setName("New Name");
        Assertions.assertEquals("New Name", board.getName(), "setName() should set the board name");
    }

    // test the addList() method
    @Test
    public void testAddList() {
        board.addList(list1);
        Assertions.assertTrue(board.getLists().contains(list1), "addList() should add a list to the board");
        Assertions.assertEquals(1, board.getLists().size(), "addList() should increase the size of the lists variable by 1");
    }

    // test the removeList() method
    @Test
    public void testRemoveList() {
        board.addList(list1);
        board.addList(list2);
        board.removeList(list1);
        Assertions.assertFalse(board.getLists().contains(list1), "removeList() should remove a list from the board");
        Assertions.assertEquals(1, board.getLists().size(), "removeList() should decrease the size of the lists variable by 1");
    }

    // test the getLists() method
    @Test
    public void testGetLists() {
        board.addList(list1);
        board.addList(list2);
        List<BoardList> expectedLists = new ArrayList<>();
        expectedLists.add(list1);
        expectedLists.add(list2);
        Assertions.assertEquals(expectedLists, board.getLists(), "getLists() should return the list of lists");
    }

    @Test
    public void testDefConstructor() {
        Board b = new Board();
        assertEquals("",b.getName());
        assertNotNull(b.getLists());
    }

    @Test
    public void testAddToList() {
        board.addList(list1);
        board.addToList(0, card1);
        assertTrue(board.getLists().get(0).getCards().contains(card1));
    }
}
