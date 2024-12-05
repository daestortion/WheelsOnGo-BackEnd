package com.respo.respo.Entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tblTransactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_wallet_id", referencedColumnName = "walletId", nullable = false)
    private OwnerWalletEntity ownerWallet;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "type", nullable = false)
    private String type;  // "credit" or "debit"

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public TransactionEntity() {}

    public TransactionEntity(OwnerWalletEntity ownerWallet, float amount, String type, LocalDateTime createdAt) {
        this.ownerWallet = ownerWallet;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public OwnerWalletEntity getOwnerWallet() {
        return ownerWallet;
    }

    public void setOwnerWallet(OwnerWalletEntity ownerWallet) {
        this.ownerWallet = ownerWallet;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
