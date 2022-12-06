package org.joshi.crazyeight.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.game.CompleteTurn;
import org.joshi.crazyeight.game.Game;
import org.joshi.crazyeight.msg.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles all messages received by the websocket.
 */
@Component
@Slf4j
public class WebSocketMsgHandler extends TextWebSocketHandler {

    @Value("${game.rigged:true}")
    private boolean rigged;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean gameStarted = false;

    private int overallPlayer = 0;

    private final ConcurrentHashMap<String, WebSocketSession> socketHandles = new ConcurrentHashMap<>();

    private final Game game;

    public WebSocketMsgHandler() {
        this.game = new Game();
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        if (gameStarted) {
            log.info("Game already started disconnecting user '{}'.", session.getId());
            session.close();
            return;
        }

        log.info("New websocket connection '{}' established.", session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        var msg = objectMapper.readValue(message.getPayload(), Message.class);

        if (msg instanceof UserRegisterMsg registerMsg) {
            handleUserRegister(registerMsg, session);
            return;
        }

        if (msg instanceof StartGameMsg) {
            log.info("Received start game message from the host.");
            handleStartGameMsg();
            return;
        }

        if (msg instanceof CompleteTurnMsg completeTurnMsg) {
            log.info("Received complete turn message.");
            handleCompleteTurn(completeTurnMsg);
            return;
        }

        if (msg instanceof DrawCardMsg drawCardMsg) {
            log.info("Received draw card message.");
            handleDrawCard(drawCardMsg);
            return;
        }

        if (msg instanceof RigRoundMsg rigRoundMsg) {
            log.info("Received rig round message.");
            handleRiggedMsg(rigRoundMsg);
        }
    }

    private void handleDrawCard(DrawCardMsg drawCardMsg) {
        if (drawCardMsg.getNum() != null) {
            game.drawCards();
        } else {
            game.drawCard();
        }
        var player = game.getPlayers().get(game.getCurrentPlayer());
        sendMsg(player.getUsername(), new UpdateHandMsg(player.getHand()));
    }

    private void handleStartGameMsg() {
        gameStarted = true;
        sendStartRoundMsg();
    }

    private void handleUserRegister(UserRegisterMsg registerMsg, WebSocketSession session) {
        String username = registerMsg.username;

        if (game.getPlayers().isEmpty()) {
            // This is the first player, inform them they are the host
            sendMsg(session, new HostMsg());
        }

        game.addPlayer(username);
        socketHandles.put(username, session);
        broadcastPlayerScores();
        log.info("Registered user '{}'.", registerMsg.username);
    }

    private void broadcastPlayerScores() {
        PlayerListMsg msg = new PlayerListMsg();
        for (var p : game.getPlayers()) {
            msg.getPlayers().add(new PlayerListMsg.PlayerScores(p.getUsername(), p.getScore()));
        }

        for (var handle : socketHandles.values()) {
            sendMsg(handle, msg);
        }
    }

    private void sendStartRoundMsg() {
        game.resetRound();
        game.setPlayerHand();
        game.setTopCard();

        // If rigged wait for rigging
        if (rigged) {
            return;
        }
        broadcastStartRound();
    }

    private void broadcastStartRound() {
        for (var p : game.getPlayers()) {
            sendMsg(p.getUsername(), new StartRoundMsg(p.getHand()));
        }
        log.info("Sent start round message to '{}' players.", game.getPlayers().size());

        game.setCurrentPlayer(overallPlayer - 1);
        updateOverallPlayer();
        startTurn(game.nextTurn());
    }

    public void startTurn(String nextPlayer) {
        StartTurnMsg msg = new StartTurnMsg();
        msg.setUsername(nextPlayer);
        msg.setCardsToDraw(game.getCardsToDraw());
        msg.setTopCard(game.getTopCard());
        msg.setCurrentSuit(game.getCurrentSuit());
        log.info("Starting turn for player '{}' with top card '{}'.", nextPlayer, msg.getTopCard());

        for (var p : game.getPlayers()) {
            sendMsg(p.getUsername(), msg);
        }
    }

    public void handleCompleteTurn(CompleteTurnMsg msg) {
        var currentPlayer = game.getCurrentPlayer();

        var completeTurn = new CompleteTurn(Card.fromText(msg.getCard()));
        completeTurn.setDrawTwoCard(Card.fromText(msg.getAdditionalCard()));
        completeTurn.setSuit(msg.getSuit());

        log.info("Completing turn for user '{}' who played card '{}'.",
                game.getPlayers().get(currentPlayer).getUsername(),
                completeTurn.getCard());

        var result = game.completeTurn(completeTurn);

        if (!result.getRoundWinner().isEmpty()) {

            // Update scores
            for (var p : game.getPlayers()) {
                p.setScore(p.getScore() + Game.getScore(p.getHand()));
            }

            broadcastRoundWinnerMsg(result.getRoundWinner());
            broadcastPlayerScores();

            if (hasOverallWinner()) {
                return;
            }

            sendStartRoundMsg();
            return;
        }

        if (!result.getSkippedPlayer().isEmpty()) {
            sendMsg(result.getSkippedPlayer(), new PlayerSkippedMsg());
        }

        if (result.getDirection() != 0) {
            for (var h : socketHandles.values()) {
                sendMsg(h, new DirectionChangeMsg(result.getDirection()));
            }
        }

        startTurn(result.getNextPlayer());

        var player = game.getPlayers().get(currentPlayer);
        log.info("Sending update hand message to player '{}'.", player.getUsername());
        sendMsg(player.getUsername(), new UpdateHandMsg(player.getHand()));
    }

    private void broadcastRoundWinnerMsg(String winner) {
        var msg = new RoundWinnerMsg();
        msg.setUsername(winner);
        for (var handle : socketHandles.values()) {
            sendMsg(handle, msg);
        }
    }

    private boolean hasOverallWinner() {
        int low = Integer.MAX_VALUE;
        String winner = "";
        boolean gameEnded = false;

        for (var p : game.getPlayers()) {
            int score = p.getScore();
            if (score >= 100) {
                gameEnded = true;
            }

            if (score < low) {
                low = score;
                winner = p.getUsername();
            }
        }
        if (gameEnded) {
            var msg = new GameWinnerMsg();
            msg.setUsername(winner);
            for (var handle : socketHandles.values()) {
                sendMsg(handle, msg);
            }

            for (var h : socketHandles.values()) {
                try {
                    h.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return gameEnded;
    }

    private void handleRiggedMsg(RigRoundMsg msg) {
        var split = msg.getRiggedCards().split(",");

        String topCard = split[0];
        List<String> riggedCards = new ArrayList<>();

        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (split[i + 2].isBlank()) {
                riggedCards.add("");
            } else {
                riggedCards.add(split[i + 2]);
            }
        }

        game.rigRound(topCard, riggedCards);

        if (!split[1].isBlank()) {
            var cards = Game.getCardsFromText(split[1]);
            Collections.reverse(cards);

            for (var c : cards) {
                game.getDeck().addCard(c);
            }
        }

        broadcastStartRound();
    }

    private <T extends Message> void sendMsg(String username, T obj) {
        sendMsg(socketHandles.get(username), obj);
    }

    private <T extends Message> void sendMsg(WebSocketSession handle, T obj) {
        try {
            handle.sendMessage(new TextMessage(objectMapper.writeValueAsString(obj)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateOverallPlayer() {
        overallPlayer++;

        if (overallPlayer == game.getPlayers().size()) {
            overallPlayer = 0;
        }
    }

}
