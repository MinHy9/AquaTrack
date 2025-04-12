package com.aquatrack.repository;

import com.aquatrack.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAlert_Log_Aquarium_AquariumId(Long aquariumId);
}
