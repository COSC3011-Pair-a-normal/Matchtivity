/**
 * Main JavaFX application:
 * Shows Start → Difficulty → Category screens
 * Launches the chosen game screen (Easy/Medium/Hard)
 * Provides a Home button in-game to return to Start
 */
package com;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.Serializable;
import java.util.List;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class MainApp extends Application {
    private Stage primaryStage;
    private Scene startScene, difficultyScene, categoryScene, winScene;
    private final int easyCount = 10, mediumCount = 18, hardCount = 30;
    private int cardCount;
    private String deckCategory;
    private GameTimer gameTimer;
    private ScoreBoard scoreboard;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Pair‑A‑Normal Matchtivity");

        loadFont();            // Preload Rock Salt font.
        initStartScene();      // Build the Start screen.
        initDifficultyScene(); // Build Difficulty screen.
        initCategoryScene();   // Build Category screen.

        stage.setScene(startScene);
        stage.show();
    }

    // Preload the custom font for use in every screen.
    private void loadFont() {
        Font.loadFont(getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf")
                     .toExternalForm(), 12);
    }

    // Helper to create a title Label with Rock Salt font.
    private Label titleLabel(String text, double size) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Rock Salt", size));
        lbl.setAlignment(Pos.CENTER);
        return lbl;
    }

    // Helper to style menu Buttons uniformly.
    private void styleButton(Button btn) {
        btn.setFont(Font.font("Rock Salt", 30));
        btn.setPrefSize(400, 100);
    }

    // Build the very first Start screen.
    private void initStartScene() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        Button startNew   = new Button("Start New Game");
        Button startSaved = new Button("Start Saved Game");
        Button exitGame   = new Button("Exit Game");
        styleButton(startNew);
        styleButton(startSaved);
        styleButton(exitGame);

        startNew.setOnAction(e -> primaryStage.setScene(difficultyScene));
        // TODO: implement load‑saved logic
        exitGame.setOnAction(e -> primaryStage.close());

        menu.getChildren().addAll(startNew, startSaved, exitGame);
        BorderPane root = new BorderPane(menu);
        root.setTop(titleLabel("Pair‑A‑Normal Matchtivity", 60));
        BorderPane.setAlignment(root.getTop(), Pos.CENTER);

        startScene = new Scene(root, 1600, 900);
    }

    // Build the Difficulty selection screen.
    private void initDifficultyScene() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        Label lbl = titleLabel("Choose Difficulty", 60);
        Button easy   = new Button("Easy");
        Button medium = new Button("Medium");
        Button hard   = new Button("Hard");
        styleButton(easy);
        styleButton(medium);
        styleButton(hard);

        easy.setOnAction(e -> { cardCount = easyCount; primaryStage.setScene(categoryScene); });
        medium.setOnAction(e -> { cardCount = mediumCount; primaryStage.setScene(categoryScene); });
        hard.setOnAction(e -> { cardCount = hardCount; primaryStage.setScene(categoryScene); });

        menu.getChildren().addAll(lbl, easy, medium, hard);
        difficultyScene = new Scene(new StackPane(menu), 1600, 900);
    }

    // Build the Deck category selection screen.
    private void initCategoryScene() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        Label lbl = titleLabel("Choose Your Category", 60);
        Button reg    = new Button("Regular Deck");
        Button color  = new Button("Color Deck");
        Button themed = new Button("Themed Deck");
        Button custom = new Button("Custom Deck");
        styleButton(reg);
        styleButton(color);
        styleButton(themed);
        styleButton(custom);

        reg.setOnAction(e -> { deckCategory = "regular"; startGame(); });
        color.setOnAction(e -> { deckCategory = "color";   startGame(); });
        themed.setOnAction(e -> { deckCategory = "themed";  startGame(); });
        custom.setOnAction(e -> { deckCategory = "custom";  startGame(); });

        menu.getChildren().addAll(lbl, reg, color, themed, custom);
        categoryScene = new Scene(new StackPane(menu), 1600, 900);
    }

    /**
     * Launch the chosen game screen.
     * Resets the previous deck, passes params via MainAppHolder,
     * and instantiates Easy/Medium/Hard.
     */
    private void startGame() {
        Deck.resetInstance();
        MainAppHolder.setParams(cardCount, deckCategory);
        if      (cardCount == easyCount)   new EasyGameScreen(this, primaryStage);
        else if (cardCount == mediumCount) new MediumGameScreen(this, primaryStage);
        else if (cardCount == hardCount)   new HardGameScreen(this, primaryStage);
    }

    // Called by the MenuButton or Win‑screen to return home.
    public void goToStartScene() {
        if (scoreboard != null) scoreboard.clearScore();  // Reset between rounds.
        primaryStage.setScene(startScene);
    }

    /**
     * Show the Win screen:
     * Displays final score & time,
     * offers a Home button (instead of Exit),
     * and stops the in‑game timer.
     */
    public void showWinScene(int finalScore, long elapsedMillis) {
        Label winLbl   = titleLabel("WOOOOOOO YOU WIN!", 60);
        Label scoreLbl = new Label("Final Score: " + finalScore);
        scoreLbl.setFont(Font.font("Rock Salt", 40));

        int mins = (int)(elapsedMillis / 60000);
        int secs = (int)(elapsedMillis / 1000) % 60;
        Label timeLbl = new Label(String.format("Time: %02d:%02d", mins, secs));
        timeLbl.setFont(Font.font("Rock Salt", 40));

        Button home = new Button("Home");
        styleButton(home);
        home.setOnAction(e -> goToStartScene());

        VBox vbox = new VBox(30, winLbl, scoreLbl, timeLbl, home);
        vbox.setAlignment(Pos.CENTER);
        winScene = new Scene(new StackPane(vbox), 1600, 900);

        primaryStage.setScene(winScene);
    }

    // Injected by BaseGameScreen.
    public void setGameTimer(GameTimer t)    { this.gameTimer = t;   }
    public void setScoreboard(ScoreBoard b) { this.scoreboard = b;  }

    // Returns elapsed time for Win‑screen.
    public long getElapsedTime() {
        return (gameTimer != null) ? gameTimer.getElapsedTime() : 0;
    }

    public class GameState implements Serializable {
        private static final long serialVersionUID = 1L;
        private int score;
        private long elapsedTime;
        private List<Card> matchedCards; 

        public GameState(long elapsedTime, int score, List<Card> matchedCards) {
            this.score = score;
            this.elapsedTime = elapsedTime;
            this.matchedCards = matchedCards;
        }

        public int getScore() {
            return score;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }
        public List<Card> getMatchedCards() {
            return matchedCards; 
        }
    }

    public void saveGame() {
        if (gameTimer == null || scoreboard == null) {
            System.out.println("Game state is incomplete. Cannot save.");
            return;
        }
        long elapsedTime = gameTimer.getElapsedTime();
        int score = scoreboard.getScore();
        List<Card> matchedCards = Deck.getInstance().getMatchedCards(); // Use Deck.getInstance() b/c singleton

        GameState gameState = new GameState(elapsedTime, score, matchedCards);

        String filePath = "savegame.dat";
        System.out.println("Saving game to: " + filePath);

        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(gameState);
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the game: " + e.getMessage());
        }
    }
    
    private void initGameScene() {
        VBox gameLayout = new VBox(20);
        gameLayout.setAlignment(Pos.CENTER);

        Font rockSaltFont = Font.font("Rock Salt", 30); 
        GameMenuButton menuButton = new GameMenuButton("Menu", rockSaltFont, this);
        
        menuButton.setOnSave(e -> saveGame());
        // Add the menu button to the layout.
        gameLayout.getChildren().add(menuButton);

        // Example game scene setup.
        Scene gameScene = new Scene(gameLayout, 1600, 900);
        primaryStage.setScene(gameScene);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
