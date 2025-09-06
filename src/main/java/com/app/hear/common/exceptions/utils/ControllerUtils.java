package com.app.hear.common.exceptions.utils;

import com.app.hear.security.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class ControllerUtils {

  @Autowired protected HttpServletRequest request;

  @Autowired protected JwtService jwtService;

  protected URI createLocation(Long id) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri();
  }

  protected String getToken() {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    throw new RuntimeException("No se encontró token en la request");
  }

  protected Claims getAllClaims() {
    return jwtService.extractAllClaims(getToken());
  }

  protected String getRole() {
    return (String) jwtService.extractAllClaims(getToken()).get("role");
  }

  protected boolean checkIsAdmin() {
    return ControllerUtilsConstants.ADMIN_ROLE.equals(getRole());
  }

  protected boolean checkIsUser() {
    return ControllerUtilsConstants.USER_ROLE.equals(getRole());
  }
}
