package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardList;
import commons.Card;

import client.utils.ServerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.net.URL;
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
    }

    public void back(){
        mainCtrl.showBoardOverview();
    }

    public void card(){
        mainCtrl.showAddCard();
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

    public void addCard(VBox parent){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        CardGUICtrl controller = new CardGUICtrl(server, mainCtrl);
        fxmlLoader.setController(controller);
        try {
            Node card = (Node) fxmlLoader.load();
            Button detailButton = (Button) card.lookup("#details");
            detailButton.setOnAction(event -> mainCtrl.showAddCard());
            int index =parent.getChildren().size()-2;
            if(parent.getChildren().size()<2){
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
        } catch (IOException e) {
            throw new RuntimeException(e);
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
