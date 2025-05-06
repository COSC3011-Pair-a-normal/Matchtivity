/*
 * Main JavaFX application:
 * Shows Start → Difficulty → Category screens
 * Launches the chosen game screen (Easy/Medium/Hard)
 * Provides a Home button in-game to return to Start
 */
package com;


import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; 
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Objects;
import java.nio.file.Path;
import java.util.ArrayList;
import java.net.URL;
import javafx.application.Platform;




public class MainApp extends Application {
    private Stage primaryStage;
    private Scene startScene, difficultyScene, categoryScene, winScene, savedScene; 
    private final int easyCount = 10, mediumCount = 18, hardCount = 30;
    private int cardCount;
    private String deckCategory;
    private GameTimer gameTimer;
    private ScoreBoard scoreboard;
    private Scene loadingScene;
    private GameState loadedGameState;
    private Scene highScoresScene;

    public static Font ROCK_SALT_FONT;
    public static Font ROCK_SALT_SMALL;


    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Pair‑A‑Normal Matchtivity");

        loadFont();            // Preload Rock Salt font.
        initStartScene();      // Build the Start screen.
        initDifficultyScene(); // Build Difficulty screen.
        initCategoryScene();   // Build Category screen.
        initStartSavedScene(); // Buid Start Saved screen. 
        initHighScoresScene(); // Build High Scores screen.
        initLoadingScene();

