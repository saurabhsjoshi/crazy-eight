package org.joshi.crazyeight.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.joshi.crazyeight.game.Game;
import org.joshi.crazyeight.msg.PlayerListMsg;
import org.joshi.crazyeight.msg.UserRegisterMsg;
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

    private final ConcurrentHashMap<String, WebSocketSession> socketHandles = new ConcurrentHashMap<>();

    private final Game game;

    public WebSocketMsgHandler() {
        this.game = new Game();
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("New websocket connection '{}' established.", session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        var msg = objectMapper.readValue(message.getPayload(), Message.class);
        if (msg instanceof UserRegisterMsg registerMsg) {
            handleUserRegister(registerMsg, session);
        }
    }

    private void handleUserRegister(UserRegisterMsg registerMsg, WebSocketSession session) {
        String username = registerMsg.username;
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
            try {
                handle.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
