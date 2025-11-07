package com.mycompany.evmc.service;

import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.service.EmployeeService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeService employeeService;

    public EmployeeUserDetailsService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeService.findByEmail(email);
        return User.withUsername(employee.getEmail())
                .password(employee.getPasswordHash())
                .roles(employee.getRole().name())
                .build();
    }
}
