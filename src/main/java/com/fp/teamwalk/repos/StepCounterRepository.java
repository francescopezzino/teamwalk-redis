package com.fp.teamwalk.repos;


import com.fp.teamwalk.domain.StepCounter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepCounterRepository extends JpaRepository<StepCounter, Long> {

    /**
     * Retrieves all counters.
     * Note: Typically not cached at the repository level to avoid
     * large collection invalidation issues.
     */
    List<StepCounter> findAllByOrderByStepsDesc();

    /**
     * Increments steps in DB and EVICTS the cache.
     * In a hybrid setup, @CacheEvict ensures:
     * 1. The local Caffeine (L1) entry is removed.
     * 2. The shared Redis (L2) entry is removed.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE StepCounter s SET s.steps = s.steps + :steps WHERE s.id = :id")
    @CacheEvict(value = "step_counters", key = "#id")
    void incrementSteps(@Param("id") Long id, @Param("steps") Integer steps);
}
