package com;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.embed.swing.JFXPanel;
import javax.swing.JFrame;
import java.io.IOException;
import java.net.URL;

public abstract class BaseGameScreen
{
    protected JFrame frame;
    protected JFXPanel jfxPanel;
    protected String fxmlPath;

    private Main main;

public void setMain(Main main) {
    this.main = main;
}


    public BaseGameScreen(JFrame frame, String fxmlPath)
    {
        this.frame = frame;
        this.fxmlPath = fxmlPath;
        initializeScreen();
    }

    protected void initializeScreen()
    {
        Platform.runLater(() ->
        {
            try
            {
                // Load the FXML file
                URL url = getClass().getResource(fxmlPath);
                if (url == null)
                {
                    throw new IllegalStateException("FXML not found: " + fxmlPath);
                }
                FXMLLoader loader = new FXMLLoader(url);
                Parent root = loader.load();
                Object controller = loader.getController();
              if (controller instanceof GameController) {
             ((GameController) controller).setMain(Main.getInstance());
}

                

                // If the loaded root is a Pane, add common UI elements.
                if (root instanceof Pane)
                {
                    Pane pane = (Pane) root;
                    addCommonElements(pane);
                }

                // Create a scene from the loaded FXML
                Scene scene = new Scene(root, 1600, 900);
                jfxPanel = new JFXPanel();
                jfxPanel.setScene(scene);

                // Replace the content of the frame with the new JavaFX content
                frame.getContentPane().removeAll();
                frame.getContentPane().add(jfxPanel);
                frame.revalidate();
                frame.repaint();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        });
    }

    // Add the common UI elements needed on every game screen.
    protected void addCommonElements(Pane pane)
    {
    // Add GameTimer
    GameTimer gameTimer = new GameTimer();
    gameTimer.setLayoutX(1300);
    gameTimer.setLayoutY(5);
    pane.getChildren().add(gameTimer);

    // Load fonts
    javafx.scene.text.Font rockSalt = javafx.scene.text.Font.loadFont(
            getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 30.0);
    javafx.scene.text.Font rockSaltSmall = javafx.scene.text.Font.loadFont(
            getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 16.0);

    // Add custom GameMenuButton and configure its actions.
    GameMenuButton menuButton = new GameMenuButton("Menu", rockSalt);
    menuButton.setLayoutX(50);
    menuButton.setLayoutY(50);
    
    // Save action (if needed)
    menuButton.setOnSave(e -> System.out.println("Game saved!"));
    
    // Restart action using reflection to create a new instance of the same game screen.
    menuButton.setOnRestart(event ->
    {
        System.out.println("Restarting the game!");
        // Clear the score (or any other game state you may need to reset)
        Main.scoreboard.clearScore();
        // Use reflection to instantiate a new screen of the same subclass as the current instance.
        Platform.runLater(() ->
        {
            try
            {
                // Assumes that each subclass has a constructor that accepts a JFrame.
                BaseGameScreen newScreen = getClass().getDeclaredConstructor(JFrame.class).newInstance(frame);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
    });
    
    // Exit action.
    menuButton.setOnExit(e -> System.exit(0));
    pane.getChildren().add(menuButton);

    // Assign the scoreboard to Main, positioning it as needed.
    Main.scoreboard = ScoreBoard.getScoreBoard(rockSaltSmall);
    Main.scoreboard.setLayoutX(50);
    Main.scoreboard.setLayoutY(400);
    pane.getChildren().add(Main.scoreboard);
    }
}
