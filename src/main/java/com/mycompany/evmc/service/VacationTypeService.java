package com.mycompany.evmc.service;

import com.mycompany.evmc.model.VacationType;
import com.mycompany.evmc.repository.VacationTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VacationTypeService {

    private final VacationTypeRepository vacationTypeRepository;

    public VacationTypeService(VacationTypeRepository vacationTypeRepository) {
        this.vacationTypeRepository = vacationTypeRepository;
    }

    // Get all vacation types
    public List<VacationType> getAllTypes() {
        return vacationTypeRepository.findAll();
    }

    // Get vacation type by ID
    public Optional<VacationType> getType(Long id) {
        return vacationTypeRepository.findById(id);
    }

    // Create a new vacation type
    public VacationType createType(VacationType type) {
        return vacationTypeRepository.save(type);
    }

    // Update an existing vacation type
    public VacationType updateType(Long id, VacationType updatedType) {
        VacationType type = vacationTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vacation type not found"));
        type.setKey(updatedType.getKey());
        type.setDisplayName(updatedType.getDisplayName());
        type.setAccrual(updatedType.isAccrual());
        return vacationTypeRepository.save(type);
    }

    // Delete a vacation type
    public void deleteType(Long id) {
        vacationTypeRepository.deleteById(id);
    }

    // Find by key (e.g. "annual", "sick")
    public Optional<VacationType> findByKey(String key) {
        return vacationTypeRepository.findByKey(key);
    }
}
