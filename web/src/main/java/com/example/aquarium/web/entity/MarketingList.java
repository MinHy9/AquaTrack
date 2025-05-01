package com.example.aquarium.web.entity;

import java.math.BigDecimal;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MarketingList {
	
	public enum Status{
		AVAILABLE,SOLD
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long listingId;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String productType;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(precision = 10, scale = 2)
    private BigDecimal price;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status=Status.AVAILABLE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
}
