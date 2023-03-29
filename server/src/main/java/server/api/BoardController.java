package server.api;

import java.util.List;
import java.util.Objects;

import commons.Board;
import commons.BoardList;
import server.DatabaseUtils;
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

    private DatabaseUtils databaseUtils;

    public BoardController(BoardRepository repo, 
        DatabaseUtils databaseUtils) {
        this.repo = repo;
        this.databaseUtils = databaseUtils;
        

        // TODO uncomment **ONLY** for debug!!
        /*
        Board board = databaseUtils.mockSimpleBoard();
        repo.save(board);
        */
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
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = "/add/list/{id}")
    public ResponseEntity<Long> addListToBoard(@PathVariable("id") long boardId,
        @RequestBody String listName) {

        System.out.println("addListToBoard: ");
        System.out.println(boardId + " " + listName);

        if (!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.findById(boardId).get();
        board.addList(new BoardList(listName));
        Board saved = repo.save(board);
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

        System.out.println("deleting " + boardId + " " + listId);

        Board board = repo.findById(boardId).get();
        board.getLists().removeIf(x -> Objects.equals(x.getId(), listId));
        repo.save(board);
        return ResponseEntity.ok(board);
    }
}
