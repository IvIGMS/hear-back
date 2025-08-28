package com.app.hear.users.dao.dto;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Builder
public class UserDownDto {
  private String email;
  private String name;
  private String lastName;
  private Long companyId;
  private Long userId;
}
