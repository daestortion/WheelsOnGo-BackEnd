package com.respo.respo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.ChatEntity;
import com.respo.respo.Entity.MessageEntity;
import com.respo.respo.Service.ChatService;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Endpoint to fetch all chats for a specific user
    @GetMapping("/getUserChats/{userId}")
    public List<ChatEntity> getUserChats(@PathVariable int userId) {
        return chatService.getChatsForUser(userId);
    }

    @GetMapping("/all")
    public List<ChatEntity> getAllChats() {
        return chatService.getAllChats();
    }

    // Retrieve all messages in a chat by chatId
    @GetMapping("/{chatId}/messages")
    public List<MessageEntity> getMessages(@PathVariable int chatId) {
        return chatService.getMessagesByChatId(chatId);
    }

    // Send a message in a chat
    @PostMapping("/{chatId}/send")
    public MessageEntity sendMessage(@PathVariable int chatId,
                                     @RequestParam(required = false) Integer userId,
                                     @RequestParam(required = false) Integer adminId,
                                     @RequestParam String messageContent) {
        return chatService.sendMessage(chatId, userId, adminId, messageContent);
    }

    // Create a new chat using @RequestBody with adminId and reportId as @RequestParam
    @PostMapping("/create")
    public ChatEntity createChat(@RequestBody ChatEntity chatEntity,
                                 @RequestParam int adminId,
                                 @RequestParam int reportId) {
        return chatService.createChat(chatEntity, adminId, reportId);
    }

    @GetMapping("/check")
    public Optional<ChatEntity> checkIfChatExists(@RequestParam int reportId) {
        return chatService.findChatByReportId(reportId);
    }

    // Endpoint to add a user to a chat
    @PostMapping("/{chatId}/addUser")
    public ChatEntity addUserToChat(@PathVariable int chatId, @RequestParam int userId) {
        return chatService.addUserToChat(chatId, userId);
    }
}
