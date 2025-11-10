package com.mycompany.evmc.mapper;

import com.mycompany.evmc.dto.AppUserDto;
import com.mycompany.evmc.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    // Convert entity → DTO (don't expose passwordHash)
    @Mapping(source = "passwordHash", target = "password", ignore = true)
    AppUserDto toDto(AppUser user);

    // Convert DTO → entity (without encoding)
    @Mapping(source = "password", target = "passwordHash", ignore = true)
    AppUser toEntity(AppUserDto dto);

    List<AppUserDto> toDtoList(List<AppUser> all);

    // Custom helper for password encoding
    default AppUser toEntity(AppUserDto dto, PasswordEncoder encoder) {
        if (dto == null) {
            return null;
        }

        AppUser.AppUserBuilder builder = AppUser.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .role(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            builder.passwordHash(encoder.encode(dto.getPassword()));
        }

        return builder.build();
    }
}
