package com.mycompany.evmc.repository;

import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VacationRepository extends JpaRepository<Vacation, UUID> {
    List<Vacation> findByEmployee(Employee employee);
}
