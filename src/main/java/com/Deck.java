package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private static final String directoryPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "currentImages";
    private static Deck uniqueInstance;
    private Deck(String category, int cardCount) {
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
        String directoryPath = System.getProperty("user.dir") + File.separator 
                               + "src" + File.separator + "main" + File.separator 
                               + "resources" + File.separator + "images" + File.separator;
        File directory = new File(directoryPath  + "classic");
        
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
                    String destinationDirectoryPath = directoryPath + "currentImages";
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
