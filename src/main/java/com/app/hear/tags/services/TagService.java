package com.app.hear.tags.services;

import com.app.hear.common.exceptions.NotFoundException;
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
        TagEntity tagEntity = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag not found with id: " + tagId));
        return modelMapper.map(tagEntity, TagDTO.class);
    }
}
