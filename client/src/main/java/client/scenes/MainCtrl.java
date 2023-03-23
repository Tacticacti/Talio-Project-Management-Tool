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
package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;


public class MainCtrl {

    private Stage primaryStage;

    private BoardOverviewCtrl boardOverviewCtrl;
    private Scene boverview;

    private AddCardCtrl addCardCtrl;
    private Scene addCard;

    private SingleBoardCtrl singleBoardCtrl ;
    private Scene singleBoard;

    private ConnectHomeCtrl connectHomeCtrl;
    private Scene home;

    public void initialize1(Stage primaryStage, Pair<ConnectHomeCtrl, Parent> homePair,
                            Pair<BoardOverviewCtrl, Parent> boverviewPair,
                            Pair<SingleBoardCtrl, Parent> singleBoardPair,
                           Pair<AddCardCtrl, Parent> addCardPair) {
        this.primaryStage = primaryStage;
        this.boardOverviewCtrl = boverviewPair.getKey();
        this.boverview = new Scene(boverviewPair.getValue());

        this.addCardCtrl = addCardPair.getKey();
        this.addCard = new Scene(addCardPair.getValue());

        this.connectHomeCtrl = homePair.getKey();
        this.home = new Scene(homePair.getValue());

        this.singleBoardCtrl = singleBoardPair.getKey();
        this.singleBoard = new Scene(singleBoardPair.getValue());

        showHome();
        primaryStage.show();
    }

    public void showAddCard() {
        primaryStage.setTitle("Add Card");
        primaryStage.setScene(addCard);
       // addCard.setOnKeyPressed(e -> addCardCtrl.keyPressed(e));
    }
    public void showBoard(){
        primaryStage.setTitle("Board");
        primaryStage.setScene(singleBoard);
    }

    public void showBoardOverview(){
        primaryStage.setTitle("Board overview");
        primaryStage.setScene(boverview);
    }
    public void showHome(){
        primaryStage.setTitle("Talio: Home connection page");
        primaryStage.setScene(home);
    }
}
