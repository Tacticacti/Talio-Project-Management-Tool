package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;


import java.io.IOException;

public class SingleBoardCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private HBox board_lists;

    @FXML
    private ScrollPane main_pane;

    @Inject
    public SingleBoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void back(){
        mainCtrl.showBoardOverview();
    }

    public void card(){
        mainCtrl.showAddCard();
    }


    public void createNewList(ActionEvent e) throws IOException {
        var lists = board_lists.getChildren();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("listGUI.fxml"));
        Node list = loader.load();
        lists.add(lists.size()-2, list);



    }


}
