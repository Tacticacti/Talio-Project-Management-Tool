package client.scenes;

import commons.Card;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("checkstyle:Indentation")

public class TagCtrl {
    private final SingleBoardCtrl singleBoardCtrl;

    public TagCtrl(SingleBoardCtrl singleBoardCtrl) {
        this.singleBoardCtrl = singleBoardCtrl;
    }

    public void enterTagName() {
        loadTagPopUp();
    }

    public void loadTagPopUp() {
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
        add.setOnAction(event -> addNewTag(textField, tagColor));
        Scene tagScene = new Scene(addTag);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Edit tag details");
        popUpStage.setScene(tagScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }

    public void addNewTag(TextField textField, ColorPicker colorPicker) {
        if (textField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No empty tag.");
            alert.setContentText("Tag name cannot be empty");
            alert.showAndWait();
        }
        singleBoardCtrl.server.addTagToBoard(SingleBoardCtrl.BoardID
                , textField.getText(), colorPicker.getValue().toString());
        Stage popUp = (Stage) textField.getScene().getWindow();
        popUp.close();

    }

    public void loadTagCard(VBox root) {
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
        add.setOnAction(event -> addCustomTagToCard(root, textField, tagColor));
        Scene tagScene = new Scene(addTag);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Edit tag details");
        popUpStage.setScene(tagScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }

    public void addCustomTagToCard(VBox root,
                                   TextField textField, ColorPicker colorPicker) {
        if (textField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No empty tag.");
            alert.setContentText("Tag name cannot be empty");
            alert.showAndWait();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tag.fxml"));
        AnchorPane tag;
        try {
            tag = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Pair<String, String> tagPair = Pair.of(textField.getText()
                , colorPicker.getValue().toString());
        tag.setUserData(tagPair);
        Label title = (Label) tag.lookup("#tagName");
        title.setText(textField.getText());
        Button deleteBtn = (Button) tag.lookup("#delBtn");
        deleteBtn.setOnAction(event -> deleteTagCard(root, tag));

        ImageView imageView = new ImageView(Objects.requireNonNull(getClass()
                .getResource("../images/trash.png")).toExternalForm());
        imageView.setFitWidth(deleteBtn.getPrefWidth());
        imageView.setFitHeight(deleteBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        deleteBtn.setGraphic(imageView);
        deleteBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT
                , new CornerRadii(5), javafx.geometry.Insets.EMPTY)));
        deleteBtn.setOnMouseEntered(event -> deleteBtn.setStyle("-fx-background-color: white"));
        deleteBtn.setOnMouseExited(
                event -> deleteBtn.setStyle("-fx-background-color: transparent"));
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(colorPicker.getValue()
                .toString())
                , null, null);
        Background background = new Background(backgroundFill);
        tag.setBackground(background);
        root.getChildren().add(tag);
        Stage popUp = (Stage) textField.getScene().getWindow();
        popUp.close();

    }

    public void deleteTagCard(VBox root, AnchorPane tag) {
        root.getChildren().remove(tag);
    }

    public void openBoardTags(VBox tagsDetails, Card current) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("tagAdding.fxml"));
        AnchorPane root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ScrollPane tagsroll = (ScrollPane) root.lookup("#scroll");
        VBox tags = (VBox) tagsroll.getContent();
        tags.setSpacing(10);
        Map<String, String> boardTags = singleBoardCtrl.current_board.getTagLists();
        for (String t : boardTags.keySet()) {
            placeTagCard(tags, t, boardTags.get(t), current);
        }
        Button save = (Button) root.lookup("#saveBtn");
        save.setOnAction(event -> saveTags(tagsDetails, tags, current));
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

