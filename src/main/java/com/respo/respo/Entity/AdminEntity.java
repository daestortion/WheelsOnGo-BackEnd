package com.respo.respo.Entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tblAdmins")
public class AdminEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;
	
	@Column(name = "userName")
	private String username; 
	
	@Column(name = "passWord")
	private String pWord;
	
	@Column(name = "isDeleted")
	private boolean isDeleted = false;

	@CreationTimestamp
    @Column(name = "timeStamp", updatable = false)
    private LocalDateTime timeStamp;

	public AdminEntity() {}

	public AdminEntity(int adminId, String username, String pWord, boolean isDeleted, LocalDateTime timestamp) {
		super();
		this.adminId = adminId;
		this.username = username;
		this.pWord = pWord;
		this.isDeleted = isDeleted;
		this.timeStamp = timestamp;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getpWord() {
		return pWord;
	}

	public void setpWord(String pWord) {
		this.pWord = pWord;
	}

	public boolean getisDeleted() {
		return isDeleted;
	}

	public void setisDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	
}
