package com.app.hear.notifications.dao.repositories;

import com.app.hear.notifications.dao.models.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {}
