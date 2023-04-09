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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


import java.io.IOException;

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

    private AdminLoginCtrl adminLoginCtrl;
    private Scene adminLogin;

    private DashboardCtrl dashboardCtrl;
    private Scene dashboard;

    private final Injector INJECTOR = createInjector(new MyModule());
    private final MyFXML FXML = new MyFXML(INJECTOR);



    public void initialize1(Stage primaryStage, Pair<ConnectHomeCtrl, Parent> homePair,
                            Pair<BoardOverviewCtrl, Parent> boardOverviewCtrlParentPair,
                            Pair<AdminLoginCtrl, Parent> adminLoginPair,
                            Pair<DashboardCtrl, Parent> dashboardPair) {
        try {
            this.primaryStage = primaryStage;


            this.boardOverviewCtrl = boardOverviewCtrlParentPair.getKey();
            this.boverview = new Scene(boardOverviewCtrlParentPair.getValue());

            this.connectHomeCtrl = homePair.getKey();
            this.home = new Scene(homePair.getValue());
            home.setOnKeyPressed(this::showHelpPage);

            this.adminLoginCtrl = adminLoginPair.getKey();
            this.adminLogin = new Scene(adminLoginPair.getValue());

            this.dashboardCtrl = dashboardPair.getKey();
            this.dashboard = new Scene(dashboardPair.getValue());

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

    public void showBoardOverview(){
        try{
            System.out.println("show overview: " +boverview);
            System.out.println("primaryStage" + primaryStage);
            primaryStage.setTitle("Board overview");
            primaryStage.setScene(boverview);
            boardOverviewCtrl.refresh();
        }catch (Exception e) {
            showErrorDialog("Error", "An error occurred while trying to display the boards." +
                    " Please try again later.\n\n" + e.getMessage());
            e.printStackTrace();
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

    public void showAdmin() {
        primaryStage.setTitle("Admin login");
        primaryStage.setScene(adminLogin);
    }

    public void showDashboard() {
        primaryStage.setTitle("Talio: Admin Dashboard");
        primaryStage.setScene(dashboard);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showHelpPage(KeyEvent event) {
        if (event.getCode() == KeyCode.SLASH && event.isShiftDown()) {
            FXMLLoader helpPageLoader = new FXMLLoader(
                    getClass().getResource("helpPage.fxml"));
            Parent helpPageParent;
            try {
                helpPageParent = helpPageLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scene helpPage = new Scene(helpPageParent);
            Stage helpPageStage = new Stage();
            helpPageStage.setTitle("Shortcut Help Page");
            helpPageStage.setResizable(false);
            helpPageStage.setScene(helpPage);
            helpPageStage.initModality(Modality.APPLICATION_MODAL);
            helpPageStage.showAndWait();
        }
    }

}
