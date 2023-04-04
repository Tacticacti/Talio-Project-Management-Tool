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
package server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import server.database.BoardListRepository;

import server.database.BoardRepository;
import server.database.CardRepository;

@Configuration
@EnableWebSocket
public class Config  {

    private final CardRepository cardRepository;
    private final BoardListRepository boardListRepository;
    private final BoardRepository boardRepository;


    public Config(CardRepository cardRepository, BoardListRepository boardListRepository,
                  BoardRepository boardRepository) {
        this.cardRepository = cardRepository;
        this.boardListRepository = boardListRepository;
        this.boardRepository = boardRepository;

    }
}
