package com;

import javafx.fxml.Initializable;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

public class GameController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private FlowPane imagesFlowPane;

    private List<ImageView> flippedCards = new ArrayList<>();
    private boolean processingCards = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeImageView();
        System.out.println("controller is working");
    }

    public static final Image backImage = new Image("/images/back_of_card.png");

    private void initializeImageView() {
        for (int i = 0; i < imagesFlowPane.getChildren().size();i++) {
            ImageView imageView = (ImageView) imagesFlowPane.getChildren().get(i);
            Image image = backImage;
            imageView.setImage(image);

            imageView.setUserData(i); // Each card has an identification number 0,1,...,n

            // functionality for making the folder for the current card images will go here

            imageView.setOnMouseClicked(event -> {
                //only allow two cards to be flipped at a time 
                if (!processingCards && flippedCards.size() < 2) {
                    flipCard((int) imageView.getUserData());    
                }
            });
        }
    }

    private void flipCard(int cardID) {
        ImageView imageView = (ImageView) imagesFlowPane.getChildren().get(cardID);
        Image image;
        System.out.println(imageView.getImage() == backImage);
        if (imageView.getImage() == backImage) {
            image = new Image("/images/classic/ace_of_spades.png");
            flippedCards.add(imageView);
            System.out.println("Flipped: " + flippedCards.size()); 
        } else {
            image = backImage;
            flippedCards.remove(imageView); 
            System.out.println("Reversed: " + flippedCards.size());
        }
        imageView.setImage(image);

        
        if (flippedCards.size() == 2) {
            processingCards = true; 
            processFlippedCards(); 
        }
    }
    
    private void processFlippedCards() {
        ImageView firstCard = flippedCards.get(0); 
        ImageView secondCard = flippedCards.get(1);

        //currently not working correctly 
        boolean cardsMatch = firstCard.getImage().equals(secondCard.getImage()); 

        if (cardsMatch) {
            System.out.println("Match!");
            //increase score
            //remove from screen 
        } else {
            System.out.println("No match!");
            
            //delay for 1.5 seconds to see cards 
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

            pause.setOnFinished(event -> {
                //reset the flipped cards 
                for (ImageView card : flippedCards) {
                    card.setImage(backImage);
                }

                flippedCards.clear();
                processingCards = false;
            });

            //pause
            pause.play();
            }

    }

}
