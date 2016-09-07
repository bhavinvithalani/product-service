package com.myretail.product.service.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.myretail.processor.price.PriceLoadProcessor;

@Component
@Path("/product/price")
public class PriceResourceImpl {
	
	@Autowired
	PriceLoadProcessor priceProcessor;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response receive(PriceData payload) {
		PriceDataResponse resp = new PriceDataResponse();
		resp.addItem(payload);

		if (payload == null || StringUtils.isEmpty(payload.getId()) || StringUtils.isEmpty(payload.getPrice()) || StringUtils.isEmpty(payload.getCurrency())) {
			resp.setReasonCode("Pay load is missing data");
			return Response.status(400).entity(resp).build();
		}

		boolean success = priceProcessor.process(payload);
		if(success){
			return Response.status(200).entity(resp).build();
		}else{
			return Response.status(500).entity(resp).build();
		}
		
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(Include.NON_NULL)
	public static class PriceDataResponse {
		private String reasonCode;

		@JsonUnwrapped
		@JsonProperty(value = "item")
		private List<PriceData> items;

		public String getReasonCode() {
			return reasonCode;
		}

		public void setReasonCode(String reasonCode) {
			this.reasonCode = reasonCode;
		}

		public List<PriceData> getItems() {
			return items;
		}

		public void setItems(List<PriceData> items) {
			this.items = items;
		}

		public void addItem(PriceData item) {
			if (items == null) {
				items = new ArrayList<>();
			}
			items.add(item);
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PriceData {
		
		
		private Long id;
		private Double price;
		private String currency;

		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Double getPrice() {
			return price;
		}

		public void setTitle(Double price) {
			this.price = price;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

	}
}
