package com.fp.teamwalk.repos;

import com.fp.teamwalk.domain.Employee;
import com.fp.teamwalk.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Used for L1/L2 tiered caching in the Service layer
    @Query("SELECT t FROM Team t WHERE t.id = :id")
    Optional<Team> findTeamById(Long id);
}
