package server.api;

import commons.BoardList;
import commons.Card;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import server.DatabaseUtils;
import server.database.BoardListRepository;
import server.services.BoardListServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/lists")
public class BoardListController {

    private final BoardListRepository repo;
    private DatabaseUtils databaseUtils;
    private SimpMessagingTemplate messagingTemplate;


    private BoardListServiceImpl boardListService;

    public BoardListController(BoardListRepository repo,
                               DatabaseUtils databaseUtils
            , SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.databaseUtils = databaseUtils;
        this.messagingTemplate = messagingTemplate;
        this.boardListService = new BoardListServiceImpl(repo, messagingTemplate);
    }

    @GetMapping(path = {"", "/"})
    public List<BoardList> getAll() {

        return boardListService.getAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BoardList> getById(@PathVariable("id") long id) {
        return boardListService.getById(id);

    }

    @PostMapping(path = "/add")
    public ResponseEntity<BoardList> addList(@RequestBody BoardList bl) {
        return boardListService.addList(bl);
    }

    @PostMapping(path = "/changeName/{id}")
    public ResponseEntity<BoardList> changeListsName(@PathVariable("id") long listId,
                                                     @RequestBody String listName) {

        return boardListService.changeName(listId, listName);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable("id") long listId) {

        return boardListService.deleteList(listId);
    }

    @PostMapping(path = "/add/{id}")
    public ResponseEntity<BoardList> addCardToId(@PathVariable("id") long listId,
                                                 @RequestBody Card card) {

        return boardListService.addCard(listId, card);
    }

    @PostMapping(path="/deleteCard/{id}")
    public ResponseEntity<BoardList> deleteCardFromId(@PathVariable ("id") long listId,
                                                      @RequestBody Pair<Boolean, Card> cardPair) {
        boolean permanent = cardPair.getFirst();
        Card card = cardPair.getSecond();
        if(permanent){
            listeners.forEach((k, l)->{
                l.accept(card);
            });
        }
        return boardListService.deleteCard(listId, card);
    }



    @PostMapping(path="/update/{id}")
    public ResponseEntity<BoardList> updateCardInId(@PathVariable("id") long listId,
                                                    @RequestBody Card card){

        System.out.println("updating card: ");
        System.out.println(listId + " " + card);

        var list = repo.findById(card.getBoardList().getId());

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

        return boardListService.insertAt(listId, req);
    }


    private Map<Object, Consumer<Card>> listeners = new HashMap<>();

    @GetMapping("/deletedtask")
    public DeferredResult<ResponseEntity<Card>> cardChanges(){
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Card>>(1000L, noContent);
        var key = new Object();
        listeners.put(key, card ->{
            res.setResult(ResponseEntity.ok(card));
        });
        res.onCompletion(()->{
            listeners.remove(key);
        });
        return res;
    }

}
