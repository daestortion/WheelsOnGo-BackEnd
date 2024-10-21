package com.respo.respo.Entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblReports")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;
    
    @Column(name = "status")
    private int status = 0;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "reports", "password", "messages", "chat"}) // Prevent recursion and sensitive info
    private UserEntity user;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({ "report", "messages" }) // Ignore 'report' field and 'messages' in ChatEntity
    private ChatEntity chat;


    @CreationTimestamp
    @Column(name = "timeStamp", updatable = false)
    private LocalDateTime timeStamp;

    public ReportEntity(int reportId, String title, String description, UserEntity user, LocalDateTime timestamp, ChatEntity chat) {
        this.reportId = reportId;
        this.title = title;
        this.description = description;
        this.user = user;
        this.timeStamp = timestamp;
        this.chat = chat;
    }

    
    public ReportEntity() {
    }


    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }


    public ChatEntity getChat() {
        return chat;
    }


    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    
}
