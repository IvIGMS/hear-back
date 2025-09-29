package com.app.hear.notifications.services;

import com.app.hear.notifications.dao.models.NotificationTypeEntity;
import com.app.hear.notifications.dao.repositories.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationTypeService {
  private final NotificationTypeRepository notificationTypeRepository;

  public NotificationTypeEntity findByCode(String code) {
    return notificationTypeRepository
        .findByCode(code)
        .orElseThrow(() -> new UsernameNotFoundException("Notification type not found"));
  }
}
