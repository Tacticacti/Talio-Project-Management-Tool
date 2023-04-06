package client.scenes;

import client.utils.CustomizationUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


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

    @FXML ColorPicker text_colour;


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

        // save to file
    }

    public void setForegroundColour() {
        System.out.println("Set the foreground colour");

        Scene current_scene =  primaryStage.getScene();


        String cssColor = b_fg.getValue().toString().replace("0x", "#");
        var rect = (Rectangle) current_scene.lookup("#fg_rect");
        rect.setFill(Paint.valueOf(cssColor));

        // save to file
    }

    public void setDefaultCardColour() {
        System.out.println("Set the default card colour");

        // save tof file
    }

    public void setTextColour() {
        System.out.println("Set the text colour");

        Scene current_scene =  primaryStage.getScene();
        AnchorPane anchor = (AnchorPane) current_scene.getRoot();

        CustomizationUtils.setTextColor(SingleBoardCtrl.BoardID, String.valueOf(text_colour.getValue()));

        CustomizationUtils.updateTextColor(anchor, SingleBoardCtrl.getBoardID());

        //anchor.setStyle("-fx-text-fill: red;");
        //anchor.getChildren().forEach(child -> child.setStyle("-fx-text-fill: red;"));

        // change default text color when added

        // save to file
    }

    public void setAccessibilityMode() {
        System.out.println("Set the accessibility mode");

        // save to file
    }

    public void setCurrentListCustomizing() {
        System.out.println("Set the current list customization");

        // save to file
    }

    public void setListBackgroundColour() {
        System.out.println("Set the list background colour");

        // save to file
    }

    public void setListCardColour() {
        System.out.println("Set the list card colour");

        // save to file
    }

}