package server.api;

import commons.Card;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class CardControllerTest {

    private TestCardRepository repo;
    private CardController controller;

    Card c1, c2;
    Tag t1, t2;

    @BeforeEach
    public void setup() {
        repo = new TestCardRepository();
        controller = new CardController(repo);
        c1 = new Card("card 1");
        c2 = new Card("card 2");

        c1.id = 0L;
        c2.id = 1L;

        t1 = new Tag("tag 1");
        t2 = new Tag("tag 2");
    }

    @Test
    public void getAll() {
        controller.add(c1);
        List<Card> model = new ArrayList<>();
        model.add(c1);
        assertEquals(model, controller.getAll());
    }

    @Test
    public void addNull() {
        var ret = controller.add(null);
        assertEquals(BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void addOK() {
        var ret = controller.add(c2);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertEquals(c2, ret.getBody());
    }

    @Test
    public void deleteWrongID() {
        var ret = controller.add(c2);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        var ret2 = controller.delete(99L);
        assertEquals(BAD_REQUEST, ret2.getStatusCode());
    }

    @Test
    public void deleteOK() {
        var ret = controller.add(c2);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        var ret2 = controller.delete(1L);
        assertNotEquals(BAD_REQUEST, ret2.getStatusCode());
    }

    @Test
    public void getCardWrongID() {
        var ret = controller.add(c2);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        var ret2 = controller.getCardById(99L);
        assertEquals(BAD_REQUEST, ret2.getStatusCode());
    }

    @Test
    public void getCardOK() {
        var ret = controller.add(c2);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        var ret2 = controller.getCardById(1L);
        assertNotEquals(BAD_REQUEST, ret2.getStatusCode());
        assertEquals(c2, ret2.getBody());
    }

    @Test
    public void addTag() {
        var ret = controller.add(c1);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.addTag(null, 99L);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.addTag(t1, 99L);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.addTag(t1, 0L);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertTrue(ret.getBody().getTags().contains(t1));
    }

    @Test
    public void deleteTag() {
        var ret = controller.add(c1);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        ret = controller.addTag(t1, 0L);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.deleteTag(null, 0L);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.deleteTag(t1, 99L);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.deleteTag(t2, 0L);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertTrue(ret.getBody().getTags().contains(t1));

        ret = controller.deleteTag(t1, 0L);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertFalse(ret.getBody().getTags().contains(t1));
    }
}
