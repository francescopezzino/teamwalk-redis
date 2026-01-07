package com.fp.teamwalk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fp.teamwalk.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Added for better constructor support
@Builder            // Recommended for modern Spring Boot 4.0 data handling
@Entity
@Table(name = "StepCounters")
public class StepCounter implements Serializable { // Requirement for Redis L2 Cache

    private static final long serialVersionUID = 1L; // Best practice for cross-version serialization

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer steps = 0;

    @OneToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference // Prevents infinite recursion during Redis serialization
    private Team team;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    private State state;

}
