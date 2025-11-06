package com.mycompany.evmc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vacation_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    private VacationType type;

    @Column(nullable = false)
    private OffsetDateTime startTimestamp;

    @Column(nullable = false)
    private OffsetDateTime endTimestamp;

    @Column(nullable = false)
    private double units; // e.g. 1.0 day or 4.0 hours

    @Column(nullable = false)
    private String unitType = "days"; // days or hours

    private String reason;

    @Builder.Default
    @Column(nullable = false)
    private String status = "pending"; // pending | approved | rejected | cancelled

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver_id")
    private Employee approver;

    private String approverComment;
}
