package com;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

import java.util.Set;
import java.io.*;
import java.util.HashSet;

import javax.swing.*;
import java.awt.*; 
import java.awt.event.*;
import java.net.URL;

public class Main implements ActionListener {
    // Buttons
    private JButton startNew, startSaved, exitGame;
    private JButton easyButton, mediumButton, hardButton;
    private JButton regDeck, colorDeck, themedDeck, customDeck;
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private boolean timerStarted = false; //Ensure only one timer created 
    private boolean isPaused = false; //Tracks paused status 
    private GameTimer gameTimer; //Game timer reference
    public static ScoreBoard scoreboard; //ScoreBoard object 
    public static Font rockSaltFont; //Game timer reference
    private static int cardCount; // Number of cards based on users choice of difficulty
    private int easyCount = 10;
    private int mediumCount = 18;
    private int hardCount = 30;
    private static String deckCategory; //stores the deck
    private static Main instance; // Singleton for access

    
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

    public static Main getInstance() {
        return instance;
    }
    

    public void initializeUI() {
        // Create the main frame
        instance = this;

        frame = new JFrame("Pair-A-Normal Matchtivity"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLocationRelativeTo(null);

        // Set up CardLayout for switching screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add different screens
        JPanel startScreen = createStartScreen();
        JPanel loadSavedScreen = createLoadSavedScreen(); 
        JPanel difficultyScreen = createDifficultyScreen();
        JPanel categoryScreen = createCategoryScreen();

        mainPanel.add(startScreen, "StartScreen");
        mainPanel.add(loadSavedScreen, "LoadSavedScreen"); 
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
        startNew.setFocusPainted(false);
        startNew.setPreferredSize(new Dimension(400, 100));
        startNew.setFont(getCustomFont(30f));
        startNew.addActionListener(this); // Will call actionPerformed()

        // Other Buttons
        startSaved = new JButton("Start Saved Game");
        startSaved.setFocusPainted(false);
        startSaved.setPreferredSize(new Dimension(400, 100));
        startSaved.setFont(getCustomFont(30f));
        startSaved.addActionListener(this); 

        exitGame = new JButton("Exit Game");
        exitGame.setFocusPainted(false);
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

    private JPanel createLoadSavedScreen() {
        JPanel loadSavedScreen = new JPanel(new GridBagLayout());
        loadSavedScreen.setBackground(Color.white);

        JLabel loadSavedLabel = new JLabel("Choose Game:", SwingConstants.CENTER);
        loadSavedLabel.setFont(getCustomFont(60f));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        //add in how to actually choose a saved game 

        // Add Components to Difficulty Screen
        
        loadSavedScreen.add(loadSavedLabel, gbc);
        /*
        gbc.gridy++; 
        difficultyScreen.add(easyButton, gbc);
        gbc.gridy++;
        difficultyScreen.add(mediumButton, gbc);
        gbc.gridy++;
        difficultyScreen.add(hardButton, gbc);
        */
        return loadSavedScreen;
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
        easyButton.setFocusPainted(false);
        easyButton.setPreferredSize(new Dimension(400, 100));
        easyButton.setFont(getCustomFont(30f));
        easyButton.addActionListener(this);

        mediumButton = new JButton("Medium");
        mediumButton.setFocusPainted(false);
        mediumButton.setPreferredSize(new Dimension(400, 100));
        mediumButton.setFont(getCustomFont(30f));
        mediumButton.addActionListener(this);

        hardButton = new JButton("Hard");
        hardButton.setFocusPainted(false);
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
        regDeck.setFocusPainted(false);
        regDeck.setPreferredSize(new Dimension(400, 100));
        regDeck.setFont(getCustomFont(30f));
        regDeck.addActionListener(this);
        
        colorDeck = new JButton("Color Deck");
        colorDeck.setFocusPainted(false);
        colorDeck.setPreferredSize(new Dimension(400, 100));
        colorDeck.setFont(getCustomFont(30f));
        colorDeck.addActionListener(this);

        themedDeck = new JButton("Themed Deck"); 
        themedDeck.setFocusPainted(false); 
        themedDeck.setPreferredSize(new Dimension(400, 100)); 
        themedDeck.setFont(getCustomFont(30f)); 
        themedDeck.addActionListener(this); 
        
        customDeck = new JButton("Custom Deck");
        customDeck.setFocusPainted(false);
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
        categoryScreen.add(themedDeck, gbc); 
        gbc.gridy++; 
        categoryScreen.add(customDeck, gbc);

        return categoryScreen;
    }

    @Override
    public void actionPerformed(ActionEvent event) { 
        if (event.getSource() == startNew) {
            cardLayout.show(mainPanel, "DifficultyScreen");
        } else if (event.getSource()  == startSaved) {
            cardLayout.show(mainPanel, "LoadSavedScreen"); 
        } else if (event.getSource() == easyButton) {
            cardCount = easyCount;
            cardLayout.show(mainPanel, "CategoryScreen"); // Move to category selection after choosing difficulty
        } else if (event.getSource() == mediumButton) {
            cardCount = mediumCount;
            cardLayout.show(mainPanel, "CategoryScreen"); // Move to category selection after choosing difficulty
        } else if (event.getSource() == hardButton) {
            cardCount = hardCount;
            cardLayout.show(mainPanel, "CategoryScreen"); // Move to category selection after choosing difficulty
        } else if (event.getSource() == regDeck) {
            deckCategory = "regular";
            startGame(cardCount, deckCategory);
        } else if (event.getSource() == colorDeck) {
            deckCategory = "color";
            startGame(cardCount, deckCategory);
        } else if (event.getSource() == themedDeck) {
            deckCategory = "themed"; 
            startGame(cardCount, deckCategory); 
        } else if (event.getSource() == customDeck) {
            deckCategory = "custom";
            startGame(cardCount, deckCategory);
        }
    }

    public void startGame(int cardCount, String deckCategory) {
        if (deckCategory.equals("regular")) {
            if (cardCount == easyCount) {
                new EasyGameScreen(frame);
            } else if (cardCount == mediumCount) {
                new MediumGameScreen(frame);
            } else if (cardCount == hardCount) {
                new HardGameScreen(frame);
            }
        } else if (deckCategory.equals("color")) {
            if (cardCount == easyCount) {
                new EasyGameScreen(frame);
            } else if (cardCount == mediumCount) {
                new MediumGameScreen(frame);
            } else if (cardCount == hardCount) {
                new HardGameScreen(frame);
            }
        } else if (deckCategory.equals("themed")) {
            if (cardCount == easyCount) {
                new EasyGameScreen(frame); 
            } else if (cardCount == mediumCount) {
                new MediumGameScreen(frame); 
            } else if (cardCount == hardCount) {
                new HardGameScreen(frame); 
            }
        } else if (deckCategory.equals("custom")) {
            if (cardCount == easyCount) {
                new EasyGameScreen(frame);
            } else if (cardCount == mediumCount) {
                new MediumGameScreen(frame);
            } else if (cardCount == hardCount) {
                new HardGameScreen(frame);
            }
        }
    }

    public static String getCategory() {
        return deckCategory;
    }
    public static int getCardCount() {
        return cardCount;
    }

    private void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("matchtivity.dat"))) {
            int score = scoreboard.getScore();//From ScoreBoard.java
            long elapsedTime = gameTimer.getElapsedTime(); // Assuming GameTimer has a getElapsedTime() method
            Set<Integer> matchedCards = new HashSet<>(); // Replace with actual matched cards logic

            GameState gameState = new GameState(score, elapsedTime, matchedCards);
            oos.writeObject(gameState);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void returnToSwingPanel() {
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(mainPanel);
            frame.revalidate();
            frame.repaint();
        });
    }
    

    public void showWinScreen() {
        SwingUtilities.invokeLater(() -> {
            JPanel winScreen = new JPanel(new BorderLayout());
            winScreen.setBackground(Color.white);
    
            JLabel winLabel = new JLabel("WOOOOOOO YOU WIN!", SwingConstants.CENTER);
            winLabel.setFont(getCustomFont(60f));
            winLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
            winScreen.add(winLabel, BorderLayout.CENTER);
    
            mainPanel.add(winScreen, "WinScreen");
            cardLayout.show(mainPanel, "WinScreen");

            System.out.println("🏁 Switching to win screen...");
System.out.println("mainPanel components: " + mainPanel.getComponentCount());
for (Component c : mainPanel.getComponents()) {
    System.out.println(" - " + c.getClass().getName());
}

    
            if (gameTimer != null) {
                gameTimer.stopTimer(); // Make sure this is stopTimer(), not stop()
            }
        });
    }
    

    public class GameState implements Serializable {
        private static final long serialVersionUID = 1L;
    
        private int score;
        private long elapsedTime; // Time in milliseconds
        private Set<Integer> matchedCards; // IDs of matched cards
    
        public GameState(int score, long elapsedTime, Set<Integer> matchedCards) {
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
        public Set<Integer> getMatchedCards() {
            return matchedCards;
        }
    }
}
