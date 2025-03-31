package com.example.aquarium.web.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Device {
	public enum DeviceType{
		TEMPERATURE_SENSEOR,PH_SENSOR,TURBIDITY_SENSOR
	}
	public enum DeviceStatus{
		ACTIVE,INACTIVE,ERROR
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int deviceId;
	
	@Enumerated(EnumType.STRING)
	private DeviceType deviceType;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DeviceStatus deviceStatus=DeviceStatus.ACTIVE;
	
	@CreationTimestamp
	private LocalDateTime lastChecked;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Aquarium aquarium;
	
	@OneToMany(mappedBy="device")
	@JsonIgnore
	private List <DeviceReading> deviceReading;

	public int getDeviceId() {
		return deviceId;
	}

	@Override
	public String toString() {
		return "Device [deviceId=" + deviceId + ", deviceType=" + deviceType + ", deviceStatus=" + deviceStatus
				+ ", lastChecked=" + lastChecked + "]";
	}
	public Device() {};
	
	public Device(DeviceType deviceType, DeviceStatus deviceStatus, LocalDateTime lastChecked) {
		super();
		this.deviceType = deviceType;
		this.deviceStatus = deviceStatus;
		this.lastChecked = lastChecked;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public DeviceStatus getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(DeviceStatus deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public LocalDateTime getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(LocalDateTime lastChecked) {
		this.lastChecked = lastChecked;
	}

	public Aquarium getAquarium() {
		return aquarium;
	}

	public void setAquarium(Aquarium aquarium) {
		this.aquarium = aquarium;
	}

}
