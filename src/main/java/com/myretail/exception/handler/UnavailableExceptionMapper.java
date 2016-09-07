package com.myretail.exception.handler;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnavailableExceptionMapper implements
		ExceptionMapper<ServiceUnavailableException> {
	@Override
	public Response toResponse(ServiceUnavailableException ex) {
		return Response.status(Response.Status.SERVICE_UNAVAILABLE)
				.entity(new ErrorMessage(ex)).build();
	}

}
