package com.example.aquarium.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aquarium.web.entity.Notification;

public interface NotificationRespository extends JpaRepository<Notification,Integer>{

}
