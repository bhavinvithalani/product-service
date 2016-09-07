package com.myretail.product.domain;

import java.io.Serializable;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Table
@JsonIgnoreProperties(value = { "productId" })
public class Price implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name = "productid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Long productId;
	
	@Column
	private Double price;
	
	@Column
	private String currency;

	public Price() {
	}

	public Price(Long itemId, Double value, String curreny_code) {
		super();
		this.productId = itemId;
		this.price = value;
		this.currency = curreny_code;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCurreny_code() {
		return currency;
	}

	public void setCurreny_code(String curreny_code) {
		this.currency = curreny_code;
	}

}
