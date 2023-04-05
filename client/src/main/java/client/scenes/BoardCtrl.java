package client.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static client.scenes.BoardOverviewCtrl.localUtils;
import static client.scenes.MainCtrl.boverview;
import static client.scenes.MainCtrl.primaryStage;

public class BoardCtrl {
    private final SingleBoardCtrl singleBoardCtrl;

    public BoardCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public void requestBoardName(TextField text, Long id) throws Exception {
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

    public void openBoardSettings(long BoardID, ConnectHomeCtrl connectHomeCtrl) throws IOException {
        System.out.println("running!" + singleBoardCtrl.server.getBoards());
        FXMLLoader loader = new FXMLLoader(
                singleBoardCtrl.getClass().getResource("customizationPage.fxml"));
        AnchorPane customization = loader.load();
        // doesn't actually delete anything just goes back to board overview
        Button delbtn = (Button) customization.lookup("#deleteBoard");
        delbtn.setOnAction(event -> {
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

            connectHomeCtrl.showBoardOverview();

        });
        Scene scene = new Scene(customization);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Customization Details");
        popUpStage.setResizable(false);
        popUpStage.setScene(scene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }
}
