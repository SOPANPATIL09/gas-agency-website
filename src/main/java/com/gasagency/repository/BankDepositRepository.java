package com.gasagency.repository;

import com.gasagency.model.BankDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankDepositRepository extends JpaRepository<BankDeposit, Integer> {
    List<BankDeposit> findByDate(String date);
    List<BankDeposit> findByDateStartingWith(String prefix);
}