/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import commons.Tag;
import jakarta.ws.rs.core.Response;

import commons.BoardList;
import org.glassfish.jersey.client.ClientConfig;

import commons.Board;
import commons.Card;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

class CustomPair<S, T> {
    private S first;
    private T second;

    public CustomPair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    public S getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public void setFirst(S first) {
        this.first = first;
    }

    public void setSecond(T second) {
        this.second = second;
    }
}

public class ServerUtils {

    // private static String server = "http://localhost:8080/";
    private static String server = "";
    public LocalUtils localUtils;

    StompSession stompSession;


    // returns true if connection is succesful 
    // flase otherwise
    public boolean check(String addr) throws IOException {

        boolean res = false;

        URL url = new URL(addr + "/TalioPresent");
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();

        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            res = true;

        return res;
    }

    public void setServer(String addr) {
        // TODO open socket connection here
        server = addr;

        stompSession = connectToSockets("ws://localhost:8080/websocket");
    }

    StompSession connectToSockets(String url){
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try{
            return stomp.connect(url, new StompSessionHandlerAdapter(){}).get();
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new IllegalStateException();
    }

    public <T> void checkForUpdatesToRefresh(String update, Class<T> tClass, Consumer<T> consumer){
        stompSession.subscribe(update, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return tClass;
            }


            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    public String getPath() {
        return server;
    }

    public void disconnect() {
        stompSession.disconnect();
        // TODO probably close sockets here
        server = "";

    }

    public Board getBoardById(Long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/boards/" + id.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(Board.class);
    }

    public List<Board> getBoards() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/boards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Board>>() {
                });
    }

    public BoardList addCardToList(Long boardListId, Card card) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/lists/add/" + boardListId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(card, APPLICATION_JSON), BoardList.class);
    }

    public Board addTagToBoard(Long boardListId, Tag tag) {
        System.out.println("reached tag");
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/boards/addTag/" + boardListId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(tag, APPLICATION_JSON), Board.class);
    }

    public Board deleteTagToBoard(Long boardListId, Tag tag) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/boards/tag/delete/" + boardListId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(tag, APPLICATION_JSON), Board.class);
    }

    public Card addTagToCard(Long cardId, Tag tag) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/cards/addTag/" + cardId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(tag, APPLICATION_JSON), Card.class);
    }

    public Card deleteTagToCard(Long cardId, Tag tag) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/cards/deleteTag/" + cardId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(tag, APPLICATION_JSON), Card.class);
    }


    public Long addEmptyList(Long boardId, String name) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/boards/add/list/" + boardId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(name, APPLICATION_JSON), Long.class);
    }

    public BoardList changeListName(Long listId, String name) throws Exception {
        if(name.equals("")) {
            throw new Exception("cannot change name to empty name");
        }

        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/lists/changeName/" + listId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(name, APPLICATION_JSON), BoardList.class);
    }

    public Void removeBoardList(Long boardId, Long listId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/boards/list/delete/" + boardId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(listId, APPLICATION_JSON), Void.class);
    }

    public BoardList updateCardFromList(Long boardListId, Card card) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/lists/update/" + boardListId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(card, APPLICATION_JSON), BoardList.class);
    }

    public Card addCard(Card card) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/cards/add") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(card, APPLICATION_JSON), Card.class);
    }

    public Card getCardById(Long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/cards/"+id.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Card>() {});
    }


    public Board addBoard(Board board) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/boards/add")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board, APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(Board.class);
        } else {
            throw new RuntimeException("Failed to add board. HTTP error code: " +
                    response.getStatus());
        }
    }

    public void deleteBoardById(Long id) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/boards/" + id.toString())
                .request()
                .delete();
    }

    public Card deleteCard(Long cardId) {
        return  ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/cards/delete/" + cardId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(getCardById(cardId), APPLICATION_JSON), Card.class);
    }

    public BoardList deleteCardFromList(Long listId, Card card){
        System.out.println(card);
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/lists/deleteCard/" + listId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(card, APPLICATION_JSON), BoardList.class);
    }

    public BoardList getList(Long listId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/lists/" + listId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(BoardList.class);
    }

    public BoardList addCardAtIndex(Long listId, long index, Card card) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/lists/insertAt/" + listId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(new CustomPair(index, card), APPLICATION_JSON), BoardList.class
                );
    }

    private static ExecutorService EXEC = Executors.newSingleThreadExecutor();
    public void registerForCardUpdate(Consumer<Card> cardConsumer){
        EXEC = Executors.newSingleThreadExecutor();
        EXEC.submit(()->{
            System.out.println("running");
            while(!Thread.interrupted()) {
                var result = ClientBuilder.newClient(new ClientConfig())
                        .target(server).path("api/lists/deletedtask")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);
                if (result.getStatus() == 204) {
                    continue;
                }
                System.out.println("sent card here");
                result.getStatus();
                var card = result.readEntity(Card.class);
                cardConsumer.accept(card);
            }
        });

    }
    public void stopExec(){
        EXEC.shutdownNow();
    }
}
