package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class CardGUICtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public CardGUICtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void details(){
        mainCtrl.showAddCard();
    }
}
