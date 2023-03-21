package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

public class CardGUICtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    public AnchorPane card;

    private Dragboard board;

    @Inject
    public CardGUICtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void details(){
        mainCtrl.showAddCard();
    }

    public void dragDetected(MouseEvent event) {
        board = card.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(card.getId());
        board.setContent(content);
        event.consume();
    }

    public void onDrag(DragEvent event) {
        System.out.println(card + " is being dragged!");

    }


}
