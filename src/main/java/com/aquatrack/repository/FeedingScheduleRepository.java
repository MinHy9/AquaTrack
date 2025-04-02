package com.aquatrack.repository;

import com.aquatrack.entity.Aquarium;
import com.aquatrack.entity.FeedingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedingScheduleRepository extends JpaRepository<FeedingSchedule, Long> {
    List<FeedingSchedule> findByAquariumAndEnabledTrue(Aquarium aquarium);
}
