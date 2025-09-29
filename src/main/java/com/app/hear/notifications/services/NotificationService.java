package com.app.hear.notifications.services;

import com.app.hear.model.NotificationDTO;
import com.app.hear.notifications.dao.models.NotificationEntity;
import com.app.hear.notifications.dao.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final NotificationTypeService notificationTypeService;
  private final ModelMapper modelMapper;

  public NotificationEntity createNotification(NotificationDTO notificationDTO) {
    NotificationEntity notificationEntity =
        modelMapper.map(notificationDTO, NotificationEntity.class);
    notificationEntity.setNotificationType(
        notificationTypeService.findByCode(notificationDTO.getNotificationTypeCode()));
    return notificationRepository.save(notificationEntity);
  }
}
