package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardList;
import commons.Card;

import client.utils.ServerUtils;
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

import com.google.inject.Inject;
import javafx.scene.Node;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    private ClipboardContent content;

    private Dragboard board;
    private AnchorPane card;

    @Inject
    public SingleBoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void pullLists(Long id) {
        Board tmpBoard = server.getBoardById(id);
        lists = FXCollections.observableList(tmpBoard.getLists());
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
        var board_lists = hbox_lists.getChildren();
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

        Button deleteList =  (Button) list.lookup("#deleteBtn");
        deleteList.setOnAction(event -> board_lists.remove(deleteList.getParent()));
        Button btn =  (Button) list.lookup("#deleteBtn");
        // btn.setOnAction(event -> board_lists.remove(btn.getParent()));
        btn.setOnAction(event -> {
                server.removeBoardList(boardId, boardList.getId());
                try {
                    refresh();
                }
                catch(Exception e) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText("Error removing list!");
                    alert.showAndWait();
                }
            }
        );

        TextField title = (TextField) list.lookup("#list_title");
        title.setText(boardList.getName());

        title.setOnAction(event -> {
            try {
                if (!title.getText().isEmpty()) {
                    System.out.println(title.getText());
                    BoardList tmp = (BoardList) list.getUserData();
                    System.out.println("requesting change name: " + boardId +  " " + tmp.getId() +
                            " " + title.getText());
                    server.changeListName(boardId, tmp.getId(), title.getText());
                }
            } catch (Exception e) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Error changing list's name!");
                alert.showAndWait();
            }
        });
        Button newCard =  (Button) list.lookup("#addNewCardButton");
        newCard.setOnAction(event ->{
            VBox parentList = (VBox) newCard.getParent();
            addCard(parentList);
        });

        // board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();
        board_lists.add(list);
        return list;
    }


    public Node displayList(BoardList boardList) throws IOException {
        var board_lists = hbox_lists.getChildren();

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
            addCard(par);
        });

        board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();
        return list;
    }

    public void placeCard(VBox parent, Card card){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        CardGUICtrl cgc = new CardGUICtrl(server, mainCtrl);
        fxmlLoader.setController(cgc);
        try {
            Node hbox = fxmlLoader.load();
            hbox.setId(UUID.randomUUID().toString());
            Button det = (Button) hbox.lookup("#details");
            Label title = (Label) hbox.lookup("#taskTitle");
            title.setText(card.getTitle());
            det.setOnAction(event -> enterCard(hbox, parent));
            nodeCardMap.put(hbox, card);
            int index = parent.getChildren().size()-1;
            if(parent.getChildren().size()==1){
                index=0;
            }
            parent.getChildren().add(index, hbox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }        
    }

    public void addCard(VBox parent){
         TextInputDialog titleinput = new TextInputDialog();
        titleinput.setTitle("Task Title");
        titleinput.setHeaderText("Create new task");
        titleinput.setContentText("Enter task title:");
        titleinput.showAndWait().ifPresent(title ->{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
            CardGUICtrl controller = new CardGUICtrl(server, mainCtrl);
            fxmlLoader.setController(controller);
            Card newCard = new Card(title);
            try {
                Node card = (Node) fxmlLoader.load();
                Label titleLabel = (Label) card.lookup("#taskTitle");
                titleLabel.setText(title);
                Button detailButton = (Button) card.lookup("#details");
                detailButton.setOnAction(event -> enterCard(card, parent));
                nodeCardMap.put(card, newCard);
                int index =parent.getChildren().size()-1;
                if(parent.getChildren().size()==1){
                    index=0;
                }
                parent.getChildren().add(index, card);
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
    //                    System.out.println("Card " + board.getString() + " is being dragged!");
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });
                card.setOnDragDone(event -> {
                    if (board.hasString()) {
                        var cardParent = (VBox) card.getParent();
                        cardParent.getChildren().remove(card);
                    }
                    System.out.println("On Drag Done: " + parent);
                    System.out.println(parent.getChildren());
                    event.consume();
                });
                card.setOnDragDropped(event -> {
                    System.out.println("On Drag Dropped: " + parent);
                    boolean success = false;
                    if (board.hasString()) {
                        System.out.println("Hello");
                        System.out.println(parent.getChildren());
                        parent.getChildren().add(0, card);
                        System.out.println("world");
                        success = true;
                        System.out.println(parent.getChildren());
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
                parent.getChildren().add(index, card);
                Card saved = server.addCard(newCard);
                newCard.setId(saved.getId());
                // server.addCardToList(1L, 0L, newCard);
                //enterCard(card);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void enterCard(Node card, VBox parent){
//        AnchorPane list = (AnchorPane) parent.getParent();
//        BoardList boardList = (BoardList) list.getUserData();
//        long listid = boardList.getId();
        long listid = 0l;
        Card blah = nodeCardMap.get(card);
        Card current = server.getCardById(nodeCardMap.get(card).getId());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddCard.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Button doneBtn = (Button) root.lookup("#doneTaskButton");
        doneBtn.setOnAction(event -> {
            try {
                done(event, current, listid);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button deleteBtn = (Button) root.lookup("#deleteTaskButton");
        deleteBtn.setOnAction(event -> delete(event, card, current, listid));
        Button cancelBtn = (Button) root.lookup("#cancelTaskButton");
        cancelBtn.setOnAction(event -> cancel(event, card));
        TextField title = (TextField) root.lookup("#taskTitle");
        title.setText(current.getTitle());
        TextArea desc =  (TextArea) root.lookup("#taskDescription");
        desc.setText(current.getDescription());
        Button addSub = (Button) root.lookup("#addSubtaskButton");
        AddCardCtrl addCardCtrl = fxmlLoader.getController();
        addSub.setOnAction(event ->  addCardCtrl.addSubTask(current));
        if(current.getSubtasks()!=null){
            for(String s: current.getSubtasks()){
                if(current.getCompletedTasks().contains(s)){
                    addCardCtrl.displayCompletedSubs(s, current);
                }else{
                    addCardCtrl.displaySubs(s, current);
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

    public void done(javafx.event.ActionEvent event, Card current, long listid) throws IOException {
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
        //long listIndex = getListIndex(boardId, listid);
        // saveCardToList(1l,0l,current);
        // server.updateCardFromList(1L, listIndex, current);
        Stage popup = (Stage) source.getScene().getWindow();
        popup.close();
        //refreshList(1l, listIndex);
    }


    public long getListIndex(Long boardId, Long listid){
        Board b = server.getBoardById(boardId);
        for(int i=0; i<b.getLists().size();i++){
            if(b.getLists().get(i).getId()==listid){
                return i;
            }
        }
        return -1;

    }

    public void updateCardFromList(Long boardId, Long listidindex, Card current){
        try{
            server.updateCardFromList(boardId, listidindex, current);
        }catch(WebApplicationException e){
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    public void saveCardToList(Long boardId, Long listidindex, Card current){
        try{
            server.addCardToList(boardId, listidindex, current);
        }catch(WebApplicationException e){
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void refreshList(long boardId, long listid) throws IOException {
        Board b = server.getBoardById(boardId);
        hbox_lists.getChildren().remove((int) listid);
        hbox_lists.getChildren().add((int) listid, displayList(b.getLists().get((int) listid)));

    }

    public void delete(ActionEvent event, Node hbox, Card current, long listid){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Task");
        alert.setContentText("Are you sure you want to delete this task? (Irreversible)");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                VBox par = (VBox) hbox.getParent();
                par.getChildren().remove(hbox);
                nodeCardMap.remove(hbox, current);
                server.deleteCard(current.getId());
                // long listIndex = getListIndex(boardId,listid);
                // server.deleteCardFromList(1l, listIndex, current);
//        try {
////            refreshList(boardId,listIndex);
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
                Button source = (Button) event.getSource();
                Stage popup = (Stage) source.getScene().getWindow();
                popup.close();
            }
        });

    }



    public void cancel(ActionEvent event, Node hboxCard){
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

    public void drawLists() throws IOException {
        var board_lists = hbox_lists.getChildren();
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

