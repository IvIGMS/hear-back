package com.app.hear.security.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.model.AuthenticationDTO;
import com.app.hear.model.AuthenticationRequestDTO;
import com.app.hear.model.RegisterRequestDTO;
import com.app.hear.model.RoleUser;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.security.dao.models.enums.RoleUserEnum;
import com.app.hear.users.dao.models.entities.UserConfigEntity;
import com.app.hear.users.dao.models.enums.Tier;
import com.app.hear.users.dao.repositories.UserRepository;
import com.app.hear.users.services.UserConfigService;
import com.app.hear.users.services.UserScopeService;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserConfigService userConfigService;
  private final UserScopeService userScopeService;
  private static final SecureRandom random = new SecureRandom();

  @Transactional
  public AuthenticationDTO register(RegisterRequestDTO request) {
    if (request.getRole().equals(RoleUser.ADMIN)) {
      throw new ServiceException(
          "No puedes registrar a un usuario con el role admin. Déjaselo a los pros");
    }
    var user =
        UserEntity.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .isActive(true) // todo: cambair esto a false para tener que activarla
            .role(RoleUserEnum.valueOf(request.getRole().getValue()))
            .build();
    userRepository.save(user);

    // Creamos el UserConfig
    userConfigService.createUserConfig(
        UserConfigEntity.builder()
            .user(user)
            .dummy("created")
            .scope(userScopeService.findByTier(Tier.FREE))
            .build());

    org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities("USER")
        .accountLocked(!user.getIsActive())
        .build();

    return AuthenticationDTO.builder().token(jwtService.generateToken(user)).build();
  }

  public AuthenticationDTO authenticate(AuthenticationRequestDTO request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (LockedException e) {
      throw new ConflictException("El usuario aún no ha confirmado su correo electrónico");
    } catch (Exception e) {
      throw new ConflictException("Error inesperado");
    }

    var jwtToken =
        jwtService.generateToken(
            userRepository.findByEmail(request.getEmail()).orElse(UserEntity.builder().build()));
    return AuthenticationDTO.builder().token(jwtToken).build();
  }
}
