package com.example.budgetwise.repo;

import com.example.budgetwise.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountIdAndTransactionDateBetween(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}
