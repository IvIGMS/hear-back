package com.app.hear.tags.dao.repositories;

import com.app.hear.tags.dao.models.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
}
