package server.services;

import commons.BoardList;
import commons.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.BoardListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        assertEquals(boardLists, actualBoards);
    }

    @Test
    void getById() {


        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.of(bl));
        when(boardListRepository.existsById(1L)).thenReturn(true);

        BoardList actBl = boardListService.getById(1L).getBody();

        verify(boardListRepository).existsById(1L);
        verify(boardListRepository).findById(1L);

        assertEquals(bl, actBl);

    }

    @Test
    void getByInvalidId(){
        BoardList bl = new BoardList();
        when(boardListRepository.existsById(1L)).thenReturn(false);

        var actBl = boardListService.getById(1L);

        verify(boardListRepository).existsById(1L);
        verify(boardListRepository, never()).findById(1L);

        assertEquals(HttpStatus.BAD_REQUEST, actBl.getStatusCode());
    }

    @Test
    void addList() {
        BoardList bl = new BoardList();

        when(boardListRepository.save(bl)).thenReturn(bl);

        BoardList result = boardListService.addList(bl).getBody();

        verify(boardListRepository).save(bl);
        verify(simpMessagingTemplate).convertAndSend("/topic/lists", bl);

        assertEquals(result, bl);


    }

    @Test
    void addNullList() {
        BoardList bl = new BoardList();

        when(boardListRepository.save(bl)).thenReturn(bl);

        var result = boardListService.addList(null);

        verify(boardListRepository, never()).save(bl);
        verify(simpMessagingTemplate, never()).convertAndSend("/topic/lists", bl);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void changeName() {
        BoardList check = new BoardList("blah");
        BoardList bl = new BoardList();

        when(boardListRepository.save(bl)).thenReturn(bl);
        when(boardListRepository.findById(1L)).thenReturn(Optional.of(bl));

        BoardList result = boardListService.changeName(1L,"blah").getBody();

        verify(boardListRepository).findById(1L);
        verify(boardListRepository).save(bl);
        verify(simpMessagingTemplate).convertAndSend("/topic/lists", bl);

        assertTrue(result.equals(check));
    }

    @Test
    void changeNameNull(){
        BoardList bl = new BoardList();

        when(boardListRepository.findById(1L)).thenReturn(Optional.empty());

        var result = boardListService.changeName(1L,"blah");

        verify(boardListRepository).findById(1L);
        verify(boardListRepository,never()).save(bl);
        verify(simpMessagingTemplate, never()).convertAndSend("/topic/lists", bl);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());

    }

    @Test
    void deleteList() {
        BoardList bl = new BoardList();

        when(boardListRepository.existsById(1L)).thenReturn(true);
        when(boardListRepository.getById(1L)).thenReturn(bl);
        doNothing().when(boardListRepository).deleteById(1L);

        var result = boardListService.deleteList(1L);

        verify(boardListRepository).existsById(1L);
        verify(boardListRepository).getById(1L);
        verify(boardListRepository).deleteById(1L);
        verify(simpMessagingTemplate).convertAndSend("/topic/lists", bl);

        assertEquals(HttpStatus.OK,result.getStatusCode());

    }

    @Test
    void deleteListInvalidId(){
        BoardList bl = new BoardList();

        when(boardListRepository.existsById(1L)).thenReturn(false);

        var result = boardListService.deleteList(1L);

        verify(boardListRepository).existsById(1L);
        verify(boardListRepository, never()).getById(1L);
        verify(boardListRepository, never()).deleteById(1L);
        verify(simpMessagingTemplate, never()).convertAndSend("/topic/lists", bl);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());

    }

    @Test
    void addCard() {
        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.of(bl));
        when(boardListRepository.save(bl)).thenReturn(bl);
        Card card = new Card();
        var result = boardListService.addCard(1L,card);

        verify(boardListRepository).findById(1L);
        verify(boardListRepository).save(bl);
        verify(simpMessagingTemplate).convertAndSend("/topic/lists",bl);

        assertEquals(bl,result.getBody());
    }
    @Test
    void addCardInvalidId(){
        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.empty());
        when(boardListRepository.save(bl)).thenReturn(bl);
        Card card = new Card();
        var result = boardListService.addCard(1L,card);

        verify(boardListRepository).findById(1L);
        verify(boardListRepository, never()).save(bl);
        verify(simpMessagingTemplate, never()).convertAndSend("/topic/lists",bl);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }

    @Test
    void deleteCard() {
        BoardList bl1 = new BoardList();
        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.of(bl1));
        when(boardListRepository.save(bl1)).thenReturn(bl1);
        Card card = new Card();
        bl1.addCard(card);
        var result = boardListService.deleteCard(1L,card);

        verify(boardListRepository).findById(1L);
        verify(boardListRepository).save(bl1);
        verify(simpMessagingTemplate).convertAndSend("/topic/lists",bl1);

        assertEquals(bl,result.getBody());
    }

    @Test
    void deleteCardInvalidId(){
        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.empty());
        Card card = new Card();
        bl.addCard(card);
        var result = boardListService.deleteCard(1L,card);

        verify(boardListRepository).findById(1L);
        verify(boardListRepository, never()).save(bl);
        verify(simpMessagingTemplate, never()).convertAndSend("/topic/lists",bl);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }

    @Test
    void insertAt() {
        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.of(bl));
        when(boardListRepository.save(bl)).thenReturn(bl);

        Card card = new Card("haha");
        Card card1 = new Card("b");
        Card card2 = new Card("h");
        bl.addCard(card1);
        bl.addCard(card2);
        BoardList bl1 = new BoardList();
        bl1.addCard(card1);
        bl1.addCard(card);
        bl1.addCard(card2);
        Pair<Long, Card> req = Pair.of(1L,card);
        var result = boardListService.insertAt(1L,req);

        verify(boardListRepository).findById(1L);
        verify(boardListRepository).save(bl);
        verify(simpMessagingTemplate).convertAndSend("/topic/lists",bl);
        assertTrue(bl1.equals(result.getBody()));
    }

    @Test
    void insertAtInvalidId(){
        BoardList bl = new BoardList();
        when(boardListRepository.findById(1L)).thenReturn(Optional.empty());
        Card card = new Card();
        Pair<Long, Card> req = Pair.of(1L,card);
        var result = boardListService.insertAt(1L,req);

        verify(boardListRepository).findById(1L);
        verify(boardListRepository, never()).save(bl);
        verify(simpMessagingTemplate, never()).convertAndSend("/topic/lists",bl);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }
}
