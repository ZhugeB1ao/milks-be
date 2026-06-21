package com.example.milksbe.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must contain 10 to 15 digits")
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be from 6 to 100 characters")
        String password,

        @NotBlank(message = "Shipping address is required")
        @Size(max = 255, message = "Shipping address must not exceed 255 characters")
        String shipToAddress
) {

}
