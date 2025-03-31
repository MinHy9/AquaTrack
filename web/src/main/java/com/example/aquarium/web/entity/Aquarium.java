package com.example.aquarium.web.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Aquarium {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int aquariumId;
	
	@CreationTimestamp
	private LocalDateTime registeredDate;
	
	public LocalDateTime getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(LocalDateTime registeredDate) {
		this.registeredDate = registeredDate;
	}

	public Aquarium() {};
	public Aquarium(LocalDateTime registeredDate) {
		super();
		this.registeredDate = registeredDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	@OneToMany(mappedBy="aquarium")
	@JsonIgnore
	private List<Device> devices;
	
	@OneToMany(mappedBy="aquarium")
	@JsonIgnore
	private List<WaterQualityLog> waterQualityLogList;
	
	public int getAquarimId() {
		return aquariumId;
	}

	public void setAquarimId(int aquarimId) {
		this.aquariumId = aquarimId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Aquarium [aquarimId=" + aquariumId + ", registeredDate=" + registeredDate + "]";
	}
}
