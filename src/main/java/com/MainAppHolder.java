/**
 * Holds static parameters so that non‐FX code (Deck, GameController)
 * can access the selected cardCount and deckCategory.
 */
package com;

public class MainAppHolder {
    private static int cardCount;
    private static String deckCategory;

    public static void setParams(int count, String category) {
        cardCount = count;
        deckCategory = category;
    }

    public static int getCardCount() {
        return cardCount;
    }

    public static String getDeckCategory() {
        return deckCategory;
    }
}