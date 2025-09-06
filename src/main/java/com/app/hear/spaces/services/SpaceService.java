package com.app.hear.spaces.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.TagCreateDTO;
import com.app.hear.model.TagDTO;
import com.app.hear.spaces.dao.repositories.SpaceRepository;
import com.app.hear.tags.dao.models.entities.TagEntity;
import com.app.hear.tags.dao.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final ModelMapper modelMapper;
}
