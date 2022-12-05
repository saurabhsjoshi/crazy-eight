package org.joshi.crazyeight.deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardTest {

    @Test
    public void testToText() {
        Card card = new Card(Suit.HEART, Rank.ACE);
        Assertions.assertEquals("1H", card.toText());

        card = new Card(Suit.HEART, Rank.EIGHT);
        Assertions.assertEquals("8H", card.toText());
    }

    @Test
    public void testFromText() {
        var card = Card.fromText("1H");
        Assertions.assertEquals(Rank.ACE, card.rank());
        Assertions.assertEquals(Suit.HEART, card.suit());

        card = Card.fromText("10S");
        Assertions.assertEquals(Rank.TEN, card.rank());
        Assertions.assertEquals(Suit.SPADES, card.suit());
    }
}
