package server.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import commons.Board;
import commons.Card;
import server.database.BoardListRepository;
import server.database.BoardRepository;
import server.database.CardRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketController extends TextWebSocketHandler {

    private final Map<WebSocketSession, String> sessions = new ConcurrentHashMap<>();

    private final CardRepository cardRepository;

    private final BoardListRepository boardListRepository;
    private final BoardRepository boardRepository;

    public WebSocketController(CardRepository cardRepository, BoardListRepository boardListRepository,
                               BoardRepository boardRepository) {
        this.cardRepository = cardRepository;
        this.boardListRepository = boardListRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(payload);

        // Parse the incoming message and determine which action should be taken
        String messageType = jsonNode.get("type").asText();
        switch (messageType) {
            case "board_update":
                // Update the board in the database
                Board board = objectMapper.readValue(jsonNode.get("board").toString(), Board.class);
                boardRepository.save(board);
                // Send a message to all connected clients to inform them of the change
                for (WebSocketSession client : sessions.keySet()) {
                    if (client.isOpen()) {
                        client.sendMessage(message);
                    }
                }
                break;
            case "card_update":
                // Update the card in the database
                Card card = objectMapper.readValue(jsonNode.get("card").toString(), Card.class);
                cardRepository.save(card);
                // Send a message to all connected clients to inform them of the change
                for (WebSocketSession client : sessions.keySet()) {
                    if (client.isOpen()) {
                        client.sendMessage(message);
                    }
                }
                break;
            case "connection_request":
                // Add the client to the sessions map
                sessions.put(session, "");
                break;
            case "disconnection_request":
                // Remove the client from the sessions map
                sessions.remove(session);
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Add the new client to the sessions map
        sessions.put(session, "");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Remove the disconnected client from the sessions map
        sessions.remove(session);
    }

}
