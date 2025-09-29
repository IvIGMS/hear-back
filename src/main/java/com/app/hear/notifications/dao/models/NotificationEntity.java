package com.app.hear.notifications.dao.models;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import com.app.hear.security.dao.models.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private boolean isSended;

  @Column(nullable = false, length = 255)
  private String message;

  // Relación con el usuario que recibe la notificación
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity receiverUser;

  // Relación con NotificationType
  @ManyToOne
  @JoinColumn(name = "notification_type_id", nullable = false)
  private NotificationTypeEntity notificationType;
}
