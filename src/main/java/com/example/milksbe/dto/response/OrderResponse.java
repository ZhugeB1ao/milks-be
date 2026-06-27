package com.example.milksbe.dto.response;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Integer id,
        Instant orderDate,
        String status,
        String statusText,
        Integer customerId,
        String customerName,
        Double totalAmount,
        List<Item> items
) {
    public record Item(
            Integer productId,
            String productName,
            Integer quantity,
            Double price,
            Double discount,
            Double lineTotal
    ) {
    }
}