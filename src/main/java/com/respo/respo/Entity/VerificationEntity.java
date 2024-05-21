package com.respo.respo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tblVerification")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore Hibernate proxy properties
public class VerificationEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vId;
	
	@ManyToOne
    @JoinColumn(name = "user")
    @JsonIgnoreProperties({"verification", "cars"}) // Prevent recursion
    private UserEntity user;
	
	@Column(name = "status")
    private int status;

	@Lob
    @Column(name = "govId")
    private byte[] govId;

    @Lob
    @Column(name = "driversLicense")
    private byte[] driversLicense;

	public VerificationEntity() {
	super();
	}
	
	public VerificationEntity(int vId, UserEntity user, byte[] govId, byte[] driversLicense) {
		super();
		this.vId = vId;
		this.user = user;
		this.status = 0;
		this.govId = govId;
		this.driversLicense = driversLicense;
	}

	public int getVId() {
		return vId;
	}

	public void setVId(int vId) {
		this.vId = vId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public byte[] getGovId() {
		return govId;
	}

	public void setGovId(byte[] govId) {
		this.govId = govId;
	}

	public byte[] getDriversLicense() {
		return driversLicense;
	}

	public void setDriversLicense(byte[] driversLicense) {
		this.driversLicense = driversLicense;
	}
}
