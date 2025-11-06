package com.mycompany.evmc.service;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(UUID id);

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(UUID id, EmployeeDto employeeDto);

    void deleteEmployee(UUID id);

    List<EmployeeDto> getEmployeesByManager(UUID managerId);

    List<EmployeeDto> getEmployeesByTeam(String team);

    Employee findByEmail(String username);
}
