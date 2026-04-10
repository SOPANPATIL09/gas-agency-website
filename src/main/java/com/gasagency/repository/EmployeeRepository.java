package com.gasagency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.gasagency.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Transactional
	void deleteByName(String name);
    Employee findByName(String name);
}