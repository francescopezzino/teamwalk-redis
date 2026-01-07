package com.fp.teamwalk.service;

import com.fp.teamwalk.model.TeamDTO;
import java.util.Optional;

/**
 * Service for Team operations.
 * Designed for 2026 Tiered Caching (Caffeine L1 + Redis L2).
 */
public interface TeamService {

    /**
     * Creates a team.
     * Implementation should trigger @CacheEvict for 'leaderboard' and 'teams'
     * to ensure the cluster refreshes its shared state.
     */
    Optional<TeamDTO> createTeamWithEmployees(TeamDTO request);

    /**
     * Disables/Removes a step counter.
     * Implementation must use @CacheEvict to clear the specific team
     * and the leaderboard from Redis (L2) and Caffeine (L1).
     */
    Optional<TeamDTO> removeStepCounter(Long teamId);

    /**
     * Retrieves a team.
     * Implementation uses @Cacheable to check local RAM (Caffeine)
     * before checking the shared Redis cluster.
     */
    Optional<TeamDTO> findTeamById(Long id);

}
