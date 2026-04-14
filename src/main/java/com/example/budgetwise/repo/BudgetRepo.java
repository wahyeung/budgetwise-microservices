package com.example.budgetwise.repo;

import com.example.budgetwise.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepo extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
}
