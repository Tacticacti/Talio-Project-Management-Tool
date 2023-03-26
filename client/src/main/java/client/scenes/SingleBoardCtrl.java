package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardList;
import commons.Card;

import jakarta.ws.rs.WebApplicationException;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


import java.net.URL;

import javafx.scene.Node;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.image.WritableImage;
import javafx.stage.Modality;
import java.io.IOException;
import java.util.ResourceBundle;

public class SingleBoardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    private final Long boardId = 1L;

    private Node newCardBtn;

    @FXML
    private HBox hbox_lists;

    @FXML
    private AnchorPane sb_anchor;

    @FXML
    private Button settingsBtn;

    private ObservableList<BoardList> lists;

    private Map<Node, Card> nodeCardMap;

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
                    System.out.println("requesting change name: " + boardId +  " " + changedBoardList.getId() +
                            " " + title.getText());
                    server.changeListName(boardId, changedBoardList.getId(), title.getText());
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Error changing list's name!");
                alert.showAndWait();
            }
        });
    }

    private void setDeleteBoardList(BoardList boardList, ObservableList<Node> board_lists, Node list) {
        Button deleteBoardList =  (Button) list.lookup("#deleteBtn");
        deleteBoardList.setOnAction(event -> {
                // deleting on client(GUI) side
                board_lists.remove(deleteBoardList.getParent());

                // deleting list on server side
                server.removeBoardList(boardId, boardList.getId());
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
        for(Card c: boardList.getCards()){
            placeCard(par, c);
        }
        btn2.setOnAction(event ->{
            addNewCard(par);
        });

        board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();
        return list;
    }

    public void placeCard(VBox parent, Card card){
        String cardTitle = card.getTitle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        try {
            Node cardNode = fxmlLoader.load();
            cardNode.setId(UUID.randomUUID().toString());
            Button detail = (Button) cardNode.lookup("#details");
            Label title = (Label) cardNode.lookup("#taskTitle");
            detail.setOnAction(event -> setCardDetail(cardNode, parent));
            title.setText(cardTitle);
            nodeCardMap.put(cardNode, card);
            System.out.println(nodeCardMap.get(cardNode));
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
        for(Node hb: subs.getChildren()){
            if(hb instanceof TextField){
                TextField subtask = (TextField) hb;
                if(!current.getSubtasks().contains(subtask.getText())
                        && !subtask.getText().equals(""))
                    current.addSubTask(subtask.getText());
            }else{
                CheckBox cb = (CheckBox) ((HBox) hb).getChildren().get(0);

                if(!current.getSubtasks().contains(cb.getText()))
                    current.addSubTask(cb.getText());
            }
        }

        server.addCard(current);
        long listIndex = getListIndex(boardId, listId);
        // saveCardToList(1l,0l,current);
        // server.updateCardFromList(1L, listIndex, current);
        updateCardFromList(boardId, listIndex, current);
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
            long listIndex = getListIndex(boardId, listId);
            saveCardToList(boardId, listIndex, newCard);
            refresh();
            //enterCard(card);
        });
    }
    private ClipboardContent content;
    private Dragboard board;
    private void setDragAndDrop(VBox parent, Node card) {
        Card draggedCard = nodeCardMap.get(card);
        card.setOnDragDetected(event -> {
            board = card.startDragAndDrop(TransferMode.MOVE);
            content = new ClipboardContent();
            content.putString(card.getId());
            board.setContent(content);

            // Create a snapshot of the current card
            WritableImage snapshot = card.snapshot(new SnapshotParameters(), null);
            ImageView imageView = new ImageView(snapshot);
            imageView.setFitWidth(card.getBoundsInLocal().getWidth());
            imageView.setFitHeight(card.getBoundsInLocal().getHeight());

            // Set the custom drag view to only show the current card being dragged
            board.setDragView(imageView.getImage(), event.getX(), event.getY());

            System.out.println("On Drag Detected: " + parent);
            System.out.println(parent.getChildren());
            event.consume();
        });
        card.setOnDragOver(event -> {
            if (board.hasString()) {
//              System.out.println("Card " + board.getString() + " is being dragged!");
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        card.setOnDragDropped(event -> {
            System.out.println("On Drag Dropped: " + parent);
            boolean success = false;
            if (board.hasString()) {
                System.out.println(parent.getChildren());
                System.out.println("Hello");
                parent.getChildren().add(0, card);
                System.out.println("world");
                success = true;
                System.out.println(parent.getChildren());
            }
            event.setDropCompleted(success);
            event.consume();
        });
        card.setOnDragDone(event -> {
            if (board.hasString()) {
                parent.getChildren().remove(card);
                server.deleteCard(draggedCard.getId());
            }
            System.out.println("On Drag Done: " + parent);
            System.out.println(parent.getChildren());
            refresh();
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
                long listIndex = getListIndex(boardId, listId);
                server.deleteCardFromList(boardId, listIndex, current);
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
        pullLists(boardId);
        try {
            drawLists();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

