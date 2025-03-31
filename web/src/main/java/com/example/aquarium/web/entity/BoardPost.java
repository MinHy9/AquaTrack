package com.example.aquarium.web.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class BoardPost {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int postId;
	
	@Column(nullable = false)
	private String title;
	
	@Column(columnDefinition = "TEXT",nullable = false)
	private String content;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "BoardPost [postId=" + postId + ", title=" + title + ", content=" + content + ", createdAt=" + createdAt
				+ "]";
	}
	public BoardPost() {};

	public BoardPost(String title, String content, LocalDateTime createdAt) {
		super();
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
	}
	
	
	

}