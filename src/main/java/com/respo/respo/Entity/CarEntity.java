package com.respo.respo.Entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;

@Entity
@Table(name = "tblCars")
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int carId;

    @Column(name = "carBrand")
    private String carBrand;
    
    @Column(name = "carModel")
    private String carModel;

	@Column(name = "carDescription")
    private String carDescription;

	@Column(name = "isRented")
	private boolean isRented = false;
		
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ownerId", referencedColumnName = "userId")  // Make sure 'userId' is correct
    @JsonIgnoreProperties({"cars","orders"})
    private UserEntity owner;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"car", "user"})
    private List<OrderEntity> orders = new ArrayList<>();

	@Column(name = "carYear")
    private String carYear;

	@Column(name = "Address")
    private String Address;

	@Column(name = "rentPrice")
    private float rentPrice;
    
    @Lob
	@Column(name = "carImage")
	private byte[] carImage;
    
    @Lob
	@Column(name = "carOR")
	private byte[] carOR;
    
    @Lob
	@Column(name = "carCR")
	private byte[] carCR;

	@Column(name = "isApproved")
    private boolean isApproved = false;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

	@Column(name = "color")
	private String color;

	@Column(name = "maxSeatingCapacity")
	private int maxSeatingCapacity;

    public CarEntity() {}

	public CarEntity(int carId, String carBrand, String carModel, String carDescription, UserEntity owner, 
                     String carYear, String Address, float rentPrice, byte[] carImage, byte[] carOR, byte[] carCR, 
                     boolean isApproved, String color, int maxSeatingCapacity) {
		super();
		this.carId = carId;
		this.carBrand = carBrand;
		this.carModel = carModel;
		this.carDescription = carDescription;
		this.owner = owner;
		this.carYear = carYear;
		this.Address = Address;
		this.rentPrice = rentPrice;
		this.carImage = carImage;
		this.carOR = carOR;
		this.carCR = carCR;
		this.isApproved = isApproved;
		this.color = color;
		this.maxSeatingCapacity = maxSeatingCapacity;
		this.isDeleted = false;
		this.isRented = false;
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public String getCarBrand() {
		return carBrand;
	}

	public void setCarBrand(String carBrand) {
		this.carBrand = carBrand;
	}

	public String getCarModel() {
		return carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getCarDescription() {
		return carDescription;
	}

	public void setCarDescription(String carDescription) {
		this.carDescription = carDescription;
	}

	public String getCarYear() {
		return carYear;
	}

	public void setCarYear(String carYear) {
		this.carYear = carYear;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String Address) {
		this.Address = Address;
	}

	public float getRentPrice() {
		return rentPrice;
	}

	public void setRentPrice(float rentPrice) {
		this.rentPrice = rentPrice;
	}

	public byte[] getCarImage() {
		return carImage;
	}

	public void setCarImage(byte[] carImage) {
		this.carImage = carImage;
	}

	public byte[] getCarOR() {
		return carOR;
	}

	public void setCarOR(byte[] carOR) {
		this.carOR = carOR;
	}

	public byte[] getCarCR() {
		return carCR;
	}

	public void setCarCR(byte[] carCR) {
		this.carCR = carCR;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isRented() {
		return isRented;
	}

	public void setRented(boolean isRented) {
		this.isRented = isRented;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getMaxSeatingCapacity() {
		return maxSeatingCapacity;
	}

	public void setMaxSeatingCapacity(int maxSeatingCapacity) {
		this.maxSeatingCapacity = maxSeatingCapacity;
	}

	public UserEntity getOwner() {
		return owner;
	}

	public void setOwner(UserEntity owner) {
		this.owner = owner;
	}

    public List<OrderEntity> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderEntity> orders) {
		this.orders = orders;
	}

    public void addOrder(OrderEntity order) {
        if (orders == null) {
            orders = new ArrayList<>();
        }
        orders.add(order);
    }
}

	
