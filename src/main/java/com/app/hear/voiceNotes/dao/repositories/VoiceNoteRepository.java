package com.app.hear.voiceNotes.dao.repositories;

import com.app.hear.voiceNotes.dao.models.entities.VoiceNoteEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoiceNoteRepository extends JpaRepository<VoiceNoteEntity, Long> {
  @Query(
      """
            SELECT
                vne
            FROM VoiceNoteEntity vne
                JOIN vne.space s
            WHERE
                (:spaceName IS NULL OR LOWER(s.name) LIKE CONCAT('%', LOWER(CAST(:spaceName AS string)), '%'))
                AND (:voiceNoteName IS NULL OR LOWER(vne.nombre) LIKE CONCAT('%', LOWER(CAST(:voiceNoteName AS string)), '%'))
            """)
  Page<VoiceNoteEntity> getVoiceNotes(
      @Param("spaceName") String spaceName,
      @Param("voiceNoteName") String voiceNoteName,
      Pageable pageable);

  @Query(
      """
                SELECT
                    vne
                FROM VoiceNoteEntity vne
                    JOIN vne.space s
                WHERE
                    s.id = :spaceId
                    AND (:voiceNoteName IS NULL OR LOWER(vne.nombre) LIKE CONCAT('%', LOWER(CAST(:voiceNoteName AS string)), '%'))
                """)
  List<VoiceNoteEntity> getVoiceNotesBySpaceId(
      @Param("spaceId") Long spaceId, @Param("voiceNoteName") String voiceNoteName);
}
