package com.respo.respo.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.CascadeType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblOrders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"order"})  // Prevent recursion
    private ReturnProofEntity returnProof;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties({"cars", "verification", "orders", "chat", "report"})
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "carId")
    @JsonIgnoreProperties({"orders", "report"})
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

    @Column(name = "status")
    private int status;

    @Column(name = "active")
    private boolean isActive = false;

    @Column(name = "deliveryOption")
    private String deliveryOption;

    @CreationTimestamp
    @Column(name = "timeStamp", updatable = false)
    private LocalDateTime timeStamp;

    @Column(name = "deliveryAddress")
    private String deliveryAddress;

    @Column(name = "isReturned")
    private boolean isReturned = false;

    @Column(name = "returnDate")
    private LocalDate returnDate;

    @Column(name = "isTerminated")
    private boolean isTerminated = false;

    @Column(name = "terminationDate")
    private LocalDate terminationDate;

    @OneToMany(mappedBy = "order")
    @JsonIgnoreProperties({"order"}) // Prevent recursion
    private List<PaymentEntity> payments;

    public OrderEntity() {
    }
    
    public OrderEntity(int orderId, UserEntity user, CarEntity car, LocalDate startDate, LocalDate endDate,
            float totalPrice, String paymentOption, boolean isDeleted, String referenceNumber, int status,
            boolean isActive, String deliveryOption, LocalDateTime timeStamp, String deliveryAddress,
            boolean isReturned, LocalDate returnDate, boolean isTerminated, LocalDate terminationDate,
            List<PaymentEntity> payments) {
        this.orderId = orderId;
        this.user = user;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.paymentOption = paymentOption;
        this.isDeleted = isDeleted;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.isActive = isActive;
        this.deliveryOption = deliveryOption;
        this.timeStamp = timeStamp;
        this.deliveryAddress = deliveryAddress;
        this.isReturned = isReturned;
        this.returnDate = returnDate;
        this.isTerminated = isTerminated;
        this.terminationDate = terminationDate;
        this.payments = payments;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated(boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public List<PaymentEntity> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentEntity> payments) {
        this.payments = payments;
    }

    
    
}
