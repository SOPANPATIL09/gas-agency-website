package com.gasagency.repository;

import com.gasagency.model.CylinderSales;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CylinderSalesRepository extends JpaRepository<CylinderSales, Integer> {
    List<CylinderSales> findByDate(String date);
    List<CylinderSales> findByDateStartingWith(String prefix);
}