package com.aquatrack.repository;

import com.aquatrack.entity.Aquarium;
import com.aquatrack.entity.WaterQualityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterQualityLogRepository extends JpaRepository<WaterQualityLog, Long> {
    List<WaterQualityLog> findTop10ByAquariumOrderByRecordedAtDesc(Aquarium aquarium);
}
