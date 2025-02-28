import javax.swing.*;
import java.awt.*; 
import java.awt.event.*; 
import java.io.File;

public class Main implements ActionListener {
    // create buttons 
    private JButton startNew; 
    private JButton startSaved;
    private JButton exitGame;
    private JFrame frame;
    private boolean timerStarted = false; // Ensure only one timer is created.
    public static Font rockSaltFont;

    public static void main(String[] args) {
        
        // Load the custom font, and set it globally.
        try
        {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT,
                new File("resources/fonts/Rock_Salt/RockSalt-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            rockSaltFont = customFont;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        Main gui = new Main(); 
        gui.go(); 
    }

    // Helper method to derive custom font at specific size.
    public static Font getCustomFont(float size)
    {
        if(rockSaltFont != null)
        {
            return rockSaltFont.deriveFont(size);
        }
        else
        {
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }

    public void go() {
        // create new frame and page title
        frame = new JFrame("Pair-A-Normal Matchtivity"); 

        // create header label 
        JLabel label = new JLabel("Pair-A-Normal Matchtivity", SwingConstants.CENTER);
        label.setFont(getCustomFont((60f))); 
        label.setOpaque(true); 
        label.setBackground(Color.white); 

        JPanel frontScreen = new JPanel(new GridBagLayout()); 
        frontScreen.setBackground(Color.white);
        
        // layout to directly choose where components go 
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(10, 10, 10, 10); 

        // buttons and set size 
        startNew = new JButton("Start New Game"); 
        startNew.setPreferredSize(new Dimension(400, 100));
        startNew.setFont(getCustomFont(30f));

        startSaved = new JButton("Start Saved Game"); 
        startSaved.setPreferredSize(new Dimension(400,100));
        startSaved.setFont(getCustomFont(30f));

        exitGame = new JButton("Exit Game");
        exitGame.setPreferredSize(new Dimension(400, 100));
        exitGame.setFont(getCustomFont(30f));

        // add action listeners 
        startNew.addActionListener(this); 
        exitGame.addActionListener(e -> System.exit(0)); // exit game on click

        // add buttons to the panel 
        frontScreen.add(startNew, gbc); 
        gbc.gridy++; 
        frontScreen.add(startSaved, gbc); 
        gbc.gridy++;
        frontScreen.add(exitGame, gbc);

        frame.setLayout(new BorderLayout()); 
        frame.add(label, BorderLayout.NORTH); 
        frame.add(frontScreen, BorderLayout.CENTER);  
        frame.pack(); 
        frame.setLocationRelativeTo(null); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setSize(1600,900); 
        frame.setVisible(true); 
    }

    // on button click 
    public void actionPerformed(ActionEvent event) { 
        // change to connect with choose difficulty
        startNew.setText("I've been clicked!"); 

        // Create and add timer once.
        if(!timerStarted)
        {
            // Create the timer panel.
            timerStarted = true;
            Timer gameTimer = new Timer();

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
            frame.addComponentListener((new ComponentAdapter()
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
            }));

            // Update the frame layout.
            frame.revalidate();
            frame.repaint();
        }
    }
}
