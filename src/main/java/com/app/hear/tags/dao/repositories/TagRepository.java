package com.app.hear.tags.dao.repositories;

import com.app.hear.tags.dao.models.entities.TagEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
  Optional<TagEntity> findByName(String name);
}
