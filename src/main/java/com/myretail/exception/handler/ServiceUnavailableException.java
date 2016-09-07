package com.myretail.exception.handler;

public class ServiceUnavailableException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceUnavailableException() {
		super();
	}

	public ServiceUnavailableException(String errorMessage) {
		super(errorMessage);
	}
	
}
