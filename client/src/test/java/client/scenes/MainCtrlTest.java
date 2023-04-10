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

import client.Main;
import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;

import org.junit.jupiter.api.Test;

import org.testfx.framework.junit.ApplicationTest;




import static com.google.inject.Guice.createInjector;


public class MainCtrlTest extends ApplicationTest {

    private MainCtrl sut = new MainCtrl();

    private static Main app;

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

//    @Override
//    public void start(Stage stage){
////        try {
////            stage = FxToolkit.registerPrimaryStage();
////            app = (Main) FxToolkit.setupApplication(Main.class);
////
////        } catch (TimeoutException e) {
////            throw new RuntimeException(e);
////        }
//        AnchorPane ap = new AnchorPane();
//        ap.setOnKeyPressed(event -> {
//            Platform.runLater(()->{
//                sut.showHelpPage(event);
//            });
//        });
//        Scene scene = new Scene(ap);
//        stage.setScene(scene);
//        stage.show();
//    }
//


//    @AfterAll
//    public static void cleanup() throws TimeoutException {
//        // clean up the JavaFX toolkit
//        FxToolkit.cleanupApplication(app);
//        Platform.exit();
//    }


    @Test
    public void writeSomeTests() {

// TODO create replacement objects and write some tests
        // sut.initialize(null, null, null);
    }
//
//    @Test
//    public void showHelpPage(){
//
//        FxRobot robot = new FxRobot();
//        Popup popup = (Popup) robot.listWindows()
//        .stream().filter(window -> window instanceof Popup).findFirst().get();
//        assertNotNull(popup);
//        assertTrue(popup.isShowing());
//    }


}

