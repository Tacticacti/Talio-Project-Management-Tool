package server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.mockito.Mockito.verify;

public class WebSocketConfigTest {

    @Mock
    private StompEndpointRegistry stompEndpointRegistry;

    @Mock
    private MessageBrokerRegistry messageBrokerRegistry;

    private WebSocketConfig webSocketConfig;

    @BeforeEach
    public void setUp() {
        System.out.println("Initializing mocks and system-under-test...");
        MockitoAnnotations.openMocks(this);
        webSocketConfig = new WebSocketConfig();
    }

    @Test
    public void testRegisterStompEndpoints() {
        System.out.println("Testing registerStompEndpoints...");
        webSocketConfig.registerStompEndpoints(stompEndpointRegistry);
        verify(stompEndpointRegistry).addEndpoint("/websocket");
        System.out.println("registerStompEndpoints test passed.");
    }

    @Test
    public void testConfigureMessageBroker() {
        System.out.println("Testing configureMessageBroker...");
        webSocketConfig.configureMessageBroker(messageBrokerRegistry);
        verify(messageBrokerRegistry).enableSimpleBroker("/topic");
        verify(messageBrokerRegistry).setApplicationDestinationPrefixes("/app");
        System.out.println("configureMessageBroker test passed.");
    }
}
