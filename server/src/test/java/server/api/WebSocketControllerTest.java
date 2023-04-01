package server.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import server.database.BoardListRepository;
import server.database.BoardRepository;
import server.database.CardRepository;

import java.util.HashMap;
import java.util.Map;

public class WebSocketControllerTest {
    private WebSocketController webSocketController;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardListRepository boardListRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private WebSocketSession session;
    private Map<String, WebSocketSession> sessions;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webSocketController = new WebSocketController(cardRepository, boardListRepository, boardRepository);
        sessions = new HashMap<>();
    }

    @Test
    public void testHandleTextMessageBoardUpdate() throws Exception {
        String boardJson = "{\"id\": \"1234\", \"title\": \"Test Board\"}";
        TextMessage message = new TextMessage("{\"type\": \"board_update\", \"board\": "
                + boardJson + "}");
        webSocketController.handleTextMessage(session, message);

        verify(boardRepository, times(1)).save(argThat(
                board -> board.getId() == 1234L &&
                        board.getName().equals("Test Board")));

        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessageCardUpdate() throws Exception {
        String cardJson = "{\"id\": \"5678\", "
                + "\"title\": \"Test Card\", "
                + "\"description\": \"This is a test card\"}";
        TextMessage message = new TextMessage("{\"type\": \"card_update\", \"card\": "
                + cardJson + "}");
        webSocketController.handleTextMessage(session, message);

        verify(cardRepository, times(1)).save(argThat(
                card -> card.getId() == 5678L &&
                        card.getTitle().equals("Test Card") &&
                        card.getDescription().equals("This is a test card")));

        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessageConnectionRequest() throws Exception {
        TextMessage message = new TextMessage("{\"type\": \"connectionRequest\"}");
        webSocketController.handleTextMessage(session, message);

        verify(session, times(1)).getId();
        verify(session, times(1)).getAttributes();
    }

    @Test
    public void testHandleTextMessageDisconnectionRequest() throws Exception {
        TextMessage message = new TextMessage("{\"type\": \"disconnectionRequest\"}");
        sessions.put(session.getId(), session);
        webSocketController.handleTextMessage(session, message);

        verify(session, times(1)).getId();
        verify(session, times(1)).getAttributes();
        verify(session, times(1)).close();
        verify(session, times(1)).isOpen();
        verifyNoMoreInteractions(session);
    }

    @Test
    public void testAfterConnectionEstablished() {
        webSocketController.afterConnectionEstablished(session);

        verify(session, times(1)).getId();
        verify(session, times(1)).getAttributes();
        verifyNoMoreInteractions(session);
    }

    @Test
    public void testAfterConnectionClosed() {
        sessions.put(session.getId(), session);
        webSocketController.afterConnectionClosed(session, null);

        verify(session, times(1)).getId();
        verify(session, times(1)).getAttributes();
        verifyNoMoreInteractions(session);
    }
}
