/**
 * Abstract base for Easy/edium/Hard screens.
 * Loads FXML.
 * Wires controller to MainApp.
 * Injects shared UI (timer, menu + home, scoreboard).
*/
package com;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;

public abstract class BaseGameScreen {
    protected Stage stage;
    protected MainApp mainApp;
    private String fxmlPath;

    public BaseGameScreen(MainApp app, Stage stage, String fxml) {
        this.mainApp  = app;
        this.stage    = stage;
        this.fxmlPath = fxml;
        load();
    }
    private void load() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Give the controller a reference back to MainApp.
            Object ctrl = loader.getController();
            if (ctrl instanceof GameController) {
                ((GameController) ctrl).setMainApp(mainApp);
            }

            // Inject shared UI elements into the correct gridpane.
            addCommonElements((Pane) root);

            // Add background image
            Image backgroundImage = new Image(getClass().getResource("/images/purpleBackground.jpg").toExternalForm());
            BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                    100, 100, true, true, false, true
                )
            );
            ((Pane) root).setBackground(new Background(bgImage));

            // Create scene using root's preferred size (from FXML)
            Scene scene = new Scene(root); // dynamic sizing
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * Inject shared UI into every game screen:
     * 1) Menu button (Save, Restart, Home, Exit)
     * 2) Scoreboard
     * 3) Timer
     * All vertically stacked on the left side.
    */
    protected void addCommonElements(Pane pane) {
        // Load fonts
        Font rockSalt      = MainApp.ROCK_SALT_FONT;
        Font rockSaltSmall = MainApp.ROCK_SALT_SMALL;
    
        // Get the existing commonElements GridPane from the FXML
        GridPane commonGrid = (GridPane) pane.lookup("#commonElements");
    
        // Ensure commonGrid is present
        if (commonGrid != null) {
            // 1) Menu button
            GameMenuButton menuButton = new GameMenuButton("Menu", rockSalt, mainApp);
            menuButton.setOnSave(e -> System.out.println("Game saved!"));
            menuButton.setOnRestart(e -> {
                System.out.println("Restarting the game!");
                ScoreBoard.getScoreBoard(rockSaltSmall).clearScore();
                try {
                    BaseGameScreen newScreen = this.getClass()
                        .getDeclaredConstructor(MainApp.class, Stage.class)
                        .newInstance(mainApp, stage);
                    Deck.resetInstance();
                    BaseGameScreen newScreen2 = this.getClass()
                        .getDeclaredConstructor(MainApp.class, Stage.class)
                        .newInstance(mainApp, stage);
                    Deck.resetInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            menuButton.setOnHome(e -> mainApp.goToStartScene());
            menuButton.setOnExit(e -> System.exit(0));
    
            commonGrid.add(menuButton, 0, 0);
    
            // 2) Scoreboard
            ScoreBoard board = ScoreBoard.getScoreBoard(rockSaltSmall);
            Parent oldParent = board.getParent();
            if (oldParent instanceof Pane) {
                ((Pane) oldParent).getChildren().remove(board);
            }
            mainApp.setScoreboard(board);
            commonGrid.add(board, 0, 1);
    
            // 3) Game timer
            GameTimer timer = new GameTimer();
            mainApp.setGameTimer(timer);
            commonGrid.add(timer, 0, 2);
        }
    }    
}
