package org.joshi.crazyeight.deck;

public enum Suit {
    CLUBS,
    DIAMONDS,
    HEART,
    SPADES;

    String toText() {
        return switch (this) {
            case CLUBS -> "C";
            case DIAMONDS -> "D";
            case HEART -> "H";
            case SPADES -> "S";
        };
    }

    static Suit fromText(String text) {
        return switch (text) {
            case "C" -> CLUBS;
            case "D" -> DIAMONDS;
            case "H" -> HEART;
            default -> SPADES;
        };
    }
}
