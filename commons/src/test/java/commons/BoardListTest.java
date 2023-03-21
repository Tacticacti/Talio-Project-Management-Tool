package commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BoardListTest {

    private BoardList boardList;
    private Card card1, card2;

    // initialize the BoardList object before each test
    @BeforeEach
    public void setup() {
        boardList = new BoardList("List 1");
        card1 = new Card("Card 1");
        card2 = new Card("Card 2");
    }

    // test the constructor
    @Test
    public void testConstructor() {
        BoardList newList = new BoardList("New List");
        Assertions.assertEquals("New List", newList.getName(),
			"Constructor should set the list name");
        Assertions.assertNotNull(newList.getCards(),
			"Constructor should initialize the cards variable");
        Assertions.assertEquals(0, newList.getCards().size(),
			"Constructor should initialize an empty cards variable");
    }

    // test the getId() and setId() methods
    @Test
    public void testId() {
        boardList.setId(123L);
        Assertions.assertEquals(123L, boardList.getId(),
			"setId() should set the id");
    }

    // test the getName() and setName() methods
    @Test
    public void testName() {
        boardList.setName("New Name");
        Assertions.assertEquals("New Name", boardList.getName(),
			"setName() should set the list name");
    }

    // test the addCard() method
    @Test
    public void testAddCard() {
        boardList.addCard(card1);
        Assertions.assertTrue(boardList.getCards().contains(card1), 
			"addCard() should add a card to the list");
        Assertions.assertEquals(1, boardList.getCards().size(), 
			"addCard() should increase the size of the cards variable by 1");
    }

    // test the removeCard() method
    @Test
    public void testRemoveCard() {
        boardList.addCard(card1);
        boardList.addCard(card2);
        boardList.removeCard(card1);
        Assertions.assertFalse(boardList.getCards().contains(card1),
			"removeCard() should remove a card from the list");
        Assertions.assertEquals(1, boardList.getCards().size(),
			"removeCard() should decrease the size of the cards variable by 1");
    }

    // test the getCards() method
    @Test
    public void testGetCards() {
        boardList.addCard(card1);
        boardList.addCard(card2);
        List<Card> expectedCards = new ArrayList<>();
        expectedCards.add(card1);
        expectedCards.add(card2);
        Assertions.assertEquals(expectedCards, boardList.getCards(),
			"getCards() should return the list of cards");
    }
}
