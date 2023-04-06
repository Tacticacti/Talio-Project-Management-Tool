package client.scenes;

import client.utils.CustomizationUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import org.glassfish.jersey.internal.inject.Custom;


import java.net.URL;
import java.util.ResourceBundle;

import static client.scenes.MainCtrl.primaryStage;

public class CustomizationPageCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private ColorPicker b_bg;

    @FXML
    private ColorPicker b_fg;

    @FXML
    private ColorPicker text_colour;

    @FXML
    private ColorPicker card_colour;

    @FXML
    private CheckBox accessibility_mode;

    @FXML
    private ColorPicker list_colour;



    @Inject
    public CustomizationPageCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;

    }
    public CustomizationPageCtrl(){
        this.server =new ServerUtils();
        this.mainCtrl = new MainCtrl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setBackgroundColour() {
        System.out.println("Set the background colour" +  primaryStage.getScene());
        Scene current_scene =  primaryStage.getScene();


        String cssColor = b_bg.getValue().toString().replace("0x", "#");
        current_scene.lookup("#hbox_lists").setStyle("-fx-background-color: "+ cssColor +";");

        CustomizationUtils.setCustomizationField(SingleBoardCtrl.getBoardID(), cssColor, 0);

        // save to file
    }

    public void setForegroundColour() {
        System.out.println("Set the foreground colour");

        Scene current_scene =  primaryStage.getScene();


        String cssColor = b_fg.getValue().toString().replace("0x", "#");
        var rect = (Rectangle) current_scene.lookup("#fg_rect");
        rect.setFill(Paint.valueOf(cssColor));

        CustomizationUtils.setCustomizationField(SingleBoardCtrl.getBoardID(), cssColor, 1);


        // save to file
    }

    public void setDefaultCardColour() {
        System.out.println("Set the default card colour");

        Scene current_scene =  primaryStage.getScene();
        String cssColor = card_colour.getValue().toString().replace("0x", "#");

        CustomizationUtils.setCustomizationField(SingleBoardCtrl.BoardID, cssColor, 2);
        CustomizationUtils.updateCardColour(current_scene.getRoot(), SingleBoardCtrl.getBoardID());

        System.out.println(cssColor);

        for (Node card : SingleBoardCtrl.nodeCardMap.keySet()) {
            card.setStyle(card.getStyle() + "-fx-background-color: "+ cssColor +";");

            // this removes the radii of the card until refresh!
            card.setStyle(card.getStyle() + "-fx-border-radius: 10;");

        }




        // save tof file
    }

    public void setTextColour() {
        System.out.println("Set the text colour");

        Scene current_scene =  primaryStage.getScene();
        AnchorPane anchor = (AnchorPane) current_scene.getRoot();

        CustomizationUtils.setCustomizationField(SingleBoardCtrl.BoardID, String.valueOf(text_colour.getValue()), 3);

        CustomizationUtils.updateTextColor(anchor, SingleBoardCtrl.getBoardID());

        //anchor.setStyle("-fx-text-fill: red;");
        //anchor.getChildren().forEach(child -> child.setStyle("-fx-text-fill: red;"));

        // change default text color when added

        // save to file
    }


    public void setAccessibilityMode() {
        System.out.println("Set the accessibility mode");

        Scene current_scene =  primaryStage.getScene();

        if (accessibility_mode.isSelected()) {
            current_scene.getRoot().getTransforms().add(new Scale(1.2, 1.2));

            var scrollPane = (ScrollPane) current_scene.getRoot().lookup("#main_pane");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

            // do something else for accessibility?
            // bold text! - comic sans? lmao


            CustomizationUtils.setCustomizationField(SingleBoardCtrl.getBoardID(), "true", 4);


        } else {
            current_scene.getRoot().getTransforms().clear();


            var scrollPane = (ScrollPane) current_scene.getRoot().lookup("#main_pane");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            CustomizationUtils.setCustomizationField(SingleBoardCtrl.getBoardID(), "false", 4);
        }





        // save to file
    }



    public void setListBackgroundColour() {
        System.out.println("Set the list background colour");

        Scene current_scene =  primaryStage.getScene();
        String cssColor = list_colour.getValue().toString().replace("0x", "#");

        CustomizationUtils.setCustomizationField(SingleBoardCtrl.getBoardID(), cssColor, 5);

        CustomizationUtils.updateListColour(SingleBoardCtrl.getBoardID());

        // save to file
    }



}