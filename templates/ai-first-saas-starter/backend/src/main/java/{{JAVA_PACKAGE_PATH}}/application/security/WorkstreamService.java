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
  private static final String ACCESS_PROFILE_AGENT_ID = "agent-access-profile";
  private static final String USER_ADMIN_AGENT_ID = "agent-user-admin";
  private static final String AUDIT_TRACE_AGENT_ID = "agent-audit-trace";
  private static final String GOVERNANCE_POLICY_AGENT_ID = "agent-governance-policy";
  private static final String AGENT_ADMIN_AGENT_ID = "agent-agent-admin";
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
    return new WorkstreamBootstrapResponse(me, me.functionalAgents(), initialItems(actor, correlationId), surfaces);
  }

  public List<MeResponse.FunctionalAgentSummary> functionalAgents(WorkosIdentity identity, String selectedContextId, String correlationId) {
    return meService.me(identity, selectedContextId, correlationId).functionalAgents();
  }

  public List<WorkstreamItem> items(WorkosIdentity identity, String selectedContextId, String functionalAgentId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    return initialItems(actor, correlationId).stream()
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

  public List<WorkstreamEvent> events(WorkosIdentity identity, String selectedContextId, String functionalAgentId, String lastEventId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    var events = initialEvents(actor, correlationId).stream()
        .filter(event -> functionalAgentId == null || functionalAgentId.isBlank() || functionalAgentId.equals(event.functionalAgentId()))
        .toList();
    if (lastEventId == null || lastEventId.isBlank()) return events;
    var lastIndex = -1;
    for (var index = 0; index < events.size(); index++) {
      if (lastEventId.equals(events.get(index).eventId())) lastIndex = index;
    }
    if (lastIndex >= 0 && lastIndex + 1 < events.size()) return events.subList(lastIndex + 1, events.size());
    return List.of(new WorkstreamEvent("evt-stale-replay-unavailable-999", "surface.stale", actor.selectedContext().tenantId(), actor.selectedContext().customerId(), USER_ADMIN_AGENT_ID, "surface-user-admin-dashboard", "dashboard", "v1", correlationId, List.of("trace-sse-replay-unavailable"), Instant.now().toString(), 999, mapOf("reason", "Replay from Last-Event-ID is unavailable; refresh the affected starter surfaces.")));
  }

  private List<SurfaceEnvelope> initialSurfaces(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return List.of(
        accessProfileSurface(actor, correlationId),
        dashboardSurface(actor, correlationId),
        listSurface(actor, correlationId),
        detailSurface(actor, correlationId),
        auditTimelineSurface(actor, correlationId),
        governancePolicySurface(actor, correlationId),
        agentAdminPlaceholderSurface(actor, correlationId));
  }

  private List<WorkstreamItem> initialItems(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var now = Instant.now().toString();
    return List.of(
        new WorkstreamItem("item-access-profile", ACCESS_PROFILE_AGENT_ID, "surface", now, correlationId, List.of("trace-access-profile"), "surface-access-profile", "Access/Profile context", "Signed-in account, profile preferences, support-access state, and selected AuthContext loaded from backend /api/workstream/bootstrap.", "ready"),
        new WorkstreamItem("item-user-admin-dashboard", USER_ADMIN_AGENT_ID, "surface", now, correlationId, List.of("trace-user-admin-dashboard"), "surface-user-admin-dashboard", "User Admin command center", "Tenant-scoped user administration dashboard loaded from /api/workstream/bootstrap.", "ready"),
        new WorkstreamItem("item-display-user-list", USER_ADMIN_AGENT_ID, "action-feedback", now, correlationId, List.of("trace-display-user-list"), "surface-user-admin-list", "Display the user list view", "Users, invitations, and memberships are available as a structured surface.", "ready"),
        new WorkstreamItem("item-audit-trace", AUDIT_TRACE_AGENT_ID, "audit-trace", now, correlationId, List.of("trace-audit-timeline"), "surface-audit-timeline", "Audit and trace timeline", "Protected reads and starter denials are visible as browser-safe audit excerpts.", "ready"),
        new WorkstreamItem("item-governance-policy", GOVERNANCE_POLICY_AGENT_ID, "decision", now, correlationId, List.of("trace-governance-policy"), "surface-governance-policy", "Governance policy guardrails", "Starter policy thresholds are read-only until the governance backend sprint adds proposal and approval capabilities.", actor.selectedContext().capabilities().contains("governance.policy.read") ? "ready" : "blocked"),
        new WorkstreamItem("item-agent-admin-placeholder", AGENT_ADMIN_AGENT_ID, "system-status", now, correlationId, List.of("trace-agent-admin-placeholder"), "surface-agent-admin-placeholder", "Agent Admin placeholder", "Agent governance records, prompt assembly, readSkill, and tool boundaries are added by the next starter sprint.", "stale"));
  }

  private SurfaceEnvelope accessProfileSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-access-profile", "detail-edit", "Access and profile", actor, correlationId,
        mapOf(
            "recordId", actor.account().accountId(),
            "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(),
            "recordKind", "account",
            "summary", "Browser-safe self-service context from backend AuthContext resolution. Raw JWTs, WorkOS secrets, and backend permission internals are never exposed.",
            "fields", List.of(
                mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"),
                mapOf("fieldId", "email", "label", "Email", "value", actor.account().displayEmail(), "editable", false, "inputType", "email", "disabledReason", "Email is owned by WorkOS/AuthKit identity reconciliation."),
                mapOf("fieldId", "selectedContext", "label", "Selected AuthContext", "value", actor.selectedContext().membershipId(), "editable", false, "inputType", "text", "disabledReason", "Context switching must be resolved by backend authorization."),
                mapOf("fieldId", "supportAccess", "label", "Support access", "value", selectedMembership(actor).supportAccess() ? "active" : "inactive", "editable", false, "inputType", "text", "disabledReason", "Support access grants are managed through audited admin capabilities.")),
            "version", 1,
            "permissionState", mapOf("canEdit", true, "reason", "Profile preferences may be edited when the profile backend capability is present; context authority remains backend-controlled.", "authoritativeCapabilityId", "profile.read"),
            "audit", mapOf("lastEventType", "AccessProfileDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-access-profile"))),
        List.of(openAuditAction()));
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

  private SurfaceEnvelope auditTimelineSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var now = Instant.now().toString();
    return envelope("surface-audit-timeline", "audit-timeline", "Audit and trace timeline", actor, correlationId,
        mapOf("events", List.of(
            mapOf("eventId", "audit-me-read", "occurredAt", now, "actor", actor.profile().displayName(), "action", "Loaded /api/me and selected AuthContext", "traceId", "trace-access-profile"),
            mapOf("eventId", "audit-workstream-bootstrap", "occurredAt", now, "actor", "WorkstreamService", "action", "Composed role-authorized starter surfaces", "traceId", "trace-workstream-bootstrap"),
            mapOf("eventId", "audit-user-admin-denial", "occurredAt", now, "actor", "UserAdminService", "action", "Preserved LAST_ADMIN_DENIED as a browser-safe denial", "traceId", "trace-user-admin-denial"))),
        List.of(openAuditAction()));
  }

  private SurfaceEnvelope governancePolicySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-governance-policy", "governance-diff", "Governance policy guardrails", actor, correlationId,
        mapOf(
            "proposalId", "starter-policy-readonly-baseline",
            "beforeSummary", "Starter foundation permits profile, user-admin, audit, and placeholder governance reads according to assigned capabilities.",
            "afterSummary", "Future governance sprint adds policy proposals, simulations, approval routing, and activation audit without changing the browser surface contract.",
            "changes", List.of(
                mapOf("path", "policy.invitation.idempotency", "before", "required", "after", "required", "impact", "Invitation actions remain idempotent and audited."),
                mapOf("path", "policy.role-change.last-admin", "before", "deny", "after", "deny-or-approval-required", "impact", "Last-admin protection remains backend-authoritative."),
                mapOf("path", "policy.agent-governance", "before", "placeholder-disabled", "after", "proposal-review", "impact", "No agent authority expansion is available until governed records are installed."))),
        List.of(simulatePolicyAction(), disabledCommitPolicyAction(), openAuditAction()));
  }

  private SurfaceEnvelope agentAdminPlaceholderSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var surface = envelope("surface-agent-admin-placeholder", "governance-diff", "Agent Admin placeholder", actor, correlationId,
        mapOf(
            "proposalId", "agent-governance-not-installed",
            "beforeSummary", "Tenant-governed starter AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, and ToolPermissionBoundary records are seeded by the backend; Agent Admin UI actions remain disabled until the next governance slices expose editing and runtime flows.",
            "afterSummary", "The next starter sprint adds prompt assembly, readSkill authorization, behavior editing proposals, and then real Agent Admin UI/API wiring.",
            "changes", List.of(
                mapOf("path", "AgentDefinition", "before", "seeded-active-default", "after", "admin-editable-governed-records", "impact", "Agent Admin remains read-only/disabled until editing endpoints exist."),
                mapOf("path", "readSkill(skillId)", "before", "unavailable", "after", "authorized capability", "impact", "Skill access must be backend-authorized and traced."))),
        List.of(disabledAgentAdminAction(), openAuditAction()));
    return new SurfaceEnvelope(surface.surfaceId(), surface.surfaceType(), surface.surfaceVersion(), surface.title(), surface.ownerFunctionalAgentId(), surface.reusableByFunctionalAgentIds(), surface.authContext(), surface.correlationId(), surface.traceIds(), surface.generatedAt(), mapOf("isStale", true, "reason", "Seeded backend records exist; Agent Admin editing/runtime APIs are installed by later tasks.", "lastKnownEventId", "evt-agent-admin-placeholder"), surface.redaction(), surface.data(), surface.actions(), surface.links());
  }

  private List<WorkstreamEvent> initialEvents(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var tenantId = actor.selectedContext().tenantId();
    var customerId = actor.selectedContext().customerId();
    var now = Instant.now().toString();
    return List.of(
        new WorkstreamEvent("evt-workstream-reconnected-001", "surface.reconnected", tenantId, customerId, ACCESS_PROFILE_AGENT_ID, "surface-access-profile", "detail-edit", "v1", correlationId, List.of("trace-sse-reconnected"), now, 1, mapOf("message", "Realtime stream connected for selected AuthContext.")),
        new WorkstreamEvent("evt-audit-appended-002", "workstream.item.appended", tenantId, customerId, AUDIT_TRACE_AGENT_ID, "surface-audit-timeline", "audit-timeline", "v1", correlationId, List.of("trace-sse-audit"), now, 2, mapOf("itemId", "item-audit-realtime", "kind", "audit-trace", "title", "Realtime trace available", "body", "SSE delivered a browser-safe audit trace update.", "surfaceId", "surface-audit-timeline", "status", "ready")),
        new WorkstreamEvent("evt-user-admin-stale-003", "surface.stale", tenantId, customerId, USER_ADMIN_AGENT_ID, "surface-user-admin-dashboard", "dashboard", "v1", correlationId, List.of("trace-sse-stale"), now, 3, mapOf("reason", "User Admin dashboard should refresh after invitation/user changes.")),
        new WorkstreamEvent("evt-surface-reconnected-004", "surface.reconnected", tenantId, customerId, USER_ADMIN_AGENT_ID, "surface-user-admin-dashboard", "dashboard", "v1", correlationId, List.of("trace-sse-reconnected-user-admin"), now, 4, mapOf("message", "Dashboard refreshed after reconnect.")));
  }

  private SurfaceEnvelope surfaceForAction(AuthContextResolver.ResolvedMe actor, String actionId, String correlationId) {
    if ("action-open-audit-trace".equals(actionId) || "action-open-trace".equals(actionId)) return auditTimelineSurface(actor, correlationId);
    if ("action-simulate-policy".equals(actionId) || "action-commit-policy".equals(actionId)) return governancePolicySurface(actor, correlationId);
    if ("action-open-agent-admin".equals(actionId)) return agentAdminPlaceholderSurface(actor, correlationId);
    if ("action-display-user-detail".equals(actionId) || "action-replace-membership-role".equals(actionId)) return detailSurface(actor, correlationId);
    if ("action-display-user-list".equals(actionId) || "action-invite-user".equals(actionId)) return listSurface(actor, correlationId);
    return dashboardSurface(actor, correlationId);
  }

  private SurfaceAction actionById(String actionId) {
    return List.of(displayListAction(), displayDetailAction(), inviteAction(), deniedReplaceRoleAction(), traceAction(), openAuditAction(), simulatePolicyAction(), disabledCommitPolicyAction(), disabledAgentAdminAction()).stream()
        .filter(action -> actionId.equals(action.actionId()))
        .findFirst()
        .orElse(null);
  }

  private SurfaceEnvelope envelope(String id, String type, String title, AuthContextResolver.ResolvedMe actor, String correlationId, Map<String, Object> data, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(id, type, "v1", title, ownerForSurface(id), reusableAgentsForSurface(id),
        mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()),
        correlationId, List.of("trace-" + id), Instant.now().toString(), null, mapOf("profile", "tenant-admin"), data, actions,
        List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + id, "rel", "deep-link")));
  }

  private {{JAVA_BASE_PACKAGE}}.domain.security.Membership selectedMembership(AuthContextResolver.ResolvedMe actor) {
    return actor.memberships().stream()
        .filter(membership -> membership.membershipId().equals(actor.selectedContext().membershipId()))
        .findFirst()
        .orElseThrow();
  }

  private String ownerForSurface(String surfaceId) {
    if (surfaceId.startsWith("surface-access-profile")) return ACCESS_PROFILE_AGENT_ID;
    if (surfaceId.startsWith("surface-audit")) return AUDIT_TRACE_AGENT_ID;
    if (surfaceId.startsWith("surface-governance")) return GOVERNANCE_POLICY_AGENT_ID;
    if (surfaceId.startsWith("surface-agent-admin")) return AGENT_ADMIN_AGENT_ID;
    return USER_ADMIN_AGENT_ID;
  }

  private List<String> reusableAgentsForSurface(String surfaceId) {
    if (surfaceId.startsWith("surface-audit")) return List.of(USER_ADMIN_AGENT_ID, GOVERNANCE_POLICY_AGENT_ID, AGENT_ADMIN_AGENT_ID);
    if (surfaceId.startsWith("surface-access-profile")) return List.of(USER_ADMIN_AGENT_ID);
    return List.of(AUDIT_TRACE_AGENT_ID);
  }

  private SurfaceAction displayListAction() { return new SurfaceAction("action-display-user-list", "Display user list view", "read", USER_ADMIN_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("UserAdminListDisplayed", true)); }
  private SurfaceAction displayDetailAction() { return new SurfaceAction("action-display-user-detail", "Display user account detail", "read", USER_ADMIN_CAPABILITY, "schema.user-admin.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("UserAdminDetailDisplayed", true)); }
  private SurfaceAction inviteAction() { return new SurfaceAction("action-invite-user", "Invite user", "command", USER_ADMIN_CAPABILITY, "schema.invitation.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("InvitationRequested", true)); }
  private SurfaceAction deniedReplaceRoleAction() { return new SurfaceAction("action-replace-membership-role", "Replace membership role", "command", USER_ADMIN_CAPABILITY, "schema.membership.role.replace.v1", true, false, new DisabledReason("LAST_ADMIN_DENIED", "Backend authorization denied this action: cannot remove the last tenant admin without an approved replacement."), new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("MembershipRoleReplacementDenied", true)); }
  private SurfaceAction traceAction() { return new SurfaceAction("action-open-trace", "Open trace", "trace", "audit.trace.read", null, false, false, null, new Idempotency(false, null), null, new Audit("TraceOpened", true)); }
  private SurfaceAction openAuditAction() { return new SurfaceAction("action-open-audit-trace", "Open audit timeline", "trace", "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-timeline", "inline"), new Audit("AuditTimelineOpened", true)); }
  private SurfaceAction simulatePolicyAction() { return new SurfaceAction("action-simulate-policy", "Simulate policy", "governance", "governance.policy.simulate", "schema.policy.simulate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy", "inline"), new Audit("PolicySimulationDisplayed", true)); }
  private SurfaceAction disabledCommitPolicyAction() { return new SurfaceAction("action-commit-policy", "Commit policy change", "approval", "governance.policy.commit", "schema.policy.commit.v1", true, true, new DisabledReason("APPROVAL_REQUIRED", "Policy commits require the governance approval backend added in a later starter sprint."), new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy", "inline"), new Audit("PolicyCommitDenied", true)); }
  private SurfaceAction disabledAgentAdminAction() { return new SurfaceAction("action-open-agent-admin", "Open Agent Admin", "governance", "agent.definitions.manage", null, false, false, new DisabledReason("AGENT_ARTIFACT_INACTIVE", "Agent governance backend records are not installed yet; this surface is intentionally stale/disabled."), new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-placeholder", "inline"), new Audit("AgentAdminPlaceholderOpened", true)); }

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
  public record SurfaceEnvelope(String surfaceId, String surfaceType, String surfaceVersion, String title, String ownerFunctionalAgentId, List<String> reusableByFunctionalAgentIds, Map<String, Object> authContext, String correlationId, List<String> traceIds, String generatedAt, Map<String, Object> stale, Map<String, Object> redaction, Map<String, Object> data, List<SurfaceAction> actions, List<Map<String, Object>> links) {}
  public record SurfaceAction(String actionId, String label, String intent, String capabilityId, String inputSchemaRef, boolean requiresConfirmation, boolean requiresApproval, DisabledReason disabled, Idempotency idempotency, ResultSurface resultSurface, Audit audit) {}
  public record DisabledReason(String reasonCode, String message) {}
  public record Idempotency(boolean required, String keySource) {}
  public record ResultSurface(String appendSurfaceType, String updateSurfaceId, String openPlacement) {}
  public record Audit(String eventType, boolean traceRequired) {}
  public record CapabilityActionRequest(String actionId, String capabilityId, Object input, String idempotencyKey, String selectedContextId, String surfaceId, String correlationId) {}
  public record CapabilityActionResult(String status, String message, String correlationId, List<String> traceIds, SurfaceEnvelope resultSurface) {}
  public record WorkstreamEvent(String eventId, String eventType, String tenantId, String customerId, String functionalAgentId, String surfaceId, String surfaceType, String surfaceVersion, String correlationId, List<String> traceIds, String occurredAt, Integer sequence, Map<String, Object> patch) {}
}
