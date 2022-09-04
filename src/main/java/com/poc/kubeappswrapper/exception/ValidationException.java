package com.poc.kubeappswrapper.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException(String exceptionstr) {
		super(exceptionstr);
		log.error(exceptionstr);
	}
}
