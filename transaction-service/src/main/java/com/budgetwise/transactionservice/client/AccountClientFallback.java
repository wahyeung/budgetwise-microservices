package com.budgetwise.transactionservice.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AccountClientFallback implements AccountClient {
    @Override
    public AccountDTO[] getAccountsByUserId(Long userId) {
        System.out.println("FALLBACK TRIGGERED for userId: " + userId);
        return  new AccountDTO[0];
    }
}
