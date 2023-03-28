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
        System.out.println(board.getLists().size());
        if(board.getLists().size() <= listId || listId < 0) {
            return ResponseEntity.badRequest().build();
        }

        board.addToList(listId.intValue(), card);
        Board saved = repo.save(board);

        return ResponseEntity.ok(saved);
    }
    @PostMapping(path="/delete/{id}")
    public void deleteCardFromId(@PathVariable ("id") long boardId
            , @RequestBody Pair<Long, Card> req)
    {
        if(!repo.existsById(boardId)) {
            throw new RuntimeException();
        }

        System.out.println("--------------------------");
        System.out.println(boardId + " " + req.getFirst() + " " +
                req.getSecond());
        System.out.println("--------------------------");

        Board board = repo.findById(boardId).get();

        Long listId = req.getFirst();
        Card card = req.getSecond();
        if(board.getLists().size() <= listId || listId < 0) {
            throw new RuntimeException();
        }

        // board.getLists().get(listId.intValue()).removeCard(card);
        board.getLists().get(listId.intValue()).getCards().removeIf(x ->
                x.getId() == card.getId());
        repo.save(board);
    }

    @PostMapping(path="/update/{id}")
    public ResponseEntity<Board> updateCardInId(@PathVariable("id") long boardId
            , @RequestBody Pair<Long, Card> req){
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
        int cardIndex = -1;
        for(int i=0; i< board.getLists().get(listId.intValue()).getCards().size(); i++){
            if(Objects.equals(board.getLists().get(listId.intValue()).getCards().get(i).getId(),
                    card.getId())) {
                cardIndex = i;
                break;
            }
        }

        if(cardIndex < 0) {
            return ResponseEntity.badRequest().build();
        }

        Card toupdate = board.getLists().get(listId.intValue()).getCards().get(cardIndex);
        toupdate.setTitle(card.getTitle());
        toupdate.setDescription(card.getDescription());
        toupdate.getSubtasks().removeAll(toupdate.getSubtasks());
        for(String s: card.getSubtasks()){
            toupdate.addSubTask(s);
        }
        Board saved = repo.save(board);
        return ResponseEntity.ok(saved);
    }



    @PostMapping(path = "/add/list/{id}")
    public ResponseEntity<BoardList> addListToBoard(@PathVariable("id") long boardId,
        @RequestBody String listName) {

        // String listName = nameIn.getBody();

        System.out.println("---------------------------");
        System.out.println("addListToBoard: ");
        System.out.println(boardId + " " + listName);
        System.out.println("---------------------------");

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
        BoardList saved = null;

        boolean found = false;
        for(BoardList bl : board.getLists()) {
            if(Objects.equals(bl.getId(), listId)) {
                found = true;
                bl.setName(listName);
                saved = bl;
                break;
            }
        }

        if(!found) {
            return ResponseEntity.badRequest().build();
        }

        repo.save(board);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = "/list/delete/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable("id") long boardId,
        @RequestBody long listId) {

        if (!repo.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("deleteting " + boardId + " " + listId);

        Board board = repo.findById(boardId).get();
        board.getLists().removeIf(x -> Objects.equals(x.getId(), listId));
        repo.save(board);
        return ResponseEntity.ok().build();
    }
}
