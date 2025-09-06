package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.UserSpaceRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSpaceRoleRepository extends JpaRepository<UserSpaceRole, Long> {
  Optional<UserSpaceRole> findByUserIdAndSpaceId(Long userId, Long spaceId);
}
