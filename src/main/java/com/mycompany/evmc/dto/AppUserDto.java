package com.mycompany.evmc.dto;

import com.mycompany.evmc.model.AppUser;
import com.mycompany.evmc.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDto {

    private Long id;

    private String username;

    // Only used when creating or updating passwords
    private String password;

    private Role role;
}
