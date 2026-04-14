package com.example.budgetwise.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exchange-rate-service",
//        url = "https://api.exchangerate-api.com",
        url = "http://localhost:9999",
        fallback = ExchangeRateClientFallback.class)
public interface ExchangeRateClient {

//    @GetMapping("/v4/latest/{currency}")
    @GetMapping("/rates/{currency}")
    String getExchangeRate(@PathVariable String currency);
}
