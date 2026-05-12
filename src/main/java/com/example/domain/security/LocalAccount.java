package com.example.domain.security;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/** Local account state that backs server-side authorization for authenticated browser users. */
public final class LocalAccount {

  private LocalAccount() {}

  public record State(
      String userId,
      String workosUserId,
      String email,
      AccountStatus status,
      UserProfile profile,
      List<RoleAssignment> roles,
      Instant createdAt,
      Instant updatedAt,
      Instant lastLoginAt) {

    public static State empty(String userId) {
      return new State(userId, null, "", AccountStatus.DELETED, UserProfile.empty(), List.of(), null, null, null);
    }

    public boolean exists() {
      return email != null && !email.isBlank() && status != AccountStatus.DELETED;
    }

    public boolean isActive() {
      return status == AccountStatus.ACTIVE;
    }

    public boolean isAppAdmin() {
      return roles.stream().anyMatch(RoleAssignment::isAppAdmin);
    }

    public boolean canAccessTenant(String tenantId) {
      return isActive() && roles.stream().anyMatch(role -> role.grantsTenant(tenantId));
    }

    public boolean canAccessCustomer(String tenantId, String customerId) {
      return isActive() && roles.stream().anyMatch(role -> role.grantsCustomer(tenantId, customerId));
    }

    public State invited(String email, UserProfile profile, List<RoleAssignment> roles, Instant at) {
      return new State(userId, workosUserId, normalizeEmail(email), AccountStatus.INVITED, profile, sortRoles(roles), at, at, null);
    }

    public State activate(String newWorkosUserId, Instant at) {
      return new State(userId, newWorkosUserId, email, AccountStatus.ACTIVE, profile, roles, createdAt, at, at);
    }

    public State withRoles(List<RoleAssignment> newRoles, Instant at) {
      return new State(userId, workosUserId, email, status, profile, sortRoles(newRoles), createdAt, at, lastLoginAt);
    }

    public State disabled(Instant at) {
      return new State(userId, workosUserId, email, AccountStatus.DISABLED, profile, roles, createdAt, at, lastLoginAt);
    }

    public State reactivated(Instant at) {
      return new State(userId, workosUserId, email, AccountStatus.ACTIVE, profile, roles, createdAt, at, lastLoginAt);
    }
  }

  public sealed interface Command {
    record Invite(String email, UserProfile profile, List<RoleAssignment> roles, Instant at) implements Command {}

    record LinkAndActivate(String workosUserId, Instant at) implements Command {}

    record ReplaceRoles(List<RoleAssignment> roles, Instant at) implements Command {}

    record Disable(Instant at) implements Command {}

    record Reactivate(Instant at) implements Command {}
  }

  static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }

  static List<RoleAssignment> sortRoles(List<RoleAssignment> roles) {
    if (roles == null) {
      return List.of();
    }
    return roles.stream()
        .sorted(
            Comparator.comparing((RoleAssignment role) -> role.role().name())
                .thenComparing(role -> role.tenantId() == null ? "" : role.tenantId())
                .thenComparing(role -> role.customerId() == null ? "" : role.customerId()))
        .toList();
  }
}
