package com.mycompany.evmc.service.impl;

import com.mycompany.evmc.dto.LoginRequest;
import com.mycompany.evmc.dto.LoginResponse;
import com.mycompany.evmc.dto.RegisterRequest;
import com.mycompany.evmc.dto.RegisterResponse;
import com.mycompany.evmc.mapper.EmployeeMapper;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.Role;
import com.mycompany.evmc.repository.EmployeeRepository;
import com.mycompany.evmc.security.JwtService;
import com.mycompany.evmc.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;
    private final JwtService jwtService;

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        Optional<Employee> userOpt = employeeRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return LoginResponse.builder()
                    .message("Invalid credentials: email not found")
                    .token(null)
                    .build();
        }

        Employee employee = userOpt.get();

        // ✅ Check password using PasswordEncoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), employee.getPasswordHash())) {
            return LoginResponse.builder()
                    .message("Invalid password")
                    .token(null)
                    .build();
        }

        // ✅ Generate JWT token using JwtService
        String jwtToken = jwtService.generateToken(Map.of(
                "id", employee.getId().toString(),
                "role", employee.getRole().name(),
                "team", employee.getTeam()
        ), employee.getEmail());


        return LoginResponse.builder()
                .message("Login successful")
                .token(jwtToken)
                .build();
    }

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            return new RegisterResponse("Email already registered", null);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        Employee employee = Employee.builder()
                .employeeNumber("EMP-" + System.currentTimeMillis())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .role(request.getRole() != null ? request.getRole() : Role.EMPLOYEE)
                .team(request.getTeam())
                .hiredAt(request.getHiredAt() != null ? request.getHiredAt() : LocalDate.now())
                .build();

        Employee saved = employeeRepository.save(employee);

        return new RegisterResponse(
                "Registration successful",
                employeeMapper.toDto(saved)
        );
    }
}
