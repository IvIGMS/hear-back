package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.UserSpaceRoleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSpaceRoleRepository extends JpaRepository<UserSpaceRoleEntity, Long> {
  Optional<UserSpaceRoleEntity> findByUserIdAndSpaceId(Long userId, Long spaceId);

  List<UserSpaceRoleEntity> findBySpaceId(Long spaceId);
}
