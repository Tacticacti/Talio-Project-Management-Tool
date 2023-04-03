package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @FXML
    private TableView<Board> boards;

    @FXML
    private TableColumn<Board, String> boardKey;

    @FXML
    private TableColumn<Board, String> boardName;

    @FXML
    private TableColumn<Board, Button> delete;

    private ObservableList<Board> boardsList;

    @Inject
    public DashboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    @SuppressWarnings("checkstyle:Indentation")
    public void initialize(URL location, ResourceBundle resources) {
        boardKey.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getId().toString())
        );
        boardName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName())
        );

        Callback<TableColumn<Board, Button>, TableCell<Board, Button>> cellFactory =
                new Callback<>() {
            public TableCell<Board, Button> call(TableColumn<Board, Button> param) {
                final TableCell<Board, Button> cell = new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(e -> {
                            Board board = getTableView().getItems().get(getIndex());
                            server.deleteBoardById(board.getId());
                        });
                    }

                    @Override
                    protected void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setGraphic(deleteButton);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        };
        delete.setCellFactory(cellFactory);
    }

    public void refresh() {
        boardsList = FXCollections.observableArrayList(server.getBoards());
        boards.setItems(boardsList);
    }

    public void back() {
        mainCtrl.showBoardOverview();
    }
}
