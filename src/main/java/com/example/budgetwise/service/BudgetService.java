package com.example.budgetwise.service;

import com.example.budgetwise.entity.Budget;
import com.example.budgetwise.entity.Transaction;
import com.example.budgetwise.entity.User;
import com.example.budgetwise.repo.AccountRepo;
import com.example.budgetwise.repo.BudgetRepo;
import com.example.budgetwise.repo.TransactionRepo;
import com.example.budgetwise.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepo budgetRepo;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;

    /**
     * Creates a new budget limit for a specific category and date range.
     * @return The persisted Budget entity.
     */
    @Transactional
    public Budget createBudget(Long userId, String category,
                             BigDecimal limitAmount,
                             LocalDate startDate, LocalDate endDate) {
        // Ensure user exists before creating budget
        User user = userRepo.findById(userId)
                .orElseThrow(()-> new RuntimeException("user not found"));

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);

        return budgetRepo.save(budget);
    }

    /**
     * Calculates the real-time status of all budgets for a user.
     * Compares budget limits against actual accumulated expenses across all accounts.
     */
    public List<Map<String, Object>> getBudgetStatus(Long userId) {
        // Fetch all defined budgets for the user
        List<Budget> budgets = budgetRepo.findByUserId(userId);

        // Retrieve all account IDs owned by this user to track cross-account spending
        List<Long> accountIds = accountRepo.findByUserId(userId)
                .stream()
                .map(a->a.getId())
                .collect(Collectors.toList());

        // Aggregate actual spending by category using flatMap and groupingBy
        Map<String, BigDecimal> spentByCategory = accountIds.stream()
                .flatMap(accountId ->transactionRepo.findByAccountId(accountId).stream())
                .filter(t->t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                       t->t.getCategory() != null? t.getCategory(): "UNCATEGORIZED",
                       Collectors.reducing(BigDecimal.ZERO,
                               t-> t.getAmount().abs(), BigDecimal::add)
                ));

        // Map each budget to its current consumption status
        return budgets.stream()
                .map(budget -> {
                    BigDecimal spent = spentByCategory.getOrDefault(budget.getCategory(), BigDecimal.ZERO);
                    BigDecimal remaining = budget.getLimitAmount().subtract(spent);
                    boolean isOverBudget = remaining.compareTo(BigDecimal.ZERO) < 0;

                    Map<String, Object> status = new HashMap<>();
                    status.put("category", budget.getCategory());
                    status.put("limit", budget.getLimitAmount());
                    status.put("spent", spent);
                    status.put("remaining", remaining);
                    status.put("isOverBudget", isOverBudget);

                    // Calculation of usage percentage with safety rounding (Scale of 2)
                    status.put("usagePercent",
                            spent.multiply(BigDecimal.valueOf(100))
                                    .divide(budget.getLimitAmount(), 2, java.math.RoundingMode.HALF_UP));
                    return status;
                })
                .collect(Collectors.toList());
    }
}
