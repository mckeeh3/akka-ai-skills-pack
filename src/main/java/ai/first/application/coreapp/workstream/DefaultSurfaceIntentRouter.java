package ai.first.application.coreapp.workstream;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/** Default deterministic surface intent router for high-confidence, side-effect-free surface opens. */
final class DefaultSurfaceIntentRouter implements SurfaceIntentRouter {
  private static final String MY_ACCOUNT_AGENT_ID = "my-account-agent";
  private static final String USER_ADMIN_AGENT_ID = "user-admin-agent";
  private static final String SAAS_OWNER_TENANT_MANAGE_CAPABILITY = "saas_owner.tenant.manage";
  private static final String SAAS_OWNER_TENANT_READ_CAPABILITY = "saas_owner.tenant.read";
  private static final String USERADMIN_VIEW_OVERVIEW = "user_admin.view_overview";
  private static final String USERADMIN_LIST_MEMBERS = "user_admin.list_members";
  private static final String USERADMIN_SEND_INVITATION = "user_admin.invite_user";
  private static final Pattern CREATE_ORGANIZATION = Pattern.compile("^(?:please\\s+)?(?:create|add)\\s+(?:an?\\s+)?organization\\s+\\\"([^\\\"]{2,120})\\\"\\s*$", Pattern.CASE_INSENSITIVE);
  private static final Pattern INVITE_USER = Pattern.compile("^(?:please\\s+)?invite\\s+user\\s+([A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63})\\s*$", Pattern.CASE_INSENSITIVE);

  static SurfaceIntentRouter create() {
    return new DefaultSurfaceIntentRouter();
  }

  @Override
  public Optional<Result> route(Request request) {
    var normalized = normalize(request.prompt());
    if (MY_ACCOUNT_AGENT_ID.equals(request.functionalAgentId()) && isMyAccountDashboardOpen(normalized)) {
      return Optional.of(routeResult(
          request,
          "surface-my-account-dashboard",
          "open my account dashboard",
          Map.of(),
          "route-my-account-dashboard-open-v1",
          "surface_open",
          null));
    }
    if (USER_ADMIN_AGENT_ID.equals(request.functionalAgentId()) && !isAmbiguousUserAdminPrompt(normalized)) {
      var userAdminRoute = routeUserAdmin(request, normalized);
      if (userAdminRoute.isPresent()) return userAdminRoute;
    }
    return Optional.empty();
  }

  private Optional<Result> routeUserAdmin(Request request, String normalized) {
    var createOrganization = CREATE_ORGANIZATION.matcher(request.prompt().trim());
    if (createOrganization.matches() && hasCapability(request, SAAS_OWNER_TENANT_MANAGE_CAPABILITY)) {
      var organizationName = createOrganization.group(1).trim();
      return Optional.of(routeResult(
          request,
          "surface-user-admin-organization-create",
          "create organization",
          Map.of("organizationName", organizationName),
          "route-user-admin-organization-create-v1",
          "surface_create_prefill",
          SAAS_OWNER_TENANT_MANAGE_CAPABILITY));
    }
    if (isOrganizationDirectoryOpen(normalized) && hasCapability(request, SAAS_OWNER_TENANT_READ_CAPABILITY)) {
      return Optional.of(routeResult(
          request,
          "surface-user-admin-organization-directory",
          "show organizations",
          Map.of(),
          "route-user-admin-organization-directory-v1",
          "surface_open",
          SAAS_OWNER_TENANT_READ_CAPABILITY));
    }
    if (isUserDirectoryOpen(normalized) && hasUserAdminSurfaceCapability(request)) {
      return Optional.of(routeResult(
          request,
          "surface-user-admin-users",
          "show users",
          Map.of(),
          "route-user-admin-user-directory-v1",
          "surface_open",
          USERADMIN_LIST_MEMBERS));
    }
    var inviteUser = INVITE_USER.matcher(request.prompt().trim());
    if (inviteUser.matches() && hasUserAdminSurfaceCapability(request)) {
      return Optional.of(routeResult(
          request,
          "surface-user-admin-invitation-create",
          "invite user",
          Map.of("email", inviteUser.group(1).toLowerCase(Locale.ROOT)),
          "route-user-admin-invitation-create-v1",
          "surface_create_prefill",
          USERADMIN_SEND_INVITATION));
    }
    return Optional.empty();
  }

  private Result routeResult(Request request, String targetSurfaceId, String canonicalPrompt, Map<String, Object> prefill, String routeId, String category, String requiredCapability) {
    var traceId = "trace-surface-intent-" + routeId.replaceFirst("^route-", "") + "-" + stableSuffix(request.correlationId());
    var metadata = requiredCapability == null
        ? Map.<String, Object>of(
            "routerContract", "surface_intent_route.v1",
            "routeId", routeId,
            "sideEffect", "none",
            "selectedContextId", request.selectedAuthContext().membershipId())
        : Map.<String, Object>of(
            "routerContract", "surface_intent_route.v1",
            "routeId", routeId,
            "sideEffect", "none",
            "requiredCapability", requiredCapability,
            "selectedContextId", request.selectedAuthContext().membershipId());
    return new Result(
        request.functionalAgentId(),
        targetSurfaceId,
        request.prompt(),
        canonicalPrompt,
        prefill,
        "high",
        category,
        true,
        List.of(traceId),
        metadata);
  }

  private static boolean hasCapability(Request request, String capability) {
    return request.selectedAuthContext().hasCapability(capability);
  }

  private static boolean hasUserAdminSurfaceCapability(Request request) {
    return hasCapability(request, USERADMIN_VIEW_OVERVIEW)
        || hasCapability(request, "saas_owner.admin.manage")
        || hasCapability(request, "tenant.user.manage")
        || hasCapability(request, "tenant.user.read")
        || hasCapability(request, "customer.user.manage")
        || hasCapability(request, "customer.user.read");
  }

  private static boolean isMyAccountDashboardOpen(String normalized) {
    return List.of(
        "open my account",
        "open my account dashboard",
        "show my account",
        "show my account dashboard",
        "my account dashboard").contains(normalized);
  }

  private static boolean isOrganizationDirectoryOpen(String normalized) {
    return List.of(
        "open organizations",
        "open organization directory",
        "show organizations",
        "show organization directory",
        "list organizations",
        "organization directory").contains(normalized);
  }

  private static boolean isUserDirectoryOpen(String normalized) {
    return List.of(
        "open users",
        "open user directory",
        "show users",
        "show user directory",
        "list users",
        "user directory").contains(normalized);
  }

  private static boolean isAmbiguousUserAdminPrompt(String normalized) {
    return normalized.contains(" and ") || normalized.contains(" then ") || normalized.contains(",");
  }

  private static String normalize(String prompt) {
    return prompt == null ? "" : prompt.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString((value == null ? "surface-intent-route" : value).hashCode(), 36);
  }
}
