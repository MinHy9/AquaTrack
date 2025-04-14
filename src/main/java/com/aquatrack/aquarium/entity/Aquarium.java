package com.aquatrack.aquarium.entity;

import com.aquatrack.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aquarium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aquariumId;

    @ManyToOne //여러어항이 하나의 유저에 연결
    @JoinColumn(name = "user_id")
    private User user;

    private String name; // 어항 이름 (예: "내 첫 번째 어항")

    @CreationTimestamp
    private LocalDateTime registeredDate;
}
