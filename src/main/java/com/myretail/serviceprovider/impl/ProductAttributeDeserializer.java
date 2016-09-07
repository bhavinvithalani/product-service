package com.myretail.serviceprovider.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.myretail.product.domain.ProductAttributesResponse;

public class ProductAttributeDeserializer extends JsonDeserializer<ProductAttributesResponse> {

	

	@Override
	public ProductAttributesResponse deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		ProductAttributesResponse productAttributeResponse = new ProductAttributesResponse();
		JsonNode rootNode = jp.getCodec().readTree(jp);
		JsonNode productCompositeResponseNode = rootNode.get("product_composite_response");	
		ArrayNode itemArray = (ArrayNode) productCompositeResponseNode.get("items");
		JsonNode itemNode = itemArray.get(0);
		JsonNode nameNode = itemNode.get("general_description");
		if(nameNode!=null){
			productAttributeResponse.setName(nameNode.asText());
		}
		return productAttributeResponse;
	}

}
