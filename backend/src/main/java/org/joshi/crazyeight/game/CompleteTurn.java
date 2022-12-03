package org.joshi.crazyeight.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.deck.Suit;

@Data
@RequiredArgsConstructor
public class CompleteTurn {

    /**
     * The card played by the player.
     */
    private final Card card;

    /**
     * Optional suit set by player if they played an eight.
     */
    private Suit suit;
}
