package org.joshi.crazyeight.game;

import lombok.Data;
import org.joshi.crazyeight.deck.Suit;

@Data
public class TurnResult {

    private String skippedPlayer = "";

    private Integer direction = 0;

    private String nextPlayer = "";

    private String winner = "";

    private String roundWinner = "";

    private Suit currentSuit;
}
