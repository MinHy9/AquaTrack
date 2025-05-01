package com.example.aquarium.web.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Alert {

    public enum AlertType {
        TEMPARATURE_WARNING, PH_WARNING, TURBIDITY_WARNING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    private boolean resolved = false;
    

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type")
    private List<AlertType> alertTypes = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private WaterQualityLog waterQualityLog;

    @OneToOne(mappedBy = "alert", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;
}
