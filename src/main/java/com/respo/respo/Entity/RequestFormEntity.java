package com.respo.respo.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblRequestForm")
public class RequestFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    @JsonIgnoreProperties({"orders", "cars", "chats", "wallet", "ownerWallet"}) // Prevent recursion
    private UserEntity user;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    @Column(name = "full_name", nullable = true) // Full name for GCash requests or Account Name for Bank
    private String fullName;

    @Column(name = "gcash_number", nullable = true) // GCash phone number (nullable for Bank requests)
    private String gcashNumber;

    @Column(name = "bank_name", nullable = true) // Bank name for Bank requests
    private String bankName;

    @Column(name = "account_number", nullable = true) // Bank account number (nullable for GCash requests)
    private String accountNumber;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private String status; // New status field for request tracking

    @Lob
    @Column(name = "proof_image", nullable = true) // Binary data for proof image
    private byte[] proofImage;

    @Column(name = "user_type", nullable = true) // New field for User Type
    private String userType;

    // Default constructor
    public RequestFormEntity() {
        this.createdAt = LocalDateTime.now(); // Automatically set the creation time
        this.status = "pending"; // Default status for a new request
    }

    // Unified constructor for both GCash and Bank requests
    public RequestFormEntity(UserEntity user, String requestType, String fullName, String gcashNumber, String bankName, String accountNumber, float amount, byte[] proofImage) {
        this.user = user;
        this.requestType = requestType;
        this.fullName = fullName; // This will be Account Name for Bank, Full Name for GCash
        this.gcashNumber = gcashNumber;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.createdAt = LocalDateTime.now(); // Automatically set the creation time
        this.status = "pending"; // Default status for a new request
        this.proofImage = proofImage; // Set the proof image
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGcashNumber() {
        return gcashNumber;
    }

    public void setGcashNumber(String gcashNumber) {
        this.gcashNumber = gcashNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getProofImage() {
        return proofImage;
    }

    public void setProofImage(byte[] proofImage) {
        this.proofImage = proofImage;
    }

    // Getters and Setters
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}