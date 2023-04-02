package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TagTest {

    @Test
    void setTitle() {
        Tag tag= new Tag("Tag");
        tag.setTitle("Tag2");
        assertEquals("Tag2", tag.getTitle());
    }

    @Test
    void getTitle() {
        Tag tag= new Tag("Tag");
        assertEquals("Tag", tag.getTitle());
    }

    @Test
    void testEquals() {
        Tag t1= new Tag("tag");
        Tag t2= new Tag("tag");
        assertEquals(t1, t2);
    }

    @Test
    void testHashCode() {
        Tag t1= new Tag("tag");
        Tag t2= new Tag("tag");
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
