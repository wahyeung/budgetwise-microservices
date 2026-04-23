package com.budgetwise.accountservice.controller;

import com.budgetwise.accountservice.entity.Budget;
import com.budgetwise.accountservice.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<Budget> createBudget(
            @RequestParam Long userId,
            @RequestParam String category,
            @RequestParam BigDecimal limitAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                budgetService.createBudget(userId, category, limitAmount, startDate, endDate));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgets(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUser(userId));
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetSummary(userId));
    }

    @PutMapping("/user/{userId}/spent")
    public ResponseEntity<Budget> updateSpent(
            @PathVariable Long userId,
            @RequestParam String category,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(budgetService.updateSpentAmount(userId, category, amount));
    }
}