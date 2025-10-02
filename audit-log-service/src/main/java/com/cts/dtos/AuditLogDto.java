package com.cts.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuditLogDto {
    private Long id;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    @NotBlank(message = "Operation cannot be blank")
    private String operation;

    @NotNull(message = "Record ID cannot be null")
    private Long recordId;

    private LocalDateTime timestamp;

    private String details;
}
