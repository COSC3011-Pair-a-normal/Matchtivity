package com;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class Deck {
    private static final String IMAGES_DIR = System.getProperty("user.dir")
        + "/src/main/resources/images/";
    private static final String CURRENT_DIR = IMAGES_DIR + "currentImages";
    private static Deck instance;
    private static ArrayList<Integer> matchedCards;
    private static boolean savedImagesMode = false;

    private Deck() {
        matchedCards = new ArrayList<Integer>();
    }

    private Deck(String category, int cardCount) {
        createDirectory();
        createDeck(category, cardCount);
        matchedCards = new ArrayList<>();
    }

    // Singleton accessor.
    public static Deck getInstance() {
        if (instance == null) {
            if (savedImagesMode) {
                instance = new Deck();
            } else {
                instance = new Deck(MainAppHolder.getDeckCategory(), MainAppHolder.getCardCount());
            }
        }
        return instance;
    }

    // Reset singleton so next startGame() recreates a fresh folder.
    public static void resetInstance() {
        instance = null;
    }

    // Add a matched card to the dynamic list
    public void addMatchedCard(int cardId) {
        matchedCards.add(cardId);  // Dynamically add the cardId
    }

    // Get the matched cards as an ArrayList
    public static ArrayList<Integer> getMatchedCards() {
        return new ArrayList<>(matchedCards); // Return a copy of the list
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

    // Copy a random set of cardCount/2 images from IMAGES_DIR/category
    private void createDeck(String category, int cardCount) {
        if (!Arrays.asList("regular", "color", "themed").contains(category)) {
            createCustomDeck(category, cardCount);
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

    // Download an image from a URL
    private static void downloadImage(String imageUrl, String savePath) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.connect();
    
        String contentType = connection.getContentType();
        if (contentType != null && contentType.startsWith("image")) {
            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, Paths.get(savePath), StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            System.out.println("Skipped non-image content: " + imageUrl + " (" + contentType + ")");
            throw new IOException("Not an image: " + contentType);
        }
    }

    // Custom deck logic for downloading images from Google API
    private void createCustomDeck(String category, int cardCount) {
        final String API_KEY = "AIzaSyDAKvfkw-UHaaGaA9EX8gyzwW3MFSSsFKE";
        final String CX = "d647c54e6eecb440b";
        final String QUERY = category;
        final int totalNeeded = 2 * cardCount;
        final int batchSize = 10;
        Set<String> uniqueUrls = new LinkedHashSet<>();

        try {
            int startIndex = 1;

            while (uniqueUrls.size() < totalNeeded) {
                String urlStr = "https://www.googleapis.com/customsearch/v1?q=" + URLEncoder.encode(QUERY, "UTF-8")
                        + "&searchType=image"
                        + "&num=" + Math.min(batchSize, totalNeeded - uniqueUrls.size())
                        + "&start=" + startIndex
                        + "&key=" + API_KEY
                        + "&cx=" + CX;

                URL url = new URL(urlStr);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder raw = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) raw.append(line);
                reader.close();

                JSONObject responseJson = new JSONObject(raw.toString());
                JSONArray items = responseJson.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    String imageUrl = items.getJSONObject(i).getString("link");
                    uniqueUrls.add(imageUrl);
                    if (uniqueUrls.size() >= totalNeeded) break;
                }
                startIndex += batchSize;
                if (items.length() < batchSize) break;
            }

            Files.createDirectories(Paths.get(CURRENT_DIR));
            List<String> imageUrls = new ArrayList<>(uniqueUrls);
            Collections.shuffle(imageUrls);

            for (int i = 0; i < imageUrls.size(); i++) {
                String imageUrl = imageUrls.get(i);
                String extension = getFileExtension(imageUrl);
                String filename = "image_" + i + extension;
                try {
                    downloadImage(imageUrl, CURRENT_DIR + File.separator + filename);
                    System.out.println("Downloaded: " + filename);
                } catch (IOException e) {
                    System.out.println("Failed to download: " + imageUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileExtension(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.endsWith(".png")) return ".png";
        if (lowerUrl.endsWith(".jpeg")) return ".jpeg";
        if (lowerUrl.endsWith(".jpg")) return ".jpg";
        return ".jpg"; // default fallback
    }

    public static void setSavedImagesMode(boolean mode) {
        savedImagesMode = mode;
    }
}
