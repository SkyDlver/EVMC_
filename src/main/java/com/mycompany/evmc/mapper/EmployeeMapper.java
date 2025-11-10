package com.mycompany.evmc.mapper;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDto toDto(Employee employee);

    Employee toEntity(EmployeeDto dto);

    List<EmployeeDto> toDtoList(List<Employee> employees);
}
