package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class TagTest {

    @Test
    void emptyConstructor(){
        Tag tag=new Tag();
        tag.setTitle("Tag");
        assertEquals("Tag", tag.getTitle());
    }
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