        stage.setScene(startScene);
        stage.show();
    }

    // Preload the custom font for use in every screen.
    private void loadFont() {
        if (ROCK_SALT_FONT != null && ROCK_SALT_SMALL != null) return;
    
        URL fontUrl = getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf");
    
        if (fontUrl != null) {
            ROCK_SALT_FONT = Font.loadFont(fontUrl.toExternalForm(), 30.0);
            ROCK_SALT_SMALL = Font.loadFont(fontUrl.toExternalForm(), 16.0);
    
            if (ROCK_SALT_FONT != null && ROCK_SALT_SMALL != null) {
                System.out.println("Rock Salt font loaded successfully.");
            } else {
                System.err.println("Rock Salt font URL found but failed to register.");
                loadFallbackFonts();
            }
        } else {
            System.err.println("Rock Salt font file not found in /fonts/Rock_Salt/");
            loadFallbackFonts();
        }
    }

    private void loadFallbackFonts() {
        ROCK_SALT_FONT = Font.font("System", 30.0);
        ROCK_SALT_SMALL = Font.font("System", 16.0);
    }
    
    // Helper to create a title Label with Rock Salt font.
    private Label titleLabel(String text, double size) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Rock Salt", size));
        lbl.getStyleClass().add("label");
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
        Button highScoresBtn = new Button("High Scores");
        Button exitGame   = new Button("Exit Game");
        styleButton(startNew);
        styleButton(startSaved);
        styleButton(highScoresBtn);
        styleButton(exitGame);

        startNew.setOnAction(e -> primaryStage.setScene(difficultyScene));
        startSaved.setOnAction(e -> primaryStage.setScene(savedScene));
        highScoresBtn.setOnAction(e -> {
            initHighScoresScene();
            primaryStage.setScene(highScoresScene);
          });
        exitGame.setOnAction(e -> primaryStage.close());

        Image backgroundImage = new Image(getClass().getResource("/images/purpleBackground.jpg").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(
                    100, 100, true, true, false, true
            )
        );

        menu.getChildren().addAll(startNew, startSaved, highScoresBtn, exitGame);
        BorderPane root = new BorderPane(menu);
        root.setTop(titleLabel("Pair‑A‑Normal Matchtivity", 60));
        root.setBackground(new Background(bgImage));
        BorderPane.setAlignment(root.getTop(), Pos.CENTER);

        startScene = new Scene(root, 1600, 900);
        startScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
        startScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
    }

    // Build high scores screen.
    private void initHighScoresScene() {
        Font rockSaltLarge  = Font.font("Rock Salt", 60);
        Font rockSaltMedium = Font.font("Rock Salt", 20);

        Label title = titleLabel("High Scores", 60);
        title.setFont(rockSaltLarge);

        VBox list = new VBox(10);
        list.setAlignment(Pos.CENTER);
      
        var top10 = HighScoresManager.getInstance().getTop10();
        if (top10.isEmpty()) {
        Label noScores = new Label("No high scores yet.");
        noScores.setFont(rockSaltMedium);
        list.getChildren().add(noScores);
        } else {
        for (HighScoreEntry e : top10) {
            Label row = new Label(
            e.getName() + " — " + e.getScore() + " — " + e.getMode()
            );
            row.setFont(rockSaltMedium);
            list.getChildren().add(row);
        }
        }
      
        Button back = new Button("Back");
        styleButton(back);
        back.setOnAction(evt -> primaryStage.setScene(startScene));
      
        VBox root = new VBox(30, title, list, back);
        root.setAlignment(Pos.CENTER);
        highScoresScene = new Scene(
          new StackPane(getBackgroundImage(), root),
          1600, 900
        );
        highScoresScene.getStylesheets().add(
          getClass().getResource("/style.css").toExternalForm()
        );
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
        difficultyScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
        difficultyScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
    }

    private void initCategoryScene() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        Label lbl = titleLabel("Choose Your Category", 60);
        Button reg    = new Button("Regular Deck");
        Button color  = new Button("Color Deck");
        Button themed = new Button("Themed Deck");

        // Create custom input area
        HBox customBox = new HBox(10);
        customBox.setAlignment(Pos.CENTER);
        Label customLabel = new Label("Custom:");
        customLabel.setFont(Font.font("Rock Salt", 30));
        //customLabel.getStyleClass().add("label"); 
        TextField customField = new TextField();
        customField.setPrefWidth(400);
        customField.setFont(Font.font("Rock Salt", 20));
        Button startCustom = new Button("Start Custom");
        startCustom.setFont(Font.font("Rock Salt", 24));

        customBox.getChildren().addAll(customLabel, customField, startCustom);

        // Style buttons
        styleButton(reg);
        styleButton(color);
        styleButton(themed);
        styleButton(startCustom);

        // Button actions
        reg.setOnAction(e -> { deckCategory = "regular"; startGame(); });
        color.setOnAction(e -> { deckCategory = "color"; startGame(); });
        themed.setOnAction(e -> { deckCategory = "themed"; startGame(); });

        startCustom.setOnAction(e -> {
            String customInput = customField.getText().trim();
            if (!customInput.isEmpty()) {
                deckCategory = customInput;
                startGame();
            }
        });

        // Background
        ImageView bg = getBackgroundImage();

        // Build menu
        menu.getChildren().addAll(lbl, reg, color, themed, customBox);
        categoryScene = new Scene(new StackPane(bg, menu), 1600, 900);
        categoryScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
        categoryScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
    }


    private void initStartSavedScene() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
    
        Label lbl = titleLabel("Start Saved Game", 60);
    
        Button loadSavedGame = new Button("Load Save");
        Button backToMenu    = new Button("Back to Menu");
        Button exitGame      = new Button("Exit Game");
    
        styleButton(loadSavedGame);
        styleButton(backToMenu);
        styleButton(exitGame);
    
        loadSavedGame.setOnAction(e -> loadGame());
        backToMenu.setOnAction(e -> {
            Deck.resetInstance();
            primaryStage.setScene(startScene); }
        );
        exitGame.setOnAction(e -> primaryStage.close());
    
        ImageView bg = getBackgroundImage();
    
        menu.getChildren().addAll(lbl, loadSavedGame, backToMenu, exitGame);
        savedScene = new Scene(new StackPane(bg, menu), 1600, 900);
        savedScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
        savedScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
    }
    


    private void initLoadingScene() {
        VBox loadingBox = new VBox(20);
        loadingBox.setAlignment(Pos.CENTER);

        Label loadingLabel = new Label("Loading...");
        loadingLabel.setFont(Font.font("Rock Salt", 40));
        loadingLabel.getStyleClass().add("label"); 

        ProgressIndicator spinner = new ProgressIndicator();

        loadingBox.getChildren().addAll(loadingLabel, spinner);
        loadingScene = new Scene(new StackPane(getBackgroundImage(), loadingBox), 1600, 900);
        loadingScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
        loadingScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 
    }

    /**
     * Launch the chosen game screen.
     * Resets the previous deck, passes params via MainAppHolder,
     * and instantiates Easy/Medium/Hard.
     */
    private void startGame() {
        //Deck.resetInstance();
        MainAppHolder.setParams(cardCount, deckCategory);

        boolean isCustom = !List.of("regular", "color", "themed").contains(deckCategory.toLowerCase());

        if (isCustom) {
            primaryStage.setScene(loadingScene); // Show loading only for custom decks

            Task<Void> loadDeckTask = new Task<>() {
                @Override
                protected Void call() {
                    Deck.getInstance(); // This will use the Google API
                    return null;
                }
            };

            loadDeckTask.setOnSucceeded(e -> {
                loadGameScene(); // Continue to the game
            });

            loadDeckTask.setOnFailed(e -> {
                Throwable ex = loadDeckTask.getException();
                ex.printStackTrace();
            });

            new Thread(loadDeckTask).start();

        } else {
            Deck.getInstance(); // Loads local deck instantly
            loadGameScene();    // Skip loading screen
        }

    }

    private void loadGameScene() {
        loadFont();
        if      (cardCount == easyCount)   new EasyGameScreen(this, primaryStage);
        else if (cardCount == mediumCount) new MediumGameScreen(this, primaryStage);
        else if (cardCount == hardCount)   new HardGameScreen(this, primaryStage);
    }

    private void loadSavedGameData(File selectedFile) {
        System.out.println("Loading game from: " + selectedFile.getAbsolutePath());
    
        try (FileInputStream fileIn = new FileInputStream(selectedFile);
            ObjectInputStream in = new ObjectInputStream(fileIn)) {
    
            GameState gameState = (GameState) in.readObject();
            this.cardCount = gameState.getCardCount();
            this.deckCategory = gameState.getDeckCategory();
            this.loadedGameState = gameState;
    
            File currentDir = new File("src/main/resources/images/currentImages");
            if (!currentDir.exists()) currentDir.mkdirs();
            for (File f : currentDir.listFiles()) f.delete();
            for (Map.Entry<String, byte[]> entry : gameState.getImageDataMap().entrySet()) {
                Path path = Paths.get(currentDir.getPath(), entry.getKey());
                Files.write(path, entry.getValue());
            }

            System.out.println("Game state loaded successfully.");
    

            MainAppHolder.setParams(cardCount, deckCategory);
            Deck.setSavedImagesMode(true);
            Deck.getInstance();
            for (int i : loadedGameState.matchedCards) {
                Deck.getInstance().addMatchedCard(i);
            }
            Deck.setSavedImagesMode(false);

            loadGameScene();
            Platform.runLater(() -> {
                applyLoadedGameState();
            });
    
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed to load the game: " + e.getMessage());
        }
    }

    
    public void applyLoadedGameState() {
        if (loadedGameState != null) {
            gameTimer.setElapsedTime(loadedGameState.getElapsedTime());
            ScoreBoard.getScoreBoard(ROCK_SALT_SMALL).setScore(loadedGameState.getScore());
            GameController.setCardMap(loadedGameState.cardMap);
        }
    }

    // Called by the MenuButton or Win‑screen to return home.
    public void goToStartScene() {
        if (scoreboard != null) scoreboard.clearScore();  // Reset between rounds.
        Deck.resetInstance();
        GameController.setCardMap(null);
        primaryStage.setScene(startScene);
    }

    /**
     * Show the Win screen:
     * Displays final score & time,
     * offers a Home button (instead of Exit),
     * and stops the in‑game timer.
     */
    public void showWinScene(int finalScore, long elapsedMillis, String mode) {
        Deck.resetInstance();
        Label winLbl   = titleLabel("YOU WIN!", 60);
        Label scoreLbl = new Label("Final Score: " + finalScore);
        scoreLbl.setFont(Font.font("Rock Salt", 40));
        //scoreLbl.getStyleClass().add("label"); 
        
        int mins = (int)(elapsedMillis / 60000);
        int secs = (int)(elapsedMillis / 1000) % 60;
        Label timeLbl = new Label(String.format("Time: %02d:%02d", mins, secs));
        timeLbl.setFont(Font.font("Rock Salt", 40));
        //timeLbl.getStyleClass().add("label"); 
        //timeLbl.getStyleClass().add("label"); 

        Button home = new Button("Home");
        styleButton(home);
        home.setOnAction(e -> goToStartScene());

        ImageView bg = getBackgroundImage(); 

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setMaxWidth(300);
        Button submit = new Button("Submit Score");
        submit.setOnAction(e -> {
            String name = nameField.getText().strip();
            if (!name.isEmpty()) {
                HighScoresManager.getInstance().add(name, finalScore, mode);
                initHighScoresScene();
                primaryStage.setScene(highScoresScene);
            }
        });

        VBox vbox = new VBox(30, winLbl, scoreLbl, timeLbl, nameField, submit, home);
        vbox.setAlignment(Pos.CENTER);
        winScene = new Scene(new StackPane(bg, vbox), 1600, 900);

        winScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); 

        primaryStage.setScene(winScene);
    }

    // Injected by BaseGameScreen.
    public void setGameTimer(GameTimer t)    { this.gameTimer = t;   }
    public void setScoreboard(ScoreBoard b) { this.scoreboard = b;  }

    // Returns elapsed time for Win‑screen.
    public long getElapsedTime() {
        return (gameTimer != null) ? gameTimer.getElapsedTime() : 0;
    }

    public static class GameState implements Serializable {
        private static final long serialVersionUID = 1L;
        private int score;
        private long elapsedTime;
        private ArrayList<Integer> matchedCards; // Card ID → Position
        private int cardCount;
        private String deckCategory;
        private Map<String, byte[]> imageDataMap;
        private List<Integer> cardMap;

        public GameState(long elapsedTime, int score, ArrayList<Integer> matchedCards, int cardCount, String deckCategory, Map<String, byte[]> imageDataMap, List<Integer> cardMap) {
            this.score = score;
            this.elapsedTime = elapsedTime;
            this.matchedCards = matchedCards;
            this.cardCount = cardCount;
            this.deckCategory = deckCategory;
            this.imageDataMap = imageDataMap;
            this.cardMap = cardMap;
        }

        public int getScore() {
            return score;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public ArrayList<Integer> getMatchedCards() {
            return matchedCards;
        }

        public int getCardCount() {
            return cardCount;
        }

        public String getDeckCategory() {
            return deckCategory;
        }

        public Map<String, byte[]> getImageDataMap() {
            return imageDataMap;
        }

        public List<Integer> getCardMap() {
            return cardMap;
        }
     }
     public void saveGame() {
        if (gameTimer == null || scoreboard == null) {
            System.out.println("Game state is incomplete. Cannot save.");
            return;
        }
    
        long elapsedTime = gameTimer.getElapsedTime();
        int score = scoreboard.getScore();
        ArrayList<Integer> matchedCards = Deck.getMatchedCards();
        List<Integer> cardMap = GameController.getCardMap();
    
        Map<String, byte[]> imageDataMap = new HashMap<>();
        File dir = new File("src/main/resources/saves");  // Save in saves directory

        if (!dir.exists()) {
            dir.mkdirs();
        }
    
        String saveFileName = "savegame_" + System.currentTimeMillis() + ".dat";  // Current time is used to make the file name unique
        File saveFile = new File(dir, saveFileName);
    
        File imageDir = new File("src/main/resources/images/currentImages");
        for (File file : Objects.requireNonNull(imageDir.listFiles())) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                imageDataMap.put(file.getName(), data);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to read image: " + file.getName());
            }
        }
    
        GameState gameState = new GameState(elapsedTime, score, matchedCards, cardCount, deckCategory, imageDataMap, cardMap);
    
        // Save the game state to the file
        try (FileOutputStream fileOut = new FileOutputStream(saveFile);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(gameState);
            System.out.println("Game saved successfully: " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the game: " + e.getMessage());
        }
    }
    

    public void loadGame() {
        // Create a FileChooser to select a save file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game Save Files", "*.dat"));
        fileChooser.setInitialDirectory(new File("src/main/resources/saves"));
    
        File selectedFile = fileChooser.showOpenDialog(primaryStage);  

        loadSavedGameData(selectedFile);
    }
    
    
    public static void main(String[] args) {
        System.out.println("RUNNING APPLICATION");
        launch(args);
    }
}

