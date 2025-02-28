// Implements a game timer panel using Swing that displays elapsed time in
// HH:MM:SS format. Timer starts automatically when the panel is created, and
// updates each second.

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Timer extends JPanel
{
    private JLabel timeLabel; // Display elapsed time HH:MM:SS.
    private long startTime; // Time in milli when timer was started.
    private javax.swing.Timer timer;

    public Timer()
    {
        // Create label starting at time 00:00:00 with center alignment.
        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        // Set custom font from main, using helper, at size 40.
        timeLabel.setFont(Main.getCustomFont(40f));
        this.setBackground(Color.white);
        // Add label to the panel.
        this.add(timeLabel);

        // Start timer to begin updating elapsed time.
        startTimer();
    }

    // Initializeds and starts the timer.
    private void startTimer()
    {
        startTime = System.currentTimeMillis();
        // Updates timer every 1000ms(1 second).
        timer = new javax.swing.Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateTimer();
            }
        });

        timer.start();
    }

    // Updates the timer with the elapsed time.
    private void updateTimer()
    {
        // Calculated elapsed time as current time - startTime.
        long elapsedTime = System.currentTimeMillis() - startTime;
        // Convert elapsed time to hours, minutes, and seconds.
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) (elapsedTime / 60000) % 60;
        int hours = (int) (elapsedTime / 3600000);
        // Update label with HH:MM:SS formated time.
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    // Stops the timer.
    public void stopTimer()
    {
        timer.stop();
    }

    // Need to impliment pause funtionality, adjusting start/stop/elapsed
    // to appropriately measure time with splits.
}