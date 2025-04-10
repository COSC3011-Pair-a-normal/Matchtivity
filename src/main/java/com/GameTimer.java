/*
Implements a JavaFX control that displays the time elapsed in HH:MM:SS format.
The time starts when the control is created, and then updates every second.

Usage example:

Within a JavaFX scene:
GameTimer gameTimer = new GameTimer();
// Alter x/y pos as needed.
gameTimer.setLayoutX(100);
gameTimer.setLayoutY(50);
pane.getChildren().add(gameTimer);

Call pauseTimer(), resumeTimer(), stopTimer() as needed to control timer
progress.

*/

package com;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GameTimer extends StackPane
{
    private Label timeLabel;
    private long startTime;
    private long elapsedGameTime = 0;
    private boolean isPaused = false;
    private Timeline timeline;

    public GameTimer()
    {
        // Create label starting at time 00:00:00.
        timeLabel = new Label("00:00:00");
        // Set custom font from main, using helper, at size 40.
        Font rockSalt = Font.loadFont(getClass().getResource
            ("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 40);
        timeLabel.setFont(rockSalt);

        // Center the label.
        // this.setAlignment(Pos.CENTER);
        // this.setStyle("-fx-background-color: white;");
        this.getChildren().add(timeLabel);

        // Start timer to begin updating elapsed time.
        startTimer();
    }

    // Initializeds and starts the timer.
    private void startTimer()
    {
        startTime = System.currentTimeMillis();
        // Create a Timeline that calls updateTimer() every second.
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Updates the timer with the elapsed time.
    private void updateTimer()
    {
        // Calculated elapsed time as current time - startTime.
        long elapsedTime = elapsedGameTime + (System.currentTimeMillis() - startTime);
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
        timeline.stop();
    }

    // Pauses the timer, calculates and stores the elapsed time.
    public void pauseTimer()
    {
        if(!isPaused)
        {
            elapsedGameTime += System.currentTimeMillis() - startTime;
            timeline.pause();
            isPaused = true;
        }
    }

    // Resumes the timer.
    public void resumeTimer()
    {
        if(isPaused)
        {
            startTime = System.currentTimeMillis();
            timeline.play();
            isPaused = false;
        }
    }

    public long getElapsedTime() {
        return elapsedGameTime + (System.currentTimeMillis() - startTime);
    }
}