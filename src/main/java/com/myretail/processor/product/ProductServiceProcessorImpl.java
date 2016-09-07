package com.myretail.processor.product;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.myretail.exception.handler.ServiceUnavailableException;
import com.myretail.product.domain.Price;
import com.myretail.product.domain.Product;
import com.myretail.product.domain.ProductAttributesResponse;
import com.myretail.serviceprovider.impl.ProductAttributesServiceProviderImpl;

@Component
public class ProductServiceProcessorImpl {
	
	@Autowired
	private ProductAttributesServiceProviderImpl attributeProvider;
	
	@Autowired
	private CassandraOperations cassandraOperations;
	
	public Product process(Long productId) throws ServiceUnavailableException {
		
		Product product = null;
		ProductAttributesResponse productAttribute = attributeProvider.getProductAttributes(productId);
		Price price = getProductPrice(productId);
		
		if(productAttribute != null && StringUtils.isNotBlank(productAttribute.getName()) && price != null){
			product = new Product();
			product.setId(productId);
			product.setName(productAttribute.getName());
			product.setCurrent_price(price);
		}
		
		
		return product;
		
	}

	private Price getProductPrice(Long productId) {
		// TODO Auto-generated method stub
		Select select = QueryBuilder.select().from("price");
		select.where(QueryBuilder.eq("productid", productId));

		Price p = cassandraOperations.selectOne(select, Price.class);
		
		return p;
	}

}
