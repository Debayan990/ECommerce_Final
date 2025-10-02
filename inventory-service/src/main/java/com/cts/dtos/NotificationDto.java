package com.cts.dtos;

import lombok.Data;

@Data
public class NotificationDto {
    private String eventType;
    private String recipient;
    private String message;
}
