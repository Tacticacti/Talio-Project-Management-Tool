package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class SomeControllerTest {
    @Test
    public void talioPresentCheck() {
        SomeController sc = new SomeController(new Admin());
        var ret = sc.talioPresenceCheck();
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertEquals("Welcome to Talio!", ret.getBody());
    }
}
