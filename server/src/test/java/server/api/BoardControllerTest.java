package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Board;
import commons.BoardList;
import commons.Card;
import server.Admin;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import server.DatabaseUtils;
import server.Encryption;

import java.util.List;

@SpringBootTest
public class BoardControllerTest {


    private SimpMessagingTemplate messagingTemplate;

    private DatabaseUtils databaseUtils;
    private TestBoardRepository boardRepo;
    private BoardController controller;
    private Board b1, b2;
    private BoardList bl1;
    private Card c1, c2;
    private Tag t1, t2;
    private Admin admin;
    private Encryption encryption;

    @BeforeEach
    public void setup() {
        databaseUtils = new DatabaseUtils();
        boardRepo = new TestBoardRepository();
        messagingTemplate = (SimpMessagingTemplate) new TestSimpMessagingTemplate();

        admin = new Admin();
        encryption = new Encryption();
        controller = new BoardController(boardRepo, new DatabaseUtils(),
                messagingTemplate, admin, encryption);
        b1 = new Board("b1");
        b2 = new Board("b2");
        bl1 = new BoardList("bl1");
        bl1.setId(0L);
        b1.addList(bl1);

        c1 = new Card("c1");
        c1.description = "desc";
        c1.subtasks = List.of("task1", "task2");
        c2 = new Card("c2");
        c2.description = "new description";
        c2.subtasks = List.of("st1", "st2");

        t1 = new Tag("tag1");
        t2 = new Tag("tag2");
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

    @Test
    public void getAll() {
        controller.add(b1);
        controller.add(b2);
        var x = controller.getAll();
        assertTrue(x.contains(b1));
        assertTrue(x.contains(b2));
        String res = controller.getAllDebug();
        assertNotNull(res);
    }

    @Test
    public void getById() {
        controller.add(b1);
        var ret = controller.getById(0);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertEquals(b1, ret.getBody());
    }

    @Test
    public void getByIdNotPresent() {
        var ret = controller.getById(99);
        assertEquals(BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void addListWrongId() {
        controller.add(b1);
        var ret = controller.addListToBoard(1000, "custom name");
        assertEquals(BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void addListOK() {
        controller.add(b1);
        String name = "custom list";
        var ret = controller.addListToBoard(0, name);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        // assertEquals(name, ret.getBody().getName());
    }

    @Test
    public void deleteListWrongID() {
        controller.add(b1);
        var ret = controller.deleteList(99L, 99L);
        assertEquals(BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void deleteListOK() {
        controller.add(b1);
        var ret = controller.deleteList(0L, 0L);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void addTagToId() {
        controller.add(b1);
        var ret = controller.addTagToId(99L, t1);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        //ret = controller.addTagToId(0L, t1);
        //assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        // TODO
        // assertTrue(ret.getBody().getTags().contains(t1));
    }

    @Test
    public void deleteBoard() {
        controller.add(b1);
        var ret = controller.deleteBoard(0L, "");
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.deleteBoard(0L, null);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.deleteBoard(0L, admin.getPassword());
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertTrue(ret.getBody());

        var ret2 = controller.getById(0L);
        assertEquals(BAD_REQUEST, ret2.getStatusCode());
    }

    @Test
    public void setBoardPassword() {
        controller.add(b1);

        String psswd = "test123";
        String hash = encryption.getHash(psswd);

        var ret = controller.setBoardPassword(99L, psswd);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.setBoardPassword(0L, psswd);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertEquals(hash, ret.getBody().getPassword());
    }

    @Test
    public void verifyPassword() {
        controller.add(b1);

        var ret = controller.verifyPassword(0L, null);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertTrue(ret.getBody());

        String psswd = "test123";
        var ret2 = controller.setBoardPassword(0L, psswd);
        assertNotEquals(BAD_REQUEST, ret2.getStatusCode());

        ret = controller.verifyPassword(99L, null);
        assertEquals(BAD_REQUEST, ret.getStatusCode());

        ret = controller.verifyPassword(0L, null);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertFalse(ret.getBody());

        ret = controller.verifyPassword(0L, "admin");
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertFalse(ret.getBody());

        ret = controller.verifyPassword(0L, psswd);
        assertNotEquals(BAD_REQUEST, ret.getStatusCode());
        assertTrue(ret.getBody());
    }
}
