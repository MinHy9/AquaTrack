package com.example.aquarium.web.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aquarium_id")
    private Aquarium aquarium;

    private LocalTime time; // "08:00" 형태

    private String daysOfWeek; // 예: "MON,WED,FRI" 또는 "EVERYDAY"

    private double amountInGrams; // 급식량

    private boolean enabled = true;

    private String tag; // "아침", "저녁", "특식" 등

    private LocalDateTime lastFedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

