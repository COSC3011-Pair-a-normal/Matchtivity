package com;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Card {
    private boolean faceUp; // Whether the card is face-up (revealed) or face-down (hidden)
    private boolean matched;
    private Image image;   // The image of the card

    // Constructor to create a card with a specific suit, value, and image
    public Card(String suit, String value, String imagePath) throws IOException {
        this.faceUp = false; // Cards start face-down by default
        this.image = loadImage(imagePath); // Load the image from a file path
        this.matched = false;
    }

    // Method to load an image from a file path
    private Image loadImage(String imagePath) throws IOException {
        return ImageIO.read(new File(imagePath)); // Load image from the file system
    }

    // Getter for the faceUp status
    public boolean isFaceUp() {
        return faceUp;
    }

    // Getter for the image
    public Image getImage() {
        return image;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isSameCard(Card othercard) {
        return this.image.equals(othercard.getImage());
    }

    // Method to turn the card face up
    public void flipFaceUp() {
        this.faceUp = true;
    }

    // Method to turn the card face down
    public void flipFaceDown() {
        this.faceUp = false;
    }
}
