package com.mycompany.evmc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private UUID id;
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String team;
    private String role;
    private LocalDate hiredAt;
}
