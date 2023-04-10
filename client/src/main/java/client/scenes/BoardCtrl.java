package client.scenes;

import client.utils.LocalUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

import static client.utils.CustomizationUtils.customizationData;
import static client.utils.LocalUtils.writeCustomization;

public class BoardCtrl {
    private final SingleBoardCtrl singleBoardCtrl;
    private final LocalUtils localUtils;

    public BoardCtrl(SingleBoardCtrl singleBoardCtrl, LocalUtils localUtils) {
        this.singleBoardCtrl = singleBoardCtrl;
        this.localUtils = localUtils;
    }

    public void requestBoardName(TextField text) throws Exception {
        if (!text.getText().isEmpty() && !Objects.equals(text.getText().trim(), "")) {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            String name = text.getText().trim();
            singleBoardCtrl.current_board.setName(name);
            System.out.println("set, " + singleBoardCtrl.current_board + " to " + name);
            singleBoardCtrl.server.addBoard(singleBoardCtrl.current_board);
        } else {
            throw new Exception("board name cannot be empty");
        }
    }

    public void openBoardSettings(long BoardID,
                                  ConnectHomeCtrl connectHomeCtrl) throws IOException {

        System.out.println("running!" + singleBoardCtrl.server.getBoards());
        FXMLLoader loader = new FXMLLoader(
                singleBoardCtrl.getClass().getResource("customizationPage.fxml"));
        AnchorPane customization = loader.load();
        // doesn't actually delete anything just goes back to board overview
        Button deletebtn = (Button) customization.lookup("#deleteBoard");
        deletebtn.setOnAction(event -> {
            if (singleBoardCtrl.checkReadOnlyMode(singleBoardCtrl.isUnlocked)) {
                return;
            }
            // remove this specific board
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();

            // remove board with this id from Board Overview
            System.out.println("remove: " + BoardID);
            try {
                localUtils.remove(BoardID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.close();
            customizationData.remove(BoardID);
            writeCustomization();


            try {
                connectHomeCtrl.showBoardOverview();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        Scene scene = new Scene(customization);
        Stage popUpStage = new Stage();

        popUpStage.setTitle("Customization Details");
        popUpStage.setResizable(false);
        popUpStage.setScene(scene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);

        popUpStage.setOnCloseRequest(new EventHandler<>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Closed customization window!");
                writeCustomization();
            }
        });

        popUpStage.showAndWait();



    }
}
