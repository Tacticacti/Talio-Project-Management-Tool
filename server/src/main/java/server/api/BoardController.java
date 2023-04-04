package server.api;

import java.util.List;
import java.util.Objects;

import commons.Board;
import commons.BoardList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import server.Admin;
import commons.Tag;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.DatabaseUtils;
import server.database.BoardRepository;
import server.services.BoardServiceImpl;

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



    private BoardServiceImpl boardService;



    private final SimpMessagingTemplate messagingTemplate;

    public BoardController(BoardRepository repo, 
        DatabaseUtils databaseUtils, SimpMessagingTemplate messagingTemplate, Admin admin) {
        this.repo = repo;
        this.databaseUtils = databaseUtils;
        this.messagingTemplate = messagingTemplate;
        this.boardService = new BoardServiceImpl(repo,messagingTemplate);
        this.admin = admin;

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
        return boardService.getAll();
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
        return boardService.getbyId(id);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return boardService.addBoard(board);
    }

    @PostMapping(path = "/add/list/{id}")
    public ResponseEntity<Long> addListToBoard(@PathVariable("id") long boardId,
                                               @RequestBody String listName) {

        return boardService.addListToBoard(boardId,listName);
    }


    @PostMapping(path = "/list/delete/{id}")
    public ResponseEntity<Board> deleteList(@PathVariable("id") long boardId,
                                            @RequestBody long listId) {

        return boardService.deleteList(boardId,listId);
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

        if(psswd == null || !psswd.equals(admin.getPassword()))
            return ResponseEntity.badRequest().build();

        repo.deleteById(boardId);
        return ResponseEntity.ok(true);
    }
}
