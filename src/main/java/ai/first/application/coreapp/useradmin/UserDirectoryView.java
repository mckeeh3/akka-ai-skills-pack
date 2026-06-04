package ai.first.application.coreapp.useradmin;

import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import java.util.Comparator;
import java.util.List;
import ai.first.application.foundation.identity.AuthContextResolver;

/** Browser-safe User Admin directory read model seam backed by scoped backend authorization. */
public final class UserDirectoryView {
  private final UserAdminService userAdminService;

  public UserDirectoryView(UserAdminService userAdminService) {
    this.userAdminService = userAdminService;
  }

  public List<UserDirectoryRow> list(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    return userAdminService.listUsers(actor, scopeType, tenantId, customerId).stream()
        .sorted(Comparator.comparing(ai.first.application.coreapp.useradmin.UserAdminService.UserDirectoryRow::displayName).thenComparing(ai.first.application.coreapp.useradmin.UserAdminService.UserDirectoryRow::accountId))
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
