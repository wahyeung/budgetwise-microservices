package com.example.budgetwise.service;

import com.example.budgetwise.entity.Account;
import com.example.budgetwise.entity.Transaction;
import com.example.budgetwise.repo.AccountRepo;
import com.example.budgetwise.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor // Generates a constructor for all final fields (Constructor Injection)
public class TransactionService {
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;

    /**
     * Core Business Logic: Fund Transfer between two accounts.
     * Use @Transactional to ensure Atomicity - both accounts must be updated successfully,
     * or the entire operation will roll back.
     */

    @Transactional(rollbackFor = Exception.class)
    // Step 1: Validate existence of both accounts
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account from = accountRepo.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account to = accountRepo.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Target account not found"));
        // Step 2: Check for sufficient funds using compareTo (standard for BigDecimal)
        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        // Step 3: Update balances (BigDecimal is immutable, must re-set the value)
        from.setBalance(from.getBalance().subtract(amount));
        accountRepo.save(from);

        to.setBalance(to.getBalance().add(amount));
        accountRepo.save(to);

        // Step 4: Record double-entry transaction history
        // Create Debit record for the sender
        Transaction debit = new Transaction();
        debit.setAccount(from);
        debit.setAmount(amount.negate());
        debit.setType(Transaction.TransactionType.TRANSFER);
        debit.setDescription("Transfer to account " + toAccountId);
        debit.setTransactionDate(LocalDateTime.now());
        transactionRepo.save(debit);

        // Create Credit record for the receiver
        Transaction credit = new Transaction();
        credit.setAccount(to);
        credit.setAmount(amount);
        credit.setType(Transaction.TransactionType.TRANSFER);
        credit.setDescription("Transfer from account " + fromAccountId);
        credit.setTransactionDate(LocalDateTime.now());
        transactionRepo.save(credit);

        log.info("Transfer completed: {} -> {}, amount:{}", fromAccountId, toAccountId, amount);
    }

    /**
     * Data Analytics: Calculate total spending grouped by category.
     * Uses Java 8 Stream API for efficient data processing.
     */
    public Map<String, BigDecimal> getSpendingByCategory(Long accountId) {
        return transactionRepo.findByAccountId(accountId)
                .stream()
                .filter(t-> t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t->t.getCategory() != null? t.getCategory() : "UNCATEGORIZED",
                        Collectors.reducing(BigDecimal.ZERO, t->t.getAmount().abs(), BigDecimal::add)
                ));
    }

    /**
     * Reporting: Summarize net balance changes by month.
     * Formats date as YYYY-MM for the map key.
     */
    public Map<String, BigDecimal> getMonthlySummary(Long accountId) {
        return transactionRepo.findByAccountId(accountId)
                .stream()
                .collect(Collectors.groupingBy(
                        t->t.getTransactionDate().getYear() +"-"
                        +String.format("%02d", t.getTransactionDate().getMonthValue()),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    /**
     * Single Transaction Handler: Handles income or expense and updates account balance.
     */
    @Transactional
    public Transaction addTransaction(Long accountId, BigDecimal amount,
                                        Transaction.TransactionType type,
                                        String category, String description) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Reflect the transaction on the account balance
        if(type == Transaction.TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else if (type == Transaction.TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(amount));

        }
        accountRepo.save(account);

        // Persist the transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepo.save(transaction);

    }
}
