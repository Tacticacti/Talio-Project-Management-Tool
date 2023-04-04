package client.scenes;

import commons.Board;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class BoardCtrl {
    private final SingleBoardCtrl singleBoardCtrl;

    public BoardCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public void requestBoardName(TextField text, Long id) throws Exception {
        if (!text.getText().isEmpty() && !Objects.equals(text.getText().trim(), "")) {
            if (checkReadOnlyMode(singleBoardCtrl.current_board, singleBoardCtrl.isUnlocked)) {
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

    public void requestPasswordChange() {
        String currentPassword, newPassword, confirmPassword;
        // If the board has a password and is unlocked
        if (singleBoardCtrl.current_board.getPassword() != null
                && !singleBoardCtrl.current_board.getPassword().isEmpty()
                && singleBoardCtrl.isUnlocked) {
            currentPassword = promptForPassword("Enter Current Password", "Current Password:");
            if (currentPassword == null) {
                return;
            }
            if (!singleBoardCtrl.current_board.getPassword().equals(currentPassword)) {
                showAlert(Alert.AlertType.ERROR,
                        "Incorrect Password", "The current password entered is incorrect.");
                return;
            }
        }
        // If the board has a password and is locked
        if (singleBoardCtrl.current_board.getPassword() != null
                && !singleBoardCtrl.current_board.getPassword().isEmpty()
                && !singleBoardCtrl.isUnlocked) {
            currentPassword =
                    promptForPassword("Unlock Board", "Enter password to unlock the board:");
            if (currentPassword == null) {
                return;
            }
            if (!singleBoardCtrl.current_board.getPassword().equals(currentPassword)) {
                showAlert(Alert.AlertType.ERROR,
                        "Incorrect Password", "The password entered is incorrect.");
                return;
            }
            setIsUnlocked(true);
            showAlert(Alert.AlertType.INFORMATION, "Board Unlocked", "The board is now unlocked.");
            return;
        }
        newPassword = promptForPassword("Enter New Password", "New Password:");
        if (newPassword == null) {
            return;
        }
        confirmPassword = promptForPassword("Confirm New Password", "Confirm New Password:");
        if (confirmPassword == null) {
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR,
                    "Password Mismatch", "The new password and confirmation do not match.");
            return;
        }
        singleBoardCtrl.current_board.setPassword(newPassword);
        singleBoardCtrl.server.addBoard(singleBoardCtrl.current_board);
        showAlert(Alert.AlertType.INFORMATION,
                "Password Updated", "The board password has been updated.");
        updatePasswordButtonImage();
    }

    String promptForPassword(String title, String contentText) {
        Dialog<String> dialog = new Dialog<String>();
        dialog.setTitle(title);
        Label promptLabel = new Label(contentText);
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
        return dialog.showAndWait().orElse(null);
    }

    void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public boolean setIsUnlocked(boolean newIsUnlocked) {
        singleBoardCtrl.isUnlocked = newIsUnlocked;
        return singleBoardCtrl.isUnlocked;
    }

    void updatePasswordButtonImage() {
        if (singleBoardCtrl.current_board.getPassword() == null
                || singleBoardCtrl.current_board.getPassword().isEmpty()) {
            ImageView imageUnlocked = new ImageView(singleBoardCtrl.getClass()
                    .getResource("../images/unlocked.png")
                    .toExternalForm());
            imageUnlocked.setFitWidth(singleBoardCtrl.passwordBtn.getPrefWidth());
            imageUnlocked.setFitHeight(singleBoardCtrl.passwordBtn.getPrefHeight());
            imageUnlocked.setPreserveRatio(true);
            singleBoardCtrl.passwordBtn.setGraphic(imageUnlocked);
        } else {
            ImageView imageLocked = new ImageView(singleBoardCtrl.getClass()
                    .getResource("../images/locked.png")
                    .toExternalForm());
            imageLocked.setFitWidth(singleBoardCtrl.passwordBtn.getPrefWidth());
            imageLocked.setFitHeight(singleBoardCtrl.passwordBtn.getPrefHeight());
            imageLocked.setPreserveRatio(true);
            singleBoardCtrl.passwordBtn.setGraphic(imageLocked);
        }
    }

    boolean checkReadOnlyMode(Board board, boolean isUnlocked) {
        if (!isUnlocked) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Read-only Mode");
            alert.setHeaderText(null);
            alert.setContentText("This board is locked. You can only view it in read-only mode.");
            alert.showAndWait();
            return true;
        } else {
            return false;
        }
    }

    public void openBoardSettings() throws IOException {
        System.out.println("running!" + singleBoardCtrl.server.getBoards());
        FXMLLoader loader = new FXMLLoader(
                singleBoardCtrl.getClass().getResource("customizationPage.fxml"));
        AnchorPane customization = loader.load();
        // doesn't actually delete anything just goes back to board overview
        Button delbtn = (Button) customization.lookup("#deleteBoard");
        delbtn.setOnAction(event -> {
            if (checkReadOnlyMode(singleBoardCtrl.current_board, singleBoardCtrl.isUnlocked)) {
                return;
            }
            // remove this specific board
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
            //mainCtrl.showBoardOverview();
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
