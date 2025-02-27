import javax.swing.*;
import java.awt.*; 
import java.awt.event.*; 

public class Main implements ActionListener{
    //create buttons 
    private JButton startNew; 
    private JButton startSaved;

    public static void main(String[] args) {  
        Main gui = new Main(); 
        gui.go(); 
    }

    public void go() {
        //create new frame and page title
        JFrame frame = new JFrame("Pair-A-Normal Matchtivity"); 

        //create header label 
        JLabel label = new JLabel("[under construction]", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 60)); 
        label.setOpaque(true); 
        label.setBackground(Color.white); 

        JPanel frontScreen = new JPanel(new GridBagLayout()); 
        frontScreen.setBackground(Color.white);
        
        //layout to directly choose where components go 
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(10, 10, 10, 10); 

        //buttons and set size 
        startNew = new JButton("Start New Game"); 
        startNew.setPreferredSize(new Dimension(400, 100));
        startSaved = new JButton("Start Saved Game"); 
        startSaved.setPreferredSize(new Dimension(400,100));

        //for clicking the button 
        startNew.addActionListener(this); 

        //add 
        frontScreen.add(startNew, gbc); 
        gbc.gridy++; 
        frontScreen.add(startSaved, gbc); 

        frame.setLayout(new BorderLayout()); 
        frame.add(label, BorderLayout.NORTH); 
        frame.add(frontScreen, BorderLayout.CENTER);  
        frame.pack(); 
        frame.setLocationRelativeTo(null); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setSize(1600,900); 
        frame.setVisible(true); 
    }

    //on button click 
    public void actionPerformed(ActionEvent event) { 
        //change to connect with choose difficulty
        startNew.setText("I've been clicked!"); 
    }
}
