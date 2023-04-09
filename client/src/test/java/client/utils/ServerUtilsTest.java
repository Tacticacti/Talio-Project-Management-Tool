package client.utils;

import commons.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServerUtilsTest {

    private ServerUtils serverUtils;
    private StompSession stompSession;
    private WebTarget webTarget;
    private Invocation.Builder invocationBuilder;

    @BeforeEach
    public void setUp() {
        stompSession = mock(StompSession.class);
        webTarget = mock(WebTarget.class);
        invocationBuilder = mock(Invocation.Builder.class);

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
