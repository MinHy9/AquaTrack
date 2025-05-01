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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class WaterQualityLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long logId;
	
	@Column(nullable=false)
	private float temperature;
	
	@Column(nullable=false)
	private float ph;

	@Column(nullable=false)
	private float turbidity;
	
	@CreationTimestamp
	public LocalDateTime recordedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	//@JsonIgnore
	private Aquarium aquarium;
		
	@OneToOne(mappedBy="waterQualityLog")
	@JsonIgnore
	private Alert alert;

}
