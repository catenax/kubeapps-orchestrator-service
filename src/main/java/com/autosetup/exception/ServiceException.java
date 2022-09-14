package com.autosetup.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceException(String exceptionstr) {
		super(exceptionstr);
		log.error(exceptionstr);
	}
}