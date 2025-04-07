package com.aquatrack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @ManyToOne
    @JoinColumn(name = "log_id", nullable = false)
    private WaterQualityLog log;

    @Enumerated(EnumType.STRING)
    private AlertType alertType; // TEMPERATURE, PH, TURBIDITY 등

    @Column(nullable = false)
    private String message;

    private boolean resolved = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
