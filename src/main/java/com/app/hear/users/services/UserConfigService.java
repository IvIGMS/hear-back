package com.app.hear.users.services;

import com.app.hear.users.dao.models.entities.UserConfigEntity;
import com.app.hear.users.dao.repositories.UserConfigRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConfigService {
  private final UserConfigRepository userConfigRepository;
  private final ModelMapper modelMapper;

  public UserConfigEntity createUserConfig(UserConfigEntity userConfigEntity) {
    return userConfigRepository.save(userConfigEntity);
  }
}
