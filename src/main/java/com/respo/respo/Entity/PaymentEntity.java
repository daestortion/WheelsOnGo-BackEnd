package com.respo.respo.Entity;

import javax.persistence.*;

@Entity
@Table(name = "tblPayments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @Lob
    @Column(name = "proof")
    private byte[] proof;

    @OneToOne
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private OrderEntity order;
    
    @JoinColumn(name = "status")
    private int status;
    
    public PaymentEntity() {
    }

    public PaymentEntity(int paymentId, byte[] proof, OrderEntity order, int status) {
        this.paymentId = paymentId;
        this.proof = proof;
        this.order = order;
        this.status = status;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public byte[] getProof() {
        return proof;
    }

    public void setProof(byte[] proof) {
        this.proof = proof;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    
    
}
