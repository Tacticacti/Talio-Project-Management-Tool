package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardList;
import commons.Card;

import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


import java.net.URL;

import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.HashMap;

public class SingleBoardCtrl implements Initializable {
    final ListCtrl listCtrl = new ListCtrl(this);
    final CardCtrl cardCtrl = new CardCtrl(this);
    final TagCtrl tagCtrl = new TagCtrl(this);
    final BoardCtrl boardCtrl = new BoardCtrl(this);
    final SecurityCtrl securityCtrl = new SecurityCtrl(this);
    ServerUtils server;
    final BoardOverviewCtrl boardOverviewCtrl;
    final MainCtrl mainCtrl;
    Long BoardID = 1L;
    Node newCardBtn;

    @FXML
    HBox tagHbox;
    @FXML
    Button newTagBtn;
    @FXML
    HBox hbox_lists;

    @FXML
    Button passwordBtn;
    @FXML
    Button settingsBtn;
    ObservableList<BoardList> lists;
    Board current_board;
    @FXML
    TextField board_name;
    Map<Node, Card> nodeCardMap;
    ClipboardContent content;
    static Dragboard dragboard;
    @FXML
    Button backBtn;
    @FXML
    Button newListBtn;
    @FXML
    Button refreshBtn;
    @FXML
    Button copyInvite;
    boolean isUnlocked;

    @Inject
    public SingleBoardCtrl(
            ServerUtils server,
            BoardOverviewCtrl boardOverviewCtrl,
            MainCtrl mainCtrl,
            Boolean isUnlocked) {
        this.boardOverviewCtrl = boardOverviewCtrl;
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.isUnlocked = isUnlocked;
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
        securityCtrl.updatePasswordButtonImage();

        newCardBtn = hbox_lists.getChildren().get(0);
        copyInvite.setOnAction(e-> copyInvite());
        passwordBtn.setOnAction(e -> securityCtrl.requestPasswordChange());
        settingsBtn.setOnAction(e->{
            try {
                if (securityCtrl.checkReadOnlyMode(isUnlocked)) {
                    return;
                }
                boardCtrl.openBoardSettings();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        refreshBtn.setOnAction(e-> refresh());
        newListBtn.setOnAction(e->{
            if (securityCtrl.checkReadOnlyMode(isUnlocked)) {
                return;
            }
            listCtrl.createNewList();
        });
        backBtn.setOnAction(e-> back());
        nodeCardMap = new HashMap<>();
        current_board = new Board();
        board_name.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                try {
                    boardCtrl.requestBoardName(board_name, BoardID);
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
        newTagBtn.setOnAction(event ->{
            if (securityCtrl.checkReadOnlyMode(isUnlocked)) {
                return;
            }
            tagCtrl.addNewTag(tagHbox);
        });
        refresh();
        System.out.println(server);
        server.checkForUpdatesToRefresh("/topic/lists", BoardList.class, boardList->{
            Platform.runLater(this::refresh);
        });
        server.checkForUpdatesToRefresh("/topic/boards", Board.class, board->{
            Platform.runLater(this::refresh);
        });
    }

    public void back(){
        ConnectHomeCtrl connectHomeCtrl = new ConnectHomeCtrl(server, mainCtrl);
        connectHomeCtrl.showBoardOverview();
    }

    boolean checkReadOnlyMode(boolean isUnlocked) {
        return securityCtrl.checkReadOnlyMode(isUnlocked);
    }

    public void placeCard(VBox parent, Card card){
        cardCtrl.placeCard(parent, card);
    }

    public void addNewCard(VBox parent){
        cardCtrl.addNewCard(parent);
    }

    static void alertError(WebApplicationException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public void updateCardFromList(Long boardId, Long listId, Card current){
        try{
            server.updateCardFromList(listId, current);
        }catch(WebApplicationException e){
            alertError(e);
        }
    }

    public void saveCardToList(Long boardId, Long listId, Card current){
        try{
            server.addCardToList(listId, current);
        }catch(WebApplicationException e){
            alertError(e);
        }
    }

    public void deleteCardFromList(Long boardId, Long listIdIndex, Card current){
        try{
            server.deleteCardFromList(listIdIndex, current);
        }catch(WebApplicationException e){
            alertError(e);
        }
    }

    public void setDelete(ActionEvent event, Node hbox, Card current, long listId){
        cardCtrl.setDelete(event, hbox, current, listId);
    }

    public void setCancel(ActionEvent event, Node hboxCard){
        cardCtrl.setCancel(event, hboxCard);
    }

    public void setBoard(Board board) {
        this.current_board = board;
        this.BoardID = board.getId();
    }

    public String pullLists(Long id) {
        current_board = server.getBoardById(id);
        lists = FXCollections.observableList(current_board.getLists());
        return current_board.getName();
    }

    public void drawLists() throws IOException {
        ObservableList<Node> board_lists = hbox_lists.getChildren();
        board_lists.clear();
        for(BoardList boardList : lists) {
            listCtrl.wrapList(boardList, board_lists);
        }
        board_lists.add(newCardBtn);
    }

    public void refresh() {
        String name = pullLists(BoardID);
        if(!name.equals(board_name.getText()))
            board_name.setText(name);
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

    public void setServer(ServerUtils server){
        this.server = server;
    }

    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }

    public Map<Node, Card> getNodeCardMap() {
        return nodeCardMap;
    }

    public Board getCurrent_board() {
        return current_board;
    }

    public HBox getHbox_lists() {
        return hbox_lists;
    }
}
