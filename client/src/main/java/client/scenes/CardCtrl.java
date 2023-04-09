package client.scenes;

import client.utils.CustomizationUtils;
import commons.BoardList;
import commons.Card;

import jakarta.ws.rs.WebApplicationException;
import javafx.animation.ScaleTransition;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("checkstyle:Indentation")
public class CardCtrl {
    final SingleBoardCtrl singleBoardCtrl;
    private final ShortcutsCtrl shortcutsCtrl = new ShortcutsCtrl(this);
    EventTarget target;

    public CardCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public void placeCard(VBox parent, Card card) {
        String cardTitle = card.getTitle();
        FXMLLoader fxmlLoader = new FXMLLoader(
                singleBoardCtrl.getClass().getResource("cardGUI.fxml"));
        try {
            Node cardNode = fxmlLoader.load();
            cardNode.setId(UUID.randomUUID().toString());
            Border border = new Border(new BorderStroke(Paint.valueOf("black")
                    , BorderStrokeStyle.DASHED
                    , new CornerRadii(10), BorderWidths.DEFAULT));
            ((AnchorPane) cardNode).setBorder(border);
            if (card.getDescription() == null || card.getDescription().equals("")) {
                cardNode.lookup("#descIndicator").setVisible(false);
            }
            if (card.getSubtasks().size() > 0) {
                ((Label) cardNode.lookup("#progress"))
                        .setText(card.getCompletedSubs() + "/"
                                + card.getSubtasks().size());
            }
            HBox tagDisplay = (HBox) cardNode.lookup("#tagDisplay");
            tagDisplay.setSpacing(5);
            if(card.getTags().size()>0){
                for(String t: card.getTags().keySet()){
                    String color = card.getTags().get(t);
                    Line line = new Line();
                    line.setStartX(0);
                    if(card.getTags().size()>4){
                        line.setEndX(tagDisplay.getPrefWidth()/(2*card.getTags().size()));
                    }else{
                        line.setEndX(tagDisplay.getPrefWidth()/5);
                    }
                    line.setStroke(Color.valueOf(color));
                    line.setStrokeWidth(tagDisplay.getPrefHeight()/2);
                    line.setStrokeLineCap(StrokeLineCap.ROUND);
                    tagDisplay.getChildren().add(line);
                }
            }
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), cardNode);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            cardNode.setOnMouseEntered(event -> {
//                System.out.println("Mouse Entered: " + target);
                target = cardNode;
                cardNode.requestFocus();
                scaleTransition.setNode(cardNode);
                scaleTransition.play();
            });
            cardNode.setOnMouseExited(event -> {
//                System.out.println("Mouse Exited: " + target);
                target = null;
                scaleTransition.stop();
                cardNode.setScaleX(1);
                cardNode.setScaleY(1);
            });
            cardNode.setOnKeyPressed((KeyEvent event) -> {
//                System.out.println("On Key Pressed: " + target);
                scaleTransition.setNode(cardNode);
                scaleTransition.play();
                shortcutsCtrl.setShortcuts(scaleTransition, event);
            });
            cardNode.setOnMouseClicked(event -> {
                cardNode.requestFocus();

                if(event.getClickCount()==2){
                    setCardDetail(scaleTransition);
                }
            });
            Button detail = (Button) cardNode.lookup("#details");
            detail.setOnMouseEntered(event -> {
                DropShadow shadow = new DropShadow();
                shadow.setRadius(30.0);
                shadow.setBlurType(BlurType.GAUSSIAN);
                shadow.setColor(Color.BLACK);
                detail.setEffect(shadow);
                detail.setStyle(
                        "-fx-cursor:hand;" +
                                " -fx-background-color: transparent");
            });
            detail.setOnMouseExited(event -> {
                detail.setEffect(null);
                detail.setStyle("-fx-cursor:pointer; " +
                        "-fx-background-color: transparent");
            });
            Label title = (Label) cardNode.lookup("#taskTitle");
            detail.setOnAction(event -> setCardDetail(scaleTransition));
            title.setText(cardTitle);
            SingleBoardCtrl.nodeCardMap.put(cardNode, card);
            setDragAndDrop(parent, cardNode);
            int index = parent.getChildren().size() - 1;
            if (parent.getChildren().size() == 1) {
                index = 0;
            }

            System.out.println("updating!");
            System.out.println(CustomizationUtils.customizationData);


            CustomizationUtils.updateTextColor(cardNode, singleBoardCtrl.BoardID);
            CustomizationUtils.updateCardColour(cardNode, singleBoardCtrl.BoardID);




