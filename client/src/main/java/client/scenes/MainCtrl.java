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


public class MainCtrl {

    public static Stage primaryStage;

    public static Scene boardOverview;

    private Scene home;

    private Scene adminLogin;

    private Scene dashboard;


    public void initialize1(Stage primaryStage, Pair<ConnectHomeCtrl, Parent> homePair,
                            Pair<BoardOverviewCtrl, Parent> boardOverviewCtrlParentPair,
                            Pair<AdminLoginCtrl, Parent> adminLoginPair,
                            Pair<DashboardCtrl, Parent> dashboardPair) {
        try {
            MainCtrl.primaryStage = primaryStage;


            boardOverview = new Scene(boardOverviewCtrlParentPair.getValue());

            this.home = new Scene(homePair.getValue());
            home.setOnKeyPressed(this::showHelpPage);

            this.adminLogin = new Scene(adminLoginPair.getValue());

            this.dashboard = new Scene(dashboardPair.getValue());

            showHome();
            primaryStage.show();
        }catch (Exception e) {
            showErrorDialog("An error occurred while" +
                    " trying to initialize the application." +
                    " Please try again later.");
        }
    }

    public void showHome(){
        try {
            primaryStage.setTitle("Talio: Home connection page");
            primaryStage.setScene(home);
        }catch (Exception e) {
            showErrorDialog("An error occurred while trying to display the home page." +
                     " Please try again later.");
        }
    }

    public void showAdmin() {
        primaryStage.setTitle("Admin login");
        primaryStage.setScene(adminLogin);
    }

    public void showDashboard() {
        primaryStage.setTitle("Talio: Admin Dashboard");
        primaryStage.setScene(dashboard);
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showHelpPage(KeyEvent event) {
        if (event.isShiftDown() && (
                event.getCode() == KeyCode.H || event.getCode() == KeyCode.SLASH)) {
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
