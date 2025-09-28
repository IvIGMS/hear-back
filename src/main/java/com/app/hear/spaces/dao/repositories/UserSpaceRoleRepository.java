package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.UserSpaceRoleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSpaceRoleRepository extends JpaRepository<UserSpaceRoleEntity, Long> {
  Optional<UserSpaceRoleEntity> findByUserIdAndSpaceId(Long userId, Long spaceId);

  List<UserSpaceRoleEntity> findBySpaceId(Long spaceId);

  @Query(
      value =
          """
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM user_space_roles usr
            WHERE usr.user_id = :userId
              AND usr.space_id = :spaceId
              AND usr.role = 'ADMIN'
            """,
      nativeQuery = true)
  boolean existsAdminRole(@Param("userId") Long userId, @Param("spaceId") Long spaceId);
}
