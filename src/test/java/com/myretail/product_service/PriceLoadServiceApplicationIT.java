package com.myretail.product_service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.myretail.product.service.resources.PriceResourceImpl.PriceDataResponse;
import com.myretail.product.service.resources.ProductResourceImpl.ProductResponse;
import com.myretail.service.application.Application;
import com.myretail.serviceprovider.impl.ProductAttributesServiceProviderImpl;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest(randomPort = true)
public class PriceLoadServiceApplicationIT {

	@Value("${local.server.port}")
	private int port;

	private RestTemplate clientRestTemplate = new TestRestTemplate();


	@Before
	public void setup() throws JsonGenerationException, JsonMappingException, IOException {
		
	}

	@Test
	public void testProductService() {

		
		String request =  "{\n  \"id\":12345,\n  \"price\":100.00,\n  \"currency\":\"USD\"\n}";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity(request, headers);
		ResponseEntity<PriceDataResponse> response = clientRestTemplate.exchange(getUrl(), HttpMethod.PUT, entity, PriceDataResponse.class);

		//System.out.println(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		//assertTrue("BIG LEBOWSKI, THE Blu-ray".equalsIgnoreCase(response.getBody().getProduct().getName()));
	}

		
	private String getUrl() {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromPath("/product/price")
				.host("localhost").port(port).scheme("http");
		
		

		return builder.build().encode().toUri().toString();
	}

	private String getUrl_jsonp() {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromPath("/LocalizationService/v1/localstore.jsonp")
				.host("localhost").port(port).scheme("http");

		return builder.build().encode().toUri().toString();
	}

	

}
