package com.mycompany.evmc.mapper;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // ✅ Map role enum -> string
    @Mapping(target = "role", expression = "java(employee.getRole().name())")
    EmployeeDto toDto(Employee employee);

    // ✅ Ignore password and other unmapped fields when converting DTO -> Entity
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "timezone", ignore = true)
    @Mapping(target = "role", expression = "java(com.mycompany.evmc.model.Role.valueOf(dto.getRole()))")
    Employee toEntity(EmployeeDto dto);

    List<EmployeeDto> toDtoList(List<Employee> employees);
}
