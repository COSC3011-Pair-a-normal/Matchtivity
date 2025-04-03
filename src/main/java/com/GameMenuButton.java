/*
Impliments a JavaFX MenuButton dropdown menu with the options to save and exit.
This menu is intended for use during the gameplay loop to provide the user
quick access to these options.

The buttons can be styled with a custom font. JavaFX doesn't support font change
directly, so I used Labels as graphics to display the text.


Usage example:

if(root instanceof javafx.scene.layout.Pane)
{
    javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) root;

    // Load the font.
    javafx.scene.text.Font rockSalt = javafx.scene.text.Font.loadFont(
        getClass().getResource("/fonts/Rock_Salt/RockSalt-Regular.ttf").toExternalForm(), 30.0);

    // Create the GameMenuButton.
    GameMenuButton menuButton = new GameMenuButton("Menu", rockSalt);

    // Position the button in the scene.
    menuButton.setLayoutX(50);
    menuButton.setLayoutY(50);

    menuButton.setOnSave(event ->
    {
        // Insert save logic here.
        System.out.println("Game saved!");
    });

    menuButton.setOnExit(event ->
    {
        // Insert exit logic here.
        System.exit(0);
    });

    // Add the menu button to the scene.
    pane.getChildren().add(menuButton);
}
*/

package com;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;

public class GameMenuButton extends MenuButton
{
    private MenuItem saveButton;
    private MenuItem restartButton; 
    private MenuItem exitButton;
    private int buttonPadding = 22;

    public GameMenuButton(String text, Font font)
    {
        super(text);
        setFont(font);                          // Apply the font to the MenuButton.

        Label saveLabel = new Label("Save");    // Create a Label for saveButton.
        saveLabel.setFont(font);                // Set saveButton Label font.
        saveLabel.setAlignment(Pos.CENTER);     // Center the save graphic.
        // Set/bind the width of the saveButton to equal that of the MenuButton.
        saveLabel.prefWidthProperty().bind(this.widthProperty().subtract(buttonPadding));
        saveButton = new MenuItem();            // Create saveButton MenuItem.
        saveButton.setGraphic(saveLabel);       // Apply svaeLabel graphic to saveButton.

        Label restartLabel = new Label("Restart");    //restart button label 
        restartLabel.setFont(font);                
        restartLabel.setAlignment(Pos.CENTER);     
        // Set/bind the width of the restartButton to equal that of the MenuButton.
        restartLabel.prefWidthProperty().bind(this.widthProperty().subtract(buttonPadding));
        restartButton = new MenuItem();            
        restartButton.setGraphic(restartLabel);       

        Label exitLabel = new Label("Exit");    // Create a Label for exitButton.
        exitLabel.setFont(font);                // Set exitButton Label font.
        exitLabel.setAlignment(Pos.CENTER);    // Center the exit graphic.
        // Set/bind the width of the exitButton to equal that of the MenuButton.
        exitLabel.prefWidthProperty().bind(this.widthProperty().subtract(buttonPadding));
        exitButton = new MenuItem();            // Create exitButton MenuItem.
        exitButton.setGraphic(exitLabel);       // Apply exitLabel graphic to exitButton.
        
        // Add the menu items, saveButton and exitButton, to the MenuButton.
        this.getItems().addAll(saveButton, restartButton, exitButton);

        this.addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
        {
            if(this.isShowing())
            {
                this.hide();
                event.consume();
            }
        });
    }

    // Set the action for the saveButton MenuItem.
    public void setOnSave(EventHandler<ActionEvent> handler)
    {
        saveButton.setOnAction(handler);
    }

    public void setOnRestart(EventHandler<ActionEvent> handler)
    {
        restartButton.setOnAction(handler); 
    }

    // Set the action for the exitButton MenuItem.
    public void setOnExit(EventHandler<ActionEvent> handler)
    {
        exitButton.setOnAction(handler);
    }

    // Close the menu if already open, instead of reopening it.
    @Override
    public void fire()
    {
        if (isShowing())
        {
            hide();
        }
        else
        {
            super.fire();
        }
    }
}
