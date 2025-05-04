package com;

public class HighScoreEntry implements Comparable<HighScoreEntry> {
    private final String name;
    private final int    score;
    private final String mode;

    public HighScoreEntry(String name, int score, String mode) {
        this.name  = name;
        this.score = score;
        this.mode  = mode;
    }

    public String getName()  { return name; }
    public int    getScore() { return score; }
    public String getMode()  { return mode;  }

    @Override
    public int compareTo(HighScoreEntry o) {
        // sort descending by score
        return Integer.compare(o.score, this.score);
    }
}
