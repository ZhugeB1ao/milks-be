package com.example.milksbe.service;

import com.example.milksbe.dto.request.LoginRequest;
import com.example.milksbe.dto.request.RegisterRequest;
import com.example.milksbe.dto.response.AuthResponse;
import com.example.milksbe.model.Account;
import com.example.milksbe.model.Customer;
import com.example.milksbe.repository.AccountRepository;
import com.example.milksbe.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {
    private static final String CUSTOMER_ROLE = "ROLE_CUSTOMER";
    private static final String DEFAULT_CUSTOMER_CATEGORY = "Copper";

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if(accountRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        Account account = new Account();
        account.setName(trim(request.name()));
        account.setAddress(trim(request.address()));
        account.setPhone(trim(request.phone()));
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setEnabled(true);
        account.setRole(CUSTOMER_ROLE);

        Account savedAccount = accountRepository.save(account);

        Customer customer = new Customer();
        customer.setAccount(savedAccount);
        customer.setCategory(DEFAULT_CUSTOMER_CATEGORY);
        customer.setShipToAddress(trim(request.shipToAddress()));

        Customer savedCustomer = customerRepository.save(customer);

        return toResponse(savedAccount, savedCustomer);
    }

    @Transactional(readOnly = true )
    public AuthResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(this::invalidCredentials);

        if (!Boolean.TRUE.equals(account.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw invalidCredentials();
        }

        return toResponse(account, findCustomer(account));
    }

    @Transactional(readOnly = true)
    public AuthResponse findMe(String email) {
        Account account = accountRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        return toResponse(account, findCustomer(account));
    }

    private Customer findCustomer(Account account) {
        if (!CUSTOMER_ROLE.equals(account.getRole())) {
            return null;
        }

        return customerRepository.findById(account.getId()).orElse(null);
    }

    private AuthResponse toResponse(Account account, Customer customer) {
        return new AuthResponse(
                account.getId(),
                account.getName(),
                account.getEmail(),
                account.getPhone(),
                account.getAddress(),
                account.getRole(),
                account.getEnabled(),
                customer == null ? null : customer.getCategory(),
                customer == null ? null : customer.getShipToAddress()
        );
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect");
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

}
