package com.example.aquarium.web.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoFeedingState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aquariumId;

    private boolean autoFeedingEnabled;

    private LocalTime feedingTime;

    private LocalDateTime lastFedAt;

    private Duration feedingInterval;          

    private double amountInGrams;             

    private boolean manualOverride;            

    @Enumerated(EnumType.STRING)
    private FeedingSchedule schedule;          

    public enum FeedingSchedule {
        ONCE_A_DAY,
        TWICE_A_DAY,
        EVERY_12_HOURS,
        CUSTOM
    }
    
    @OneToOne(fetch = FetchType.LAZY)
    private Aquarium aquarium;
}
