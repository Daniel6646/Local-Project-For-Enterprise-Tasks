package com.debuglab.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String customerName;

    private Double amount;

    private String status;

    private String filePath;

    private LocalDateTime createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "OrderEntity [id=" + id + ", customerName=" + customerName + ", amount=" + amount + ", status=" + status
				+ ", filePath=" + filePath + ", createdAt=" + createdAt + "]";
	}

	public OrderEntity(Long id, String customerName, Double amount, String status, String filePath,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.customerName = customerName;
		this.amount = amount;
		this.status = status;
		this.filePath = filePath;
		this.createdAt = createdAt;
	}

	public OrderEntity() {
		super();
	}
	
	


}

