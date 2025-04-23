/*
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; 
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;
    private Scene startScene, difficultyScene, categoryScene, winScene, savedScene; 
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
        initStartSavedScene(); // Buid Start Saved screen. 

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
        lbl.setStyle("-fx-text-fill: white;"); 
        lbl.setAlignment(Pos.CENTER);
        return lbl;
    }

    // Helper to style menu Buttons uniformly.
    private void styleButton(Button btn) {
        btn.setFont(Font.font("Rock Salt", 30));
        btn.setPrefSize(400, 100);
        btn.setStyle("-fx-background-radius: 50; -fx-border-radius: 50;"); 
    }

    // Helper to return uniform background image. 
    private ImageView getBackgroundImage() {
        ImageView background = new ImageView(
            new Image(getClass().getResource("/images/purpleBackground.jpg").toExternalForm())
        );
        background.setFitWidth(1600);
        background.setFitHeight(900);
        background.setPreserveRatio(false);
    
        return background;
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
        startSaved.setOnAction(e -> primaryStage.setScene(savedScene));
        exitGame.setOnAction(e -> primaryStage.close());

        Image backgroundImage = new Image(getClass().getResource("/images/purpleBackground.jpg").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, true, false)
        );

        menu.getChildren().addAll(startNew, startSaved, exitGame);
        BorderPane root = new BorderPane(menu);
        root.setTop(titleLabel("Pair‑A‑Normal Matchtivity", 60));
        root.setBackground(new Background(bgImage));
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

        ImageView bg = getBackgroundImage(); 

        menu.getChildren().addAll(lbl, easy, medium, hard);
        difficultyScene = new Scene(new StackPane(bg, menu), 1600, 900);
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

        ImageView bg = getBackgroundImage(); 

        menu.getChildren().addAll(lbl, reg, color, themed, custom);
        categoryScene = new Scene(new StackPane(bg, menu), 1600, 900);
    }

    private void initStartSavedScene() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        Label lbl = titleLabel("Start Saved Game", 60); 
        Button exitGame = new Button("Exit Game");
        styleButton(exitGame);

        exitGame.setOnAction(e -> primaryStage.close());

        ImageView bg = getBackgroundImage(); 

        menu.getChildren().addAll(lbl, exitGame);
        savedScene = new Scene(new StackPane(bg, menu), 1600, 900);
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
        scoreLbl.setStyle("-fx-text-fill: white;"); 

        int mins = (int)(elapsedMillis / 60000);
        int secs = (int)(elapsedMillis / 1000) % 60;
        Label timeLbl = new Label(String.format("Time: %02d:%02d", mins, secs));
        timeLbl.setFont(Font.font("Rock Salt", 40));
        timeLbl.setStyle("-fx-text-fill: white;"); 

        Button home = new Button("Home");
        styleButton(home);
        home.setOnAction(e -> goToStartScene());

        ImageView bg = getBackgroundImage(); 

        VBox vbox = new VBox(30, winLbl, scoreLbl, timeLbl, home);
        vbox.setAlignment(Pos.CENTER);
        winScene = new Scene(new StackPane(bg, vbox), 1600, 900);

        primaryStage.setScene(winScene);
    }

    // Injected by BaseGameScreen.
    public void setGameTimer(GameTimer t)    { this.gameTimer = t;   }
    public void setScoreboard(ScoreBoard b) { this.scoreboard = b;  }

    // Returns elapsed time for Win‑screen.
    public long getElapsedTime() {
        return (gameTimer != null) ? gameTimer.getElapsedTime() : 0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
