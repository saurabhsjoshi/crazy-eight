package org.joshi.crazyeight.deck;

public enum Suit {
    CLUBS,
    DIAMONDS,
    HEART,
    SPADES;

    String toText() {
        switch (this) {
            case CLUBS -> {
                return "C";
            }
            case DIAMONDS -> {
                return "D";
            }
            case HEART -> {
                return "H";
            }
            case SPADES -> {
                return "S";
            }
        }
        return "";
    }
}
