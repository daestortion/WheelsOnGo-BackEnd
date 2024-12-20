package com.respo.respo.Entity;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblOwnerWallet")
public class OwnerWalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int walletId;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @JsonIgnoreProperties({"ownerWallet", "verification", "reports", "chats", "cars", "orders", "wallet"})
    private UserEntity user;

    @OneToMany(mappedBy = "ownerWallet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TransactionEntity> transactions;
    
    @Column(name = "onlineEarning")
    private double onlineEarning = 0.0;

    @Column(name = "cashEarning")
    private double cashEarning = 0.0;

    @Column(name = "cashRefundable")
    private double cashRefundable = 0.0;

    // Constructors
    public OwnerWalletEntity() {}

    public OwnerWalletEntity(UserEntity user, double onlineEarning, double cashEarning, double cashRefundable) {
        this.user = user;
        this.onlineEarning = onlineEarning;
        this.cashEarning = cashEarning;
        this.cashRefundable = cashRefundable;
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

    public double getOnlineEarning() {
        return onlineEarning;
    }

    public void setOnlineEarning(double onlineEarning) {
        this.onlineEarning = onlineEarning;
    }

    public double getCashEarning() {
        return cashEarning;
    }

    public void setCashEarning(double cashEarning) {
        this.cashEarning = cashEarning;
    }

    public double getCashRefundable() {
        return cashRefundable;
    }

    public void setCashRefundable(double cashRefundable) {
        this.cashRefundable = cashRefundable;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }
}
