package com.aquatrack.sensor.entity;

import com.aquatrack.aquarium.entity.Aquarium;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterQualityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    //센서 데이터는 어항에 종속
    @ManyToOne
    @JoinColumn(name = "aquarium_id", nullable = false)
    private Aquarium aquarium;

    private Float temperature;
    private Float pH;
    private Float turbidity;

    @Column(nullable = false)
    private String status;

    private LocalDateTime recordedAt;
}
