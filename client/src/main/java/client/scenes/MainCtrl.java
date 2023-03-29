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

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;


import static com.google.inject.Guice.createInjector;


public class MainCtrl {

    public static Stage primaryStage;

    private BoardOverviewCtrl boardOverviewCtrl;
    public static Scene boverview;

    private AddCardCtrl addCardCtrl;
    private Scene addCard;

    private SingleBoardCtrl singleBoardCtrl ;
    private Scene singleBoard;

    private ConnectHomeCtrl connectHomeCtrl;
    private Scene home;

    private final Injector INJECTOR = createInjector(new MyModule());
    private final MyFXML FXML = new MyFXML(INJECTOR);



    public void initialize1(Stage primaryStage, Pair<ConnectHomeCtrl, Parent> homePair,
                            Pair<BoardOverviewCtrl, Parent> boverviewPair,
                            Pair<SingleBoardCtrl, Parent> singleBoardPair,
                           Pair<AddCardCtrl, Parent> addCardPair) {
        try {
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
        }catch (Exception e) {
            showErrorDialog("Error", "An error occurred while" +
                    " trying to initialize the application." +
                    " Please try again later.");
        }
    }

    public void showAddCard() {
        try {
            primaryStage.setTitle("Add Card");
            primaryStage.setScene(addCard);
            // addCard.setOnKeyPressed(e -> addCardCtrl.keyPressed(e));
        } catch (Exception e) {
            showErrorDialog("Error", "An error occurred while trying to add a card." +
                    " Please try again later.");
        }
    }
    public void showBoard(){
        try {
            primaryStage.setTitle("Board");
            primaryStage.setScene(singleBoard);
        }catch (Exception e) {
            showErrorDialog("Error", "An error occurred while" +
                    " trying to display the board." +
                    " Please try again later.");
        }
    }

    //public void showBoard(Board board) throws IOException {

    //    primaryStage.setTitle("Board");

    //    FXMLLoader loader = new FXMLLoader(getClass().getResource("SingleBoard.fxml"));

    //    Node root = loader.load();


        //Scene new_scene = new Scene(root);



        //TextField board_name = (TextField) new_scene.lookup("#board_name");

        //board_name.setText(board.getId().toString());

        //primaryStage.setScene(new Scene(loaded_board.getValue()));
        // build board from board

    //    primaryStage.setScene(root.getScene());
    //    System.out.println("after entering board, " + primaryStage);
    //}


    public void showBoardOverview(){
        try{
            System.out.println("show overview: " +boverview);
            System.out.println("primaryStage" + primaryStage);
            primaryStage.setTitle("Board overview");
            primaryStage.setScene(boverview);
            boardOverviewCtrl.refresh();
        }catch (Exception e) {
            showErrorDialog("Error", "An error occurred while trying to display the boards." +
                    " Please try again later.");
        }
    }
    public void showHome(){
        try {
            primaryStage.setTitle("Talio: Home connection page");
            primaryStage.setScene(home);
        }catch (Exception e) {
            showErrorDialog("Error", "An error occurred while trying to display the home page." +
                     " Please try again later.");
        }
    }

    public String getPrimaryStage() {
        return primaryStage.toString();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
