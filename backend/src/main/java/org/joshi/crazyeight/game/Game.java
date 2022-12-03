package org.joshi.crazyeight.game;

import org.joshi.crazyeight.deck.CardDeck;

import java.util.ArrayList;
import java.util.List;

public class Game {

    /**
     * List of players.
     */
    private final List<Player> players = new ArrayList<>();

    private final CardDeck deck = new CardDeck();

    public Game() {
        deck.shuffle();
    }

    public void addPlayer(String username) {
        players.add(new Player(username, 0));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public boolean canStartGame() {
        var size = players.size();
        return size > 2 && size < 5;
    }
}
