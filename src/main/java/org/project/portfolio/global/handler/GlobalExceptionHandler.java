package org.project.portfolio.global.handler;

import java.util.HashMap;
import java.util.Map;
import org.project.portfolio.global.constants.Message;
import org.project.portfolio.global.exception.DuplicateResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
    Map<String, String> details = new HashMap<>();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
    }
    Map<String, Object> response = new HashMap<>();
    response.put("message", Message.BAD_REQUEST);
    response.put("details", details);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<Map> handleDuplicateResource(DuplicateResourceException e) {

    Map<String, Object> response = new HashMap<>();
    response.put("message", Message.CONFLICT);
    response.put("detail", e.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }
}
