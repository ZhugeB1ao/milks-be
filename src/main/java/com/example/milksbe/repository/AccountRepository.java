package com.example.milksbe.repository;
import com.example.milksbe.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}