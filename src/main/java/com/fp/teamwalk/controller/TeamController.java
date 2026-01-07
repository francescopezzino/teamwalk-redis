package com.fp.teamwalk.controller;

import com.fp.teamwalk.domain.Employee;
import com.fp.teamwalk.exception.ResourceNotFoundException;
import com.fp.teamwalk.model.StepCounterDTO;
import com.fp.teamwalk.model.TeamDTO;
import com.fp.teamwalk.service.EmployeeService;
import com.fp.teamwalk.service.StepCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
@Validated // Required for @Min validation on parameters
@Tag(name = "Team Management", description = "Endpoints for team steps and scoring")
public class TeamController {

    private final EmployeeService employeeService;
    private final StepCounterService stepCounterService;

    /**
     * US 2: Add steps to team.
     * Uses atomic increment via service and evicts L1/L2 caches.
     */
    @PutMapping("/addSteps/{employeeId}")
    @Operation(summary = "Add steps to a team's counter via an employee ID")
    public ResponseEntity<StepCounterDTO> addStepsToTeamx(
            @PathVariable Long employeeId,
            @RequestParam @Min(1) Integer steps) {

        // Service handles @CacheEvict and DB atomic increment
        TeamDTO teamDTO = employeeService.addStepsToTeamStepCounterByEmployeeId(employeeId, steps)
                .orElseThrow(() -> new ResourceNotFoundException("Team or StepCounter not found for Employee: " + employeeId));

        // The service layer findById is @Cacheable (Caffeine/Redis)
        return stepCounterService.getStepCounterById(teamDTO.id())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Step counter data inconsistent in storage."));
    }

    /**
     * US 3: Get team’s step count.
     * Highly optimized via Tiered Caching.
     */
    @GetMapping("/teamscore/{employeeId}")
    @Operation(summary = "Retrieve current team score for an employee")
    public ResponseEntity<StepCounterDTO> retrieveTeamSteps(@PathVariable Long employeeId) {

        Employee employee = employeeService.findEmployeeById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        if (employee.getTeam() == null || employee.getTeam().getStepcounter() == null) {
            throw new ResourceNotFoundException("Employee's team does not have an active step counter.");
        }

        // Cache-aside pattern: Checks local RAM first, then Redis, then DB
        return stepCounterService.getStepCounterById(employee.getTeam().getStepcounter().getId())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Step counter not found."));
    }
}
