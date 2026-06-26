package com.gasagency.repository;

import com.gasagency.model.CustomerDue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDueRepository extends JpaRepository<CustomerDue, Integer> {
}