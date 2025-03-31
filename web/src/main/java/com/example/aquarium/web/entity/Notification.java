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
import jakarta.persistence.OneToOne;

@Entity
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationId;
	
	@Column(nullable = false,columnDefinition = "TEXT")
	private String message;
	
	@CreationTimestamp
	private LocalDateTime sentAt;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Alert alert;
	
	@Override
	public String toString() {
		return "Notification [notificationId=" + notificationId + ", message=" + message + ", sentAt=" + sentAt + "]";
	}

	public Notification(String message, LocalDateTime sentAt) {
		super();
		this.message = message;
		this.sentAt = sentAt;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

}
