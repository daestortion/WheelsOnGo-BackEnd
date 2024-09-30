package com.respo.respo.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.ChatEntity;
import com.respo.respo.Entity.MessageEntity;
import com.respo.respo.Entity.ReportEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.ChatRepository;
import com.respo.respo.Repository.MessageRepository;
import com.respo.respo.Repository.ReportRepository;
import com.respo.respo.Repository.UserRepository;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

     @Autowired
    private ReportRepository reportRepository;

    public List<MessageEntity> getMessagesByChatId(int chatId) {
        Optional<ChatEntity> chat = chatRepository.findById(chatId);
        return chat.map(ChatEntity::getMessages).orElseThrow(() -> new IllegalArgumentException("Chat not found"));
    }

    public MessageEntity sendMessage(int chatId, int userId, String messageContent) {
        Optional<ChatEntity> chat = chatRepository.findById(chatId);
        Optional<UserEntity> sender = userRepository.findById(userId);

        if (chat.isEmpty() || sender.isEmpty()) {
            throw new IllegalArgumentException("Chat or user not found");
        }

        MessageEntity message = new MessageEntity(chat.get(), sender.get(), messageContent, LocalDateTime.now());
        return messageRepository.save(message);
    }

    public ChatEntity updateChatStatus(int chatId, String status) {
        Optional<ChatEntity> chat = chatRepository.findById(chatId);
        if (chat.isPresent()) {
            ChatEntity existingChat = chat.get();
            existingChat.setStatus(status); // Update the status (e.g., "resolved" or "pending")
            return chatRepository.save(existingChat); // Save the updated chat
        } else {
            throw new IllegalArgumentException("Chat not found");
        }
    }

     // Method to create a new chat
    public ChatEntity createChat(int reportId, List<Integer> userIds) {
        // Fetch the report using the reportId
        Optional<ReportEntity> report = reportRepository.findById(reportId);
        if (report.isEmpty()) {
            throw new IllegalArgumentException("Report not found");
        }

        // Fetch users by their IDs
        List<UserEntity> users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("No valid users found");
        }

        // Create a new ChatEntity
        ChatEntity chat = new ChatEntity();
        chat.setReport(report.get());
        chat.setUsers(users);
        chat.setStatus("pending");
        chat.setCreatedAt(LocalDateTime.now());

        // Save and return the new chat entity
        return chatRepository.save(chat);
    }
}
