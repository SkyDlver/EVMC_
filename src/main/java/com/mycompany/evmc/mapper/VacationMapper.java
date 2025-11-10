package com.mycompany.evmc.mapper;

import com.mycompany.evmc.dto.VacationDto;
import com.mycompany.evmc.model.Vacation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VacationMapper {
    VacationDto toDto(Vacation vacation);
    Vacation toEntity(VacationDto dto);
    List<VacationDto> toDtoList(List<Vacation> vacations);
}
