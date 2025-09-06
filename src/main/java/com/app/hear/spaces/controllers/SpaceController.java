package com.app.hear.spaces.controllers;


import com.app.hear.api.SpacesApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.model.SpaceCreateDTO;
import com.app.hear.model.SpaceDTO;
import com.app.hear.spaces.services.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SpaceController extends ControllerUtils implements SpacesApi {
    private final SpaceService spaceService;

    @Override
    public ResponseEntity<SpaceDTO> createSpace(SpaceCreateDTO spaceCreateDTO) {
        return ResponseEntity.status(201).body(spaceService.createSpace(spaceCreateDTO));
    }

    @Override
    public ResponseEntity<SpaceDTO> getSpaceById(Long spaceId) {
        return ResponseEntity.ok(spaceService.getSpaceById(spaceId));
    }
}
