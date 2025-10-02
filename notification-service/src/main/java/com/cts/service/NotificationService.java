package com.cts.service;

import com.cts.dtos.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto sendNotification(NotificationDto notificationDto);
    NotificationDto getNotificationById(Long id);
    List<NotificationDto> getAllNotifications();
    NotificationDto updateNotification(Long id, NotificationDto notificationDto); // Added PUT operation
    String deleteNotification(Long id);
}
