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
import jakarta.persistence.OneToOne;

@Entity
public class WaterQualityLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int logId;
	
	@Column(nullable=false)
	private float temperature;
	
	@Column(nullable=false)
	private float ph;

	@Column(nullable=false)
	private float turbidity;
	
	@CreationTimestamp
	public LocalDateTime recordedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Aquarium aquarium;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private DeviceReading deviceReading;
	
	@OneToOne(mappedBy="waterQualityLog")
	@JsonIgnore
	private Alert alert;

	@Override
	public String toString() {
		return "WaterQualityLog [logId=" + logId + ", temperature=" + temperature + ", ph=" + ph + ", turbidity="
				+ turbidity + ", recordedAt=" + recordedAt + "]";
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public WaterQualityLog() {};
	public WaterQualityLog(int logId, float temperature, float ph, float turbidity, LocalDateTime recordedAt) {
		super();
		this.logId = logId;
		this.temperature = temperature;
		this.ph = ph;
		this.turbidity = turbidity;
		this.recordedAt = recordedAt;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public float getPh() {
		return ph;
	}

	public void setPh(float ph) {
		this.ph = ph;
	}

	public float getTurbidity() {
		return turbidity;
	}

	public void setTurbidity(float turbidity) {
		this.turbidity = turbidity;
	}

	public LocalDateTime getRecordedAt() {
		return recordedAt;
	}

	public void setRecordedAt(LocalDateTime recordedAt) {
		this.recordedAt = recordedAt;
	}

	public Aquarium getAquarium() {
		return aquarium;
	}

	public void setAquarium(Aquarium aquarium) {
		this.aquarium = aquarium;
	}

	public DeviceReading getDeviceReading() {
		return deviceReading;
	}

	public void setDeviceReading(DeviceReading deviceReading) {
		this.deviceReading = deviceReading;
	}
}
