package com.example.milksbe.dto.response;

public record AuthResponse(
        Integer id,
        String name,
        String email,
        String phone,
        String address,
        String role,
        Boolean enabled,
        String customerCategory,
        String shipToAddress
) {
}
