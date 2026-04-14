package com.budgetwise.transactionservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;




//Call account-service via OpenFeign
@FeignClient(name = "account-service", fallback = AccountClientFallback.class)
public interface AccountClient {

    @GetMapping("/api/accounts/user/{userId}")
    AccountDTO[] getAccountsByUserId(@PathVariable Long userId);

}
