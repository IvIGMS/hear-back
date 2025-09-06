package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<SpaceEntity, Long> {
  Optional<Object> findByName(String name);
}
