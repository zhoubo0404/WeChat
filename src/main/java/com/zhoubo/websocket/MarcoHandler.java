package com.zhoubo.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

/**
 * Created by zhoubo on 2017/7/25.
 */
public class MarcoHandler extends AbstractWebSocketHandler {
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
        System.out.println("Received message; " + message.getPayload());
        Thread.sleep(2000);
        session.sendMessage(new TextMessage("Polo!"));
    }
}
