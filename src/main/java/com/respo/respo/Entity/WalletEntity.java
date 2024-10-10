package com.respo.respo.Entity;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblWallets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int walletId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    @JsonIgnoreProperties({"wallet", "hibernateLazyInitializer", "handler"})
    private UserEntity user;

    @Column(name = "balance", nullable = false)
    private double balance;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public WalletEntity() {}

    public WalletEntity(int walletId, UserEntity user, double balance, boolean isActive) {
        this.walletId = walletId;
        this.user = user;
        this.balance = balance;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
