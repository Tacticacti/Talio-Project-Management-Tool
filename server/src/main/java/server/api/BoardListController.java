package server.api;

import commons.BoardList;
import commons.Card;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.DatabaseUtils;
import server.database.BoardListRepository;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class BoardListController {

    private final BoardListRepository repo;
    private DatabaseUtils databaseUtils;
    private SimpMessagingTemplate messagingTemplate;

    public BoardListController(BoardListRepository repo,
                               DatabaseUtils databaseUtils
            , SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.databaseUtils = databaseUtils;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping(path = {"", "/"})
    public List<BoardList> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BoardList> getById(@PathVariable("id") long id) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping(path = "/add")
    public ResponseEntity<BoardList> addList(@RequestBody BoardList bl) {
        if(bl == null) {
            return ResponseEntity.badRequest().build();
        }

        BoardList saved = repo.save(bl);
        messagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = "/changeName/{id}")
    public ResponseEntity<BoardList> changeListsName(@PathVariable("id") long listId,
                                                     @RequestBody String listName) {

        var result = repo.findById(listId);

        if(result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BoardList saved = result.get();
        saved.setName(listName);

        saved = repo.save(saved);
        messagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable("id") long listId) {

        if(!repo.existsById(listId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("deleting " + listId);
        BoardList bl = repo.getById(listId);
        repo.deleteById(listId);
        messagingTemplate.convertAndSend("/topic/lists", bl);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/add/{id}")
    public ResponseEntity<BoardList> addCardToId(@PathVariable("id") long listId,
        @RequestBody Card card) {

        System.out.println("add card: " + listId + " " + card);

        var list = repo.findById(listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        list.get().addCard(card);

        BoardList saved = repo.save(list.get());
        messagingTemplate.convertAndSend("/topic/lists", saved);

        return ResponseEntity.ok(saved);
    }

    @PostMapping(path="/deleteCard/{id}")
    public ResponseEntity<BoardList> deleteCardFromId(@PathVariable ("id") long listId,
            @RequestBody Card card)
    {

        System.out.println("delete from: " + listId + " " + card);

        var list = repo.findById(listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        list.get().getCards().removeIf(x -> x.getId() == card.getId());

        BoardList saved = repo.save(list.get());
        messagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path="/update/{id}")
    public ResponseEntity<BoardList> updateCardInId(@PathVariable("id") long listId,
            @RequestBody Card card){

     
        System.out.println("updating card: ");
        System.out.println(listId + " " + card);

        var list = repo.findById(listId);

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
        databaseUtils.updateCard(toUpdate, card);
        toUpdate.board = list.get().board;
        toUpdate.boardList = list.get();

        BoardList saved = repo.save(list.get());
        messagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = "/insertAt/{id}")
    public ResponseEntity<BoardList> insertAt(@PathVariable("id") long listId,
                                              @RequestBody Pair<Long, Card> req) {

        var list = repo.findById(listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long index = req.getFirst();
        Card card = req.getSecond();

        System.out.println("inserting card: ");
        System.out.println(listId + " " + card + " at: " + index);

        list.get().getCards().add(index.intValue(), card);

        BoardList saved = repo.save(list.get());
        messagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }
}
