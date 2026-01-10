package com.fp.teamwalk.service.impl;


import com.fp.teamwalk.domain.StepCounter;
import com.fp.teamwalk.domain.Team;
import com.fp.teamwalk.exception.ResourceNotFoundException;
import com.fp.teamwalk.mapper.StepCounterMapper;
import com.fp.teamwalk.model.StepCounterDTO;
import com.fp.teamwalk.repos.StepCounterRepository;
import com.fp.teamwalk.repos.TeamRepository;
import com.fp.teamwalk.service.StepCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fp.teamwalk.enums.State.ENABLED;

@Service
@RequiredArgsConstructor
public class StepCounterServiceImpl implements StepCounterService {

    private final TeamRepository teamRepository;
    private final StepCounterMapper stepCounterMapper;
    private final StepCounterRepository stepCounterRepository;

    /**
     * Creating a counter affects the leaderboard.
     * @CacheEvict clears both Caffeine (local) and Redis (global) 2026 layers.
     */
    @Override
    @Transactional
    @CacheEvict(value = "leaderboard", beforeInvocation = true, allEntries = true)
    public Optional<StepCounterDTO> addTeamStepCounter(StepCounterDTO stepCounterDto) {
        Team team = teamRepository.findById(stepCounterDto.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        StepCounter stepCounter = stepCounterMapper.toStepCounterEntity(stepCounterDto);
        stepCounter.setTeam(team);
        stepCounter.setState(ENABLED);
        stepCounterRepository.save(stepCounter);

        team.setStepcounter(stepCounter);
        teamRepository.save(team);

        return Optional.of(stepCounterMapper.toStepCounterDto(stepCounter));
    }

    /**
     * Tiered Lookup (2026 Pattern):
     * 1. Checks Caffeine (L1) RAM.
     * 2. Checks Redis (L2) Shared Memory.
     * 3. DB Fallback.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "step_counters", key = "#id", unless = "#result == null")
    public Optional<StepCounterDTO> getStepCounterById(Long id) {
        return stepCounterRepository.findById(id)
                .map(stepCounterMapper::toStepCounterDto);
    }

    /**
     * Caches the entire enabled leaderboard in 2026.
     * Hits Redis/Caffeine to avoid heavy database sorting queries.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "leaderboard", key = "'all_enabled'")
    public Optional<List<StepCounterDTO>> getAllTeamScoreDesc() {
        List<StepCounter> stepCounters = stepCounterRepository.findAllByOrderByStepsDesc();

        List<StepCounterDTO> stepCounterDtos = stepCounters.stream()
                .filter(sc -> sc.getState() == ENABLED)
                .map(stepCounterMapper::toStepCounterDto)
                .toList();

        return Optional.of(stepCounterDtos);
    }
}
