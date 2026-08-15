package com.example.personalproject.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationException(
    MethodArgumentNotValidException exception) {

    String message = exception.getBindingResult()
      .getFieldErrors()
      .stream()
      .findFirst()
      .map(error -> error.getDefaultMessage())
      .orElse("資料格式錯誤");

    return ResponseEntity
      .badRequest()
      .body(message);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(
    IllegalArgumentException exception) {

    return ResponseEntity
      .badRequest()
      .body(exception.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(
    AccessDeniedException exception) {

    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(exception.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(
    IllegalStateException exception) {

    return ResponseEntity
      .status(HttpStatus.SERVICE_UNAVAILABLE)
      .body(exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception exception) {

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body("系統發生錯誤");
  }
}
