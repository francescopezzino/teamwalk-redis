package com.fp.teamwalk.service.impl;

import com.fp.teamwalk.domain.Employee;
import com.fp.teamwalk.domain.StepCounter;
import com.fp.teamwalk.domain.Team;
import com.fp.teamwalk.exception.ResourceNotFoundException;
import com.fp.teamwalk.mapper.TeamMapper;
import com.fp.teamwalk.model.TeamDTO;
import com.fp.teamwalk.repos.EmployeeRepository;
import com.fp.teamwalk.repos.StepCounterRepository;
import com.fp.teamwalk.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StepCounterRepository stepCounterRepository;
    private final TeamMapper teamMapper;

    /**
     * L1/L2 Caching:
     * 1. Check Caffeine (Local)
     * 2. Check Redis (Shared)
     * 3. DB Fallback
     */
    @Override
    @Cacheable(value = "employees", key = "#id", unless = "#result == null")
    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    /**
     * Updates step counts and evicts stale cache entries.
     * In 2026, eviction is preferred over update for complex nested DTOs
     * to ensure absolute consistency across the L1/L2 layers.
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "teams", key = "#result.id", condition = "#result != null"),
            @CacheEvict(value = "step_counters", allEntries = true)
    })
    public Optional<TeamDTO> addStepsToTeamStepCounterByEmployeeId(Long employeeId, Integer employeeSteps) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Team team = employee.getTeam();
        if (team != null && team.getStepcounter() != null) {
            Long stepCounterId = team.getStepcounter().getId();

            StepCounter stepCounter = stepCounterRepository.findById(stepCounterId)
                    .orElseThrow(() -> new ResourceNotFoundException("StepCounter not found"));

            Integer currentSteps = stepCounter.getSteps() != null ? stepCounter.getSteps() : 0;
            stepCounter.setSteps(currentSteps + employeeSteps);

            stepCounterRepository.save(stepCounter);
        }

        return Optional.ofNullable(teamMapper.toTeamDto(team));
    }
}
