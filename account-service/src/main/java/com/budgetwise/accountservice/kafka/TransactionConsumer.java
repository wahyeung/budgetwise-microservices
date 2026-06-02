package com.budgetwise.accountservice.kafka;

import com.budgetwise.accountservice.event.TransactionCreatedEvent;
import com.budgetwise.accountservice.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionConsumer {

    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "transaction-created", groupId = "account-service-group")
    @Transactional
    @CacheEvict(value = "accountSummary", allEntries = true)
    public void handleTransactionCreated(String message) {
        try {
            TransactionCreatedEvent event = objectMapper.readValue(message, TransactionCreatedEvent.class);
            log.info("Received TransactionCreated event: accountId={}, amount={}, type={}",
                    event.getAccountId(), event.getAmount(), event.getType());

            accountRepository.findById(event.getAccountId()).ifPresent(account -> {
                BigDecimal amount = event.getAmount();
                if ("INCOME".equals(event.getType())) {
                    account.setBalance(account.getBalance().add(amount));
                } else if ("EXPENSE".equals(event.getType())) {
                    account.setBalance(account.getBalance().subtract(amount.abs()));
                }
                accountRepository.save(account);
                log.info("Updated balance for accountId={}, new balance={}",
                        account.getId(), account.getBalance());
            });
        } catch (Exception e) {
            log.error("Failed to process TransactionCreated event: {}", e.getMessage());
        }
    }
}