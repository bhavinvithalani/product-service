package com.myretail.serviceprovider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.myretail.exception.handler.ServiceUnavailableException;
import com.myretail.product.domain.ProductAttributesResponse;

@Component
public class ProductAttributesServiceProviderImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProductAttributesServiceProviderImpl.class);

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${attribute.service.url.prefix}")
	private String prefixUrl;

	@Value("${attribute.service.url.suffix}")
	private String suffixUrl;

	public ProductAttributesResponse getProductAttributes(Long productId) throws ServiceUnavailableException {

		ResponseEntity<ProductAttributesResponse> response = null;
		ProductAttributesResponse productAttributesResponse = null;

		String url = prefixUrl + productId + suffixUrl;
		
		try {
			response = restTemplate.getForEntity(url, ProductAttributesResponse.class);

			if (HttpStatus.OK == response.getStatusCode()) {
				productAttributesResponse = response.getBody();
			} else {
				LOGGER.error("Got not sucess http status code: "
						+ response.getStatusCode().value() + " for "
						+ url);
			}
		} catch (RestClientException e) {
			LOGGER.error(
					"RestClientException Exception occured while retrieving product attributes for item id:"
							+ productId, e);
			throw new ServiceUnavailableException("Unavailable reach attribute service for productid" + productId);
			
		}
		
		return productAttributesResponse;
	}

}
