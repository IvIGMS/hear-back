package com.app.hear.notifications.dao.repositories;

import com.app.hear.notifications.dao.models.NotificationTypeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationTypeEntity, Long> {

  Optional<NotificationTypeEntity> findByCode(String code);
}
