package com.mycompany.evmc.mapper;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // Entity -> DTO
    @Mapping(target = "role", expression = "java(employee.getRole() != null ? employee.getRole().name() : null)")
    EmployeeDto toDto(Employee employee);

    // DTO -> Entity
    @Mapping(target = "role", expression = "java(dto.getRole() != null ? com.mycompany.evmc.model.Role.valueOf(dto.getRole()) : null)")
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "timezone", ignore = true)
    Employee toEntity(EmployeeDto dto);

    List<EmployeeDto> toDtoList(List<Employee> employees);
}
