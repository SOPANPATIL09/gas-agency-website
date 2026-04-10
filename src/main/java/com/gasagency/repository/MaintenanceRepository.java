package com.gasagency.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gasagency.model.Maintenance;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {
}