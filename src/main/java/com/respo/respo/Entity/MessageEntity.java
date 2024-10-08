package com.respo.respo.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tblMessages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    @JsonIgnore // Prevent recursive serialization
    private ChatEntity chat; // Many-to-one relationship with ChatEntity

    @ManyToOne
    @JoinColumn(name = "userId", nullable = true) // Nullable if it's an admin sender
    private UserEntity sender; // The user sender of the message

    @ManyToOne
    @JoinColumn(name = "adminId", nullable = true) // Nullable if it's a user sender
    private AdminEntity adminSender; // The admin sender of the message

    @Column(name = "messageContent")
    private String messageContent;

    @Column(name = "sentAt")
    private LocalDateTime sentAt;

    // Constructors, getters, and setters
    public MessageEntity() {
    }

    public MessageEntity(ChatEntity chat, UserEntity sender, AdminEntity adminSender, String messageContent, LocalDateTime sentAt) {
        this.chat = chat;
        this.sender = sender;
        this.adminSender = adminSender;
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

    public AdminEntity getAdminSender() {
        return adminSender;
    }

    public void setAdminSender(AdminEntity adminSender) {
        this.adminSender = adminSender;
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
