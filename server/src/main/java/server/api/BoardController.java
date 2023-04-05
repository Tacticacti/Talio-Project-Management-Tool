package server.api;

import java.util.List;
import java.util.Objects;

import commons.Board;
import commons.BoardList;
import server.Admin;
import commons.Tag;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.DatabaseUtils;
import server.Encryption;
import server.database.BoardRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository repo;

    private final DatabaseUtils databaseUtils;
    private final Admin admin;
    private final Encryption encryption;

    private final SimpMessagingTemplate messagingTemplate;

    public BoardController(BoardRepository repo,
                           DatabaseUtils databaseUtils,
                           SimpMessagingTemplate messagingTemplate,
                           Admin admin,
                           Encryption encryption) {
        this.repo = repo;
        this.databaseUtils = databaseUtils;
        this.messagingTemplate = messagingTemplate;
        this.admin = admin;
        this.encryption = encryption;

        // TODO uncomment **ONLY** for debug!!
        /*
        Board board = databaseUtils.mockSimpleBoard();
        repo.save(board);
        */
    }
    @PostMapping( "/addTag/{id}")
    public ResponseEntity<Board> addTagToId(@PathVariable("id") long listId,
                                                 @RequestBody Tag tag) {

        var board = repo.findById(listId);

        if (!repo.existsById(listId)) {
            return ResponseEntity.badRequest().build();
        }

        board.get().addBoardTag(tag);

        Board saved = repo.save(board.get());
        messagingTemplate.convertAndSend("/topic/boards", saved);

        return ResponseEntity.ok(saved);
    }

    @GetMapping(path = {"", "/"})
    public List<Board> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/debug")
    public String getAllDebug() {
        List<Board> list = repo.findAll();
        String res = "";
        for(Board b : list)
            res += b + "<br> \n";
        return res;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Board> getById(@PathVariable("id") long id) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Board> add(@RequestBody Board board) {
        if(board == null) {
            return ResponseEntity.badRequest().build();
        }
        Board saved = repo.save(board);
        messagingTemplate.convertAndSend("/topic/boards", saved);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = "/add/list/{id}")
    public ResponseEntity<Long> addListToBoard(@PathVariable("id") long boardId,
        @RequestBody String listName) {

        if (!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.findById(boardId).get();
        board.addList(new BoardList(listName));
        Board saved = repo.save(board);
        messagingTemplate.convertAndSend("/topic/boards", saved);
        int index = saved.getLists().size()-1;
        Long listId = saved.getLists().get(index).getId();
        return ResponseEntity.ok(listId);
    }


    @PostMapping(path = "/list/delete/{id}")
    public ResponseEntity<Board> deleteList(@PathVariable("id") long boardId,
                                            @RequestBody long listId) {

        if (!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.findById(boardId).get();
        board.getLists().removeIf(x -> Objects.equals(x.getId(), listId));
        repo.save(board);
        messagingTemplate.convertAndSend("/topic/boards", board);
        return ResponseEntity.ok(board);
    }

    @PostMapping(path = "/tag/delete/{id}")
    public ResponseEntity<Board> deleteTag(@PathVariable("id") long boardId,
                                            @RequestBody Tag tag) {

        if (!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("deleting " + boardId + " " + tag);

        Board board = repo.findById(boardId).get();
        board.getTagLists().removeIf(x -> Objects.equals(x.getId(), tag.getId()));
        repo.save(board);
        messagingTemplate.convertAndSend("/topic/boards", board);
        return ResponseEntity.ok(board);

    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<Boolean> deleteBoard(@PathVariable("id") long boardId,
            @RequestBody String psswd) {

        if(psswd == null || !psswd.equals(admin.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(boardId);
        return ResponseEntity.ok(true);
    }

    @PostMapping(path = "/changePassword/{id}")
    public ResponseEntity<Board> setBoardPassword(@PathVariable("id") long boardId,
                                               @RequestBody String psswd) {

        if(!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.findById(boardId).get();

        String hashed = encryption.getHash(psswd);
        board.setPassword(hashed);
        Board saved = repo.save(board);
        return ResponseEntity.ok(saved);
    }

    @GetMapping(path = "/removePassword/{id}")
    public ResponseEntity<Board> resetBoardPassword(@PathVariable("id") long boardId) {

        if(!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.findById(boardId).get();

        board.setPassword(null);
        Board saved = repo.save(board);
        return ResponseEntity.ok(saved);
    }
    @PostMapping(path = "/verifyPassword/{id}")
    public ResponseEntity<Boolean> verifyPassword(@PathVariable("id") long boardId,
                                                @RequestBody String psswd) {

        if(!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.findById(boardId).get();

        // if board has no password
        if(board.getPassword() == null) {
            return ResponseEntity.ok(true);
        }

        if(psswd == null) {
            return ResponseEntity.ok(false);
        }

        String hashed = encryption.getHash(psswd);
        if(Objects.equals(hashed, board.getPassword()))
            return ResponseEntity.ok(true);
        return ResponseEntity.ok(false);
    }
}
