package com.cts.service;

import com.cts.dtos.NotificationDto;
import com.cts.entities.Notification;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public NotificationDto sendNotification(NotificationDto notificationDto) {
        Notification notification = modelMapper.map(notificationDto, Notification.class);
        notification.setSentAt(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        return modelMapper.map(savedNotification, NotificationDto.class);
    }

    @Override
    public NotificationDto getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        return modelMapper.map(notification, NotificationDto.class);
    }

    @Override
    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDto updateNotification(Long id, NotificationDto notificationDto) {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        // Update fields
        existingNotification.setEventType(notificationDto.getEventType());
        existingNotification.setRecipient(notificationDto.getRecipient());
        existingNotification.setMessage(notificationDto.getMessage());

        Notification updatedNotification = notificationRepository.save(existingNotification);
        return modelMapper.map(updatedNotification, NotificationDto.class);
    }

    @Override
    public String deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notificationRepository.delete(notification);
        return "Notification with ID " + id + " deleted successfully.";
    }
}
