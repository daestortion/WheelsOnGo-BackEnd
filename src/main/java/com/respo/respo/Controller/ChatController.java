package com.respo.respo.Controller;

import java.util.List;

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

    // CREATE a new group chat
    @PostMapping
    public ChatEntity createChat(@RequestBody CreateChatRequest request) {
        return chatService.createChat(request.getReportId(), request.getUserIds());
    }

    // DTO for create chat request
    public static class CreateChatRequest {
        private int reportId;
        private List<Integer> userIds;

        public int getReportId() {
            return reportId;
        }

        public void setReportId(int reportId) {
            this.reportId = reportId;
        }

        public List<Integer> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<Integer> userIds) {
            this.userIds = userIds;
        }
    }
}
