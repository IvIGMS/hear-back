package com.app.hear.users.controllers;

import com.app.hear.api.UsersApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.model.TestDTO;
import com.app.hear.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController extends ControllerUtils implements UsersApi {
  private final UserService userService;

  @Value("${info.app.version}")
  private String appVersion;

  @Override
  public ResponseEntity<TestDTO> getTest() {
    return ResponseEntity.ok(TestDTO.builder().version(appVersion).build());
  }
}
