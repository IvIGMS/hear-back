package com.app.hear.tags.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.TagCreateDTO;
import com.app.hear.model.TagDTO;
import com.app.hear.tags.dao.models.entities.TagEntity;
import com.app.hear.tags.dao.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  private final ModelMapper modelMapper;

  public TagDTO getTagById(Long tagId) {
    TagEntity tagEntity =
        tagRepository
            .findById(tagId)
            .orElseThrow(() -> new NotFoundException("Tag not found with id: " + tagId));
    return modelMapper.map(tagEntity, TagDTO.class);
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
}
