package com;

import javafx.geometry.Pos; 
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


        //styling the "panel"
        this.setSpacing(20); 
        this.setAlignment(Pos.CENTER); 
        this.getChildren().add(scoreLabel); 
    }

    //increase and clear methods 
    public void increaseScore() {
        score++; 
        scoreLabel.setText("Score: " + score); 
    }

    /*
    private void clearScore() {
        score = 0; 
        scoreLabel.setText("Score: " + score); 
    }
    */

}
