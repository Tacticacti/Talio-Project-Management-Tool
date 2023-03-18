package client.scenes;

// import commons.Board;
import commons.BoardList;
import client.utils.ServerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
        var tmp = server.getListsFrom(id);
        lists = FXCollections.observableList(tmp);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pullLists(1L);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(new Label("test"));
        mainAnchor.getChildren().addAll(vbox);
        // test.setItems(data);
    }

    public void back(){
        mainCtrl.showBoardOverview();
    }

    public void card(){
        mainCtrl.showAddCard();
    }
}
