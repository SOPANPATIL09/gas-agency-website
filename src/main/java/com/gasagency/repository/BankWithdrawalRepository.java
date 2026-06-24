package com.gasagency.repository;

import com.gasagency.model.BankWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankWithdrawalRepository extends JpaRepository<BankWithdrawal, Integer> {
    List<BankWithdrawal> findByDate(String date);
    List<BankWithdrawal> findByDateStartingWith(String prefix);
}