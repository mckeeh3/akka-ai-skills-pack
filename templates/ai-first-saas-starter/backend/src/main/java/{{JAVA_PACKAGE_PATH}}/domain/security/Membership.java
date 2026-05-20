package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.List;

/** Local authorization grant for one account in one SaaS Owner, Tenant, or Customer scope. */
public record Membership(
    String membershipId,
    String accountId,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    List<FoundationRole> roles,
    MembershipStatus status,
    boolean supportAccess,
    Instant expiresAt) {

  public Membership {
    roles = List.copyOf(roles == null ? List.of() : roles);
    if (scopeType == ScopeType.SAAS_OWNER && (tenantId != null || customerId != null)) {
      throw new IllegalArgumentException("SaaS Owner memberships must not carry tenant/customer ids");
    }
    if (scopeType == ScopeType.TENANT && tenantId == null) {
      throw new IllegalArgumentException("Tenant memberships require tenantId");
    }
    if (scopeType == ScopeType.CUSTOMER && (tenantId == null || customerId == null)) {
      throw new IllegalArgumentException("Customer memberships require tenantId and customerId");
    }
  }

  public boolean active() {
    return status == MembershipStatus.ACTIVE;
  }

  public List<String> capabilities() {
    if (!active()) {
      return List.of();
    }
    return roles.stream().flatMap(role -> role.capabilities().stream()).distinct().sorted().toList();
  }
}
