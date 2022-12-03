package org.joshi.crazyeight.deck;

public record Card(Suit suit, Rank rank) {

    public String toText() {
        return rank.toText() + suit.toText();
    }
}
