package com;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.regex.*;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Deck {
    static final String IMAGES_DIR = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator;
    private static final String directoryPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "currentImages";
    private static Deck uniqueInstance;
    private Deck(String category, int cardCount) {
        //createCustomDeck();
        createDirectory();
        createDeck(category, cardCount);
    }
    public static Deck getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Deck(Main.getCategory(), Main.getCardCount());
        }
        return uniqueInstance;
    }

    private void createDirectory() {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean directoryCreated = directory.mkdirs();
            if (directoryCreated) {
                System.out.println("Directory created successfully at: " + directoryPath);
            } else {
                System.out.println("Failed to create directory at: " + directoryPath);
            }
        }

        // Add a shutdown hook to delete the directory when the program ends
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (directory.exists()) {
                deleteDirectory(directory);
                System.out.println("Directory deleted successfully at: " + directoryPath);
            }
        }));
    }

    private static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            String[] entries = directory.list();
            if (entries != null) {
                for (String entry : entries) {
                    File currentFile = new File(directory, entry);
                    deleteDirectory(currentFile);  // Recursively delete the contents
                }
            }
        }
        return directory.delete();  // Delete the directory itself
    }

    private void createDeck(String category, int cardCount) {
        if (category.equals("custom")) {
            createCustomDeck();
        } else {
            File directory = new File(IMAGES_DIR + category.toString());             
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();

                if (files != null && files.length >= cardCount / 2) {
                    List<File> fileList = new ArrayList<>();
                    Collections.addAll(fileList, files);

                    // Shuffle the list to randomize the order of files
                    Collections.shuffle(fileList, new Random());

                    // Get the first half of random files
                    List<File> randomFiles = fileList.subList(0, cardCount / 2);

                    // Loop through the files to copy them and create the mapping
                    for (int i = 0; i < randomFiles.size(); i++) {
                        File file = randomFiles.get(i);
                        System.out.println(file.getAbsolutePath());
                        
                        // Define the destination directory where you want to copy the file
                        String destinationDirectoryPath = directoryPath;
                        File destinationDirectory = new File(destinationDirectoryPath);

                        // Ensure the destination directory exists
                        if (!destinationDirectory.exists()) {
                            destinationDirectory.mkdirs(); // Create the directory if it doesn't exist
                        }

                        // Copy the file to the new directory
                        try {
                            Path sourcePath = file.toPath();
                            Path destinationPath = new File(destinationDirectory, file.getName()).toPath();

                            // Copy the file to the destination directory
                            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("File copied to: " + destinationPath.toString());

                            // Create the cardIDtoImageID mapping: card ID -> image ID
                            //cardIDtoImageID.add(i);  // Add the card ID to the list
                        } catch (IOException e) {
                            System.out.println("Error copying the file: " + e.getMessage());
                        }
                    }

                    // Optional: Shuffle cardIDtoImageID to randomize the image IDs
                    //Collections.shuffle(cardIDtoImageID);
                    //System.out.println("Shuffled card IDs: " + cardIDtoImageID);
                } else {
                    System.out.println("Not enough image files in the directory.");
                }
            } else {
                System.out.println("Directory does not exist.");
            }
        }
    }

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
        
    private void createCustomDeck() {
        final String API_KEY = "AIzaSyDAKvfkw-UHaaGaA9EX8gyzwW3MFSSsFKE";
        final String CX = "d647c54e6eecb440b"; // Replace with your Custom Search Engine ID
        final String QUERY = "classic movie poster";
        final int totalNeeded = 2 * Main.getCardCount(); // Total images needed
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

                // If fewer items returned than requested, break (no more results)
                if (items.length() < batchSize) break;
            }

            // Make sure directory exists
            Files.createDirectories(Paths.get(directoryPath));

            // Shuffle and download
            List<String> imageUrls = new ArrayList<>(uniqueUrls);
            Collections.shuffle(imageUrls);

            for (int i = 0; i < imageUrls.size(); i++) {
                String imageUrl = imageUrls.get(i);
                String extension = getFileExtension(imageUrl);
                String filename = "image_" + i + extension;
                try {
                    downloadImage(imageUrl, directoryPath + File.separator + filename);
                    System.out.println("Downloaded: " + filename);
                } catch (IOException e) {
                    System.out.println("Failed to download: " + imageUrl);
                }
            }

            // Cleanup on exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                deleteDirectory(new File(directoryPath));
                System.out.println("Directory deleted successfully at: " + directoryPath);
            }));

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
}


