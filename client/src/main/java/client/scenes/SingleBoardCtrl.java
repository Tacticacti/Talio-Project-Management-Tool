package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardList;
import commons.Card;

import jakarta.ws.rs.WebApplicationException;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;


import java.net.URL;

import javafx.scene.Node;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.image.WritableImage;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

public class SingleBoardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Long BOARDID = 1L;
    private Node newCardBtn;
    @FXML
    private HBox hbox_lists;
    @FXML
    private AnchorPane sb_anchor;
    @FXML
    private Button settingsBtn;
    private ObservableList<BoardList> lists;
    private Map<Node, Card> nodeCardMap;
    private ClipboardContent content;
    private Dragboard dragboard;

    @Inject
    public SingleBoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize (URL location, ResourceBundle resources){
        ImageView imageView = new ImageView(getClass()
            .getResource("../images/settings_icon.png")
            .toExternalForm());
        newCardBtn = hbox_lists.getChildren().get(0);
        imageView.setFitWidth(settingsBtn.getPrefWidth());
        imageView.setFitHeight(settingsBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        settingsBtn.setGraphic(imageView);
        nodeCardMap = new HashMap<>();
//        Board b = server.getBoardById(1l);

//         try {
//             createNewList();
// //            for(BoardList bl: b.getLists()){
// //                hbox_lists.getChildren()
// //                .add((hbox_lists.getChildren().size()-2), displayList(bl));
// //            }
//         }
//         catch(IOException e) {
//             e.printStackTrace();
//         }
        //server.addCardToList(1L, 0L, new Card("card from init"));

        //  TODO change 1L -> board_id if we are going multiboard
        // pullLists(1L);

        // for(BoardList bl : lists) {
        //     System.out.println("processing: " + bl.getName());
        //     var node = wrapBoardList(bl);
        //     mainAnchor.getChildren().addAll(node);
        //     System.out.println(node.getChildren());
        // }

    }

    public void back(){
        mainCtrl.showBoardOverview();
    }

    public void createNewList() {
        ObservableList<Node> board_lists = hbox_lists.getChildren();
        BoardList boardList = server.addEmptyList(1L, " ");
        Node list;
        try {
            list = wrapList(boardList, board_lists);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        refresh();
    }

    public Node wrapList(BoardList boardList, ObservableList<Node> board_lists) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("listGUI.fxml"));
        Node list = loader.load();
        list.setUserData(boardList);
        // set up deleting a board list
        setDeleteBoardList(boardList, board_lists, list);

        // set up putting list title
        setListTitle(boardList, list);

        // set up adding new card
        Button newCardButton =  (Button) list.lookup("#addNewCardButton");
        VBox parentList = (VBox) newCardButton.getParent();
        parentList.setUserData(boardList);
        parentList.setSpacing(20);
        for(Card card: boardList.getCards()){
            placeCard(parentList, card);
        }
        newCardButton.setOnAction(event ->{
            addNewCard(parentList);
        });
        // board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();
        board_lists.add(list);
        return list;
    }

    private void setListTitle(BoardList boardList, Node list) {
        TextField title = (TextField) list.lookup("#list_title");
        title.setText(boardList.getName());
        title.setOnAction(event -> {
            try {
                if (!title.getText().isEmpty()) {
                    System.out.println(title.getText());
                    BoardList changedBoardList = (BoardList) list.getUserData();
                    System.out.println("requesting change name: " + BOARDID +  " " +
                            changedBoardList.getId() + " " + title.getText());
                    server.changeListName(BOARDID, changedBoardList.getId(), title.getText());
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Error changing list's name!");
                alert.showAndWait();
            }
        });
    }

    private void setDeleteBoardList(BoardList boardList, ObservableList<Node> board_lists,
                                    Node list) {
        Button deleteBoardList =  (Button) list.lookup("#deleteBtn");
        deleteBoardList.setOnAction(event -> {
                // deleting on client(GUI) side
                board_lists.remove(deleteBoardList.getParent());

                // deleting list on server side
                server.removeBoardList(BOARDID, boardList.getId());
                try {
                    refresh();
                }
                catch(Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText("Error removing list!");
                    alert.showAndWait();
                }
            }
        );
    }


    public Node displayList(BoardList boardList) throws IOException {
        ObservableList<Node> board_lists = hbox_lists.getChildren();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("listGUI.fxml"));
        Node list = loader.load();
        TextField title = (TextField) list.lookup("#list_title");
        title.setText(boardList.getName());


        Button btn =  (Button) list.lookup("#deleteBtn");
        btn.setOnAction(event -> board_lists.remove(btn.getParent()));

        Button btn2 =  (Button) list.lookup("#addNewCardButton");
        VBox par = (VBox) btn2.getParent();
        par.setSpacing(40);
        for(Card c: boardList.getCards()){
            placeCard(par, c);
        }
        btn2.setOnAction(event ->{
            addNewCard(par);
        });

        board_lists.get(board_lists.size()-2).lookup("#list_title")
                .requestFocus();
        return list;
    }

    public void placeCard(VBox parent, Card card){
        String cardTitle = card.getTitle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        try {
            Node cardNode = fxmlLoader.load();
            Border border = new Border(new BorderStroke(Paint.valueOf("black")
                    , BorderStrokeStyle.DASHED
                    , new CornerRadii(10), BorderWidths.DEFAULT));
            ((AnchorPane) cardNode).setBorder(border);
            if(card.getDescription()==null || card.getDescription().equals("")){
                cardNode.lookup("#descIndicator").setVisible(false);
            }
            if(card.getSubtasks().size()>0){
                ((Label) cardNode.lookup("#progress"))
                        .setText(card.getCompletedSubs()+"/"
                                + card.getSubtasks().size());
            }
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), cardNode);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            cardNode.setOnMouseEntered(event -> {
                scaleTransition.play();
            });
            cardNode.setOnMouseExited(event -> {
                scaleTransition.stop();
                cardNode.setScaleX(1);
                cardNode.setScaleY(1);
            });
            cardNode.setId(UUID.randomUUID().toString());
            Button detail = (Button) cardNode.lookup("#details");
            detail.setOnMouseEntered(event -> {
                DropShadow shadow = new DropShadow();
                shadow.setRadius(30.0);
                shadow.setBlurType(BlurType.GAUSSIAN);
                shadow.setColor(Color.BLACK);
                detail.setEffect(shadow);
                detail.setStyle("-fx-font-weight: bold; " +
                        "-fx-cursor:hand;" +
                        " -fx-background-color: transparent");
            });
            detail.setOnMouseExited(event -> {
                detail.setEffect(null);
                detail.setStyle("-fx-cursor:pointer; " +
                        "-fx-background-color: transparent");
            });
            Label title = (Label) cardNode.lookup("#taskTitle");
            detail.setOnAction(event -> setCardDetail(cardNode, parent));
            title.setText(cardTitle);
            nodeCardMap.put(cardNode, card);
            setDragAndDrop(parent, cardNode);
            int index = parent.getChildren().size()-1;
            if(parent.getChildren().size()==1){
                index=0;
            }
            parent.getChildren().add(index, cardNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }        
    }

    public void setCardDetail(Node cardNode, VBox parent){
//        AnchorPane list = (AnchorPane) parent.getParent(); unused variable
        BoardList boardList = (BoardList) parent.getUserData();
        long listId = boardList.getId();
        Card card = server.getCardById(nodeCardMap.get(cardNode).getId());
        System.out.println("-------------------------");
        System.out.println("got: " + card);
        System.out.println("-------------------------");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddCard.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // sets up done card button
        Button doneButton = (Button) root.lookup("#doneTaskButton");
        doneButton.setOnAction(event -> {setDone(listId, card, event);});

        //sets up delete card button
        Button deleteButton = (Button) root.lookup("#deleteTaskButton");
        deleteButton.setOnAction(event -> setDelete(event, cardNode, card, listId));

        //sets up cancel card button
        Button cancelButton = (Button) root.lookup("#cancelTaskButton");
        cancelButton.setOnAction(event -> setCancel(event, cardNode));

        //sets up card title
        TextField title = (TextField) root.lookup("#taskTitle");
        title.setText(card.getTitle());

        //sets up card description
        TextArea description =  (TextArea) root.lookup("#taskDescription");
        description.setText(card.getDescription());

        // sets up sub-task operations
        Button addSubTask = (Button) root.lookup("#addSubtaskButton");
        AddCardCtrl addCardCtrl = fxmlLoader.getController();
        addSubTask.setOnAction(event ->  addCardCtrl.addSubTask(card));
        if(card.getSubtasks()!=null){
            for(String str: card.getSubtasks()){
                if(card.getCompletedTasks().contains(str)){
                    addCardCtrl.displayCompletedSubs(str, card);
                } else{
                    addCardCtrl.displaySubs(str, card);
                }
            }
        }
        Scene scene = new Scene(root);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Card Details");
        popUpStage.setScene(scene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }

    private void setDone(long listId, Card current, ActionEvent event) {
        Button source = (Button) event.getSource();
        AnchorPane ap = (AnchorPane) source.getParent();
        TextField title = (TextField) ap.lookup("#taskTitle");
        current.setTitle(title.getText());
        TextArea desc =  (TextArea) ap.lookup("#taskDescription");
        current.setDescription(desc.getText());
        VBox subs = (VBox) ap.lookup("#subtaskVbox");
        for(int i=0; i<subs.getChildren().size();i++){
            Node hb = subs.getChildren().get(i);
            if(hb instanceof TextField){
                TextField subtask = (TextField) hb;
                if(!current.getSubtasks().contains(subtask.getText())
                        && !subtask.getText().equals(""))
                    current.addSubTask(subtask.getText());
            }else{
                CheckBox cb = (CheckBox) ((HBox) hb).getChildren().get(0);
                System.out.println("Text of cur subtask processed: " + cb.getText());
                if(!current.getSubtasks().contains(cb.getText()))
                    current.addSubTask(cb.getText());
                if(!current.getSubtasks().get(i).equals(cb.getText())
                        && current.getSubtasks().contains(cb.getText())){
                    List<String> complTask = current.getCompletedTasks();
                    current.removeSubTask(cb.getText());
                    current.addSubtaskAtIndex(cb.getText(), i);
                    if(cb.isSelected()){
                        current.completeSubTask(cb.getText());
                    }
                }
            }
        }
        server.addCard(current);
        long listIndex = getListIndex(BOARDID, listId);
        // saveCardToList(1l,0l,current);
        // server.updateCardFromList(1L, listIndex, current);
        updateCardFromList(BOARDID, listIndex, current);
        Stage popup = (Stage) source.getScene().getWindow();
        popup.close();
        refresh();
    }

    public void addNewCard(VBox parent){
        BoardList boardList = (BoardList) parent.getUserData();
        long listId = boardList.getId();
        TextInputDialog titleInput = new TextInputDialog();
        titleInput.setTitle("Task Title");
        titleInput.setHeaderText("Create new task");
        titleInput.setContentText("Enter task title:");
        titleInput.showAndWait().ifPresent(cardTitle ->{

            //TODO: Fix case when cardTitle is empty
            System.out.println("-----------------------------");
            System.out.println(cardTitle);
            System.out.println("-----------------------------");

            Card newCard = new Card(cardTitle);
            placeCard(parent, newCard);
            Card saved = server.addCard(newCard);
            newCard.setId(saved.getId());
            //server.addCardToList(1L, 0L, newCard);
            long listIndex = getListIndex(BOARDID, listId);
            saveCardToList(BOARDID, listIndex, newCard);
            refresh();
            //enterCard(card);
        });
    }

    private void setDragAndDrop(VBox parent, Node cardNode) {
        Card card = nodeCardMap.get(cardNode);
        Long listId = ((BoardList) parent.getUserData()).getId();
        cardNode.setOnDragDetected(event -> {
            dragboard = cardNode.startDragAndDrop(TransferMode.MOVE);
            content = new ClipboardContent();
            content.putString(cardNode.getId() + "; " + ((BoardList) parent.getUserData()).getId());
            dragboard.setContent(content);

            // Create a snapshot of the current card
            WritableImage snapshot = cardNode.snapshot(new SnapshotParameters(), null);
            ImageView imageView = new ImageView(snapshot);
            imageView.setFitWidth(cardNode.getBoundsInLocal().getWidth());
            imageView.setFitHeight(cardNode.getBoundsInLocal().getHeight());

            // Set the custom drag view to only show the current card being dragged
            dragboard.setDragView(imageView.getImage(), event.getX(), event.getY());
            event.consume();
        });
        cardNode.setOnDragOver(event -> {
            if (dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        cardNode.setOnDragDropped(event -> {
            boolean success = false;
            if (dragboard.hasString()) {
                String[] splitDragboard = dragboard.getString().split(";");
                long originalListId = Long.parseLong(splitDragboard[1].trim());
                long originalListIndex = getListIndex(BOARDID, originalListId);
                ObservableList<Node> hboxChildren = hbox_lists.getChildren();
                AnchorPane originalList = (AnchorPane) (hboxChildren.get((int) originalListIndex));
                int originalListSize = originalList.getChildren().size();
                VBox originalParent = (VBox) originalList.getChildren().get(originalListSize-1);
                Node draggedCardNode = originalParent.lookup("#" + splitDragboard[0].trim());
                if (draggedCardNode != null && originalParent != parent) {
                    parent.getChildren().add(0, draggedCardNode);
                    Card draggedCard = nodeCardMap.get(draggedCardNode);
                    saveCardToList(BOARDID, listId, draggedCard);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        cardNode.setOnDragDone(event -> {
            if (dragboard.hasString() && event.isDropCompleted()) {
                parent.getChildren().remove(cardNode);
                deleteCardFromList(BOARDID, listId, card);
            }
//            refresh(); TODO:uncomment after server error has been fixed.
            event.consume();
        });
    }


    public long getListIndex(Long boardId, Long listId){
        Board b = server.getBoardById(boardId);
        for(int i=0; i<b.getLists().size();++i){
            if(b.getLists().get(i).getId()==listId){
                return i;
            }
        }
        return -1;

    }

    public void updateCardFromList(Long boardId, Long listindex, Card current){
        try{
            server.updateCardFromList(boardId, listindex, current);
        }catch(WebApplicationException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            //TODO:set custom error message
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    public void saveCardToList(Long boardId, Long listIdIndex, Card current){
        try{
            server.addCardToList(boardId, listIdIndex, current);
        }catch(WebApplicationException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            //TODO:set custom error message
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void deleteCardFromList(Long boardId, Long listIdIndex, Card current){
        try{
            server.deleteCardFromList(boardId, listIdIndex, current);
        }catch(WebApplicationException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            //TODO:set custom error message
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void setDelete(ActionEvent event, Node hbox, Card current, long listId){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Task");
        alert.setContentText("Are you sure you want to delete this task? (Irreversible)");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                VBox par = (VBox) hbox.getParent();
                par.getChildren().remove(hbox);
                nodeCardMap.remove(hbox, current);
                //server.deleteCard(current.getId());
                long listIndex = getListIndex(BOARDID, listId);
                server.deleteCardFromList(BOARDID, listIndex, current);
                refresh();
                Button source = (Button) event.getSource();
                Stage popup = (Stage) source.getScene().getWindow();
                popup.close();
            }
        });

    }



    public void setCancel(ActionEvent event, Node hboxCard){
        Button cancel = (Button) event.getSource();
        Stage popup = (Stage) cancel.getScene().getWindow();
        popup.close();
    }


    public void openBoardSettings() throws IOException {
        System.out.println("running!");


        FXMLLoader loader = new FXMLLoader(getClass().getResource("customizationPage.fxml"));
        AnchorPane customization = loader.load();

        customization.setLayoutY(250);
        customization.setLayoutX(770);

        customization.setScaleX(1.5);
        customization.setScaleY(1.5);


        Button closebtn =  (Button) customization.lookup("#closeCustomizationMenu");
        closebtn.setOnAction(event -> sb_anchor.getChildren().remove(closebtn.getParent()));

        // doesn't actually delete anything just goes back to board overview
        Button delbtn =  (Button) customization.lookup("#deleteBoard");
        delbtn.setOnAction(event -> {
            // remove this specific board


            mainCtrl.showBoardOverview();

        });



        sb_anchor.getChildren().add(customization);


    }

    public void pullLists(Long id) {
        Board tmpBoard = server.getBoardById(id);
        lists = FXCollections.observableList(tmpBoard.getLists());
    }

    public void drawLists() throws IOException {
        ObservableList<Node> board_lists = hbox_lists.getChildren();
        board_lists.clear();
        for(BoardList boardList : lists) {
            wrapList(boardList, board_lists);
        }
        board_lists.add(newCardBtn);
    }

    public void refresh() {
        pullLists(BOARDID);
        try {
            drawLists();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

