package org.joshi.crazyeight.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    @Test
    void testCanStartGame() {
        Game game = new Game();

        game.addPlayer("testUser1");
        assertFalse(game.canStartGame());

        game.addPlayer("testUser2");
        assertFalse(game.canStartGame());

        game.addPlayer("testUser3");
        assertTrue(game.canStartGame());

        game.addPlayer("testUser4");
        assertTrue(game.canStartGame());

        game.addPlayer("testUser5");
        assertFalse(game.canStartGame());
    }
}
