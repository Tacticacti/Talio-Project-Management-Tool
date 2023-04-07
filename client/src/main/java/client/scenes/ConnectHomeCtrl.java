package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.scene.input.KeyEvent;

import com.google.inject.Inject;

import java.io.IOException;

import static client.scenes.MainCtrl.primaryStage;

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

    public void connect() throws IOException {
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
        // mainCtrl.showBoardOverview();
        showBoardOverview();
    }

    public void connectDefault() throws IOException {
        boolean ok = false;
        boolean exception = false;
        String addr = "localhost:8080";
        try {
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
        showBoardOverview();
    }

    public void showBoardOverview() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BoardOverview.fxml"));
        BoardOverviewCtrl boardOverviewCtrl = new BoardOverviewCtrl(server, mainCtrl);
        loader.setController(boardOverviewCtrl);
        AnchorPane overview;
        try {
            overview = (AnchorPane) loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene boverview = new Scene(overview);
        boverview.setOnKeyPressed(mainCtrl::showHelpPage);
        primaryStage.setTitle("Board overview");
        primaryStage.setScene(boverview);
        primaryStage.setOnCloseRequest(e->{
            server.disconnect();
            server.stopExec();
        });
        boardOverviewCtrl.refresh();
    }

    public void keyPressed(KeyEvent e) throws IOException {
        switch(e.getCode()) {
            case ENTER:
                connect();
                break;
        }
    }
}
