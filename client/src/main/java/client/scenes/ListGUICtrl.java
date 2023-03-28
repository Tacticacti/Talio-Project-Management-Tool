package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListGUICtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button addNewCardButton;

    @FXML
    private VBox CardsList;

    @Inject
    public ListGUICtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addNewCardButton.setOnAction(event -> {
            try {
                addCard();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addCard() throws IOException {
        FXMLLoader card = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        Node cardadded = card.load();
        CardsList.getChildren().add(CardsList.getChildren().size()-2, cardadded);
    }
}
