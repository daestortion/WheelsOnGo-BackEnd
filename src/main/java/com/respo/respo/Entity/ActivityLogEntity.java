package com.respo.respo.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblActivityLogs")
public class ActivityLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;

    @Column(name = "action")
    private String action; // e.g. "User JohnDoe has been created."

    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "username")
    private String username;

    public ActivityLogEntity() {}

    public ActivityLogEntity(String action, String username) {
        this.action = action;
        this.username = username;
        this.timestamp = LocalDateTime.now();
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}