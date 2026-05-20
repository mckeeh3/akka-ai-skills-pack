package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Browser-facing agent workstream API adapter for the starter User Admin vertical. */
public final class WorkstreamService {
  private static final String USER_ADMIN_AGENT_ID = "agent-user-admin";
  private static final String USER_ADMIN_CAPABILITY = "secure-tenant-user-foundation";
  private final MeService meService;
  private final AuthContextResolver authContextResolver;
  private final UserDirectoryView userDirectoryView;
  private final InvitationView invitationView;
  private final UserAdminService userAdminService;
  private final InvitationService invitationService;

  public WorkstreamService(
      MeService meService,
      AuthContextResolver authContextResolver,
      UserDirectoryView userDirectoryView,
      InvitationView invitationView,
      UserAdminService userAdminService,
      InvitationService invitationService) {
    this.meService = meService;
    this.authContextResolver = authContextResolver;
    this.userDirectoryView = userDirectoryView;
    this.invitationView = invitationView;
    this.userAdminService = userAdminService;
    this.invitationService = invitationService;
  }

  public WorkstreamBootstrapResponse bootstrap(WorkosIdentity identity, String selectedContextId, String correlationId) {
    var me = meService.me(identity, selectedContextId, correlationId);
    var actor = authContextResolver.resolveMe(identity, me.selectedAuthContext().selectedContextId(), correlationId);
    var surfaces = initialSurfaces(actor, correlationId);
    return new WorkstreamBootstrapResponse(me, me.functionalAgents(), initialItems(correlationId), surfaces);
  }

  public List<MeResponse.FunctionalAgentSummary> functionalAgents(WorkosIdentity identity, String selectedContextId, String correlationId) {
    return meService.me(identity, selectedContextId, correlationId).functionalAgents();
  }

  public List<WorkstreamItem> items(WorkosIdentity identity, String selectedContextId, String functionalAgentId, String correlationId) {
    authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    return initialItems(correlationId).stream()
        .filter(item -> functionalAgentId == null || functionalAgentId.isBlank() || functionalAgentId.equals(item.functionalAgentId()))
        .toList();
  }

