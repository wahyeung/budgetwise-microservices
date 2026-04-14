package com.example.budgetwise.controller;

import com.example.budgetwise.client.ExchangeRateClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateClient exchangeRateClient;

    @GetMapping("/{currency}")
    @CircuitBreaker(name = "exchange-rate-service", fallbackMethod = "fallback")
    public ResponseEntity<String> getRate(@PathVariable String currency){
        return ResponseEntity.ok(exchangeRateClient.getExchangeRate(currency));
    }

    public ResponseEntity<String> fallback(String currency, Exception e) {
        return ResponseEntity.ok("{\"fallback\": true, \"message\": \"Exchange rate service unavailable\"}");
    }
}
