package org.joshi.crazyeight.deck;

public record Card(Suit suit, Rank rank) {

    public String toText() {
        return rank.toText() + suit.toText();
    }

    public static Card fromText(String text) {
        return new Card(Suit.fromText(text.substring(1)), Rank.fromText(text.substring(0, 1)));
    }
}
