package com.respo.respo.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblPayments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @ManyToOne
    @JoinColumn(name = "orderId")
    @JsonIgnoreProperties({ "payments", "user" }) // Prevent recursion
    private OrderEntity order;

    @Column(name = "amount")
    private float amount;
    
    @Column(name = "paymentDate")
    @CreationTimestamp
    private LocalDateTime paymentDate;

    @Lob
    @Column(name = "proofOfPayment")
    private byte[] proofOfPayment;

    @Column(name = "paymentMethod")
    private String paymentMethod;

    @Column(name = "status")
    private int status; // Status of the payment (e.g., paid, pending, etc.)

    @Column(name = "isRefunded")
    private boolean isRefunded = false;

    @Column(name = "refundDate")
    private LocalDateTime refundDate;

    @Column(name = "refundable")
    private float refundable; // Field to store refund amount

    public PaymentEntity() {
    }

    public PaymentEntity(int paymentId, OrderEntity order, float amount, LocalDateTime paymentDate,
            byte[] proofOfPayment, String paymentMethod, int status, boolean isRefunded, LocalDateTime refundDate, float refundable) {
        this.paymentId = paymentId;
        this.order = order;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.proofOfPayment = proofOfPayment;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.isRefunded = isRefunded;
        this.refundDate = refundDate;
        this.refundable = refundable; // Initialize refundable amount
    }

    // Getters and Setters

    public float getRefundable() {
        return refundable;
    }

    public void setRefundable(float refundable) {
        this.refundable = refundable;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public byte[] getProofOfPayment() {
        return proofOfPayment;
    }

    public void setProofOfPayment(byte[] proofOfPayment) {
        this.proofOfPayment = proofOfPayment;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isRefunded() {
        return isRefunded;
    }

    public void setRefunded(boolean isRefunded) {
        this.isRefunded = isRefunded;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }
}

