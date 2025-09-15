package com.app.hear.tags.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.TagCreateDTO;
import com.app.hear.model.TagDTO;
import com.app.hear.model.TagSpaceRequestDTO;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.models.entities.UserSpaceRole;
import com.app.hear.spaces.dao.models.enums.RoleUserSpace;
import com.app.hear.spaces.services.SpaceService;
import com.app.hear.spaces.services.UserSpaceRoleService;
import com.app.hear.tags.dao.models.entities.TagEntity;
import com.app.hear.tags.dao.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  private final ModelMapper modelMapper;
  private final SpaceService spaceService;
  private final UserSpaceRoleService userSpaceRoleService;

  public TagDTO getTagById(Long tagId) {
    TagEntity tagEntity =
        tagRepository
            .findById(tagId)
            .orElseThrow(() -> new NotFoundException("Tag not found with id: " + tagId));
    return modelMapper.map(tagEntity, TagDTO.class);
  }

  public TagEntity getTagEntityById(Long tagId) {
    return tagRepository
        .findById(tagId)
        .orElseThrow(() -> new NotFoundException("Tag not found with id: " + tagId));
  }

  public TagDTO createTag(TagCreateDTO tagCreateDTO) {
    tagRepository
        .findByName(tagCreateDTO.getName())
        .ifPresent(
            t -> {
              throw new ConflictException(
                  "Tag with name '" + tagCreateDTO.getName() + "' already exists");
            });

    TagEntity tagEntity = new TagEntity();
    tagEntity.setName(tagCreateDTO.getName());

    TagEntity savedTag = tagRepository.save(tagEntity);

    return modelMapper.map(savedTag, TagDTO.class);
  }

  @Transactional
  public void addTagToSpace(Long userId, TagSpaceRequestDTO tagSpaceRequestDTO) {
    Long tagId = tagSpaceRequestDTO.getTagId();
    Long spaceId = tagSpaceRequestDTO.getSpaceId();

    UserSpaceRole userSpaceRole =
        userSpaceRoleService.getUserSpaceRoleByUserIdAndSpaceId(userId, spaceId);
    if (!userSpaceRole.getRole().equals(RoleUserSpace.ADMIN)) {
      throw new ConflictException(
          "El user no tiene permisos para hacer cambios en este space. Necesita ser admin.");
    }
    TagEntity tagEntity = getTagEntityById(tagId);
    SpaceEntity spaceEntity = spaceService.getSpaceEntityById(spaceId);

    if (tagRepository.existsTagInSpace(tagId, spaceId)) {
      throw new ConflictException("Este tag y este space ya están vinculados");
    }
    spaceEntity.getTags().add(tagEntity);
  }

  public void dropTagToSpace() {
    // Tenemos que eliminar de la tabla simplemente
  }
}
