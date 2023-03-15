package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

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
