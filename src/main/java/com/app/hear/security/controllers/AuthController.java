package com.app.hear.security.controllers;

import com.app.hear.api.LoginApi;
import com.app.hear.api.RegisterApi;
import com.app.hear.model.AuthenticationDTO;
import com.app.hear.model.AuthenticationRequestDTO;
import com.app.hear.model.RegisterRequestDTO;
import com.app.hear.security.services.AuthenticationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements RegisterApi, LoginApi {

  private final AuthenticationService authenticationService;

  @Override
  public ResponseEntity<AuthenticationDTO> login(
      AuthenticationRequestDTO authenticationRequestDTO) {
    return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
  }

  @Override
  public ResponseEntity<AuthenticationDTO> register(RegisterRequestDTO registerRequestDTO) {
    return ResponseEntity.ok(authenticationService.register(registerRequestDTO));
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return RegisterApi.super.getRequest();
  }
}
