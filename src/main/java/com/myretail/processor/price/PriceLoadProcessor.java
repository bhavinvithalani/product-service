package com.myretail.processor.price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.myretail.config.PriceRepo;
import com.myretail.product.domain.Price;
import com.myretail.product.service.resources.PriceResourceImpl.PriceData;

@Component
public class PriceLoadProcessor {

//	@Autowired
//	private PriceRepo priceRepo;
	
	@Autowired
	private CassandraOperations cassandraOperations;
	
	public boolean process(PriceData payload){
		
		Price price = new Price();
		price.setProductId(payload.getId());
		price.setCurreny_code(payload.getCurrency());
		price.setPrice(payload.getPrice());
		//priceRepo.save(price);
		try{
			cassandraOperations.insert(price);
		}catch(Exception e){
			return false;
		}
		return true;
				
	}
	
}
