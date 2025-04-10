package com;

import javafx.geometry.Pos; 
import javafx.scene.control.Label; 
import javafx.scene.layout.VBox;  
import javafx.scene.text.Font;

public class ScoreBoard extends VBox{
    private int score = 0;
    private Label scoreLabel; 

    private static ScoreBoard uniqueScoreBoard; 

    private ScoreBoard() {
        scoreLabel = new Label("Score: " + score); 
        this.setSpacing(20); 
        this.setAlignment(Pos.CENTER); 
        this.getChildren().add(scoreLabel); 
    }

    public static synchronized ScoreBoard getScoreBoard(Font font) {
        if (uniqueScoreBoard == null) {
            uniqueScoreBoard = new ScoreBoard(); 
            uniqueScoreBoard.scoreLabel.setFont(font); 
        }

        return uniqueScoreBoard; 
    }

    //increase and clear methods 
    public void increaseScore() {
        score++; 
        scoreLabel.setText("Score: " + score); 
    }

    
    public void clearScore() {
        score = 0; 
        scoreLabel.setText("Score: " + score); 
    }

    public int getScore() {
        return score; 
    }

}
