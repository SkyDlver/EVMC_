package com.mycompany.evmc.dto;

import com.mycompany.evmc.model.EmployeeRole;
import com.mycompany.evmc.model.Gender;
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
    private String surName;
    private String department;
    private Gender gender;
    private EmployeeRole employeeRole;
    private LocalDate hiredAt;
    private boolean onHoliday;
    private LocalDate holidayStartDate;
    private LocalDate holidayEndDate;
}
