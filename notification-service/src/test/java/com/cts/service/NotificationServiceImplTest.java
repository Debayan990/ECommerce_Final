package com.cts.service;

import com.cts.dtos.NotificationDto;
import com.cts.entities.Notification;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringBootTest
class NotificationServiceImplTest {

    // Creates a mock of the repository to isolate the service from the database.
    @Mock
    private NotificationRepository notificationRepository;

    // Creates a mock for the ModelMapper utility.
    @Mock
    private ModelMapper modelMapper;

    // Creates a real service instance and injects the defined mocks.
    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationDto notificationDto;

    // This method runs before each test to provide fresh, consistent data.
    @BeforeEach
    void init() {
        notification = new Notification(1L, "LowStock", "manager@example.com", "Stock for item 1005 is low.", LocalDateTime.now());
        notificationDto = new NotificationDto(1L, "LowStock", "manager@example.com", "Stock for item 1005 is low.", notification.getSentAt());
    }

    @Test
    void sendNotification() {
        // Define mock behavior for creating a notification.
        when(modelMapper.map(any(NotificationDto.class), eq(Notification.class))).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(modelMapper.map(any(Notification.class), eq(NotificationDto.class))).thenReturn(notificationDto);

        // Call the service method.
        NotificationDto result = notificationService.sendNotification(notificationDto);

        // Verify the result and mock interaction.
        assertNotNull(result);
        assertEquals("LowStock", result.getEventType());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getNotificationById() {
        // Arrange: Mock the repository to find a notification.
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(modelMapper.map(any(Notification.class), eq(NotificationDto.class))).thenReturn(notificationDto);

        // Call the service method.
        NotificationDto result = notificationService.getNotificationById(1L);

        // Verify the correct notification was returned.
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(notificationRepository).findById(1L);
    }

    @Test
    void getNotificationById_NotFound() {
        // Simulate that the notification is not found.
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        // Verify that the correct exception is thrown.
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(99L));
    }

    @Test
    void getAllNotifications() {
        // Mock the repository to return a list of notifications.
        when(notificationRepository.findAll()).thenReturn(List.of(notification));
        when(modelMapper.map(any(Notification.class), eq(NotificationDto.class))).thenReturn(notificationDto);

        // Call the service method.
        List<NotificationDto> results = notificationService.getAllNotifications();

        // Check the size of the returned list.
        assertEquals(1, results.size());
        assertEquals("manager@example.com", results.get(0).getRecipient());
        verify(notificationRepository).findAll();
    }

    @Test
    void updateNotification() {
        // Mock finding and saving the notification for an update.
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(modelMapper.map(any(Notification.class), eq(NotificationDto.class))).thenReturn(notificationDto);

        // Call the update method.
        NotificationDto result = notificationService.updateNotification(1L, notificationDto);

        // Ensure the result is correct and save was called.
        assertNotNull(result);
        verify(notificationRepository).save(notification);
    }

    @Test
    void updateNotification_NotFound() {
        // Simulate that the notification to update is not found.
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        // Verify that an exception is thrown and save is never called.
        assertThrows(ResourceNotFoundException.class, () -> notificationService.updateNotification(99L, notificationDto));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteNotification() {
        // Mock the repository to find the notification to be deleted.
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        doNothing().when(notificationRepository).delete(notification); // Specify that the delete method does nothing.

        // Call the delete method.
        String result = notificationService.deleteNotification(1L);

        // Check the success message and verify the delete method was called.
        assertEquals("Notification with ID 1 deleted successfully.", result);
        verify(notificationRepository).delete(notification);
    }

    @Test
    void deleteNotification_NotFound() {
        // Simulate that the notification to delete is not found.
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        // Verify that an exception is thrown and delete is never called.
        assertThrows(ResourceNotFoundException.class, () -> notificationService.deleteNotification(99L));
        verify(notificationRepository, never()).delete(any(Notification.class));
    }
}