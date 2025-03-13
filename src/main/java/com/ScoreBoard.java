package com;

import javafx.geometry.Pos; 
import javafx.scene.control.Button;
import javafx.scene.control.Label; 
import javafx.scene.layout.VBox;  
import javafx.scene.text.Font;

public class ScoreBoard extends VBox{
    private int score = 0;
    private Label scoreLabel; 

    public ScoreBoard(Font font) {
        //initalize label for the score 
        scoreLabel = new Label ("Score: " + score); 
        scoreLabel.setFont(font); 

        //buttons for increasing and clearing score, 
        //update when clicked 
        //eventually change with game functionality
        Button increaseScore = new Button("Increase Score"); 
        increaseScore.setFont(font); 
        increaseScore.setOnAction(event -> increaseScore()); 

        Button clearScore = new Button("Clear Score");  
        clearScore.setFont(font); 
        clearScore.setOnAction(event -> clearScore()); 

        //styling the "panel"
        this.setSpacing(20); 
        this.setAlignment(Pos.CENTER); 
        this.getChildren().addAll(scoreLabel, increaseScore, clearScore); 
    }

    //increase and clear methods 
    private void increaseScore() {
        score++; 
        scoreLabel.setText("Score: " + score); 
    }

    private void clearScore() {
        score = 0; 
        scoreLabel.setText("Score: " + score); 
    }

}
