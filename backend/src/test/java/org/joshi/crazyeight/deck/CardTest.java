package org.joshi.crazyeight.deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardTest {

    @Test
    public void testToText() {
        Card card = new Card(Suit.HEART, Rank.ACE);
        Assertions.assertEquals("AH", card.toText());

        card = new Card(Suit.HEART, Rank.EIGHT);
        Assertions.assertEquals("8H", card.toText());
    }
}
