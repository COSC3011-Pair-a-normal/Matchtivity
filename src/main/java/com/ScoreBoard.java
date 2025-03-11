package com;

import javax.swing.*;
import java.awt.*; 
import java.awt.event.*; 

/* Initalize in Main with: 
 * scoreboard = new ScoreBoard(); 
 * screen.add(scoreboard); 
 */

public class ScoreBoard extends JPanel{
    private int score; 
    private int length = 100; 
    private int width = 50;  
    private JLabel display; 

    public ScoreBoard() {
        score = 0; 
        
        display = new JLabel("Score: " + this.score, JLabel.CENTER); 
        
        this.setSize(length, width); 
        this.setLayout(new BorderLayout()); 

        this.add(display, BorderLayout.CENTER); 

        //button to manually update score, change to automatic
        //when user matches two cards 
        JButton updateButton = new JButton("Update Score"); 
        this.add(updateButton, BorderLayout.SOUTH); 

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addScore(); 
            }
        }); 
    }

    public void addScore() {
        //increase score and update text 
        score++; 
        display.setText("Score: " + score); 
    }

    public void clear() {
        //reset score and update text 
        score = 0;
        display.setText("Score: " + score); 
    }

}
