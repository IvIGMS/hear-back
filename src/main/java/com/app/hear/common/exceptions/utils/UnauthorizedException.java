package com.app.hear.common.exceptions.utils;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException(String message, Object... args) {
    super(String.format(message, args));
  }
}
