package org.joshi.crazyeight.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.joshi.crazyeight.deck.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a player in the game.
 */
@Data
@RequiredArgsConstructor
public class Player {
    private final String username;
    private int score = 0;
    private List<Card> hand = new ArrayList<>();

    public void removeCard(Card card) {
        for (var c : hand) {
            if (c.rank() == card.rank() && c.suit() == card.suit()) {
                hand.remove(c);
                break;
            }
        }
    }
}
