package com.example.budgetwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BudgetwiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetwiseApplication.class, args);
    }

}
