package org.joshi.crazyeight.deck;

public enum Rank {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE;

    static Rank fromText(String text) {
        return switch (text) {
            case "1" -> ACE;
            case "2" -> TWO;
            case "3" -> THREE;
            case "4" -> FOUR;
            case "5" -> FIVE;
            case "6" -> SIX;
            case "7" -> SEVEN;
            case "8" -> EIGHT;
            case "9" -> NINE;
            case "10" -> TEN;
            case "J" -> JACK;
            case "Q" -> QUEEN;
            default -> KING;
        };
    }

    String toText() {
        switch (this) {
            case TWO -> {
                return "2";
            }
            case THREE -> {
                return "3";
            }
            case FOUR -> {
                return "4";
            }
            case FIVE -> {
                return "5";
            }
            case SIX -> {
                return "6";
            }
            case SEVEN -> {
                return "7";
            }
            case EIGHT -> {
                return "8";
            }
            case NINE -> {
                return "9";
            }
            case TEN -> {
                return "10";
            }
            case JACK -> {
                return "J";
            }
            case QUEEN -> {
                return "Q";
            }
            case KING -> {
                return "K";
            }
            case ACE -> {
                return "1";
            }
        }
        return "";
    }
}
