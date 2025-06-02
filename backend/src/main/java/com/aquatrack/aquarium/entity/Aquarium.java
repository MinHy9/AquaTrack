package com.aquatrack.aquarium.entity;

import com.aquatrack.feeding.entity.FeedingSchedule;
import com.aquatrack.user.entity.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // @JsonIgnore 쓰지 말 것!
    private List<FeedingSchedule> feedingSchedules;
    private String name; // 어항 이름 (예: "내 첫 번째 어항")

    @CreationTimestamp
    private LocalDateTime registeredDate;

    private String fishName;

    private Float customMinTemperature;
    private Float customMaxTemperature;

    private Float customMinPH;
    private Float customMaxPH;

    private Float customMaxTurbidity;

}
