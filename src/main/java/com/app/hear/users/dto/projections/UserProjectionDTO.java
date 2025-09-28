package com.app.hear.users.dto.projections;

public interface UserProjectionDTO {
  Long getId();

  String getEmail();

  String getFirstname();

  String getLastname();

  Boolean getIsEmailActive();

  String getRole();
}
