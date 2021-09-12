package com.manuanand.stocksimulator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Portfolio {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Integer userId;
	
	private Integer stockId;
	
	private Integer numStocks;
	
	private Double pricePerStock;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getStockId() {
		return stockId;
	}

	public void setStockId(Integer stockId) {
		this.stockId = stockId;
	}

	public Integer getNumStocks() {
		return numStocks;
	}

	public void setNumStocks(Integer numStocks) {
		this.numStocks = numStocks;
	}

	public Double getPricePerStock() {
		return pricePerStock;
	}

	public void setPricePerStock(Double pricePerStock) {
		this.pricePerStock = pricePerStock;
	}
}