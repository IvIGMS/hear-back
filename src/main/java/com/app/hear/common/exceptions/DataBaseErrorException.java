package com.app.hear.common.exceptions;

public class DataBaseErrorException extends RuntimeException {
  public DataBaseErrorException(String message) {
    super(message);
  }

  public DataBaseErrorException(String message, Object... args) {
    super(String.format(message, args));
  }
}
