package com.aquatrack.repository;

import com.aquatrack.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByLog_Aquarium_AquariumId(Long aquariumId);
}
