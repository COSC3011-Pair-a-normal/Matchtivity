package com;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

import javax.swing.*;
import java.awt.*; 
import java.awt.event.*; 
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main implements ActionListener {
    // Buttons
    private JButton startNew, startSaved, exitGame;
    private JButton easyButton, mediumButton, hardButton;
    private JButton regDeck, colorDeck, customDeck;
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private boolean timerStarted = false; //Ensure only one timer created 
    private boolean isPaused = false; //Tracks paused status 
    private GameTimer gameTimer; //Game timer reference
    public static ScoreBoard scoreboard; //ScoreBoard object 
    public static Font rockSaltFont; //Game timer reference
    
    private JFXPanel jfxPanel;

    public static void main(String[] args) {

        initializeJavaFX();

        // Load custom font
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT,
                new File("src\\main\\resources\\fonts\\Rock_Salt\\RockSalt-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            rockSaltFont = customFont;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new Main().initializeUI());
    }
    private static void initializeJavaFX() {
        new JFXPanel();  // Creates the JavaFX runtime environment
    }

    public static Font getCustomFont(float size) {
        return (rockSaltFont != null) ? rockSaltFont.deriveFont(size) : new Font("Serif", Font.PLAIN, (int) size);
    }

    public void initializeUI() {
        // Create the main frame
        frame = new JFrame("Pair-A-Normal Matchtivity"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLocationRelativeTo(null);

        // Set up CardLayout for switching screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add different screens
        JPanel startScreen = createStartScreen();
        JPanel difficultyScreen = createDifficultyScreen();
        JPanel categoryScreen = createCategoryScreen();

        mainPanel.add(startScreen, "StartScreen");
        mainPanel.add(difficultyScreen, "DifficultyScreen");
        mainPanel.add(categoryScreen, "CategoryScreen");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createStartScreen() {
        JPanel startScreen = new JPanel(new GridBagLayout());
        startScreen.setBackground(Color.white);
        
        JLabel titleLabel = new JLabel("Pair-A-Normal Matchtivity", SwingConstants.CENTER);
        titleLabel.setFont(getCustomFont(60f));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.white);

        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.anchor = GridBagConstraints.CENTER;

        // "Start New Game" Button (Switches to Difficulty Screen)
        startNew = new JButton("Start New Game"); 
        startNew.setPreferredSize(new Dimension(400, 100));
        startNew.setFont(getCustomFont(30f));
        startNew.addActionListener(this); // Will call actionPerformed()

        // Other Buttons
        startSaved = new JButton("Start Saved Game");
        startSaved.setFocusPainted(false);
        startSaved.setPreferredSize(new Dimension(400, 100));
        startSaved.setFont(getCustomFont(30f));

        exitGame = new JButton("Exit Game");
        exitGame.setPreferredSize(new Dimension(400, 100));
        exitGame.setFont(getCustomFont(30f));
        exitGame.addActionListener(e -> System.exit(0)); // Exit the game

        // Add Buttons to Start Screen
        startScreen.add(startNew, gbc);
        gbc.gridy++;
        startScreen.add(startSaved, gbc);
        gbc.gridy++;
        startScreen.add(exitGame, gbc);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(startScreen, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDifficultyScreen() {
        JPanel difficultyScreen = new JPanel(new GridBagLayout());
        difficultyScreen.setBackground(Color.white);

        JLabel difficultyLabel = new JLabel("Choose Difficulty", SwingConstants.CENTER);
        difficultyLabel.setFont(getCustomFont(60f));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Difficulty Buttons
        easyButton = new JButton("Easy");
        easyButton.setPreferredSize(new Dimension(400, 100));
        easyButton.setFont(getCustomFont(30f));
        easyButton.addActionListener(this);

        mediumButton = new JButton("Medium");
        mediumButton.setPreferredSize(new Dimension(400, 100));
        mediumButton.setFont(getCustomFont(30f));
        mediumButton.addActionListener(this);

        hardButton = new JButton("Hard");
        hardButton.setPreferredSize(new Dimension(400, 100));
        hardButton.setFont(getCustomFont(30f));
        hardButton.addActionListener(this);

        // Add Components to Difficulty Screen
        
        difficultyScreen.add(difficultyLabel, gbc);
        gbc.gridy++; 
        difficultyScreen.add(easyButton, gbc);
        gbc.gridy++;
        difficultyScreen.add(mediumButton, gbc);
        gbc.gridy++;
        difficultyScreen.add(hardButton, gbc);

        return difficultyScreen;
    }

    private JPanel createCategoryScreen() {
        JPanel categoryScreen = new JPanel(new GridBagLayout());
        categoryScreen.setBackground(Color.white);

        JLabel categoryLabel = new JLabel("Choose Your Category", SwingConstants.CENTER);
        categoryLabel.setFont(getCustomFont(60f));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Category Buttons
        regDeck = new JButton("Regular Deck");
        regDeck.setPreferredSize(new Dimension(400, 100));
        regDeck.setFont(getCustomFont(30f));
        regDeck.addActionListener(this);
        
        colorDeck = new JButton("Color Deck");
        colorDeck.setPreferredSize(new Dimension(400, 100));
        colorDeck.setFont(getCustomFont(30f));
        colorDeck.addActionListener(this);
        
        customDeck = new JButton("Custom Deck");
        customDeck.setPreferredSize(new Dimension(400, 100));
        customDeck.setFont(getCustomFont(30f));
        customDeck.addActionListener(this);

        // Add Components to category Screen
        
        categoryScreen.add(categoryLabel, gbc);
        gbc.gridy++; 
        categoryScreen.add(regDeck, gbc);
        gbc.gridy++;
        categoryScreen.add(colorDeck, gbc);
        gbc.gridy++;
        categoryScreen.add(customDeck, gbc);

        return categoryScreen;
    }

    @Override
    public void actionPerformed(ActionEvent event) { 
        if (event.getSource() == startNew) {
            cardLayout.show(mainPanel, "DifficultyScreen");
        } else if (event.getSource() == easyButton || event.getSource() == mediumButton || event.getSource() == hardButton) {
            cardLayout.show(mainPanel, "CategoryScreen"); // Move to category selection after choosing difficulty
        } else if (event.getSource() == regDeck) {
            EasyGameScreen(); // Modify this to use difficulty selection
        } else if (event.getSource() == colorDeck) {
            MediumGameScreen();
        } else if (event.getSource() == customDeck) {
            HardGameScreen();
        }
    }

            /*
            // Create and add timer once.
            if(!timerStarted)
            {
                // Create the timer panel.
                timerStarted = true;
            isPaused = false;
            gameTimer = new GameTimer();

                // Calculate initial timer position.
                int frameWidth = frame.getWidth();
                int frameHeight = frame.getHeight();
                int timerWidth = 300;
                int timerHeight = 100;

                int timerXPos = (int) (frameWidth * 0.95) - timerWidth; // 10% margin from right.
                int timerYPos = (int) (frameHeight * 0.05); // 5% margin from top.

                gameTimer.setBounds(timerXPos, timerYPos, timerWidth, timerHeight);

                // Add timer panel to frame's layed pane for relative positioning.
                frame.getLayeredPane().add(gameTimer, JLayeredPane.POPUP_LAYER);

                // Add a listener that repositions the timer if the frame is resized.
            frame.addComponentListener(new ComponentAdapter()
                {
                    @Override
                    public void componentResized(ComponentEvent e)
                    {
                        // Generate new positioning.
                        int newFrameWidth = frame.getWidth();
                        int newFrameHeight = frame.getHeight();
                        int newXPos = (int) (newFrameWidth * 0.95) - timerWidth;
                        int newYPos = (int) (newFrameHeight * 0.05);
                        gameTimer.setBounds(newXPos, newYPos, timerWidth, timerHeight);
                    }  
            });

                // Update the frame layout.
                frame.revalidate();
                frame.repaint();

            // Update start button text.
            startNew.setText("Press to pause");
        }
        else
        {
            if(!isPaused)
            {
                gameTimer.pauseTimer();
                isPaused = true;
                startNew.setText("Press to resume");
            }
            else
            {
                gameTimer.resumeTimer();
                isPaused = false;
                startNew.setText("Press to pause");
            }
        }
 
        }
    private void showJavaFXGameScreen() {
        // Initialize the JavaFX thread
        Platform.runLater(() -> {
            // Create a JavaFX Scene
            StackPane gamePane = new StackPane();
            Button startButton = new Button("Start Game");

            // Set up the action for the start button
            startButton.setOnAction(event -> {
                // Game logic for starting the game goes here
                System.out.println("Game Started with selected difficulty!");
            });

            gamePane.getChildren().add(startButton);
            Scene scene = new Scene(gamePane, 1600, 900);

            // Set up the JavaFX scene in the JFXPanel
            jfxPanel = new JFXPanel();
            jfxPanel.setScene(scene);

            // Replace the content of the frame with JavaFX content
            frame.getContentPane().removeAll();
            frame.getContentPane().add(jfxPanel);
            frame.revalidate();
            frame.repaint();
        });
    }
*/
    /*
    private void EasyGameScreen() {
        // Initialize the JavaFX thread
        Platform.runLater(() -> {
            // Create a JavaFX Scene
            StackPane gamePane = new StackPane();
            Button startButton = new Button("Easy Game: under construction");

            // Set up the action for the start button
            startButton.setOnAction(event -> {
                // Game logic for starting the game goes here
                System.out.println("Game Started with selected difficulty!");
            });

            gamePane.getChildren().add(startButton);
            Scene scene = new Scene(gamePane, 1600, 900);

            // Set up the JavaFX scene in the JFXPanel
            jfxPanel = new JFXPanel();
            jfxPanel.setScene(scene);

            // Replace the content of the frame with JavaFX content
            frame.getContentPane().removeAll();
            frame.getContentPane().add(jfxPanel);
            frame.revalidate();
            frame.repaint();
        });
    }
        */
        private void EasyGameScreen() {
            // Initialize the JavaFX thread
            Platform.runLater(() -> {
                try {
                    // Ensure the path to test.fxml is correct
                    URL fxmlUrl = getClass().getResource("/easy.fxml");
                    if (fxmlUrl == null) {
                        throw new IllegalStateException("FXML file not found: /easy.fxml");
                    }
        
                    // Create an FXMLLoader object to load the FXML file
                    FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        
                    // Load the FXML file into the root node
                    Parent root = fxmlLoader.load();

                    if(root instanceof javafx.scene.layout.Pane)
                    {
                        javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) root;

                        
                        GameTimer gameTimer = new GameTimer();
                        gameTimer.setLayoutX(1300);
                        gameTimer.setLayoutY(5);
                        pane.getChildren().add(gameTimer);
                        

                        // Load the font.
                        javafx.scene.text.Font rockSalt = javafx.scene.text.Font.loadFont(
                            getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 30.0);

                        

                        // Create the GameMenuButton.
                        GameMenuButton menuButton = new GameMenuButton("Menu", rockSalt);

                        // Position the button.
                        menuButton.setLayoutX(50);
                        menuButton.setLayoutY(50);

                        menuButton.setOnSave(event ->
                        {
                            // Insert save logic here.
                            System.out.println("Game saved! That's creamy and dreamy!");
                        });

                        menuButton.setOnExit(event ->
                        {
                            // Insert exit logic here.
                            System.exit(0);
                        });

                        // Add the menu button to the scene.
                        pane.getChildren().add(menuButton);  


                        //create the scoreboard 
                        //load a smaller size of the font 
                        javafx.scene.text.Font rockSaltSmall = javafx.scene.text.Font.loadFont(
                            getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 16.0);
                        
                        scoreboard = new ScoreBoard(rockSaltSmall); 
                        
                        scoreboard.setLayoutX(50);
                        scoreboard.setLayoutY(400); 
                        
                        pane.getChildren().add(scoreboard);
                    }
        
                    // Create the JavaFX scene with the loaded FXML
                    Scene scene = new Scene(root, 1600, 900);
        
                    // Set up the JavaFX scene in the JFXPanel
                    jfxPanel = new JFXPanel();
                    jfxPanel.setScene(scene);

        
                    // Replace the content of the frame with JavaFX content
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(jfxPanel);
                    frame.revalidate();
                    frame.repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace(); // Log the exception if the FXML is not found
                }
            });
        }
        
        private void MediumGameScreen() {
            // Initialize the JavaFX thread
            Platform.runLater(() -> {
                try {
                    // Ensure the path to test.fxml is correct
                    URL fxmlUrl = getClass().getResource("/medium.fxml");
                    if (fxmlUrl == null) {
                        throw new IllegalStateException("FXML file not found: /medium.fxml");
                    }
        
                    // Create an FXMLLoader object to load the FXML file
                    FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        
                    // Load the FXML file into the root node
                    Parent root = fxmlLoader.load();

                    if(root instanceof javafx.scene.layout.Pane)
                    {
                        javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) root;

                        
                        GameTimer gameTimer = new GameTimer();
                        gameTimer.setLayoutX(1300);
                        gameTimer.setLayoutY(5);
                        pane.getChildren().add(gameTimer);
                        

                        // Load the font.
                        javafx.scene.text.Font rockSalt = javafx.scene.text.Font.loadFont(
                            getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 30.0);

                        //load a smaller size of the font 
                        javafx.scene.text.Font rockSaltSmall = javafx.scene.text.Font.loadFont(
                            getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 16.0);

                        // Create the GameMenuButton.
                        GameMenuButton menuButton = new GameMenuButton("Menu", rockSalt);

                        // Position the button.
                        menuButton.setLayoutX(50);
                        menuButton.setLayoutY(50);

                        menuButton.setOnSave(event ->
                        {
                            // Insert save logic here.
                            System.out.println("Game saved! That's creamy and dreamy!");
                        });

                        menuButton.setOnExit(event ->
                        {
                            // Insert exit logic here.
                            System.exit(0);
                        });

                        // Add the menu button to the scene.
                        pane.getChildren().add(menuButton); 

                        scoreboard = new ScoreBoard(rockSaltSmall); 

                        scoreboard.setLayoutX(50);
                        scoreboard.setLayoutY(400); 

                        pane.getChildren().add(scoreboard); 
                    }
        
                    // Create the JavaFX scene with the loaded FXML
                    Scene scene = new Scene(root, 1600, 900);
        
                    // Set up the JavaFX scene in the JFXPanel
                    jfxPanel = new JFXPanel();
                    jfxPanel.setScene(scene);

        
                    // Replace the content of the frame with JavaFX content
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(jfxPanel);
                    frame.revalidate();
                    frame.repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace(); // Log the exception if the FXML is not found
                }
            });
        }
    /*  
    private void MediumGameScreen() {
        // Initialize the JavaFX thread
        Platform.runLater(() -> {
            // Create a JavaFX Scene
            StackPane gamePane = new StackPane();
            Button startButton = new Button("Medium Game: under construction");

            // Set up the action for the start button
            startButton.setOnAction(event -> {
                // Game logic for starting the game goes here
                //System.out.println("Game Started with selected difficulty!");
            });

            gamePane.getChildren().add(startButton);
            Scene scene = new Scene(gamePane, 1600, 900);

            // Set up the JavaFX scene in the JFXPanel
            jfxPanel = new JFXPanel();
            jfxPanel.setScene(scene);

            
            GameTimer gameTimer = new GameTimer();
            gameTimer.setLayoutX(100);
            gameTimer.setLayoutY(50);
            gamePane.getChildren().add(gameTimer);
            

            // Replace the content of the frame with JavaFX content
            frame.getContentPane().removeAll();
            frame.getContentPane().add(jfxPanel);
            frame.revalidate();
            frame.repaint();
        });
    }
    */

  private void HardGameScreen() {
        // Initialize the JavaFX thread
        Platform.runLater(() -> {
            try {
                // Load hard_regular.fxml (30 cards, regular deck)
                URL fxmlUrl = getClass().getResource("/hard.fxml");
                if (fxmlUrl == null) {
                    throw new IllegalStateException("FXML file not found: /hard_regular.fxml");
                }
    
                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
                Parent root = fxmlLoader.load();
    
                if (root instanceof javafx.scene.layout.Pane) {
                    javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) root;
    
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
    
                    // Menu button
                    GameMenuButton menuButton = new GameMenuButton("Menu", rockSalt);
                    menuButton.setLayoutX(50);
                    menuButton.setLayoutY(50);
                    menuButton.setOnSave(event -> {
                        System.out.println("Hard game saved!");
                    });
                    menuButton.setOnExit(event -> {
                        System.exit(0);
                    });
                    pane.getChildren().add(menuButton);
    
                    // Scoreboard
                    scoreboard = new ScoreBoard(rockSaltSmall);
                    scoreboard.setLayoutX(50);
                    scoreboard.setLayoutY(400);
                    pane.getChildren().add(scoreboard);
                }
    
                // Set the scene
                Scene scene = new Scene(root, 1600, 900);
                jfxPanel = new JFXPanel();
                jfxPanel.setScene(scene);
    
                frame.getContentPane().removeAll();
                frame.getContentPane().add(jfxPanel);
                frame.revalidate();
                frame.repaint();
    
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
        });
    }
}
