package com;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.net.URL;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class GameController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane imagesGridPane;

    private List<ImageView> flippedCards = new ArrayList<>();
    private boolean processingCards = false;

    public static final Image backImage = new Image("/images/back_of_card.png"); 
    public List<Integer> cardIDtoImageID = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int cardCount = Main.getCardCount();
        for (int i = 0; i < cardCount/2; i++) {
            cardIDtoImageID.add(i);
        }
        for (int j = 0; j < cardCount/2; j++) {
            cardIDtoImageID.add(j);
        }
        Collections.shuffle(cardIDtoImageID);
        Deck deck = Deck.getInstance();
        initializeImageView();
    }

    // This method will initialize the image view and set the back of the card
    private void initializeImageView() {
        for (int i = 0; i < imagesGridPane.getChildren().size(); i++) {
            ImageView imageView = (ImageView) imagesGridPane.getChildren().get(i);
            imageView.setImage(backImage);  // Set back image for each card

            imageView.setUserData(i); // Each card has an identification number (0, 1, 2, ...)

            // Add mouse click event for card flipping
            imageView.setOnMouseClicked(event -> {
                //only allow two cards to be flipped at a time 
                if (!processingCards && flippedCards.size() < 2) {
                    flipCard((int) imageView.getUserData());    
                }
            });
        }
    }

    // Flip the card (showing the front image and then flipping back to the back)
    private void flipCard(int cardID) {
        ImageView imageView = (ImageView) imagesGridPane.getChildren().get(cardID);
        Image image;
    
        if (imageView.getImage() == backImage) {
            // Get the image corresponding to the current card ID from the shuffled cardIDtoImageID array
            Integer imageID = cardIDtoImageID.get(cardID);
            String directoryPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "currentImages";
            File directory = new File(directoryPath);
        
            // Check if the directory exists and is a directory
            if (directory.exists() && directory.isDirectory()) {
                // List all the files in the directory (filtering for image files)
                File[] files = directory.listFiles();
                File frontImageFile = files[imageID];
                // Create the Image object from the file URI
                image = new Image(frontImageFile.toURI().toString());
                flippedCards.add(imageView);
            } else {
                image = backImage;  // Flip it back to the back image
            }
        } else {
            image = backImage;
            flippedCards.remove(imageView); 
        }
    
        // Update the image on the imageView
        imageView.setImage(image);

        if (flippedCards.size() == 2) {
            processingCards = true; 
            processFlippedCards(); 
        }
    }

    private void processFlippedCards() {
        ImageView firstCard = flippedCards.get(0); 
        ImageView secondCard = flippedCards.get(1);

        // matching logic using the cardIDtoImageID mapping
        boolean cardsMatch = cardIDtoImageID.get((int) firstCard.getUserData()) == cardIDtoImageID.get((int) secondCard.getUserData());

        if (cardsMatch) {
            Main.scoreboard.increaseScore(); 
            PauseTransition pause = new PauseTransition(Duration.seconds(1));

            pause.setOnFinished(event -> {
                firstCard.setVisible(false); 
                secondCard.setVisible(false); 

                flippedCards.clear(); 
            }); 

            pause.play(); 

            processingCards = false;
        } else { 
            //delay for 1 second to see cards 
            PauseTransition pause = new PauseTransition(Duration.seconds(1));

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
