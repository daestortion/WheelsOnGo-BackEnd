package com.respo.respo.Service;

import com.respo.respo.Entity.ChatEntity;
import com.respo.respo.Entity.MessageEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.ChatRepository;
import com.respo.respo.Repository.MessageRepository;
import com.respo.respo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

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
}
