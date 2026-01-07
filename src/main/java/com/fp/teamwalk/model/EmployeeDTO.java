package com.fp.teamwalk.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * Using Java Records for DTOs is a 2026 best practice for
 * thread-safe, immutable cache entries.
 * DTO for Employee data.
 * Optimized for L1 (Caffeine) and L2 (Redis) caching in 2026.
 */
public record EmployeeDTO(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,
        String firstName,
        String lastName
) implements Serializable {
    // Unique ID for serialization versioning in 2026
    // Explicit serialVersionUID is a best practice for Redis compatibility
    private static final long serialVersionUID = 1L;
}
