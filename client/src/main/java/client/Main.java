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
package client;

import static client.utils.LocalUtils.readCustomization;
import static com.google.inject.Guice.createInjector;

import client.scenes.AdminLoginCtrl;
import client.scenes.DashboardCtrl;
import client.scenes.MainCtrl;
import client.scenes.BoardOverviewCtrl;
import client.scenes.ConnectHomeCtrl;
import client.utils.LocalUtils;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        var boverview = FXML.load(BoardOverviewCtrl.class, "client", "scenes",
			"BoardOverview.fxml");
//      var addcard = FXML.load(AddCardCtrl.class, "client", "scenes", "AddCard.fxml");
//      var singleBoard = FXML.load(SingleBoardCtrl.class, "client", "scenes", "SingleBoard.fxml");
        var home = FXML.load(ConnectHomeCtrl.class
                , "client", "scenes", "ConnectHomePage.fxml");
        var admin = FXML.load(AdminLoginCtrl.class, "client", "scenes", "AdminLogin.fxml");
        var dashboard = FXML.load(DashboardCtrl.class, "client", "scenes", "Dashboard.fxml");
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        readCustomization();

        mainCtrl.initialize1(primaryStage, home, boverview, admin, dashboard);
    }
}
