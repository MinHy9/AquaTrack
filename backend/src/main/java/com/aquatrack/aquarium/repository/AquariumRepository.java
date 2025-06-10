package com.aquatrack.aquarium.repository;

import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AquariumRepository extends JpaRepository<Aquarium, Long> {
    List<Aquarium> findByUser(User user);

    Optional<Aquarium> findByUserEmail(String email);

    Optional<Aquarium> findByUser_UserId(Long userId);//id로 유저찾기

    boolean existsByBoardId(String boardId);

    Optional<Aquarium> findByBoardId(String boardId);
}
