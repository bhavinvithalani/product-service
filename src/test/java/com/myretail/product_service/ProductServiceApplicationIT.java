package com.myretail.product_service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.exception.handler.ServiceUnavailableException;
import com.myretail.product.domain.ProductAttributesResponse;
import com.myretail.product.service.resources.ProductResourceImpl.ProductResponse;
import com.myretail.service.application.Application;
import com.myretail.serviceprovider.impl.ProductAttributesServiceProviderImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest(randomPort = true)
public class ProductServiceApplicationIT {

	@Value("${local.server.port}")
	private int port;

	private RestTemplate clientRestTemplate = new TestRestTemplate();

	@Autowired
	private ProductAttributesServiceProviderImpl attributeProvider;

	@Before
	public void setup() throws JsonGenerationException, JsonMappingException,
			IOException, ServiceUnavailableException {
		MockitoAnnotations.initMocks(this);
		//MockitoAnnotations.initMocks(ProductAttributesServiceProviderImpl.class);
	}

	@Test
	public void testProductService() {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("x-int", "YES");
		ProductAttributesResponse attr = getAttributes();
		try {
			when(attributeProvider.getProductAttributes(123456l)).thenReturn(
					attr);
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity<ProductResponse> response = clientRestTemplate.exchange(
				getUrl(123456l), HttpMethod.GET, entity,
				ProductResponse.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue("BIG LEBOWSKI, THE Blu-ray".equalsIgnoreCase(response
				.getBody().getProduct().getName()));
	}

	@Test
	public void testProductServiceWithNoProductData() {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("x-int", "YES");

		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity<ProductResponse> response = clientRestTemplate.exchange(
				getUrl(12345l), HttpMethod.GET, entity, ProductResponse.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testProductServiceWithNoPrice() {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("x-int", "YES");

		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity<ProductResponse> response = clientRestTemplate.exchange(
				getUrl(15117729l), HttpMethod.GET, entity,
				ProductResponse.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	private String getUrl() {
		return getUrl(null);
	}

	private String getUrl(Long itemId) {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromPath("/v1/products/" + itemId).host("localhost")
				.port(port).scheme("http");

		return builder.build().encode().toUri().toString();
	}

	

	public ProductAttributesResponse getAttributes() {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		ClassLoader classLoader = getClass().getClassLoader();
		String jsonResponse = null;
		ProductAttributesResponse response = null;
		try {
			InputStream stream = classLoader
					.getResourceAsStream("attribute-test-data/attribute_good.json");
			if (stream != null) {

				jsonResponse = IOUtils.toString(stream);

			}
			// Object to JSON in file

			response = mapper.readValue(jsonResponse,
					ProductAttributesResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}

}
