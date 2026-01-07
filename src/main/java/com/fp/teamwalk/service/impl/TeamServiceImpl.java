package com.fp.teamwalk.service.impl;


import com.fp.teamwalk.domain.Employee;
import com.fp.teamwalk.domain.StepCounter;
import com.fp.teamwalk.domain.Team;
import com.fp.teamwalk.enums.State;
import com.fp.teamwalk.exception.ResourceNotFoundException;
import com.fp.teamwalk.mapper.TeamMapper;
import com.fp.teamwalk.model.TeamDTO;
import com.fp.teamwalk.repos.StepCounterRepository;
import com.fp.teamwalk.repos.TeamRepository;
import com.fp.teamwalk.service.TeamService;
import com.fp.teamwalk.service.impl.TeamServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final TeamRepository teamRepository;
    private final StepCounterRepository stepCounterRepository;

    @Override
    @Transactional
    // 2026 Best Practice: Evict all layers (L1/L2) for any new team to refresh leaderboard
    @CacheEvict(value = "leaderboard", allEntries = true)
    public Optional<TeamDTO> createTeamWithEmployees(TeamDTO teamDto) {
        Team team = teamMapper.toTeamEntity(teamDto);

        // Manually manage the bi-directional relationship for Hibernate consistency
        if (team.getEmployees() != null) {
            team.getEmployees().forEach(emp -> emp.setTeam(team));
        }

        Team saved = teamRepository.save(team);
        return Optional.of(teamMapper.toTeamDto(saved));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "teams", key = "#id"),
            // Essential for 2026: Leaderboard must be cleared if a team's counter is disabled
            @CacheEvict(value = "leaderboard", allEntries = true)
    })
    public Optional<TeamDTO> removeStepCounter(Long id) {
        // Use optimized EntityGraph fetch to avoid N+1 queries during DTO mapping
        Team team = teamRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));

        if (team.getStepcounter() != null) {
            StepCounter sc = team.getStepcounter();
            sc.setState(State.DISABLED);
            stepCounterRepository.save(sc);

            team.setStepcounter(null);
            teamRepository.save(team);
        }
        return Optional.of(teamMapper.toTeamDto(team));
    }

    @Override
    @Transactional(readOnly = true)
    // Tiered Lookup (L1 Caffeine -> L2 Redis -> DB)
    @Cacheable(value = "teams", key = "#id", unless = "#result == null")
    public Optional<TeamDTO> findTeamById(Long id) {
        return teamRepository.findWithDetailsById(id)
                .map(teamMapper::toTeamDto);
    }
}
