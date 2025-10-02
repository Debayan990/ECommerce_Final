package com.cts.service;

import com.cts.client.NotificationServiceClient;
import com.cts.dtos.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSenderService {

    private final NotificationServiceClient notificationServiceClient;     // Inject the Feign Client

    public void sendLowStockNotification(Long itemId, String token) {
        try {
            NotificationDto notification = new NotificationDto();
            notification.setEventType("LowStock");
            notification.setRecipient("inventory.manager@company.com");
            notification.setMessage("Stock for item " + itemId + " is low.");
            notificationServiceClient.sendNotification(notification, token);
        } catch (Exception e) {
            System.err.println("Error sending low stock notification: " + e.getMessage());
        }
    }
}
