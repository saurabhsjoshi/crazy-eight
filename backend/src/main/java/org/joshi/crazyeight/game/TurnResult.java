package org.joshi.crazyeight.game;

import lombok.Data;

@Data
public class TurnResult {

    private String skippedPlayer = "";

    private Integer direction = 0;

    private String nextPlayer = "";

    private String winner = "";
}
