package com.respo.respo.Controller;

import com.respo.respo.Entity.MessageEntity;
import com.respo.respo.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Retrieve all messages in a chat by chatId
    @GetMapping("/{chatId}/messages")
    public List<MessageEntity> getMessages(@PathVariable int chatId) {
        return chatService.getMessagesByChatId(chatId);
    }

    // Send a message in a chat
    @PostMapping("/{chatId}/messages")
    public MessageEntity sendMessage(@PathVariable int chatId, @RequestParam int userId, @RequestParam String messageContent) {
        return chatService.sendMessage(chatId, userId, messageContent);
    }
}
