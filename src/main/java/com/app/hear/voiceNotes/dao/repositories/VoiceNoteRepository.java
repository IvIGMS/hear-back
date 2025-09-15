package com.app.hear.voiceNotes.dao.repositories;

import com.app.hear.voiceNotes.dao.models.entities.VoiceNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceNoteRepository extends JpaRepository<VoiceNoteEntity, Long> {}
