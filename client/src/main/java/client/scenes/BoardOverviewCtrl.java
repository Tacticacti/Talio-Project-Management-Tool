package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.LocalUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.Board;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

import static client.scenes.MainCtrl.primaryStage;
import static com.google.inject.Guice.createInjector;

public class BoardOverviewCtrl implements Initializable {
    private ServerUtils server;
    private LocalUtils localUtils;
    private final MainCtrl mainCtrl;

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private Set<Long> drawnBoards;
    static Set<Node> boardsNodes;

    @FXML
    private Button createBoard;

    @FXML
    private Button enter;

    @FXML
    private Button disconnect;

    private final int MAX_BOARDS_IN_ROW = 5;

    //private ArrayList board_scenes = new ArrayList<Board>();




    @FXML
    private VBox board_rows;

    @FXML
    private TextField search_box;

    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        drawnBoards = new HashSet<>();
        boardsNodes = new HashSet<>();
    }

    public void createBoard() throws IOException {

        var last_row = (HBox) board_rows.getChildren().get(board_rows.getChildren().size()-1);

        // add board to current hbox
        if (last_row.getChildren().size() < MAX_BOARDS_IN_ROW) {

            // load board and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            AnchorPane board = loader.load();
            last_row.getChildren().add(board);

            Board new_board = server.addBoard(new Board());

            // enter board when you click it
            Button btn =  (Button) board.lookup("#enterButton");
            btn.setOnAction(event -> {
                try {
                    addJoinedBoard(new_board);
                    localUtils.add(new_board.getId());
                    enterBoard(new_board);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // enter board after creating it by default
            addJoinedBoard(new_board);
            localUtils.add(new_board.getId());
            enterBoard(new_board);
        }
        // create a new row in vbox
        else {

            // load board and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            Node board = loader.load();

            // add new row to vbox and style it to be consistent
            HBox hbox = new HBox();
            hbox.setSpacing(100);
            HBox.setMargin(board, new Insets(0, 0, 0, 187.5));

            Board new_board = server.addBoard(new Board());

            // add it to board overview
            hbox.getChildren().add(board);
            board_rows.getChildren().add(hbox);

            // enter board when you click it
            Button btn =  (Button) board.lookup("#enterButton");
            btn.setOnAction(event -> {
                try {
                    addJoinedBoard(new_board);
                    localUtils.add(new_board.getId());
                    enterBoard(new_board);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // enter board after creating it by default
            addJoinedBoard(new_board);
            localUtils.add(new_board.getId());
            enterBoard(new_board);
        }

    }

    private void enterBoard(Board new_board) throws IOException {
        System.out.println(server.getBoards());

        primaryStage.setTitle("Board");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SingleBoard.fxml"));

        // Check if the board is already joined
        boolean isUnlocked = drawnBoards.contains(new_board.getId());

        SingleBoardCtrl singleBoardCtrl = new SingleBoardCtrl(server, mainCtrl, isUnlocked);
        singleBoardCtrl.setBoard(new_board);
        loader.setController(singleBoardCtrl);

        var singleBoard = (Parent) loader.load();

        Scene new_scene = new Scene(singleBoard);
        TextField board_name = (TextField) new_scene.lookup("#board_name");
        System.out.println("board name" + new_board.getName());
        board_name.setText(new_board.getName());
        System.out.println(new_board.getName());

        primaryStage.setScene(new_scene);

        System.out.println(new_scene);
    }


    public void onJoinBoard() throws IOException {
        String text = search_box.getText();
        Boolean boardFound = false;

        // debug
        System.out.println(server.getBoards());

        for (Board board : server.getBoards()) {
            if (board.getId().toString().equals(text) &&
                    !drawnBoards.contains(board.getId())) {
                boardFound = true;
                // check if board is password protected
                if (board.getPassword() != null) {
                    // prompt user for password
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Enter Board Password");
                    dialog.setHeaderText("This board is password protected.");

                    Label promptLabel = new Label("Please enter the password," +
                            " or leave empty for Read-Only mode:");
                    PasswordField passwordField = new PasswordField();
                    passwordField.setPromptText("Password");

                    VBox vbox = new VBox();
                    vbox.getChildren().addAll(promptLabel, passwordField);
                    vbox.setSpacing(10);

                    dialog.getDialogPane().setContent(vbox);

                    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

                    dialog.setResultConverter(button ->
                            button == okButtonType ? passwordField.getText() : null);

                    Optional<String> result = dialog.showAndWait();


                    if (result.isPresent() && result.get().equals(board.getPassword())) {
                        addJoinedBoard(board);
                        localUtils.add(board.getId());
                        enterBoard(board);
                    } else if (result.isPresent()
                            && !result.get().isEmpty()
                            && !result.get().equals(board.getPassword())) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setHeaderText("Error joining board!");
                        alert.setContentText("Invalid password for password-protected board.");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setHeaderText("Read-Only Mode");
                        alert.setContentText("You are now entering the board in Read-Only mode.");
                        alert.showAndWait();

                        enterBoard(board);
                    }
                } else {
                    addJoinedBoard(board);
                    localUtils.add(board.getId());
                    enterBoard(board);
                }
                System.out.println("hello!");
            }
        }

        // show error message if board not found
        if (!boardFound) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setHeaderText("Error joining board!");
            alert.setContentText("Invalid board ID or board is already joined.");
            alert.showAndWait();
        }
    }

    public void correctText(Node board) {
        Board board2 = (Board) board.getUserData();
        BorderPane tmp = (BorderPane) board.lookup("#mainPane");

        Text id = (Text) tmp.lookup("#boardId");
        id.setText(board2.getId().toString());

        Text name = (Text) tmp.lookup("#boardName");
        name.setText(board2.getName());
    }

    public void addJoinedBoard(Board board2) throws IOException {


        var last_row = (HBox) board_rows.getChildren().get(board_rows.getChildren().size()-1);

        drawnBoards.add(board2.getId());

        // add board to current hbox
        if (last_row.getChildren().size() < MAX_BOARDS_IN_ROW) {
            // create bord and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            AnchorPane board = loader.load();

            board.setUserData(board2);
            boardsNodes.add(board);
            correctText(board);

            last_row.getChildren().add(board);


            //Board new_board = server.addBoard(new Board());
            Button btn =  (Button) board.lookup("#enterButton");

            btn.setOnAction(event -> {
                try {
                    enterBoard(board2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });


        }
        // create a new row in vbox
        else {

            // create bord and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            Node board = loader.load();

            board.setUserData(board2);
            boardsNodes.add(board);
            correctText(board);

            HBox hbox = new HBox();
            hbox.setSpacing(100);
            //hbox.setPadding(new Insets(102, 0, 0, 102));
            HBox.setMargin(board, new Insets(0, 0, 0, 187.5));

            hbox.getChildren().add(board);

            // add created board
            board_rows.getChildren().add(hbox);


            Button btn =  (Button) board.lookup("#enterButton");
            btn.setOnAction(event -> {
                try {
                    enterBoard(board2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }

    }

    public void refresh() {
        if(localUtils == null)
            localUtils = new LocalUtils();
        if(!Objects.equals(localUtils.getPath(), server.getPath())) {
            try {
                localUtils.setPath(server.getPath());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        try {
            localUtils.fetch();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Error fetching boards from file!\n" + e.getMessage());
            alert.showAndWait();
        }
        boardsNodes.forEach(x -> {
            correctText(x);
        });
        localUtils.getBoards().forEach(x -> {
            if(drawnBoards.contains(x))
                return;
            try {
                addJoinedBoard(server.getBoardById(x));
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Error getting board id: " + x +
                        " from server\n" + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    public void disconnect() {
        mainCtrl.showHome();
        server.disconnect();
    }

    public void resetFile() {
        try {
            localUtils.reset();
        }
        catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
        refresh();
    }
    public void setServer(ServerUtils server){
        this.server = server;
    }
    @FXML
    private Button reset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disconnect.setOnAction(e->{
            disconnect();
        });
        createBoard.setOnAction(e->{
            try {
                createBoard();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        enter.setOnAction(e->{
            try {
                onJoinBoard();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        reset.setOnAction(e->{
            resetFile();
        });
        search_box.setOnAction(e->{
            try {
                onJoinBoard();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        server.checkForUpdatesToRefresh("/topic/boards", Board.class, board->{
            refresh();
        });
    }
}
