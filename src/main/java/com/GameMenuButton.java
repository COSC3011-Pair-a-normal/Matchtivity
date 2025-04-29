/*
 * Implements a custom JavaFX ToggleButton that shows a ContextMenu
 * with Save, Restart, Home, and Exit options during gameplay.
 * Uses Labels as graphics so we can apply a custom font.
 *
 * Usage:
 * GameMenuButton menu = new GameMenuButton("Menu", rockSaltFont);
 * menu.setOnSave(e -> /* save logic *​/);
 * menu.setOnRestart(e -> /* restart logic *​/);
 * menu.setOnHome(e -> /* go home *​/);
 * menu.setOnExit(e -> /* exit logic *​/);
 * pane.getChildren().add(menu);
 */
package com;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class GameMenuButton extends ToggleButton {
    private MenuItem saveButton;
    private MenuItem restartButton;
    private MenuItem homeButton;
    private MenuItem exitButton;
    private final int buttonPadding = 22;
    private ContextMenu contextMenu;
    private MainApp mainApp; // Reference to MainApp instance.

    public GameMenuButton(String text, Font font, MainApp mainApp) {
        super(text);
        this.mainApp = mainApp;
        setFont(font);
        setPrefWidth(200);
        setStyle("-fx-background-radius: 50; -fx-border-radius: 50;"); 

        // Save item.
        Label saveLabel = new Label("Save");
        saveLabel.setFont(font);
        saveLabel.setAlignment(Pos.CENTER);
        saveLabel.prefWidthProperty().bind(widthProperty().subtract(buttonPadding));
        saveButton = new MenuItem();
        saveButton.setGraphic(saveLabel);

        // Restart item.
        Label restartLabel = new Label("Restart");
        restartLabel.setFont(font);
        restartLabel.setAlignment(Pos.CENTER);
        restartLabel.prefWidthProperty().bind(widthProperty().subtract(buttonPadding));
        restartButton = new MenuItem();
        restartButton.setGraphic(restartLabel);

        // Home item.
        Label homeLabel = new Label("Home");
        homeLabel.setFont(font);
        homeLabel.setAlignment(Pos.CENTER);
        homeLabel.prefWidthProperty().bind(widthProperty().subtract(buttonPadding));
        homeButton = new MenuItem();
        homeButton.setGraphic(homeLabel);

        // Exit item.
        Label exitLabel = new Label("Exit");
        exitLabel.setFont(font);
        exitLabel.setAlignment(Pos.CENTER);
        exitLabel.prefWidthProperty().bind(widthProperty().subtract(buttonPadding));
        exitButton = new MenuItem();
        exitButton.setGraphic(exitLabel);

        // Build context menu.
        contextMenu = new ContextMenu(saveButton, restartButton, homeButton, exitButton);
        contextMenu.setAutoHide(false);

        // Toggle menu visibility.
        setOnMouseClicked((MouseEvent e) -> {
            if (isSelected()) {
                contextMenu.show(this, Side.BOTTOM, 0, 0);
            } else {
                contextMenu.hide();
            }
        });
    }

    // Hook up handlers for each menu item.
    public void setOnSave(EventHandler<ActionEvent> h) {saveButton.setOnAction(e -> mainApp.saveGame());}
    public void setOnRestart(EventHandler<ActionEvent> h) { restartButton.setOnAction(h);}
    public void setOnHome(EventHandler<ActionEvent> h) {homeButton.setOnAction(h);}
    public void setOnExit(EventHandler<ActionEvent> h) {exitButton.setOnAction(h);}
}
