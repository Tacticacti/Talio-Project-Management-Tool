package commons;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TagTest {

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
    void setColor(){
        Tag t = new Tag();
        t.setColor("#ffffff");
        assertTrue(t.getColor().equals("#ffffff"));
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
        assertNotEquals(t1, new String("tag"));
    }

    @Test
    void testHashCode() {
        Tag t1= new Tag("tag");
        Tag t2= new Tag("tag");
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
