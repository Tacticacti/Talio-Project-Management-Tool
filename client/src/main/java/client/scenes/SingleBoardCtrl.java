package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class SingleBoardCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @FXML
    private HBox hbox_lists;

    @FXML
    private AnchorPane sb_anchor;

    @FXML
    private Button settingsBtn;

    @Inject
    public SingleBoardCtrl(ServerUtils server, MainCtrl mainCtrl) throws IOException {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void initialize() throws IOException {
        createNewList();
        ImageView imageView = new ImageView(getClass().getResource("../images/settings_icon.png").toExternalForm());
        imageView.setFitWidth(settingsBtn.getPrefWidth());
        imageView.setFitHeight(settingsBtn.getPrefHeight());
        imageView.setPreserveRatio(true);
        settingsBtn.setGraphic(imageView);
    }


    public void back(){
        mainCtrl.showBoardOverview();
    }

    public void card(){
        mainCtrl.showAddCard();
    }


    public void createNewList() throws IOException {
        var board_lists = hbox_lists.getChildren();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("listGUI.fxml"));
        Node list = loader.load();
        board_lists.add(board_lists.size()-1, list);

        Button btn =  (Button) list.lookup("#deleteBtn");
        btn.setOnAction(event -> board_lists.remove(btn.getParent()));

        TextField title = (TextField) list.lookup("#list_title");

        title.setOnAction(event -> {
            try {
                if (!title.getText().isEmpty()) {
                    createNewList();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button btn2 =  (Button) list.lookup("#addNewCardButton");
        btn2.setOnAction(event ->{
            VBox par = (VBox) btn2.getParent();
            addCard(par);
        });

        board_lists.get(board_lists.size()-2).lookup("#list_title").requestFocus();


    }

    




    public void addCard(VBox parent){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cardGUI.fxml"));
        try {
            Node card = (Node) fxmlLoader.load();
            Button det = (Button) card.lookup("#details");
            det.setOnAction(event -> mainCtrl.showAddCard());
            int index =parent.getChildren().size()-2;
            if(parent.getChildren().size()<2){
                index=0;
            }
            parent.getChildren().add(index,card);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteList() {

        hbox_lists.getChildren().remove(0);

    }

    // is this setup only for title (?)
    public void enterOnTextField() throws IOException {
        createNewList();
    }


    public void openBoardSettings() throws IOException {
        System.out.println("running!");


        FXMLLoader loader = new FXMLLoader(getClass().getResource("customizationPage.fxml"));
        AnchorPane customization = loader.load();

        customization.setLayoutY(250);
        customization.setLayoutX(770);

        customization.setScaleX(1.5);
        customization.setScaleY(1.5);


        Button closebtn =  (Button) customization.lookup("#closeCustomizationMenu");
        closebtn.setOnAction(event -> sb_anchor.getChildren().remove(closebtn.getParent()));

        // doesn't actually delete anything just goes back to board overview
        Button delbtn =  (Button) customization.lookup("#deleteBoard");
        delbtn.setOnAction(event -> {
            // remove this specific board


            mainCtrl.showBoardOverview();

        });



        sb_anchor.getChildren().add(customization);


    }


}
