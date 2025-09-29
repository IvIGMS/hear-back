package com.app.hear.spaces.services;

import com.app.hear.catalog.dao.models.CatalogColorEntity;
import com.app.hear.catalog.services.CatalogColorService;
import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.ColorDTO;
import com.app.hear.model.RoleUserSpace;
import com.app.hear.model.SpaceCreateDTO;
import com.app.hear.model.SpaceDTO;
import com.app.hear.model.UserSpaceRoleRequestDTO;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.models.entities.UserSpaceRoleEntity;
import com.app.hear.spaces.dao.repositories.SpaceRepository;
import com.app.hear.users.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpaceService {
  private final SpaceRepository spaceRepository;
  private final ModelMapper modelMapper;
  private final UserSpaceRoleService userSpaceRoleService;
  private final CatalogColorService catalogColorService;
  private final UserService userService;

  public SpaceDTO createSpace(SpaceCreateDTO spaceCreateDTO, Long ownerId) {
    // Crear el space
    spaceRepository
        .findByName(spaceCreateDTO.getName())
        .ifPresent(
            space -> {
              throw new ConflictException(
                  "Space with name " + spaceCreateDTO.getName() + " already exists");
            });
    testMaximunSpacesUser(ownerId);
    SpaceEntity spaceEntity = modelMapper.map(spaceCreateDTO, SpaceEntity.class);
    // Set color
    spaceEntity.setColor(getRandomColor());
    // Guardar
    SpaceEntity savedSpace = spaceRepository.save(spaceEntity);
    // Asignar rol de admin a quien lo crea
    userSpaceRoleService.createUserSpaceRole(
        UserSpaceRoleRequestDTO.builder()
            .spaceId(savedSpace.getId())
            .userId(ownerId)
            .role(RoleUserSpace.ADMIN)
            .build(),
        ownerId);
    return modelMapper.map(savedSpace, SpaceDTO.class);
  }

  private void testMaximunSpacesUser(Long ownerId) {
    UserEntity user = userService.getUserEntityById(ownerId);
    int maxSpacesAdmin = user.getUserConfig().getScope().getNumMaxScopesAdmin();
    int counterSpacesAdmin = getSpacesByUser(ownerId).getAdmin().size();

    if (maxSpacesAdmin == counterSpacesAdmin || maxSpacesAdmin < counterSpacesAdmin) {
      throw new ConflictException(
          "No puedes crear mas scopes. Has llegado al límite para un usuario gratuito. Actualiza a un plan premium.");
    }
  }

  public SpaceDTO getSpaceById(Long ownerId, Long spaceId) {
    userSpaceRoleService.getUserSpaceRoleByUserIdAndSpaceId(ownerId, spaceId);
    SpaceEntity spaceEntity =
        spaceRepository
            .findById(spaceId)
            .orElseThrow(() -> new NotFoundException("Space with id " + spaceId + " not found"));

    return modelMapper.map(spaceEntity, SpaceDTO.class);
  }

  public SpaceEntity getSpaceEntityById(Long spaceId) {
    return spaceRepository
        .findById(spaceId)
        .orElseThrow(() -> new NotFoundException("Space with id " + spaceId + " not found"));
  }

  public CatalogColorEntity getRandomColor() {
    List<String> allColors =
        catalogColorService.getAllColors().stream().map(ColorDTO::getCode).toList();

    String color = allColors.get(ThreadLocalRandom.current().nextInt(allColors.size()));
    return catalogColorService.getColorEntityByCode(color);
  }

  public com.app.hear.model.SpaceListDTO getSpacesByUser(Long userId) {
    List<SpaceEntity> list = spaceRepository.getSpacesByUser(userId);
    List<SpaceEntity> spaceMember = new ArrayList<>();
    List<SpaceEntity> spaceAdmin = new ArrayList<>();

    list.forEach(
        space -> {
          UserSpaceRoleEntity rolCurrentUser =
              space.getUserRoles().stream()
                  .filter(role -> role.getUser().getId().equals(userId))
                  .findFirst()
                  .orElseThrow(() -> new NotFoundException("User not found in this space"));
          if (rolCurrentUser.getRole().name().equals("ADMIN")) {
            spaceAdmin.add(space);
          } else if (rolCurrentUser.getRole().name().equals("MEMBER")) {
            spaceMember.add(space);
          }
        });
    List<SpaceDTO> spaceAdminDto =
        spaceAdmin.stream().map(space -> modelMapper.map(space, SpaceDTO.class)).toList();
    List<SpaceDTO> spaceMemberDto =
        spaceMember.stream().map(space -> modelMapper.map(space, SpaceDTO.class)).toList();

    return com.app.hear.model.SpaceListDTO.builder()
        .admin(spaceAdminDto)
        .member(spaceMemberDto)
        .build();
  }

  public SpaceEntity getSpaceEntityByVoiceNoteId(Long voiceNoteId) {
    return spaceRepository
        .findByVoiceNotesId(voiceNoteId)
        .orElseThrow(() -> new NotFoundException("Space not found by this voiceNoteId"));
  }

  @Transactional
  public void deleteSpaceById(Long spaceId, Long ownerId) {
    UserSpaceRoleEntity userSpaceRole =
        userSpaceRoleService.getUserSpaceRoleByUserIdAndSpaceId(ownerId, spaceId);
    if (userSpaceRole
        .getRole()
        .name()
        .equals(com.app.hear.spaces.dao.models.enums.RoleUserSpace.ADMIN.name())) {
      SpaceEntity spaceEntity = getSpaceEntityById(spaceId);
      if (!spaceEntity.getVoiceNotes().isEmpty()) {
        throw new ConflictException(
            "No se puede eliminar el space porque tiene audios vinculados. Elimina estos audios primero");
      }

      userSpaceRoleService.cleanBeforeDeleteASpace(spaceId);
      spaceRepository.deleteById(spaceId);
    } else {
      throw new ConflictException(
          "No tienes perimos para borrar este space, eres member, necesitas ser admin");
    }
  }

  public boolean areYouAdminOfTheSpace(Long spaceId, Long userId) {
    AtomicBoolean result = new AtomicBoolean(false);

    SpaceEntity spaceEntity = getSpaceEntityById(spaceId);

    spaceEntity
        .getUserRoles()
        .forEach(
            ur -> {
              if (ur.getUser().getId().equals(userId)
                  && ur.getRole()
                      .equals(com.app.hear.spaces.dao.models.enums.RoleUserSpace.ADMIN)) {
                result.set(true);
              }
            });

    return result.get();
  }
}
