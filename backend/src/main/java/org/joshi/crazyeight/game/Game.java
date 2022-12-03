package org.joshi.crazyeight.game;

import java.util.ArrayList;
import java.util.List;

public class Game {

    /**
     * List of players.
     */
    private final List<Player> players = new ArrayList<>();

    public void addPlayer(String username) {
        players.add(new Player(username, 0));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean canStartGame() {
        //TODO: Implement
        return false;
    }
}
