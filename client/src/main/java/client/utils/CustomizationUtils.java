package client.utils;

import client.scenes.SingleBoardCtrl;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static client.scenes.MainCtrl.primaryStage;

public class CustomizationUtils {

    public static Color textColor = Color.BLACK;
    public static Color listColor = Color.WHITE;

    public static Map<Long, String> customizationData = new HashMap<>();



    public static void updateTextColor(Node node, Long boardID) {

        String color = getCustomizationField(boardID, 3);

        //System.out.println(boardID + color + node);


        if (node.getStyle().isEmpty()) {
            node.setStyle("-fx-text-fill:  "+ color + ";");
        } else if (!node.getStyle().contains("-fx-text-fill: " + color + ";")) {
            node.setStyle(node.getStyle() + "-fx-text-fill: "+ color + ";");
        }

        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(child -> {
                if (!"details".equals(child.getId())) {
                    updateTextColor(child, boardID);
                }
            });
        }

    }








    public static void updateBackgroundColour(Node node, Long BoardID) {
        var colour = getCustomizationField(BoardID, 0);

        System.out.println("TESTING BACKGROUND COLOUR");
        System.out.println(colour);
        System.out.println("TESTING BACKGROUND COLOUR");

        if (node.getStyle().isEmpty()) {
            node.setStyle("-fx-background-color: "+ colour +";");
        } else if (!node.getStyle().contains("-fx-background-color: "+ colour +";")) {
            node.setStyle(node.getStyle() + "-fx-background-color: "+ colour +";");
        }


        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable()
                    .forEach(child -> updateTextColor(child, BoardID));
        }

    }

    public static void updateForegroundColour(Node node, Long BoardID) {
        var colour = getCustomizationField(BoardID, 1);
        var rect = (Rectangle) node.lookup("#fg_rect");
        rect.setFill(Paint.valueOf(colour));




    }


    public static void updateCardColour(Node node, Long BoardID) {
        var colour = getCustomizationField(BoardID, 2);





        if (node.getStyle().isEmpty()) {
            node.setStyle("-fx-background-color: "+ colour +";");
        } else if (!node.getStyle().contains("-fx-background-color: "+ colour +";")) {
            node.setStyle(node.getStyle() + "-fx-background-color: "+ colour +";");
        }



        // every card
        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(child -> {
                if ("card".equals(child.getId())) {
                    updateCardColour(child, BoardID);
                    System.out.println("this is running - updating card colour!");
                }
            });
        }



        // save to file

    }



    public static void setCustomizationField(Long boardID, String updatedField, int index) {
        System.out.println(updatedField);
        var updatedBoardData = customizationData.get(boardID).split(",");
        System.out.println(Arrays.toString(updatedBoardData));
        updatedBoardData[index] = updatedField;
        customizationData.put(boardID, Arrays.toString(updatedBoardData)
                .replace("0x", "#")
                .replace("[", "")
                .replace("]", "")
                .replace(" ", ""));

    }


    public static void addDefaultCustomization(Long boardID) {
        // background, foreground, cardColour, text, accesibility, listcolor, board_color
        customizationData.put(boardID, "white,#403e3e,white,black,false,white,black");
        System.out.println(customizationData);

        // if there is already one in saved file
    }

    public static String getCustomizationField(Long board, int index) {
        if(!customizationData.containsKey(board)) {
            return "";
        }

        String boardData = customizationData.get(board);



        return (boardData.split(",")[index]).replace("0x", "#");
    }

    // takes in main anchor-pane of single board
    public static void updateAccessibilityMode(Node node) {

        var accessibility = Boolean.parseBoolean(CustomizationUtils
                .getCustomizationField(SingleBoardCtrl.getBoardID(), 4));

        var current_scene = primaryStage.getScene();
        if (accessibility) {

            current_scene.getRoot().getTransforms().add(new Scale(1.2, 1.2));
            var scrollPane = (ScrollPane) current_scene.getRoot().lookup("#main_pane");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);


        } else {

            current_scene.getRoot().getTransforms().clear();
            var scrollPane = (ScrollPane) current_scene.getRoot().lookup("#main_pane");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
    }


    public static void updateListColour(Long BoardID) {
        var colour = getCustomizationField(BoardID, 5);
        var current_scene = primaryStage.getScene();
        var h_boxlist = (HBox) current_scene.lookup("#hbox_lists");

        System.out.println("updating list colour!");


        if (h_boxlist != null) {
            for (Node node2 : h_boxlist.getChildren()) {
                if (node2.getId().equals("list_anchor")) {
                    var node_vbox = node2.lookup("#card_list");
                    //var node_vbox = node2;
                    node_vbox.setStyle("-fx-background-color: "+ colour +";"
                            + "-fx-background-radius: 10;");

                }
            }
        }

    }


}
