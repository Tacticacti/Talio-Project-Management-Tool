package server.api;

import java.util.List;
import java.util.Objects;

import commons.Board;
import commons.Card;
import commons.BoardList;
import server.DatabaseUtils;
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

    @PostMapping(path = "/add/{id}")
    public ResponseEntity<Board> addCardToId(@PathVariable("id") long boardId, 
        @RequestBody Pair<Long, Card> req) {

        if(!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println(boardId + " " + req.getFirst() + " " +
            req.getSecond());

        Board board = repo.findById(boardId).get();

        Long listId = req.getFirst();
        Card card = req.getSecond();

        var list = databaseUtils.getListById(board, listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        list.get().addCard(card);

        Board saved = repo.save(board);

        return ResponseEntity.ok(saved);
    }
    @PostMapping(path="/delete/{id}")
    public ResponseEntity<Board> deleteCardFromId(@PathVariable ("id") long boardId
            , @RequestBody Pair<Long, Card> req)
    {
        if(!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println(boardId + " " + req.getFirst() + " " +
                req.getSecond());

        Board board = repo.findById(boardId).get();

        Long listId = req.getFirst();
        Card card = req.getSecond();

        var list = databaseUtils.getListById(board, listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        list.get().getCards().removeIf(x -> x.getId() == card.getId());

        repo.save(board);
        return ResponseEntity.ok(board);
    }

    @PostMapping(path="/update/{id}")
    public ResponseEntity<Board> updateCardInId(@PathVariable("id") long boardId
            , @RequestBody Pair<Long, Card> req){

        if(!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("updating card: ");
        System.out.println(boardId + " " + req.getFirst() + " " +
                req.getSecond());

        Board board = repo.findById(boardId).get();

        Long listId = req.getFirst();
        Card card = req.getSecond();

        var list = databaseUtils.getListById(board, listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var cards = list.get().getCards();
        var result = cards.stream()
                .filter(x -> x.getId() == card.getId())
                .findFirst();

        if(result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Card toUpdate = result.get();
        databaseUtils.updateCard(toUpdate, card.title, card.description,
                card.subtasks, card.tags);

        toUpdate.board = board;
        toUpdate.boardList = list.get();

        Board saved = repo.save(board);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = "/add/list/{id}")
    public ResponseEntity<BoardList> addListToBoard(@PathVariable("id") long boardId,
        @RequestBody String listName) {

        System.out.println("addListToBoard: ");
        System.out.println(boardId + " " + listName);

        if (!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.findById(boardId).get();
        board.addList(new BoardList(listName));
        Board saved = repo.save(board);
        Long listId = saved.getLists().size()-1L;
        return ResponseEntity.ok(saved.getLists().get(listId.intValue()));
    }

    @PostMapping(path = "/list/changeName/{id}")
    public ResponseEntity<BoardList> changeListsName(@PathVariable("id") long boardId,
        @RequestBody Pair<String, Long> req) {

        if (!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Long listId = req.getSecond();
        String listName = req.getFirst();

        Board board = repo.findById(boardId).get();

        var result = board.getLists().stream()
                .filter(x -> Objects.equals(x.getId(), listId))
                .findFirst();

        if(result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BoardList saved = result.get();
        saved.setName(listName);

        repo.save(board);
        return ResponseEntity.ok(saved);
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
