package com.example.aquarium.web.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	
	@JsonIgnore
	@Column(nullable = false)
	private String password;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private List<Aquarium> aquariums; 
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private List<BoardPost> posts;
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private List<MarketingList> marketingList;
	

	public User() {}
	public User(String username, String email, LocalDateTime createdAt) {
		super();
		this.username = username;
		this.email = email;
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "User [id=" + userId + ", username=" + username + ", email=" + email + ", created_at=" + createdAt + "]";
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
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

	public List<MarketingList> getMarketingList() {
		return marketingList;
	}

	public void setMarketingList(List<MarketingList> marketingList) {
		this.marketingList = marketingList;
	}
	
}
