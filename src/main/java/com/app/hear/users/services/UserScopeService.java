package com.app.hear.users.services;

import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.users.dao.models.entities.UserScopeEntity;
import com.app.hear.users.dao.models.enums.Tier;
import com.app.hear.users.dao.repositories.UserScopeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserScopeService {
  private final UserScopeRepository userScopeRepository;
  private final ModelMapper modelMapper;

  public UserScopeEntity findByTier(Tier tier) {
    return userScopeRepository
        .findByTier(tier)
        .orElseThrow(() -> new NotFoundException("User scope not found by this tier"));
  }
}
