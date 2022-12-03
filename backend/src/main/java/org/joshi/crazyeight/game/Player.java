package org.joshi.crazyeight.game;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class that represents a player in the game.
 */
@Data
@AllArgsConstructor
public class Player {
    private String username;
    private int score;
}
