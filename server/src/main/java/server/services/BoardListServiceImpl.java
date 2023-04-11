package server.services;

import commons.BoardList;
import commons.Card;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.BoardListRepository;

import java.util.List;

@Service
public class BoardListServiceImpl implements BoardListServices {
    SimpMessagingTemplate simpMessagingTemplate;
    BoardListRepository boardListRepository;

    public BoardListServiceImpl(BoardListRepository boardListRepository
            , SimpMessagingTemplate simpMessagingTemplate){
        this.boardListRepository = boardListRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    public List<BoardList> getAll(){
        return boardListRepository.findAll();
    }

    public ResponseEntity<BoardList> getById(long id){
        if(!boardListRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(boardListRepository.findById(id).get());
    }

    public ResponseEntity<BoardList> addList(BoardList bl){
        if(bl == null) {
            return ResponseEntity.badRequest().build();
        }

        BoardList saved = boardListRepository.save(bl);
        simpMessagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<BoardList> changeName(long listId, String listName){
        var result = boardListRepository.findById(listId);

        if(result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BoardList saved = result.get();
        saved.setName(listName);

        saved = boardListRepository.save(saved);
        simpMessagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<Void> deleteList(long listId){
        if(!boardListRepository.existsById(listId)) {
            return ResponseEntity.badRequest().build();
        }

        // System.out.println("deleting " + listId);
        BoardList bl = boardListRepository.getById(listId);
        boardListRepository.deleteById(listId);
        simpMessagingTemplate.convertAndSend("/topic/lists", bl);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BoardList> addCard(long listId, Card card){
        // System.out.println("add card: " + listId + " " + card);

        var list = boardListRepository.findById(listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        list.get().addCard(card);

        BoardList saved = boardListRepository.save(list.get());
        simpMessagingTemplate.convertAndSend("/topic/lists", saved);

        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<BoardList> deleteCard(long listId, Card card){
        // System.out.println("delete from: " + listId + " " + card);

        var list = boardListRepository.findById(listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        list.get().getCards().removeIf(x -> x.getId() == card.getId());

        BoardList saved = boardListRepository.save(list.get());
        simpMessagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }
    public ResponseEntity<BoardList> insertAt(long listId, Pair<Long, Card> req){
        var list = boardListRepository.findById(listId);

        if(list.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long index = req.getFirst();
        Card card = req.getSecond();

        // System.out.println("inserting card: ");
        // System.out.println(listId + " " + card + " at: " + index);

        list.get().getCards().add(index.intValue(), card);

        BoardList saved = boardListRepository.save(list.get());
        simpMessagingTemplate.convertAndSend("/topic/lists", saved);
        return ResponseEntity.ok(saved);
    }

}
