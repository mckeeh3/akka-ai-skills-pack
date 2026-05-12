package com.example.domain;

import java.time.Instant;
import java.util.List;

public record UserAccount(
  String userId,
  String workosUserId,
  String email,
  AccountStatus status,
  UserProfile profile,
  List<RoleAssignment> roles,
  Instant createdAt,
  Instant updatedAt,
  Instant lastLoginAt
) {
  public static UserAccount empty(String userId) {
    var now = Instant.now();
    return new UserAccount(userId, null, "", AccountStatus.INVITED, UserProfile.empty(), List.of(), now, now, null);
  }

  public boolean exists() {
    return email != null && !email.isBlank() && status != AccountStatus.DELETED;
  }

  public boolean isActive() {
    return status == AccountStatus.ACTIVE;
  }

  public boolean hasRole(Role role) {
    return roles != null && roles.stream().anyMatch(assignment -> assignment.role() == role);
  }

  public boolean isAppAdmin() {
    return hasRole(Role.APP_ADMIN);
  }

  public boolean canAdminTenant(String tenantId) {
    return roles != null && roles.stream().anyMatch(assignment ->
      assignment.role() == Role.APP_ADMIN ||
        (assignment.role() == Role.TENANT_ADMIN && tenantId != null && tenantId.equals(assignment.tenantId())));
  }

  public boolean canAdminCustomer(String tenantId, String customerId) {
    return roles != null && roles.stream().anyMatch(assignment ->
      assignment.role() == Role.APP_ADMIN ||
        (assignment.role() == Role.TENANT_ADMIN && tenantId != null && tenantId.equals(assignment.tenantId())) ||
        (assignment.role() == Role.CUSTOMER_ADMIN &&
          tenantId != null && tenantId.equals(assignment.tenantId()) &&
          customerId != null && customerId.equals(assignment.customerId())));
  }

  public UserAccount invited(String email, UserProfile profile, List<RoleAssignment> roles) {
    var now = Instant.now();
    return new UserAccount(userId, workosUserId, email, AccountStatus.INVITED, profile, roles, now, now, null);
  }

  public UserAccount activate(String workosUserId) {
    return new UserAccount(userId, workosUserId, email, AccountStatus.ACTIVE, profile, roles, createdAt, Instant.now(), Instant.now());
  }

  public UserAccount withProfile(UserProfile profile) {
    return new UserAccount(userId, workosUserId, email, status, profile, roles, createdAt, Instant.now(), lastLoginAt);
  }

  public UserAccount withRoles(List<RoleAssignment> roles) {
    return new UserAccount(userId, workosUserId, email, status, profile, roles, createdAt, Instant.now(), lastLoginAt);
  }

  public UserAccount disabled() {
    return new UserAccount(userId, workosUserId, email, AccountStatus.DISABLED, profile, roles, createdAt, Instant.now(), lastLoginAt);
  }
}
