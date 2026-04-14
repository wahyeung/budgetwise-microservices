package com.example.budgetwise.client;

import org.springframework.stereotype.Component;

@Component
public class ExchangeRateClientFallback implements ExchangeRateClient {

    @Override
    public String getExchangeRate(String currency) {
        return "{\"fallback\": true, \"message\": \"Exchange rate service unavailable, using cached rates\"}";
    }
}
