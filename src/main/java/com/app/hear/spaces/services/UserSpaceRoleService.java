package com.app.hear.spaces.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.UserSpaceRoleDTO;
import com.app.hear.model.UserSpaceRoleRequestDTO;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.models.entities.UserSpaceRoleEntity;
import com.app.hear.spaces.dao.repositories.SpaceRepository;
import com.app.hear.spaces.dao.repositories.UserSpaceRoleRepository;
import com.app.hear.users.services.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSpaceRoleService {
  private final UserSpaceRoleRepository userSpaceRoleRepository;
  private final ModelMapper modelMapper;
  private final UserService userService;
  private final SpaceRepository spaceRepository;

  public UserSpaceRoleDTO createUserSpaceRole(
      UserSpaceRoleRequestDTO userSpaceRoleRequestDTO, Long ownerId) {
    userSpaceRoleRepository
        .findByUserIdAndSpaceId(
            userSpaceRoleRequestDTO.getUserId(), userSpaceRoleRequestDTO.getSpaceId())
        .ifPresent(
            userSpaceRole -> {
              throw new ConflictException(
                  "User with id "
                      + userSpaceRoleRequestDTO.getUserId()
                      + " already has a role in space with id "
                      + userSpaceRoleRequestDTO.getSpaceId());
            });

    UserEntity userEntity = userService.getUserEntityById(userSpaceRoleRequestDTO.getUserId());
    SpaceEntity spaceEntity = getSpaceById(userSpaceRoleRequestDTO.getSpaceId());

    UserSpaceRoleEntity userSpaceRole = new UserSpaceRoleEntity();
    userSpaceRole.setUser(userEntity);
    userSpaceRole.setSpace(spaceEntity);
    userSpaceRole.setRole(
        modelMapper.map(
            userSpaceRoleRequestDTO.getRole(),
            com.app.hear.spaces.dao.models.enums.RoleUserSpace.class));

    UserSpaceRoleEntity savedUserSpaceRole = userSpaceRoleRepository.save(userSpaceRole);

    return modelMapper.map(savedUserSpaceRole, UserSpaceRoleDTO.class);
  }

  public void cleanBeforeDeleteASpace(Long spaceId) {
    List<UserSpaceRoleEntity> spaceRoleToDelete = userSpaceRoleRepository.findBySpaceId(spaceId);
    if (!spaceRoleToDelete.isEmpty()) {
      spaceRoleToDelete.forEach(
          role -> {
            userSpaceRoleRepository.deleteById(role.getId());
          });
    } else {
      log.info("No hay ningún rol que limpiar en para este space");
    }
  }

  public UserSpaceRoleEntity getUserSpaceRoleByUserIdAndSpaceId(Long userId, Long spaceId) {
    getSpaceById(spaceId);
    return userSpaceRoleRepository
        .findByUserIdAndSpaceId(userId, spaceId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "El user " + userId + " no tiene permisos sobre el space " + spaceId));
  }

  public SpaceEntity getSpaceById(Long spaceId) {
    return spaceRepository
        .findById(spaceId)
        .orElseThrow(() -> new NotFoundException("Space with id " + spaceId + " not found"));
  }

  public boolean existRoleByUserIdAndSpaceId(Long userId, Long spaceId) {
    return userSpaceRoleRepository.findByUserIdAndSpaceId(userId, spaceId).isPresent();
  }

  public UserSpaceRoleDTO deleteUserSpaceRole(
      Long ownerId, UserSpaceRoleRequestDTO userSpaceRoleRequestDTO) {
    if (!existRoleByUserIdAndSpaceId(ownerId, userSpaceRoleRequestDTO.getSpaceId())) {
      throw new ConflictException(
          "El usuario que pide la petición de borrar otro usuario no es admin en este space");
    }
    UserSpaceRoleEntity userSpaceRole =
        userSpaceRoleRepository
            .findByUserIdAndSpaceId(
                userSpaceRoleRequestDTO.getUserId(), userSpaceRoleRequestDTO.getSpaceId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "El user "
                            + userSpaceRoleRequestDTO.getUserId()
                            + " no tiene permisos sobre el space "
                            + userSpaceRoleRequestDTO.getSpaceId()));
    userSpaceRoleRepository.deleteById(userSpaceRole.getId());
    return modelMapper.map(userSpaceRole, UserSpaceRoleDTO.class);
  }
}
