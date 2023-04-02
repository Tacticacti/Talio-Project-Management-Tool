package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class DashboardCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @Inject
    public DashboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void back() {
        mainCtrl.showBoardOverview();
    }
}
