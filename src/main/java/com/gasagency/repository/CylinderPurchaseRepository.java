package com.gasagency.repository;

import com.gasagency.model.CylinderPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CylinderPurchaseRepository extends JpaRepository<CylinderPurchase, Integer> {
    List<CylinderPurchase> findByPurchaseDateStartingWith(String prefix);
}