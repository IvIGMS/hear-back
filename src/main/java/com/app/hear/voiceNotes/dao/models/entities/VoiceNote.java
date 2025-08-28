package com.app.hear.voiceNotes.dao.models.entities;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.app.hear.security.dao.models.entities.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "voice_notes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceNote extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private Integer duration; // en segundos

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private UserEntity uploadedBy;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private SpaceEntity space;
}

