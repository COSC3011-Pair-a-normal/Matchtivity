import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Main implements ActionListener {
    // Buttons
    private JButton startNew, startSaved, exitGame;
    private JButton easyButton, mediumButton, hardButton;
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    public static Font rockSaltFont;

    public static void main(String[] args) {
        // Load custom font
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT,
                    new File("resources/fonts/Rock_Salt/RockSalt-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            rockSaltFont = customFont;
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Main().initializeUI());
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

        // 🔹 Set up CardLayout for switching screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 🔹 Add different screens
        JPanel startScreen = createStartScreen();
        JPanel difficultyScreen = createDifficultyScreen();

        mainPanel.add(startScreen, "StartScreen");
        mainPanel.add(difficultyScreen, "DifficultyScreen");

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

        // 🔹 "Start New Game" Button (Switches to Difficulty Screen)
        startNew = new JButton("Start New Game");
        startNew.setPreferredSize(new Dimension(400, 100));
        startNew.setFont(getCustomFont(30f));
        startNew.addActionListener(this); // Will call actionPerformed()

        // 🔹 Other Buttons
        startSaved = new JButton("Start Saved Game");
        startSaved.setPreferredSize(new Dimension(400, 100));
        startSaved.setFont(getCustomFont(30f));

        exitGame = new JButton("Exit Game");
        exitGame.setPreferredSize(new Dimension(400, 100));
        exitGame.setFont(getCustomFont(30f));
        exitGame.addActionListener(e -> System.exit(0)); // Exit the game

        // 🔹 Add Buttons to Start Screen
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

        // 🔹 Difficulty Buttons
        easyButton = new JButton("Easy");
        easyButton.setPreferredSize(new Dimension(400, 100));
        easyButton.setFont(getCustomFont(30f));

        mediumButton = new JButton("Medium");
        mediumButton.setPreferredSize(new Dimension(400, 100));
        mediumButton.setFont(getCustomFont(30f));

        hardButton = new JButton("Hard");
        hardButton.setPreferredSize(new Dimension(400, 100));
        hardButton.setFont(getCustomFont(30f));

        // 🔹 Add Components to Difficulty Screen
        
        difficultyScreen.add(difficultyLabel, gbc);
        gbc.gridy++;
        difficultyScreen.add(easyButton, gbc);
        gbc.gridy++;
        difficultyScreen.add(mediumButton, gbc);
        gbc.gridy++;
        difficultyScreen.add(hardButton, gbc);

        return difficultyScreen;
    }

    
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == startNew) {
            
            cardLayout.show(mainPanel, "DifficultyScreen"); // switches screens to difficulty screen 
        }
    }
}
