package com.app.hear.tags.dao.repositories;

import com.app.hear.tags.dao.models.entities.TagEntity;

import java.util.Objects;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
  Optional<TagEntity> findByName(String name);

  @Query(value = """
        SELECT CASE 
                 WHEN EXISTS (
                     SELECT 1 
                     FROM space_tags st
                     WHERE st.tag_id = ?1
                       AND st.space_id = ?2
                 ) 
                 THEN CAST(1 AS BIT) 
                 ELSE CAST(0 AS BIT) 
               END
        """, nativeQuery = true)
  boolean existsTagInSpace(Long tagId, Long spaceId);
}
