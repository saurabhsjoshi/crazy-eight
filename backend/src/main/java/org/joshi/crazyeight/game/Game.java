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

    private int currentPlayer = -1;

    private int direction = 1;

    private int cardsDrawn = 0;

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
        cardsDrawn = 0;
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

    /**
     * Get the username of next player.
     */
    public String nextTurn() {
        currentPlayer += direction;

        if (currentPlayer < 0) {
            currentPlayer = players.size() - 1;
        } else if (currentPlayer >= players.size()) {
            currentPlayer = 0;
        }

        return players.get(currentPlayer).getUsername();
    }

    public void reverse() {
        if (direction == -1) {
            direction = 1;
        } else {
            direction = -1;
        }
    }

    public void drawCard() {
        if (cardsDrawn == 3) {
            return;
        }
        players.get(currentPlayer).getHand().add(deck.top());
        cardsDrawn++;
    }

    public void rigRound(String riggedTopCard, List<String> riggedCards) {
        Card card = Card.fromText(riggedTopCard);

        deck.remove(card);
        deck.addCard(topCard);

        topCard = new Card(card.suit(), card.rank());

        for (int i = 0; i < riggedCards.size(); i++) {
            var p = players.get(i);

            for (var c : p.getHand()) {
                deck.addCard(c);
            }

            var cards = getCardsFromText(riggedCards.get(i));

            for (var c : cards) {
                deck.remove(c);
            }

            p.setHand(cards);
        }
    }

    private List<Card> getCardsFromText(String text) {
        List<Card> cards = new ArrayList<>();

        var split = text.split("\\s+");
        for (var s : split) {
            cards.add(Card.fromText(s));
        }
        return cards;
    }

    public String completeTurn(CompleteTurn turn) {
        // TODO: Implement
        return null;
    }
}
