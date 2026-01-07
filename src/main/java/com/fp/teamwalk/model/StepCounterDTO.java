package com.fp.teamwalk.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * StepCounterDTO modified for 2026 hybrid caching.
 * Implements Serializable for Redis L2 compatibility.
 */
public record StepCounterDTO(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Long id,
        String name,
        Long teamId,
        Integer steps
) implements Serializable {

    // Recommended for stable versioning during 2026 updates
    private static final long serialVersionUID = 1L;

    public StepCounterDTO {
        if (steps == null) {
            steps = 0;
        }
    }
}
