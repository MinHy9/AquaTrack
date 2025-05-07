package com.aquatrack.feeding.repository;

import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.feeding.entity.FeedingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedingScheduleRepository extends JpaRepository<FeedingSchedule, Long> {
    List<FeedingSchedule> findByAquariumAndEnabledTrue(Aquarium aquarium);
}
