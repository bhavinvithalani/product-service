package com.myretail.exception.handler;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultExceptionMapper.class);

	private static final String NEW_LINE = "\n";

	@Context
	protected UriInfo uriInfo;

	@Context
	protected HttpHeaders headers;

	@Context
	protected SecurityContext securityContext;

	@Override
	public Response toResponse(Throwable ex) {
		LOGGER.error("Internal server error. Return status code: 500", ex);

		ErrorMessage errorMessage = new ErrorMessage();
		setHttpStatus(ex, errorMessage);
		errorMessage.setMessage(ex.getMessage());
		if (isDebug()) {
			errorMessage.setDeveloperMessage(getCustomStackTrace(ex));
		}
		return Response.status(errorMessage.getStatus()).entity(errorMessage)
				.build();
	}

	private void setHttpStatus(Throwable ex, ErrorMessage errorMessage) {
		if (ex instanceof WebApplicationException) {
			errorMessage.setStatus(((WebApplicationException) ex).getResponse()
					.getStatus());
			errorMessage.setCode(String.valueOf(((WebApplicationException) ex)
					.getResponse().getStatus()));
		} else {
			errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR
					.getStatusCode()); // defaults to internal server error 500
			errorMessage.setCode(Response.Status.INTERNAL_SERVER_ERROR
					.toString());
		}
	}

	private boolean isDebug() {
		String debugParam = uriInfo.getQueryParameters().getFirst("debug");
		if (StringUtils.isNotBlank(debugParam)
				&& "true".equalsIgnoreCase(debugParam)) {
			return true;
		}
		return false;
	}

	private String getCustomStackTrace(Throwable aThrowable) {
		StringBuilder result = new StringBuilder();
		result.append(aThrowable.toString());
		result.append(NEW_LINE);

		// add each element of the stack trace
		for (StackTraceElement element : aThrowable.getStackTrace()) {
			result.append(element);
			result.append(NEW_LINE);
		}
		return result.toString();
	}
}