            parent.getChildren().add(index, cardNode);
            //cardNode.getScene();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setCardDetail(ScaleTransition scaleTransition) {
        if (target == null) {
            return;
        }
        AnchorPane cardNode = (AnchorPane) target;
        VBox parent = (VBox) cardNode.getParent();
        if (!singleBoardCtrl.isUnlocked) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Read-only Mode");
            alert.setHeaderText(null);
            alert.setContentText("You cannot edit the content of the card in Read-only Mode.");
            alert.showAndWait();
        }
        BoardList boardList = (BoardList) parent.getUserData();
        long listId = boardList.getId();
        Card card = singleBoardCtrl.server.getCardById(
                SingleBoardCtrl.nodeCardMap.get(cardNode).getId());
        FXMLLoader fxmlLoader = new FXMLLoader(
                singleBoardCtrl.getClass().getResource("AddCard.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // sets up done card button
        Button doneButton = (Button) root.lookup("#doneTaskButton");
        doneButton.setOnAction(event -> {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            setDone(listId, card, event);
        });
        //sets up delete card button
        Button deleteButton = (Button) root.lookup("#deleteTaskButton");
        deleteButton.setOnAction(event -> {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            setDelete(event, cardNode, card, listId);
        });
        //sets up cancel card button
        Button cancelButton = (Button) root.lookup("#cancelTaskButton");
        cancelButton.setOnAction(this::setCancel);
        //sets up card title
        TextField title = (TextField) root.lookup("#taskTitle");
        title.setText(card.getTitle());
        //sets up card description
        TextArea description = (TextArea) root.lookup("#taskDescription");
        description.setText(card.getDescription());
        // sets up sub-task operations
        Button addSubTask = (Button) root.lookup("#addSubtaskButton");
        AddCardCtrl addCardCtrl = fxmlLoader.getController();
        addCardCtrl.setCard(card);
        addCardCtrl.setButton(doneButton);
        addSubTask.setOnAction(event -> addCardCtrl.addSubTask(card));
        if (card.getSubtasks() != null) {
            for (String str : card.getSubtasks()) {
                if (card.getCompletedTasks().contains(str)) {
                    addCardCtrl.displayCompletedSubs(str, card);
                } else {
                    addCardCtrl.displaySubs(str, card);
                }
            }
        }
        Button custom = (Button) root.lookup("#CustomTag");

        Button board = (Button) root.lookup("#BoardTag");

        VBox tags = (VBox) root.lookup("#tagVbox");
        board.setOnAction(event -> singleBoardCtrl.openBoardTags(tags, card));
        custom.setOnAction(event -> singleBoardCtrl.addCustomTag(tags));
        showTags(tags, card);
        MainCtrl mainCtrl = singleBoardCtrl.getMainCtrl();
        Stage popUpStage = new Stage();
        root.setOnKeyPressed(event -> {
            mainCtrl.showHelpPage(event);
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                singleBoardCtrl.server.stopExec();
                popUpStage.close();
                cardNode.requestFocus();
                scaleTransition.setNode(cardNode);
                scaleTransition.play();
            }
        });
        Scene scene = new Scene(root);
        popUpStage.setTitle("Task Details");
        popUpStage.setScene(scene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
        popUpStage.setOnCloseRequest(event -> singleBoardCtrl.server.stopExec());
        singleBoardCtrl.refresh();
    }

    public void showTags(VBox parent, Card current) {
        for (String t : current.getTags().keySet()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Tag.fxml"));
            AnchorPane tag;
            try {
                tag = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Pair<String, String> tagPair = Pair.of(t, current.getTags().get(t));
            tag.setUserData(tagPair);
            Label title = (Label) tag.lookup("#tagName");
            title.setText(t);
            Button deleteBtn = (Button) tag.lookup("#delBtn");
            deleteBtn.setOnAction(event -> deleteTag(event, parent));
            ImageView imageView = new ImageView(Objects.requireNonNull(getClass()
                    .getResource("../images/trash.png")).toExternalForm());
            imageView.setFitWidth(deleteBtn.getPrefWidth());
            imageView.setFitHeight(deleteBtn.getPrefHeight());
            imageView.setPreserveRatio(true);
            deleteBtn.setGraphic(imageView);
            deleteBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT
                    , new CornerRadii(5), javafx.geometry.Insets.EMPTY)));
            deleteBtn.setOnMouseEntered(
                    event -> deleteBtn.setStyle("-fx-background-color: white"));
            deleteBtn.setOnMouseExited(
                    event -> deleteBtn.setStyle("-fx-background-color: transparent"));
            BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(current.getTags()
                    .get(t))
                    , null, null);
            Background background = new Background(backgroundFill);
            tag.setBackground(background);
            parent.getChildren().add(tag);
        }

    }

    public void deleteTag(ActionEvent event, VBox parent) {
        Button source = (Button) event.getSource();
        parent.getChildren().remove(source.getParent());
        //singleBoardCtrl.server.deleteTagToCard(card.getId(), tag);
    }

    void setDone(long listId, Card current, ActionEvent event) {
        Button source = (Button) event.getSource();
        AnchorPane ap = (AnchorPane) source.getParent();
        TextField title = (TextField) ap.lookup("#taskTitle");
        if (!title.getText().trim().isEmpty()) {
            current.setTitle(title.getText());
        }
        TextArea desc = (TextArea) ap.lookup("#taskDescription");
        current.setDescription(desc.getText());
        VBox subs = (VBox) ap.lookup("#subtaskVbox");
        for (int i = 0; i < subs.getChildren().size(); i++) {
            Node hb = subs.getChildren().get(i);
            if (hb instanceof TextField subtask) {
                if (!current.getSubtasks().contains(subtask.getText())
                        && !subtask.getText().equals(""))
                    current.addSubTask(subtask.getText());
            } else {
                CheckBox cb = (CheckBox) ((HBox) hb).getChildren().get(0);
                System.out.println("Text of cur subtask processed: " + cb.getText());
                if (!current.getSubtasks().contains(cb.getText()))
                    current.addSubTask(cb.getText());

                if (cb.isSelected()) {
                    current.completeSubTask(cb.getText());
                }

                if (!current.getSubtasks().get(i).equals(cb.getText())
                        && current.getSubtasks().contains(cb.getText())) {
                    current.removeSubTask(cb.getText());
                    current.addSubtaskAtIndex(cb.getText(), i);
                    if (cb.isSelected()) {
                        current.completeSubTask(cb.getText());
                    }
                }
            }
        }
        VBox tags = (VBox) ap.lookup("#tagVbox");

        current.getTags().clear();
        for (Node n : tags.getChildren()) {
            AnchorPane tagAp = (AnchorPane) n;
            Pair<String, String> tagPair = (Pair<String, String>) tagAp.getUserData();
            if (!current.getTags().containsKey(((Pair<String, String>) tagAp.getUserData())
                    .getLeft())) {
                current.addTag(tagPair.getLeft(), tagPair.getRight());
            }
            if (!singleBoardCtrl.current_board.getTagLists().containsKey(tagPair.getLeft()))
                singleBoardCtrl.server.addTagToBoard(singleBoardCtrl.BoardID, tagPair.getLeft()
                        , tagPair.getRight());
        }
        singleBoardCtrl.server.addCard(current);
        singleBoardCtrl.updateCardFromList(listId, current);
        singleBoardCtrl.server.stopExec();

        Stage popup = (Stage) source.getScene().getWindow();
        popup.close();
        //singleBoardCtrl.refresh();
    }

    public void setDelete(ActionEvent event, Node hbox, Card current, long listId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Task");
        alert.setContentText("Are you sure you want to delete this task? (Irreversible)");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                VBox par = (VBox) hbox.getParent();
                par.getChildren().remove(hbox);
                singleBoardCtrl.nodeCardMap.remove(hbox, current);
                singleBoardCtrl.server.deleteCardFromList(listId, current, true);
                //singleBoardCtrl.refresh();
                Button source = (Button) event.getSource();
                Stage popup = (Stage) source.getScene().getWindow();
                System.out.println(popup);
                //popup.close();
            }
        });
        singleBoardCtrl.server.stopExec();
    }

    public void setCancel(ActionEvent event) {
        Button cancel = (Button) event.getSource();
        Stage popup = (Stage) cancel.getScene().getWindow();
        popup.close();
        singleBoardCtrl.server.stopExec();
    }

    public void addNewCard(VBox parent) {
        BoardList boardList = (BoardList) parent.getUserData();
        long listId = boardList.getId();
        Optional<String> cardTitle = showTitleDialog();
        if (cardTitle.isPresent()) {
            Card newCard = new Card(cardTitle.get());
            placeCard(parent, newCard);
            Card saved = singleBoardCtrl.server.addCard(newCard);
            newCard.setId(saved.getId());
            singleBoardCtrl.saveCardToList(listId, newCard);
            singleBoardCtrl.refresh();
        }
    }

    public Optional<String> showTitleDialog() {
        TextInputDialog titleInput = new TextInputDialog();
        titleInput.setTitle("Task Title");
        titleInput.setHeaderText("Set task title");
        titleInput.setContentText("Enter task title:");
        Optional<String> result = titleInput.showAndWait();
        if (result.isPresent() && result.get().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Title cannot be empty!", ButtonType.OK);
            alert.showAndWait();
            return showTitleDialog();
        }
        return result;
    }

    void setDragAndDrop(VBox parent, Node cardNode) {
        Long listId = ((BoardList) parent.getUserData()).getId();
        cardNode.setOnDragDetected(event -> {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            SingleBoardCtrl.dragboard = cardNode.startDragAndDrop(TransferMode.MOVE);
            singleBoardCtrl.content = new ClipboardContent();
            singleBoardCtrl.content.putString(
                    cardNode.getId() + "; " + ((BoardList) parent.getUserData()).getId());
            SingleBoardCtrl.dragboard.setContent(singleBoardCtrl.content);
            // Create a snapshot of the current card
            WritableImage snapshot = cardNode.snapshot(new SnapshotParameters(), null);
            ImageView imageView = new ImageView(snapshot);
            imageView.setFitWidth(cardNode.getBoundsInLocal().getWidth());
            imageView.setFitHeight(cardNode.getBoundsInLocal().getHeight());
            // Set the custom drag view to only show the current card being dragged
            SingleBoardCtrl.dragboard.setDragView(
                    imageView.getImage(), event.getX(), event.getY());
            event.consume();
        });
        cardNode.setOnDragOver(event -> {
            if (SingleBoardCtrl.dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        cardNode.setOnDragDropped(event -> {
            boolean success = false;
            if (SingleBoardCtrl.dragboard.hasString()) {
                String[] splitDragboard = SingleBoardCtrl.dragboard.getString().split(";");
                long sourceListId = Long.parseLong(splitDragboard[1].trim());
                long sourceListIndex =
                        singleBoardCtrl.listCtrl.getListIndex(
                                singleBoardCtrl.BoardID, sourceListId);
                ObservableList<Node> hboxChildren = singleBoardCtrl.hbox_lists.getChildren();
                AnchorPane sourceList = (AnchorPane) (hboxChildren.get((int) sourceListIndex));
                int sourceListSize = sourceList.getChildren().size();
                VBox sourceParent = (VBox) sourceList.getChildren().get(sourceListSize - 1);
                Node draggedCardNode = sourceParent.lookup("#" + splitDragboard[0].trim());
                Card draggedCard = SingleBoardCtrl.nodeCardMap.get(draggedCardNode);
                if (draggedCardNode != null) {
                    if (sourceParent != parent) {
                        parent.getChildren().add(0, draggedCardNode);
                        singleBoardCtrl.deleteCardFromList(sourceListId, draggedCard, false);
                        singleBoardCtrl.saveCardToList(listId, draggedCard);
                        success = true;
                    } else {
                        ObservableList<Node> children = parent.getChildren();
                        int draggedIndex = children.indexOf((AnchorPane) event.getGestureSource());
                        int dropIndex = children.indexOf((AnchorPane) event.getGestureTarget());
                        draggedCardNode = children.remove(draggedIndex);
                        singleBoardCtrl.deleteCardFromList(sourceListId, draggedCard, false);
                        children.add(dropIndex, draggedCardNode);
                        addCardAtIndex(sourceListId, dropIndex, draggedCard);
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        cardNode.setOnDragDone(event -> {
            VBox sourceParent = (VBox) ((AnchorPane) event.getGestureSource()).getParent();
            VBox targetParent = null;
            if (event.getGestureTarget() != null &&
                    event.getGestureTarget() instanceof AnchorPane) {
                targetParent = (VBox) ((AnchorPane) event.getGestureTarget()).getParent();
            }
            if (singleBoardCtrl.dragboard.hasString()
                    && event.isDropCompleted() && sourceParent != targetParent) {
                parent.getChildren().remove(cardNode);
            }
            singleBoardCtrl.refresh();
            event.consume();
        });
    }

    void addCardAtIndex(long sourceListId, int dropIndex, Card card) {
        try {
            singleBoardCtrl.server.addCardAtIndex(sourceListId, dropIndex, card);
        } catch (WebApplicationException e) {
            singleBoardCtrl.alertError(e);
        }
    }
}
