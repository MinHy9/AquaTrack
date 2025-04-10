package com.example.aquarium.web.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	@Email
	private String email;
	
	@Column(nullable = false)
	private String phoneNumber;
	
	@Column(nullable = false)
	private String password;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy="user",cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Aquarium> aquariums; 
	
	@OneToMany(mappedBy="user",cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<BoardPost> posts;
	
	@OneToMany(mappedBy="user",cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<MarketingList> marketingList;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Alert> alerts;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Notification> notifications;
	

	public User() {}
	public User(String username, String email, LocalDateTime createdAt,String phoneNumber,String password) {
		super();
		this.username = username;
		this.email = email;
		this.createdAt = createdAt;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}
	
	public void addAlert(Alert alert) {
	    this.alerts.add(alert);
	    alert.setUser(this);
	}

	
	public List<Notification> getNotifications() {
	    if (this.alerts == null) {
	        return List.of();
	    }

	    return this.alerts.stream()
	        .map(Alert::getNotification)
	        .filter(Objects::nonNull)
	        .collect(Collectors.toList());
	}
	

	@Override
	public String toString() {
		return "User [id=" + userId + ", username=" + username + ", email=" + email + ", created_at=" + createdAt + "]";
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<Aquarium> getAquariums() {
		return aquariums;
	}

	public void setAquariums(List<Aquarium> aquariums) {
		this.aquariums = aquariums;
	}

	public List<BoardPost> getPosts() {
		return posts;
	}

	public void setPosts(List<BoardPost> posts) {
		this.posts = posts;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}
	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}
	public List<MarketingList> getMarketingList() {
		return marketingList;
	}

	public void setMarketingList(List<MarketingList> marketingList) {
		this.marketingList = marketingList;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
