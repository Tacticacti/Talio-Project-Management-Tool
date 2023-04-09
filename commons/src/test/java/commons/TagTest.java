package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagTest {

    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        tag1 = new Tag("tag1", "#FFFFFF");
        tag2 = new Tag("tag2", "#000000");
    }

    @Test
    void emptyConstructor(){
        Tag tag=new Tag();
        tag.setTitle("Tag");
        assertEquals("Tag", tag.getTitle());
    }

    @Test
    void constructor(){
        Tag t = new Tag("name", "#ffffff");
        assertEquals("name", t.getTitle());
        assertEquals("#ffffff", t.getColor());
        assertNotEquals(null, t.cards);
    }
    @Test
    void setTitle() {
        Tag tag= new Tag("Tag");
        tag.setTitle("Tag2");
        assertEquals("Tag2", tag.getTitle());
    }

    @Test
    void addCard(){
        Tag t = new Tag();
        Card c = new Card();
        t.addCard(c);
        assertTrue(t.cards.contains(c));
    }

    @Test
    void removeCard(){
        Tag t = new Tag();
        Card c = new Card();
        t.addCard(c);
        assertTrue(t.cards.contains(c));
        t.removeCard(c);
        assertFalse(t.cards.contains(c));
    }

    @Test
    void getTitle() {
        Tag tag= new Tag("Tag");
        assertEquals("Tag", tag.getTitle());
    }

    @Test
    void IdTest(){
        Tag tag = new Tag("tag");
        tag.setId(123);
        assertEquals(123, tag.getId());
    }

    @Test
    void testEquals() {
        Tag t1= new Tag("tag");
        Tag t2= new Tag("tag");
        assertEquals(t1, t2);
        assertNotEquals(t1, "tag");
    }

    @Test
    void testHashCode() {
        Tag t1= new Tag("tag");
        Tag t2= new Tag("tag");
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    @DisplayName("Equals method returns false for tags with different title")
    void notEqualsTest() {
        assertNotEquals(tag1, tag2);
    }

    @Test
    @DisplayName("Add card method adds a card to the tag's set of cards")
    void addCardTest() {
        Card card = new Card("card1", "description1");
        tag1.addCard(card);
        assertEquals(1, tag1.cards.size());
        assertEquals(card, tag1.cards.iterator().next());
    }

    @Test
    @DisplayName("Remove card method removes a card from the tag's set of cards")
    void removeCardTest() {
        Card card = new Card("card1", "description1");
        tag1.addCard(card);
        assertEquals(1, tag1.cards.size());
        tag1.removeCard(card);
        assertEquals(0, tag1.cards.size());
    }

    @Test
    @DisplayName("Set and get color")
    void setColor() {
        Tag tag = new Tag("tag");
        tag.setColor("#000000");
        assertEquals("#000000", tag.getColor());
    }
}
