/**
 * Represents a single card:
 * holds its image,
 * knows face‑up/face‑down status,
 * and whether it’s been matched.
 */
package com;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean faceUp;
    private boolean matched;
    private Image image;

    public Card(String suit, String value, String imagePath) throws IOException {
        faceUp = false;
        matched = false;
        image = ImageIO.read(new File(imagePath));
    }

    public boolean isFaceUp()   { return faceUp; }
    public boolean isMatched()  { return matched; }
    public Image getImage()     { return image; }

    public void setMatched(boolean m) { matched = m; }
    public void flipFaceUp()          { faceUp = true;  }
    public void flipFaceDown()        { faceUp = false; }

    // Compare by image equality.
    public boolean isSameCard(Card other) {
        return this.image.equals(other.image);
    }
}
