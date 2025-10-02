package com.cts.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDto {
    private String serviceName;
    private String operation;
    private Long recordId;
    private String details;
    private LocalDateTime timestamp;
}
