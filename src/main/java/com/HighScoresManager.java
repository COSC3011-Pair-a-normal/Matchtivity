package com;

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class HighScoresManager {
    private static final Path FILE =
      Paths.get(System.getProperty("user.home"), ".matchtivity_hs");
    private final List<HighScoreEntry> entries = new ArrayList<>();
    private static HighScoresManager instance;

    private HighScoresManager() { load(); }
    public static synchronized HighScoresManager getInstance() {
        if (instance == null) instance = new HighScoresManager();
        return instance;
    }

    private void load() {
        if (!Files.exists(FILE)) return;
        try (BufferedReader r = Files.newBufferedReader(FILE)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(",", 3);
                entries.add(new HighScoreEntry(
                  parts[0],
                  Integer.parseInt(parts[1]),
                  parts[2]
                ));
            }
            Collections.sort(entries);
        } catch (IOException|NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (BufferedWriter w = Files.newBufferedWriter(FILE)) {
            for (var e : entries) {
                w.write(e.getName() + "," + e.getScore() + "," + e.getMode());
                w.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<HighScoreEntry> getTop10() {
        return entries.stream().limit(10).toList();
    }

    public void add(String name, int score, String mode) {
        entries.add(new HighScoreEntry(name, score, mode));
        Collections.sort(entries);
        if (entries.size() > 10) entries.subList(10, entries.size()).clear();
        save();
    }
}
