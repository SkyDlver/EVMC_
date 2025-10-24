package com.mycompany.evmc.repository;

import com.mycompany.evmc.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {

    Optional<LeaveBalance> findByEmployee_IdAndType_Id(UUID employeeId, Long typeId);

    List<LeaveBalance> findByEmployee_Id(UUID employeeId);
}
