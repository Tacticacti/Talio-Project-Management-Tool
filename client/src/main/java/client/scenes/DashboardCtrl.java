package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class DashboardCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @FXML
    private ListView boards;

    private ObservableList<Board> boardsList;

    @Inject
    public DashboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void refresh() {
        server.getBoards();
    }

    public void back() {
        mainCtrl.showBoardOverview();
    }
}
