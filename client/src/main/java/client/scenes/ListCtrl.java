package client.scenes;

import client.utils.CustomizationUtils;
import commons.Board;
import commons.BoardList;
import commons.Card;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import java.io.IOException;
import java.util.Objects;



public class ListCtrl {
    private final SingleBoardCtrl singleBoardCtrl;

    public ListCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public void createNewList() {
        if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
            return;
        }
        singleBoardCtrl.refresh();
    }

    public void wrapList(BoardList boardList, ObservableList<Node> board_lists) throws IOException {
        FXMLLoader loader = new FXMLLoader(singleBoardCtrl.getClass().getResource("listGUI.fxml"));
        AnchorPane list = loader.load();
        list.setUserData(boardList);

        CustomizationUtils.updateTextColor(list, SingleBoardCtrl.BoardID);



        // set up deleting a board list
        setDeleteBoardList(boardList, board_lists, list);
        for (Node anchorPane : singleBoardCtrl.hbox_lists.getChildren()) {
            anchorPane.setOnDragEntered(event -> {
                event.acceptTransferModes(TransferMode.MOVE);
                System.out.println("I'm bigParent alpha!");
            });
        }
        // set up putting list title
        setListTitle(boardList, list);
        list.setOnDragOver(event -> {
            if (SingleBoardCtrl.dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                System.out.println("draggin vbox");
            }
            event.consume();
        });
        var list_vbox = (VBox) list.getChildren().get(list.getChildren().size() - 1);
        Long listId = ((BoardList) list.getUserData()).getId();
        list_vbox.setOnDragDropped(event -> {
            if (SingleBoardCtrl.dragboard.hasString()) {
                String[] splitDragboard = SingleBoardCtrl.dragboard.getString().split(";");
                long originalListId = Long.parseLong(splitDragboard[1].trim());
                long originalListIndex = getListIndex(SingleBoardCtrl.BoardID, originalListId);
                ObservableList<Node> hboxChildren = singleBoardCtrl.hbox_lists.getChildren();
                AnchorPane originalList = (AnchorPane) (hboxChildren.get((int) originalListIndex));
                int originalListSize = originalList.getChildren().size();
                VBox originalParent = (VBox) originalList.getChildren().get(originalListSize - 1);
                Node draggedCardNode = originalParent.lookup("#" + splitDragboard[0].trim());
                if (draggedCardNode != null && originalParent != list_vbox) {
                    list_vbox.getChildren().add(0, draggedCardNode);
                    Card draggedCard = SingleBoardCtrl.nodeCardMap.get(draggedCardNode);
                    singleBoardCtrl.deleteCardFromList(originalListId, draggedCard);
                    singleBoardCtrl.saveCardToList(listId, draggedCard);
                }
            }
            //event.consume();
        });
        // set up adding new card
        Button newCardButton = (Button) list.lookup("#addNewCardButton");
        VBox parentList = (VBox) newCardButton.getParent();
        parentList.setUserData(boardList);
        parentList.setSpacing(20);
        for (Card card : boardList.getCards()) {
            singleBoardCtrl.placeCard(parentList, card);
        }
        newCardButton.setOnAction(event -> {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            singleBoardCtrl.addNewCard(parentList);
        });

        // update customization for list from CustomizationUtils (read from file)



        // board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();
        board_lists.add(list);
    }

    public void requestNameChange(TextField title, Node list) throws Exception {
        if (!title.getText().isEmpty()) {
            System.out.println(title.getText().trim());
            BoardList changedBoardList = (BoardList) list.getUserData();
            System.out.println("requesting change name: " + SingleBoardCtrl.BoardID + " " +
                    changedBoardList.getId() + " " + title.getText().trim());
            singleBoardCtrl.server.changeListName(changedBoardList.getId(), title.getText().trim());
        } else {
            throw new Exception("List name cannot be empty.");
        }
    }

    void setListTitle(BoardList boardList, Node list) {
        TextField title = (TextField) list.lookup("#list_title");
        title.setText(boardList.getName());
        title.setOnAction(event -> {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            try {
                requestNameChange(title, list);
                singleBoardCtrl.refresh();
            } catch (Exception e) {
                singleBoardCtrl.refresh();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Error changing list's name!\n\n" + e.getMessage());
                alert.showAndWait();
            }
        });
        title.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                    return;
                }
                try {
                    requestNameChange(title, list);
                    singleBoardCtrl.refresh();
                } catch (Exception e) {
                    singleBoardCtrl.refresh();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText("Error changing list's name!\n\n" + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    void setDeleteBoardList(BoardList boardList, ObservableList<Node> board_lists,
                            Node list) {
        Button deleteBoardList = (Button) list.lookup("#deleteBtn");
        deleteBoardList.setOnAction(event -> {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Delete List");
            alert.setContentText("Are you sure you want to delete this list? (Irreversible)");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // deleting on client(GUI) side
                    board_lists.remove(deleteBoardList.getParent());
                    // deleting list on server side
                    singleBoardCtrl.server.removeBoardList(SingleBoardCtrl.BoardID,
                            boardList.getId());
                    singleBoardCtrl.current_board.removeList(boardList);
                    try {
                        singleBoardCtrl.refresh();
                    } catch (Exception e) {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.initModality(Modality.APPLICATION_MODAL);
                        error.setContentText("Error removing list!");
                        error.showAndWait();
                    }
                }
            });
        });
    }

    public long getListIndex(Long boardId, Long listId) {
        Board b = singleBoardCtrl.server.getBoardById(boardId);
        for (int i = 0; i < b.getLists().size(); ++i) {
            if (Objects.equals(b.getLists().get(i).getId(), listId)) {
                return i;
            }
        }
        return -1;
    }
}
