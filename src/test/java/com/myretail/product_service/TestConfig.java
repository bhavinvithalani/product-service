package com.myretail.product_service;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.myretail.serviceprovider.impl.ProductAttributesServiceProviderImpl;

@Configuration
public class TestConfig {

	@Bean
	public ProductAttributesServiceProviderImpl attributeProvider(){
		return Mockito.mock(ProductAttributesServiceProviderImpl.class);

	}
}
