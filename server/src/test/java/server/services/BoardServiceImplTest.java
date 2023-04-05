package server.services;

import commons.Board;
import commons.BoardList;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.BoardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class BoardServiceImplTest {


    @Mock
    private SimpMessagingTemplate messagingTemplate;


    @Test
    void deleteList() {
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        Board expectedBoard = new Board("John", "Doe");
        BoardList bl = new BoardList();
        bl.setId(2L);
        expectedBoard.addList(bl);

        when(boardRepository.save(expectedBoard)).thenReturn(expectedBoard);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(expectedBoard));
        when(boardRepository.existsById(1L)).thenReturn(true);

        Board actualBoard = boardService.deleteList(1L, 2L).getBody();

        verify(boardRepository).save(expectedBoard);
        verify(boardRepository).existsById(1L);
        verify(boardRepository).findById(1L);
        verify(messagingTemplate).convertAndSend("/topic/boards", actualBoard);


        assertEquals(expectedBoard, actualBoard);

    }

    @Test
    void deleteListInvalidId(){
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        Board expectedBoard = new Board("John", "Doe");
        BoardList bl = new BoardList();
        bl.setId(2L);
        expectedBoard.addList(bl);

        when(boardRepository.existsById(1L)).thenReturn(false);

        var result = boardService.deleteList(1L,2L);
        verify(boardRepository, never()).save(expectedBoard);
        verify(boardRepository).existsById(1L);
        verify(boardRepository, never()).findById(1L);
        verify(messagingTemplate, never()).convertAndSend("/topic/boards", expectedBoard);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

    }

    @Test
    void getAll() {
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        Board board = new Board("John", "Doe");
        Board board1 = new Board("John", "Doe");
        Board board2 = new Board();
        List<Board> boards = new ArrayList<>();
        boards.add(board1);
        boards.add(board1);
        boards.add(board1);
        when(boardRepository.findAll()).thenReturn(boards);

        List<Board> actualBoards = boardService.getAll();

        verify(boardRepository).findAll();

        assertEquals(boards, actualBoards);
    }

    @Test
    void getbyId() {
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        Board expectedBoard = new Board("John", "Doe");
        when(boardRepository.findById(1L)).thenReturn(Optional.of(expectedBoard));
        when(boardRepository.existsById(1L)).thenReturn(true);

        Board actualBoard = boardService.getbyId(1L).getBody();

        verify(boardRepository).existsById(1L);
        verify(boardRepository).findById(1L);

        assertEquals(expectedBoard, actualBoard);
    }

    @Test
    void getByInvalidId(){
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);


        when(boardRepository.existsById(1L)).thenReturn(false);

        var actualBoard = boardService.getbyId(1L);

        verify(boardRepository).existsById(1L);
        verify(boardRepository, never()).findById(1L);

        assertEquals(HttpStatus.BAD_REQUEST, actualBoard.getStatusCode());
    }

    @Test
    void addBoard() {
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        Board expectedBoard = new Board("John", "Doe");
        when(boardRepository.save(expectedBoard)).thenReturn(expectedBoard);

        Board actualBoard = boardService.addBoard(expectedBoard).getBody();

        verify(boardRepository).save(expectedBoard);
        verify(messagingTemplate).convertAndSend("/topic/boards", actualBoard);

        assertEquals(expectedBoard, actualBoard);
    }

    @Test
    void addNullBoard() {
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        Board expectedBoard = new Board("John", "Doe");
        when(boardRepository.save(expectedBoard)).thenReturn(expectedBoard);

        var actualBoard = boardService.addBoard(null);

        verify(boardRepository, never()).save(expectedBoard);
        verify(boardRepository, never()).save(null);
        verify(messagingTemplate, never()).convertAndSend("/topic/boards", expectedBoard);

        assertEquals(HttpStatus.BAD_REQUEST, actualBoard.getStatusCode());
    }

    @Test
    void addListToBoard() {
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        Board expectedBoard = new Board("John", "Doe");
        BoardList bl = new BoardList("b");
        bl.setId(1L);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(expectedBoard));
        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.save(expectedBoard)).thenReturn(expectedBoard);


        var actualBoard = boardService.addListToBoard(1L, "b");

        verify(boardRepository).save(expectedBoard);
        verify(messagingTemplate).convertAndSend("/topic/boards", expectedBoard);

        assertEquals(expectedBoard.getLists().get(0).getName(), "b");
        assertEquals(actualBoard.getStatusCode(), HttpStatus.OK);

    }

    @Test
    void addListToInvalidBoard(){
        BoardRepository boardRepository = mock(BoardRepository.class);
        BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, messagingTemplate);

        when(boardRepository.existsById(1L)).thenReturn(false);
        Board update = new Board();
        var actualBoard = boardService.addListToBoard(1L, "nh");

        verify(boardRepository).existsById(1L);
        verify(boardRepository, never()).findById(1L);
        verify(boardRepository, never()).save(update);
        verify(messagingTemplate, never()).convertAndSend("/topic/boards", update);

        assertEquals(HttpStatus.BAD_REQUEST, actualBoard.getStatusCode());
    }

}
