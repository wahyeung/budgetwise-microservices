package com.budgetwise.accountservice.client;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserDTO getUserById(Long id) {
        UserDTO fallback = new UserDTO();
        fallback.setId(id);
        fallback.setUsername("unknown");
        fallback.setEmail("unavailable");
        return fallback;
    }
}
