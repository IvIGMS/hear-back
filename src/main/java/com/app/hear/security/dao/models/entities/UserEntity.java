package com.app.hear.security.dao.models.entities;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import com.app.hear.security.dao.models.enums.RoleEnum;
import com.app.hear.spaces.dao.models.entities.UserSpaceRole;
import com.app.hear.voiceNotes.dao.models.entities.VoiceNote;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String firstname;

  @Column(nullable = false)
  private String lastname;

  @OneToMany(mappedBy = "user")
  private List<UserSpaceRole> spaceRoles;

  @OneToMany(mappedBy = "uploadedBy")
  private List<VoiceNote> voiceNotes;

  @Column(nullable = false)
  private Boolean isActive = true;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleEnum role;
}