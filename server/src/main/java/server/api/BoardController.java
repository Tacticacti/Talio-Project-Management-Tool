package server.api;

import java.util.List;
import commons.Board;
import commons.BoardList;
import server.database.BoardRepository;

import org.springframework.data.util.Pair;
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

    public BoardController(BoardRepository repo) {
        this.repo = repo;
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

    // wip
    // -------
    // the idea is to pass id of a list and board and this function will be able
    // to add this list to the board
    @PostMapping(path = "/addListToBoard")
    public ResponseEntity<Board> addListToBoard(@RequestBody Pair<Long, Long> req) {
        Long boardId = req.getFirst();
        // Blist list = req.getSecond();
        Long list_id = req.getSecond();
        Board board = null;
        BoardList list = null;
        System.err.println("reached milestone 1");
        try {
            board = repo.findById(boardId).get();
            // column = columnRepo.findById(columnId).get();
        }
        catch(Exception e) {
            System.err.println("findById failed: " + boardId);
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        System.err.println("reached milestone 2");
        System.err.println(board);
        if(board == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(board);
    }
}