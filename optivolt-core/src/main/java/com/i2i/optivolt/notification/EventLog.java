package com.i2i.optivolt.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_logs")
@Getter
@Setter
@NoArgsConstructor

public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_id", nullable = false)
    private Long homeId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "email_sent")
    private boolean emailSent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
