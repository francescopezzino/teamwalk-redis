package com.fp.teamwalk.service;

import com.fp.teamwalk.domain.Employee;
import com.fp.teamwalk.model.TeamDTO;

import java.util.Optional;

public interface EmployeeService {

    Optional<Employee> findEmployeeById(Long id);

    Optional<TeamDTO> addStepsToTeamStepCounterByEmployeeId(Long employeeId, Integer employeeSteps);

}
