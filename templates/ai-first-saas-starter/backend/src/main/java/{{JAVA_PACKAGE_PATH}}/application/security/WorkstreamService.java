package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.BehaviorChangeProposal;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Browser-facing agent workstream API adapter for foundation, Agent Admin, and Governance/Policy surfaces. */
public final class WorkstreamService {
  private static final String MY_ACCOUNT_AGENT_ID = "agent-my-account";
  private static final String USER_ADMIN_AGENT_ID = "agent-user-admin";
  private static final String AUDIT_TRACE_AGENT_ID = "agent-audit-trace";
  private static final String GOVERNANCE_POLICY_AGENT_ID = "agent-governance-policy";
  private static final String AGENT_ADMIN_AGENT_ID = "agent-agent-admin";
  private static final String USER_ADMIN_CAPABILITY = "secure-tenant-user-foundation";
  private static final String AGENT_DEFINITIONS_CAPABILITY = "agent.definitions.manage";
  private static final String AGENT_PROMPTS_CAPABILITY = "agent.prompts.govern";
  private static final String AGENT_SKILLS_CAPABILITY = "agent.skills.govern";
  private static final String AGENT_TOOL_BOUNDARIES_CAPABILITY = "agent.tool_boundaries.manage";
  private static final String AGENT_MODELS_READ_CAPABILITY = "agent.models.read";
  private static final String AGENT_MODELS_MANAGE_CAPABILITY = "agent.models.manage";
  private static final String AGENT_RUNTIME_TEST_CAPABILITY = "agent.runtime.test";
  private final MeService meService;
  private final AuthContextResolver authContextResolver;
  private final UserDirectoryView userDirectoryView;
  private final InvitationView invitationView;
  private final InvitationService invitationService;
  private final AgentBehaviorRepository agentBehaviorRepository;
  private final AgentRuntimeService agentRuntimeService;
  private final WorkstreamLogRepository workstreamLogRepository;

  public WorkstreamService(
      MeService meService,
      AuthContextResolver authContextResolver,
      UserDirectoryView userDirectoryView,
      InvitationView invitationView,
      UserAdminService userAdminService,
      InvitationService invitationService,
      AgentBehaviorRepository agentBehaviorRepository,
      AgentRuntimeService agentRuntimeService) {
    this(meService, authContextResolver, userDirectoryView, invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new InMemoryWorkstreamLogRepository());
  }

  public WorkstreamService(
      MeService meService,
      AuthContextResolver authContextResolver,
      UserDirectoryView userDirectoryView,
      InvitationView invitationView,
      UserAdminService userAdminService,
      InvitationService invitationService,
      AgentBehaviorRepository agentBehaviorRepository,
      AgentRuntimeService agentRuntimeService,
      WorkstreamLogRepository workstreamLogRepository) {
    this.meService = meService;
    this.authContextResolver = authContextResolver;
    this.userDirectoryView = userDirectoryView;
    this.invitationView = invitationView;
    this.invitationService = invitationService;
    this.agentBehaviorRepository = agentBehaviorRepository;
    this.agentRuntimeService = agentRuntimeService;
    this.workstreamLogRepository = workstreamLogRepository;
  }

  public WorkstreamBootstrapResponse bootstrap(WorkosIdentity identity, String selectedContextId, String correlationId) {
    var me = meService.me(identity, selectedContextId, correlationId);
    var actor = authContextResolver.resolveMe(identity, me.selectedAuthContext().selectedContextId(), correlationId);
    return new WorkstreamBootstrapResponse(me, me.functionalAgents(), initialItems(actor, correlationId), initialSurfaces(actor, correlationId));
  }

  public List<MeResponse.FunctionalAgentSummary> functionalAgents(WorkosIdentity identity, String selectedContextId, String correlationId) {
    return meService.me(identity, selectedContextId, correlationId).functionalAgents();
  }

