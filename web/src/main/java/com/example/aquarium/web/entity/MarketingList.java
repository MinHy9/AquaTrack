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



@Entity
public class MarketingList {
	
	public enum Status{
		AVAILABLE,SOLD
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int listingId;
	
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
	
	public MarketingList() {};
	
	public MarketingList(String title, String productType, String description, BigDecimal price,
			Status status) {
		super();
		this.title = title;
		this.productType = productType;
		this.description = description;
		this.price = price;
		this.status = status;
	}

	@Override
	public String toString() {
		return "MakeringList [listingId=" + listingId + ", title=" + title + ", productType=" + productType
				+ ", description=" + description + ", price=" + price + ", status=" + status + "]";
	}

	public int getListingId() {
		return listingId;
	}

	public void setListingId(int listingId) {
		this.listingId = listingId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
}
