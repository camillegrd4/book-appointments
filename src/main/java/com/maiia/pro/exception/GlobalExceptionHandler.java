package com.maiia.pro.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgs(IllegalArgumentException ex) {
		logger.warn("IllegalArgumentException: {}", ex.getMessage());
		return new ResponseEntity<>(buildBody("IllegalArgumentException", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
		logger.warn("NotFoundException: {}", ex.getMessage());
		return new ResponseEntity<>(buildBody("NotFoundException", ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(BadRequestException ex) {
		logger.warn("BadRequestException: {}", ex.getMessage());
		return new ResponseEntity<>(buildBody("BadRequestException", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
		logger.warn("Exception: {}", ex.getMessage());
		return new ResponseEntity<>(buildBody(ex.getClass().getSimpleName(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private Map<String, Object> buildBody(String error, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("error", error);
		body.put("message", message);
		return body;
	}

}