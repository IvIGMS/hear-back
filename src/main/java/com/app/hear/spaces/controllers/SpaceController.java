package com.app.hear.spaces.controllers;


import com.app.hear.api.TagsApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.model.TagCreateDTO;
import com.app.hear.model.TagDTO;
import com.app.hear.spaces.services.SpaceService;
import com.app.hear.tags.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SpaceController extends ControllerUtils implements TagsApi {
    private final SpaceService spaceService;
}
