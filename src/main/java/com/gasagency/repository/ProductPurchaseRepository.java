package com.gasagency.repository;

import com.gasagency.model.ProductPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, Integer> {
    List<ProductPurchase> findByPurchaseDateStartingWith(String prefix);
}