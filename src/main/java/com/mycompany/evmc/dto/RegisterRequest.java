package com.mycompany.evmc.dto;

import com.mycompany.evmc.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String password;

    private Role role = Role.EMPLOYEE; // Default role

    @NotNull(message = "Hired date is required")
    private LocalDate hiredAt;

    private String team;
}
