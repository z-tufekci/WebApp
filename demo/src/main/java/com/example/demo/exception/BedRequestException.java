package com.example.demo.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BedRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
}