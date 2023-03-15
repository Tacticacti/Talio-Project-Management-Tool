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

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private BoardOverviewCtrl boardOverviewCtrl;
    private Scene boverview;

    private AddCardCtrl addCardCtrl;
    private Scene addCard;

    private SingleBoardCtrl singleBoardCtrl ;
    private Scene singleBoard;

    private ConnectHomeCtrl connectHomeCtrl;
    private Scene home;

    private AddQuoteCtrl addCtrl;
    private Scene add;




    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
            Pair<AddQuoteCtrl, Parent> add) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        showOverview();
        primaryStage.show();
    }

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

    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    public void showAddCard() {
        primaryStage.setTitle("Add Card");
        primaryStage.setScene(addCard);
        addCard.setOnKeyPressed(e -> addCardCtrl.keyPressed(e));
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