    public void saveTags(VBox parendDetails, VBox parent, Card current) {
        for (Node n : parent.getChildren()) {
            if (n instanceof CheckBox cb) {
                Pair<String, String> tagCheck = (Pair<String, String>) cb.getUserData();
                if (!cb.isSelected() && current.getTags().containsKey(tagCheck.getLeft())) {
                    parendDetails.getChildren().removeIf(x -> (x.getUserData())
                            .equals(cb.getUserData()));
                }
                if (cb.isSelected() && !current.getTags()
                        .containsKey(tagCheck.getLeft())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Tag.fxml"));
                    AnchorPane tag;
                    try {
                        tag = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    tag.setUserData(tagCheck);
                    Label title = (Label) tag.lookup("#tagName");
                    title.setText(tagCheck.getLeft());
                    Button deleteBtn = (Button) tag.lookup("#delBtn");
                    deleteBtn.setOnAction(
                            event -> deleteTagCard((VBox) (
                                    (AnchorPane) event.getSource()).getParent(), tag));
                    ImageView imageView = new ImageView(Objects.requireNonNull(getClass()
                            .getResource("../images/trash.png")).toExternalForm());
                    imageView.setFitWidth(deleteBtn.getPrefWidth());
                    imageView.setFitHeight(deleteBtn.getPrefHeight());
                    imageView.setPreserveRatio(true);
                    deleteBtn.setGraphic(imageView);
                    deleteBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT
                            , new CornerRadii(5), javafx.geometry.Insets.EMPTY)));
                    deleteBtn.setOnMouseEntered(
                            event -> deleteBtn.setStyle("-fx-background-color: white"));
                    deleteBtn.setOnMouseExited(
                            event -> deleteBtn.setStyle("-fx-background-color: transparent"));
                    BackgroundFill backgroundFill = new BackgroundFill
                            (Paint.valueOf(tagCheck.getRight())
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

    public void placeTagCard(VBox parent, String t, String c, Card current) {
        CheckBox cb = new CheckBox();
        Pair<String, String> tagPair = Pair.of(t, c);
        cb.setUserData(tagPair);
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
        title.setText(t);
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(c)
                , null, null);
        Background background = new Background(backgroundFill);
        tag.setBackground(background);
        cb.setGraphic(tag);
        if (current.getTags().containsKey(t)) {
            cb.setSelected(true);
        }
        parent.getChildren().add(cb);

    }

    //boardpage tags

    public void placeTag(HBox parent, String tag, String color) {
        parent.setSpacing(10);
        FXMLLoader tagL = new FXMLLoader(getClass().getResource("Tag.fxml"));
        AnchorPane tagBox;
        try {
            tagBox = tagL.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setDoubleClick(tagBox, tag, color);
        Label title = (Label) tagBox.lookup("#tagName");
        title.setText(tag);
        title.setTextFill(Color.BLACK);
        Button deleteBtn = (Button) tagBox.lookup("#delBtn");
        deleteBtn.setOnAction(event -> deleteTag(tag));
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass()
                .getResource("../images/trash.png")).toExternalForm());
        imageView.setFitWidth(deleteBtn.getPrefWidth());
        imageView.setFitHeight(deleteBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        deleteBtn.setGraphic(imageView);
        deleteBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT
                , new CornerRadii(5), javafx.geometry.Insets.EMPTY)));
        BackgroundFill backgroundFillbtn = new BackgroundFill(Color.TRANSPARENT
                , CornerRadii.EMPTY, Insets.EMPTY);
        Background backgroundbtn = new Background(backgroundFillbtn);
        deleteBtn.setBackground(backgroundbtn);
        deleteBtn.setOnMouseEntered(event -> {
            deleteBtn.setStyle("-fx-background-color: white");
        });
        deleteBtn.setOnMouseExited(event -> {
            deleteBtn.setStyle("-fx-background-color: transparent");
        });
        VBox setTags = new VBox();
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(color)
                , null, null);
        Background background = new Background(backgroundFill);
        tagBox.setBackground(background);

        VBox.setMargin(setTags, new Insets(0, 5, 0, 5));
        setTags.setSpacing(15);

        tagBox.setPrefHeight(setTags.getPrefHeight() / 2);

        setTags.setPrefHeight(parent.getHeight());
        setTags.setPrefWidth(tagBox.getWidth());

        if (parent.getChildren().size() == 0) {
            setTags.getChildren().add(tagBox);
            parent.getChildren().add(setTags);
        } else {
            VBox previous = (VBox) parent.getChildren().get(parent.getChildren().size() - 1);
            if (previous.getChildren().size() == 2) {
                setTags.getChildren().add(tagBox);
                parent.getChildren().add(setTags);
            } else {
                previous.getChildren().add(tagBox);
            }
        }

    }

    public void setDoubleClick(AnchorPane pane, String tag, String color) {
        pane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editTag(tag, color);
            }
        });
    }

    public void editTag(String tag, String color) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TagPopUp.fxml"));
        AnchorPane addTag;
        try {
            addTag = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TextField textField = (TextField) addTag.lookup("#tagName");
        textField.setText(tag);
        ColorPicker tagColor = (ColorPicker) addTag.lookup("#tagColor");
        tagColor.setValue(Color.valueOf(color));
        Button add = (Button) addTag.lookup("#addTag");
        add.setOnAction(event -> editOldTag(textField, tagColor, tag));
        Scene tagScene = new Scene(addTag);
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Edit tag details");
        popUpStage.setScene(tagScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();

    }

    public void editOldTag(TextField textField, ColorPicker colorPicker, String tag) {
        if (textField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No empty tag.");
            alert.setContentText("Tag name cannot be empty");
            alert.showAndWait();
        }
        Map<String, String> map = singleBoardCtrl.current_board.getTagLists();
        singleBoardCtrl.server.updateCardsTag(singleBoardCtrl.BoardID, tag
                , textField.getText(), colorPicker.getValue().toString());

        if (map.keySet().contains(tag) && !tag.equals(textField.getText())) {
            singleBoardCtrl.server.deleteTagToBoard(singleBoardCtrl.BoardID, tag);
        }

        singleBoardCtrl.server.addTagToBoard(singleBoardCtrl.BoardID, textField.getText()
                , colorPicker.getValue().toString());


        Stage popUp = (Stage) textField.getScene().getWindow();
        popUp.close();

    }

    public void deleteTag(String tag) {
        singleBoardCtrl.server.deleteTagToBoard(SingleBoardCtrl.BoardID, tag);
        // singleBoardCtrl.refresh();
    }
}
