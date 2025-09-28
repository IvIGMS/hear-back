package com.app.hear.users.services;

import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.UserDTO;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.security.dao.models.enums.RoleUserEnum;
import com.app.hear.users.dao.dto.UserDownDto;
import com.app.hear.users.dao.repositories.UserRepository;
import com.app.hear.users.dto.projections.UserProjectionDTO;
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
    return userRepository.findByRole(RoleUserEnum.ADMIN);
  }

  public List<UserDownDto> usersDownToday() {
    LocalDate now = LocalDate.now();
    return userRepository.usersDownToday(now);
  }

  public List<UserDTO> getUserCanBeInvitedToThisSpace(Long spaceId) {
    List<UserProjectionDTO> userProjections =
        userRepository.getUserCanBeInvitedToThisSpace(spaceId);
    return userProjections.stream()
        .map(
            user ->
                UserDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .isEmailActive(user.getIsEmailActive())
                    .role(user.getRole())
                    .build())
        .toList();
  }
}
