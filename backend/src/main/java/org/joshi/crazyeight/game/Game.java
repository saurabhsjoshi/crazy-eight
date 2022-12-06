package org.joshi.crazyeight.game;

import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.deck.CardDeck;
import org.joshi.crazyeight.deck.Rank;
import org.joshi.crazyeight.deck.Suit;

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

    private int cardsToDraw = 0;

    private Suit currentSuit;

    public void addPlayer(String username) {
        players.add(new Player(username));
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
        setTopCard(null);
        cardsDrawn = 0;
    }

    public void setPlayerHand() {
        for (var p : players) {
            p.setHand(deck.top(5));
        }
    }

    public void setTopCard() {
        if (topCard != null) {
            setTopCard(deck.top());
            return;
        }
        setTopCard(deck.top());
        while (topCard.rank() == Rank.EIGHT) {
            var cur = new Card(topCard.suit(), topCard.rank());
            setTopCard(deck.top());
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

    public void drawCards() {
        players.get(currentPlayer).getHand().addAll(deck.top(cardsToDraw));
    }

    public void rigRound(String riggedTopCard, List<String> riggedCards) {
        Card card = Card.fromText(riggedTopCard);

        deck.remove(card);
        deck.addCard(topCard);

        setTopCard(new Card(card.suit(), card.rank()));

        // Put players current card back in the deck
        for (var p : players) {
            for (var c : p.getHand()) {
                deck.addCard(c);
            }
            p.getHand().clear();
        }

        for (int i = 0; i < riggedCards.size(); i++) {
            var p = players.get(i);
            var cards = getCardsFromText(riggedCards.get(i));
            for (var c : cards) {
                deck.remove(c);
            }
            p.setHand(cards);
        }
    }

    public static List<Card> getCardsFromText(String text) {
        List<Card> cards = new ArrayList<>();

        var split = text.split("\\s+");
        for (var s : split) {
            cards.add(Card.fromText(s));
        }
        return cards;
    }

    public TurnResult completeTurn(CompleteTurn turn) {

        TurnResult result = new TurnResult();

        if (turn.getCard() != null) {
            players.get(currentPlayer).removeCard(turn.getCard());
            setTopCard(turn.getCard());
        } else {
            // Player skipped their turn
            cardsToDraw = 0;
        }

        if (players.get(currentPlayer).getHand().isEmpty()) {
            result.setRoundWinner(players.get(currentPlayer).getUsername());
            return result;
        }

        cardsDrawn = 0;

        if (cardsToDraw != 0) {
            // Player skipped draw two
            if (turn.getDrawTwoCard() != null) {
                players.get(currentPlayer).removeCard(turn.getDrawTwoCard());
                setTopCard(turn.getDrawTwoCard());
            }

            if (topCard.rank() != Rank.TWO) {
                // Reset if the card played is not another two
                cardsToDraw = 0;
            }
        }

        if (topCard.rank() == Rank.EIGHT) {
            currentSuit = turn.getSuit();
        }

        switch (topCard.rank()) {
            case ACE -> {
                reverse();
                result.setDirection(direction);
            }
            case QUEEN -> result.setSkippedPlayer(nextTurn());
            case TWO -> cardsToDraw += 2;
        }

        result.setCurrentSuit(currentSuit);
        result.setNextPlayer(nextTurn());
        return result;
    }

    public static Integer getScore(List<Card> cards) {
        if (cards.isEmpty()) {
            return 0;
        }
        int score = 0;

        for (var c : cards) {
            score += switch (c.rank()) {
                case TWO -> 2;
                case THREE -> 3;
                case FOUR -> 4;
                case FIVE -> 5;
                case SIX -> 8;
                case SEVEN -> 7;
                case EIGHT -> 50;
                case NINE -> 9;
                case TEN, JACK, QUEEN, KING -> 10;
                case ACE -> 1;
            };
        }

        return score;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCardsToDraw() {
        return cardsToDraw;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    private void setTopCard(Card card) {
        if (card != null) {
            topCard = card;
            currentSuit = topCard.suit();
        }
    }
}
