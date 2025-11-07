package com.mycompany.evmc.mapper;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "role", expression = "java(employee.getRole() != null ? employee.getRole().name() : null)")
    @Mapping(target = "password", ignore = true)
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "role", expression = "java(dto.getRole() != null ? com.mycompany.evmc.model.Role.valueOf(dto.getRole()) : null)")
    @Mapping(target = "passwordHash", ignore = true)
    Employee toEntity(EmployeeDto dto);

    List<EmployeeDto> toDtoList(List<Employee> employees);

    default Employee toEntity(EmployeeDto dto, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        Employee employee = toEntity(dto);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            employee.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        return employee;
    }
}
