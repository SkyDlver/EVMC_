package com.mycompany.evmc.service.impl;

import com.mycompany.evmc.dto.AppUserDto;
import com.mycompany.evmc.mapper.AppUserMapper;
import com.mycompany.evmc.model.AppUser;
import com.mycompany.evmc.repository.AppUserRepository;
import com.mycompany.evmc.service.AppUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AppUserServiceImpl(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    @Override
    public List<AppUserDto> getAllUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(appUserMapper::toDto)
                .toList();
    }

    @Override
    public AppUserDto getUserById(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        return appUserMapper.toDto(user);
    }

    @Override
    public AppUserDto createUser(AppUserDto dto) {
        AppUser entity = appUserMapper.toEntity(dto);
        AppUser saved = appUserRepository.save(entity);
        return appUserMapper.toDto(saved);
    }

    @Override
    public AppUserDto updateUser(Long id, AppUserDto dto) {
        AppUser existing = appUserRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        // update fields
        existing.setUsername(dto.getUsername());
        existing.setRole(dto.getRole());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPasswordHash(dto.getPassword());
        }

        AppUser updated = appUserRepository.save(existing);
        return appUserMapper.toDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!appUserRepository.existsById(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        appUserRepository.deleteById(id);
    }

    @Override
    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
    }
}
