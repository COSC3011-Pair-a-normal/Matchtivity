/**
 * Controller for the game board. 
 * Initializes card grid with back‑of‑card images.
 * Handles card flipping animations.
 * Implements matching logic and win detection.
 * Dynamically resizes grid based on difficulty.
 */
package com;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.animation.RotateTransition;
import javafx.animation.Interpolator;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

public class GameController implements Initializable {
    @FXML private GridPane imagesGridPane;      // Grid containing all card ImageViews.
    private MainApp mainApp;                    // Reference back to the JavaFX application.
    private List<ImageView> flippedCards = new ArrayList<>(); // Currently face‑up cards.
    private boolean processing = false;         // Prevents extra clicks during evaluation.
    private ScoreManager scoreManager;
    private int totalScore;
    private String currentMode;

    public static final Image backImage =
        new Image("/images/BackOfCard_Orange.png");            // Shared back‑of‑card image.
    public static List<Integer> cardMap = new ArrayList<>();          // Maps card index → image ID.

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int count = MainAppHolder.getCardCount();
        switch (count) {
            case 10: currentMode = "EASY";   break;
            case 18: currentMode = "MEDIUM"; break;
            case 30: currentMode = "HARD";   break;
            default: currentMode = "UNKNOWN";break;
        }
        cardMap = new ArrayList<>();

        scoreManager = new ScoreManager();
        totalScore = 0;
        ScoreBoard.getScoreBoard(mainApp.ROCK_SALT_SMALL).clearScore();

        // Build two of each ID, then shuffle if the game is new
        for (int i = 0; i < count/2; i++) cardMap.add(i);
        for (int i = 0; i < count/2; i++) cardMap.add(i);
        Collections.shuffle(cardMap);

        // Ensure the currentImages directory is populated.
        Deck.getInstance();

