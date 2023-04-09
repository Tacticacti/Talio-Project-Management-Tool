package client.scenes;

import commons.BoardList;
import commons.Card;
import javafx.animation.ScaleTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Optional;

public class ShortcutsCtrl {
    private final CardCtrl cardCtrl;

    public ShortcutsCtrl(CardCtrl cardCtrl) {
        this.cardCtrl = cardCtrl;
    }

    void setShortcuts(ScaleTransition scaleTransition, KeyEvent event) {
        if (cardCtrl.target instanceof AnchorPane) {
            switch (event.getCode()) {

                case E -> editTaskTitleShortcut();
                case BACK_SPACE, DELETE -> deleteCardShortcut();
                case ENTER -> cardCtrl.setCardDetail(scaleTransition);
                case UP -> moveUpShortcut(scaleTransition, event);
                case DOWN -> moveDownShortcut(scaleTransition, event);
                case LEFT -> moveLeftShortcut(scaleTransition);
                case RIGHT -> moveRightShortcut(scaleTransition);
            }
            if (cardCtrl.target != null && cardCtrl.target instanceof AnchorPane) {
                ((AnchorPane) cardCtrl.target).requestFocus();
                scaleTransition.setNode((AnchorPane) cardCtrl.target);
                scaleTransition.play();
            }
        }
    }

    void moveRightShortcut(ScaleTransition scaleTransition) {
        if (cardCtrl.target == null) {
            return;
        }
        AnchorPane cardNode = (AnchorPane) cardCtrl.target;
        VBox parent = (VBox) cardNode.getParent();
        ObservableList<Node> boardLists = cardCtrl.singleBoardCtrl.getHbox_lists().getChildren();
        int listIndex = boardLists.indexOf(parent.getParent());
        if (listIndex < boardLists.size() - 2) {
            AnchorPane rightList = (AnchorPane) boardLists.get(listIndex + 1);
            setHighlightedCard(scaleTransition, cardNode, boardLists, listIndex, rightList);
        }
    }

    void moveLeftShortcut(ScaleTransition scaleTransition) {
        if (cardCtrl.target == null) {
            return;
        }
        AnchorPane cardNode = (AnchorPane) cardCtrl.target;
        VBox parent = (VBox) cardNode.getParent();
        ObservableList<Node> boardLists = cardCtrl.singleBoardCtrl.getHbox_lists().getChildren();
        int listIndex = boardLists.indexOf(parent.getParent());
        if (listIndex > 0) {
            AnchorPane leftList = (AnchorPane) boardLists.get(listIndex - 1);
            setHighlightedCard(scaleTransition, cardNode, boardLists, listIndex, leftList);
        }
    }

    void setHighlightedCard(ScaleTransition scaleTransition, AnchorPane cardNode,
                            ObservableList<Node> boardLists, int listIndex,
                            AnchorPane list) {
        AnchorPane currentList = (AnchorPane) boardLists.get(listIndex);
        int size = list.getChildren().size();
        VBox listVbox = (VBox) list.getChildren().get(size - 1);
        VBox currentVbox = (VBox) currentList.getChildren().get(size - 1);
        int cardIndex = currentVbox.getChildren().indexOf(cardNode);
        AnchorPane highlightCard = (AnchorPane) cardCtrl.target;
        if (listVbox.getChildren().size() - 2 >= cardIndex) {
            highlightCard = (AnchorPane) listVbox.getChildren().get(cardIndex);
        } else if (listVbox.getChildren().size() - 2 >= cardIndex - 1 && cardIndex > 0) {
            highlightCard = (AnchorPane) listVbox.getChildren().get(cardIndex - 1);
        } else if (listVbox.getChildren().size() > 1) {
            highlightCard = (AnchorPane) listVbox.getChildren().get(0);
        }
        scaleTransition.stop();
        cardNode.setScaleY(1);
        cardNode.setScaleX(1);
        cardCtrl.target = highlightCard;
    }

