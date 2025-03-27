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

    public static final Image backImage = new Image("/images/back_of_card.png");

    private void initializeImageView() {
        for (int i = 0; i < imagesFlowPane.getChildren().size();i++) {
            ImageView imageView = (ImageView) imagesFlowPane.getChildren().get(i);
            //Image image = new Image("/images/classic/2_of_clubs.png");
            Image image = backImage;
            imageView.setImage(image);

            imageView.setUserData(i); // Each card has an identification number 0,1,...,n

            // functionality for making the folder for the current card images will go here

            imageView.setOnMouseClicked(event -> {
                flipCard((int) imageView.getUserData());
            });
        }
    }

    private void flipCard(int cardID) {
        ImageView imageView = (ImageView) imagesFlowPane.getChildren().get(cardID);
        Image image;
        System.out.println(imageView.getImage() == backImage);
        if (imageView.getImage() == backImage) {
            image = new Image("/images/classic/ace_of_spades.png");
        } else {
            image = backImage;
        }
        imageView.setImage(image);
    }

}
