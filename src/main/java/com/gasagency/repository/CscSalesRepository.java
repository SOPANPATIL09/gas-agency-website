package com.gasagency.repository;

import com.gasagency.model.CscSales;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CscSalesRepository extends JpaRepository<CscSales, Integer> {
    List<CscSales> findByDateStartingWith(String prefix);
}