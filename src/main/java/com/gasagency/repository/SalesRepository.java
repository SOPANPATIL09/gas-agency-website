package com.gasagency.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gasagency.model.Sales;

public interface SalesRepository extends JpaRepository<Sales, Integer> {
}