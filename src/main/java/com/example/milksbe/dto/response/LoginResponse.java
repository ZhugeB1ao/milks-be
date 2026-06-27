package com.example.milksbe.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        AuthResponse user
) {
}