    void moveDownShortcut(ScaleTransition scaleTransition, KeyEvent event) {
        if (cardCtrl.target == null) {
            return;
        }
        AnchorPane cardNode = (AnchorPane) cardCtrl.target;
        VBox parent = (VBox) cardNode.getParent();
        ObservableList<Node> children = parent.getChildren();
        int index = children.indexOf(cardNode);
        if (event.isShiftDown()) {
            Map<Node, Card> nodeCardMap = cardCtrl.singleBoardCtrl.getNodeCardMap();
            Card card = nodeCardMap.get(cardNode);
            BoardList boardList = (BoardList) parent.getUserData();
            if (index < boardList.getCards().size() - 1) {
                scaleTransition.stop();
                cardNode.setScaleY(1);
                cardNode.setScaleX(1);
                Node temp = children.remove(index);
                cardCtrl.singleBoardCtrl.deleteCardFromList(boardList.getId(), card, false);
                children.add(index + 1, cardNode);
                cardCtrl.addCardAtIndex(boardList.getId(), index + 1, card);
                cardCtrl.target = temp;
            }
        } else if (index < children.size() - 2) {
            Node node = children.get(index + 1);
            scaleTransition.stop();
            cardNode.setScaleY(1);
            cardNode.setScaleX(1);
            node.requestFocus();
            cardCtrl.target = node;
        }
    }

    void moveUpShortcut(ScaleTransition scaleTransition, KeyEvent event) {
        if (cardCtrl.target == null) {
            return;
        }
        AnchorPane cardNode = (AnchorPane) cardCtrl.target;
        VBox parent = (VBox) cardNode.getParent();
        ObservableList<Node> children = parent.getChildren();
        int index = children.indexOf(cardNode);
        if (event.isShiftDown()) {
            Map<Node, Card> nodeCardMap = cardCtrl.singleBoardCtrl.getNodeCardMap();
            Card card = nodeCardMap.get(cardNode);
            BoardList boardList = (BoardList) parent.getUserData();
            if (index > 0) {
                scaleTransition.stop();
                cardNode.setScaleY(1);
                cardNode.setScaleX(1);
                Node temp = children.remove(index);
                cardCtrl.singleBoardCtrl.deleteCardFromList(boardList.getId(), card, false);

                children.add(index - 1, cardNode);
                cardCtrl.addCardAtIndex(boardList.getId(), index - 1, card);
                cardCtrl.target = temp;
            }
        } else {
            if (index > 0) {

                Node node = children.get(index - 1);
                scaleTransition.stop();
                cardNode.setScaleY(1);
                cardNode.setScaleX(1);
                node.requestFocus();
                cardCtrl.target = node;
            }
        }
    }

    void deleteCardShortcut() {
        if (cardCtrl.target == null) {
            return;
        }
        AnchorPane cardNode = (AnchorPane) cardCtrl.target;
        VBox parent = (VBox) cardNode.getParent();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Task");
        alert.setContentText("Are you sure you want to delete this task? (Irreversible)");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Map<Node, Card> nodeCardMap = cardCtrl.singleBoardCtrl.getNodeCardMap();
                Card deleteCard = nodeCardMap.remove(cardNode);
                BoardList boardList = (BoardList) parent.getUserData();
                cardCtrl.singleBoardCtrl.deleteCardFromList(boardList.getId(), deleteCard, true);

                cardCtrl.target = null;
            }
        });
    }

    void editTaskTitleShortcut() {
        if (cardCtrl.target == null) {
            return;
        }
        Node cardNode = (AnchorPane) cardCtrl.target;
        VBox parent = (VBox) cardNode.getParent();
        Optional<String> newTitle = cardCtrl.showTitleDialog();
        Map<Node, Card> nodeCardMap = cardCtrl.singleBoardCtrl.getNodeCardMap();
        Card card = nodeCardMap.get(cardNode);
        if (card != null && newTitle.isPresent()) {
            card.setTitle(newTitle.get());
            BoardList boardList = (BoardList) parent.getUserData();
            cardCtrl.singleBoardCtrl.updateCardFromList(boardList.getId(),
                    card);
            cardCtrl.target = cardNode;
        }
    }
}
