package com.app.hear.users.services;

import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.UserDTO;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.security.dao.models.enums.RoleEnum;
import com.app.hear.users.dao.dto.UserDownDto;
import com.app.hear.users.dao.repositories.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  public UserEntity getUserEntityById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found: " + userId));
  }

  public UserDTO getUserDTOById(Long userId) {
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    return modelMapper.map(user, UserDTO.class);
  }

  public List<UserDTO> getUsersByOwner(Long ownerId) {
    List<UserEntity> users = userRepository.getUsersByOwnerId(ownerId);
    return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
  }

  public List<UserEntity> getAdminUsers() {
    return userRepository.findByRole(RoleEnum.ADMIN);
  }

  public List<UserDownDto> usersDownToday() {
    LocalDate now = LocalDate.now();
    return userRepository.usersDownToday(now);
  }
}
