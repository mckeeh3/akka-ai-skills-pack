package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import java.util.List;

/** Browser-safe User Admin directory read model seam backed by scoped backend authorization. */
public final class UserDirectoryView {
  private final UserAdminService userAdminService;

  public UserDirectoryView(UserAdminService userAdminService) {
    this.userAdminService = userAdminService;
  }

  public List<UserDirectoryRow> list(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    return userAdminService.listUsers(actor, scopeType, tenantId, customerId).stream()
        .map(row -> new UserDirectoryRow(row.accountId(), row.displayName(), row.membershipId(), row.roles(), row.status(), row.scopeType(), row.tenantId(), row.customerId()))
        .toList();
  }

  public record UserDirectoryRow(
      String accountId,
      String displayName,
      String membershipId,
      List<FoundationRole> roles,
      MembershipStatus membershipStatus,
      ScopeType scopeType,
      String tenantId,
      String customerId) {}
}
