package com;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.net.URL;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.animation.RotateTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Interpolator;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

//import javax.swing.text.html.ImageView; 

public class GameController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane imagesGridPane;

    private List<ImageView> flippedCards = new ArrayList<>();
    private boolean processingCards = false;

    public static final Image backImage = new Image("/images/BackOfCard_Orange.png"); 
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

        //dynamically resize the GrodPane's images based on the number of rows and columns
        adjustGridSize(); 
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

    // Are we flipping from back → front, or front → back?
    boolean flippingToFront = imageView.getImage() == backImage;
    Image newImage;

    if (flippingToFront) {
        // — your original directory logic —
        String directoryPath = System.getProperty("user.dir")
            + File.separator + "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + "images"
            + File.separator + "currentImages";
        File directory = new File(directoryPath);

        // Grab the file and build the Image
        File[] files = directory.listFiles();
        File frontImageFile = files[cardIDtoImageID.get(cardID)];
        newImage = new Image(frontImageFile.toURI().toString());

        flippedCards.add(imageView);
    } else {
        newImage = backImage;
        flippedCards.remove(imageView);
    }

    // — flip animation —
    RotateTransition spinOut = new RotateTransition(Duration.millis(250), imageView);
    spinOut.setAxis(Rotate.Y_AXIS);
    spinOut.setFromAngle(0);
    spinOut.setToAngle(90);
    spinOut.setInterpolator(Interpolator.LINEAR);

    spinOut.setOnFinished(evt -> {
    // halfway through (90°), swap in the new image
    imageView.setImage(newImage);
    // set us up at 270° for the second half
    imageView.setRotate(270);
    // spin from 270° → 360° (i.e. back to 0°)
    RotateTransition spinIn = new RotateTransition(Duration.millis(250), imageView);
    spinIn.setAxis(Rotate.Y_AXIS);
    spinIn.setFromAngle(270);
    spinIn.setToAngle(360);
    spinIn.setInterpolator(Interpolator.LINEAR);
    spinIn.play();
    });

    // kick off the first half
    spinOut.play();

    // once two cards are face‑up, start your matching logic
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
            PauseTransition pause = new PauseTransition(Duration.seconds(0.8));

           pause.setOnFinished(event -> {
    firstCard.setVisible(false); 
    secondCard.setVisible(false); 
    flippedCards.clear();

    // Check if all cards are matched
    int totalMatched = 0;
    for (javafx.scene.Node node : imagesGridPane.getChildren()) {
        if (node instanceof ImageView && !node.isVisible()) {
            totalMatched++;
        }
    }

    if (totalMatched == Main.getCardCount()) {
        Platform.runLater(() -> {
            main.returnToSwingPanel(); // bring the swing panel back
            main.showWinScreen();      // then it shows the win screen
        });
        

    }
});

            pause.play(); 

            processingCards = false;
        } else {
            // let player see both cards for 0.8s
            PauseTransition wait = new PauseTransition(Duration.seconds(0.8));
            wait.setOnFinished(evt -> {
                for (ImageView card : flippedCards) {
                    // 1) spin the front face away: 0° → 90°
                    RotateTransition spinOut = new RotateTransition(Duration.millis(250), card);
                    spinOut.setAxis(Rotate.Y_AXIS);
                    spinOut.setFromAngle(0);
                    spinOut.setToAngle(90);
                    spinOut.setInterpolator(Interpolator.LINEAR);

                    spinOut.setOnFinished(e2 -> {
                        // halfway: swap to the backside
                        card.setImage(backImage);
                        // set us up at 270° (i.e. -90°) so the second half spins forward
                        card.setRotate(270);

                        // 2) spin the back‑face into view: 270° → 360°
                        RotateTransition spinIn = new RotateTransition(Duration.millis(250), card);
                        spinIn.setAxis(Rotate.Y_AXIS);
                        spinIn.setFromAngle(270);
                        spinIn.setToAngle(360);
                        spinIn.setInterpolator(Interpolator.LINEAR);
                        spinIn.play();
                    });

                    spinOut.play();
                }

                // clear state once all flip‑backs are running
                flippedCards.clear();
                processingCards = false;
            });
            wait.play();
        }        
    }
    
    private void adjustGridSize() {
        int rows = 0;
        int columns = 0;
    
        // Determine grid size based on card count
        if (Main.getCardCount() == 10) {
            rows = 2;
            columns = 5;
        } else if (Main.getCardCount() == 18) {
            rows = 3;
            columns = 6;
        } else if (Main.getCardCount() == 30) {
            rows = 5;
            columns = 6;
        }
    
        // Clear any existing constraints to avoid duplication
        imagesGridPane.getRowConstraints().clear();
        imagesGridPane.getColumnConstraints().clear();
    
        // Set horizontal and vertical gaps between the cards
        imagesGridPane.setHgap(10); // Horizontal gap between cards
        imagesGridPane.setVgap(10); // Vertical gap between cards
    
        // Add RowConstraints and ColumnConstraints to evenly distribute space
        for (int i = 0; i < columns; i++) {
            javafx.scene.layout.ColumnConstraints column = new javafx.scene.layout.ColumnConstraints();
            column.setPercentWidth(100.0 / columns);  // Evenly distribute width
            imagesGridPane.getColumnConstraints().add(column);
        }
    
        for (int i = 0; i < rows; i++) {
            javafx.scene.layout.RowConstraints row = new javafx.scene.layout.RowConstraints();
            row.setPercentHeight(100.0 / rows);  // Evenly distribute height
            imagesGridPane.getRowConstraints().add(row);
        }

        
    
        // Adjust the card sizes and spacing
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                ImageView imageView = (ImageView) imagesGridPane.getChildren().get(row * columns + col);
    
                // Ensure cards fit the grid cells and maintain aspect ratio
                imageView.setPreserveRatio(true);
    
                // Increase card size proportionally
                imageView.fitWidthProperty().bind(imagesGridPane.widthProperty().multiply(0.85).divide(columns).subtract(imagesGridPane.getHgap())); // Adjust width based on column count, subtracting horizontal gap
                imageView.fitHeightProperty().bind(imagesGridPane.heightProperty().multiply(0.85).divide(rows).subtract(imagesGridPane.getVgap())); // Adjust height based on row count, subtracting vertical gap
    
                // Remove any margin around cards to avoid extra space
                javafx.scene.layout.GridPane.setMargin(imageView, new javafx.geometry.Insets(0)); // No margin
            }
        }

        
    
        // Center the grid within the available space
        imagesGridPane.setAlignment(Pos.CENTER); // Center the entire grid in the available space
    }             
// Reference to Main
private Main main;

public void setMain(Main main) {
    this.main = main;
}

}

