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
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "tblMessages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    @JsonIgnore // Prevent recursive serialization
    private ChatEntity chat;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = true) // Nullable if it's an admin sender
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "adminId", nullable = true) // Nullable if it's a user sender
    private AdminEntity adminSender;

    @Column(name = "messageContent")
    private String messageContent;

    @Column(name = "sentAt")
    private LocalDateTime sentAt;

    // Constructors
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

    @JsonProperty("chatId")
    public Integer getChatId() {
        return chat != null ? chat.getChatId() : null;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    @JsonProperty("userId")
    public Integer getUserId() {
        return sender != null ? sender.getUserId() : null;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    @JsonProperty("adminId")
    public Integer getAdminId() {
        return adminSender != null ? adminSender.getAdminId() : null;
    }

    public void setAdminSender(AdminEntity adminSender) {
        this.adminSender = adminSender;
    }

    @JsonProperty("senderLabel")
    public String getSenderLabel() {
        // Return "Admin" for admins and username for users
        if (adminSender != null) {
            return "Admin";
        } else if (sender != null) {
            return sender.getUsername();
        }
        return "Unknown";
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
