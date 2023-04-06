package client.scenes;

import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;

import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class SecurityCtrl {
    private final SingleBoardCtrl singleBoardCtrl;
    private final BoardOverviewCtrl boardOverviewCtrl;

    public SecurityCtrl(SingleBoardCtrl singleBoardCtrl, BoardOverviewCtrl boardOverviewCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
        this.boardOverviewCtrl = boardOverviewCtrl;
    }

    public void requestPasswordChange() {
        String currentPassword, newPassword, confirmPassword;
        // If the board has a password and is unlocked
        if (singleBoardCtrl.current_board.getPassword() != null && singleBoardCtrl.isUnlocked) {
            currentPassword = promptForPassword("Enter Current Password", "Current Password:");
            if (currentPassword == null) {
                return;
            }
            if(!singleBoardCtrl.server.verifyBoardPassword(
                    singleBoardCtrl.current_board.getId(), currentPassword)) {
                showAlert(Alert.AlertType.ERROR,
                        "Incorrect Password", "The current password entered is incorrect.");
                return;
            }
        }

        // If the board has a password and is locked
        if (singleBoardCtrl.current_board.getPassword() != null
                && !singleBoardCtrl.isUnlocked) {
            currentPassword =
                    promptForPassword("Unlock Board", "Enter password to unlock the board:");
            if (currentPassword == null) {
                return;
            }
            if(!singleBoardCtrl.server.verifyBoardPassword(
                    singleBoardCtrl.current_board.getId(), currentPassword)) {
                showAlert(Alert.AlertType.ERROR,
                        "Incorrect Password", "The password entered is incorrect.");
                return;
            }
            setIsUnlocked(true);
            try {
                boardOverviewCtrl.addJoinedBoard(singleBoardCtrl.current_board);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                boardOverviewCtrl.localUtils.add(singleBoardCtrl.current_board.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            showAlert(Alert.AlertType.INFORMATION,
                    "Board Unlocked", "You successfully joined the board!");
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
        if (Objects.equals("", newPassword)) {
            singleBoardCtrl.server.removeBoardPassword(singleBoardCtrl.current_board.getId());
            showAlert(Alert.AlertType.INFORMATION,
                    "Password removed", "The board password has been deleted.");
            singleBoardCtrl.refresh();
            updatePasswordButtonImage();
        }
        else {
            singleBoardCtrl.server.setBoardPassword(
                    singleBoardCtrl.current_board.getId(), newPassword);
            showAlert(Alert.AlertType.INFORMATION,
                    "Password Updated", "The board password has been updated.");
            singleBoardCtrl.refresh();
            updatePasswordButtonImage();
        }
    }

    String promptForPassword(String title, String contentText) {
        Dialog<String> dialog = new Dialog<>();
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

    public void setIsUnlocked(boolean newIsUnlocked) {
        singleBoardCtrl.isUnlocked = newIsUnlocked;
    }

    void updatePasswordButtonImage() {
        if (singleBoardCtrl.current_board.getPassword() == null) {
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

    boolean checkReadOnlyMode(boolean isUnlocked) {
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
}
