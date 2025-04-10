package com.example.aquarium.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aquarium.web.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	List<User> findAllBy();
}
