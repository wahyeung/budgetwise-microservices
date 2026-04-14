package com.budgetwise.accountservice.controller;

import com.budgetwise.accountservice.entity.Account;
import com.budgetwise.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestParam Long userId,
            @RequestParam String accountName,
            @RequestParam Account.AccountType accountType,
            @RequestParam(required = false, defaultValue = "0") BigDecimal initialBalance) {
        return ResponseEntity.ok(
                accountService.createAccount(userId, accountName, accountType, initialBalance));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountSummary(userId));
    }
}