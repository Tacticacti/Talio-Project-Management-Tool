package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.io.IOException;

public class AdminLoginCtrl {
    @FXML
    private TextField psswdField;

    @FXML
    private Button loginBtn;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public AdminLoginCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void login() {
        String psswd = "";
        boolean res = false;
        try {
            psswd = psswdField.getText();
            res = server.checkAdminPassword(psswd);
        }
        catch(Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Error logging in: " + e.getMessage());
            alert.showAndWait();
        }

        if(res) {
            // System.out.println("psswd ok");
            server.setAdminPassword(psswd);
            mainCtrl.showDashboard();
        }
        else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Wrong password!");
            alert.showAndWait();
        }
    }

    public void back() throws IOException {
        ConnectHomeCtrl connectHomeCtrl = new ConnectHomeCtrl(server, mainCtrl);
        connectHomeCtrl.showBoardOverview();
        //mainCtrl.showBoardOverview();
    }
}
