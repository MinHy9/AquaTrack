package com.aquatrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private Long aquariumId; // 자동급식 상태는 어항별로 관리됨

    private boolean autoFeedingEnabled;
}
