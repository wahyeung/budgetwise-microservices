package com.example.budgetwise.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance =  BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AccountType{
        CHECKING,
        SAVINGS,
        CREDIT,
        INVESTMENT
    }


}
