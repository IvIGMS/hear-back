package com.app.hear.spaces.controllers;

import com.app.hear.spaces.services.UserSpaceRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserSpaceRoleController {
    private final UserSpaceRoleService userSpaceRoleService;
}
