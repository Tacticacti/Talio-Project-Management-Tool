package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class CardGUICtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    public AnchorPane card;
    ClipboardContent content;

    private Dragboard board;

    @Inject
    public CardGUICtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void details(){
        mainCtrl.showAddCard();
    }
}
