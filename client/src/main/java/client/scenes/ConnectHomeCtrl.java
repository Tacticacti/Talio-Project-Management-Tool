package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.scene.input.KeyEvent;

import com.google.inject.Inject;

public class ConnectHomeCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField serverAddress;

    @Inject
    public ConnectHomeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void connect(){
        boolean ok = false;
        boolean exception = false;
        String addr = "";
        try {
            addr = serverAddress.getText();
            addr = addr.trim();
            ok = server.check(addr);
        }
        catch(Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("No talio instant present on: " + addr);
            alert.showAndWait();
            exception = true;
            return;
        }
        if(!ok && !exception) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Invalid url: " + addr);
            alert.showAndWait();
            return;
        }
        server.setServer(addr);
        mainCtrl.showBoardOverview();
    }

    public void keyPressed(KeyEvent e) {
        switch(e.getCode()) {
            case ENTER:
                connect();
                break;
        }
    }
}
