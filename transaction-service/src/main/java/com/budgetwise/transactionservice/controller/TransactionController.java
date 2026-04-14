package com.budgetwise.transactionservice.controller;

import com.budgetwise.transactionservice.client.AccountClient;
import com.budgetwise.transactionservice.client.AccountDTO;
import com.budgetwise.transactionservice.entity.Transaction;
import com.budgetwise.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService  transactionService;
    private final AccountClient accountClient;

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam Transaction.TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String description){
        return ResponseEntity.ok(
                transactionService.addTransaction(accountId, amount, type, category, description));
    }

    @GetMapping("/account/{accountId}/spending")
    public ResponseEntity<Map<String, BigDecimal>> getSpending(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getSpendingByCategory(accountId));
    }

    @GetMapping("/account/{accountId}/monthly")
    public ResponseEntity<Map<String, BigDecimal>> getMonthly(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getMonthlySummary(accountId));
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getUserSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getUserTransactionSummary(userId));
    }

}
