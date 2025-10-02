package com.cts.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDto {
    private Long id;

    @NotBlank(message = "Event type cannot be blank")
    private String eventType;

    @NotBlank(message = "Recipient cannot be blank")
    @Email(message = "Recipient must be a valid email address")
    private String recipient;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private LocalDateTime sentAt;
}
