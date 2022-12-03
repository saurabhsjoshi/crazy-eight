package org.joshi.crazyeight.game;

import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.deck.CardDeck;
import org.joshi.crazyeight.deck.Rank;

import java.util.ArrayList;
import java.util.List;

public class Game {

    /**
     * List of players.
     */
    private final List<Player> players = new ArrayList<>();

    private final CardDeck deck = new CardDeck();

    private Card topCard;

    public void addPlayer(String username) {
        players.add(new Player(username, 0));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public Card getTopCard() {
        return topCard;
    }

    public boolean canStartGame() {
        var size = players.size();
        return size > 2 && size < 5;
    }

    public void resetRound() {
        deck.reset();
        deck.shuffle();
        topCard = null;
    }

    public void setPlayerHand() {
        for (var p : players) {
            p.setHand(deck.top(5));
        }
    }

    public void setTopCard() {
        if (topCard != null) {
            topCard = deck.top();
        }
        topCard = deck.top();
        while (topCard.rank() == Rank.EIGHT) {
            var cur = new Card(topCard.suit(), topCard.rank());
            topCard = deck.top();
            deck.addCard(cur);
            deck.shuffle();
        }
    }
}
