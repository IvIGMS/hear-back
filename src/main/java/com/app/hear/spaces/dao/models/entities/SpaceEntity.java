package com.app.hear.spaces.dao.models.entities;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import com.app.hear.tags.dao.models.entities.TagEntity;
import com.app.hear.voiceNotes.dao.models.entities.VoiceNote;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "spaces")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "space")
    private List<UserSpaceRole> userRoles;

    @OneToMany(mappedBy = "space")
    private List<VoiceNote> voiceNotes;

    @ManyToMany
    @JoinTable(
            name = "space_tags",
            joinColumns = @JoinColumn(name = "space_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagEntity> tags;
}
