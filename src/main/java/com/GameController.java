package com;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class GameController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private FlowPane imagesFlowPane;

    private List<ImageView> flippedCards = new ArrayList<>();
    private boolean processingCards = false;

    // Path to the directory you want to create and later delete
    private static final String directoryPath = System.getProperty("user.dir") + File.separator 
        + "src" + File.separator + "main" + File.separator + "resources" + File.separator 
        + "images" + File.separator + "currentImages";

    // Image resource
    public static final Image backImage = new Image("/images/back_of_card.png");

    // this needs to be generalized depending on the size of the game, but for right now it is 10 cards for easy mode => 5 pairs => 5 images
    // 0,1,2,3,4 are the unique imageID's, and each imageID has two cards that correspond to a matching pair
    public List<Integer> cardIDtoImageID = new ArrayList<>(List.of(0,1,2,3,4,0,1,2,3,4));
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeImageView();
        createDirectory();
        createDeck();
         // This List maps the cardID number to the imageID number. for example, if cardIDtoImageID.get(3) returns a value of 1, this means the 4th card has the second image as its card face.
    // As a result, if two cardID's map to the same imageID, they are a match. if cardIDtoImageID.get(i) == cardIDtoImageID.get(j) returns true, cards i and j are a match.
    // this needs to be generalized for different sized games, but for now it is 10 cards for easy mode

        

        System.out.println("controller is working");
    }

    // This method will initialize the image view and set the back of the card
    private void initializeImageView() {
        for (int i = 0; i < imagesFlowPane.getChildren().size(); i++) {
            ImageView imageView = (ImageView) imagesFlowPane.getChildren().get(i);
            imageView.setImage(backImage);  // Set back image for each card

            imageView.setUserData(i); // Each card has an identification number (0, 1, 2, ...)

            // Add mouse click event for card flipping
            imageView.setOnMouseClicked(event -> {
                //only allow two cards to be flipped at a time 
                if (!processingCards && flippedCards.size() < 2) {
                    flipCard((int) imageView.getUserData());    
                }
            });
        }
    }

    // Create the directory for storing images if it doesn't already exist
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

    // Helper method to delete the directory and its contents
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

    // Flip the card (showing the front image and then flipping back to the back)
    private void flipCard(int cardID) {
        ImageView imageView = (ImageView) imagesFlowPane.getChildren().get(cardID);
        Image image;
    
        if (imageView.getImage() == backImage) {
            // Get the image corresponding to the current card ID from the shuffled cardIDtoImageID array
            Integer imageID = cardIDtoImageID.get(cardID);
            String directoryPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "currentImages";
            File directory = new File(directoryPath);
        
            // Check if the directory exists and is a directory
            if (directory.exists() && directory.isDirectory()) {
                // List all the files in the directory (filtering for image files)
                File[] files = directory.listFiles();
                File frontImageFile = files[imageID];
                // Create the Image object from the file URI
                image = new Image(frontImageFile.toURI().toString());
                flippedCards.add(imageView);
                System.out.println("Flipped: " + flippedCards.size()); 
            } else {
                image = backImage;  // Flip it back to the back image
            }
        } else {
            image = backImage;
            flippedCards.remove(imageView); 
            System.out.println("Reversed: " + flippedCards.size());
        }
    
        // Update the image on the imageView
        imageView.setImage(image);

        if (flippedCards.size() == 2) {
            processingCards = true; 
            processFlippedCards(); 
        }
    }

    private void processFlippedCards() {
        ImageView firstCard = flippedCards.get(0); 
        ImageView secondCard = flippedCards.get(1);

        // matching logic using the cardIDtoImageID mapping
        boolean cardsMatch = cardIDtoImageID.get((int) firstCard.getUserData()) == cardIDtoImageID.get((int) secondCard.getUserData());

        if (cardsMatch) {
            System.out.println("Match!");
            //increase score
            // currently the cards just stay flipped once they are matched, a matchedCards list will probably be necessary for saving the game
            flippedCards.clear();
            processingCards = false;
        } else {
            System.out.println("No match!");
            
            //delay for 1.5 seconds to see cards 
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

            pause.setOnFinished(event -> {
                //reset the flipped cards 
                for (ImageView card : flippedCards) {
                    card.setImage(backImage);
                }

                flippedCards.clear();
                processingCards = false;
            });

            //pause
            pause.play();
            }

    }

    // makes the randomized deck of cards stored in the currentImages directory,
    // and shuffles the cardIDtoImageID mapping
    private void createDeck() {
        String directoryPath = System.getProperty("user.dir") + File.separator 
                               + "src" + File.separator + "main" + File.separator 
                               + "resources" + File.separator + "images" + File.separator + "classic";
        File directory = new File(directoryPath);
        
         // Check if the directory exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            // List all the files in the directory (filtering for image files)
            File[] files = directory.listFiles();

            // Check if there are enough files in the directory
            if (files != null && files.length >= 5) {
                // Convert array to list for easier manipulation
                List<File> fileList = new ArrayList<>();
                Collections.addAll(fileList, files);

                // Shuffle the list to randomize the order of files
                Collections.shuffle(fileList, new Random());

                // Get the first 5 random files
                List<File> randomFiles = fileList.subList(0, 5);

                System.out.println("Selected 5 random files:");
                for (File file : randomFiles) {
                    System.out.println(file.getAbsolutePath());
                    
                    // Define the destination directory where you want to copy the file
                    String destinationDirectoryPath = System.getProperty("user.dir") + File.separator 
                                                      + "src" + File.separator + "main" + File.separator 
                                                      + "resources" + File.separator + "images" + File.separator 
                                                      + "currentImages";
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
                    } catch (IOException e) {
                        System.out.println("Error copying the file: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Not enough image files in the directory.");
            }
        } else {
            System.out.println("Directory does not exist.");
        }
        Collections.shuffle(cardIDtoImageID);
    }
    
}
