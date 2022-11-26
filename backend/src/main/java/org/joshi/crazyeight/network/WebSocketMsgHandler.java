package org.joshi.crazyeight.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joshi.crazyeight.msg.UserRegisterMsg;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * This class handles all messages received by the websocket.
 */
@Component
public class WebSocketMsgHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        System.out.println("Websocket connection established");
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        var msg = objectMapper.readValue(message.getPayload(), Message.class);
        if (msg instanceof UserRegisterMsg registerMsg) {
            System.out.println("User registered with username " + registerMsg.username);
        }
        System.out.println("Received message " + message.getPayload());
    }

}
