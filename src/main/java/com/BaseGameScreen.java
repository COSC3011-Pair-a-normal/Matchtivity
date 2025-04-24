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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.text.Font;

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

    // Load the FXML, wire the controller, add common UI, show.
    private void load() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Give the controller a reference back to MainApp.
            Object ctrl = loader.getController();
            if (ctrl instanceof GameController) {
                ((GameController)ctrl).setMainApp(mainApp);
            }

            // Inject timer, menu (with Home), and scoreboard.
            addCommonElements((Pane)root);

            Scene scene = new Scene(root, 1600, 900);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inject shared UI into every game screen:
     * 1) Timer at top‑right
     * 2) Menu button at top‑left (Save, Restart, Home, Exit)
     * 3) Singleton scoreboard, safely re‑parented
     */
    protected void addCommonElements(Pane pane) {
        // 1) Game timer
        GameTimer timer = new GameTimer();
        mainApp.setGameTimer(timer);
        timer.setLayoutX(1300);
        timer.setLayoutY(5);
        pane.getChildren().add(timer);

        // 2) Load fonts
        Font rockSalt      = Font.loadFont(getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 30.0);
        Font rockSaltSmall = Font.loadFont(getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 16.0);

        // 3) Menu button (Save, Restart, Home, Exit)
        GameMenuButton menuButton = new GameMenuButton("Menu", rockSalt, mainApp);
        menuButton.setLayoutX(50);
        menuButton.setLayoutY(50);

        menuButton.setOnSave(e -> System.out.println("Game saved!"));
        menuButton.setOnRestart(e -> {
            System.out.println("Restarting the game!");
            // cClear the score and re‑instantiate this screen.
            ScoreBoard.getScoreBoard(rockSaltSmall).clearScore();
            try {
                BaseGameScreen newScreen = this.getClass()
                                             .getDeclaredConstructor(MainApp.class, Stage.class)
                                             .newInstance(mainApp, stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        // Home should call the same method as your Win‑screen button.
        menuButton.setOnHome(e -> mainApp.goToStartScene());
        menuButton.setOnExit(e -> System.exit(0));

        pane.getChildren().add(menuButton);

        // 4) Safe re‑parent the singleton scoreboard.
        ScoreBoard board = ScoreBoard.getScoreBoard(rockSaltSmall);
        Parent oldParent = board.getParent();
        // Only remove if it's already in a Pane.
        if (oldParent instanceof Pane) {
            ((Pane) oldParent).getChildren().remove(board);
        }
        // Now add it to THIS pane.
        mainApp.setScoreboard(board);
        board.setLayoutX(50);
        board.setLayoutY(400);
        pane.getChildren().add(board);
    }
}
