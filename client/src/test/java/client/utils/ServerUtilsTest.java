package client.utils;

import commons.Board;
import commons.Card;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
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
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
