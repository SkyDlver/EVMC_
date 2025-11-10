package com.mycompany.evmc.service;

import com.mycompany.evmc.dto.VacationDto;

import java.util.List;
import java.util.UUID;

public interface VacationService {

    List<VacationDto> getVacationsByEmployeeId(UUID employeeId);

    VacationDto startVacation(UUID employeeId, VacationDto vacation);

    void endVacation(UUID vacationId);
}
