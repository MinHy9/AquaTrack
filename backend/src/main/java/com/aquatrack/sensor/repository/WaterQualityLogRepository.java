package com.aquatrack.sensor.repository;

import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.stats.dto.DailySensorStatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaterQualityLogRepository extends JpaRepository<WaterQualityLog, Long> {
    List<WaterQualityLog> findTop10ByAquariumOrderByRecordedAtDesc(Aquarium aquarium);
    // 일간 통계
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "DATE(w.recordedAt), " +
            "AVG(w.temperature), " +
            "AVG(w.pH), " +
            "AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.user.email = :email " +
            "GROUP BY DATE(w.recordedAt) " +
            "ORDER BY DATE(w.recordedAt) ASC")
    List<DailySensorStatResponse> getDailyStatsByUser(@Param("email") String email);

    // 주간 통계
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "FUNCTION('DATE_FORMAT', w.recordedAt, '%x-W%v'), " +
            "AVG(w.temperature), AVG(w.pH), AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.user.email = :email " +
            "GROUP BY FUNCTION('DATE_FORMAT', w.recordedAt, '%x-W%v') " +
            "ORDER BY FUNCTION('DATE_FORMAT', w.recordedAt, '%x-W%v') ASC")
    List<DailySensorStatResponse> getWeeklyStats(@Param("email") String email);

    // 월간 통계
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m'), " +
            "AVG(w.temperature), AVG(w.pH), AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.user.email = :email " +
            "GROUP BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m') ASC")
    List<DailySensorStatResponse> getMonthlyStats(@Param("email") String email);

    // 시간별 통계 (최근 24시간 - 시간대 변환 없음)
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m-%d %H:00'), " +
            "AVG(w.temperature), AVG(w.pH), AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.user.email = :email " +
            "AND w.recordedAt >= :startTime " +
            "GROUP BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m-%d %H:00') " +
            "ORDER BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m-%d %H:00') ASC")
    List<DailySensorStatResponse> getHourlyStats(@Param("email") String email, @Param("startTime") LocalDateTime startTime);

    //실시간 센서값
    Optional<WaterQualityLog> findTopByAquarium_User_EmailOrderByRecordedAtDesc(String email);

    //경고 재전송에 필요한 시간값
    List<WaterQualityLog> findByRecordedAtAfter(Instant time);

}
