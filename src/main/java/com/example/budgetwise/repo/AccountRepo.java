package com.example.budgetwise.repo;

import com.example.budgetwise.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepo extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
}
