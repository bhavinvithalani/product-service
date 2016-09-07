package com.myretail.config;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Component;

import com.myretail.product.domain.Price;

@Component
public interface PriceRepo extends CassandraRepository<Price> {

}
