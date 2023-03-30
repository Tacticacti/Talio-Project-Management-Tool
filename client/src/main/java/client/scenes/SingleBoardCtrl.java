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
import javafx.scene.input.Clipboard;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import static client.scenes.MainCtrl.boverview;
import static client.scenes.MainCtrl.primaryStage;

public class SingleBoardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Long BoardID = 1L;
    private Node newCardBtn;
    @FXML
    private HBox hbox_lists;
    @FXML
    private AnchorPane sb_anchor;
    @FXML
    private Button settingsBtn;
    private ObservableList<BoardList> lists;

    private Board current_board;

    @FXML
    private TextField board_name;
    private Map<Node, Card> nodeCardMap;
    private ClipboardContent content;
    private Dragboard dragboard;



    @Inject
    public SingleBoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void requestBoardName(TextField text, Long id) throws Exception {
        if(!text.getText().isEmpty() && !Objects.equals(text.getText().trim(), "")) {
            String name = text.getText().trim();
            current_board.setName(name);
            System.out.println("set, " + current_board + " to " + name);
            server.addBoard(current_board);
        }
        else {
            throw new Exception("board name cannot be empty");
        }
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
        current_board = new Board();
        board_name.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                try {
                    requestBoardName(board_name, BoardID);
                    refresh();
                } catch (Exception e) {
                    refresh();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText("Error changing board's name!\n\n" + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    public void back(){
        primaryStage.setScene(boverview);
    }

    public void createNewList() {
        ObservableList<Node> board_lists = hbox_lists.getChildren();

        Long listId = server.addEmptyList(BoardID, "task list");
        BoardList boardList = server.getList(listId);
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

    public void requestNameChange(TextField title, Node list) throws Exception {
        if (!title.getText().isEmpty()) {
            System.out.println(title.getText().trim());
            BoardList changedBoardList = (BoardList) list.getUserData();
            System.out.println("requesting change name: " + BoardID +  " " +
                    changedBoardList.getId() + " " + title.getText().trim());
            server.changeListName(changedBoardList.getId(), title.getText().trim());
        }
        else {
            throw new Exception("List name cannot be empty.");
        }
    }

    private void setListTitle(BoardList boardList, Node list) {
        TextField title = (TextField) list.lookup("#list_title");
        title.setText(boardList.getName());
        title.setOnAction(event -> {
            try {
                requestNameChange(title, list);
                refresh();
            } catch (Exception e) {
                refresh();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Error changing list's name!\n\n" + e.getMessage());
                alert.showAndWait();
            }
        });
        title.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                try {
                    requestNameChange(title, list);
                    refresh();
                } catch (Exception e) {
                    refresh();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText("Error changing list's name!\n\n" + e.getMessage());
                    alert.showAndWait();
                }
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
                server.removeBoardList(BoardID, boardList.getId());
                current_board.removeList(boardList);
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
            cardNode.setOnMouseClicked(event -> {
                if(event.getClickCount()==2){
                    setCardDetail(cardNode, parent);
                }

            });
            cardNode.setId(UUID.randomUUID().toString());
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
        if(!title.getText().trim().isEmpty()){
            current.setTitle(title.getText());
        }
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
                    current.removeSubTask(cb.getText());
                    current.addSubtaskAtIndex(cb.getText(), i);
                    if(cb.isSelected()){
                        current.completeSubTask(cb.getText());
                    }
                }
            }
        }
        server.addCard(current);
        updateCardFromList(BoardID, listId, current);
        
        Stage popup = (Stage) source.getScene().getWindow();
        popup.close();
        refresh();
    }

    public void addNewCard(VBox parent){
        BoardList boardList = (BoardList) parent.getUserData();
        long listId = boardList.getId();
        Optional<String> cardTitle = showTitleDialog();
        if (cardTitle.isPresent()) {
            Card newCard = new Card(cardTitle.get());
            placeCard(parent, newCard);
            Card saved = server.addCard(newCard);
            newCard.setId(saved.getId());
            saveCardToList(BoardID, listId, newCard);
            refresh();
        }

    }
    public Optional<String> showTitleDialog(){
        TextInputDialog titleInput = new TextInputDialog();
        titleInput.setTitle("Task Title");
        titleInput.setHeaderText("Create new task");
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
                long originalListIndex = getListIndex(BoardID, originalListId);
                ObservableList<Node> hboxChildren = hbox_lists.getChildren();
                AnchorPane originalList = (AnchorPane) (hboxChildren.get((int) originalListIndex));
                int originalListSize = originalList.getChildren().size();
                VBox originalParent = (VBox) originalList.getChildren().get(originalListSize-1);
                Node draggedCardNode = originalParent.lookup("#" + splitDragboard[0].trim());
                if (draggedCardNode != null && originalParent != parent) {
                    parent.getChildren().add(0, draggedCardNode);
                    Card draggedCard = nodeCardMap.get(draggedCardNode);
                    System.out.println("sending: " + BoardID + " " + listId + " " + draggedCard);
                    deleteCardFromList(BoardID, originalListId, draggedCard);
                    saveCardToList(BoardID, listId, draggedCard);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        cardNode.setOnDragDone(event -> {
            if (dragboard.hasString() && event.isDropCompleted()) {
                parent.getChildren().remove(cardNode);
            }
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

    public void updateCardFromList(Long boardId, Long listId, Card current){
        try{
            server.updateCardFromList(listId, current);
        }catch(WebApplicationException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            //TODO:set custom error message
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    public void saveCardToList(Long boardId, Long listId, Card current){
        try{
            server.addCardToList(listId, current);
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
            server.deleteCardFromList(listIdIndex, current);
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

                server.deleteCardFromList(listId, current);
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

        System.out.println("running!" + server.getBoards());


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

    /*
    public void onTypeTitle() {
        current_board.setName(board_name.getText());
        System.out.println("set, " + current_board + " to " + board_name.getText() );
        server.addBoard(current_board);
    }
     */

    public void setBoard(Board board) {
        this.current_board = board;
        this.BoardID = board.getId();
    }
    public void pullLists(Long id) {
        current_board = server.getBoardById(id);
        lists = FXCollections.observableList(current_board.getLists());
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
        pullLists(BoardID);
        try {
            drawLists();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void copyInvite() {
        ClipboardContent content = new ClipboardContent();
        content.putString(BoardID.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }
}

