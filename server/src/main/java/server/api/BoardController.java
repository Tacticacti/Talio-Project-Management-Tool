package server.api;

import java.util.List;
import commons.Board;
import commons.Columnn;
import server.database.BoardRepository;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository repo;

    public BoardController(BoardRepository repo) {
        this.repo = repo;
    }
    
    @GetMapping(path = {"", "/"})
    public String getAll() {
        List<Board> list = repo.findAll();
        String res = "";
        for(Board b : list)
            res += b + "<br> \n";
        return res;
    }

    // wip
    // still crashes for some reason :(
    // -------
    // the idea is to pass id of a column and board and this function will be able
    // to add this column to the board
    @PostMapping(path = "/add")
    public ResponseEntity<Board> addColumn(@RequestBody Pair<Long, Long> req) {
        Long boardId = req.getFirst();
        // Columnn columnn = req.getSecond();
        Long columnId = req.getSecond();
        Board board = null;
        Columnn column = null;
        System.err.println("reached milestone 1");
        try {
            board = repo.getById(boardId);
            // column = columnRepo.getById(columnId);
        }
        catch(Exception e) {
            System.err.println("getById failed: " + boardId);
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