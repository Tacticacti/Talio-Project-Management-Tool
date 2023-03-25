package client.scenes;

import commons.Board;
import commons.BoardList;
import commons.Card;
import client.utils.ServerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


import java.net.URL;

import com.google.inject.Inject;
import javafx.scene.Node;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class SingleBoardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @FXML
    private HBox hbox_lists;

    @FXML
    private AnchorPane sb_anchor;

    @FXML
    private Button settingsBtn;

    private ObservableList<BoardList> lists;

    private Map<Node, Card> nodeCardMap;

    private Map<VBox, BoardList> boxBoardListMap;

    private Random Ids;

    @Inject
    public SingleBoardCtrl(ServerUtils server, MainCtrl mainCtrl) throws IOException {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void pullLists(Long id) {
        Board tmpBoard = server.getBoardById(id);
        lists = FXCollections.observableList(tmpBoard.getLists());
    }

    public AnchorPane wrapBoardList(BoardList boardList) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("listGUI.fxml"));
        AnchorPane node;
        try {
            node = loader.load();
        }
        catch(Exception e) {
            e.printStackTrace();
            return new AnchorPane();
        }

        ((TextField) node.getChildren().get(0)).setText(boardList.getName());

        VBox target = (VBox) node.getChildren().get(3);

        for(Card c : boardList.getCards()) {
            System.out.println("processing card: " + c.toString());
            // temporary solution
            target.getChildren().addAll(new Label(c.getTitle()));
        }

        return node;
    }

    @Override
    public void initialize (URL location, ResourceBundle resources){
        ImageView imageView = new ImageView(getClass()
                .getResource("../images/settings_icon.png").toExternalForm());
        imageView.setFitWidth(settingsBtn.getPrefWidth());
        imageView.setFitHeight(settingsBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        settingsBtn.setGraphic(imageView);
        nodeCardMap = new HashMap<>();
//        Board b = server.getBoardById(1l);

        try {
            createNewList();
//            for(BoardList bl: b.getLists()){
//                hbox_lists.getChildren().add((hbox_lists.getChildren().size()-2), displayList(bl));
//            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
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



    public void createNewList() throws IOException {
        var board_lists = hbox_lists.getChildren();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("listGUI.fxml"));
        Node list = loader.load();
        board_lists.add(board_lists.size()-1, list);

        Button btn =  (Button) list.lookup("#deleteBtn");
        btn.setOnAction(event -> board_lists.remove(btn.getParent()));

        TextField title = (TextField) list.lookup("#list_title");

        title.setOnAction(event -> {
            try {
                if (!title.getText().isEmpty()) {
                    createNewList();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button btn2 =  (Button) list.lookup("#addNewCardButton");
        VBox par = (VBox) btn2.getParent();
        btn2.setOnAction(event ->{
            addCard(par);
        });

        board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();
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
            det.setOnAction(event -> enterCard(hbox));
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        CardGUICtrl cgc = new CardGUICtrl(server, mainCtrl);
        fxmlLoader.setController(cgc);
        Card newCard = new Card();
        try {
            Node card = fxmlLoader.load();
            card.setId(UUID.randomUUID().toString());
            Button det = (Button) card.lookup("#details");
            det.setOnAction(event -> enterCard(card));
            nodeCardMap.put(card, newCard);
            int index = parent.getChildren().size()-1;
            if(parent.getChildren().size()==1){
                index=0;
            }
            parent.getChildren().add(index, card);
            Card saved = server.addCard(newCard);
            newCard.setId(saved.getId());
           // server.addCardToList(1L, 0L, newCard);
            enterCard(card);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enterCard(Node card){
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
                done(event, current);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button deleteBtn = (Button) root.lookup("#deleteTaskButton");
        deleteBtn.setOnAction(event -> delete(event, card, current));
        Button cancelBtn = (Button) root.lookup("#cancelTaskButton");
        cancelBtn.setOnAction(event -> cancel(event, card));
        TextField title = (TextField) root.lookup("#taskTitle");
        title.setText(current.getTitle());
        TextArea desc =  (TextArea) root.lookup("#taskDescription");
        desc.setText(current.getDescription());
        if(current.getSubtasks()!=null){
            for(String s: current.getSubtasks()){
                AddCardCtrl addCardCtrl = fxmlLoader.getController();
                addCardCtrl.displaySubs(s);
            }
        }
        Scene scene = new Scene(root);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Card Details");
        popUpStage.setScene(scene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }

    public void done(javafx.event.ActionEvent event, Card current) throws IOException {
        Button source = (Button) event.getSource();
        AnchorPane ap = (AnchorPane) source.getParent();
        TextField title = (TextField) ap.lookup("#taskTitle");
        current.setTitle(title.getText());
        TextArea desc =  (TextArea) ap.lookup("#taskDescription");
        current.setDescription(desc.getText());
        VBox subs = (VBox) ap.lookup("#subtaskVbox");
        for(Node hb: subs.getChildren()){
            CheckBox cb = (CheckBox) ((HBox) hb).getChildren().get(0);

            if(!current.getSubtasks().contains(cb.getText()))
                current.addSubTask(cb.getText());
        }
        server.addCard(current);
       // server.updateCardFromList(1L, 0L, current);
        Stage popup = (Stage) source.getScene().getWindow();
        popup.close();
        refreshList(1l, 0l);
    }

    public void refreshList(long boardId, long listid) throws IOException {
        Board b = server.getBoardById(boardId);
        hbox_lists.getChildren().remove((int) listid);
        hbox_lists.getChildren().add((int) listid, displayList(b.getLists().get((int) listid)));

    }

    public void delete(ActionEvent event, Node hbox, Card current){
        VBox par = (VBox) hbox.getParent();
        par.getChildren().remove(hbox);
        nodeCardMap.remove(hbox, current);
        //server.deleteCard(current.getId());
       // server.deleteCardFromList(1l, 0l, current);
        Button source = (Button) event.getSource();
        Stage popup = (Stage) source.getScene().getWindow();
        popup.close();
    }

    public void cancel(ActionEvent event, Node hboxCard){
        Button cancel = (Button) event.getSource();
        Stage popup = (Stage) cancel.getScene().getWindow();
        popup.close();
        TextField title = (TextField) cancel.getParent().lookup("#taskTitle");
        if(title.getText()==null){
            VBox list = (VBox) hboxCard.getParent();
            list.getChildren().remove(hboxCard);
            server.deleteCard(nodeCardMap.get(hboxCard).getId());
        }

    }



    public void deleteList() {

        hbox_lists.getChildren().remove(0);

    }

    // is this setup only for title (?)
    public void enterOnTextField() throws IOException {
        createNewList();
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


}

