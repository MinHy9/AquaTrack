package com.aquatrack.alert.repository;

import com.aquatrack.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByLog_Aquarium_AquariumId(Long aquariumId);

    @Query("SELECT a FROM Alert a WHERE a.log.aquarium.user.email = :email ORDER BY a.createdAt DESC")
    List<Alert> findAllByUserEmail(@Param("email") String email);


}
