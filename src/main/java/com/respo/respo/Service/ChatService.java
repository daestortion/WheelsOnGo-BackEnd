package com.respo.respo.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.AdminEntity;
import com.respo.respo.Entity.ChatEntity;
import com.respo.respo.Entity.MessageEntity;
import com.respo.respo.Entity.ReportEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.AdminRepository;
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
    private AdminRepository adminRepository; // You need this repository for fetching AdminEntity

    public List<ChatEntity> getAllChats() {
        return chatRepository.findAll();
    }

    @Autowired
    private ReportRepository reportRepository; // Make sure this is autowired

    public ChatEntity createChat(ChatEntity chatEntity, int adminId, int reportId) {
        // Check if a chat already exists for this report
        Optional<ChatEntity> existingChat = chatRepository.findByReport_ReportId(reportId);
        if (existingChat.isPresent()) {
            return existingChat.get(); // Return the existing chat if found
        }

        // Otherwise, proceed to create a new chat
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        chatEntity.setAdmin(admin);
        chatEntity.setReport(report);

        // Save the chat entity
        return chatRepository.save(chatEntity);
    }

    public List<MessageEntity> getMessagesByChatId(int chatId) {
        Optional<ChatEntity> chat = chatRepository.findById(chatId);
        return chat.map(ChatEntity::getMessages).orElseThrow(() -> new IllegalArgumentException("Chat not found"));
    }

    public MessageEntity sendMessage(int chatId, Integer userId, Integer adminId, String messageContent) {
        Optional<ChatEntity> chat = chatRepository.findById(chatId);
        
        if (chat.isEmpty()) {
            throw new IllegalArgumentException("Chat not found");
        }
    
        MessageEntity message;
    
        if (adminId != null) {
            // If the sender is an admin
            Optional<AdminEntity> adminSender = adminRepository.findById(adminId);
            if (adminSender.isPresent()) {
                message = new MessageEntity(chat.get(), null, adminSender.get(), messageContent, LocalDateTime.now());
            } else {
                throw new IllegalArgumentException("Admin not found");
            }
        } else if (userId != null) {
            // If the sender is a user
            Optional<UserEntity> userSender = userRepository.findById(userId);
            if (userSender.isPresent()) {
                message = new MessageEntity(chat.get(), userSender.get(), null, messageContent, LocalDateTime.now());
            } else {
                throw new IllegalArgumentException("User not found");
            }
        } else {
            throw new IllegalArgumentException("Sender not provided");
        }
    
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

        // Method to check if a chat exists for the given reportId
        public Optional<ChatEntity> findChatByReportId(int reportId) {
            return chatRepository.findByReport_ReportId(reportId);
        }

        
    // Method to add a user to a chat
    public ChatEntity addUserToChat(int chatId, int userId) {
        // Fetch the chat by chatId
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // Fetch the user by userId
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Add the user to the chat's user list
        if (!chat.getUsers().contains(user)) {
            chat.getUsers().add(user);
        }

        // Save and return the updated chat entity
        return chatRepository.save(chat);
    }
}