package com;

public class ScoreManager {
    private static final double INITIAL_POOL = 1000.0;
    private static final double DECAY_SCALE  = 200.0; 
    // Larger faster drop.

    private double hiddenPool;
    private long   startTime;
    private int    mistakes;

    public ScoreManager() {
        reset();
    }

    // Call when a new pair‐matching round begins.
    public void reset() {
        hiddenPool = INITIAL_POOL;
        startTime  = System.currentTimeMillis();
        mistakes   = 0;
    }

    // Call whenever the player makes a mismatch.
    public void recordMistake() {
        mistakes++;
        updateHiddenPool();
    }

    // Recompute pool based on elapsed time & mistakes.,
    private void updateHiddenPool() {
        double secs = (System.currentTimeMillis() - startTime) / 1000.0;
        double decay = Math.log(1 + secs + mistakes) * DECAY_SCALE;
        hiddenPool = Math.max(0, INITIAL_POOL - decay);
    }

    // Call when the player finds a matching pair.
    public int collectHiddenValue() {
        updateHiddenPool();
        int points = (int)Math.round(hiddenPool);
        reset();  // for the next round
        return points;
    }

    // Show a live meter
    public double getHiddenPool() {
        updateHiddenPool();
        return hiddenPool;
    }
}