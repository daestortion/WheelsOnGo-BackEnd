package com.respo.respo.Entity;

import java.time.LocalDate;
import java.util.Random;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblOrders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties({"cars", "verification", "orders"}) // Ignore specific nested objects during serialization
    private UserEntity user;
    
    @ManyToOne
    @JoinColumn(name = "carId")
    private CarEntity car;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "totalPrice")
    private float totalPrice;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "referenceNumber", unique = true)
    private String referenceNumber; // Unique reference number
    
    public OrderEntity() {}

    public OrderEntity(int orderId, UserEntity user, CarEntity car, LocalDate startDate, LocalDate endDate, float totalPrice, boolean isDeleted) {
        super();
        this.orderId = orderId;
        this.user = user;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.isDeleted = isDeleted;
        this.referenceNumber = generateReferenceNumber();
    }

    private String generateReferenceNumber() {
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
