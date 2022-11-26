package org.joshi.crazyeight.network;

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

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        System.out.println("Websocket connection established");
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        System.out.println("Received message " + message.getPayload());
    }

}
