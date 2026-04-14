package com.example.budgetwise.service;

import com.example.budgetwise.entity.Account;
import com.example.budgetwise.entity.Transaction;
import com.example.budgetwise.entity.User;
import com.example.budgetwise.repo.AccountRepo;
import com.example.budgetwise.repo.TransactionRepo;
import com.example.budgetwise.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;

    // Injecting custom thread pool to implement Bulkhead Pattern for resource isolation
    @Autowired
    @Qualifier("budgetExecutor")
    private  Executor budgetExecutor;

    /**
     * Aggregates financial data for a user across all accounts in parallel.
     * Uses CompletableFuture to optimize response time by processing each account on a separate thread.
     */
    public Map<String, Object> getUserFinancialSummary(Long userId) {
        List<Account> accounts = accountRepo.findByUserId(userId);

        // Map each account into an asynchronous task
        List<CompletableFuture<Map<String, Object>>> futures = accounts.stream()
                .map(account -> CompletableFuture.supplyAsync(()->{
                    // This block runs concurrently for each account
                    List<Transaction> transactions =
                            transactionRepo.findByAccountId(account.getId());

                    // Aggregate total income using Stream reduce
                    BigDecimal totalIncome = transactions.stream()
                            .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    // Aggregate total expense
                    BigDecimal totalExpense = transactions.stream()
                            .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    log.info("Processing account {} on thread: {}",
                            account.getId(), Thread.currentThread().getName());

                    Map<String, Object> result = new HashMap<>();
                    result.put("accountId", account.getId());
                    result.put("accountName", account.getAccountName());
                    result.put("balance", account.getBalance());
                    result.put("totalIncome", totalIncome);
                    result.put("totalExpense", totalExpense);
                    return result;
                    },budgetExecutor)) // Using our dedicated thread pool
                .collect(Collectors.toList());

        // Barrier: Wait for all parallel tasks to complete and join the results
        List<Map<String, Object>> accountSummaries = futures.stream()
                .map(CompletableFuture::join) // Non-blocking wait for each future result
                .collect(Collectors.toList());

        // Calculate total Net Worth across all accounts
        BigDecimal netWorth = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("totalAccounts", accounts.size());
        summary.put("netWorth", netWorth);
        summary.put("accounts", accountSummaries);
        return summary;
    }

    /**
     * Standard Persistence Logic: Creates a new account for a user.
     * Uses @Transactional to ensure the account is only saved if the user is valid.
     */

    @Transactional(rollbackFor = Exception.class)
    public Account createAccount(Long userId, String accountName,
                                 Account.AccountType type, BigDecimal initialBalance){
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found " + userId));

        Account account = new Account();
        account.setUser(user);
        account.setAccountName(accountName);
        account.setAccountType(type);
        account.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);

        return accountRepo.save(account);
    }

    public List<Account> getUserAccounts(Long userId) {
        return accountRepo.findByUserId(userId);
    }










}
