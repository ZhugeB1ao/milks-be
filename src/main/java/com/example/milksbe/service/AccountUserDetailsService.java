package com.example.milksbe.service;

import com.example.milksbe.model.Account;
import com.example.milksbe.repository.AccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AccountUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmailIgnoreCase(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        return User.withUsername(account.getEmail())
                .password(account.getPassword())
                .authorities(account.getRole())
                .disabled(!Boolean.TRUE.equals(account.getEnabled()))
                .build();
    }
}
