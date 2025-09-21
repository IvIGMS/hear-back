package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpaceRepository extends JpaRepository<SpaceEntity, Long> {
  Optional<Object> findByName(String name);

  @Query(
      "SELECT s "
          + "FROM SpaceEntity s "
          + "INNER JOIN s.userRoles ur "
          + "WHERE ur.user.id = :userId")
  List<SpaceEntity> getSpacesByUser(@Param("userId") Long userId);
}
