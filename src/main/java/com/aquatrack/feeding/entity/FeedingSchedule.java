package com.aquatrack.feeding.entity;

import com.aquatrack.aquarium.entity.Aquarium;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aquarium_id")
    private Aquarium aquarium;

    private String time; // 예: "08:00", "18:00"

    private boolean enabled = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
