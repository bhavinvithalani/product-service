package com.myretail.product.service.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.myretail.exception.handler.ServiceUnavailableException;
import com.myretail.processor.product.ProductServiceProcessorImpl;
import com.myretail.product.domain.Product;

@Component
@Path("v1/products")
public class ProductResourceImpl {
    
    @Autowired
    private ProductServiceProcessorImpl processor;

   

    @GET
	@Path("{productId : [0-9]*}")
	@Produces("application/json")
	public Response getProductById(@PathParam("productId") long productId) throws ServiceUnavailableException {
    	
    	Product product = processor.process(productId);
    	ProductResponse resp = new ProductResponse();
    	if(product != null){
    		resp.setProduct(product);
			return Response.status(200).entity(resp).build();
		}else{
			resp.setReasonCode("Product was not found");
			return Response.status(404).entity(resp).build();
		}
		
	}

    

    @JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(Include.NON_NULL)
	public static class ProductResponse {
		private String reasonCode;

		@JsonUnwrapped
		private Product product;

		public String getReasonCode() {
			return reasonCode;
		}

		public void setReasonCode(String reasonCode) {
			this.reasonCode = reasonCode;
		}

		public Product getProduct() {
			return product;
		}

		public void setProduct(Product product) {
			this.product = product;
		}



	}

}
