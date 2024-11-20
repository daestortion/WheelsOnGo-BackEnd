package com.respo.respo.Entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tblReturnProof")
public class ReturnProofEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(name = "proof")
    private byte[] proof;

    @Lob
    @Column(name = "ownerProof")
    private byte[] ownerProof;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "returnDate")
    private LocalDate returnDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "penalty")
    private float penalty;

    @Column(name = "ownerApproval")
    private boolean ownerApproval;

    @Column(name = "ownerRemark")
    private String ownerRemark;

    @OneToOne
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private OrderEntity order;


    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getProof() {
        return proof;
    }

    public void setProof(byte[] proof) {
        this.proof = proof;
    }

    public byte[] getOwnerProof() {
        return ownerProof;
    }

    public void setOwnerProof(byte[] ownerProof) {
        this.ownerProof = ownerProof;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public float getPenalty() {
        return penalty;
    }

    public void setPenalty(float penalty) {
        this.penalty = penalty;
    }

    public boolean isOwnerApproval() {
        return ownerApproval;
    }

    public void setOwnerApproval(boolean ownerApproval) {
        this.ownerApproval = ownerApproval;
    }

    public String getOwnerRemark() {
        return ownerRemark;
    }

    public void setOwnerRemark(String ownerRemark) {
        this.ownerRemark = ownerRemark;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}
