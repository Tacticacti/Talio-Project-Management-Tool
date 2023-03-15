package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class ConnectHomeCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public ConnectHomeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void connect(){
        mainCtrl.showBoardOverview();
    }
}
