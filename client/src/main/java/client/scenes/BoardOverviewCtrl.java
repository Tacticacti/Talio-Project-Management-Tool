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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static client.scenes.MainCtrl.primaryStage;
import static com.google.inject.Guice.createInjector;

public class BoardOverviewCtrl {
    private final ServerUtils server;
    private final LocalUtils localUtils;
    private final MainCtrl mainCtrl;

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private Set<Long> drawnBoards;

    private final int MAX_BOARDS_IN_ROW = 5;

    //private ArrayList board_scenes = new ArrayList<Board>();




    @FXML
    private VBox board_rows;

    @FXML
    private TextField search_box;

    @Inject
    public BoardOverviewCtrl(LocalUtils localUtils, ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.localUtils = localUtils;
        drawnBoards = new HashSet<>();
    }



    public void createBoard() throws IOException {

        var last_row = (HBox) board_rows.getChildren().get(board_rows.getChildren().size()-1);

        // add board to current hbox
        if (last_row.getChildren().size() < MAX_BOARDS_IN_ROW) {

            // create bord and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            AnchorPane board = loader.load();
            //HBox.setMargin(board, new Insets(20, 20, 20, 20));

            // enter clicked board

            Board new_board = server.addBoard(new Board());


            // add created board
            last_row.getChildren().add(board);

            //Board new_board = new Board();
            //System.out.println(new_board.getId());



            //Board new_board = server.addBoard(new Board());
            Button btn =  (Button) board.lookup("#enterButton");

            btn.setOnAction(event -> {
                try {
                    enterBoard(new_board);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            //.add(new_board);



        }
        // create a new row in vbox
        else {

            // create bord and style it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddedBoard.fxml"));
            Node board = loader.load();

            HBox hbox = new HBox();
            hbox.setSpacing(100);
            //hbox.setPadding(new Insets(102, 0, 0, 102));
            HBox.setMargin(board, new Insets(0, 0, 0, 187.5));

            Board new_board = server.addBoard(new Board());


            hbox.getChildren().add(board);



            // enter clicked board



            // add created board
            board_rows.getChildren().add(hbox);


           // Board new_board = server.addBoard(new Board());

            Button btn =  (Button) board.lookup("#enterButton");
            btn.setOnAction(event -> {
                try {
                    enterBoard(new_board);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });


        }

    }

    private void enterBoard(Board new_board) throws IOException {

        System.out.println(server.getBoards());

        primaryStage.setTitle("Board");

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("SingleBoard.fxml"));
        //loader.setController();
        var singleBoardPair = FXML.load(SingleBoardCtrl.class, new_board,
                "client", "scenes", "SingleBoard.fxml");

        //var singleBoardCtrl = new SingleBoardCtrl(server, mainCtrl);
        //singleBoardCtrl.setBoard(new_board);


        //loader.setController(singleBoardCtrl);


        var singleBoard = singleBoardPair.getValue();

        //singleBoard.getRoot().setController(singleBoardCtrl);



        Scene new_scene = new Scene(singleBoard);
        TextField board_name = (TextField) new_scene.lookup("#board_name");
        System.out.println("board name" + new_board.getName());
        board_name.setText(new_board.getName());
        System.out.println(new_board.getName());


            //primaryStage.setScene(new Scene(loaded_board.getValue()));
            // build board from board

        primaryStage.setScene(new_scene);

        System.out.println(new_scene);
        //System.out.println();


    }


    public void onJoinBoard() throws IOException {
        String text = search_box.getText();

        // debug
        System.out.println(server.getBoards());

        for (Board board : server.getBoards()) {
            if (board.getId().toString().equals(text)) {
                addJoinedBoard(board);
                localUtils.add(board.getId());
            }
        }
    }

    public void correctText(Node board, Board board2) {
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

            correctText(board, board2);

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

            correctText(board, board2);

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
        try {
            localUtils.fetch();
        }
        catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Error fetching boards from file!\n" + e.getMessage());
            alert.showAndWait();
        }
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
}
