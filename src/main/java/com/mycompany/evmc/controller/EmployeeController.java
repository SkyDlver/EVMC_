package com.mycompany.evmc.controller;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * Create a new employee (replaces AuthController /register)
     */
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.ok(employeeService.createEmployee(employeeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeDto employeeDto
    ) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByManager(@PathVariable UUID managerId) {
        return ResponseEntity.ok(employeeService.getEmployeesByManager(managerId));
    }

    @GetMapping("/team/{team}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByTeam(@PathVariable String team) {
        return ResponseEntity.ok(employeeService.getEmployeesByTeam(team));
    }
}
