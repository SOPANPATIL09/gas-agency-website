package com.gasagency.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gasagency.model.NewConnection;

public interface NewConnectionRepository extends JpaRepository<NewConnection, Integer> {

}