package com.example.aquarium.web.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class DeviceReading {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int readingId;
	
	@OneToOne(mappedBy="deviceReading")
	private WaterQualityLog waterQualityLog;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Device device;
	
	@CreationTimestamp
	private LocalDateTime recordedAt;
	
	
	public WaterQualityLog getWaterQualityLog() {
		return waterQualityLog;
	}
	public void setWaterQualityLog(WaterQualityLog waterQualityLog) {
		this.waterQualityLog = waterQualityLog;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public LocalDateTime getRecordedAt() {
		return recordedAt;
	}
	public void setRecordedAt(LocalDateTime recordedAt) {
		this.recordedAt = recordedAt;
	}

	@Override
	public String toString() {
		return "DeviceReading [recordedAt=" + recordedAt + "]";
	}
	
	public DeviceReading() {}

	public DeviceReading(LocalDateTime recordedAt) {
		super();
		this.recordedAt = recordedAt;
	}
	
	
}
