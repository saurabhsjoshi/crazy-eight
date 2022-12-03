package org.joshi.crazyeight.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that represents a deck of cards.
 */
public class CardDeck {
    private final List<Card> cards;

    public CardDeck() {
        cards = new ArrayList<>(Suit.values().length * Rank.values().length);
        reset();
    }

    /**
     * Reset the deck of cards.
     */
    public void reset() {
        for (var r : Rank.values()) {
            for (var s : Suit.values()) {
                cards.add(new Card(r, s));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card top() {
        return cards.remove(cards.size() - 1);
    }

    public boolean empty() {
        return cards.isEmpty();
    }
}
