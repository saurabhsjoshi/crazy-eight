package org.joshi.crazyeight.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.joshi.crazyeight.game.Game;
import org.joshi.crazyeight.msg.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles all messages received by the websocket.
 */
@Component
@Slf4j
public class WebSocketMsgHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean gameStarted = false;

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
        }
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
        for (var p : game.getPlayers()) {
            sendMsg(p.getUsername(), new StartRoundMsg(p.getHand()));
        }
        log.info("Sent start round message to '{}' players.", game.getPlayers().size());
        game.setTopCard();

        startTurn();
    }

    public void startTurn() {
        String nextPlayer = game.nextTurn();
        StartTurnMsg msg = new StartTurnMsg();
        msg.setCardsToDraw(game.getCardsToDraw());
        msg.setTopCard(game.getTopCard());
        log.info("Starting turn for player '{}'", nextPlayer);
        sendMsg(nextPlayer, msg);
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

}
