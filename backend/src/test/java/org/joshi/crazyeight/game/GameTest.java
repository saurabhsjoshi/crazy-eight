package org.joshi.crazyeight.game;

import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.deck.Rank;
import org.joshi.crazyeight.deck.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void testReverse() {
        addFourPlayers();
        assertEquals("testUser1", game.nextTurn());
        assertEquals("testUser2", game.nextTurn());
        game.reverse();
        assertEquals("testUser1", game.nextTurn());
    }

    @Test
    void testDrawCard() {
        addFourPlayers();
        game.setPlayerHand();
        game.nextTurn();

        for (int i = 0; i < 3; i++) {
            game.drawCard();
        }

        assertEquals(8, game.getPlayers().get(0).getHand().size());


        game.drawCard();

        // Should not allow to draw more than three cards
        assertEquals(8, game.getPlayers().get(0).getHand().size());
    }

    @Test
    void testRigRound() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        List<String> riggedCards = List.of(
                "4H 7S 5D 6D 9D",
                "4S 6S KC 8H 10D",
                "9S 6C 9C JD 3H",
                "7D JH QH KH 5C"
        );

        game.rigRound("4D", riggedCards);

        var topCard = game.getTopCard();
        assertEquals(Rank.FOUR, topCard.rank());
        assertEquals(Suit.DIAMONDS, topCard.suit());

        var hand = game.getPlayers().get(0).getHand();

        List<Card> expectedCards = List.of(
                new Card(Suit.HEART, Rank.FOUR),
                new Card(Suit.SPADES, Rank.SEVEN),
                new Card(Suit.DIAMONDS, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.DIAMONDS, Rank.NINE)
        );

        for (int i = 0; i < 5; i++) {
            assertEquals(expectedCards.get(i).rank(), hand.get(i).rank());
            assertEquals(expectedCards.get(i).suit(), hand.get(i).suit());
        }

        assertEquals(31, game.getDeck().size());
    }

    @Test
    void testCompleteTurn() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        assertEquals("testUser1", game.nextTurn());

        List<String> riggedCards = List.of(
                "4H 7S 5D 6D 9D",
                "4S 6S KC 8H 10D",
                "9S 6C 9C JD 3H",
                "7D JH QH KH 5C"
        );

        game.rigRound("4D", riggedCards);

        Card playedCard = new Card(Suit.HEART, Rank.FOUR);
        CompleteTurn completeTurn = new CompleteTurn(playedCard);

        var nextPlayer = game.completeTurn(completeTurn);

        assertEquals(playedCard, game.getTopCard());
        assertEquals("testUser2", nextPlayer.getNextPlayer());
    }

    @Test
    void testCompleteTurn_Reverse() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        assertEquals("testUser1", game.nextTurn());
        List<String> riggedCards = List.of(
                "4H 7S 5D 6D 9D",
                "4S 1D KC 8H 10D",
                "9S 6C 9C JD 3H",
                "7D JH QH KH 5C"
        );
        game.rigRound("4D", riggedCards);
        game.completeTurn(new CompleteTurn(new Card(Suit.HEART, Rank.FOUR)));

        var nextPlayer = game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.ACE)));
        assertEquals(-1, nextPlayer.getDirection());
        assertEquals("testUser1", nextPlayer.getNextPlayer());
    }

    @Test
    void testCompleteTurn_Skip() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        assertEquals("testUser1", game.nextTurn());
        List<String> riggedCards = List.of(
                "4H 7S 5D 6D 9D",
                "4S QD KC 8H 10D",
                "9S 6C 9C JD 3H",
                "7D JH QH KH 5C"
        );
        game.rigRound("4D", riggedCards);
        game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.FOUR)));

        var nextPlayer = game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.QUEEN)));
        assertEquals("testUser3", nextPlayer.getSkippedPlayer());
        assertEquals("testUser4", nextPlayer.getNextPlayer());
    }

    @Test
    void testCompleteTurn_DrawTwo() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        assertEquals("testUser1", game.nextTurn());
        List<String> riggedCards = List.of(
                "4H 7S 5D 6D 9D",
                "4S QD KC 8H 2D",
                "9S 6C 9C JD 3H",
                "7D JH QH KH 5C"
        );
        game.rigRound("4D", riggedCards);
        game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.FOUR)));

        var nextPlayer = game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.TWO)));

        assertEquals("testUser3", nextPlayer.getNextPlayer());
        assertEquals(2, game.getCardsToDraw());

        game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.SEVEN)));
        assertEquals(0, game.getCardsToDraw());
    }

    @Test
    void testCompleteTurn_DrawFour() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        assertEquals("testUser1", game.nextTurn());
        List<String> riggedCards = List.of(
                "4H 7S 5D 6D 9D",
                "4S QD KC 8H 2D",
                "9S 6C 9C JD 2H",
                "7D JH QH KH 5C"
        );

        game.rigRound("4D", riggedCards);
        game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.FOUR)));
        game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.TWO)));
        var nextPlayer = game.completeTurn(new CompleteTurn(new Card(Suit.HEART, Rank.TWO)));

        assertEquals("testUser4", nextPlayer.getNextPlayer());
        assertEquals(4, game.getCardsToDraw());
    }

    @Test
    void testDrawCards() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        assertEquals("testUser1", game.nextTurn());
        List<String> riggedCards = List.of(
                "4H 7S 5D 6D 9D",
                "4S QD KC 8H 2D",
                "9S 6C 9C JD 3H",
                "7D JH QH KH 5C"
        );
        game.rigRound("4D", riggedCards);
        game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.FOUR)));
        var nextPlayer = game.completeTurn(new CompleteTurn(new Card(Suit.DIAMONDS, Rank.TWO)));

        assertEquals("testUser3", nextPlayer.getNextPlayer());
        assertEquals(2, game.getCardsToDraw());

        game.drawCards();

        assertEquals(7, game.getPlayers().get(2).getHand().size());
    }

    @Test
    void testWinner() {
        addFourPlayers();
        game.setPlayerHand();
        game.setTopCard();

        assertEquals("testUser1", game.nextTurn());
        List<String> riggedCards = List.of(
                "4H",
                "4S QD KC 8H 2D",
                "9S 6C 9C JD 3H",
                "7D JH QH KH 5C"
        );
        game.rigRound("4D", riggedCards);

        var result = game.completeTurn(new CompleteTurn(new Card(Suit.HEART, Rank.FOUR)));

        assertEquals("testUser1", result.getRoundWinner());
    }

    @Test
    void testScoring() {

        assertEquals(50, Game.getScore(List.of(new Card(Suit.HEART, Rank.EIGHT))));

        List<Card> cards = new ArrayList<>();
        assertEquals(0, Game.getScore(cards));
        cards.add(new Card(Suit.HEART, Rank.KING)); // 10
        cards.add(new Card(Suit.SPADES, Rank.QUEEN)); // 20
        cards.add(new Card(Suit.DIAMONDS, Rank.JACK)); // 30
        cards.add(new Card(Suit.DIAMONDS, Rank.TWO)); // 32
        assertEquals(32, Game.getScore(cards));
    }
}
