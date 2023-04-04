package client.scenes;

import commons.BoardList;
import commons.Tag;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Optional;

public class TagCtrl {
    private final SingleBoardCtrl singleBoardCtrl;

    public TagCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public Optional<String> enterTagName() {
        TextInputDialog tagInput = new TextInputDialog();
        tagInput.setTitle("Tag name");
        tagInput.setHeaderText("Create new tag");
        tagInput.setContentText("Enter tag");
        Optional<String> result = tagInput.showAndWait();
        if (result.isPresent() && result.get().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Tag cannot be empty!", ButtonType.OK);
            alert.showAndWait();
            return singleBoardCtrl.cardCtrl.showTitleDialog();
        }
        return result;
    }

    public void addNewTag(HBox parent) {
        Optional<String> tagTitle = enterTagName();
        if (tagTitle.isPresent()) {
            Tag newTag = new Tag(tagTitle.get());
            placeTag(parent, newTag);
            singleBoardCtrl.server.addTagToBoard(singleBoardCtrl.BoardID, newTag);
        }
    }

    public void setUpNewTag(BoardList boardList)
            throws IOException {
        if (singleBoardCtrl.checkReadOnlyMode(
                singleBoardCtrl.current_board, singleBoardCtrl.isUnlocked)) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(
                singleBoardCtrl.getClass().getResource("SingleBoard.fxml"));
        AnchorPane list = loader.load();

        Button newTagBtn = (Button) list.lookup("#newTagBtn");
        HBox parentList = (HBox) newTagBtn.getParent();
        parentList.setUserData(boardList);
        parentList.setSpacing(5);
        for (Tag tag : boardList.getTags()) {
            placeTag(parentList, tag);
        }
    }

    public void placeTag(HBox parent, Tag tag) {
        String tagTitle = tag.getTitle();
    }
}
