package com.example.budgetwise.controller;

import com.example.budgetwise.entity.Account;
import com.example.budgetwise.service.AccountService;
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
            @RequestParam(required = false, defaultValue = "0")BigDecimal initialBalance) {
                return ResponseEntity.ok(
                        accountService.createAccount(userId, accountName, accountType, initialBalance));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getUserAccounts(@PathVariable Long userId) {
                return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }

    /**
     * REST Endpoint: Aggregates real-time financial data for a specific user.
     * * Performance Highlight: Leverages CompletableFuture to process multiple accounts
     * in parallel, significantly reducing response time compared to sequential processing.
     * * @param userId Path variable identifying the user.
     * @return 200 OK with a complex JSON Map containing Net Worth, account counts,
     * and detailed per-account summaries.
     */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getFinancialSummary(@PathVariable Long userId) {
                return ResponseEntity.ok(accountService.getUserFinancialSummary(userId));
    }


}
