package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Card;

import jakarta.ws.rs.WebApplicationException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.stage.Modality;
import javafx.stage.Stage;





public class AddCardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField taskTitle;

    @FXML
    private TextArea taskDescription;

    @FXML
    private Label board;

    @FXML
    private Label column;

    @FXML
    private VBox subtaskVbox;

    @FXML
    private ListView tags;

    @FXML
    private Button cancelTaskButton;

    private Card current;




    @Inject
    public AddCardCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;

    }
    public AddCardCtrl(){
        this.server =new ServerUtils();
        this.mainCtrl = new MainCtrl();
    }
    private Node node;

    public void setUp(Node card){

    }
    public void cancel(ActionEvent event) {
        //node.getParent();

//        for(String s: additions){
//            current.removeSubTask(s);
//        }
//        for(String s: deletions){
//            current.addSubTask(s);
//        }

        //clearFields();//clearing all fields
        Stage popup = (Stage) cancelTaskButton.getScene().getWindow();
        popup.close();
        // mainCtrl.showBoard();//returning to the board overview
    }

    public void done(){
        if(!taskTitle.getText().equals(current.getTitle())){
            current.setTitle(taskTitle.getText());
        }
        if(taskDescription.getText()!=null)
            if(!taskDescription.getText().equals(current.getDescription())){
                current.setDescription(taskDescription.getText());
            }
        Stage popup = (Stage) cancelTaskButton.getScene().getWindow();
        popup.close();
        server.addCard(current);
    }


    public void saveCard(){
        //since we auto create a card in a column,
        // it is already in the column list and we need to find it via its id
        //and then change the values of its attributes
        try {
            server.addCard(new Card());
        } catch (WebApplicationException e){
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        //MainCtrl.showPreviousBoardOverview();
        //save button
    }
    public void deleteCard(){
        server.deleteCard(5L);
        //finding the card in the database by text in title and description
        //finding the column id from card
        //deleting the card from list of cards for column
        //deleting card from database
        //delete button
        //return to board overview
    }

    public boolean checkIfValid(Card card, String text) {
        if(text == "")
            return false;
        return !card.getSubtasks().contains(text);
    }

    public void addSubTask(Card current){

        //showcase a textfield for user input
        TextField sub = new TextField();
        sub.setPromptText("Enter subtask here");
        subtaskVbox.getChildren().add(0, sub);
        sub.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER) {
                if(checkIfValid(current, sub.getText().trim())) {
                    subtaskVbox.getChildren().remove(sub);
                    displaySubs(sub.getText().trim(), current);
                }
                else {
                    displayAlert("Invalid subtask name!");
                }
            }
        });
        // added.addSubTask("");
        //adding a subtask
    }
    public void displaySubs(String text, Card current){
        HBox sub = new HBox();
        CheckBox cb = createCheckbox(text, current);
        sub.getChildren().add(cb);
        createSubtask(sub, current);
    }

    public CheckBox createCheckbox(String text, Card current){
        CheckBox cb = new CheckBox();
        cb.setText(text);
        TextField textField = new TextField();
        cb.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                textField.setText(cb.getText());
                cb.setGraphic(textField);
                textField.requestFocus();
            }
        });

        textField.setOnAction(event -> {
            cb.setText(textField.getText());
            cb.setGraphic(null);
        });
        cb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(cb.isSelected()){
                    current.completeSubTask(cb.getText());
                    cb.setOpacity(0.5);
                }else{
                    current.uncompleteSubTask(cb.getText());
                    cb.setOpacity(1);
                }
            }
        });
        return cb;
    }

    public void displayCompletedSubs(String text, Card current){
        HBox sub = new HBox();
        CheckBox cb = createCheckbox(text, current);
        cb.setSelected(true);
        cb.setOpacity(0.5);
        sub.getChildren().add(cb);
        createSubtask(sub, current);
    }
    private void createSubtask(HBox sub, Card current){
        Button delBtn = new Button();
        HBox.setHgrow(delBtn, Priority.ALWAYS);
        sub.setAlignment(Pos.BASELINE_LEFT);
        sub.setPadding(new Insets(0));
        delBtn.setOnAction(event -> deleteSubTask(delBtn, current));
        delBtn.setPrefHeight(20);
        ImageView imageView = new ImageView(getClass()
                .getResource("../images/trash.png").toExternalForm());
        imageView.setFitWidth(delBtn.getPrefWidth());
        imageView.setFitHeight(delBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        delBtn.setGraphic(imageView);
        sub.getChildren().add(delBtn);
        sub.setPrefWidth(subtaskVbox.getWidth());
        setDragAndDrop(sub);
        subtaskVbox.getChildren().add(subtaskVbox.getChildren().size(), sub);
    }

    public void deleteSubTask(Button delBtn, Card current){
        subtaskVbox.getChildren().remove(delBtn.getParent());
        HBox parent = (HBox) delBtn.getParent();
        CheckBox cb = (CheckBox) parent.getChildren().get(0);
        current.removeSubTask(cb.getText());
    }

    private void setDragAndDrop(HBox subtaskBox){
        subtaskBox.setOnDragDetected(event -> {
            // Start drag and drop operation
            Dragboard db = subtaskBox.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("VBoxItem");
            db.setContent(content);
            event.consume();
        });

        subtaskBox.setOnDragOver(event -> {
            // Accept the drag if it's a MOVE operation
            if (event.getGestureSource() != subtaskBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        subtaskBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                // Get the index of the item being dragged
                int draggedIndex = subtaskVbox.getChildren()
                        .indexOf((HBox) event.getGestureSource());

                // Get the index where the item is being dropped
                int dropIndex = subtaskVbox.getChildren().indexOf(subtaskBox);

                // Remove the item being dragged from its old position
                subtaskVbox.getChildren().remove(draggedIndex);

                // Add the item being dragged at the new position
                subtaskVbox.getChildren().add(dropIndex, (HBox) event.getGestureSource());
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

    }
    public void addTag(){
        //adding a tag
    }

    public void displayAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(text);
        alert.showAndWait();
    }
}


