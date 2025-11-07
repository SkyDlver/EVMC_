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
    private String firstName;
    private String lastName;
    private String email;
    private String password; // for input only, will be hashed in entity
    private String role; // store role as String
    private LocalDate hiredAt;
    private boolean onHoliday;
    private LocalDate holidayStartDate;
    private LocalDate holidayEndDate;
}
