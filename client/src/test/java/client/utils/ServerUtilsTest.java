package client.utils;

import commons.Board;
import commons.BoardList;
import commons.Card;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import okhttp3.mockwebserver.MockWebServer;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ServerUtilsTest {

    private ServerUtils serverUtils;
    private StompSession stompSession;
    private WebTarget webTarget;
    private Invocation.Builder invocationBuilder;
    MockWebServer server = new MockWebServer();

    Client client;

    @BeforeEach
    public void setUp() throws IOException {
       // server.start();
        stompSession = mock(StompSession.class);
        webTarget = mock(WebTarget.class);
        invocationBuilder = mock(Invocation.Builder.class);
        client = mock(Client.class);


        serverUtils = new ServerUtils() {
            @Override
            StompSession connectToSockets(String url) {
                return stompSession;
            }
        };
        serverUtils.localUtils = new LocalUtils();
        serverUtils.stompSession = stompSession;
    }

    @Test
    public void testSetServer() {
        String serverAddress = "localhost:8080";
        serverUtils.setServer(serverAddress);

        assertEquals("http://" + serverAddress, serverUtils.getPath());
    }


    @Test
    public void getBoardById() throws IOException {
        server.start();
        serverUtils.setTestServer(server.url("/").toString());
        Board expectedBoard = new Board( "Test Board");
        Response response = mock(Response.class);
        expectedBoard.setId(1L);

        mockStatic(ClientBuilder.class);
        // Mock the behavior of the ClientBuilder
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the behavior of the WebTarget
        Mockito.when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        Mockito.when(invocationBuilder.accept(MediaType.APPLICATION_JSON))
                .thenReturn(invocationBuilder);
        when(invocationBuilder.get(Board.class)).thenReturn(expectedBoard);

//        // Mock the behavior of the Response
//        Mockito.when(response.getStatus()).thenReturn(200);
//        Mockito.when(response.readEntity(Board.class)).thenReturn(expectedBoard);

        Board actualBoard = serverUtils.getBoardById(1L);

        assertEquals(expectedBoard, actualBoard);

        server.shutdown();


    }

    @Test
    public void getBoards() throws IOException {

        server.start();
        serverUtils.setTestServer(server.url("/").toString());
        List<Board> expectedBoards = Arrays.asList(new Board("Board 1"), new Board("Board 2"));
        Response response = mock(Response.class);

        mockStatic(ClientBuilder.class);
        // Mock the behavior of the ClientBuilder
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the behavior of the WebTarget
        Mockito.when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        Mockito.when(invocationBuilder.accept(MediaType.APPLICATION_JSON))
                .thenReturn(invocationBuilder);
        when(invocationBuilder.get(new GenericType<List<Board>>() {})).thenReturn(expectedBoards);

        List<Board> actualBoards= serverUtils.getBoards();

        assertEquals(expectedBoards, actualBoards);

        server.shutdown();
    }

    @Test
    public void addCardToList() throws IOException {
        server.start();
        serverUtils.setTestServer(server.url("/").toString());
        Card card = new Card("Test Card");
        Response response = mock(Response.class);


        mockStatic(ClientBuilder.class);
        // Mock the behavior of the ClientBuilder
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);
        
        // Mock the behavior of the WebTarget
        Mockito.when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        Mockito.when(invocationBuilder.accept(MediaType.APPLICATION_JSON))
                .thenReturn(invocationBuilder);
        when(invocationBuilder.get(Card.class)).thenReturn(card);

//        // Mock the behavior of the Response
//        Mockito.when(response.getStatus()).thenReturn(200);
//        Mockito.when(response.readEntity(Board.class)).thenReturn(expectedBoard);

        serverUtils.addCardToList(1L, card);

        verify(webTarget).path("api/lists/add/1");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(Entity.entity(card, MediaType.APPLICATION_JSON),
                BoardList.class);

        server.shutdown();
    }

    @Test
    public void testCheckForUpdatesToRefresh() {
        String update = "/topic/update";
        Class<Card> tClass = Card.class;
        Consumer<Card> consumer = card -> System.out.println("Card received: " + card);

        serverUtils.checkForUpdatesToRefresh(update, tClass, consumer);

        verify(stompSession).subscribe(eq(update), any(StompFrameHandler.class));
    }

    @Test
    public void testDisconnect() {
        serverUtils.disconnect();

        verify(stompSession).disconnect();
    }

    @Test
    public void testRegisterForCardUpdateAndStopExec() throws InterruptedException {
        Consumer<Card> cardConsumer = card -> System.out.println("Card received: " + card);

        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(anyString())).thenReturn(invocationBuilder);

        // Mock the response objects
        Response noContentResponse = mock(Response.class);
        when(noContentResponse.getStatus()).thenReturn(Response.Status.NO_CONTENT.getStatusCode());

        Response okResponse = mock(Response.class);
        when(okResponse.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
        when(okResponse.readEntity(Card.class)).thenReturn(new Card());

        when(invocationBuilder.get(Response.class))
                .thenReturn(noContentResponse, okResponse);

        serverUtils.registerForCardUpdate(cardConsumer);

        Thread.sleep(1000); // Allow time for the executor to process the messages
        serverUtils.stopExec();
    }
}
