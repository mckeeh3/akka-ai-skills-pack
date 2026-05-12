package com.example.domain.security;

import java.time.Instant;
import java.util.List;

/** Parsed backend-only startup admin bootstrap input. */
public record BootstrapAdmin(
    String userId,
    String email,
    UserProfile profile,
    List<RoleAssignment> roles,
    Instant configuredAt) {}
