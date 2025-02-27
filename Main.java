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

        JLabel label = new JLabel("[under construction]", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 60)); 
        label.setOpaque(true); 
        label.setBackground(Color.white); 

        JPanel buttonPanel = new JPanel(); 
        buttonPanel.setBackground(Color.white); 

        startNew = new JButton("Start New Game"); 
        startSaved = new JButton("Start Saved Game"); 

        //for clicking the button 
        startNew.addActionListener(this); 

        //add buttons to panel 
        buttonPanel.add(startNew);
        buttonPanel.add(startSaved);  

        frame.add(label, BorderLayout.NORTH); 
        frame.add(buttonPanel, BorderLayout.CENTER);  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setSize(200,200); 
        frame.setVisible(true); 
    }

    public void actionPerformed(ActionEvent event) { 
        //change to connect with choose difficulty
        startNew.setText("I've been clicked!"); 
    }
}