  public List<WorkstreamItem> items(WorkosIdentity identity, String selectedContextId, String functionalAgentId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    var initial = initialItems(actor, correlationId).stream()
        .filter(item -> functionalAgentId == null || functionalAgentId.isBlank() || functionalAgentId.equals(item.functionalAgentId()))
        .toList();
    var persisted = workstreamLogRepository.items(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), functionalAgentId);
    var combined = new ArrayList<WorkstreamItem>();
    combined.addAll(initial);
    combined.addAll(persisted);
    return combined;
  }

  public SurfaceEnvelope surface(WorkosIdentity identity, String selectedContextId, String surfaceId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    var persisted = workstreamLogRepository.surface(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), surfaceId);
    if (persisted.isPresent()) return persisted.orElseThrow();
    return initialSurfaces(actor, correlationId).stream()
        .filter(surface -> surfaceId.equals(surface.surfaceId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
  }

  public CapabilityActionResult runAction(WorkosIdentity identity, String selectedContextId, CapabilityActionRequest request) {
    if (!Objects.equals(selectedContextId, request.selectedContextId())) throw new AuthorizationException(403, "CONTEXT_FORBIDDEN");
    var actor = authContextResolver.resolveMe(identity, selectedContextId, request.correlationId());
    var action = actionById(request.actionId());
    if (action == null || !Objects.equals(action.capabilityId(), request.capabilityId())) throw new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN");
    if (!actor.selectedContext().capabilities().contains(action.capabilityId()) && !USER_ADMIN_CAPABILITY.equals(action.capabilityId()) && !"audit.trace.read".equals(action.capabilityId())) throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    if (action.idempotency().required() && (request.idempotencyKey() == null || request.idempotencyKey().isBlank())) return new CapabilityActionResult("validation-error", "This action requires a client-generated idempotency key.", request.correlationId(), List.of("trace-validation-idempotency"), null);
    if (action.disabled() != null) return new CapabilityActionResult("denied", action.disabled().message(), request.correlationId(), List.of("trace-denied-" + action.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));

    if ("action-invite-user".equals(request.actionId())) {
      invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
          request.idempotencyKey(), actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId(),
          stringInput(request.input(), "email", "new-user@example.test"), stringInput(request.input(), "displayName", "New User"),
          List.of(FoundationRole.TENANT_EMPLOYEE), Instant.now().plus(7, ChronoUnit.DAYS), "workstream-invite", request.correlationId()));
    } else if ("action-propose-prompt-diff".equals(request.actionId())) {
      agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.PROMPT, "Approved revised prompt. Continue to require backend authorization, approval, and trace links.", List.of(), "UI-proposed prompt clarification", request.correlationId()));
    } else if ("action-test-agent-prompt".equals(request.actionId())) {
      agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_RUNTIME_TEST_CAPABILITY, request.correlationId(), stringInput(request.input(), "prompt", "Summarize current governed-agent readiness.")));
    } else if ("action-simulate-tool-boundary".equals(request.actionId())) {
      var unsafeGrant = new ToolPermissionBoundary.ToolGrant("email.send", ToolPermissionBoundary.Category.EXTERNAL_SIDE_EFFECT, "tenant.email.send", List.of("execute"), List.of("runtime"), "HIGH", "AUTONOMOUS", true, "full_work_trace");
      agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY, null, List.of(unsafeGrant), "Simulate policy-blocked side-effecting tool grant", request.correlationId()));
    } else if ("action-approve-skill-manifest".equals(request.actionId())) {
      return new CapabilityActionResult("approval-required", "Skill manifest approval is recorded as a governed review gate; activation must use an approved backend governance command.", request.correlationId(), List.of("trace-skill-manifest-approval-required"), agentSkillManifestSurface(actor, request.correlationId()));
    }
    return new CapabilityActionResult("accepted", action.label() + " accepted by backend-authoritative starter capability.", request.correlationId(), List.of("trace-" + request.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));
  }

  public WorkstreamMessageResponse submitMessage(WorkosIdentity identity, String selectedContextId, WorkstreamMessageRequest request, String fallbackCorrelationId) {
    var requestCorrelationId = firstNonBlank(request.correlationId(), fallbackCorrelationId, "workstream-message");
    if (request.selectedContextId() == null || request.selectedContextId().isBlank() || !Objects.equals(selectedContextId, request.selectedContextId())) throw new AuthorizationException(403, "CONTEXT_FORBIDDEN");
    if (request.functionalAgentId() == null || request.functionalAgentId().isBlank()) throw new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN");
    if (request.prompt() == null || request.prompt().isBlank()) throw new AuthorizationException(400, "PROMPT_REQUIRED");
    var actor = authContextResolver.resolveMe(identity, selectedContextId, requestCorrelationId);
    var functionalAgent = MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities()).stream()
        .filter(agent -> request.functionalAgentId().equals(agent.functionalAgentId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    if (!"visible".equals(functionalAgent.availability())) {
      persistDeniedFunctionalAgent(actor, request.functionalAgentId(), requestCorrelationId, request.idempotencyKey(), "FUNCTIONAL_AGENT_FORBIDDEN");
      throw new AuthorizationException(403, "FUNCTIONAL_AGENT_FORBIDDEN");
    }
    var duplicate = workstreamLogRepository.findByIdempotencyKey(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), request.functionalAgentId(), request.idempotencyKey());
    if (duplicate.isPresent()) {
      var existing = duplicate.orElseThrow();
      return new WorkstreamMessageResponse(existing.correlationId(), existing.idempotencyKey(), existing.userItem(), existing.agentItem(), existing.surface());
    }

    var runtime = agentRuntimeService.invokeWorkstreamAgent(new AgentRuntimeService.RuntimeInvocationRequest(
        actor.selectedContext().tenantId(), request.functionalAgentId(), actor.selectedContext(), requestCorrelationId, request.prompt()));
    var responseSeed = firstNonBlank(request.idempotencyKey(), requestCorrelationId, request.functionalAgentId());
    var userItemId = "item-message-user-" + stableSuffix(responseSeed + ":user");
    var agentItemId = "item-message-agent-" + stableSuffix(responseSeed + ":agent");
    var surfaceId = "surface-message-" + stableSuffix(responseSeed + ":markdown_response");
    var now = Instant.now().toString();
    var traceIds = runtime.traceIds().isEmpty() ? List.of("trace-workstream-message-" + stableSuffix(responseSeed + ":trace")) : runtime.traceIds();
    var userItem = new WorkstreamItem(userItemId, request.functionalAgentId(), "user-message", now, requestCorrelationId, traceIds, null, "User request", request.prompt(), "accepted");
    var markdown = runtime.decision() == AgentRuntimeTrace.Decision.ALLOWED
        ? runtime.markdown()
        : "## " + functionalAgent.label() + " unavailable\n\nModel-backed workstream execution was blocked before a response was produced. " + runtime.safeErrorSummary() + "\n\nTrace ids: `" + String.join("`, `", traceIds) + "`.";
    var surface = markdownResponseSurface(surfaceId, agentItemId, functionalAgent, actor, requestCorrelationId, traceIds, markdown);
    var agentItem = new WorkstreamItem(agentItemId, request.functionalAgentId(), "markdown_response", now, requestCorrelationId, traceIds, surface.surfaceId(), functionalAgent.label(), runtime.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "Model-backed response produced by governed workstream agent runtime." : "Model-backed workstream response blocked by governed runtime/provider boundary.", runtime.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "ready" : "blocked");
    var persisted = workstreamLogRepository.appendMessage(new WorkstreamLogRepository.WorkstreamMessageLogEntry(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), request.functionalAgentId(), request.idempotencyKey(), requestCorrelationId, userItem, agentItem, surface));
    return new WorkstreamMessageResponse(persisted.correlationId(), persisted.idempotencyKey(), persisted.userItem(), persisted.agentItem(), persisted.surface());
  }


  private void persistDeniedFunctionalAgent(AuthContextResolver.ResolvedMe actor, String functionalAgentId, String correlationId, String idempotencyKey, String reasonCode) {
    var now = Instant.now().toString();
    var seed = firstNonBlank(idempotencyKey, correlationId, functionalAgentId, reasonCode);
    var item = new WorkstreamItem("item-denial-" + stableSuffix(seed), functionalAgentId, "system_message", now, correlationId, List.of("trace-denial-" + stableSuffix(seed)), null, "Message not submitted", "Backend authorization denied this workstream message: " + reasonCode + ".", "blocked");
    workstreamLogRepository.appendSystemEntry(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), item, null);
  }

  public List<WorkstreamEvent> events(WorkosIdentity identity, String selectedContextId, String functionalAgentId, String lastEventId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    var events = initialEvents(actor, correlationId).stream().filter(event -> functionalAgentId == null || functionalAgentId.isBlank() || functionalAgentId.equals(event.functionalAgentId())).toList();
    if (lastEventId == null || lastEventId.isBlank()) return events;
    for (var index = 0; index < events.size(); index++) if (lastEventId.equals(events.get(index).eventId()) && index + 1 < events.size()) return events.subList(index + 1, events.size());
    return List.of(new WorkstreamEvent("evt-stale-replay-unavailable-999", "surface.stale", actor.selectedContext().tenantId(), actor.selectedContext().customerId(), USER_ADMIN_AGENT_ID, "surface-v0-user-admin-markdown", "markdown_response", "v1", correlationId, List.of("trace-sse-replay-unavailable"), Instant.now().toString(), 999, mapOf("reason", "Replay from Last-Event-ID is unavailable; refresh the affected starter markdown_response surfaces.")));
  }

  private List<SurfaceEnvelope> initialSurfaces(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return List.of(
        initialMarkdownSurface("surface-v0-my-account-markdown", "item-v0-my-account-markdown", MY_ACCOUNT_AGENT_ID, "My Account v0 response", actor, correlationId,
            "## My Account\n\n### Available now\n- Review signed-in profile, settings, selected context, and browser-safe capability basis.\n- Ask the My Account workstream about safe self-service next steps.\n\n### Full-core follow-up\nRicher profile and settings edit surfaces remain explicit full-core/demo follow-up behavior."),
        initialMarkdownSurface("surface-v0-user-admin-markdown", "item-v0-user-admin-markdown", USER_ADMIN_AGENT_ID, "User Admin v0 response", actor, correlationId,
            "## User Admin\n\n### Available now\n- Ask about invitations, memberships, roles, and tenant-scoped access review.\n- Message submission returns a backend-authorized `markdown_response`.\n\n### Full-core follow-up\nStructured user tables, invitation workflows, and access-review actions remain explicit full-core/demo follow-up behavior."),
        initialMarkdownSurface("surface-v0-agent-admin-markdown", "item-v0-agent-admin-markdown", AGENT_ADMIN_AGENT_ID, "Agent Admin v0 response", actor, correlationId,
            "## Agent Admin\n\n### Available now\n- Ask about seeded agent definitions, prompts, skills, tool boundaries, model refs, and trace obligations at starter scope.\n\n### Full-core follow-up\nPrompt editors, manifest diffs, model governance, and test consoles remain explicit full-core/demo follow-up behavior."),
        initialMarkdownSurface("surface-v0-audit-trace-markdown", "item-v0-audit-trace-markdown", AUDIT_TRACE_AGENT_ID, "Audit/Trace v0 response", actor, correlationId,
            "## Audit/Trace\n\n### Available now\n- Ask for browser-safe audit and trace summaries for the selected context.\n- Correlation and trace ids are preserved in the response envelope.\n\n### Full-core follow-up\nRich audit timelines and investigation views remain explicit full-core/demo follow-up behavior."),
        initialMarkdownSurface("surface-v0-governance-policy-markdown", "item-v0-governance-policy-markdown", GOVERNANCE_POLICY_AGENT_ID, "Governance/Policy v0 response", actor, correlationId,
            "## Governance/Policy\n\n### Available now\n- Ask about policy guardrails, approval boundaries, deferred decisions, and safe next steps.\n\n### Full-core follow-up\nPolicy simulations, proposal diffs, and approval cards remain explicit full-core/demo follow-up behavior."));
  }

  private List<WorkstreamItem> initialItems(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var now = Instant.now().toString();
    return List.of(
        initialMarkdownItem("item-v0-my-account-markdown", MY_ACCOUNT_AGENT_ID, now, correlationId, "surface-v0-my-account-markdown", "My Account v0 response", "Five core v0 starter surface for account, profile, settings, selected context, and authority basis."),
        initialMarkdownItem("item-v0-user-admin-markdown", USER_ADMIN_AGENT_ID, now, correlationId, "surface-v0-user-admin-markdown", "User Admin v0 response", "Five core v0 starter surface for invitations, memberships, roles, and access review questions."),
        initialMarkdownItem("item-v0-agent-admin-markdown", AGENT_ADMIN_AGENT_ID, now, correlationId, "surface-v0-agent-admin-markdown", "Agent Admin v0 response", "Five core v0 starter surface for governed agent definitions, prompts, skills, tool boundaries, models, and traces."),
        initialMarkdownItem("item-v0-audit-trace-markdown", AUDIT_TRACE_AGENT_ID, now, correlationId, "surface-v0-audit-trace-markdown", "Audit/Trace v0 response", "Five core v0 starter surface for browser-safe audit and trace summaries."),
        initialMarkdownItem("item-v0-governance-policy-markdown", GOVERNANCE_POLICY_AGENT_ID, now, correlationId, "surface-v0-governance-policy-markdown", "Governance/Policy v0 response", "Five core v0 starter surface for policy guardrails, approval boundaries, and deferred full-core behavior."));
  }

  private WorkstreamItem initialMarkdownItem(String itemId, String functionalAgentId, String now, String correlationId, String surfaceId, String title, String body) {
    return new WorkstreamItem(itemId, functionalAgentId, "markdown_response", now, correlationId, List.of("trace-" + surfaceId), surfaceId, title, body, "ready");
  }

  private SurfaceEnvelope initialMarkdownSurface(String surfaceId, String workstreamEntryId, String functionalAgentId, String title, AuthContextResolver.ResolvedMe actor, String correlationId, String markdown) {
    var agent = MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities()).stream()
        .filter(summary -> functionalAgentId.equals(summary.functionalAgentId()))
        .findFirst()
        .orElse(new MeResponse.FunctionalAgentSummary(functionalAgentId, title.replace(" v0 response", ""), "Five core v0 starter workstream.", "workstream", "markdown_response", List.of(), "visible", null));
    return markdownResponseSurface(surfaceId, workstreamEntryId, agent, actor, correlationId, List.of("trace-" + surfaceId), markdown);
  }

  private SurfaceEnvelope myAccountDashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-account-dashboard", "dashboard", "My Account", actor, correlationId,
        mapOf("cards", List.of(mapOf("cardId", "card-my-profile", "label", "Profile", "value", "View or edit", "severity", "info"), mapOf("cardId", "card-my-settings", "label", "Settings", "value", "Preferences", "severity", "info"), mapOf("cardId", "card-sign-out", "label", "Sign out", "value", "End session", "severity", "warning")), "sections", List.of(mapOf("sectionId", "self-service", "label", "Self-service", "summary", "Profile and settings open as request/response surfaces in the My Account workstream."), mapOf("sectionId", "security-boundary", "label", "Security boundary", "summary", "Roles, memberships, support access, and tenant administration stay in governed admin workstreams."))),
        List.of(showProfileAction(), showSettingsAction(), signOutAction(), openAuditAction()));
  }

  private SurfaceEnvelope myProfileSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-profile", "detail-edit", "User profile", actor, correlationId,
        mapOf("recordId", actor.account().accountId() + "-profile", "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(), "recordKind", "profile", "summary", "Current signed-in user profile. Administrative role and membership changes are intentionally not editable here.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", actor.account().displayEmail(), "editable", false, "inputType", "email", "disabledReason", "Email is owned by WorkOS/AuthKit identity reconciliation."), mapOf("fieldId", "locale", "label", "Locale", "value", "en-US", "editable", true, "inputType", "select"), mapOf("fieldId", "timeZone", "label", "Time zone", "value", "America/New_York", "editable", true, "inputType", "text")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", "profile.update"), "audit", mapOf("lastEventType", "UserProfileDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-profile"))), List.of(updateProfileAction(), openAuditAction()));
  }

  private SurfaceEnvelope mySettingsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-settings", "detail-edit", "User settings", actor, correlationId,
        mapOf("recordId", actor.account().accountId() + "-settings", "recordLabel", actor.profile().displayName() + " settings", "recordKind", "settings", "summary", "Current signed-in user preferences for the workstream shell and notifications.", "fields", List.of(mapOf("fieldId", "preferredColorMode", "label", "Color mode", "value", "system", "editable", true, "inputType", "select"), mapOf("fieldId", "notificationDigest", "label", "Notification digest", "value", "daily", "editable", true, "inputType", "select"), mapOf("fieldId", "composerDensity", "label", "Composer density", "value", "comfortable", "editable", true, "inputType", "select")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", "profile.update"), "audit", mapOf("lastEventType", "UserSettingsDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-settings"))), List.of(updateSettingsAction(), openAuditAction()));
  }

  private SurfaceEnvelope dashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var users = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var invites = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    return envelope("surface-user-admin-dashboard", "dashboard", "User Admin command center", actor, correlationId, mapOf("cards", List.of(mapOf("cardId", "card-pending-invitations", "label", "Pending invitations", "value", invites.size(), "severity", invites.isEmpty() ? "info" : "warning"), mapOf("cardId", "card-active-users", "label", "Active users", "value", users.size(), "severity", "info"), mapOf("cardId", "card-access-review", "label", "Access review items", "value", 0, "severity", "info"))), List.of(displayListAction(), inviteAction(), traceAction()));
  }

  private SurfaceEnvelope listSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var rows = new ArrayList<Map<String, Object>>();
    for (var user : userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())) rows.add(mapOf("id", user.accountId(), "rowType", "user-directory", "email", user.accountId(), "displayName", user.displayName(), "role", user.roles().toString(), "status", user.membershipStatus().name().toLowerCase(), "traceId", "trace-user-" + user.accountId()));
    for (var invite : invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())) rows.add(mapOf("id", invite.invitationId(), "rowType", "invitation-queue", "email", invite.targetEmail(), "displayName", invite.targetEmail(), "role", invite.requestedRoles().toString(), "status", invite.status().name().toLowerCase(), "delivery", invite.deliveryStatus().name().toLowerCase(), "traceId", "trace-invite-" + invite.invitationId()));
    return envelope("surface-user-admin-list", "list-search", "Users, invitations, and memberships", actor, correlationId, mapOf("query", "scope:" + actor.selectedContext().scopeType().name().toLowerCase(), "rows", rows, "pageInfo", mapOf("totalKnownCount", rows.size()), "mobileFallback", "table-to-card"), List.of(displayDetailAction(), inviteAction(), deniedReplaceRoleAction(), traceAction()));
  }

  private SurfaceEnvelope detailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-user-admin-detail-admin", "detail-edit", "Tenant Admin account detail", actor, correlationId, mapOf("recordId", actor.account().accountId(), "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(), "recordKind", "account", "summary", "Scoped detail/edit surface backed by UserAdminService authorization, idempotency, and audit semantics.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"), mapOf("fieldId", "role", "label", "Membership role", "value", actor.selectedContext().roles().toString(), "editable", false, "inputType", "select", "disabledReason", "Role changes are checked by backend policy.")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", USER_ADMIN_CAPABILITY), "audit", mapOf("lastEventType", "UserAdminDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-user-admin-detail"))), List.of(deniedReplaceRoleAction(), traceAction()));
  }

  private SurfaceEnvelope auditTimelineSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var now = Instant.now().toString();
    var events = new ArrayList<Map<String, Object>>();
    events.add(mapOf("eventId", "audit-me-read", "occurredAt", now, "actor", actor.profile().displayName(), "action", "Loaded /api/me and selected AuthContext", "traceId", "trace-my-account"));
    for (var trace : agentRuntimeService.traces()) events.add(mapOf("eventId", trace.traceId(), "occurredAt", trace.occurredAt().toString(), "actor", trace.actorId(), "action", trace.traceType() + " " + trace.decision() + " · " + trace.safeSummary(), "traceId", trace.traceId()));
    return envelope("surface-audit-timeline", "audit-timeline", "Audit and trace timeline", actor, correlationId, mapOf("events", events), List.of(openAuditAction()));
  }

  private SurfaceEnvelope governancePolicySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-governance-policy", "governance-diff", "Governance/Policy decision queue", actor, correlationId,
        mapOf("proposalId", "starter-governance-policy-review", "beforeSummary", "Behavior changes are draft proposals with redacted evidence and baseline checks.", "afterSummary", "Human approval activates reviewed prompt, skill, manifest, tool-boundary, model-policy, or rubric changes through backend commands.", "changes", List.of(mapOf("path", "improvements.review", "before", "pending-human", "after", "approve-or-request-changes", "impact", "Evaluator and behavior editor agents cannot self-approve."), mapOf("path", "improvements.activate", "before", "approved-only", "after", "backend-governance-command", "impact", "Stale baseline, missing simulation, or authority expansion returns denial."), mapOf("path", "trace.evidence", "before", "redacted-links", "after", "audit-trace-linked", "impact", "Decision cards link PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace.")), "decisionCard", mapOf("risk", "medium", "confidence", "0.82", "requiresApproval", true, "denialShape", "TARGET_NOT_FOUND_OR_FORBIDDEN")), List.of(simulatePolicyAction(), commitPolicyAction(), openAuditAction()));
  }

  private SurfaceEnvelope agentAdminCatalogSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var rows = agentBehaviorRepository.agentDefinitions(actor.selectedContext().tenantId()).stream().map(agent -> mapOf("id", agent.agentDefinitionId(), "displayName", agent.displayName(), "status", agent.status().name(), "authorityLevel", agent.authorityLevel().name(), "functionalAreaId", agent.functionalAreaId(), "tracePolicy", agent.traceRequirements().toString())).toList();
    return envelope("surface-agent-admin-catalog", "list-search", "Agent Admin catalog", actor, correlationId, mapOf("query", "tenant:" + actor.selectedContext().tenantId(), "rows", rows, "pageInfo", mapOf("totalKnownCount", rows.size()), "emptyCopy", "Empty when no governed AgentDefinition records are seeded."), List.of(displayAgentCatalogAction(), openAgentDetailAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var agent = agentBehaviorRepository.agentDefinition(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return envelope("surface-agent-admin-detail", "detail-edit", "Agent Admin readiness detail", actor, correlationId, mapOf("recordId", agent.agentDefinitionId(), "recordLabel", agent.displayName(), "recordKind", "agent-definition", "summary", "Active governed agent uses approved prompt, compact manifest, deny-by-default tool boundary, safe model refs, and required traces.", "fields", List.of(mapOf("fieldId", "status", "label", "Status", "value", agent.status().name(), "editable", false), mapOf("fieldId", "promptDocumentId", "label", "Prompt", "value", agent.promptDocumentId() + "@" + agent.activePromptVersion(), "editable", false), mapOf("fieldId", "skillManifestId", "label", "Skill manifest", "value", agent.skillManifestId() + "@" + agent.activeSkillManifestVersion(), "editable", false), mapOf("fieldId", "toolBoundaryId", "label", "Tool boundary", "value", agent.toolBoundaryId() + "@" + agent.activeToolBoundaryVersion(), "editable", false), mapOf("fieldId", "modelConfigRef", "label", "Model ref", "value", agent.modelConfigRefId(), "editable", false, "disabledReason", "Provider secret values are never browser-visible")), "version", 1, "permissionState", mapOf("canEdit", true, "reason", "Edits must create governed behavior proposals.", "authoritativeCapabilityId", AGENT_DEFINITIONS_CAPABILITY), "audit", mapOf("lastEventType", "AgentDefinitionDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-agent-detail"))), List.of(proposePromptDiffAction(), testPromptAction(), manageModelRefAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentPromptGovernanceSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-prompt-governance", "governance-diff", "Prompt governance review", actor, correlationId, mapOf("proposalId", "proposal-prompt-review", "beforeSummary", "Active prompt remains in force.", "afterSummary", "Proposed prompt diff requires validation and human approval before activation.", "changes", List.of(mapOf("path", "prompt.system", "before", "seeded active prompt", "after", "clarified backend authorization wording", "impact", "No authority expansion; validation-error blocks unsafe text.")), "uiStates", List.of("Loading surface", "Empty", "validation-error", "approval-required"), "requiresApproval", true), List.of(proposePromptDiffAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentSkillManifestSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-skill-manifest-diff", "governance-diff", "Skill manifest diff", actor, correlationId, mapOf("proposalId", "proposal-skill-manifest-review", "beforeSummary", "Compact manifest lists approved skill ids and when-to-use hints only.", "afterSummary", "readSkill(skillId) remains the only path to full approved skill text.", "changes", List.of(mapOf("path", "manifest.entries.access-review", "before", "active", "after", "active", "impact", "SkillLoadTrace emitted for allowed and denied loads.")), "requiresApproval", true, "traceLabels", List.of("SkillLoadTrace", "readSkill(skillId)")), List.of(approveSkillManifestAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentToolBoundarySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-tool-boundary-diff", "governance-diff", "Tool boundary simulation", actor, correlationId, mapOf("proposalId", "proposal-tool-boundary-email-send", "beforeSummary", "Read-only and readSkill grants are active.", "afterSummary", "Autonomous high-impact email.send grant is policy-blocked.", "changes", List.of(mapOf("path", "toolGrants.email.send", "before", "absent", "after", "AUTONOMOUS HIGH side effect", "impact", "TOOL_BOUNDARY_DENIED; approval-required for external side effects.")), "requiresApproval", true, "denial", "TOOL_BOUNDARY_DENIED"), List.of(simulateToolBoundaryAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentModelRefsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-model-refs", "detail-edit", "Agent model refs", actor, correlationId, mapOf("recordId", "starter-default-model", "recordLabel", "starter-default-model", "recordKind", "model-config-ref", "summary", "Safe model aliases only; Provider secret values are never browser-visible.", "secretVisibility", "redacted", "fields", List.of(mapOf("fieldId", "providerAlias", "label", "Provider alias", "value", "starter-provider", "editable", false), mapOf("fieldId", "secret", "label", "Provider secret", "value", "[REDACTED]", "editable", false, "disabledReason", "Provider secret values are never browser-visible")), "version", 1, "permissionState", mapOf("canEdit", false, "reason", "MODEL_POLICY_DENIED", "authoritativeCapabilityId", AGENT_MODELS_READ_CAPABILITY)), List.of(manageModelRefAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentTestConsoleSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_RUNTIME_TEST_CAPABILITY, correlationId, "No-side-effect agent test console"));
    return envelope("surface-agent-test-console", "workflow-status", "No-side-effect agent test console", actor, correlationId, mapOf("workflowId", "agent-runtime-test", "status", "completed", "steps", List.of(mapOf("stepId", "prompt-assembly", "label", "PromptAssemblyTrace", "status", prompt.decision().name()), mapOf("stepId", "skill-load", "label", "SkillLoadTrace", "status", "available-through-readSkill(skillId)"), mapOf("stepId", "agent-work", "label", "AgentWorkTrace", "status", "no production side effects")), "traceIds", List.of(prompt.traceId(), "trace-agent-work-88")), List.of(testPromptAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentBehaviorProposalSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var proposals = agentRuntimeService.proposals().stream().map(proposal -> mapOf("id", proposal.proposalId(), "target", proposal.targetArtifact().name(), "status", proposal.status().name(), "risk", proposal.riskClassification(), "reviewedBy", proposal.reviewedByAccountId(), "denial", proposal.reviewReason())).toList();
    return envelope("surface-agent-behavior-proposal", "decision", "Behavior proposal decision", actor, correlationId, mapOf("decisionId", "decision-behavior-proposal", "recommendation", "Approve only validated, non-authority-expanding behavior changes.", "riskScore", 42, "confidenceScore", 88, "evidence", List.of(mapOf("evidenceId", "proposal-count", "label", "Proposal queue", "summary", proposals.size() + " proposals tracked"), mapOf("evidenceId", "approval-boundary", "label", "Approval boundary", "summary", "approval-required for activation")), "proposals", proposals), List.of(proposePromptDiffAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentAdminTraceSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-admin-trace", "audit-timeline", "Agent Admin traces", actor, correlationId, mapOf("events", List.of(mapOf("eventId", "trace-prompt-assembly-42", "occurredAt", Instant.now().toString(), "actor", "AgentRuntimeService", "action", "PromptAssemblyTrace emitted for deterministic prompt assembly", "traceId", "trace-prompt-assembly-42"), mapOf("eventId", "trace-skill-load-17", "occurredAt", Instant.now().toString(), "actor", "readSkill(skillId)", "action", "SkillLoadTrace emitted for allowed or denied skill loads", "traceId", "trace-skill-load-17"), mapOf("eventId", "trace-agent-work-88", "occurredAt", Instant.now().toString(), "actor", "No-side-effect agent test console", "action", "AgentWorkTrace links test-mode output to governed prompt and skills", "traceId", "trace-agent-work-88"))), List.of(openAgentTraceAction()));
  }

  private SurfaceEnvelope markdownResponseSurface(String surfaceId, String workstreamEntryId, MeResponse.FunctionalAgentSummary agent, AuthContextResolver.ResolvedMe actor, String correlationId, List<String> traceIds, String markdown) {
    return new SurfaceEnvelope(surfaceId, "markdown_response", "v1", agent.label(), agent.functionalAgentId(), List.of("agent-audit-trace"),
        mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()),
        correlationId, traceIds, Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential", "providerCredentialValue")),
        mapOf("markdown", markdown, "title", agent.label() + " response", "summary", "Backend-authorized starter response for " + agent.label() + ".", "workstreamEntryId", workstreamEntryId, "producingAgentId", agent.functionalAgentId(), "sourceRefs", List.of(mapOf("refType", "capability", "refId", agent.requiredCapabilityIds().isEmpty() ? "profile.read" : agent.requiredCapabilityIds().get(0), "label", "Backend capability boundary"), mapOf("refType", "trace", "refId", traceIds.get(0), "label", "Workstream message trace")), "sections", List.of(mapOf("anchor", "starter-scope", "title", "Starter scope"), mapOf("anchor", "safe-next-steps", "title", "Safe next steps")), "safety", mapOf("sanitized", false, "blockedUnsafeLinks", 0, "blockedRawHtml", false, "redactionNote", "Provider secrets, raw JWTs, invitation tokens, and hidden capabilities are never included."), "trace", mapOf("correlationId", correlationId, "traceIds", traceIds)),
        List.of(openAuditAction()), List.of(mapOf("label", "Open trace", "href", "/ui?traceId=" + traceIds.get(0), "rel", "trace")));
  }

  private List<WorkstreamEvent> initialEvents(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var tenantId = actor.selectedContext().tenantId(); var customerId = actor.selectedContext().customerId(); var now = Instant.now().toString();
    return List.of(new WorkstreamEvent("evt-workstream-reconnected-001", "surface.reconnected", tenantId, customerId, MY_ACCOUNT_AGENT_ID, "surface-v0-my-account-markdown", "markdown_response", "v1", correlationId, List.of("trace-sse-reconnected"), now, 1, mapOf("message", "Realtime stream connected for selected AuthContext.")), new WorkstreamEvent("evt-audit-appended-002", "workstream.item.appended", tenantId, customerId, AUDIT_TRACE_AGENT_ID, "surface-v0-audit-trace-markdown", "markdown_response", "v1", correlationId, List.of("trace-sse-audit"), now, 2, mapOf("itemId", "item-v0-audit-trace-markdown", "kind", "markdown_response", "title", "Audit/Trace v0 response", "body", "SSE delivered a browser-safe five-core v0 trace update.", "surfaceId", "surface-v0-audit-trace-markdown", "status", "ready")), new WorkstreamEvent("evt-user-admin-stale-003", "surface.stale", tenantId, customerId, USER_ADMIN_AGENT_ID, "surface-v0-user-admin-markdown", "markdown_response", "v1", correlationId, List.of("trace-sse-stale"), now, 3, mapOf("reason", "User Admin markdown_response should refresh after invitation/user changes.")), new WorkstreamEvent("evt-agent-admin-reconnected-004", "surface.reconnected", tenantId, customerId, AGENT_ADMIN_AGENT_ID, "surface-v0-agent-admin-markdown", "markdown_response", "v1", correlationId, List.of("trace-agent-admin-reconnected"), now, 4, mapOf("message", "Agent Admin v0 markdown_response is backed by governed records.")));
  }

  private SurfaceEnvelope surfaceForAction(AuthContextResolver.ResolvedMe actor, String actionId, String correlationId) {
    return switch (actionId) {
      case "action-open-audit-trace", "action-open-trace", "action-open-agent-trace" -> auditTimelineSurface(actor, correlationId);
      case "action-show-my-profile", "action-update-my-profile" -> myProfileSurface(actor, correlationId);
      case "action-show-my-settings", "action-update-my-settings" -> mySettingsSurface(actor, correlationId);
      case "action-sign-out" -> myAccountDashboardSurface(actor, correlationId);
      case "action-simulate-policy", "action-commit-policy" -> governancePolicySurface(actor, correlationId);
      case "action-display-agent-catalog" -> agentAdminCatalogSurface(actor, correlationId);
      case "action-open-agent-detail" -> agentAdminDetailSurface(actor, correlationId);
      case "action-propose-prompt-diff" -> agentPromptGovernanceSurface(actor, correlationId);
      case "action-test-agent-prompt" -> agentTestConsoleSurface(actor, correlationId);
      case "action-approve-skill-manifest" -> agentSkillManifestSurface(actor, correlationId);
      case "action-simulate-tool-boundary" -> agentToolBoundarySurface(actor, correlationId);
      case "action-manage-model-ref" -> agentModelRefsSurface(actor, correlationId);
      case "action-display-user-detail", "action-replace-membership-role" -> detailSurface(actor, correlationId);
      case "action-display-user-list", "action-invite-user" -> listSurface(actor, correlationId);
      default -> dashboardSurface(actor, correlationId);
    };
  }

  private SurfaceAction actionById(String actionId) {
    return List.of(showProfileAction(), showSettingsAction(), updateProfileAction(), updateSettingsAction(), signOutAction(), displayListAction(), displayDetailAction(), inviteAction(), deniedReplaceRoleAction(), traceAction(), openAuditAction(), simulatePolicyAction(), commitPolicyAction(), displayAgentCatalogAction(), openAgentDetailAction(), proposePromptDiffAction(), testPromptAction(), approveSkillManifestAction(), simulateToolBoundaryAction(), manageModelRefAction(), openAgentTraceAction()).stream().filter(action -> actionId.equals(action.actionId())).findFirst().orElse(null);
  }

  private SurfaceEnvelope envelope(String id, String type, String title, AuthContextResolver.ResolvedMe actor, String correlationId, Map<String, Object> data, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(id, type, "v1", title, ownerForSurface(id), reusableAgentsForSurface(id), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, List.of("trace-" + id), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential")), data, actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + id, "rel", "deep-link")));
  }

  private String ownerForSurface(String surfaceId) { if (surfaceId.startsWith("surface-my-")) return MY_ACCOUNT_AGENT_ID; if (surfaceId.startsWith("surface-audit")) return AUDIT_TRACE_AGENT_ID; if (surfaceId.startsWith("surface-governance")) return GOVERNANCE_POLICY_AGENT_ID; if (surfaceId.startsWith("surface-agent")) return AGENT_ADMIN_AGENT_ID; return USER_ADMIN_AGENT_ID; }
  private List<String> reusableAgentsForSurface(String surfaceId) { if (surfaceId.startsWith("surface-audit")) return List.of(USER_ADMIN_AGENT_ID, GOVERNANCE_POLICY_AGENT_ID, AGENT_ADMIN_AGENT_ID, MY_ACCOUNT_AGENT_ID); if (surfaceId.startsWith("surface-my-")) return List.of(AUDIT_TRACE_AGENT_ID); if (surfaceId.startsWith("surface-agent")) return List.of(GOVERNANCE_POLICY_AGENT_ID, AUDIT_TRACE_AGENT_ID); if (surfaceId.startsWith("surface-governance")) return List.of(AGENT_ADMIN_AGENT_ID, AUDIT_TRACE_AGENT_ID); return List.of(AUDIT_TRACE_AGENT_ID); }

  private SurfaceAction showProfileAction() { return new SurfaceAction("action-show-my-profile", "Show user profile", "read", "profile.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-profile", "inline"), new Audit("UserProfileDisplayed", true)); }
  private SurfaceAction showSettingsAction() { return new SurfaceAction("action-show-my-settings", "Show user settings", "read", "profile.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-settings", "inline"), new Audit("UserSettingsDisplayed", true)); }
  private SurfaceAction updateProfileAction() { return new SurfaceAction("action-update-my-profile", "Save profile changes", "command", "profile.update", "schema.my-account.profile.update.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-my-profile", "inline"), new Audit("UserProfileUpdateRequested", true)); }
  private SurfaceAction updateSettingsAction() { return new SurfaceAction("action-update-my-settings", "Save settings changes", "command", "profile.update", "schema.my-account.settings.update.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-my-settings", "inline"), new Audit("UserSettingsUpdateRequested", true)); }
  private SurfaceAction signOutAction() { return new SurfaceAction("action-sign-out", "Sign out", "command", "profile.read", null, true, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-dashboard", "inline"), new Audit("SessionSignOutRequested", true)); }

  private SurfaceAction displayListAction() { return new SurfaceAction("action-display-user-list", "Display user list view", "read", USER_ADMIN_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("UserAdminListDisplayed", true)); }
  private SurfaceAction displayDetailAction() { return new SurfaceAction("action-display-user-detail", "Display user account detail", "read", USER_ADMIN_CAPABILITY, "schema.user-admin.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("UserAdminDetailDisplayed", true)); }
  private SurfaceAction inviteAction() { return new SurfaceAction("action-invite-user", "Invite user", "command", USER_ADMIN_CAPABILITY, "schema.invitation.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("InvitationRequested", true)); }
  private SurfaceAction deniedReplaceRoleAction() { return new SurfaceAction("action-replace-membership-role", "Replace membership role", "command", USER_ADMIN_CAPABILITY, "schema.membership.role.replace.v1", true, false, new DisabledReason("LAST_ADMIN_DENIED", "Backend authorization denied this action: cannot remove the last tenant admin without an approved replacement."), new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("MembershipRoleReplacementDenied", true)); }
  private SurfaceAction traceAction() { return new SurfaceAction("action-open-trace", "Open trace", "trace", "audit.trace.read", null, false, false, null, new Idempotency(false, null), null, new Audit("TraceOpened", true)); }
  private SurfaceAction openAuditAction() { return new SurfaceAction("action-open-audit-trace", "Open audit timeline", "trace", "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-timeline", "inline"), new Audit("AuditTimelineOpened", true)); }
  private SurfaceAction simulatePolicyAction() { return new SurfaceAction("action-simulate-policy", "Run governance simulation", "governance", "governance.policy.simulate", "schema.policy.simulate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy", "inline"), new Audit("PolicySimulationRequested", true)); }
  private SurfaceAction commitPolicyAction() { return new SurfaceAction("action-commit-policy", "Approve governance change", "approval", "governance.policy.commit", "schema.policy.commit.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy", "inline"), new Audit("PolicyCommitApprovalRequested", true)); }
  private SurfaceAction displayAgentCatalogAction() { return new SurfaceAction("action-display-agent-catalog", "Display agent catalog", "read", AGENT_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction openAgentDetailAction() { return new SurfaceAction("action-open-agent-detail", "Open agent readiness detail", "read", AGENT_DEFINITIONS_CAPABILITY, "schema.agent-definition.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction proposePromptDiffAction() { return new SurfaceAction("action-propose-prompt-diff", "Propose prompt diff", "proposal", AGENT_PROMPTS_CAPABILITY, "schema.prompt-version.proposal.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-prompt-governance", "side-panel"), new Audit("PromptVersionDraftProposed", true)); }
  private SurfaceAction testPromptAction() { return new SurfaceAction("action-test-agent-prompt", "Run no-side-effect prompt test", "workflow", AGENT_RUNTIME_TEST_CAPABILITY, "schema.agent-runtime.test.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface("workflow-status", null, "inline"), new Audit("AgentRuntimeTestRequested", true)); }
  private SurfaceAction approveSkillManifestAction() { return new SurfaceAction("action-approve-skill-manifest", "Approve manifest review", "approval", AGENT_SKILLS_CAPABILITY, null, true, true, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-agent-skill-manifest-diff", "inline"), new Audit("AgentSkillManifestApproved", true)); }
  private SurfaceAction simulateToolBoundaryAction() { return new SurfaceAction("action-simulate-tool-boundary", "Simulate tool boundary change", "governance", AGENT_TOOL_BOUNDARIES_CAPABILITY, "schema.tool-boundary.simulation.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("ToolBoundarySimulationRequested", true)); }
  private SurfaceAction manageModelRefAction() { return new SurfaceAction("action-manage-model-ref", "Request model ref change", "proposal", AGENT_MODELS_MANAGE_CAPABILITY, null, false, false, new DisabledReason("MODEL_POLICY_DENIED", "This starter denies switching to a disabled provider alias; provider secrets remain redacted."), new Idempotency(true, "client-generated"), new ResultSurface("decision", null, "inline"), new Audit("AgentModelRefChangeDenied", true)); }
  private SurfaceAction openAgentTraceAction() { return new SurfaceAction("action-open-agent-trace", "Open agent work trace", "trace", "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentWorkTraceOpened", true)); }

  private static String stringInput(Object input, String key, String fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return value; return fallback; }
  private static String firstNonBlank(String... values) { for (var value : values) if (value != null && !value.isBlank()) return value; return null; }
  private static String stableSuffix(String value) { return Integer.toUnsignedString(Objects.requireNonNullElse(value, "workstream-message").hashCode(), 36); }
  private static Map<String, Object> mapOf(Object... values) { var map = new LinkedHashMap<String, Object>(); for (int i = 0; i + 1 < values.length; i += 2) map.put(String.valueOf(values[i]), values[i + 1]); return map; }

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
  public record WorkstreamMessageRequest(String selectedContextId, String functionalAgentId, String prompt, String correlationId, String idempotencyKey) {}
  public record WorkstreamMessageResponse(String correlationId, String idempotencyKey, WorkstreamItem userItem, WorkstreamItem agentItem, SurfaceEnvelope surface) {}
  public record WorkstreamEvent(String eventId, String eventType, String tenantId, String customerId, String functionalAgentId, String surfaceId, String surfaceType, String surfaceVersion, String correlationId, List<String> traceIds, String occurredAt, Integer sequence, Map<String, Object> patch) {}
}
