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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	
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
	
	@Builder
	public User(String username, String email, LocalDateTime createdAt,String phoneNumber,String password) {
		super();
		this.username = username;
		this.email = email;
		this.createdAt = createdAt;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}
	
//	public void addAlert(Alert alert) {
//	    this.alerts.add(alert);
//	    alert.setUser(this);
//	}
//	
//	public List<Notification> getNotifications() {
//	    if (this.alerts == null) {
//	        return List.of();
//	    }
//
//	    return this.alerts.stream()
//	        .map(Alert::getNotification)
//	        .filter(Objects::nonNull)
//	        .collect(Collectors.toList());
//	}	
	
}
