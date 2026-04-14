package com.example.budgetwise.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String category;  // e.g. "FOOD", "RENT", "SALARY"
    private String description;

    private LocalDateTime transactionDate = LocalDateTime.now();

    public enum TransactionType{
        INCOME,
        EXPENSE,
        TRANSFER
    }
}
