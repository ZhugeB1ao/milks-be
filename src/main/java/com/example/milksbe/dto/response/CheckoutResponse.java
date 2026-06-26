package com.example.milksbe.dto.response;

import java.time.Instant;
import java.util.List;

public record CheckoutResponse(
        Integer id,
        Instant orderDate,
        String status,
        Integer customerId,
        Double totalAmount,
        List<Item> items
) {
    public record Item(
            Integer productId,
            String productDescription,
            Integer quantity,
            Double price,
            Double discount,
            Double lineTotal
    ){
    }
}
