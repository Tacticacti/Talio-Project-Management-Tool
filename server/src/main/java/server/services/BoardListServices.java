package server.services;

import commons.BoardList;
import commons.Card;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface BoardListServices {
    public List<BoardList> getAll();

    public ResponseEntity<BoardList> getById(long id);

    public ResponseEntity<BoardList> addList(BoardList bl);

    public ResponseEntity<BoardList> changeName(long listId, String listName);

    public ResponseEntity<Void> deleteList(long listId);

    public ResponseEntity<BoardList> addCard(long listId, Card card);

    public ResponseEntity<BoardList> deleteCard(long listId, Card card);
    public ResponseEntity<BoardList> insertAt(long listId, Pair<Long, Card> req);
}
