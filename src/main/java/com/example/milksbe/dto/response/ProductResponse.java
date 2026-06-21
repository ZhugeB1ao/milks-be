package com.example.milksbe.dto.response;

public record ProductResponse(
        Integer id,
        String description,
        Double price,
        Double discount,
        String imageUrl,
        Integer categoryId,
        String categoryName
) {
}