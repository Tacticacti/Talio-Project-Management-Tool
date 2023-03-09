package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Board;

public class BoardControllerTest {

    private TestBoardRepository boardRepo;
    private BoardController controller;

    @BeforeEach
    public void setup() {
        boardRepo = new TestBoardRepository();
        controller = new BoardController(boardRepo);
    }

    @Test
    public void cannotAddNullBoard() {
        var ret = controller.add(null);
        assertEquals(BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void addBoard() {
        var ret = controller.add(new Board("board"));
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertTrue(boardRepo.calledMethods.contains("save"));
    }
}
