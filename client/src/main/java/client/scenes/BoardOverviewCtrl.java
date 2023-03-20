package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.io.IOException;

public class BoardOverviewCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    private final int MAX_BOARDS_IN_ROW = 5;

    @FXML
    private VBox board_rows;

    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void enterBoard(){
        mainCtrl.showBoard();
    }

    public void createBoard() throws IOException {

        var last_row = (HBox) board_rows.getChildren().get(board_rows.getChildren().size()-1);

        // add board to current hbox
        if (last_row.getChildren().size() < MAX_BOARDS_IN_ROW) {

            // create bord and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            AnchorPane board = loader.load();
            //HBox.setMargin(board, new Insets(20, 20, 20, 20));

            // enter clicked board
            Button btn =  (Button) board.lookup("#enterButton");
            btn.setOnAction(event -> mainCtrl.showBoard());

            // add created board
            last_row.getChildren().add(board);

        }
        // create a new row in vbox
        else {

            // create bord and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            Node board = loader.load();

            HBox hbox = new HBox();
            hbox.setSpacing(100);
            //hbox.setPadding(new Insets(102, 0, 0, 102));
            HBox.setMargin(board, new Insets(0, 0, 0, 187.5));
            hbox.getChildren().add(board);



            // enter clicked board
            Button btn =  (Button) board.lookup("#enterButton");
            btn.setOnAction(event -> mainCtrl.showBoard());


            // add created board
            board_rows.getChildren().add(hbox);
        }

        mainCtrl.showBoard();
    }


}
