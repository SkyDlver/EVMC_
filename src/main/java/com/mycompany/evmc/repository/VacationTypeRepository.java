package com.mycompany.evmc.repository;

import com.mycompany.evmc.model.VacationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VacationTypeRepository extends JpaRepository<VacationType, Long> {

    Optional<VacationType> findByKey(String key);

    boolean existsByKey(String key);
}