  public SurfaceEnvelope surface(WorkosIdentity identity, String selectedContextId, String surfaceId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    return initialSurfaces(actor, correlationId).stream()
        .filter(surface -> surfaceId.equals(surface.surfaceId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
  }

  public CapabilityActionResult runAction(WorkosIdentity identity, String selectedContextId, CapabilityActionRequest request) {
    if (!Objects.equals(selectedContextId, request.selectedContextId())) {
      throw new AuthorizationException(403, "CONTEXT_FORBIDDEN");
    }
    var actor = authContextResolver.resolveMe(identity, selectedContextId, request.correlationId());
    var action = actionById(request.actionId());
    if (action == null || !Objects.equals(action.capabilityId(), request.capabilityId())) {
      throw new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN");
    }
    if (action.idempotency().required() && (request.idempotencyKey() == null || request.idempotencyKey().isBlank())) {
      return new CapabilityActionResult("validation-error", "This action requires a client-generated idempotency key.", request.correlationId(), List.of("trace-validation-idempotency"), null);
    }
    if (action.disabled() != null) {
      return new CapabilityActionResult("denied", action.disabled().message(), request.correlationId(), List.of("trace-user-admin-denial"), surfaceForAction(actor, request.actionId(), request.correlationId()));
    }
    if ("action-invite-user".equals(request.actionId())) {
      invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
          request.idempotencyKey(), actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId(),
          stringInput(request.input(), "email", "new-user@example.test"), stringInput(request.input(), "displayName", "New User"),
          List.of(FoundationRole.TENANT_EMPLOYEE), Instant.now().plus(7, ChronoUnit.DAYS), "workstream-invite", request.correlationId()));
    }
    return new CapabilityActionResult("accepted", action.label() + " accepted by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-" + request.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));
  }

  private List<SurfaceEnvelope> initialSurfaces(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return List.of(dashboardSurface(actor, correlationId), listSurface(actor, correlationId), detailSurface(actor, correlationId));
  }

  private List<WorkstreamItem> initialItems(String correlationId) {
    var now = Instant.now().toString();
    return List.of(
        new WorkstreamItem("item-user-admin-dashboard", USER_ADMIN_AGENT_ID, "surface", now, correlationId, List.of("trace-user-admin-dashboard"), "surface-user-admin-dashboard", "User Admin command center", "Tenant-scoped user administration dashboard loaded from /api/workstream/bootstrap.", "ready"),
        new WorkstreamItem("item-display-user-list", USER_ADMIN_AGENT_ID, "action-feedback", now, correlationId, List.of("trace-display-user-list"), "surface-user-admin-list", "Display the user list view", "Users, invitations, and memberships are available as a structured surface.", "ready"));
  }

  private SurfaceEnvelope dashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var users = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var invites = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    return envelope("surface-user-admin-dashboard", "dashboard", "User Admin command center", actor, correlationId,
        mapOf(
            "cards", List.of(
                mapOf("cardId", "card-pending-invitations", "label", "Pending invitations", "value", invites.size(), "severity", invites.isEmpty() ? "info" : "warning"),
                mapOf("cardId", "card-active-users", "label", "Active users", "value", users.size(), "severity", "info"),
                mapOf("cardId", "card-access-review", "label", "Access review items", "value", 0, "severity", "info"),
                mapOf("cardId", "card-support-access", "label", "Support access grants", "value", 0, "severity", "info")),
            "sections", List.of(
                mapOf("sectionId", "invitation-queue", "label", "Invitation queue", "summary", "No raw invitation tokens are exposed to the browser."),
                mapOf("sectionId", "access-review", "label", "Access review", "summary", "Role and last-admin decisions remain backend-authoritative."),
                mapOf("sectionId", "admin-audit", "label", "Admin audit excerpts", "summary", "Protected reads and User Admin actions return trace ids."))),
        List.of(displayListAction(), inviteAction(), traceAction()));
  }

  private SurfaceEnvelope listSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var rows = new ArrayList<Map<String, Object>>();
    for (var user : userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())) {
      rows.add(mapOf("id", user.accountId(), "rowType", "user-directory", "email", user.accountId(), "displayName", user.displayName(), "role", user.roles().toString(), "status", user.membershipStatus().name().toLowerCase(), "traceId", "trace-user-" + user.accountId()));
    }
    for (var invite : invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())) {
      rows.add(mapOf("id", invite.invitationId(), "rowType", "invitation-queue", "email", invite.targetEmail(), "displayName", invite.targetEmail(), "role", invite.requestedRoles().toString(), "status", invite.status().name().toLowerCase(), "delivery", invite.deliveryStatus().name().toLowerCase(), "traceId", "trace-invite-" + invite.invitationId()));
    }
    return envelope("surface-user-admin-list", "list-search", "Users, invitations, and memberships", actor, correlationId,
        mapOf("query", "scope:" + actor.selectedContext().scopeType().name().toLowerCase(), "rows", rows, "pageInfo", mapOf("totalKnownCount", rows.size()), "mobileFallback", "table-to-card"),
        List.of(displayDetailAction(), inviteAction(), deniedReplaceRoleAction(), traceAction()));
  }

  private SurfaceEnvelope detailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-user-admin-detail-admin", "detail-edit", "Tenant Admin account detail", actor, correlationId,
        mapOf(
            "recordId", actor.account().accountId(),
            "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(),
            "recordKind", "account",
            "summary", "Scoped detail/edit surface backed by UserAdminService authorization, idempotency, and audit semantics.",
            "fields", List.of(
                mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"),
                mapOf("fieldId", "email", "label", "Email", "value", actor.account().displayEmail(), "editable", false, "inputType", "email", "disabledReason", "Email changes require identity-provider reconciliation."),
                mapOf("fieldId", "role", "label", "Membership role", "value", actor.selectedContext().roles().toString(), "editable", false, "inputType", "select", "disabledReason", "Role changes are checked by backend policy.")),
            "version", 1,
            "permissionState", mapOf("canEdit", true, "reason", "Profile fields are editable; role changes use governed commands.", "authoritativeCapabilityId", USER_ADMIN_CAPABILITY),
            "audit", mapOf("lastEventType", "UserAdminDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-user-admin-detail"))),
        List.of(deniedReplaceRoleAction(), traceAction()));
  }

  private SurfaceEnvelope surfaceForAction(AuthContextResolver.ResolvedMe actor, String actionId, String correlationId) {
    if ("action-display-user-detail".equals(actionId) || "action-replace-membership-role".equals(actionId)) return detailSurface(actor, correlationId);
    if ("action-display-user-list".equals(actionId) || "action-invite-user".equals(actionId)) return listSurface(actor, correlationId);
    return dashboardSurface(actor, correlationId);
  }

  private SurfaceAction actionById(String actionId) {
    return List.of(displayListAction(), displayDetailAction(), inviteAction(), deniedReplaceRoleAction(), traceAction()).stream()
        .filter(action -> actionId.equals(action.actionId()))
        .findFirst()
        .orElse(null);
  }

  private SurfaceEnvelope envelope(String id, String type, String title, AuthContextResolver.ResolvedMe actor, String correlationId, Map<String, Object> data, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(id, type, "v1", title, USER_ADMIN_AGENT_ID, List.of("agent-audit-trace"),
        mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()),
        correlationId, List.of("trace-" + id), Instant.now().toString(), Map.of("profile", "tenant-admin"), data, actions,
        List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + id, "rel", "deep-link")));
  }

  private SurfaceAction displayListAction() { return new SurfaceAction("action-display-user-list", "Display user list view", "read", USER_ADMIN_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("UserAdminListDisplayed", true)); }
  private SurfaceAction displayDetailAction() { return new SurfaceAction("action-display-user-detail", "Display user account detail", "read", USER_ADMIN_CAPABILITY, "schema.user-admin.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("UserAdminDetailDisplayed", true)); }
  private SurfaceAction inviteAction() { return new SurfaceAction("action-invite-user", "Invite user", "command", USER_ADMIN_CAPABILITY, "schema.invitation.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("InvitationRequested", true)); }
  private SurfaceAction deniedReplaceRoleAction() { return new SurfaceAction("action-replace-membership-role", "Replace membership role", "command", USER_ADMIN_CAPABILITY, "schema.membership.role.replace.v1", true, false, new DisabledReason("LAST_ADMIN_DENIED", "Backend authorization denied this action: cannot remove the last tenant admin without an approved replacement."), new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("MembershipRoleReplacementDenied", true)); }
  private SurfaceAction traceAction() { return new SurfaceAction("action-open-trace", "Open trace", "trace", "audit.trace.read", null, false, false, null, new Idempotency(false, null), null, new Audit("TraceOpened", true)); }

  private static String stringInput(Object input, String key, String fallback) {
    if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return value;
    return fallback;
  }

  private static Map<String, Object> mapOf(Object... values) {
    var map = new LinkedHashMap<String, Object>();
    for (int i = 0; i + 1 < values.length; i += 2) map.put(String.valueOf(values[i]), values[i + 1]);
    return map;
  }

  public record WorkstreamBootstrapResponse(MeResponse me, List<MeResponse.FunctionalAgentSummary> functionalAgents, List<WorkstreamItem> items, List<SurfaceEnvelope> surfaces) {}
  public record WorkstreamItem(String itemId, String functionalAgentId, String kind, String createdAt, String correlationId, List<String> traceIds, String surfaceId, String title, String body, String status) {}
  public record SurfaceEnvelope(String surfaceId, String surfaceType, String surfaceVersion, String title, String ownerFunctionalAgentId, List<String> reusableByFunctionalAgentIds, Map<String, Object> authContext, String correlationId, List<String> traceIds, String generatedAt, Map<String, String> redaction, Map<String, Object> data, List<SurfaceAction> actions, List<Map<String, Object>> links) {}
  public record SurfaceAction(String actionId, String label, String intent, String capabilityId, String inputSchemaRef, boolean requiresConfirmation, boolean requiresApproval, DisabledReason disabled, Idempotency idempotency, ResultSurface resultSurface, Audit audit) {}
  public record DisabledReason(String reasonCode, String message) {}
  public record Idempotency(boolean required, String keySource) {}
  public record ResultSurface(String appendSurfaceType, String updateSurfaceId, String openPlacement) {}
  public record Audit(String eventType, boolean traceRequired) {}
  public record CapabilityActionRequest(String actionId, String capabilityId, Object input, String idempotencyKey, String selectedContextId, String surfaceId, String correlationId) {}
  public record CapabilityActionResult(String status, String message, String correlationId, List<String> traceIds, SurfaceEnvelope resultSurface) {}
}
