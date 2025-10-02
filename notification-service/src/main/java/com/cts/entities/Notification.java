package com.cts.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false, length = 150)
    private String recipient;

    @Column(nullable = false, columnDefinition = "TEXT")     //Overrides the default column type and sets it to TEXT.
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}
