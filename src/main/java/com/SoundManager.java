package com;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {
    public static void play(String fileName) {
        try {
            URL soundURL = SoundManager.class.getResource("/" + fileName);
            if (soundURL == null) {
                System.out.println("Sound file not found: " + fileName);
                return;
            }

            Media sound = new Media(soundURL.toString());
            MediaPlayer player = new MediaPlayer(sound);
            player.setOnError(() -> {
                System.out.println("MediaPlayer error: " + player.getError());
            });
            player.play();
        } catch (Exception e) {
            System.out.println("Could not play sound: " + fileName);
            e.printStackTrace();
        }
    }
}
