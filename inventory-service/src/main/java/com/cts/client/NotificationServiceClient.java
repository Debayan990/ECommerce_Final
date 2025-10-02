package com.cts.client;

import com.cts.dtos.NotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "notification-service", url = "${api.gateway.url}")
public interface NotificationServiceClient {

    @PostMapping("/api/notifications")
    void sendNotification(@RequestBody NotificationDto notificationDto, @RequestHeader("Authorization") String token);
}
