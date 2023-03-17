package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class BoardOverviewCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void enterBoard(){
        mainCtrl.showBoard();
    }

}
