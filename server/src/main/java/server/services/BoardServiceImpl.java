package server.services;

import commons.Board;
import commons.BoardList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

import java.util.List;
import java.util.Objects;

@Service
public class BoardServiceImpl implements BoardService {

    private BoardRepository boardRepository;

    SimpMessagingTemplate simpMessagingTemplate;

    public BoardServiceImpl(BoardRepository boardRepository
            , SimpMessagingTemplate simpMessagingTemplate){
        this.boardRepository = boardRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    public ResponseEntity<Board> deleteList(long boardId, long listId){
        if (!boardRepository.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("deleting " + boardId + " " + listId);

        Board board = boardRepository.findById(boardId).get();
        board.getLists().removeIf(x -> Objects.equals(x.getId(), listId));
        boardRepository.save(board);
        simpMessagingTemplate.convertAndSend("/topic/boards", board);
        return ResponseEntity.ok(board);
    }

    @Override
    public List<Board> getAll(){
        return boardRepository.findAll();
    }

    @Override
    public ResponseEntity<Board> getbyId(long id){
        if(!boardRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(boardRepository.findById(id).get());
    }

    @Override

    public ResponseEntity<Board> addBoard(Board board){
        if(board == null) {
            return ResponseEntity.badRequest().build();
        }
        Board saved = boardRepository.save(board);
        simpMessagingTemplate.convertAndSend("/topic/boards", saved);
        return ResponseEntity.ok(saved);
    }

    @Override

    public ResponseEntity<Long> addListToBoard( long boardId, String listName){
        System.out.println("addListToBoard: ");
        System.out.println(boardId + " " + listName);

        if (!boardRepository.existsById(boardId)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = boardRepository.findById(boardId).get();
        board.addList(new BoardList(listName));
        Board saved = boardRepository.save(board);
        simpMessagingTemplate.convertAndSend("/topic/boards", saved);
        int index = saved.getLists().size()-1;
        Long listId = saved.getLists().get(index).getId();
        return ResponseEntity.ok(listId);
    }
}
