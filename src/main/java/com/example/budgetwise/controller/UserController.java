package com.example.budgetwise.controller;

import com.example.budgetwise.entity.User;
import com.example.budgetwise.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepo userRepo;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return  ResponseEntity.ok(userRepo.save(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
