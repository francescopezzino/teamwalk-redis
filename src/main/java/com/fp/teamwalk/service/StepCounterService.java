package com.fp.teamwalk.service;

import com.fp.teamwalk.model.StepCounterDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service for StepCounter operations.
 * Optimized for tiered caching (Caffeine L1 + Redis L2) in 2026.
 */
public interface StepCounterService {

    /**
     * Creates a new counter.
     * Implementation should trigger @CacheEvict for the 'leaderboard' 
     * to ensure cluster-wide consistency.
     */
    Optional<StepCounterDTO> addTeamStepCounter(StepCounterDTO stepCounterDto);

    /**
     * Retrieves a counter by ID.
     * Implementation uses @Cacheable to check Caffeine first, then Redis.
     */
    Optional<StepCounterDTO> getStepCounterById(Long id);

    /**
     * Retrieves the leaderboard.
     * In 2026, this is cached in Redis (L2) to prevent expensive sorting 
     * queries on the primary database.
     */
    Optional<List<StepCounterDTO>> getAllTeamScoreDesc();
}
