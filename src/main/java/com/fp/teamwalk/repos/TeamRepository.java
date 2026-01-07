package com.fp.teamwalk.repos;

import com.fp.teamwalk.domain.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Optimized fetch for caching.
     * Using @EntityGraph ensures that 'employees' and 'stepcounter' are loaded
     * in a single query before the Service maps them to a DTO for the Redis cache.
     */
    @EntityGraph(attributePaths = {"employees", "stepcounter"})
    Optional<Team> findWithDetailsById(Long id);
}
