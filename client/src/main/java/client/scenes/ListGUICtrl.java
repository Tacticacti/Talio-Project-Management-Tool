package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class ListGUICtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public ListGUICtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }
}
