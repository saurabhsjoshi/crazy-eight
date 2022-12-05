package org.joshi.crazyeight.game;

import lombok.Data;

@Data
public class TurnResult {

    private String skippedPlayer;

    private int direction;

    private String nextPlayer;

    private String winner;
}
