package com.app.hear.notifications.dao.models;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTypeEntity extends AuditableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String description;

  @Column(nullable = false, length = 50)
  private String code;

  @OneToMany(mappedBy = "notificationType", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<NotificationEntity> notifications;
}
