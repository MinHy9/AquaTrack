package com.example.aquarium.web.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.aquarium.web.entity.Alert;
import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.entity.Notification;
import com.example.aquarium.web.entity.User;

@Repository
public interface NotificationRespository extends JpaRepository<Notification,Long>{
	List<Notification> findByUser(User user);
	//하루 전송 횟수 카운트
	@Query("SELECT COUNT(n) FROM Notification n " +
		       "WHERE n.user = :user " +
		       "AND n.alert.waterQualityLog.aquarium = :aquarium " +
		       "AND :alertType MEMBER OF n.alert.alertTypes " +
		       "AND n.sentAt >= :startOfDay")
		int countResendsToday(
		    @Param("user") User user,
		    @Param("aquarium") Aquarium aquarium,
		    @Param("alertType") Alert.AlertType alertType,
		    @Param("startOfDay") LocalDateTime startOfDay
		);
	List<Notification> findByAlert(Alert alert);
 
	
	@Query("SELECT n FROM Notification n " +
		       "JOIN FETCH n.alert a " +
		       "JOIN FETCH a.waterQualityLog wql " + // WaterQualityLog를 JOIN
		       "JOIN FETCH wql.aquarium aq " + // Aquarium을 JOIN
		       "WHERE a.user.id = :userId " +
		       "AND aq.id = :aquariumId " + // Aquarium ID 필터 추가
		       "AND n.sentAt > :cutoff")
		List<Notification> findRecentNotificationsWithAlertAndAquarium(
		        @Param("userId") Long userId, 
		        @Param("aquariumId") Long aquariumId, 
		        @Param("cutoff") LocalDateTime cutoff);
	
}
