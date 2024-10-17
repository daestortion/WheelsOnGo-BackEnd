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

    // New attributes
    @Column(name = "credit", nullable = false)
    private float credit;

    @Column(name = "debit", nullable = false)
    private float debit;

    @Column(name = "refundable", nullable = false)
    private float refundable;

    public WalletEntity() {}

    public WalletEntity(int walletId, UserEntity user, double balance, boolean isActive, float credit, float debit, float refundable) {
        this.walletId = walletId;
        this.user = user;
        this.balance = balance;
        this.isActive = isActive;
        this.credit = credit;
        this.debit = debit;
        this.refundable = refundable;
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

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public float getDebit() {
        return debit;
    }

    public void setDebit(float debit) {
        this.debit = debit;
    }

    public float getRefundable() {
        return refundable;
    }

    public void setRefundable(float refundable) {
        this.refundable = refundable;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
