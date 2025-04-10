package com.example.aquarium.web.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Alert {
	
	public enum AlertType{
		TEMPARATURE_WARNING,PH_WARNING,TURBIDITY_WARNING
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int alertId;
	
	@Column(columnDefinition = "TEXT",nullable = false)
	private String message;
	
	private boolean resolved = false;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private AlertType alertType;
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private WaterQualityLog waterQualityLog;
	
	@OneToOne(mappedBy = "alert", cascade = CascadeType.ALL)
	@JsonIgnore
	private Notification notification;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Alert(String message, boolean resolved, LocalDateTime createdAt, AlertType alertType) {
		super();
		this.message = message;
		this.resolved = resolved;
		this.createdAt = createdAt;
		this.alertType = alertType;
	}
	public Alert() {
		
	}

	@Override
	public String toString() {
		return "Alert [alertId=" + alertId + ", message=" + message + ", resolved=" + resolved + ", createdAt="
				+ createdAt + ", alertType=" + alertType + "]";
	}

	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public AlertType getAlertType() {
		return alertType;
	}

	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}

	public WaterQualityLog getWaterQualityLog() {
		return waterQualityLog;
	}

	public void setWaterQualityLog(WaterQualityLog waterQualityLog) {
		this.waterQualityLog = waterQualityLog;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
}
