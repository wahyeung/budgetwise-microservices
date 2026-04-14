package com.example.budgetwise.config;

import com.example.budgetwise.entity.Account;
import com.example.budgetwise.entity.Budget;
import com.example.budgetwise.entity.Transaction;
import com.example.budgetwise.entity.User;
import com.example.budgetwise.repo.AccountRepo;
import com.example.budgetwise.repo.BudgetRepo;
import com.example.budgetwise.repo.TransactionRepo;
import com.example.budgetwise.repo.UserRepo;
import com.example.budgetwise.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepo userRepo;
    private final BudgetRepo budgetRepo;
    private final AccountRepo accountRepo;
    private final BudgetService budgetService;
    private final TransactionRepo transactionRepo;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        User user = new User();
        user.setEmail("luna@budgetwise.com");
        user.setUsername("luna");
        user.setPasswordHash("hashed_password");
        userRepo.save(user);
        log.info("Created user: {}", user.getId());

        Account checking = new Account();
        checking.setUser(user);
        checking.setAccountName("Main Checking");
        checking.setAccountType(Account.AccountType.CHECKING);
        checking.setBalance(new BigDecimal("5000.00"));
        accountRepo.save(checking);

        Account savings = new Account();
        savings.setUser(user);
        savings.setAccountName("Savings Account");
        savings.setAccountType(Account.AccountType.SAVINGS);
        savings.setBalance(new BigDecimal("10000.00"));
        accountRepo.save(savings);

        log.info("Created accounts: {} and {}", checking.getId(), savings.getId());

        createTransaction(checking, new BigDecimal("3500.00"),
                Transaction.TransactionType.INCOME, "SALARY", "Monthly salary");
        createTransaction(checking, new BigDecimal("800.00"),
                Transaction.TransactionType.EXPENSE, "RENT", "Monthly rent");
        createTransaction(checking, new BigDecimal("250.00"),
                Transaction.TransactionType.EXPENSE, "FOOD", "Groceries");
        createTransaction(checking, new BigDecimal("120.00"),
                Transaction.TransactionType.EXPENSE, "FOOD", "Restaurants");
        createTransaction(checking, new BigDecimal("80.00"),
                Transaction.TransactionType.EXPENSE, "TRANSPORT", "Gas");
        createTransaction(checking, new BigDecimal("200.00"),
                Transaction.TransactionType.EXPENSE, "ENTERTAINMENT", "Shopping");
        createTransaction(savings, new BigDecimal("1000.00"),
                Transaction.TransactionType.INCOME, "INTEREST", "Monthly interest");



        Budget foodBudget = new Budget();
        foodBudget.setUser(user);
        foodBudget.setCategory("FOOD");
        foodBudget.setLimitAmount(new BigDecimal("300.00"));
        foodBudget.setStartDate(LocalDate.now().withDayOfMonth(1));
        foodBudget.setEndDate(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        budgetRepo.save(foodBudget);

        Budget rentBudget = new Budget();
        rentBudget.setUser(user);
        rentBudget.setCategory("RENT");
        rentBudget.setLimitAmount(new BigDecimal("1000.00"));
        rentBudget.setStartDate(LocalDate.now().withDayOfMonth(1));
        rentBudget.setEndDate(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        budgetRepo.save(rentBudget);

        log.info("Test data initialized successfully!");
        log.info("=== Demo endpoints ===");
        // Parallel Aggregation using CompletableFuture
        log.info("GET  /api/accounts/user/1/summary   -> CompletableFuture Parallel Aggregation");
        // Category Statistics using Stream API
        log.info("GET  /api/transactions/account/1/spending -> Stream API Category Statistics");
        // Monthly Summary using Stream API
        log.info("GET  /api/transactions/account/1/monthly -> Stream API Monthly Summary");
        // Transactional Fund Transfer
        log.info("POST /api/transactions/transfer?fromAccountId=1&toAccountId=2&amount=500 -> @Transactional Fund Transfer");
    }

    private void createTransaction(Account account, BigDecimal amount,
                                   Transaction.TransactionType type,
                                   String category, String description) {
        Transaction t = new Transaction();
        t.setAccount(account);
        t.setAmount(amount);
        t.setCategory(category);
        t.setDescription(description);
        t.setType(type);
        t.setTransactionDate(LocalDateTime.now());
        transactionRepo.save(t);
    }
}
