package com.maiia.pro.exception;

public class BadRequestException extends Exception {
	public BadRequestException(String errorMessage) {
		super(errorMessage);
	}
}
