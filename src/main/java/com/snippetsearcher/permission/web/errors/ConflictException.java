package com.snippetsearcher.permission.web.errors;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
