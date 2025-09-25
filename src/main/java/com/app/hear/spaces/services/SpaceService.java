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
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.models.entities.UserSpaceRole;
import com.app.hear.spaces.dao.repositories.SpaceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceService {
  private final SpaceRepository spaceRepository;
  private final ModelMapper modelMapper;
  private final UserSpaceRoleService userSpaceRoleService;
  private final CatalogColorService catalogColorService;

  public SpaceDTO createSpace(SpaceCreateDTO spaceCreateDTO, Long ownerId) {
    // Crear el space
    spaceRepository
        .findByName(spaceCreateDTO.getName())
        .ifPresent(
            space -> {
              throw new ConflictException(
                  "Space with name " + spaceCreateDTO.getName() + " already exists");
            });
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
            .build());
    return modelMapper.map(savedSpace, SpaceDTO.class);
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
          UserSpaceRole rolCurrentUser =
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
}
