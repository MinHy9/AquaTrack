package com.example.aquarium.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aquarium.web.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findAllBy();
	List<User> findDistinctByAlertsResolvedFalse();
	User findByUsername(String username);
}
