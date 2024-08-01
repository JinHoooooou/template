package org.project.portfolio.global.exception;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedMediaTypeException extends AuthenticationException {

  public UnsupportedMediaTypeException(String message) {
    super(message);
  }
}
