package com.respo.respo.Controller;

import com.respo.respo.Entity.ChatEntity;
import com.respo.respo.Entity.MessageEntity;
import com.respo.respo.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    
    @GetMapping("/all")
    public List<ChatEntity> getAllChats() {
        return chatService.getAllChats();
    }

    @Autowired
    private ChatService chatService;
    // Retrieve all messages in a chat by chatId
    @GetMapping("/{chatId}/messages")
    public List<MessageEntity> getMessages(@PathVariable int chatId) {
        return chatService.getMessagesByChatId(chatId);
    }

    // Send a message in a chat
    @PostMapping("/{chatId}/send")
    public MessageEntity sendMessage(@PathVariable int chatId, @RequestParam int userId, @RequestParam String messageContent) {
        return chatService.sendMessage(chatId, userId, messageContent);
    }

    
    // Create a new chat using @RequestBody with adminId and reportId as @RequestParam
    @PostMapping("/create")
    public ChatEntity createChat(
            @RequestBody ChatEntity chatEntity,
            @RequestParam int adminId,
            @RequestParam int reportId) {
        return chatService.createChat(chatEntity, adminId, reportId);
    }

}