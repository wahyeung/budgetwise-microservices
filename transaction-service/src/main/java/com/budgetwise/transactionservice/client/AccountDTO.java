package com.budgetwise.transactionservice.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AccountDTO {
    private Long id;
    private Long userId;
    private String accountName;
    private String accountType;
    private BigDecimal balance;
    private String createdAt;
}