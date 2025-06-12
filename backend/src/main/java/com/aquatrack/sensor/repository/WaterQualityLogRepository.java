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
    // 일간 통계 (어항별)
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "DATE(w.recordedAt), " +
            "AVG(w.temperature), " +
            "AVG(w.pH), " +
            "AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.aquariumId = :aquariumId " +
            "GROUP BY DATE(w.recordedAt) " +
            "ORDER BY DATE(w.recordedAt) ASC")
    List<DailySensorStatResponse> getDailyStatsByAquarium(@Param("aquariumId") Long aquariumId);

    // 주간 통계 (어항별)
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "FUNCTION('DATE_FORMAT', w.recordedAt, '%x-W%v'), " +
            "AVG(w.temperature), AVG(w.pH), AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.aquariumId = :aquariumId " +
            "GROUP BY FUNCTION('DATE_FORMAT', w.recordedAt, '%x-W%v') " +
            "ORDER BY FUNCTION('DATE_FORMAT', w.recordedAt, '%x-W%v') ASC")
    List<DailySensorStatResponse> getWeeklyStatsByAquarium(@Param("aquariumId") Long aquariumId);

    // 월간 통계 (어항별)
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m'), " +
            "AVG(w.temperature), AVG(w.pH), AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.aquariumId = :aquariumId " +
            "GROUP BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m') ASC")
    List<DailySensorStatResponse> getMonthlyStatsByAquarium(@Param("aquariumId") Long aquariumId);

    // 시간별 통계 (어항별)
    @Query("SELECT new com.aquatrack.stats.dto.DailySensorStatResponse(" +
            "FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m-%d %H:00'), " +
            "AVG(w.temperature), AVG(w.pH), AVG(w.turbidity)) " +
            "FROM WaterQualityLog w " +
            "WHERE w.aquarium.aquariumId = :aquariumId " +
            "AND w.recordedAt >= :startTime " +
            "GROUP BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m-%d %H:00') " +
            "ORDER BY FUNCTION('DATE_FORMAT', w.recordedAt, '%Y-%m-%d %H:00') ASC")
    List<DailySensorStatResponse> getHourlyStatsByAquarium(
            @Param("aquariumId") Long aquariumId,
            @Param("startTime") LocalDateTime startTime);

    //실시간 센서값
    Optional<WaterQualityLog> findTopByAquarium_User_EmailOrderByRecordedAtDesc(String email);

    //경고 재전송에 필요한 시간값
    List<WaterQualityLog> findByRecordedAtAfter(LocalDateTime time);

}
