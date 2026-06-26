package com.example.milksbe.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CheckoutRequest(
        @NotEmpty(message = "Cart must have at least one item")
        List<@Valid Item> items
) {
    public record Item(
            @NotNull(message = "Product id is required")
            @Positive(message = "Product id must be positive")
            Integer productId,

            @NotNull(message = "Quantity is required")
            @Min(value = 1, message = "Quantity must be at least 1")
            Integer quantity
    ) {
    }
}
