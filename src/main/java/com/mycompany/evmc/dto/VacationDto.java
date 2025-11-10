package com.mycompany.evmc.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class VacationDto {
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
}
