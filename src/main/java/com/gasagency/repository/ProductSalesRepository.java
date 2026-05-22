package com.gasagency.repository;

import com.gasagency.model.ProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductSalesRepository extends JpaRepository<ProductSales, Integer> {
    List<ProductSales> findByDate(String date);
    List<ProductSales> findByDateStartingWith(String prefix);
}