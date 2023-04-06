package client.scenes;

import commons.BoardList;
import commons.Card;
import commons.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TagCtrl {
    private final SingleBoardCtrl singleBoardCtrl;

    public TagCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public void enterTagName() {
        loadTagPopUp();
    }

    public void loadTagPopUp(){
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

    public void loadTagCard(VBox root, Card current){
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
            addCustomTagToCard(root,current,textField,tagColor);
        });
        Scene tagScene = new Scene(addTag);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Edit tag details");
        popUpStage.setScene(tagScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }

    public void addCustomTagToCard(VBox root, Card current, TextField textField, ColorPicker colorPicker){
        Tag newTag = new Tag();
        if(textField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No empty tag.");
            alert.setContentText("Tag name cannot be empty");
            alert.showAndWait();
        }
        newTag.setTitle(textField.getText());
        newTag.setColor(colorPicker.getValue().toString());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tag.fxml"));
        AnchorPane tag;
        try {
            tag = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tag.setUserData(newTag);
        Label title = (Label) tag.lookup("#tagName");
        title.setText(newTag.getTitle());
        Button deleteBtn = (Button) tag.lookup("#delBtn");
        deleteBtn.setOnAction(event -> {
            deleteTagCard(root,tag);
        });

        ImageView imageView = new ImageView(getClass()
                .getResource("../images/trash.png").toExternalForm());
        imageView.setFitWidth(deleteBtn.getPrefWidth());
        imageView.setFitHeight(deleteBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        deleteBtn.setGraphic(imageView);
        deleteBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), javafx.geometry.Insets.EMPTY)));
        deleteBtn.setOnMouseEntered(event -> {
            deleteBtn.setStyle("-fx-background-color: white");
        });
        deleteBtn.setOnMouseExited(event -> {
            deleteBtn.setStyle("-fx-background-color: transparent");
        });
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(newTag.getColor())
                , null, null);
        Background background = new Background(backgroundFill);
        tag.setBackground(background);
        root.getChildren().add(tag);
        Stage popUp = (Stage) textField.getScene().getWindow();
        popUp.close();

    }

    public void deleteTagCard( VBox root, AnchorPane tag){
        root.getChildren().remove(tag);
    }

    public void openBoardTags(VBox tagsDetails, Card current){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("tagAdding.fxml"));
        AnchorPane root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ScrollPane tagsroll =(ScrollPane) root.lookup("#scroll");
        VBox tags = (VBox) tagsroll.getContent();
        tags.setSpacing(10);
        List<Tag> boardTags = singleBoardCtrl.current_board.getTagLists();
        for(Tag t: boardTags){
            placeTagCard(tags,t, current);
        }
        Button save = (Button) root.lookup("#saveBtn");
        save.setOnAction(event -> {
            saveTags(tagsDetails,tags, current);
        });
        Button back = (Button) root.lookup("#backBtn");
        back.setOnAction(event -> {
            Button source = (Button) event.getSource();
            Stage popup = (Stage) source.getScene().getWindow();
            popup.close();
        });
        Scene tagScene = new Scene(root);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Board Tags");
        popUpStage.setScene(tagScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();


    }

    public void saveTags(VBox parendDetails, VBox parent, Card current){
        for(Node n: parent.getChildren()){
            if(n instanceof CheckBox)
            {
                CheckBox cb = (CheckBox) n;
                if(!cb.isSelected() && current.getTags().contains(cb.getUserData())){
                    parendDetails.getChildren().removeIf(x->((Tag)x.getUserData()).equals((Tag)cb.getUserData()));
                }
                if(cb.isSelected() && !current.getTags().contains(cb.getUserData())){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Tag.fxml"));
                    AnchorPane tag;
                    try {
                        tag = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    tag.setUserData(cb.getUserData());
                    Label title = (Label) tag.lookup("#tagName");
                    title.setText(((Tag)cb.getUserData()).getTitle());
                    Button deleteBtn = (Button) tag.lookup("#delBtn");
                    deleteBtn.setOnAction(event -> {
                        deleteTagCard((VBox) ((AnchorPane) event.getSource()).getParent(),tag);
                    });
                    ImageView imageView = new ImageView(getClass()
                            .getResource("../images/trash.png").toExternalForm());
                    imageView.setFitWidth(deleteBtn.getPrefWidth());
                    imageView.setFitHeight(deleteBtn.getPrefHeight());
                    imageView.setPreserveRatio(true);
                    deleteBtn.setGraphic(imageView);
                    deleteBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), javafx.geometry.Insets.EMPTY)));
                    deleteBtn.setOnMouseEntered(event -> {
                        deleteBtn.setStyle("-fx-background-color: white");
                    });
                    deleteBtn.setOnMouseExited(event -> {
                        deleteBtn.setStyle("-fx-background-color: transparent");
                    });
                    BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(((Tag) cb.getUserData()).getColor())
                            , null, null);
                    Background background = new Background(backgroundFill);
                    tag.setBackground(background);
                    parendDetails.getChildren().add(tag);
                }
            }
        }
        Stage popup = (Stage) parent.getScene().getWindow();
        popup.close();
    }

    public void placeTagCard(VBox parent, Tag t, Card current){
        CheckBox cb = new CheckBox();
        cb.setUserData(t);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tag.fxml"));
        AnchorPane tag;
        try {
            tag = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Button delete = (Button) tag.lookup("#delBtn");
        delete.setVisible(false);
        Label title = (Label) tag.lookup("#tagName");
        title.setText(t.getTitle());
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(t.getColor())
                , null, null);
        Background background = new Background(backgroundFill);
        tag.setBackground(background);
        cb.setGraphic(tag);
        if(current.getTags().contains(t)){
            cb.setSelected(true);
        }
        parent.getChildren().add(cb);

    }

    //boardpage tags

    public void placeTag(HBox parent, Tag tag) {
        parent.setSpacing(10);
        FXMLLoader tagL = new FXMLLoader(getClass().getResource("Tag.fxml"));
        AnchorPane tagBox;
        try {
            tagBox = tagL.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setDoubleClick(tagBox,tag);
        Label title = (Label) tagBox.lookup("#tagName");
        title.setText(tag.getTitle());
        Button deleteBtn = (Button) tagBox.lookup("#delBtn");
        deleteBtn.setOnAction(event -> {
            deleteTag(tag);
        });
        ImageView imageView = new ImageView(getClass()
                .getResource("../images/trash.png").toExternalForm());
        imageView.setFitWidth(deleteBtn.getPrefWidth());
        imageView.setFitHeight(deleteBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        deleteBtn.setGraphic(imageView);
        deleteBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), javafx.geometry.Insets.EMPTY)));
        deleteBtn.setOnMouseEntered(event -> {
            deleteBtn.setStyle("-fx-background-color: white");
        });
        deleteBtn.setOnMouseExited(event -> {
            deleteBtn.setStyle("-fx-background-color: transparent");
        });
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

    public void setDoubleClick(AnchorPane pane, Tag tag){
        pane.setOnMouseClicked(event -> {
            if(event.getClickCount()==2){
                editTag(tag);
            }
        });
    }

    public void editTag(Tag tag){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TagPopUp.fxml"));
        AnchorPane addTag;
        try {
            addTag = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TextField textField = (TextField) addTag.lookup("#tagName");
        textField.setText(tag.getTitle());
        ColorPicker tagColor = (ColorPicker) addTag.lookup("#tagColor");
        tagColor.setValue(Color.valueOf(tag.getColor()));
        Button add = (Button) addTag.lookup("#addTag");
        add.setOnAction(event -> {
            editOldTag(singleBoardCtrl.tagHbox,textField,tagColor, tag);
        });
        Scene tagScene = new Scene(addTag);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Edit tag details");
        popUpStage.setScene(tagScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();

    }

    public void editOldTag(HBox parent, TextField textField, ColorPicker colorPicker, Tag tag) {
        if(textField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No empty tag.");
            alert.setContentText("Tag name cannot be empty");
            alert.showAndWait();
        }
        tag.setTitle(textField.getText());
        tag.setColor(colorPicker.getValue().toString());
        singleBoardCtrl.server.addTagToBoard(singleBoardCtrl.BoardID, tag);
        Stage popUp = (Stage) textField.getScene().getWindow();
        popUp.close();

    }
    public void deleteTag(Tag tag){
        singleBoardCtrl.server.deleteTagToBoard(singleBoardCtrl.BoardID, tag);
       // singleBoardCtrl.refresh();
    }
}
