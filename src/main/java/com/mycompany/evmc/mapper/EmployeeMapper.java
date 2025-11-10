package com.mycompany.evmc.mapper;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "employeeRole", source = "employeeRole")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "employeeRole", source = "employeeRole")
    Employee toEntity(EmployeeDto dto);

    List<EmployeeDto> toDtoList(List<Employee> employees);
}
