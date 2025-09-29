package com.app.hear.spaces.dao.models.entities;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.spaces.dao.models.enums.InvitationStatus;
import com.app.hear.spaces.dao.models.enums.RoleUserSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "space_invitations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceInvitationEntity extends AuditableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "space_id")
  private SpaceEntity space;

  @ManyToOne
  @JoinColumn(name = "sender_user_id", nullable = false)
  private UserEntity senderUser;

  @ManyToOne
  @JoinColumn(name = "receiver_user_id", nullable = false)
  private UserEntity receiverUser;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  RoleUserSpace roleUserSpace;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  InvitationStatus invitationStatus;
}
