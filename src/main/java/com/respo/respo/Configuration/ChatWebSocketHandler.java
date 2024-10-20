package com.respo.respo.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.respo.respo.Service.ChatService;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;

    private final Map<String, List<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String chatId = getChatIdFromSession(session);
        chatSessions.computeIfAbsent(chatId, k -> new ArrayList<>()).add(session); // Add the session to the chat room
        System.out.println("New WebSocket connection for chatId: " + chatId);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String chatId = getChatIdFromSession(session);
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

        // Deserialize the incoming message (assumed to be JSON with userId and messageContent)
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
        Integer userId = (Integer) messageData.get("userId");
        String messageContent = (String) messageData.get("messageContent");

        // Save the message to the database using the existing chat service
        chatService.sendMessage(Integer.parseInt(chatId), userId, null, messageContent); // Adjust as needed

        // Broadcast the message only to sessions in the same chat room
        List<WebSocketSession> sessionsInChat = chatSessions.get(chatId);
        for (WebSocketSession s : sessionsInChat) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        String chatId = getChatIdFromSession(session);
        chatSessions.getOrDefault(chatId, new ArrayList<>()).remove(session); // Remove the closed session from the list
        System.out.println("WebSocket connection closed for chatId: " + chatId);
    }

    // Helper method to extract the chatId from the session URL
    private String getChatIdFromSession(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.split("/ws/chat/")[1];  // Extract chatId from path like "/ws/chat/{chatId}"
    }
}
