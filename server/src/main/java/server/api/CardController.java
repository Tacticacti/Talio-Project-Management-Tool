package server.api;
import commons.Card;

//import commons.Tag;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

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

    @PostMapping(path="/add")
    public ResponseEntity<Card> add(@RequestBody Card card){
        if(card == null) {
            return ResponseEntity.badRequest().build();
        }

        Card saved = repo.save(card);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path="/addTag/{id}")
    public ResponseEntity<Card> addTag(@RequestBody Tag tag, @PathVariable("id") long cardId){
        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Card> card =repo.findById(cardId);
        if(card.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        Card update = card.get();
        update.addTag(tag);
        if(!update.board.getTagLists().contains(tag))
            update.board.addBoardTag(tag);

        Card cardSaved= repo.save(update);

        return ResponseEntity.ok(cardSaved);
    }

    @PostMapping(path="/deleteTag/{id}")
    public ResponseEntity<Card> deleteTag(@RequestBody Tag tag, @PathVariable("id") long cardId){
        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Card> card =repo.findById(cardId);
        if(card.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        Card update = card.get();
        update.removeTag(tag);
        Card cardSaved= repo.save(update);

        return ResponseEntity.ok(cardSaved);
    }

    @PostMapping(path="/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long cardId){
        if(!repo.existsById(cardId)){
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable ("id") long cardId){
        if(!repo.existsById(cardId)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(cardId).get());
    }

}
