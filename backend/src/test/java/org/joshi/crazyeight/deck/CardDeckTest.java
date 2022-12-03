package org.joshi.crazyeight.deck;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardDeckTest {

    @Test
    public void testCardDeckShuffle() {
        List<Card> expectedCards = new ArrayList<>();
        for (var r : Rank.values()) {
            for (var s : Suit.values()) {
                expectedCards.add(new Card(r, s));
            }
        }

        CardDeck cardDeck = new CardDeck();
        cardDeck.shuffle();

        while (!cardDeck.empty()) {
            expectedCards.remove(cardDeck.top());
        }

        assertTrue(expectedCards.isEmpty());
    }
}
