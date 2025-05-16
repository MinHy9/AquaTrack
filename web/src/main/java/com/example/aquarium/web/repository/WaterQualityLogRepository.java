package com.example.aquarium.web.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.entity.WaterQualityLog;

@Repository
public interface WaterQualityLogRepository extends JpaRepository<WaterQualityLog,Long> {
	List<WaterQualityLog> findByAquarium(Aquarium aquarium);
	List<WaterQualityLog> findByAquariumAndRecordedAtAfter(Aquarium aquarium, LocalDateTime cutoff);
}
