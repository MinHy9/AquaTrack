package com.aquatrack.repository;

import com.aquatrack.entity.Aquarium;
import com.aquatrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AquariumRepository extends JpaRepository<Aquarium, Long> {
    List<Aquarium> findByUser(User user);
}
