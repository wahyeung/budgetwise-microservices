package com.budgetwise.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets", indexes = {
        @Index(name = "idx_budgets_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal limitAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient
    public BigDecimal getRemainingAmount() {
        return limitAmount.subtract(spentAmount);
    }

    @Transient
    public String getStatus() {
        if (spentAmount.compareTo(limitAmount) >= 0) return "EXCEEDED";
        if (spentAmount.compareTo(limitAmount.multiply(new BigDecimal("0.8"))) >= 0) return "WARNING";
        return "ON_TRACK";
    }
}