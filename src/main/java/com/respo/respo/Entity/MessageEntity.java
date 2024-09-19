package com.respo.respo.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblMessages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    private ChatEntity chat; // Many-to-one relationship with ChatEntity

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity sender; // The sender of the message

    @Column(name = "messageContent")
    private String messageContent;

    @Column(name = "sentAt")
    private LocalDateTime sentAt;

    // Constructors, getters, and setters
    public MessageEntity() {
    }

    public MessageEntity(ChatEntity chat, UserEntity sender, String messageContent, LocalDateTime sentAt) {
        this.chat = chat;
        this.sender = sender;
        this.messageContent = messageContent;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public ChatEntity getChat() {
        return chat;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
