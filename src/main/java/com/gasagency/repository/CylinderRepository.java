package com.gasagency.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gasagency.model.Cylinder;

public interface CylinderRepository extends JpaRepository<Cylinder, Integer> {
	
	List<Cylinder> findByType(String type);

	Cylinder findTopByTypeOrderByDateDesc(String type);
	  Cylinder findTopByTypeOrderByIdDesc(String type );
}