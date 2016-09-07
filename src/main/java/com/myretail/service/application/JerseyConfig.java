package com.myretail.service.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.stereotype.Component;

//@ApplicationPath("ProductServices")
@Component
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(JacksonFeature.class);
		register(RequestContextFilter.class);

		packages("com.myretail.product.service.resources", "com.myretail.exception.handler");
	}
}
