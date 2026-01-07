package com.fp.teamwalk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Teams")
// Required for L2 (Redis) distributed caching
public class Team implements Serializable {

    private static final long serialVersionUID = 20260107L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_counter_id")
    @JsonManagedReference
    private StepCounter stepcounter;

    @Version
    private Integer version;

    @Override
    public String toString() {
        return "Team{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
