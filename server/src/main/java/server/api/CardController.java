package server.api;
import commons.Card;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.CardRepository;

//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardRepository repo;

    public CardController(CardRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = {"", "/"})
    public List<Card> getAll() {
        return repo.findAll();
    }

    @PostMapping(path={"", "/add"})
    public ResponseEntity<Card> add(@RequestBody Card card){
        if(card == null) {
            return ResponseEntity.badRequest().build();
        }

        Card saved = repo.save(card);
        return ResponseEntity.ok(saved);
    }
    @PostMapping(path="/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long cardId){
        System.out.println("-----reach");
        if(!repo.existsById(cardId)){
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path={"", "/{id}"})
    public ResponseEntity<Card> getCardById(@PathVariable ("id") long cardId){
        if(!repo.existsById(cardId)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(cardId).get());
    }

}
