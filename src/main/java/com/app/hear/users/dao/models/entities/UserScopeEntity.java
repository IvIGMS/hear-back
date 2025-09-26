package com.app.hear.users.dao.models.entities;

import com.app.hear.users.dao.models.enums.Tier;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "user_scope")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserScopeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "scope")
  private List<UserConfigEntity> userConfigs;

  @Column(nullable = false)
  private int numMaxVoiceNotesByScope;

  @Column(nullable = false)
  private int numMaxScopesAdmin;

  @Column(nullable = false)
  private int numMaxScopesMember;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Tier tier;
}
