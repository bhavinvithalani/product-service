package com.myretail.product_service;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.product.domain.ProductAttributesResponse;

public class AttributeUtil {

	public static Object getAttributes;

	public static ProductAttributesResponse getAttributes() throws JsonGenerationException, JsonMappingException, IOException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		ProductAttributesResponse obj = new ProductAttributesResponse();

		//Object to JSON in file
		mapper.writeValue(new File("/src/test/resources/attribute-test-data/attribute_good.json"), obj);
		return obj;

		
	}

}
