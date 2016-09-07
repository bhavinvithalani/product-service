package com.myretail.exception.handler;

import java.io.Serializable;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ParamException;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ErrorMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/** contains the same HTTP Status code returned by the server */
	int status;

	/** application specific error code */
	String code;

	/** message describing the error */
	String message;

	/** extra information that might useful for developers */
	String developerMessage;

	public ErrorMessage() {
		//default no arg constructor
	}
	
	public ErrorMessage(ServiceUnavailableException ex) {
		this.status = Response.Status.SERVICE_UNAVAILABLE.getStatusCode();
		
		this.developerMessage = ex.getMessage();
	}

	public ErrorMessage(ParamException ex) {
		this.status = Response.Status.BAD_REQUEST.getStatusCode();
		this.code = Response.Status.BAD_REQUEST.toString();
		this.message = ex.getParameterName();
		this.developerMessage = ex.getMessage();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
}
