package server.services;

import commons.Board;
import commons.BoardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.BoardListRepository;
import server.database.BoardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
class BoardListServiceImplTest {
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    private BoardListRepository boardListRepository;

    private BoardListServiceImpl boardListService;

    @BeforeEach
    void setUp(){
         boardListRepository = mock(BoardListRepository.class);
         simpMessagingTemplate = mock(SimpMessagingTemplate.class);
         boardListService = new BoardListServiceImpl(boardListRepository, simpMessagingTemplate);

    }

    @Test
    void getAll() {

        BoardList bl = new BoardList("John");
        BoardList bl1 = new BoardList("John");
        BoardList bl2 = new BoardList();
        List<BoardList> boardLists = new ArrayList<>();
        boardLists.add(bl);
        boardLists.add(bl1);
        boardLists.add(bl2);
        when(boardListRepository.findAll()).thenReturn(boardLists);

        List<BoardList> actualBoards = boardListService.getAll();

        verify(boardListRepository).findAll();

        assertEquals(boardLists,actualBoards);
    }

    @Test
    void getById() {


        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.of(bl));
        when(boardListRepository.existsById(1L)).thenReturn(true);

        BoardList actBl = boardListService.getById(1L).getBody();

        verify(boardListRepository).existsById(1L);
        verify(boardListRepository).findById(1L);

        assertEquals(bl,actBl);

    }

    @Test
    void addList() {
        BoardList bl = new BoardList();

        when(boardListRepository.save(bl)).thenReturn(bl);

        BoardList result = boardListService.addList(bl).getBody();

        verify(boardListRepository).save(bl);
        verify(simpMessagingTemplate).convertAndSend("/topic/lists",bl);

        assertEquals(result,bl);


    }

    @Test
    void addNullList() {
        BoardList bl = new BoardList();

        when(boardListRepository.save(bl)).thenReturn(bl);

        var result = boardListService.addList(null);

        verify(boardListRepository, never()).save(bl);
        verify(simpMessagingTemplate,never()).convertAndSend("/topic/lists",bl);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }

    @Test
    void changeName() {
    }

    @Test
    void deleteList() {
    }

    @Test
    void addCard() {
    }

    @Test
    void deleteCard() {
    }

    @Test
    void insertAt() {
    }
}