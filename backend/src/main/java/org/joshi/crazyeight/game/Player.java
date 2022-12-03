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
    private final int score;
    private List<Card> hand = new ArrayList<>();
}
