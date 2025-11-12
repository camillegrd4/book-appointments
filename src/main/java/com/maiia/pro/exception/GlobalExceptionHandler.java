package com.maiia.pro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
	private static final String TIMESTAMP = "timestamp";
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgs(IllegalArgumentException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(ERROR, "IllegalArgumentException");
		body.put(MESSAGE, ex.getMessage());

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(ERROR, "NotFoundException");
		body.put(MESSAGE, ex.getMessage());

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(BadRequestException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(ERROR, "BadRequestException");
		body.put(MESSAGE, ex.getMessage());

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
		Map<String, Object> body = new HashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(ERROR, ex.getClass().getSimpleName());
		body.put(MESSAGE, ex.getMessage());

		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}