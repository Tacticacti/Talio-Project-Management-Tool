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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.glassfish.jersey.client.ClientConfig;

import commons.Board;
import commons.Card;
import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

class CustomPairLongCard {
    public Long first;
    public Card second;

    CustomPairLongCard(Long first, Card second) {
        this.first = first;
        this.second = second;
    }
}

public class ServerUtils {

    private static String server = "http://localhost:8080/";
    // private static String server = "";

    // TODO remove if no longer useful
    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    // returns true if connection is succesful 
    // flase otherwise
    public boolean check(String addr) throws UnknownHostException, IOException {

        boolean res = false;
        
        URL url = new URL(addr+"/api/boards/TalioPresent");
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();

        if(urlConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            res = true;

        return res;
    }

    public void setServer(String addr) {
        server = addr;
    }

    public Board getBoardById(Long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/boards/"+id.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Board>() {});
    }

    public List<Board> getBoards() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/boards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Board>>() {});
    }

    public Board addCardToList(Long boardId, Long boardListId, Card card) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/boards/add/" + boardId.toString()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(new CustomPairLongCard(boardListId, card),
                    APPLICATION_JSON), Board.class);
    }

    // TODO remove if no longer useful
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    // TODO remove if no longer useful
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }
}