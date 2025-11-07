package com.mycompany.evmc.service.impl;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.mapper.EmployeeMapper;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.Role;
import com.mycompany.evmc.repository.EmployeeRepository;
import com.mycompany.evmc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return employeeMapper.toDtoList(employeeRepository.findAll());
    }

    @Override
    public EmployeeDto getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found with id: " + id));
        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        if (employeeRepository.findByEmail(employeeDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Map DTO to entity
        Employee employee = employeeMapper.toEntity(employeeDto);

        // Set employee number and hired date
        employee.setHiredAt(employee.getHiredAt() != null ? employee.getHiredAt() : LocalDate.now());

        // Handle password
        if (employeeDto.getPassword() != null && !employeeDto.getPassword().isEmpty()) {
            employee.setPasswordHash(passwordEncoder.encode(employeeDto.getPassword()));
        }

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }


    @Override
    public EmployeeDto updateEmployee(UUID id, EmployeeDto employeeDto) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found with id: " + id));

        existing.setFirstName(employeeDto.getFirstName());
        existing.setLastName(employeeDto.getLastName());
        existing.setEmail(employeeDto.getEmail());
        existing.setRole(Role.valueOf(employeeDto.getRole()));

        // Update password if provided
        if (employeeDto.getPassword() != null && !employeeDto.getPassword().isEmpty()) {
            existing.setPasswordHash(passwordEncoder.encode(employeeDto.getPassword()));
        }

        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toDto(updated);
    }


    @Override
    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }


    @Override
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Employee not found with email: " + email));
    }


    @Override
    public void startHoliday(UUID id, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Holiday start and end dates required");
        }

        if (!isHolidayEligible(id, startDate)) {
            throw new IllegalStateException("Employee cannot take holiday yet (10-month rule)");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        employee.setOnHoliday(true);
        employee.setHolidayStartDate(startDate);
        employee.setHolidayEndDate(endDate);

        employeeRepository.save(employee);
    }


    @Override
    public void endHoliday(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found with id: " + id));

        employee.setOnHoliday(false);
        employee.setHolidayStartDate(null);
        employee.setHolidayEndDate(null);

        employeeRepository.save(employee);
    }

    @Override
    public boolean isHolidayEligible(UUID employeeId, LocalDate proposedStart) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        LocalDate now = LocalDate.now();
        LocalDate hireEligibleDate = employee.getHiredAt().plusMonths(10);
        LocalDate lastHolidayEligibleDate = employee.getHolidayEndDate() != null
                ? employee.getHolidayEndDate().plusMonths(10)
                : hireEligibleDate;

        // Employee eligible only if proposedStart >= both thresholds
        return !proposedStart.isBefore(hireEligibleDate) && !proposedStart.isBefore(lastHolidayEligibleDate);
    }

}
