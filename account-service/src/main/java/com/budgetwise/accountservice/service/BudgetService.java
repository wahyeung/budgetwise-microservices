package com.budgetwise.accountservice.service;

import com.budgetwise.accountservice.entity.Budget;
import com.budgetwise.accountservice.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    @Transactional
    public Budget createBudget(Long userId, String category,
                               BigDecimal limitAmount,
                               LocalDate startDate, LocalDate endDate) {
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        return budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    // ✅ Stream API — budget status summary
    public Map<String, Object> getBudgetSummary(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);

        Map<String, List<Budget>> byStatus = budgets.stream()
                .collect(Collectors.groupingBy(Budget::getStatus));

        BigDecimal totalLimit = budgets.stream()
                .map(Budget::getLimitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = budgets.stream()
                .map(Budget::getSpentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "userId", userId,
                "totalBudgets", budgets.size(),
                "totalLimit", totalLimit,
                "totalSpent", totalSpent,
                "onTrack", byStatus.getOrDefault("ON_TRACK", List.of()).size(),
                "warning", byStatus.getOrDefault("WARNING", List.of()).size(),
                "exceeded", byStatus.getOrDefault("EXCEEDED", List.of()).size(),
                "budgets", budgets
        );
    }

    // ✅ @Transactional — update spent amount
    @Transactional
    public Budget updateSpentAmount(Long userId, String category, BigDecimal amount) {
        return budgetRepository.findByUserIdAndCategory(userId, category)
                .map(budget -> {
                    budget.setSpentAmount(budget.getSpentAmount().add(amount));
                    if (budget.getStatus().equals("EXCEEDED")) {
                        log.warn("Budget EXCEEDED for user {} category {}", userId, category);
                    }
                    return budgetRepository.save(budget);
                })
                .orElseThrow(() -> new RuntimeException(
                        "No budget found for user " + userId + " category " + category));
    }
}