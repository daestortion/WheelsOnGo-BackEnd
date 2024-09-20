package com.respo.respo.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblOrders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties({ "cars", "verification", "orders" }) // Ignore specific nested objects during serialization
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "carId")
    @JsonIgnoreProperties({ "orders" })
    private CarEntity car;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "totalPrice")
    private float totalPrice;

    @Column(name = "paymentOption")
    private String paymentOption;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "referenceNumber", unique = true)
    private String referenceNumber; // Unique reference number

    @Lob
    @Column(name = "payment")
    private byte[] payment;

    @Column(name = "status")
    private int status;

    @Column(name = "active")
    private boolean isActive = false;

    @Column(name = "deliveryOption")
    private String deliveryOption; // New attribute for delivery option

    @CreationTimestamp
    @Column(name = "timeStamp", updatable = false)
    private LocalDateTime timeStamp;

    @Column(name = "deliveryAddress")
    private String deliveryAddress; // New attribute for delivery address

    @Column(name = "isReturned")
    private boolean isReturned = false; // New attribute for return status

    @Column(name = "returnDate")
    private LocalDate returnDate; // New attribute for return date

    @Lob
    @Column(name = "proofOfReturn")
    private byte[] proofOfReturn; // New attribute for proof of return

    public OrderEntity() {
    }

    public OrderEntity(int orderId, UserEntity user, CarEntity car, LocalDate startDate, LocalDate endDate,
            float totalPrice,
            String paymentOption, boolean isDeleted, String referenceNumber, byte[] payment, int status,
            boolean isActive,
            String deliveryOption, String deliveryAddress, LocalDateTime timestamp, boolean isReturned,
            LocalDate returnDate, byte[] proofOfReturn) {
        this.orderId = orderId;
        this.user = user;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.paymentOption = paymentOption;
        this.isDeleted = isDeleted;
        this.referenceNumber = referenceNumber;
        this.payment = payment;
        this.status = status;
        this.isActive = isActive;
        this.deliveryOption = deliveryOption;
        this.deliveryAddress = deliveryAddress;
        this.timeStamp = timestamp;
        this.isReturned = isReturned;
        this.returnDate = returnDate;
        this.proofOfReturn = proofOfReturn;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean isReturned) {
        this.isReturned = isReturned;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public byte[] getProofOfReturn() {
        return proofOfReturn;
    }

    public void setProofOfReturn(byte[] proofOfReturn) {
        this.proofOfReturn = proofOfReturn;
    }

    public String generateReferenceNumber() {
        Random random = new Random();
        return String.format("%08d", random.nextInt(100000000)); // Generates an 8-digit random number
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CarEntity getCar() {
        return car;
    }

    public void setCar(CarEntity car) {
        this.car = car;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public byte[] getPayment() {
        return payment;
    }

    public void setPayment(byte[] payment) {
        this.payment = payment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        this.isActive = status == 1;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}
