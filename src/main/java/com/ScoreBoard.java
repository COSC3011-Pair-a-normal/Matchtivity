/**
 * Singleton UI component displaying the current score.
 */
package com;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ScoreBoard extends VBox {
    private int score = 0;
    private Label scoreLabel;

    private static ScoreBoard instance;

    private ScoreBoard() {
        scoreLabel = new Label("Score: 0");
        scoreLabel.getStyleClass().add("label");
        setSpacing(20);
        setAlignment(Pos.CENTER);
        getChildren().add(scoreLabel);
    }

    //Get or create the single scoreboard, applying the given font.
    public static synchronized ScoreBoard getScoreBoard(Font font) {
        if (instance == null) {
            instance = new ScoreBoard();
            instance.scoreLabel.setFont(font);
        }
        Font f = (font != null)
            ? font
            : Font.font("System", 16);
        instance.scoreLabel.setFont(f);
        return instance;
    }

    // Increment the score display by one.
    public void increaseScore() {
        score++;
        scoreLabel.setText("Score: " + score);
    }

    // Reset the score to zero.
    public void clearScore() {
        score = 0;
        scoreLabel.setText("Score: 0");
    }

    public int getScore() { return score; }

    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText("Score: " + score);
    }
}
