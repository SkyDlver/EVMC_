package com.mycompany.evmc.security;

import com.mycompany.evmc.model.Employee;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class CustomEmployeeDetails implements UserDetails {

    private final Employee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security expects roles to start with "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()));
    }

    @Override
    public String getPassword() {
        return employee.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // you can add logic later
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // or tie to a field like employee.isLocked()
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // same as above
    }

    @Override
    public boolean isEnabled() {
        return true; // or employee.isActive()
    }

    public UUID getId() {
        return employee.getId();
    }

    public String getFullName() {
        return employee.getFirstName() + " " + employee.getLastName();
    }
}
