package com.app.hear.spaces.services;

import com.app.hear.spaces.dao.repositories.UserSpaceRoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSpaceRoleService {
    private final UserSpaceRoleRepository repository;
    private final ModelMapper modelMapper;
}
