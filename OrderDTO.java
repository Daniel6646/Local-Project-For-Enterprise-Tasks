package com.debuglab.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderDTO {

    @NotBlank
    private String customerName;

    @Min(1)
    private Double amount;

    private String status;

    private String filePath;

    private LocalDateTime createdAt;
    
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

	@Override
	public String toString() {
		return "OrderDTO [customerName=" + customerName + ", amount=" + amount + "]";
	}



	public OrderDTO(@NotBlank String customerName, @Min(1) Double amount, String status, String filePath,
			LocalDateTime createdAt) {
		super();
		this.customerName = customerName;
		this.amount = amount;
		this.status = status;
		this.filePath = filePath;
		this.createdAt = createdAt;
	}

	public OrderDTO() {
		super();
	}




}

