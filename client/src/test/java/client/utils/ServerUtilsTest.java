package client.utils;

import commons.Board;
import commons.BoardList;
import commons.Card;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;


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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;


public class ServerUtilsTest {

    private ServerUtils serverUtils;
    private StompSession stompSession;
    private WebTarget webTarget;
    private Invocation.Builder invocationBuilder;
    MockWebServer server = new MockWebServer();

    Client client;

    @BeforeEach
    public void setUp() throws IOException {
        Mockito.framework().clearInlineMocks();
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
        server.close();
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
   public void addEmptyList() throws IOException {
        server.start();
        serverUtils.setTestServer(server.url("/").toString());
        Board board = new Board("3252");
        board.setId(2L);
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
        when(invocationBuilder.get(Board.class)).thenReturn(board);

//        // Mock the behavior of the Response
//        Mockito.when(response.getStatus()).thenReturn(200);
//        Mockito.when(response.readEntity(Board.class)).thenReturn(expectedBoard);

        Long result = serverUtils.addEmptyList(2L, "Test List");

        assertNotNull(board);

        server.shutdown();
    }



    @Test
    public void changeListName() throws Exception {
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock board list with the expected name
        BoardList expectedList = new BoardList();
        expectedList.setId(1L);
        expectedList.setName("List");

        mockStatic(ClientBuilder.class);
        // Mock the behavior of the ClientBuilder
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the behavior of the WebTarget
        Mockito.when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        Mockito.when(invocationBuilder.accept(MediaType.APPLICATION_JSON))
                .thenReturn(invocationBuilder);
        when(invocationBuilder.post(Entity.entity("List", APPLICATION_JSON), BoardList.class))
                .thenReturn(expectedList);

        // Call the method being tested
        BoardList actualList = serverUtils.changeListName(1L, "List");

        // Verify that the expected request was sent
        verify(webTarget).path("api/lists/changeName/1");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(Entity.entity("List", APPLICATION_JSON), BoardList.class);

        // Verify that the returned BoardList has the expected name
        assertEquals(expectedList.getName(), actualList.getName());

        server.shutdown();
    }

    @Test
    public void removeBoardList() throws Exception {
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock board list with the expected name
        BoardList expectedList = new BoardList();
        expectedList.setId(1L);
        expectedList.setName("List");

        mockStatic(ClientBuilder.class);
        // Mock the behavior of the ClientBuilder
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the behavior of the WebTarget
        Mockito.when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        Mockito.when(invocationBuilder.accept(MediaType.APPLICATION_JSON))
                .thenReturn(invocationBuilder);
        when(invocationBuilder.post(Entity.entity("List", APPLICATION_JSON), BoardList.class))
                .thenReturn(expectedList);

        serverUtils.removeBoardList(1L, 1L);


        server.shutdown();
    }

    @Test
    public void updateCardFromList() throws IOException {
        server.start();
        serverUtils.setTestServer(server.url("/").toString());
        Card card = new Card("Card");
        card.setId(1L);
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

        serverUtils.updateCardFromList(1L, card);

        verify(webTarget).path("api/lists/update/1");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(Entity.entity(card, APPLICATION_JSON), BoardList.class);
    }

    @Test
    public void testAddCard() throws IOException {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock Card object with some data
        Card mockCard = new Card("Card");
        mockCard.setId(1L);

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.
                post(Entity.entity(mockCard, APPLICATION_JSON), Card.class)).thenReturn(mockCard);

        // Call the method being tested
        Card result = serverUtils.addCard(mockCard);

        // Verify that the expected request was sent
        verify(webTarget).path("api/cards/add");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(Entity.entity(mockCard, APPLICATION_JSON), Card.class);

        // Verify that the returned Card object matches the expected one
        assertEquals(mockCard, result);

        // Shutdown the mock server
        server.shutdown();
    }

    @Test
    public void testGetCardById() throws Exception {
        // Start the server and set the server URL in the serverUtils object
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock card with the expected ID and name
        Card expectedCard = new Card("Card");
        expectedCard.setId(1L);

        // Mock the behavior of the ClientBuilder
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(anyString())).thenReturn(webTarget);

