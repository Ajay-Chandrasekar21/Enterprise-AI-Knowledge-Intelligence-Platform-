package com.enterprise.eakip.api.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    
    // Maps SessionID to WebSocketSession
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("WebSocket connection established. SessionID: {}", sessionId);
        
        try {
            session.sendMessage(new TextMessage("{\"status\":\"CONNECTED\",\"message\":\"Notification feed active\"}"));
        } catch (IOException e) {
            log.error("Error sending connection status message: {}", e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received message on WebSocket: {}", message.getPayload());
        // Handle echo or client messages if required
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        log.info("WebSocket connection closed. SessionID: {}, status: {}", sessionId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error on session {}: {}", session.getId(), exception.getMessage());
    }

    public void sendNotificationToAll(String jsonPayload) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonPayload));
                } catch (IOException e) {
                    log.error("Failed to send socket notification: {}", e.getMessage());
                }
            }
        });
    }
}
