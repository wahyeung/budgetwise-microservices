package com.example.budgetwise.controller;

import com.example.budgetwise.entity.Transaction;
import com.example.budgetwise.repo.TransactionRepo;
import com.example.budgetwise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    //return @Transactional transfer
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {
        transactionService.transfer(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok("Transfer successful");
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam Transaction.TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(transactionService.addTransaction(accountId, amount, type, description, category));
    }

    //return stream categorized statics
    @GetMapping("/account/{accountId}/spending")
    public ResponseEntity<Map<String, BigDecimal>> getSpending(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getSpendingByCategory(accountId));
    }

    //Return Stream MonthlySummary
    @GetMapping("/account/{accountId}/monthly")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlySummary(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getMonthlySummary(accountId));
    }

}
