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
  private static final String ACCESS_PROFILE_AGENT_ID = "agent-access-profile";
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

  public WorkstreamService(
      MeService meService,
      AuthContextResolver authContextResolver,
      UserDirectoryView userDirectoryView,
      InvitationView invitationView,
      UserAdminService userAdminService,
      InvitationService invitationService,
      AgentBehaviorRepository agentBehaviorRepository,
      AgentRuntimeService agentRuntimeService) {
    this.meService = meService;
    this.authContextResolver = authContextResolver;
    this.userDirectoryView = userDirectoryView;
    this.invitationView = invitationView;
    this.invitationService = invitationService;
    this.agentBehaviorRepository = agentBehaviorRepository;
    this.agentRuntimeService = agentRuntimeService;
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

  public List<WorkstreamEvent> events(WorkosIdentity identity, String selectedContextId, String functionalAgentId, String lastEventId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    var events = initialEvents(actor, correlationId).stream().filter(event -> functionalAgentId == null || functionalAgentId.isBlank() || functionalAgentId.equals(event.functionalAgentId())).toList();
    if (lastEventId == null || lastEventId.isBlank()) return events;
    for (var index = 0; index < events.size(); index++) if (lastEventId.equals(events.get(index).eventId()) && index + 1 < events.size()) return events.subList(index + 1, events.size());
    return List.of(new WorkstreamEvent("evt-stale-replay-unavailable-999", "surface.stale", actor.selectedContext().tenantId(), actor.selectedContext().customerId(), USER_ADMIN_AGENT_ID, "surface-user-admin-dashboard", "dashboard", "v1", correlationId, List.of("trace-sse-replay-unavailable"), Instant.now().toString(), 999, mapOf("reason", "Replay from Last-Event-ID is unavailable; refresh the affected starter surfaces.")));
  }

  private List<SurfaceEnvelope> initialSurfaces(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return List.of(accessProfileSurface(actor, correlationId), dashboardSurface(actor, correlationId), listSurface(actor, correlationId), detailSurface(actor, correlationId), auditTimelineSurface(actor, correlationId), governancePolicySurface(actor, correlationId), agentAdminCatalogSurface(actor, correlationId), agentAdminDetailSurface(actor, correlationId), agentPromptGovernanceSurface(actor, correlationId), agentSkillManifestSurface(actor, correlationId), agentToolBoundarySurface(actor, correlationId), agentModelRefsSurface(actor, correlationId), agentTestConsoleSurface(actor, correlationId), agentBehaviorProposalSurface(actor, correlationId), agentAdminTraceSurface(actor, correlationId));
  }

  private List<WorkstreamItem> initialItems(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var now = Instant.now().toString();
    return List.of(
        new WorkstreamItem("item-access-profile", ACCESS_PROFILE_AGENT_ID, "surface", now, correlationId, List.of("trace-access-profile"), "surface-access-profile", "Access/Profile context", "Signed-in account and selected AuthContext loaded from backend /api/workstream/bootstrap.", "ready"),
        new WorkstreamItem("item-user-admin-dashboard", USER_ADMIN_AGENT_ID, "surface", now, correlationId, List.of("trace-user-admin-dashboard"), "surface-user-admin-dashboard", "User Admin command center", "Tenant-scoped user administration dashboard loaded from /api/workstream/bootstrap.", "ready"),
        new WorkstreamItem("item-audit-trace", AUDIT_TRACE_AGENT_ID, "audit-trace", now, correlationId, List.of("trace-audit-timeline"), "surface-audit-timeline", "Audit and trace timeline", "Protected reads and starter denials are visible as browser-safe audit excerpts.", "ready"),
        new WorkstreamItem("item-governance-policy", GOVERNANCE_POLICY_AGENT_ID, "decision", now, correlationId, List.of("trace-governance-policy"), "surface-governance-policy", "Governance policy review", "Improvement proposals, approvals, denials, and activation gates are surfaced as governed decision cards.", "waiting-for-human"),
        new WorkstreamItem("item-agent-admin-catalog", AGENT_ADMIN_AGENT_ID, "surface", now, correlationId, List.of("trace-agent-catalog"), "surface-agent-admin-catalog", "Agent catalog", "Seeded governed agent records are visible and capability-backed.", "ready"),
        new WorkstreamItem("item-agent-admin-detail", AGENT_ADMIN_AGENT_ID, "surface", now, correlationId, List.of("trace-agent-detail"), "surface-agent-admin-detail", "Agent readiness detail", "Prompt, skill manifest, tool boundary, model ref, and trace obligations are visible.", "ready"),
        new WorkstreamItem("item-agent-prompt-review", AGENT_ADMIN_AGENT_ID, "decision", now, correlationId, List.of("trace-prompt-review"), "surface-agent-prompt-governance", "Prompt governance review requires validation fixes", "Prompt changes require proposal, validation, and approval before activation.", "waiting-for-human"),
        new WorkstreamItem("item-agent-tool-boundary", AGENT_ADMIN_AGENT_ID, "decision", now, correlationId, List.of("trace-tool-boundary-denial"), "surface-agent-tool-boundary-diff", "Tool boundary simulation policy-blocked", "Side-effecting autonomous email tool expansion is denied pending separate policy approval.", "blocked"),
        new WorkstreamItem("item-agent-test-console", AGENT_ADMIN_AGENT_ID, "workflow-status", now, correlationId, List.of("trace-agent-work-88"), "surface-agent-test-console", "No-side-effect agent test console", "Prompt assembly and readSkill traces are available without production side effects.", "ready"));
  }

  private SurfaceEnvelope accessProfileSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-access-profile", "detail-edit", "Access and profile", actor, correlationId,
        mapOf("recordId", actor.account().accountId(), "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(), "recordKind", "account", "summary", "Browser-safe self-service context. Raw JWTs, WorkOS secrets, and backend permission internals are never exposed.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", actor.account().displayEmail(), "editable", false, "inputType", "email", "disabledReason", "Email is owned by WorkOS/AuthKit identity reconciliation.")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", "profile.read"), "audit", mapOf("lastEventType", "AccessProfileDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-access-profile"))), List.of(openAuditAction()));
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
    events.add(mapOf("eventId", "audit-me-read", "occurredAt", now, "actor", actor.profile().displayName(), "action", "Loaded /api/me and selected AuthContext", "traceId", "trace-access-profile"));
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

  private List<WorkstreamEvent> initialEvents(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var tenantId = actor.selectedContext().tenantId(); var customerId = actor.selectedContext().customerId(); var now = Instant.now().toString();
    return List.of(new WorkstreamEvent("evt-workstream-reconnected-001", "surface.reconnected", tenantId, customerId, ACCESS_PROFILE_AGENT_ID, "surface-access-profile", "detail-edit", "v1", correlationId, List.of("trace-sse-reconnected"), now, 1, mapOf("message", "Realtime stream connected for selected AuthContext.")), new WorkstreamEvent("evt-audit-appended-002", "workstream.item.appended", tenantId, customerId, AUDIT_TRACE_AGENT_ID, "surface-audit-timeline", "audit-timeline", "v1", correlationId, List.of("trace-sse-audit"), now, 2, mapOf("itemId", "item-audit-realtime", "kind", "audit-trace", "title", "Realtime trace available", "body", "SSE delivered a browser-safe audit trace update.", "surfaceId", "surface-audit-timeline", "status", "ready")), new WorkstreamEvent("evt-user-admin-stale-003", "surface.stale", tenantId, customerId, USER_ADMIN_AGENT_ID, "surface-user-admin-dashboard", "dashboard", "v1", correlationId, List.of("trace-sse-stale"), now, 3, mapOf("reason", "User Admin dashboard should refresh after invitation/user changes.")), new WorkstreamEvent("evt-agent-admin-reconnected-004", "surface.reconnected", tenantId, customerId, AGENT_ADMIN_AGENT_ID, "surface-agent-admin-catalog", "list-search", "v1", correlationId, List.of("trace-agent-admin-reconnected"), now, 4, mapOf("message", "Agent Admin surfaces are backed by governed records.")));
  }

  private SurfaceEnvelope surfaceForAction(AuthContextResolver.ResolvedMe actor, String actionId, String correlationId) {
    return switch (actionId) {
      case "action-open-audit-trace", "action-open-trace", "action-open-agent-trace" -> auditTimelineSurface(actor, correlationId);
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
    return List.of(displayListAction(), displayDetailAction(), inviteAction(), deniedReplaceRoleAction(), traceAction(), openAuditAction(), simulatePolicyAction(), commitPolicyAction(), displayAgentCatalogAction(), openAgentDetailAction(), proposePromptDiffAction(), testPromptAction(), approveSkillManifestAction(), simulateToolBoundaryAction(), manageModelRefAction(), openAgentTraceAction()).stream().filter(action -> actionId.equals(action.actionId())).findFirst().orElse(null);
  }

  private SurfaceEnvelope envelope(String id, String type, String title, AuthContextResolver.ResolvedMe actor, String correlationId, Map<String, Object> data, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(id, type, "v1", title, ownerForSurface(id), reusableAgentsForSurface(id), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, List.of("trace-" + id), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential")), data, actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + id, "rel", "deep-link")));
  }

  private String ownerForSurface(String surfaceId) { if (surfaceId.startsWith("surface-access-profile")) return ACCESS_PROFILE_AGENT_ID; if (surfaceId.startsWith("surface-audit")) return AUDIT_TRACE_AGENT_ID; if (surfaceId.startsWith("surface-governance")) return GOVERNANCE_POLICY_AGENT_ID; if (surfaceId.startsWith("surface-agent")) return AGENT_ADMIN_AGENT_ID; return USER_ADMIN_AGENT_ID; }
  private List<String> reusableAgentsForSurface(String surfaceId) { if (surfaceId.startsWith("surface-audit")) return List.of(USER_ADMIN_AGENT_ID, GOVERNANCE_POLICY_AGENT_ID, AGENT_ADMIN_AGENT_ID); if (surfaceId.startsWith("surface-agent")) return List.of(GOVERNANCE_POLICY_AGENT_ID, AUDIT_TRACE_AGENT_ID); if (surfaceId.startsWith("surface-governance")) return List.of(AGENT_ADMIN_AGENT_ID, AUDIT_TRACE_AGENT_ID); return List.of(AUDIT_TRACE_AGENT_ID); }

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
  public record WorkstreamEvent(String eventId, String eventType, String tenantId, String customerId, String functionalAgentId, String surfaceId, String surfaceType, String surfaceVersion, String correlationId, List<String> traceIds, String occurredAt, Integer sequence, Map<String, Object> patch) {}
}
