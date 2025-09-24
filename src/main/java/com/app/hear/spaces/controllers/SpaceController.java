package com.app.hear.spaces.controllers;

import static com.app.hear.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

import com.app.hear.api.SpacesApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.common.exceptions.utils.UnauthorizedException;
import com.app.hear.model.SpaceCreateDTO;
import com.app.hear.model.SpaceDTO;
import com.app.hear.model.UserSpaceRoleDTO;
import com.app.hear.model.UserSpaceRoleRequestDTO;
import com.app.hear.spaces.services.SpaceService;
import com.app.hear.spaces.services.UserSpaceRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SpaceController extends ControllerUtils implements SpacesApi {
  private final SpaceService spaceService;
  private final UserSpaceRoleService userSpaceRoleService;

  @Override
  public ResponseEntity<SpaceDTO> createSpace(SpaceCreateDTO spaceCreateDTO) {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long ownerId = getAllClaims().get("user_id", Long.class);
    SpaceDTO spaceDTO = spaceService.createSpace(spaceCreateDTO, ownerId);
    return ResponseEntity.created(createLocation(spaceDTO.getId())).body(spaceDTO);
  }

  @Override
  public ResponseEntity<SpaceDTO> getSpaceById(Long spaceId) {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long ownerId = getAllClaims().get("user_id", Long.class);
    return ResponseEntity.ok(spaceService.getSpaceById(ownerId, spaceId));
  }

  @Override
  public ResponseEntity<UserSpaceRoleDTO> createUserSpaceRole(
      UserSpaceRoleRequestDTO userSpaceRoleRequestDTO) {
    UserSpaceRoleDTO userSpaceRoleDTO =
        userSpaceRoleService.createUserSpaceRole(userSpaceRoleRequestDTO);
    return ResponseEntity.created(createLocation(userSpaceRoleDTO.getId())).body(userSpaceRoleDTO);
  }

  @Override
  public ResponseEntity<com.app.hear.model.SpaceListDTO> getSpacesByUser() {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long userId = getAllClaims().get("user_id", Long.class);

    return ResponseEntity.ok(spaceService.getSpacesByUser(userId));
  }
}
