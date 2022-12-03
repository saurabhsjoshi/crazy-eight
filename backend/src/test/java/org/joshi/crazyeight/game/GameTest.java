package org.joshi.crazyeight.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Game game;

    @BeforeEach
    public void setup() {
        game = new Game();
    }

    @Test
    void testCanStartGame() {
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

    @Test
    void testResetRound() {
        game.resetRound();
        assertEquals(52, game.getDeck().size());
        assertNull(game.getTopCard());
    }

    @Test
    void testSetPlayerHand() {
        addFourPlayers();
        game.setPlayerHand();
        for (var p : game.getPlayers()) {
            assertEquals(5, p.getHand().size());
        }
    }

    @Test
    void testSetTopCard() {
        game.setTopCard();
        assertNotNull(game.getTopCard());
    }

    private void addFourPlayers() {
        game.addPlayer("testUser1");
        game.addPlayer("testUser2");
        game.addPlayer("testUser3");
        game.addPlayer("testUser4");
    }


}
