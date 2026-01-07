package com.fp.teamwalk.controller;

import com.fp.teamwalk.model.StepCounterDTO;
import com.fp.teamwalk.model.TeamDTO;
import com.fp.teamwalk.service.StepCounterService;
import com.fp.teamwalk.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/teams")
@Tag(name = "Admin Management", description = "Team and Leaderboard administration")
public class TeamManagementController {

    private final TeamService teamService;
    private final StepCounterService stepCounterService;

    @PostMapping
    @Operation(summary = "Create a new team with employees")
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO request) {
        Optional<TeamDTO> teamOptional = teamService.createTeamWithEmployees(request);
        if (teamOptional.isPresent()) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(teamOptional.get().id())
                    .toUri();
            return ResponseEntity.created(location).body(teamOptional.get());
        }
        throw new UnsupportedOperationException("Some errors occurred, cannot create a step counter");
    }

    @PostMapping("/addTeamStepCounter")
    @Operation(summary = "US 1: Assign a step counter to a team")
    public ResponseEntity<StepCounterDTO> createTeamStepCounter(@Valid @RequestBody StepCounterDTO request) {
        return stepCounterService.addTeamStepCounter(request)
                .map(dto -> {
                    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(dto.id())
                            .toUri();
                    return ResponseEntity.created(location).body(dto);
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/removeTeamStepCounter/{teamId}")
    @Operation(summary = "US 1: Disable/Remove a team's step counter")
    public ResponseEntity<TeamDTO> removeTeamStepCounterId(@PathVariable Long teamId) {
        return teamService.removeStepCounter(teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "US 4: View leaderboard (Sorted Descending)")
    public ResponseEntity<List<StepCounterDTO>> getLeaderboard() {
        return stepCounterService.getAllTeamScoreDesc()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping(value = "/leaderboardFlux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream leaderboard updates via Server-Sent Events (SSE)")
    public Flux<StepCounterDTO> getLeaderboardFlux() {
        return stepCounterService.getAllTeamScoreDesc()
                .map(Flux::fromIterable)
                .orElse(Flux.empty());
    }
}
