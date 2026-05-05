package com.budgetwise.accountservice.service;

import com.budgetwise.accountservice.client.UserClient;
import com.budgetwise.accountservice.entity.Account;
import com.budgetwise.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserClient userClient;

    @CacheEvict(value = "acccountSummary", key = "#userId")
    public Account createAccount(Long userId, String accountName,
                                 Account.AccountType type, BigDecimal initialBalance) {
        //Call user-service via OpenFeign to validate user existence
        UserClient.UserDTO user = userClient.getUserById(userId);
        log.info("Creating account for user: {}", user.getUsername());

        Account account = new Account();
        account.setUserId(userId);
        account.setAccountName(accountName);
        account.setAccountType(type);
        account.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    //CompletableFuture + Stream
    @Cacheable(value = "accountSummary", key = "#userId")
    public Map<String, Object> getAccountSummary(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        UserClient.UserDTO user = userClient.getUserById(userId);

        BigDecimal netWorth = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("username", user.getUsername());
        summary.put("totalAccounts", accounts.size());
        summary.put("netWorth", netWorth);
        summary.put("accounts", accounts);
        return summary;
    }
}
