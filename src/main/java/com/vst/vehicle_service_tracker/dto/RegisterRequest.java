package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String role; // optional; defaults to CUSTOMER

    // Customer-specific fields
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}