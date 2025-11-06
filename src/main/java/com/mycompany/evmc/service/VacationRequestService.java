package com.mycompany.evmc.service;

import com.mycompany.evmc.model.VacationRequest;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.repository.VacationRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class VacationRequestService {

    private final VacationRequestRepository vacationRequestRepository;

    public VacationRequestService(VacationRequestRepository vacationRequestRepository) {
        this.vacationRequestRepository = vacationRequestRepository;
    }

    // Create a new vacation request
    public VacationRequest createRequest(VacationRequest request) {
        request.setStatus("pending");
        return vacationRequestRepository.save(request);
    }

    // Get a vacation request by ID
    public Optional<VacationRequest> getRequest(UUID id) {
        return vacationRequestRepository.findById(id);
    }

    // Get all vacation requests
    public List<VacationRequest> getAllRequests() {
        return vacationRequestRepository.findAll();
    }

    // Get all requests for a specific employee
    public List<VacationRequest> getRequestsByEmployee(Employee employee) {
        return vacationRequestRepository.findByEmployee(employee);
    }

    // Update the status (approve/reject/cancel)
    public VacationRequest updateStatus(UUID id, String status, Employee approver, String comment) {
        VacationRequest request = vacationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        request.setStatus(status);
        request.setApprover(approver);
        request.setApproverComment(comment);
        return vacationRequestRepository.save(request);
    }

    // Delete a request
    public void deleteRequest(UUID id) {
        vacationRequestRepository.deleteById(id);
    }
}
