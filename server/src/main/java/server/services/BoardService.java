package server.services;

import commons.Board;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface BoardService {

    public ResponseEntity<Board> deleteList(long boardId, long listId);
    public List<Board> getAll();

    public ResponseEntity<Board> getbyId(long id);

    public ResponseEntity<Board> addBoard(Board board);

    public ResponseEntity<Long> addListToBoard( long boardId, String listName);
}
