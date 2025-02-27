import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Create a new JFrame (window)
        JFrame frame = new JFrame("Pair-a-normal Matchtivity");

        // Set the default close operation so the program will exit when the window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("[under construction]", SwingConstants.CENTER);

        // Add the label to the frame
        frame.add(label);

        // Set the size of the window
        frame.setSize(1000, 1000);

        // Make the window visible
        frame.setVisible(true);
    }
}
