package com.mycompany.evmc.service.impl;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.mapper.EmployeeMapper;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.Role;
import com.mycompany.evmc.repository.EmployeeRepository;
import com.mycompany.evmc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
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

        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setEmployeeNumber("EMP-" + System.currentTimeMillis());
        employee.setHiredAt(employee.getHiredAt() != null ? employee.getHiredAt() : LocalDate.now());

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
        existing.setTeam(employeeDto.getTeam());
        existing.setRole(Role.valueOf(employeeDto.getRole()));

        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toDto(updated);
    }

    @Override
    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<EmployeeDto> getEmployeesByManager(UUID managerId) {
        return employeeMapper.toDtoList(employeeRepository.findByManager_Id(managerId));
    }

    @Override
    public List<EmployeeDto> getEmployeesByTeam(String team) {
        return employeeMapper.toDtoList(employeeRepository.findByTeam(team));
    }
}
