package server.api;

import commons.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class CardControllerTest {

    private TestCardRepository repo;
    private CardController controller;

    Card c1, c2;

    @BeforeEach
    public void setup() {
        repo = new TestCardRepository();
        controller = new CardController(repo);
        c1 = new Card("card 1");
        c2 = new Card("card 2");

        c1.id = 0L;
        c2.id = 1L;
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
}
