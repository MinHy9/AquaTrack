package com.aquatrack.feeding.repository;

import com.aquatrack.feeding.entity.AutoFeedingState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoFeedingStateRepository extends JpaRepository<AutoFeedingState, Long> {

}
