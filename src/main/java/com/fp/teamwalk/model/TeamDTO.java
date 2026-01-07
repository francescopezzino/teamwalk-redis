package com.fp.teamwalk.model;


import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * TeamDTO Record for TeamWalk.
 * Optimized for local (Caffeine) and distributed (Redis) caching.
 */
public record TeamDTO(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        String name,

        List<EmployeeDTO> employees
) implements Serializable {

    // Unique version ID for Redis serialization compatibility
    private static final long serialVersionUID = 20260107L;
}

