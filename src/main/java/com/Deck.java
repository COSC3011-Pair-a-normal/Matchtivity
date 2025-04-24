/**
 * Manages the “currentImages” folder for the game:
 * Copies a random half‑deck of images from the chosen category folder.
 * Cleans up old images on startup and shutdown.
 * Stub for custom‑deck API logic.
 */
package com;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.net.ssl.HttpsURLConnection;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class Deck {
    private static final String IMAGES_DIR = System.getProperty("user.dir")
        + "/src/main/resources/images/";
    private static final String CURRENT_DIR = IMAGES_DIR + "currentImages";
    private static Deck instance;

    private List<Card> matchedCards; // List to store matched cards.

    private Deck(String category, int cardCount) {
        matchedCards = new ArrayList<>(); // Initialize the matched cards list.
        createDirectory();               // Wipe old images / ensure folder.
        createDeck(category, cardCount); // Populate folder.
    }

    // Singleton accessor.
    public static Deck getInstance() {
        if (instance == null) {
            instance = new Deck(
                MainAppHolder.getDeckCategory(),
                MainAppHolder.getCardCount()
            );
        }
        return instance;
    }

    // Reset singleton so next startGame() recreates a fresh folder.
    public static void resetInstance() {
        instance = null;
    }

    public void addMatchedCard(Card card) { 
        matchedCards.add(card); // Add a card to the matched cards list
    }
    public List<Card> getMatchedCards() {
        return new ArrayList<>(matchedCards); // Returns a copy
    }

    // Wipe or create the currentImages folder and register shutdown cleanup.
    private void createDirectory() {
        File dir = new File(CURRENT_DIR);
        if (dir.exists()) {
            for (File f : dir.listFiles()) f.delete();
        } else {
            dir.mkdirs();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            deleteDirectory(dir);
        }));
    }

    // Recursively delete a directory.
    private static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File f : Optional.ofNullable(dir.listFiles()).orElse(new File[0])) {
                deleteDirectory(f);
            }
        }
        return dir.delete();
    }

    /**
     * Copy a random set of cardCount/2 images from IMAGES_DIR/category
     * into CURRENT_DIR.
     */
    private void createDeck(String category, int cardCount) {
        if ("custom".equals(category)) {
            createCustomDeck();
            return;
        }
        File srcDir = new File(IMAGES_DIR + category);
        File[] all = srcDir.listFiles();
        if (all == null || all.length < cardCount/2) {
            System.err.println("Not enough images in " + srcDir);
            return;
        }
        List<File> list = Arrays.asList(all);
        Collections.shuffle(list);
        list.subList(0, cardCount/2).forEach(f -> {
            try {
                Files.copy(f.toPath(), Paths.get(CURRENT_DIR, f.getName()),
                           StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Placeholder: fetch images via Google API for custom decks.
    private void createCustomDeck() {
        // TODO: implement Google Custom Search API download logic
    }
}
