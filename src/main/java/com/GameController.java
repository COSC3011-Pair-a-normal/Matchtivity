package com;

import javafx.fxml.Initializable;

import java.util.ResourceBundle;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class GameController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private FlowPane imagesFlowPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeImageView();
        System.out.println("controller is working");
    }

    private void initializeImageView() {
        for (int i = 0; i < imagesFlowPane.getChildren().size();i++) {
            ImageView imageView = (ImageView) imagesFlowPane.getChildren().get(i);
            Image image = new Image("/images/classic/2_of_clubs.png");
            imageView.setImage(image);
        }
    }

}
