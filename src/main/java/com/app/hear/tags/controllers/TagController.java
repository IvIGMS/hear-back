package com.app.hear.tags.controllers;

import static com.app.hear.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

import com.app.hear.api.TagsApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.common.exceptions.utils.UnauthorizedException;
import com.app.hear.model.TagCreateDTO;
import com.app.hear.model.TagDTO;
import com.app.hear.model.TagSpaceRequestDTO;
import com.app.hear.tags.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class TagController extends ControllerUtils implements TagsApi {
  private final TagService tagService;

  @Override
  public ResponseEntity<TagDTO> createTag(TagCreateDTO tagCreateDTO) {
    return ResponseEntity.status(201).body(tagService.createTag(tagCreateDTO));
  }

  @Override
  public ResponseEntity<TagDTO> getTagById(Long tagId) {
    return ResponseEntity.ok(tagService.getTagById(tagId));
  }

  @Override
  public ResponseEntity<Void> addTagToSpace(TagSpaceRequestDTO tagSpaceRequestDTO) {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long ownerId = getAllClaims().get("user_id", Long.class);
    tagService.addTagToSpace(ownerId, tagSpaceRequestDTO);
    return ResponseEntity.noContent().build();
  }
}
