package com.mycompany.evmc.repository;

import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, UUID> {

    List<VacationRequest> findByEmployee_Id(UUID employeeId);

    List<VacationRequest> findByStatus(String status);

    List<VacationRequest> findByApprover_Id(UUID approverId);

    List<VacationRequest> findByEmployee_Team(String team);

    List<VacationRequest> findByEmployee(Employee employee);
    List<VacationRequest> findAllByEmployee(Employee employee);
}
