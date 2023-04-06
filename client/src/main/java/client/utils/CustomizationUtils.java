package client.utils;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CustomizationUtils {

    public static Color textColor = Color.BLACK;
    public static Color listColor = Color.WHITE;

    public static Map<Long, String> customizationData = new HashMap<Long,String>();



    public static void updateTextColor(Node node, Long boardID) {

        String color = getBoardTextColor(boardID);

        //System.out.println(boardID + color + node);


        if (node.getStyle().isEmpty()) {
            node.setStyle("-fx-text-fill:  "+ color + ";");
        } else if (!node.getStyle().contains("-fx-text-fill: " + color + ";")) {
            node.setStyle(node.getStyle() + "-fx-text-fill: "+ color + ";");
        }


        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(child -> updateTextColor(child, boardID));
        }

        customizationData.put(10L, "f");
    }

    public static void setTextColor(Long boardID, String updatedColor) {
        System.out.println(updatedColor);
        var updatedBoardData = customizationData.get(boardID).split(",");
        System.out.println(updatedBoardData);
        updatedBoardData[3] = updatedColor;
        customizationData.put(boardID, Arrays.toString(updatedBoardData)
                .replace("0x", "#")
                .replace("[", "")
                .replace("]", "")
                .replace(" ", ""));

        System.out.println(customizationData);
        System.out.println("hello");

    }

    public static void addDefaultCustomization(Long boardID) {
        customizationData.put(boardID, "white,grey,white,black,off,white,white");
        System.out.println(customizationData);

        // if there is already one in saved file
    }

    public static String getBoardTextColor(Long board) {
        String boardData = customizationData.get(board);


        return (boardData.split(",")[3]).toString().replace("0x", "#");
    }



    // make methods to load customization data

}
