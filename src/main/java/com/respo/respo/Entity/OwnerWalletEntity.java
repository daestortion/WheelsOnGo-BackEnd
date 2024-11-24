package com.respo.respo.Entity;

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

    @Column(name = "onlineEarning")
    private float onlineEarning = 0.0f;

    @Column(name = "cashEarning")
    private float cashEarning = 0.0f;

    @Column(name = "cashRefundable")
    private float cashRefundable = 0.0f;

    // Constructors
    public OwnerWalletEntity() {}

    public OwnerWalletEntity(UserEntity user, float onlineEarning, float cashEarning, float cashRefundable) {
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

    public float getOnlineEarning() {
        return onlineEarning;
    }

    public void setOnlineEarning(float onlineEarning) {
        this.onlineEarning = onlineEarning;
    }

    public float getCashEarning() {
        return cashEarning;
    }

    public void setCashEarning(float cashEarning) {
        this.cashEarning = cashEarning;
    }

    public float getCashRefundable() {
        return cashRefundable;
    }

    public void setCashRefundable(float cashRefundable) {
        this.cashRefundable = cashRefundable;
    }
}
