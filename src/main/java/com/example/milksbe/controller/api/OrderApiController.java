package com.example.milksbe.controller.api;

import com.example.milksbe.dto.request.CheckoutRequest;
import com.example.milksbe.dto.response.CheckoutResponse;
import com.example.milksbe.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {
    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication
    ) {
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        return orderService.checkout(authentication.getName(), request);
    }
}
