package com.mycompany.evmc.service;

import com.mycompany.evmc.dto.AppUserDto;
import com.mycompany.evmc.model.AppUser;

import java.util.List;

public interface AppUserService {

    List<AppUserDto> getAllUsers();

    AppUserDto getUserById(Long id);

    AppUserDto createUser(AppUserDto dto);

    AppUserDto updateUser(Long id, AppUserDto dto);

    void deleteUser(Long id);

    AppUser findByUsername(String username);
}
