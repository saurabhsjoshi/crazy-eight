package org.joshi.crazyeight.game;

import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.deck.Rank;
import org.joshi.crazyeight.deck.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Game game;

    private void addFourPlayers() {
        game.addPlayer("testUser1");
        game.addPlayer("testUser2");
        game.addPlayer("testUser3");
        game.addPlayer("testUser4");
    }

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

    @Test
    void testSetTopCard_8() {
        var eightCard = new Card(Suit.HEART, Rank.EIGHT);
        game.getDeck().addCard(eightCard);
        game.setTopCard();
        assertNotNull(game.getTopCard());
        assertNotSame(eightCard, game.getTopCard());
        assertEquals(51, game.getDeck().size());
    }

    @Test
    void testNextTurn() {
        addFourPlayers();
        assertEquals("testUser1", game.nextTurn());
        assertEquals("testUser2", game.nextTurn());
        assertEquals("testUser3", game.nextTurn());
    }
}
