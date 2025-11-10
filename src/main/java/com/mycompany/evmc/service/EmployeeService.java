package com.mycompany.evmc.service;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(UUID id);

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(UUID id, EmployeeDto employeeDto);

    void deleteEmployee(UUID id);

    // ✅ New holiday methods
    void startHoliday(UUID id, LocalDate startDate, LocalDate endDate);
    void endHoliday(UUID id);

    boolean isHolidayEligible(UUID employeeId, LocalDate proposedStart);
}
