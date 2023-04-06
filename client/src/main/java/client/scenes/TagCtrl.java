package client.scenes;

import commons.BoardList;
import commons.Tag;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class TagCtrl {
    private final SingleBoardCtrl singleBoardCtrl;

    public TagCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public void enterTagName() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TagPopUp.fxml"));
        AnchorPane addTag;
        try {
            addTag = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TextField textField = (TextField) addTag.lookup("#tagName");
        ColorPicker tagColor = (ColorPicker) addTag.lookup("#tagColor");
        Button add = (Button) addTag.lookup("#addTag");
        add.setOnAction(event -> {
            addNewTag(singleBoardCtrl.tagHbox,textField,tagColor);
        });
        Scene tagScene = new Scene(addTag);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Edit tag details");
        popUpStage.setScene(tagScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }

    public void addNewTag(HBox parent, TextField textField, ColorPicker colorPicker) {
        Tag newTag = new Tag();
        if(textField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No empty tag.");
            alert.setContentText("Tag name cannot be empty");
            alert.showAndWait();
        }
        newTag.setTitle(textField.getText());
        newTag.setColor(colorPicker.getValue().toString());

        singleBoardCtrl.server.addTagToBoard(singleBoardCtrl.BoardID, newTag);
        Stage popUp = (Stage) textField.getScene().getWindow();
        popUp.close();

    }


    public void placeTag(HBox parent, Tag tag) {
//        HBox tagBox = new HBox();
//        Label tagTitle = new Label(tag.getTitle());
//
//        Button deleteBtn = new Button();
//        deleteBtn.setPrefWidth(5);
//        deleteBtn.setPrefHeight(5);
//        deleteBtn.setOnAction(event -> {
//            deleteTag(tag);
//        });
//
//        ImageView imageView = new ImageView(getClass()
//                .getResource("../images/trash.png").toExternalForm());
//        imageView.setFitWidth(deleteBtn.getPrefWidth());
//        imageView.setFitHeight(deleteBtn.getPrefHeight());
//        imageView.setPreserveRatio(true);
//        deleteBtn.setGraphic(imageView);
//
////        HBox.setHgrow(deleteBtn, Priority.ALWAYS);
////        HBox.setHgrow(tagTitle,Priority.ALWAYS);
//
//        tagBox.getChildren().add(tagTitle);
//        tagBox.getChildren().add(deleteBtn);
//        tagBox.setAlignment(Pos.BASELINE_CENTER);
//
//
//
//
////        VBox.setVgrow(tagBox, Priority.ALWAYS);
//        VBox.setMargin(tagBox, new Insets(5, 0, 5, 0));
        parent.setSpacing(10);
        FXMLLoader tagL = new FXMLLoader(getClass().getResource("Tag.fxml"));
        AnchorPane tagBox;
        try {
            tagBox = tagL.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Label title = (Label) tagBox.lookup("#tagName");
        title.setText(tag.getTitle());
        Button deleteBtn = (Button) tagBox.lookup("#delBtn");
        deleteBtn.setOnAction(event -> {
            deleteTag(tag);
        });
        deleteBtn.setText("\u2716");
        VBox setTags = new VBox();
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(tag.getColor())
                , null, null);
        Background background = new Background(backgroundFill);
        tagBox.setBackground(background);

        VBox.setMargin(setTags,new Insets(0,5,0,5));
        setTags.setSpacing(15);

        tagBox.setPrefHeight(setTags.getPrefHeight()/2);

        setTags.setPrefHeight(parent.getHeight());
        setTags.setPrefWidth(tagBox.getWidth());

        if(parent.getChildren().size() == 0){
            setTags.getChildren().add(tagBox);
            parent.getChildren().add(setTags);
        }else {
            VBox previous = (VBox) parent.getChildren().get(parent.getChildren().size()-1);
            if(previous.getChildren().size()==2){
                setTags.getChildren().add(tagBox);
                parent.getChildren().add(setTags);
            }else{
                previous.getChildren().add(tagBox);
            }
        }

    }
    public void deleteTag(Tag tag){
        singleBoardCtrl.server.deleteTagToBoard(singleBoardCtrl.BoardID, tag);
        singleBoardCtrl.refresh();
    }
}
