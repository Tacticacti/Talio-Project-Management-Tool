package client.scenes;

import commons.Board;
import commons.BoardList;
import commons.Card;
import client.utils.ServerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

public class SingleBoardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<BoardList> lists;

    @FXML
    private AnchorPane mainAnchor;

    @Inject
    public SingleBoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void pullLists(Long id) {
        Board tmpBoard = server.getBoardById(id);
        lists = FXCollections.observableList(tmpBoard.getLists());
    }

    public AnchorPane wrapBoardList(BoardList boardList) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("listGUI.fxml"));
        AnchorPane node;
        try {
            node = loader.load();
        }
        catch(Exception e) {
            e.printStackTrace();
            return new AnchorPane();
        }

        ((TextField) node.getChildren().get(0)).setText(boardList.getName());

        VBox target = (VBox) node.getChildren().get(3);

        for(Card c : boardList.getCards()) {
            System.out.println("processing card: " + c.toString());
            // temporary solution
            target.getChildren().addAll(new Label(c.getTitle()));
        }

        return node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        server.addCardToList(1L, 0L, new Card("card from init"));

        // TODO change 1L -> board_id if we are going multiboard
        pullLists(1L); 

        for(BoardList bl : lists) {
            System.out.println("processing: " + bl.getName());
            var node = wrapBoardList(bl);
            mainAnchor.getChildren().addAll(node);
            System.out.println(node.getChildren());
        }
    }

    public void back(){
        mainCtrl.showBoardOverview();
    }

    public void card(){
        mainCtrl.showAddCard();
    }
}
