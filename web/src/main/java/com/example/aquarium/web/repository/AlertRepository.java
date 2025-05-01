package com.example.aquarium.web.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aquarium.web.entity.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long>{
	List<Alert> findByUser_UserIdAndResolvedFalse(Long userId);
}
