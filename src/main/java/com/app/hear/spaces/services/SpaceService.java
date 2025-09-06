package com.app.hear.spaces.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.SpaceCreateDTO;
import com.app.hear.model.SpaceDTO;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.repositories.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final ModelMapper modelMapper;

    public SpaceDTO createSpace(SpaceCreateDTO spaceCreateDTO) {
        spaceRepository.findByName(spaceCreateDTO.getName()).ifPresent(space -> {
            throw new ConflictException("Space with name " + spaceCreateDTO.getName() + " already exists");
        });
        SpaceEntity spaceEntity = modelMapper.map(spaceCreateDTO, SpaceEntity.class);
        SpaceEntity savedSpace = spaceRepository.save(spaceEntity);
        return modelMapper.map(savedSpace, SpaceDTO.class);
    }

    public SpaceDTO getSpaceById(Long spaceId) {
        SpaceEntity spaceEntity = spaceRepository.findById(spaceId).orElseThrow(() -> new NotFoundException("Space with id " + spaceId + " not found"));
        return modelMapper.map(spaceEntity, SpaceDTO.class);
    }
}
