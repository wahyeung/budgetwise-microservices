package com.budgetwise.transactionservice.service;

import com.budgetwise.transactionservice.client.AccountClient;
import com.budgetwise.transactionservice.client.AccountDTO;
import com.budgetwise.transactionservice.entity.Transaction;
import com.budgetwise.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
//    private final org.springframework.web.client.RestTemplate restTemplate =
//            new org.springframework.web.client.RestTemplate();

    @Transactional
    public Transaction addTransaction(Long accountId, BigDecimal amount,
                                      Transaction.TransactionType type,
                                      String category, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }

    //Stream — Expense Statistics by Category
    public Map<String, BigDecimal> getSpendingByCategory(Long accountId) {
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory() : "UNCATEGORIZED",
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> t.getAmount().abs(), BigDecimal::add)
                ));
    }

    //Stream — Monthly Summary Aggregation
    public Map<String, BigDecimal> getMonthlySummary(Long accountId) {
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getYear() + "-"
                                + String.format("%02d", t.getTransactionDate().getMonthValue()),
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount, BigDecimal::add)
                ));
    }

    //OpenFeign — Retrieve Transactions for All User Accounts
//    public Map<String, Object> getUserTransactionSummary(Long userId) {
//        List<AccountDTO> accounts = accountClient.getAccountsByUserId(userId);
//        log.info("Accounts from account-service: {}", accounts);
//        accounts.forEach(a -> log.info("Account id: {}, name: {}", a.getId(), a.getAccountName()));
//        log.info("Found {} accounts for user {}", accounts.size(), userId);
//
//        List<Transaction> allTransactions = accounts.stream()
//                .flatMap(account ->
//                        transactionRepository.findByAccountId(account.getId()).stream())
//                .collect(Collectors.toList());
//
//        BigDecimal totalIncome = allTransactions.stream()
//                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
//                .map(Transaction::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal totalExpense = allTransactions.stream()
//                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
//                .map(Transaction::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return Map.of(
//                "userId", userId,
//                "totalTransactions", allTransactions.size(),
//                "totalIncome", totalIncome,
//                "totalExpense", totalExpense
//        );
//    }
    public Map<String, Object> getUserTransactionSummary(Long userId) {

        AccountDTO[] accounts = accountClient.getAccountsByUserId(userId);

        log.info("Accounts from account-service for user {}: {}",
                userId, java.util.Arrays.toString(accounts));

        if (accounts == null || accounts.length == 0) {
            return Map.of("userId", userId, "totalTransactions", 0,
                    "totalIncome", java.math.BigDecimal.ZERO,
                    "totalExpense", java.math.BigDecimal.ZERO);
        }

        List<Transaction> allTransactions = java.util.Arrays.stream(accounts)
                .flatMap(account ->
                        transactionRepository.findByAccountId(account.getId()).stream())
                .collect(Collectors.toList());

        BigDecimal totalIncome = allTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = allTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("totalTransactions", allTransactions.size());
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        return summary;
    }



}
