import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Card {
    private boolean faceUp; // Whether the card is face-up (revealed) or face-down (hidden)
    private Image image;   // The image of the card

    // Constructor to create a card with a specific suit, value, and image
    public Card(String suit, String value, String imagePath) throws IOException {
        this.faceUp = false; // Cards start face-down by default
        this.image = loadImage(imagePath); // Load the image from a file path
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

    // Method to turn the card face up
    public void flipFaceUp() {
        this.faceUp = true;
    }

    // Method to turn the card face down
    public void flipFaceDown() {
        this.faceUp = false;
    }
/*
    // Method to display the card (useful for debugging or printing)
    public void displayCard() {
        if (faceUp) {
            System.out.println(value + " of " + suit); // Display value and suit when face-up
        } else {
            System.out.println("[Face Down]"); // Indicate the card is face-down
        }
    }
 
    // Override the toString() method to customize how the card is displayed
    @Override
    public String toString() {
        return (faceUp) ? value + " of " + suit : "[Face Down]";
    }
*/
    // Method to check if two cards are the same (based on suit and value)
    public boolean matches(Card other) {
        return this.image.equals(other.image);
    }
}