        loadMatches();
        initGrid();       // Populate ImageViews and back images.
        adjustGridSize(); // Make grid adapt to chosen difficulty.
    }

    // Setter injected by FXMLLoader to give controller a link back to the MainApp.
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initialize each ImageView in the GridPane:
     * set back‑of‑card image
     * tag with userData = its index
     * add click handler (only when face‑down, up to 2 at a time)
     */
    private void initGrid() {
        for (int i = 0; i < imagesGridPane.getChildren().size(); i++) {
            ImageView iv = (ImageView) imagesGridPane.getChildren().get(i);
            iv.setImage(backImage);
            iv.setUserData(i);
            iv.setOnMouseClicked(e -> {
                if (!processing
                 && flippedCards.size() < 2
                 && iv.getImage() == backImage)
                {
                    flipCard((int) iv.getUserData());
                }
            });
        }
    }

    /**
     * Flip animation and state change:
     * Prevent double‑clicking same card during flip.
     * Swap in correct front image halfway through a Y‑axis spin.
     * After two flips, trigger match evaluation.
     */
    private void flipCard(int cardID) {
        ImageView iv = (ImageView) imagesGridPane.getChildren().get(cardID);
        boolean toFront = iv.getImage() == backImage;

        // Guard: ignore repeated flips before first half completes.
        if (toFront && flippedCards.contains(iv)) return;

        Image newImage;
        if (toFront) {
            // Load the front image from currentImages folder.
            File dir = new File(System.getProperty("user.dir")
                + "/src/main/resources/images/currentImages");
            File[] files = dir.listFiles();
            newImage = new Image(files[cardMap.get(cardID)].toURI().toString());
            flippedCards.add(iv);
        } else {
            // Flip back to the backside.
            newImage = backImage;
            flippedCards.remove(iv);
        }

        // First half spin: 0 → 90°, then swap image.
        RotateTransition spinOut = new RotateTransition(Duration.millis(250), iv);
        spinOut.setAxis(Rotate.Y_AXIS);
        spinOut.setFromAngle(0);
        spinOut.setToAngle(90);
        spinOut.setInterpolator(Interpolator.LINEAR);
        spinOut.setOnFinished(evt -> {
            iv.setImage(newImage);
            iv.setRotate(270);
            // Second half spin: 270° → 360°.
            RotateTransition spinIn = new RotateTransition(Duration.millis(250), iv);
            spinIn.setAxis(Rotate.Y_AXIS);
            spinIn.setFromAngle(270);
            spinIn.setToAngle(360);
            spinIn.setInterpolator(Interpolator.LINEAR);
            spinIn.play();
        });
        spinOut.play();

        // After two are flipped, evaluate match.
        if (flippedCards.size() == 2) {
            processing = true;
            processFlippedCards();
        }
    }

    /**
     * Once two cards are face‑up:
     * If they match, remove them and increase score.
     * If not, flip both back after a short pause.
     * When all are matched, trigger win screen.
     */
    private void processFlippedCards() {
        ImageView first  = flippedCards.get(0);
        ImageView second = flippedCards.get(1);
        boolean match = cardMap.get((int) first.getUserData())
                      == cardMap.get((int) second.getUserData());

        if (match) {
            // Matched pair: hide them after 0.8s and update score.
            Deck.getInstance().addMatchedCard((int) first.getUserData());
            Deck.getInstance().addMatchedCard((int) second.getUserData());
            ScoreBoard.getScoreBoard(mainApp.ROCK_SALT_SMALL).setScore(totalScore);
            try {
                SoundManager.play("ding.mp3");
            } catch (Exception e) {
                System.out.println("Sound error: " + e.getMessage());
            }

            int earned = scoreManager.collectHiddenValue();
            totalScore += earned;
            ScoreBoard.getScoreBoard(mainApp.ROCK_SALT_SMALL).setScore(totalScore);

            PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
            pause.setOnFinished(e -> {
                first.setVisible(false);
                second.setVisible(false);
                flippedCards.clear();

                // Check for win
                int done = 0;
                for (javafx.scene.Node n : imagesGridPane.getChildren()) {
                    if (n instanceof ImageView && !n.isVisible()) done++;
                }
                if (done == MainAppHolder.getCardCount()) {
                    Platform.runLater(() ->
                        mainApp.showWinScene(
                            totalScore,
                            mainApp.getElapsedTime(),
                            currentMode
                        )
                    );
                }
                processing = false;
            });
            pause.play();
        } else {

            scoreManager.recordMistake();
            // Not a match: flip both back after 0.8s.
            PauseTransition wait = new PauseTransition(Duration.seconds(0.8));
            wait.setOnFinished(e -> {
                for (ImageView iv : new ArrayList<>(flippedCards)) {
                    RotateTransition out = new RotateTransition(Duration.millis(250), iv);
                    out.setAxis(Rotate.Y_AXIS);
                    out.setFromAngle(0);
                    out.setToAngle(90);
                    out.setInterpolator(Interpolator.LINEAR);
                    out.setOnFinished(e2 -> {
                        iv.setImage(backImage);
                        iv.setRotate(270);
                        RotateTransition in = new RotateTransition(Duration.millis(250), iv);
                        in.setAxis(Rotate.Y_AXIS);
                        in.setFromAngle(270);
                        in.setToAngle(360);
                        in.setInterpolator(Interpolator.LINEAR);
                        in.play();
                    });
                    out.play();
                }
                flippedCards.clear();
                processing = false;
            });
            wait.play();
        }
    }

    /**
     * Dynamically adjust the grid’s rows, columns, gaps, and each ImageView’s fit
     * properties to fill the scene evenly based on the card count.
     */
    private void adjustGridSize() {
        int rows, cols;
        switch (MainAppHolder.getCardCount()) {
            case 10: rows = 2; cols = 5; break;
            case 18: rows = 3; cols = 6; break;
            case 30: rows = 5; cols = 6; break;
            default: rows = 2; cols = 5; break;
        }

        imagesGridPane.getRowConstraints().clear();
        imagesGridPane.getColumnConstraints().clear();
        imagesGridPane.setHgap(10);
        imagesGridPane.setVgap(10);

        // Evenly distribute columns.
        for (int i = 0; i < cols; i++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPercentWidth(100.0 / cols);
            imagesGridPane.getColumnConstraints().add(cc);
        }
        // Evenly distribute rows.
        for (int i = 0; i < rows; i++) {
            javafx.scene.layout.RowConstraints rc = new javafx.scene.layout.RowConstraints();
            rc.setPercentHeight(100.0 / rows);
            imagesGridPane.getRowConstraints().add(rc);
        }

        // Bind each ImageView’s fit size.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                ImageView iv = (ImageView) imagesGridPane.getChildren().get(r * cols + c);
                iv.setPreserveRatio(true);
                iv.fitWidthProperty().bind(
                    imagesGridPane.widthProperty()
                        .multiply(0.85).divide(cols)
                        .subtract(imagesGridPane.getHgap())
                );
                iv.fitHeightProperty().bind(
                    imagesGridPane.heightProperty()
                        .multiply(0.85).divide(rows)
                        .subtract(imagesGridPane.getVgap())
                );
                javafx.scene.layout.GridPane.setMargin(iv, new javafx.geometry.Insets(0));
            }
        }

        imagesGridPane.setAlignment(Pos.CENTER);
    }

    public void loadMatches() {
        // Retrieve the list of matched cards
        List<Integer> matchedCards = Deck.getMatchedCards();
    
        // Loop through the matched cards and hide them
        for (Integer matchedCardId : matchedCards) {
            ImageView matchedCard = (ImageView) imagesGridPane.getChildren().get(matchedCardId);
            if (matchedCard != null) {
                matchedCard.setVisible(false);
            }
        }
    }

    public static List<Integer> getCardMap() {
        return cardMap;
    }

    public static void setCardMap(List<Integer> cardMap) {
        GameController.cardMap = cardMap;
    }
}
