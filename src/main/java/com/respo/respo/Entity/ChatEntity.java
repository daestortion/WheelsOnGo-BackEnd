package com.respo.respo.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tblChats")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatId;

    @OneToOne
    @JoinColumn(name = "reportId", referencedColumnName = "reportId")
    private ReportEntity report; // One-to-one relationship with ReportEntity

    @ManyToMany
    @JoinTable(
        name = "tblChatUsers",
        joinColumns = @JoinColumn(name = "chatId"),
        inverseJoinColumns = @JoinColumn(name = "userId")
    )
    private List<UserEntity> users = new ArrayList<>(); // Many-to-many relationship with users

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageEntity> messages = new ArrayList<>(); // Messages exchanged in the chat

    @Column(name = "status")
    private String status; // Status attribute (e.g., "pending", "resolved")

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    // Constructors, getters, and setters
    public ChatEntity() {
    }

    public ChatEntity(ReportEntity report, List<UserEntity> users, String status, LocalDateTime createdAt) {
        this.report = report;
        this.users = users;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public ReportEntity getReport() {
        return report;
    }

    public void setReport(ReportEntity report) {
        this.report = report;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
