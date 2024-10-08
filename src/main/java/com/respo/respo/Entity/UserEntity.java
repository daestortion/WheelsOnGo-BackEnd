package com.respo.respo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Base64; // Import Base64

@Entity
@Table(name = "tblUsers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore Hibernate proxy properties
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	
	@Column(name = "username")
	private String username; 
	
	@Column(name = "fName")
	private String fName;
	
	@Column(name = "isRenting")
	private boolean isRenting = false;
	
	@Column(name = "isOwner")
	private boolean isOwner = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user"}) // Prevent recursion
    private VerificationEntity verification;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnoreProperties({"user", "chat"}) // Ignore 'user' and 'chat' in ReportEntity
    private List<ReportEntity> reports; // List to store reports associated with the user

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"owner"}) // Prevent recursion
    private List<CarEntity> cars; // List to store cars associated with the user
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user"}) // Prevent recursion
    private List<OrderEntity> orders; // List to store orders associated with the user
	
	public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

	@CreationTimestamp
    @Column(name = "timeStamp", updatable = false)
    private LocalDateTime timeStamp;

	@Lob
	@Column(name = "profilePic")
	private byte[] profilePic;
	
	@Column(name = "lName")
	private String lName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "pWord")
	private String pWord;

	@Column(name = "pNum")
	private String pNum;

	@Column(name = "is_deleted")
	private boolean isDeleted = false;
	
	public UserEntity() {}
	
	public UserEntity(int userId, String username, String fName, List<CarEntity> cars, byte[] profilePic, String lName,
			String email, String pWord, String pNum, List<OrderEntity> orders, LocalDateTime timestamp) {
		super();
		this.userId = userId;
		this.username = username;
		this.orders = orders;
		this.fName = fName;
		this.isRenting = false;
		this.cars = cars;
		this.profilePic = profilePic;
		this.lName = lName;
		this.email = email;
		this.pWord = pWord;
		this.pNum = pNum;
		this.isDeleted = false;
		this.timeStamp = timestamp;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getfName() {
		return fName;
	}

	public List<ReportEntity> getReports() {
        return reports;
    }

    public void setReports(List<ReportEntity> reports) {
        this.reports = reports;
    }

	public void setfName(String fName) {
		this.fName = fName;
	}

	public boolean isRenting() {
		return isRenting;
	}

	public void setRenting(boolean isRenting) {
		this.isRenting = isRenting;
	}

	public void setOwner(boolean isOwner){
		this.isOwner = isOwner ;
	}

	public boolean isOwner(){
		return this.isOwner;
	}

	public List<CarEntity> getCars() {
		return cars;
	}

	public void setCars(List<CarEntity> cars) {
		this.cars = cars;
	}

	public String getlName() {
		return lName;
	}
	
	public byte[] getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getpWord() {
		return pWord;
	}

	public void setpWord(String pWord) {
		this.pWord = pWord;
	}

	public String getpNum() {
		return pNum;
	}

	public void setpNum(String pNum) {
		this.pNum = pNum;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

    public String getProfilePicBase64() {
        return this.profilePic != null ? Base64.getEncoder().encodeToString(this.profilePic) : null;
    }

    public VerificationEntity getVerification() {
        return verification;
    }

    public void setVerification(VerificationEntity verification) {
        this.verification = verification;
    }

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	
}
