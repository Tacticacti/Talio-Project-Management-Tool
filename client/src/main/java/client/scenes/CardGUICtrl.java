package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.scene.layout.AnchorPane;

public class CardGUICtrl {
    private final MainCtrl mainCtrl;
    public AnchorPane card;

    @Inject
    public CardGUICtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void details(){
        mainCtrl.showAddCard();
    }
}
