/*
 * A custom JavaFX control displaying elapsed time in HH:MM:SS.
 * Starts timing when instantiated.
 * Updates every second.
 * Supports pause/resume/stop.
 *
 * Usage:
 * GameTimer timer = new GameTimer();
 * pane.getChildren().add(timer);
 * ...
 * timer.pauseTimer();
 * timer.resumeTimer();
 * timer.stopTimer();
 */
package com;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GameTimer extends StackPane {
    private final Label timeLabel;
    private long startTime;
    private long elapsedGameTime = 0;
    private boolean isPaused = false;
    private Timeline timeline;

    public GameTimer() {
        // Initialize label to 00:00:00.
        timeLabel = new Label("00:00:00");
        timeLabel.getStyleClass().add("label");
        Font rockSalt = Font.loadFont(
            getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 40);
        timeLabel.setFont(rockSalt);

        getChildren().add(timeLabel);
        startTimer();
    }

    // Start or restart the timeline.
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Recalculate elapsed time and update the label.
    private void updateTimer() {
        long elapsed = elapsedGameTime + (System.currentTimeMillis() - startTime);
        int hrs = (int)(elapsed / 3600000);
        int mins = (int)(elapsed / 60000) % 60;
        int secs = (int)(elapsed / 1000) % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", hrs, mins, secs));
    }

    // Stop the timer completely.
    public void stopTimer() {
        timeline.stop();
    }

    // Pause the timer, preserving elapsed time.
    public void pauseTimer() {
        if (!isPaused) {
            elapsedGameTime += System.currentTimeMillis() - startTime;
            timeline.pause();
            isPaused = true;
        }
    }

    // Resume from a paused state.
    public void resumeTimer() {
        if (isPaused) {
            startTime = System.currentTimeMillis();
            timeline.play();
            isPaused = false;
        }
    }

    // Get total elapsed time in milliseconds.
    public long getElapsedTime() {
        return elapsedGameTime + (System.currentTimeMillis() - startTime);
    }
}
