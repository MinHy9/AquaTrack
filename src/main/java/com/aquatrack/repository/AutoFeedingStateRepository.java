package com.aquatrack.repository;

import com.aquatrack.entity.AutoFeedingState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoFeedingStateRepository extends JpaRepository<AutoFeedingState, Long> {

}
