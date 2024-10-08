package com.respo.respo.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblChats")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatId;

    @OneToOne
    @JoinColumn(name = "reportId", referencedColumnName = "reportId")
    @JsonIgnoreProperties({"chat", "user"}) // Ignore chat and user in ReportEntity
    private ReportEntity report;

    @ManyToMany
    @JoinTable(
        name = "tblChatUsers",
        joinColumns = @JoinColumn(name = "chatId"),
        inverseJoinColumns = @JoinColumn(name = "userId")
    )
    @JsonIgnoreProperties({"report", "chat"}) // Ignore 'report' and 'chat' in UserEntity
    private List<UserEntity> users = new ArrayList<>(); // Many-to-many relationship with users


    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageEntity> messages = new ArrayList<>(); // Messages exchanged in the chat

    @ManyToOne
    @JoinColumn(name = "adminId", referencedColumnName = "adminId")
    private AdminEntity admin; // Many-to-one relationship with AdminEntity

    @Column(name = "status")
    private String status; // Status attribute (e.g., "pending", "resolved")

	@CreationTimestamp
	@Column(name = "timeStamp", updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, and setters
    public ChatEntity() {
    }

    public ChatEntity(ReportEntity report, List<UserEntity> users, AdminEntity admin, String status, LocalDateTime createdAt) {
        this.report = report;
        this.users = users;
        this.admin = admin;
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

    public AdminEntity getAdmin() {
        return admin;
    }

    public void setAdmin(AdminEntity admin) {
        this.admin = admin;
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