        // Mock the behavior of the WebTarget
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.get(any(GenericType.class))).thenReturn(expectedCard);

        // Call the method being tested
        Card result = serverUtils.getCardById(1L);

        // Verify that the expected request was sent
        verify(webTarget).path("api/cards/1");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).get(any(GenericType.class));

        // Verify that the method returns the expected result
        assertEquals(expectedCard, result);

        // Stop the server
        server.shutdown();
    }

    @Test
    public void addBoard() throws Exception {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock Card object with some data
        Board mockBoard = new Board("Board");
        mockBoard.setId(1L);

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);

        // Mock the Response object and return it from the post() method call
        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
        when(mockResponse.readEntity(Board.class)).thenReturn(mockBoard);
        when(invocationBuilder.post(Entity.entity(mockBoard, APPLICATION_JSON)))
                .thenReturn(mockResponse);

        // Call the method being tested
        Board result = serverUtils.addBoard(mockBoard);

        // Verify that the expected request was sent
        verify(webTarget).path("api/boards/add");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(Entity.entity(mockBoard, APPLICATION_JSON));

        // Verify that the returned Board object matches the expected one
        assertEquals(mockBoard, result);

        // Shutdown the mock server
        server.shutdown();
    }

    @Test
    public void deleteCardFromList() throws IOException {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock Card and BoardList object with some data
        Card mockCard = new Card("Card", "Description");
        mockCard.setId(1L);
        BoardList mockBoardList = new BoardList("List");
        mockBoardList.setId(2L);
        mockBoardList.addCard(mockCard);

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.post(any(Entity.class),
                eq(BoardList.class))).thenReturn(mockBoardList);

        // Call the method being tested
        BoardList result = serverUtils.deleteCardFromList
                (mockBoardList.getId(), mockCard, true);

        // Verify that the expected request was sent
        verify(webTarget).path("api/lists/deleteCard/" + mockBoardList.getId());
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(any(Entity.class), eq(BoardList.class));

        // Verify that the returned BoardList object matches the expected one
        assertEquals(mockBoardList, result);

        // Shutdown the mock server
        server.shutdown();
    }


    @Test
    public void getList() throws IOException {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock BoardList object with some data
        BoardList mockBoardList = new BoardList("List");

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.get(BoardList.class)).thenReturn(mockBoardList);

        // Call the method being tested
        BoardList result = serverUtils.getList(1L);

        // Verify that the expected request was sent
        verify(webTarget).path("api/lists/1");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).get(BoardList.class);

        // Verify that the returned BoardList object matches the expected one
        assertEquals(mockBoardList, result);

        // Shutdown the mock server
        server.shutdown();
    }

    @Test
    public void addCardAtIndex() throws Exception {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock Card, BoardList and index
        Card mockCard = new Card("Card", "Description");
        mockCard.setId(1L);
        BoardList mockList = new BoardList("List");
        mockList.setId(1L);
        long mockIndex = 0L;

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.post(any(Entity.class), eq(BoardList.class)))
                .thenReturn(mockList);

        // Call the method being tested
        serverUtils.addCardAtIndex(mockList.getId(), mockIndex, mockCard);

        // Verify that the expected request was sent
        verify(webTarget).path("api/lists/insertAt/" + mockList.getId());
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(
                any(Entity.class), eq(BoardList.class));

        // Shutdown the mock server
        server.shutdown();
    }

    @Test
    public void testCheckAdminPassword() throws IOException {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.post(any(Entity.class), eq(boolean.class))).thenReturn(true);

        // Call the method being tested
        boolean result = serverUtils.checkAdminPassword("password");

        // Verify that the expected request was sent
        verify(webTarget).path("adminLogin");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(any(Entity.class), eq(boolean.class));

        // Verify that the returned value matches the expected one
        assertTrue(result);

        // Shutdown the mock server
        server.shutdown();
    }


    @Test
    public void testVerifyBoardPassword() throws IOException {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Create a mock board ID and password
        Long boardId = 1L;
        String password = "password123";

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).
                thenReturn(invocationBuilder);
        when(invocationBuilder.post(any(Entity.class), eq(boolean.class))).
                thenReturn(true);

        // Call the method being tested
        boolean result = serverUtils.verifyBoardPassword(boardId, password);

        // Verify that the expected request was sent
        verify(webTarget).path("api/boards/verifyPassword/" + boardId);
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(any(Entity.class), eq(boolean.class));

        // Verify that the returned boolean matches the expected one
        assertTrue(result);

        // Shutdown the mock server
        server.shutdown();
    }


    @Test
    public void testSetBoardPassword() throws IOException {
        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.post(any(Entity.class), eq(Board.class))).thenReturn(new Board());

        // Call the method being tested
        serverUtils.setBoardPassword(1L, "password");

        // Verify that the expected request was sent
        verify(webTarget).path("api/boards/changePassword/1");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).post(any(Entity.class), eq(Board.class));

        // Shutdown the mock server
        server.shutdown();
    }

    @Test
    public void testRemoveBoardPassword() throws IOException {
        // Create a mock board
        Board mockBoard = new Board("Mock Board");

        // Start the mock server
        server.start();
        serverUtils.setTestServer(server.url("/").toString());

        // Mock the ClientBuilder and its behavior
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newClient(any(ClientConfig.class))).thenReturn(client);
        when(client.target(Mockito.anyString())).thenReturn(webTarget);

        // Mock the WebTarget and its behavior
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.accept(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        when(invocationBuilder.get(Board.class)).thenReturn(mockBoard);

        // Call the method being tested
        serverUtils.removeBoardPassword(mockBoard.getId());

        // Verify that the expected request was sent
        verify(webTarget).path("api/boards/removePassword/" + mockBoard.getId());
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).accept(MediaType.APPLICATION_JSON);
        verify(invocationBuilder).get(Board.class);

        // Shutdown the mock server
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
