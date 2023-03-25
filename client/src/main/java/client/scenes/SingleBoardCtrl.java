package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardList;
import commons.Card;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

    private ClipboardContent content;

    private Dragboard board;
    private AnchorPane card;

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
            .getResource("../images/settings_icon.png")
            .toExternalForm());
        imageView.setFitWidth(settingsBtn.getPrefWidth());
        imageView.setFitHeight(settingsBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        settingsBtn.setGraphic(imageView);
        try {
            createNewList();
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

    public void card(){
        mainCtrl.showAddCard();
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
        btn2.setOnAction(event ->{
            VBox par = (VBox) btn2.getParent();
            addCard(par);
        });

        board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();


    }






    public void addCard(VBox parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        CardGUICtrl controller = new CardGUICtrl(server, mainCtrl);
        fxmlLoader.setController(controller);
        try {
            Node cardNode = fxmlLoader.load();
            Button detailsButton = (Button) cardNode.lookup("#details");
            detailsButton.setOnAction(event -> mainCtrl.showAddCard());
            int insertIndex = parent.getChildren().size() - 2;
            if (parent.getChildren().size() < 2) {
                insertIndex = 0;
            }
            cardNode.setOnDragDetected(event -> {
                Dragboard dragboard = cardNode.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(cardNode.getId());
                dragboard.setContent(content);
                dragboard.setDragView(cardNode.snapshot(null, null));
                event.consume();
            });

            cardNode.setOnDragOver(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cardNode.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                boolean success = false;
                if (dragboard.hasString()) {
                    String cardId = dragboard.getString();
                    Node draggedCardNode = parent.lookup("#" + cardId);
                    if (draggedCardNode != null) {
                        VBox draggedCardParent = (VBox) draggedCardNode.getParent();
                        if (draggedCardParent != parent) {
                            draggedCardParent.getChildren().remove(draggedCardNode);
                            int dropIndex = parent.getChildren().size() - 1;
                            parent.getChildren().add(dropIndex, draggedCardNode);
                            success = true;
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            System.out.println("Insert Index: " + insertIndex + ", Card Node: " + cardNode);
            parent.getChildren().add(insertIndex, cardNode);
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
}
