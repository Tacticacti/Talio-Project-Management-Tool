package server.api;

import java.util.List;
import commons.Board;
import commons.Card;
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

        // TODO STUPID DEBUG I HAVE TO REMOVE THIS ASAP!!!!!
        /*
        Board board = new Board("test board");
        BoardList l1 = new BoardList("test list 1");
        l1.addCard(new Card("aa"));
        l1.addCard(new Card("bb"));
        l1.addCard(new Card("ca"));

        board.addList(l1);

        BoardList l2 = new BoardList("test list 2");
        l2.addCard(new Card("az"));
        l2.addCard(new Card("bz"));
        l2.addCard(new Card("cz"));

        board.addList(l2);
        repo.save(board);
        */
        // TODO END OF DEBUG!!!!
    }

    @GetMapping(path = "/TalioPresent")
    public ResponseEntity<String> talioPresenceCheck() {
        return ResponseEntity.ok("Welcome to Talio!");
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

    @PostMapping(path = "/add/{id}")
    public ResponseEntity<Board> addCardToId(@PathVariable("id") long boardId, 
        @RequestBody Pair<Long, Card> req) {

        if(!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("--------------------------");
        System.out.println(boardId + " " + req.getFirst() + " " + 
            req.getSecond());
        System.out.println("--------------------------");

        Board board = repo.findById(boardId).get();

        Long listId = req.getFirst();
        Card card = req.getSecond();
        if(board.getLists().size() <= listId || listId < 0) {
            return ResponseEntity.badRequest().build();
        }

        board.addToList(listId.intValue(), card);
        Board saved = repo.save(board);

        return ResponseEntity.ok(saved);
    }
}

