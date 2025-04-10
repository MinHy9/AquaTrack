package com.example.aquarium.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aquarium.web.entity.Alert;


public interface AlertRepository extends JpaRepository<Alert, Integer>{

}
