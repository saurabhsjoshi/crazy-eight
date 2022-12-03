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
        cards.clear();
        for (var r : Rank.values()) {
            for (var s : Suit.values()) {
                cards.add(new Card(s, r));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card top() {
        return cards.remove(cards.size() - 1);
    }

    public List<Card> top(int count) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(top());
        }
        return cards;
    }

    public int size() {
        return cards.size();
    }

    public boolean empty() {
        return cards.isEmpty();
    }

    public void addCard(Card card) {
        for (var c : cards) {
            if (c.rank() == card.rank() && c.suit() == card.suit()) {
                cards.remove(c);
                break;
            }
        }
        cards.add(card);
    }
}
