package com.example.aquarium.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.entity.User;

@Repository
public interface AquariumRepository extends JpaRepository<Aquarium, Long> {
	List<Aquarium> findByUser(User user);
}
