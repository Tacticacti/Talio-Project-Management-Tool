package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class SingleBoardCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

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


}
