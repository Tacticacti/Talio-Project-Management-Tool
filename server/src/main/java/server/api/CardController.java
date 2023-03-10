package server.api;
import commons.Card;

import server.database.CardRepository;

//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
