package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Card;

import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;


public class AddCardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Card added;

    @FXML
    private TextField title;

    @FXML
    private TextField description;

    @FXML
    private Label board;

    @FXML
    private Label column;

    @FXML
    private VBox subtaskVbox;

    @FXML
    private ListView tags;

    @Inject
    public AddCardCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;
        added = new Card();
    }
    public void cancel() {
        //clearFields();//clearing all fields
        mainCtrl.showBoard();//returning to the board overview
    }

    public void done(){
        mainCtrl.showBoard();
    }


    public void saveCard(){
        //since we auto create a card in a column,
        // it is already in the column list and we need to find it via its id
        //and then change the values of its attributes
        try {
            added.setTitle(title.getText());
            added.setDescription(description.getText());
           // server.addCard(added);
        } catch (WebApplicationException e){
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clearFields();
        //MainCtrl.showPreviousBoardOverview();
        //save button
    }
    public void deleteCard(){
        //finding the card in the database by text in title and description
        //finding the column id from card
        //deleting the card from list of cards for column
        //deleting card from database
        //delete button
        //return to board overview
    }
    public void addSubTask(){

        //showcase a textfield for user input
        //get input and place in a checkboxlistcell in the listview, which has to be editable
        TextField sub = new TextField();
        sub.setPromptText("Enter subtask here");
        subtaskVbox.getChildren().add(0,sub);
        sub.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER){
                subtaskVbox.getChildren().remove(sub);
                displaySubs(sub.getText());
            }
        });
        // added.addSubTask("");
        //adding a subtask
    }
    public void displaySubs(String text){
        HBox sub = new HBox();
        CheckBox cb = new CheckBox();
        cb.setText(text);
        Button delBtn = new Button();
        HBox button = new HBox();
        button.setAlignment(Pos.TOP_CENTER);
        delBtn.setOnAction(event -> subtaskVbox.getChildren().remove(delBtn.getParent()));
        delBtn.setPrefHeight(20);
        ImageView imageView = new ImageView(getClass().getResource("../images/trash.png").toExternalForm());
        imageView.setFitWidth(delBtn.getPrefWidth());
        imageView.setFitHeight(delBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        delBtn.setGraphic(imageView);
        button.getChildren().add(delBtn);
        sub.getChildren().add(cb);
        sub.getChildren().add(delBtn);
        sub.setPrefWidth(subtaskVbox.getWidth());
        subtaskVbox.getChildren().add(subtaskVbox.getChildren().size(),sub);
    }
    public void deleteSubTask(){
        //clicking the delete button on the interface for a subtask
        //getting selected subtask and index form listview using getSelected
        // and placing in two list of String and int
        //removing them from the interface (Listview)
        //removing them from card
        added.removeSubTask("");
        //adding a subtask

    }
    public void addTag(){
        //adding a tag
    }


//    private Card getCard() {
//        //method for getting a new card from the user input
//        added = new Card(title.getText(), description.getText());
//        ObservableList<String> tasks;
//        tasks = subtasks.getItems();
//        for(String s:tasks) {
//            added.addSubTask(s);
//        }
//        //list of subtasks
//        //list of tags
//        return added;
//    }

    private void clearFields() {
        title.clear();
        description.clear();
        //subtasks.getItems().clear();//removing all added subtasks
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                saveCard();
                break;
            case ESCAPE:
                cancel();
                break;
            case DELETE:
                deleteCard();
            default:
                break;
        }
    }
}
