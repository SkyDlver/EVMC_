package com.mycompany.evmc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "leave_balances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "type_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private VacationType type;

    @Builder.Default
    @Column(nullable = false)
    private double balance = 0.0;

    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
