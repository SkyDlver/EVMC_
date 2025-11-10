package com.mycompany.evmc.service.impl;

import com.mycompany.evmc.dto.VacationDto;
import com.mycompany.evmc.mapper.VacationMapper;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.Vacation;
import com.mycompany.evmc.repository.EmployeeRepository;
import com.mycompany.evmc.repository.VacationRepository;
import com.mycompany.evmc.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;
    private final VacationMapper vacationMapper;

    @Override
    public List<VacationDto> getVacationsByEmployeeId(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        return vacationMapper.toDtoList(vacationRepository.findByEmployee(employee));
    }

    @Override
    public VacationDto startVacation(UUID employeeId, VacationDto vacationDto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        Vacation vacation = Vacation.builder()
                .employee(employee)
                .startDate(vacationDto.getStartDate())
                .endDate(vacationDto.getEndDate())
                .build();

        Vacation saved = vacationRepository.save(vacation);
        return vacationMapper.toDto(saved);
    }

    @Override
    public void endVacation(UUID vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new NoSuchElementException("Vacation not found"));

        vacationRepository.delete(vacation);
    }
}
