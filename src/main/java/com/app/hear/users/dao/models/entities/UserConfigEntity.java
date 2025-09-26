package com.app.hear.users.dao.models.entities;

import com.app.hear.security.dao.models.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConfigEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserEntity user;

  @Column(nullable = false)
  private String dummy;

  @ManyToOne
  @JoinColumn(name = "scope_id", nullable = false)
  private UserScopeEntity scope;
}
