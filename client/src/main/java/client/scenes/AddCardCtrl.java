package client.scenes;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Card;
import commons.Person;
import commons.Quote;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;

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
    private ListView subtasks;

    @FXML
    private ListView tags;

    @Inject
    public AddCardCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;
        added = new Card();
    }
    public void cancel() {
        clearFields();//clearing all fields
        mainCtrl.showOverview();//returning to the board overview
    }

    public void saveCard(){
        //since we auto create a card in a column,
        // it is already in the column list and we need to find it via its id
        //and then change the values of its attributes
        try {
            added.setTitle(title.getText());
            added.setDescription(description.getText());
            server.addCard(added);
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

        CheckBoxListCell newcell = new CheckBoxListCell();
        subtasks.getItems()
                .add(subtasks.getItems().size()-1,
                        newcell);

        added.addSubTask("");
        //adding a subtask
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
        subtasks.getItems().clear();//removing all added subtasks
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
