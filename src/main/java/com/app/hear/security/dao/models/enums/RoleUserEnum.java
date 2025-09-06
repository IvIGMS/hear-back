package com.app.hear.security.dao.models.enums;

public enum RoleUserEnum {
  USER("USER"),
  ADMIN("ADMIN");

  private final String role;

  RoleUserEnum(String role) {
    this.role = role;
  }

  public String getValue() {
    return role;
  }
}
