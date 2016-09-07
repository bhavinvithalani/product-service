package com.myretail.product.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.myretail.serviceprovider.impl.ProductAttributeDeserializer;

@JsonDeserialize(using=ProductAttributeDeserializer.class)
public class ProductAttributesResponse {
	
	private Long productId;
	
	private String name;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String str = "{\nProductAttributeData-->\n";
		str += "productId-->"+productId+"\n";
		str += "name-->"+name;
		return str;
	}	

}
