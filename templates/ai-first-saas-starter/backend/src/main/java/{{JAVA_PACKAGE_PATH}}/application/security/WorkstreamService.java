package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentAdminService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.FailClosedWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.WorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.BehaviorChangeProposal;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Browser-facing agent workstream API adapter for foundation, Agent Admin, and Governance/Policy surfaces. */
public final class WorkstreamService {
  private static final String MY_ACCOUNT_AGENT_ID = "agent-my-account";
  private static final String USER_ADMIN_AGENT_ID = "agent-user-admin";
  private static final String AUDIT_TRACE_AGENT_ID = "agent-audit-trace";
  private static final String GOVERNANCE_POLICY_AGENT_ID = "agent-governance-policy";
  private static final String AGENT_ADMIN_AGENT_ID = "agent-agent-admin";
  private static final String USER_ADMIN_CAPABILITY = "secure-tenant-user-foundation";
  private static final String USERADMIN_VIEW_OVERVIEW = "USERADMIN_VIEW_OVERVIEW";
  private static final String USERADMIN_LIST_INVITATIONS = "USERADMIN_LIST_INVITATIONS";
  private static final String USERADMIN_SEND_INVITATION = "USERADMIN_SEND_INVITATION";
  private static final String USERADMIN_RESEND_INVITATION = "USERADMIN_RESEND_INVITATION";
  private static final String USERADMIN_REVOKE_INVITATION = "USERADMIN_REVOKE_INVITATION";
  private static final String USERADMIN_LIST_MEMBERS = "USERADMIN_LIST_MEMBERS";
  private static final String USERADMIN_LIST_ROLES_CAPABILITIES = "USERADMIN_LIST_ROLES_CAPABILITIES";
  private static final String USERADMIN_PREVIEW_ROLE_CHANGE = "USERADMIN_PREVIEW_ROLE_CHANGE";
  private static final String USERADMIN_CHANGE_MEMBER_ROLES = "USERADMIN_CHANGE_MEMBER_ROLES";
  private static final String USERADMIN_UPDATE_MEMBER_STATUS = "USERADMIN_UPDATE_MEMBER_STATUS";
  private static final String USERADMIN_VIEW_TRACE_REFERENCE = "USERADMIN_VIEW_TRACE_REFERENCE";
  private static final String USERADMIN_ACCESS_REVIEW_START = UserAdminAccessReviewService.START_CAPABILITY;
  private static final String USERADMIN_ACCESS_REVIEW_READ = UserAdminAccessReviewService.READ_CAPABILITY;
  private static final String USERADMIN_ACCESS_REVIEW_CANCEL = UserAdminAccessReviewService.CANCEL_CAPABILITY;
  private static final String USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT = UserAdminAccessReviewService.ACCEPT_RESULT_CAPABILITY;
  private static final String USERADMIN_ACCESS_REVIEW_REJECT_RESULT = UserAdminAccessReviewService.REJECT_RESULT_CAPABILITY;
  private static final String AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY = "agent_admin.list_definitions";
  private static final String AGENT_ADMIN_GET_DEFINITION_CAPABILITY = "agent_admin.get_definition";
  private static final String AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY = "agent_admin.get_prompt_version";
  private static final String AGENT_ADMIN_GET_SKILL_VERSION_CAPABILITY = "agent_admin.get_skill_version";
  private static final String AGENT_ADMIN_GET_REFERENCE_VERSION_CAPABILITY = "agent_admin.get_reference_version";
  private static final String AGENT_ADMIN_GET_MANIFEST_CAPABILITY = "agent_admin.get_manifest";
  private static final String AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY = "agent_admin.get_tool_boundary";
  private static final String AGENT_ADMIN_GET_MODEL_REF_CAPABILITY = "agent_admin.get_model_ref";
  private static final String AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY = "agent_admin.list_seed_material";
  private static final String AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY = "agent_admin.simulate_tool_boundary";
  private static final String AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY = "agent_admin.draft_behavior_change";
  private static final String AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY = "agent_admin.submit_behavior_change_for_review";
  private static final String AGENT_ADMIN_REVIEW_CAPABILITY = "agent_admin.approve_behavior_change";
  private static final String AGENT_ADMIN_REJECT_CAPABILITY = "agent_admin.reject_behavior_change";
  private static final String AGENT_ADMIN_ACTIVATE_CAPABILITY = "agent_admin.activate_behavior_change";
  private static final String AGENT_ADMIN_CANCEL_CAPABILITY = "agent_admin.cancel_behavior_change";
  private static final String AGENT_ADMIN_ROLLBACK_CAPABILITY = "agent_admin.rollback_behavior_change";
  private static final String AGENT_ADMIN_EVALUATE_CAPABILITY = "agent_admin.start_behavior_review_task";
  private static final String MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY = "my_account.view_summary";
  private static final String MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY = "my_account.update_profile_settings";
  private static final String MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY = "my_account.open_authorized_workstream";
  private static final String AUDIT_TRACE_READ_CAPABILITY = "audit.trace.read";
  private static final String AUDIT_TRACE_DASHBOARD_CAPABILITY = "audit.trace.dashboard.read";
  private static final String AUDIT_TRACE_SEARCH_CAPABILITY = "audit.trace.search";
  private static final String AUDIT_TRACE_DETAIL_CAPABILITY = "audit.trace.detail.read";
  private static final String AUDIT_TRACE_TIMELINE_CAPABILITY = "audit.trace.timeline.read";
  private static final String AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY = "audit.trace.failureEvidence.read";
  private static final String AUDIT_TRACE_GUIDE_CAPABILITY = "audit.trace.investigationGuide.read";
  private static final String AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY = "audit.trace.summaryTask.start";
  private static final String GOVERNANCE_POLICY_READ_CAPABILITY = "governance.policy.read";
  private static final String GOVERNANCE_POLICY_SIMULATE_CAPABILITY = "governance.policy.simulate";
  private static final String GOVERNANCE_POLICY_PROPOSE_CAPABILITY = "governance.policy.propose";
  private static final String GOVERNANCE_POLICY_APPROVE_CAPABILITY = "governance.policy.approve";
  private static final String GOVERNANCE_POLICY_ACTIVATE_CAPABILITY = "governance.policy.activate";
  private static final String GOVERNANCE_POLICY_ROLLBACK_CAPABILITY = "governance.policy.rollback";
  private static final String GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY = "governance.policy.analysis.start";
  private final MeService meService;
  private final AuthContextResolver authContextResolver;
  private final UserDirectoryView userDirectoryView;
  private final InvitationView invitationView;
  private final UserAdminService userAdminService;
  private final UserAdminAccessReviewService accessReviewService;
  private final InvitationService invitationService;
  private final AgentBehaviorRepository agentBehaviorRepository;
  private final AgentAdminService agentAdminService;
  private final AgentRuntimeService agentRuntimeService;
  private final AuditTraceService auditTraceService;
  private final GovernancePolicyService governancePolicyService;
  private final WorkstreamAgentRuntimeInvoker workstreamAgentRuntimeInvoker;
  private final WorkstreamLogRepository workstreamLogRepository;
  private final Map<String, CapabilityActionResult> idempotentActionResults = new ConcurrentHashMap<>();

  public WorkstreamService(
      MeService meService,
      AuthContextResolver authContextResolver,
      UserDirectoryView userDirectoryView,
      InvitationView invitationView,
      UserAdminService userAdminService,
      InvitationService invitationService,
      AgentBehaviorRepository agentBehaviorRepository,
      AgentRuntimeService agentRuntimeService) {
    this(meService, authContextResolver, userDirectoryView, invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new FailClosedWorkstreamAgentRuntimeInvoker(), new InMemoryWorkstreamLogRepository());
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
    this(meService, authContextResolver, userDirectoryView, invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new FailClosedWorkstreamAgentRuntimeInvoker(), workstreamLogRepository);
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
      WorkstreamAgentRuntimeInvoker workstreamAgentRuntimeInvoker,
      WorkstreamLogRepository workstreamLogRepository) {
    this.meService = meService;
    this.authContextResolver = authContextResolver;
    this.userDirectoryView = userDirectoryView;
    this.invitationView = invitationView;
    this.userAdminService = userAdminService;
    this.accessReviewService = new UserAdminAccessReviewService(new InMemoryAccessReviewTaskRepository(), userAdminService, Clock.systemUTC());
    this.invitationService = invitationService;
    this.agentBehaviorRepository = agentBehaviorRepository;
    this.agentAdminService = new AgentAdminService(agentBehaviorRepository, authContextResolver);
    this.agentRuntimeService = agentRuntimeService;
    this.workstreamAgentRuntimeInvoker = Objects.requireNonNull(workstreamAgentRuntimeInvoker);
    this.workstreamLogRepository = workstreamLogRepository;
    this.auditTraceService = new AuditTraceService(authContextResolver, new InMemoryAuditTraceRepository(agentRuntimeService, workstreamLogRepository));
    this.governancePolicyService = new GovernancePolicyService(new InMemoryGovernancePolicyRepository(), authContextResolver, Clock.systemUTC());
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
    var dynamic = dynamicSurface(actor, surfaceId, correlationId);
    if (dynamic != null) return dynamic;
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
    if (!isActionCapabilityVisible(actor, action.capabilityId())) throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    if (action.idempotency().required() && (request.idempotencyKey() == null || request.idempotencyKey().isBlank())) return new CapabilityActionResult("validation-error", "This action requires a client-generated idempotency key.", request.correlationId(), List.of("trace-validation-idempotency"), null);
    var actionIdempotencyKey = action.idempotency().required() ? actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + request.actionId() + ":" + request.idempotencyKey() : null;
    if (actionIdempotencyKey != null && idempotentActionResults.containsKey(actionIdempotencyKey)) return idempotentActionResults.get(actionIdempotencyKey);
    if (action.disabled() != null) return new CapabilityActionResult("denied", action.disabled().message(), request.correlationId(), List.of("trace-denied-" + action.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));

    CapabilityActionResult result = null;
    if ("action-invite-user".equals(request.actionId())) {
      var invite = invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
          request.idempotencyKey(), actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId(),
          stringInput(request.input(), "email", "new-user@example.test"), stringInput(request.input(), "displayName", "New User"),
          List.of(FoundationRole.TENANT_EMPLOYEE), Instant.now().plus(7, ChronoUnit.DAYS), "workstream-invite", request.correlationId()));
      result = invitationActionResult("accepted", "Invitation queued by backend-authoritative User Admin capability.", request.correlationId(), invite.invitationId(), actor);
    } else if ("action-useradmin-resend-invitation".equals(request.actionId())) {
      var invite = invitationService.resend(actor, stringInput(request.input(), "invitationId", latestInvitationId(actor)), request.idempotencyKey(), stringInput(request.input(), "reason", "workstream resend"), request.correlationId());
      result = invitationActionResult("accepted", "Invitation resend queued by backend-authoritative User Admin capability.", request.correlationId(), invite.invitationId(), actor);
    } else if ("action-useradmin-revoke-invitation".equals(request.actionId())) {
      var invitationId = stringInput(request.input(), "invitationId", latestInvitationId(actor));
      var alreadyRevoked = invitationStatus(actor, invitationId).equals("REVOKED");
      var invite = invitationService.revoke(actor, invitationId, stringInput(request.input(), "reason", "workstream revoke"), request.correlationId());
      result = invitationActionResult(alreadyRevoked ? "no-op" : "accepted", "Invitation revoke processed by backend-authoritative User Admin capability.", request.correlationId(), invite.invitationId(), actor);
    } else if ("action-useradmin-preview-role-change".equals(request.actionId())) {
      var preview = userAdminService.previewRoleChange(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), rolesInput(request.input()), stringInput(request.input(), "reason", "workstream role preview"), request.correlationId());
      result = new CapabilityActionResult(preview.allowed() ? (preview.noOp() ? "no-op" : "accepted") : "denied", preview.message(), request.correlationId(), List.of(preview.traceId()), roleChangePreviewSurface(actor, preview, request.correlationId()));
    } else if ("action-useradmin-change-member-roles".equals(request.actionId())) {
      var changed = userAdminService.changeMemberRoles(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), rolesInput(request.input()), stringInput(request.input(), "reason", "workstream role change"), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(changed.status(), changed.message(), request.correlationId(), List.of(changed.traceId()), detailSurface(actor, request.correlationId()));
    } else if ("action-useradmin-disable-member".equals(request.actionId()) || "action-useradmin-reactivate-member".equals(request.actionId())) {
      var targetStatus = "action-useradmin-reactivate-member".equals(request.actionId()) ? MembershipStatus.ACTIVE : MembershipStatus.SUSPENDED;
      var changed = userAdminService.updateMemberStatus(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), targetStatus, stringInput(request.input(), "reason", "workstream member status change"), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(changed.status(), changed.message(), request.correlationId(), List.of(changed.traceId()), listSurface(actor, request.correlationId()));
    } else if ("action-useradmin-start-access-review".equals(request.actionId())) {
      var task = accessReviewService.start(actor, request.idempotencyKey(), request.correlationId());
      result = accessReviewActionResult(task, "blocked-runtime", "Access-review task record created; governed worker execution is blocked until provider/runtime configuration is available.", request.correlationId(), actor);
    } else if ("action-useradmin-read-access-review".equals(request.actionId())) {
      var task = accessReviewService.read(actor, stringInput(request.input(), "taskId", ""), request.correlationId());
      result = accessReviewActionResult(task, "accepted", "Access-review task read through backend-authoritative User Admin capability.", request.correlationId(), actor);
    } else if ("action-useradmin-cancel-access-review".equals(request.actionId())) {
      var task = accessReviewService.cancel(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "workstream cancel"), request.correlationId());
      result = accessReviewActionResult(task, "accepted", "Access-review task cancellation recorded; access state unchanged.", request.correlationId(), actor);
    } else if ("action-useradmin-accept-access-review-result".equals(request.actionId())) {
      var task = accessReviewService.acceptResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "accepted by User Admin"), request.correlationId());
      result = accessReviewActionResult(task, "accepted", "Access-review result accepted as human review evidence; access state unchanged.", request.correlationId(), actor);
    } else if ("action-useradmin-reject-access-review-result".equals(request.actionId())) {
      var task = accessReviewService.rejectResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "rejected by User Admin"), request.correlationId());
      result = accessReviewActionResult(task, "accepted", "Access-review result rejected as human review evidence; access state unchanged.", request.correlationId(), actor);
    } else if ("action-propose-prompt-diff".equals(request.actionId())) {
      agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.PROMPT, "Approved revised Agent Admin prompt. Continue to require backend authorization, approval, and trace links.", List.of(), "Agent Admin UI-proposed prompt clarification", request.correlationId()));
    } else if ("action-test-agent-prompt".equals(request.actionId())) {
      agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, request.correlationId(), stringInput(request.input(), "prompt", "Summarize current Agent Admin governed-agent readiness.")));
    } else if ("action-simulate-tool-boundary".equals(request.actionId())) {
      var unsafeGrant = new ToolPermissionBoundary.ToolGrant("email.send", ToolPermissionBoundary.Category.EXTERNAL_SIDE_EFFECT, "tenant.email.send", List.of("execute"), List.of("runtime"), "HIGH", "AUTONOMOUS", true, "full_work_trace");
      agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY, null, List.of(unsafeGrant), "Agent Admin simulation of policy-blocked side-effecting tool grant", request.correlationId()));
    } else if ("action-approve-skill-manifest".equals(request.actionId())) {
      result = new CapabilityActionResult("approval-required", "Skill manifest approval is recorded as a governed review gate; activation must use an approved backend governance command.", request.correlationId(), List.of("trace-skill-manifest-approval-required"), agentSkillManifestSurface(actor, request.correlationId()));
    } else if ("action-update-my-profile".equals(request.actionId()) || "action-update-my-settings".equals(request.actionId())) {
      var update = updateOwnProfileSettings(actor, request);
      result = new CapabilityActionResult(update.changed() ? "accepted" : "no-op", update.changed() ? "My Account profile/settings changes were persisted by the backend." : "My Account profile/settings update was a no-op.", request.correlationId(), List.of("trace-my-account-profile-settings-" + stableSuffix(request.idempotencyKey())), surfaceForAction(authContextResolver.resolveMe(identity, selectedContextId, request.correlationId()), request.actionId(), request.correlationId()));
    } else if ("action-open-user-admin".equals(request.actionId()) || "action-open-agent-admin".equals(request.actionId()) || "action-open-audit-trace".equals(request.actionId()) || "action-open-governance-policy".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", request.actionId(), request.correlationId());
    } else if ("action-audit-trace-dashboard".equals(request.actionId())) {
      result = auditTraceReadResult(actor, "Audit trace dashboard loaded.", request.correlationId(), auditTraceDashboardSurface(actor, request.correlationId()));
    } else if ("action-audit-trace-search".equals(request.actionId())) {
      result = auditTraceReadResult(actor, "Audit trace search completed with scoped, redacted rows.", request.correlationId(), auditTraceSearchSurface(actor, request.input(), request.correlationId()));
    } else if ("action-audit-trace-detail".equals(request.actionId())) {
      result = auditTraceReadResult(actor, "Audit trace detail loaded with browser-safe redaction.", request.correlationId(), auditTraceDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-audit-trace-timeline".equals(request.actionId())) {
      result = auditTraceReadResult(actor, "Correlation timeline assembled from authorized evidence.", request.correlationId(), auditTraceCorrelationTimelineSurface(actor, request.input(), request.correlationId()));
    } else if ("action-audit-trace-failure-evidence".equals(request.actionId())) {
      result = auditTraceReadResult(actor, "Failure evidence loaded without provider secrets or hidden prompt text.", request.correlationId(), auditTraceFailureEvidenceSurface(actor, request.input(), request.correlationId()));
    } else if ("action-audit-trace-investigation-guide".equals(request.actionId())) {
      result = auditTraceReadResult(actor, "Investigation guidance returned backend-authorized next steps.", request.correlationId(), auditTraceInvestigationGuideSurface(actor, request.input(), request.correlationId()));
    } else if ("action-audit-trace-start-summary-task".equals(request.actionId())) {
      result = new CapabilityActionResult("blocked_provider_or_runtime", "Audit summary worker is deferred until a real governed AutonomousAgent task runtime, provider configuration, and tool-boundary lifecycle are implemented; no deterministic summary success was fabricated.", request.correlationId(), List.of("trace-audit-summary-worker-blocked"), auditTraceSummaryTaskBlockedSurface(actor, request.correlationId()));
    } else if ("action-governance-policy-dashboard".equals(request.actionId())) {
      result = governancePolicyReadResult(actor, "Governance/Policy dashboard loaded from GovernancePolicyService for the selected AuthContext.", request.correlationId(), governancePolicyDashboardSurface(actor, request.correlationId()));
    } else if ("action-governance-policy-list".equals(request.actionId())) {
      result = governancePolicyReadResult(actor, "Governance/Policy inventory loaded from GovernancePolicyService for the selected AuthContext.", request.correlationId(), governancePolicyInventorySurface(actor, request.correlationId()));
    } else if ("action-governance-policy-read".equals(request.actionId())) {
      result = governancePolicyReadResult(actor, "Governance/Policy policy detail loaded from GovernancePolicyService for the selected AuthContext.", request.correlationId(), governancePolicyDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-governance-policy-draft-proposal".equals(request.actionId())) {
      var draft = governancePolicyService.draftProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(draft.status(), draft.message(), request.correlationId(), draft.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), draft.surface(), List.of(governanceSubmitProposalAction(), governanceSimulateProposalAction(), governanceDecideProposalAction(), governanceActivateProposalAction(), openAuditAction())));
    } else if ("action-governance-policy-submit-proposal".equals(request.actionId())) {
      var submit = governancePolicyService.submitProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(submit.status(), submit.message(), request.correlationId(), submit.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), submit.surface(), List.of(governanceSubmitProposalAction(), governanceSimulateProposalAction(), governanceDecideProposalAction(), governanceActivateProposalAction(), openAuditAction())));
    } else if ("action-governance-policy-simulate".equals(request.actionId()) || "action-simulate-policy".equals(request.actionId())) {
      var simulation = governancePolicyService.simulateProposal(actor, request.input(), request.correlationId());
      result = new CapabilityActionResult(simulation.status(), simulation.message(), request.correlationId(), simulation.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), simulation.surface(), List.of(governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), openAuditAction())));
    } else if ("action-governance-policy-decide".equals(request.actionId())) {
      var decision = governancePolicyService.decideProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(decision.status(), decision.message(), request.correlationId(), decision.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), decision.surface(), List.of(governanceActivateProposalAction(), governanceRollbackPolicyAction(), openAuditAction())));
    } else if ("action-governance-policy-activate".equals(request.actionId()) || "action-commit-policy".equals(request.actionId())) {
      var activation = governancePolicyService.activateProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(activation.status(), activation.message(), request.correlationId(), activation.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), activation.surface(), List.of(governanceRollbackPolicyAction(), openAuditAction())));
    } else if ("action-governance-policy-rollback".equals(request.actionId())) {
      var rollback = governancePolicyService.rollbackProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(rollback.status(), rollback.message(), request.correlationId(), rollback.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), rollback.surface(), List.of(governanceListPoliciesAction(), openAuditAction())));
    } else if ("action-governance-policy-start-impact-analysis".equals(request.actionId())) {
      result = new CapabilityActionResult("blocked_provider_or_runtime", "Governance/Policy impact analysis is intentionally not ready for successful execution: deterministic policy/proposal/simulation foundations exist, but this slice has not selected a real model-backed AutonomousAgent task lifecycle, provider, and tool-boundary path; no fake progress is returned.", request.correlationId(), List.of("trace-governance-policy-analysis-blocked"), governancePolicyImpactAnalysisBlockedSurface(actor, request.correlationId()));
    }
    if (result == null) result = new CapabilityActionResult("accepted", action.label() + " accepted by backend-authoritative starter capability.", request.correlationId(), List.of("trace-" + request.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));
    if (actionIdempotencyKey != null) idempotentActionResults.put(actionIdempotencyKey, result);
    return result;
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

    var runtime = workstreamAgentRuntimeInvoker.invokeWorkstreamAgent(new AgentRuntimeService.RuntimeInvocationRequest(
        actor.selectedContext().tenantId(), request.functionalAgentId(), actor.selectedContext(), requestCorrelationId, request.prompt()));
    var responseSeed = firstNonBlank(request.idempotencyKey(), requestCorrelationId, request.functionalAgentId());
    var userItemId = "item-message-user-" + stableSuffix(responseSeed + ":user");
    var agentItemId = "item-message-agent-" + stableSuffix(responseSeed + ":agent");
    var surfaceId = "surface-message-" + stableSuffix(responseSeed + ":markdown_response");
    var now = Instant.now().toString();
    var traceIds = runtime.traceIds().isEmpty() ? List.of("trace-workstream-message-" + stableSuffix(responseSeed + ":trace")) : runtime.traceIds();
    var userItem = new WorkstreamItem(userItemId, request.functionalAgentId(), "user-request", now, requestCorrelationId, traceIds, null, null, request.prompt(), "ready");
    var surface = runtime.decision() == AgentRuntimeTrace.Decision.ALLOWED
        ? markdownResponseSurface(surfaceId, agentItemId, functionalAgent, actor, requestCorrelationId, traceIds, runtime.markdown())
        : blockedAgentSystemMessageSurface(surfaceId, agentItemId, functionalAgent, actor, requestCorrelationId, traceIds, runtime);
    var agentItemBody = runtime.decision() == AgentRuntimeTrace.Decision.ALLOWED ? null : "Model-backed workstream response blocked by governed runtime/provider boundary. Open the system_message for safe recovery steps.";
    var agentItemKind = runtime.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "markdown_response" : "system_message";
    var agentItem = new WorkstreamItem(agentItemId, request.functionalAgentId(), agentItemKind, now, requestCorrelationId, traceIds, surface.surfaceId(), functionalAgent.label(), agentItemBody, runtime.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "ready" : "blocked");
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
        .orElse(new MeResponse.FunctionalAgentSummary(functionalAgentId, title.replace(" v0 response", ""), "Five core v0 starter workstream.", "workstream", new MeResponse.WorkstreamIconDescriptor(functionalAgentId, title.replace(" v0 response", ""), "workstream", "workstream", "accent-workstream", "Open " + title.replace(" v0 response", "") + " workstream", "Open " + title.replace(" v0 response", "") + " workstream", null), "markdown_response", List.of(), "visible", null));
    return markdownResponseSurface(surfaceId, workstreamEntryId, agent, actor, correlationId, List.of("trace-" + surfaceId), markdown);
  }

  private SurfaceEnvelope myAccountDashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_VIEW_SUMMARY", "browser-safe account/context summary", correlationId);
    return envelope("surface-my-account-dashboard", "dashboard", "My Account", actor, correlationId,
        mapOf("cards", List.of(mapOf("cardId", "card-my-profile", "label", "Profile", "value", actor.profile().displayName(), "severity", "info"), mapOf("cardId", "card-my-settings", "label", "Settings", "value", actor.settings().uiMode().name().toLowerCase(), "severity", "info"), mapOf("cardId", "card-current-context", "label", "Selected context", "value", actor.selectedContext().tenantId(), "severity", "info")), "sections", List.of(mapOf("sectionId", "self-service", "label", "Self-service", "summary", "Profile and settings updates are backend-validated self-service actions only."), mapOf("sectionId", "security-boundary", "label", "Security boundary", "summary", "Roles, memberships, support access, and tenant administration stay in governed admin workstreams.")), "nextSteps", myAccountNextSteps(actor)),
        List.of(showProfileAction(), showSettingsAction(), signOutAction(), openUserAdminAction(), openAgentAdminAction(), openAuditAction(), openGovernancePolicyAction()));
  }

  private SurfaceEnvelope myProfileSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-profile", "detail-edit", "User profile", actor, correlationId,
        mapOf("recordId", actor.account().accountId() + "-profile", "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(), "recordKind", "profile", "summary", "Current signed-in user profile. Administrative role and membership changes are intentionally not editable here.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", actor.account().displayEmail(), "editable", false, "inputType", "email", "disabledReason", "Email is owned by WorkOS/AuthKit identity reconciliation."), mapOf("fieldId", "locale", "label", "Locale", "value", "en-US", "editable", false, "inputType", "select", "disabledReason", "Locale changes are deferred beyond My Account v0."), mapOf("fieldId", "timeZone", "label", "Time zone", "value", "America/New_York", "editable", false, "inputType", "text", "disabledReason", "Time zone changes are deferred beyond My Account v0.")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY), "audit", mapOf("lastEventType", "UserProfileDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-profile"))), List.of(updateProfileAction(), openAuditAction()));
  }

  private SurfaceEnvelope mySettingsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-settings", "detail-edit", "User settings", actor, correlationId,
        mapOf("recordId", actor.account().accountId() + "-settings", "recordLabel", actor.profile().displayName() + " settings", "recordKind", "settings", "summary", "Current signed-in user preferences for the workstream shell and notifications.", "fields", List.of(mapOf("fieldId", "preferredColorMode", "label", "Color mode", "value", actor.settings().uiMode().name().toLowerCase(), "editable", true, "inputType", "select"), mapOf("fieldId", "notificationDigest", "label", "Notification digest", "value", "daily", "editable", false, "inputType", "select", "disabledReason", "Notification digest is deferred beyond My Account v0."), mapOf("fieldId", "composerDensity", "label", "Composer density", "value", "comfortable", "editable", false, "inputType", "select", "disabledReason", "Composer density is deferred beyond My Account v0.")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY), "audit", mapOf("lastEventType", "UserSettingsDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-settings"))), List.of(updateSettingsAction(), openAuditAction()));
  }

  private SurfaceEnvelope dashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_VIEW_OVERVIEW, "user_admin.dashboard.v1", correlationId);
    var users = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var invites = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var failedInvites = invites.stream().filter(invite -> invite.deliveryStatus().name().equals("FAILED")).count();
    return envelope("surface-user-admin-dashboard", "dashboard", "User Admin command center", actor, correlationId,
        mapOf("surfaceContract", "user_admin.dashboard.v1", "readiness", mapOf("directory", "backend-derived", "invitationOutbox", "backend-derived", "accessReviewWorker", "blocked_provider_or_runtime"), "cards", List.of(mapOf("cardId", "card-pending-invitations", "label", "Pending invitations", "value", invites.size(), "severity", invites.isEmpty() ? "info" : "warning"), mapOf("cardId", "card-active-users", "label", "Active users", "value", users.size(), "severity", "info"), mapOf("cardId", "card-failed-invitations", "label", "Failed invitation delivery", "value", failedInvites, "severity", failedInvites == 0 ? "info" : "warning"), mapOf("cardId", "card-access-review", "label", "Access review items", "value", 0, "severity", "blocked_provider_or_runtime")), "attentionItems", List.of(mapOf("itemId", "invitation-delivery", "label", "Invitation delivery", "status", failedInvites == 0 ? "clear" : "needs-review"), mapOf("itemId", "trace-useradmin-dashboard", "label", "Trace", "status", "available")), "capabilityIds", List.of(USERADMIN_VIEW_OVERVIEW, USERADMIN_LIST_MEMBERS, USERADMIN_LIST_INVITATIONS, USERADMIN_SEND_INVITATION, USERADMIN_RESEND_INVITATION, USERADMIN_REVOKE_INVITATION, USERADMIN_LIST_ROLES_CAPABILITIES, USERADMIN_UPDATE_MEMBER_STATUS, USERADMIN_ACCESS_REVIEW_START, USERADMIN_ACCESS_REVIEW_READ, USERADMIN_ACCESS_REVIEW_CANCEL, USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT, USERADMIN_ACCESS_REVIEW_REJECT_RESULT)),
        List.of(displayListAction(), inviteAction(), resendInvitationAction(), revokeInvitationAction(), updateMemberStatusAction(), previewRoleChangeAction(), startAccessReviewAction(), traceAction()));
  }

  private SurfaceEnvelope listSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_MEMBERS, "user_admin.member_directory.v1", correlationId);
    var rows = new ArrayList<Map<String, Object>>();
    for (var user : userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())) rows.add(mapOf("id", user.accountId(), "rowType", "user-directory", "email", user.accountId(), "displayName", user.displayName(), "role", user.roles().toString(), "status", user.membershipStatus().name().toLowerCase(), "traceId", "trace-useradmin-user-" + stableSuffix(user.accountId())));
    for (var invite : invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())) rows.add(invitationRow(invite));
    return envelope("surface-user-admin-list", "list-search", "Users, invitations, and memberships", actor, correlationId, mapOf("surfaceContracts", List.of("user_admin.member_directory.v1", "user_admin.invitation_panel.v1"), "query", "scope:" + actor.selectedContext().scopeType().name().toLowerCase(), "rows", rows, "pageInfo", mapOf("totalKnownCount", rows.size()), "mobileFallback", "table-to-card", "capabilityIds", List.of(USERADMIN_LIST_MEMBERS, USERADMIN_LIST_INVITATIONS, USERADMIN_UPDATE_MEMBER_STATUS), "systemStates", List.of("system_message", "last-admin", "self-disable", "idempotency")), List.of(displayDetailAction(), inviteAction(), resendInvitationAction(), revokeInvitationAction(), updateMemberStatusAction(), previewRoleChangeAction(), traceAction()));
  }

  private SurfaceEnvelope invitationPanelSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_INVITATIONS, "user_admin.invitation_panel.v1", correlationId);
    var invitations = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream().map(this::invitationRow).toList();
    return envelope("surface-user-admin-invitation-panel", "list-search", "Invitation lifecycle", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_panel.v1", "query", "invitations:scope:" + actor.selectedContext().scopeType().name().toLowerCase(), "rows", invitations, "pageInfo", mapOf("totalKnownCount", invitations.size()), "emptyCopy", "Invite a teammate with backend authorization, idempotency, audit, outbox, and trace evidence.", "capabilityIds", List.of(USERADMIN_LIST_INVITATIONS, USERADMIN_SEND_INVITATION, USERADMIN_RESEND_INVITATION, USERADMIN_REVOKE_INVITATION), "systemStates", List.of("system_message", "validation-error", "blocked_provider_or_runtime")),
        List.of(inviteAction(), resendInvitationAction(), revokeInvitationAction(), traceAction()));
  }

  private SurfaceEnvelope detailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-user-admin-detail-admin", "detail-edit", "Tenant Admin account detail", actor, correlationId, mapOf("recordId", actor.account().accountId(), "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(), "recordKind", "account", "summary", "Scoped detail/edit surface backed by UserAdminService authorization, idempotency, and audit semantics.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"), mapOf("fieldId", "membershipStatus", "label", "Membership status", "value", "active", "editable", false, "inputType", "status", "disabledReason", "Status changes are checked by backend last-admin and self-disable policy."), mapOf("fieldId", "role", "label", "Membership role", "value", actor.selectedContext().roles().toString(), "editable", false, "inputType", "select", "disabledReason", "Role changes are checked by backend policy.")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", USERADMIN_CHANGE_MEMBER_ROLES), "audit", mapOf("lastEventType", "UserAdminDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-user-admin-detail"))), List.of(updateMemberStatusAction(), previewRoleChangeAction(), changeMemberRolesAction(), deniedReplaceRoleAction(), traceAction()));
  }

  private SurfaceEnvelope roleChangePreviewSurface(AuthContextResolver.ResolvedMe actor, UserAdminService.RoleChangePreview preview, String correlationId) {
    return envelope("surface-user-admin-role-change-preview", "detail-edit", "Role change preview", actor, correlationId,
        mapOf("surfaceContract", "user_admin.role_change_preview.v1", "status", preview.allowed() ? preview.noOp() ? "no-op" : "ready" : "denied", "message", preview.message(), "capabilityDelta", preview.capabilityDelta(), "affectedWorkstreams", preview.affectedWorkstreams(), "policyHints", preview.policyHints(), "lastAdminImpact", preview.lastAdminImpact(), "traceLinks", List.of(preview.traceId()), "systemStates", List.of("system_message", "last-admin", "self-disable", "idempotency")),
        List.of(changeMemberRolesAction(), traceAction()));
  }

  private SurfaceEnvelope auditTimelineSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var now = Instant.now().toString();
    var events = new ArrayList<Map<String, Object>>();
    events.add(mapOf("eventId", "audit-me-read", "occurredAt", now, "actor", actor.profile().displayName(), "action", "Loaded /api/me and selected AuthContext", "traceId", "trace-my-account"));
    for (var trace : agentRuntimeService.traces()) events.add(mapOf("eventId", trace.traceId(), "occurredAt", trace.occurredAt().toString(), "actor", trace.actorId(), "action", trace.traceType() + " " + trace.decision() + " · " + trace.safeSummary(), "traceId", trace.traceId()));
    return envelope("surface-audit-timeline", "audit-timeline", "Audit and trace timeline", actor, correlationId, mapOf("events", events), List.of(openAuditAction()));
  }

  private CapabilityActionResult auditTraceReadResult(AuthContextResolver.ResolvedMe actor, String message, String correlationId, SurfaceEnvelope surface) {
    var status = "validation-error".equals(surface.surfaceType()) ? "validation-error" : "accepted";
    return new CapabilityActionResult(status, message, correlationId, surface.traceIds(), surface);
  }

  private SurfaceEnvelope auditTraceDashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var surface = auditTraceService.dashboard(actor, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction()));
  }

  private SurfaceEnvelope auditTraceSearchSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.search(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction()));
  }

  private SurfaceEnvelope auditTraceDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.detail(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction()));
  }

  private SurfaceEnvelope auditTraceCorrelationTimelineSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.timeline(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDetailAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction()));
  }

  private SurfaceEnvelope auditTraceFailureEvidenceSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.failureEvidence(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceTimelineAction(), auditTraceInvestigationGuideAction()));
  }

  private SurfaceEnvelope auditTraceInvestigationGuideSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.investigationGuide(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceSummaryTaskBlockedAction()));
  }

  private SurfaceEnvelope auditTraceSummaryTaskBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "audit summary worker readiness blocked_provider_or_runtime", correlationId);
    return envelope("surface-audit-trace-summary-task", "workflow-status", "Audit summary worker readiness", actor, correlationId,
        mapOf("surfaceContract", "audit.trace.summaryTask.v1", "workflowId", "audit-trace-summary-task", "status", "blocked_provider_or_runtime", "summary", "Scheduled audit summary worker remains deferred: deterministic trace search/detail/timeline and AuditTraceAgent evidence tooling exist, but there is no real governed AutonomousAgent task runtime selected for this worker yet.", "traceIds", List.of("trace-audit-summary-worker-blocked"), "requiredCapabilityId", AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "providerFailures", List.of("blocked_provider_or_runtime"), "blockers", List.of(mapOf("code", "blocked_provider_or_runtime", "message", "Implement a future bounded AutonomousAgent mini-project before enabling model-backed scheduled audit summaries.")), "noDirectMutation", true, "safety", "Audit summary output cannot mutate traces, policy, users, behavior records, authorization, redaction, or tenant filtering; no model-less successful worker result is exposed."),
        List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceSummaryTaskBlockedAction()));
  }

  private SurfaceEnvelope auditTraceEnvelope(AuthContextResolver.ResolvedMe actor, String correlationId, AuditTraceService.SurfaceData surface, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(surface.surfaceId(), surface.surfaceType(), "v1", surface.title(), AUDIT_TRACE_AGENT_ID, reusableAgentsForSurface(surface.surfaceId()), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, surface.traceIds(), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload")), surface.data(), actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + surface.surfaceId(), "rel", "deep-link")));
  }

  private SurfaceEnvelope accessReviewSurface(AuthContextResolver.ResolvedMe actor, AccessReviewTask task, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_ACCESS_REVIEW_READ, "user_admin.access_review_task.v1", correlationId);
    return envelope("surface-user-admin-access-review", "workflow-status", "User Admin access review", actor, correlationId,
        mapOf("surfaceContract", "user_admin.access_review_task.v1", "workflowId", task.taskId(), "taskId", task.taskId(), "status", accessReviewStatus(task), "initiatingCapabilityId", USERADMIN_ACCESS_REVIEW_START, "scope", mapOf("scopeType", task.scopeType().name(), "tenantId", task.tenantId(), "customerId", task.customerId()), "progress", mapOf("percent", task.progressPercent(), "summary", task.summary()), "blockers", task.blockerCode() == null ? List.of() : List.of(mapOf("code", task.blockerCode(), "message", "Governed AutonomousAgent provider/runtime is not configured; the starter fails closed instead of returning model-less access-review recommendations.")), "evidenceRefs", task.evidenceRefs(), "recommendations", task.recommendationRefs(), "resultReviewState", task.decision() == null ? "pending_worker_result" : task.decision(), "providerFailures", task.blockerCode() == null ? List.of() : List.of("blocked_provider_or_runtime"), "traceIds", task.traceIds(), "noDirectMutation", true, "safety", "access-review output cannot directly mutate memberships, invitations, roles, capabilities, or authorization state"),
        List.of(startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), acceptAccessReviewResultAction(), rejectAccessReviewResultAction(), traceAction()));
  }

  private SurfaceEnvelope accessReviewBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_ACCESS_REVIEW_START, "access-review autonomous task unavailable", correlationId);
    return envelope("surface-user-admin-access-review", "workflow-status", "User Admin access review", actor, correlationId,
        mapOf("surfaceContract", "user_admin.access_review_task.v1", "workflowId", "user-admin-access-review", "status", "blocked_provider_or_runtime", "summary", "Start creates a durable task record, but worker execution remains blocked until governed AutonomousAgent provider/runtime configuration is enabled.", "traceIds", List.of("trace-useradmin-access-review-blocked"), "requiredCapabilityId", USERADMIN_ACCESS_REVIEW_START, "noDirectMutation", true),
        List.of(startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), traceAction()));
  }

  private String accessReviewStatus(AccessReviewTask task) {
    return task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : task.status().name().toLowerCase();
  }

  private CapabilityActionResult governancePolicyReadResult(AuthContextResolver.ResolvedMe actor, String message, String correlationId, SurfaceEnvelope surface) {
    return new CapabilityActionResult("accepted", message, correlationId, surface.traceIds(), surface);
  }

  private SurfaceEnvelope governancePolicyEnvelope(AuthContextResolver.ResolvedMe actor, String correlationId, GovernancePolicyService.SurfaceData surface, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(surface.surfaceId(), surface.surfaceType(), "v1", surface.title(), GOVERNANCE_POLICY_AGENT_ID, reusableAgentsForSurface(surface.surfaceId()), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, surface.traceIds(), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload", "providerSecret")), surface.data(), actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + surface.surfaceId(), "rel", "deep-link")));
  }

  private CapabilityActionResult governancePolicyDraftProposal(AuthContextResolver.ResolvedMe actor, CapabilityActionRequest request) {
    var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(
        actor.selectedContext().tenantId(),
        AgentBehaviorSeedLoader.GOVERNANCE_POLICY_AGENT_ID,
        actor.selectedContext(),
        BehaviorChangeProposal.TargetArtifact.PROMPT,
        stringInput(request.input(), "proposedContent", "Draft Governance/Policy prompt clarification. Preserve backend authorization, approval, and trace requirements."),
        List.of(),
        stringInput(request.input(), "rationale", "Governance/Policy workstream draft proposal"),
        request.correlationId()));
    return new CapabilityActionResult("accepted", "Draft policy-change proposal created without activating authority.", request.correlationId(), List.of(proposal.correlationId()), governancePolicyProposalSurface(actor, request.correlationId(), proposal.status().name().toLowerCase(), "Draft proposal is inert until review, simulation, approval, and activation."));
  }

  private SurfaceEnvelope governancePolicySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return governancePolicyDashboardSurface(actor, correlationId);
  }

  private SurfaceEnvelope governancePolicyDashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.dashboard(actor, correlationId), List.of(governanceDashboardAction(), governanceListPoliciesAction(), governanceDraftProposalAction(), governanceSimulateProposalAction(), governanceStartImpactAnalysisAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicyInventorySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.inventory(actor, correlationId), List.of(governanceReadPolicyAction(), governanceDraftProposalAction(), governanceSimulateProposalAction(), openAuditAction()));
  }

  private Map<String, Object> governancePolicyRow(String id, String name, String status, String source, List<String> capabilityIds, String traceId) {
    return mapOf("policyId", id, "name", name, "type", "governance", "status", status, "affectedCapabilityIds", capabilityIds, "sourceArtifact", source, "lastChangeTraceId", traceId);
  }

  private SurfaceEnvelope governancePolicyDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.detail(actor, input, correlationId), List.of(governanceListPoliciesAction(), governanceDraftProposalAction(), governanceSimulateProposalAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicyProposalSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String state, String summary) {
    var draft = governancePolicyService.draftProposal(actor, mapOf("rationale", summary, "title", "Governance policy proposal"), "surface-fallback-" + stableSuffix(correlationId + state), correlationId);
    return governancePolicyEnvelope(actor, correlationId, draft.surface(), List.of(governanceSubmitProposalAction(), governanceSimulateProposalAction(), governanceDecideProposalAction(), governanceActivateProposalAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicySimulationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateGovernancePolicyInputScope(actor, input, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, GOVERNANCE_POLICY_SIMULATE_CAPABILITY, "proposal simulation", correlationId);
    return envelope("surface-governance-policy-simulation", "governance-diff", "Policy simulation", actor, correlationId,
        mapOf("simulationId", "sim-" + stableSuffix(correlationId), "proposalId", stringInput(input, "proposalId", "starter-governance-policy-review"), "affectedCapabilities", List.of(GOVERNANCE_POLICY_APPROVE_CAPABILITY, GOVERNANCE_POLICY_ACTIVATE_CAPABILITY, "agent.references.read"), "expectedDenials", List.of("model cannot self-approve", "prompt text cannot grant tool access", "cross-tenant evidence is omitted"), "expectedAllows", List.of("authorized read-only policy inventory", "authorized simulation evidence"), "warnings", List.of("Activation still requires approved proposal and idempotency key."), "confidence", "bounded-starter", "evidenceTraceLinks", List.of("trace-governance-policy-simulation-" + stableSuffix(correlationId))),
        List.of(governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicyDecisionSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return envelope("surface-governance-policy-decision", "decision", "Governance decision", actor, correlationId,
        mapOf("decisionId", "decision-" + stableSuffix(correlationId), "decision", stringInput(input, "decision", "approve"), "actor", actor.account().accountId(), "authorityBasis", GOVERNANCE_POLICY_APPROVE_CAPABILITY, "rationale", stringInput(input, "rationale", "Human governance review recorded."), "result", "approval-recorded-activation-still-separate", "auditCorrelationId", correlationId),
        List.of(governanceActivateProposalAction(), governanceRollbackPolicyAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicyActivationBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-governance-policy-activation-blocked", "decision", "Policy activation blocked", actor, correlationId,
        mapOf("status", "approval-required", "requiredCapabilityId", GOVERNANCE_POLICY_ACTIVATE_CAPABILITY, "safeReason", "Approved proposal, current version, activation target, rollback reference, and backend authority are required.", "sideEffect", "none", "traceLinks", List.of("trace-governance-policy-activation-blocked-" + stableSuffix(correlationId))),
        List.of(governanceSimulateProposalAction(), governanceDecideProposalAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicyRollbackBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-governance-policy-rollback-blocked", "decision", "Policy rollback blocked", actor, correlationId,
        mapOf("status", "blocked-runtime", "requiredCapabilityId", GOVERNANCE_POLICY_ROLLBACK_CAPABILITY, "safeReason", "No activated starter policy change with rollback metadata exists for this request.", "sideEffect", "none", "traceLinks", List.of("trace-governance-policy-rollback-blocked-" + stableSuffix(correlationId))),
        List.of(governanceListPoliciesAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicyImpactAnalysisBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY, "impact-analysis readiness decision blocked_provider_or_runtime", correlationId);
    return envelope("surface-governance-policy-impact-analysis", "workflow-status", "Policy impact analysis readiness", actor, correlationId,
        mapOf("surfaceContract", "governance.policy.analysis_task.v1", "workflowId", "governance-policy-impact-analysis", "taskId", null, "status", "blocked_provider_or_runtime", "summary", "Policy-impact analysis remains a justified durable worker candidate, but this starter slice exposes only a fail-closed readiness surface until a real model-backed AutonomousAgent task lifecycle, provider configuration, and ToolPermissionBoundary are implemented.", "readinessDecision", "not_ready_real_worker_required", "workerSelection", mapOf("justified", true, "reason", "impact analysis spans proposal state, capability inventory, trace evidence, model/provider work, progress, cancellation, and human result review", "currentRuntime", "blocked_provider_or_runtime"), "providerFailures", List.of("blocked_provider_or_runtime"), "blockers", List.of(mapOf("code", "autonomous_agent_runtime_not_selected", "message", "No concrete Governance/Policy AutonomousAgent task runtime has been implemented for impact analysis."), mapOf("code", "provider_or_model_not_configured_for_worker", "message", "The starter must fail closed rather than return deterministic/model-less successful analysis."), mapOf("code", "tool_boundary_not_granted", "message", "Future worker must use read-only evidence tools and cannot gain mutation authority.")), "evidenceRefs", List.of("GovernancePolicyService.proposal", "GovernancePolicyService.simulation", "governancePolicyEvidence.read", "audit.trace.read"), "forbiddenEffects", List.of("approve", "activate", "rollback", "mutate policy", "mutate users", "mutate agent behavior", "change provider config", "bypass deterministic authorization"), "traceIds", List.of("trace-governance-policy-analysis-blocked"), "requiredCapabilityId", GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY, "readCapabilityId", "governance.policy.analysis.read", "noDirectMutation", true, "noFakeProgress", true, "followUpRecommendation", "Append a separate bounded worker mini-project only after selecting and validating a real provider-backed AutonomousAgent/internal task runtime."),
        List.of(governanceStartImpactAnalysisAction(), openAuditAction()));
  }

  private void validateGovernancePolicyInputScope(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    if (input instanceof Map<?, ?> map) {
      var tenantId = map.get("tenantId") instanceof String value ? value : null;
      var customerId = map.get("customerId") instanceof String value ? value : null;
      if (tenantId != null && !tenantId.isBlank() && !tenantId.equals(actor.selectedContext().tenantId())) {
        authContextResolver.appendDeniedTrace(actor, "GOVERNANCE_POLICY_SCOPE_DENIED", "tenant-mismatch", correlationId);
        throw new AuthorizationException(403, "GOVERNANCE_POLICY_TENANT_FORBIDDEN");
      }
      if (customerId != null && actor.selectedContext().customerId() != null && !customerId.equals(actor.selectedContext().customerId())) {
        authContextResolver.appendDeniedTrace(actor, "GOVERNANCE_POLICY_SCOPE_DENIED", "customer-mismatch", correlationId);
        throw new AuthorizationException(403, "GOVERNANCE_POLICY_CUSTOMER_FORBIDDEN");
      }
    }
  }

  private SurfaceEnvelope agentAdminCatalogSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-admin-catalog", "list-search", "Agent Admin catalog", actor, correlationId, agentAdminService.catalog(actor, correlationId), List.of(displayAgentCatalogAction(), openAgentDetailAction(), listAgentSeedMaterialAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-admin-detail", "detail-edit", "Agent Admin readiness detail", actor, correlationId, agentAdminService.definitionDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId), List.of(proposePromptDiffAction(), testPromptAction(), manageModelRefAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentPromptGovernanceSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-prompt-governance", "governance-diff", "Prompt governance review", actor, correlationId, agentAdminService.promptDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId), List.of(proposePromptDiffAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentSkillManifestSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-skill-manifest-diff", "governance-diff", "Skill and reference manifest detail", actor, correlationId, agentAdminService.manifestDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId), List.of(approveSkillManifestAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentToolBoundarySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-tool-boundary-diff", "governance-diff", "Tool boundary detail", actor, correlationId, agentAdminService.toolBoundaryDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId), List.of(simulateToolBoundaryAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentModelRefsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-model-refs", "detail-edit", "Agent model refs", actor, correlationId, agentAdminService.modelRefDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId), List.of(manageModelRefAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentSeedMaterialSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-seed-material", "list-search", "Agent seed material", actor, correlationId, agentAdminService.seedMaterialDetail(actor, correlationId), List.of(openAgentTraceAction()));
  }

  private SurfaceEnvelope agentTestConsoleSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, correlationId, "No-side-effect Agent Admin test console"));
    return envelope("surface-agent-test-console", "workflow-status", "No-side-effect agent test console", actor, correlationId, mapOf("workflowId", "agent-runtime-test", "status", "completed", "steps", List.of(mapOf("stepId", "prompt-assembly", "label", "PromptAssemblyTrace", "status", prompt.decision().name()), mapOf("stepId", "skill-load", "label", "SkillLoadTrace", "status", "available-through-readSkill(skillId)"), mapOf("stepId", "agent-work", "label", "AgentWorkTrace", "status", "no production side effects")), "traceIds", List.of(prompt.traceId(), "trace-agent-work-88")), List.of(testPromptAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentBehaviorProposalSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var proposals = agentRuntimeService.proposals().stream().map(proposal -> mapOf("id", proposal.proposalId(), "target", proposal.targetArtifact().name(), "status", proposal.status().name(), "risk", proposal.riskClassification(), "reviewedBy", proposal.reviewedByAccountId(), "denial", proposal.reviewReason())).toList();
    return envelope("surface-agent-behavior-proposal", "decision", "Behavior proposal decision", actor, correlationId, mapOf("decisionId", "decision-behavior-proposal", "recommendation", "Approve only validated, non-authority-expanding behavior changes.", "riskScore", 42, "confidenceScore", 88, "evidence", List.of(mapOf("evidenceId", "proposal-count", "label", "Proposal queue", "summary", proposals.size() + " proposals tracked"), mapOf("evidenceId", "approval-boundary", "label", "Approval boundary", "summary", "approval-required for activation")), "proposals", proposals), List.of(proposePromptDiffAction(), submitBehaviorChangeAction(), approveSkillManifestAction(), rejectBehaviorChangeAction(), activateBehaviorChangeAction(), cancelBehaviorChangeAction(), rollbackBehaviorChangeAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentAdminTraceSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-admin-trace", "audit-timeline", "Agent Admin traces", actor, correlationId, mapOf("events", List.of(mapOf("eventId", "trace-prompt-assembly-42", "occurredAt", Instant.now().toString(), "actor", "AgentRuntimeService", "action", "PromptAssemblyTrace emitted for deterministic prompt assembly", "traceId", "trace-prompt-assembly-42"), mapOf("eventId", "trace-skill-load-17", "occurredAt", Instant.now().toString(), "actor", "readSkill(skillId)", "action", "SkillLoadTrace emitted for allowed or denied skill loads", "traceId", "trace-skill-load-17"), mapOf("eventId", "trace-agent-work-88", "occurredAt", Instant.now().toString(), "actor", "No-side-effect agent test console", "action", "AgentWorkTrace links test-mode output to governed prompt and skills", "traceId", "trace-agent-work-88"))), List.of(openAgentTraceAction()));
  }

  private SurfaceEnvelope blockedAgentSystemMessageSurface(String surfaceId, String workstreamEntryId, MeResponse.FunctionalAgentSummary agent, AuthContextResolver.ResolvedMe actor, String correlationId, List<String> traceIds, AgentRuntimeService.RuntimeInvocationResult runtime) {
    var safeSummary = firstNonBlank(runtime.safeErrorSummary(), "Provider or governed runtime configuration is unavailable.");
    var safeCode = firstNonBlank(runtime.safeErrorCode(), "blocked_provider_or_runtime");
    return new SurfaceEnvelope(surfaceId, "system_message", "v1", agent.label() + " unavailable", agent.functionalAgentId(), List.of("agent-audit-trace"),
        mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()),
        correlationId, traceIds, Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential", "providerCredentialValue")),
        mapOf("status", "blocked_provider_or_runtime", "severity", "warning", "title", agent.label() + " unavailable", "summary", "Model-backed workstream execution was blocked before a response was produced.", "safeErrorCode", safeCode, "message", "Model-backed workstream execution was blocked before a response was produced. " + safeCode + ": " + safeSummary, "recoverySteps", List.of("Verify model provider configuration and active ModelConfigRef.", "Review PromptAssemblyTrace and AgentWorkTrace for this correlation id.", "Retry after backend configuration is restored; no deterministic canned guidance was returned."), "workstreamEntryId", workstreamEntryId, "producingAgentId", agent.functionalAgentId(), "capabilityId", agent.requiredCapabilityIds().isEmpty() ? USER_ADMIN_CAPABILITY : agent.requiredCapabilityIds().get(0), "sourceRefs", List.of(mapOf("refType", "trace", "refId", traceIds.get(0), "label", "Blocked runtime trace")), "safety", mapOf("sanitized", true, "redactionNote", "Provider secrets, raw JWTs, invitation tokens, and hidden capabilities are never included."), "trace", mapOf("correlationId", correlationId, "traceIds", traceIds)),
        List.of(openAuditAction()), List.of(mapOf("label", "Open trace", "href", "/ui?traceId=" + traceIds.get(0), "rel", "trace")));
  }

  private SurfaceEnvelope markdownResponseSurface(String surfaceId, String workstreamEntryId, MeResponse.FunctionalAgentSummary agent, AuthContextResolver.ResolvedMe actor, String correlationId, List<String> traceIds, String markdown) {
    return new SurfaceEnvelope(surfaceId, "markdown_response", "v1", agent.label(), agent.functionalAgentId(), List.of("agent-audit-trace"),
        mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()),
        correlationId, traceIds, Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential", "providerCredentialValue")),
        mapOf("markdown", markdown, "title", agent.label() + " response", "summary", "Backend-authorized starter response for " + agent.label() + ".", "workstreamEntryId", workstreamEntryId, "producingAgentId", agent.functionalAgentId(), "sourceRefs", List.of(mapOf("refType", "capability", "refId", agent.requiredCapabilityIds().isEmpty() ? MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY : agent.requiredCapabilityIds().get(0), "label", "Backend capability boundary"), mapOf("refType", "trace", "refId", traceIds.get(0), "label", "Workstream message trace")), "sections", List.of(mapOf("anchor", "starter-scope", "title", "Starter scope"), mapOf("anchor", "safe-next-steps", "title", "Safe next steps")), "safety", mapOf("sanitized", false, "blockedUnsafeLinks", 0, "blockedRawHtml", false, "redactionNote", "Provider secrets, raw JWTs, invitation tokens, and hidden capabilities are never included."), "trace", mapOf("correlationId", correlationId, "traceIds", traceIds)),
        List.of(openAuditAction()), List.of(mapOf("label", "Open trace", "href", "/ui?traceId=" + traceIds.get(0), "rel", "trace")));
  }

  private List<WorkstreamEvent> initialEvents(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var tenantId = actor.selectedContext().tenantId(); var customerId = actor.selectedContext().customerId(); var now = Instant.now().toString();
    return List.of(new WorkstreamEvent("evt-workstream-reconnected-001", "surface.reconnected", tenantId, customerId, MY_ACCOUNT_AGENT_ID, "surface-v0-my-account-markdown", "markdown_response", "v1", correlationId, List.of("trace-sse-reconnected"), now, 1, mapOf("message", "Realtime stream connected for selected AuthContext.")), new WorkstreamEvent("evt-audit-appended-002", "workstream.item.appended", tenantId, customerId, AUDIT_TRACE_AGENT_ID, "surface-v0-audit-trace-markdown", "markdown_response", "v1", correlationId, List.of("trace-sse-audit"), now, 2, mapOf("itemId", "item-v0-audit-trace-markdown", "kind", "markdown_response", "title", "Audit/Trace v0 response", "body", "SSE delivered a browser-safe five-core v0 trace update.", "surfaceId", "surface-v0-audit-trace-markdown", "status", "ready")), new WorkstreamEvent("evt-user-admin-stale-003", "surface.stale", tenantId, customerId, USER_ADMIN_AGENT_ID, "surface-v0-user-admin-markdown", "markdown_response", "v1", correlationId, List.of("trace-sse-stale"), now, 3, mapOf("reason", "User Admin markdown_response should refresh after invitation/user changes.")), new WorkstreamEvent("evt-agent-admin-reconnected-004", "surface.reconnected", tenantId, customerId, AGENT_ADMIN_AGENT_ID, "surface-v0-agent-admin-markdown", "markdown_response", "v1", correlationId, List.of("trace-agent-admin-reconnected"), now, 4, mapOf("message", "Agent Admin v0 markdown_response is backed by governed records.")));
  }

  private SurfaceEnvelope dynamicSurface(AuthContextResolver.ResolvedMe actor, String surfaceId, String correlationId) {
    return switch (surfaceId) {
      case "surface-user-admin-dashboard" -> dashboardSurface(actor, correlationId);
      case "surface-user-admin-list" -> listSurface(actor, correlationId);
      case "surface-user-admin-invitation-panel" -> invitationPanelSurface(actor, correlationId);
      case "surface-user-admin-detail-admin" -> detailSurface(actor, correlationId);
      case "surface-user-admin-access-review" -> accessReviewBlockedSurface(actor, correlationId);
      case "surface-agent-admin-catalog" -> agentAdminCatalogSurface(actor, correlationId);
      case "surface-agent-admin-detail" -> agentAdminDetailSurface(actor, correlationId);
      case "surface-agent-prompt-governance" -> agentPromptGovernanceSurface(actor, correlationId);
      case "surface-agent-skill-manifest-diff" -> agentSkillManifestSurface(actor, correlationId);
      case "surface-agent-tool-boundary-diff" -> agentToolBoundarySurface(actor, correlationId);
      case "surface-agent-model-refs" -> agentModelRefsSurface(actor, correlationId);
      case "surface-agent-seed-material" -> agentSeedMaterialSurface(actor, correlationId);
      case "surface-agent-test-console" -> agentTestConsoleSurface(actor, correlationId);
      case "surface-agent-behavior-proposal" -> agentBehaviorProposalSurface(actor, correlationId);
      case "surface-agent-admin-trace" -> agentAdminTraceSurface(actor, correlationId);
      case "surface-audit-trace-summary-task" -> auditTraceSummaryTaskBlockedSurface(actor, correlationId);
      case "surface-governance-policy-dashboard" -> governancePolicyDashboardSurface(actor, correlationId);
      case "surface-governance-policy-inventory" -> governancePolicyInventorySurface(actor, correlationId);
      case "surface-governance-policy-detail" -> governancePolicyDetailSurface(actor, null, correlationId);
      case "surface-governance-policy-proposal" -> governancePolicyProposalSurface(actor, correlationId, "draft", "Draft proposal is inert until review, simulation, approval, and activation.");
      case "surface-governance-policy-simulation" -> governancePolicySimulationSurface(actor, null, correlationId);
      case "surface-governance-policy-decision" -> governancePolicyDecisionSurface(actor, null, correlationId);
      case "surface-governance-policy-activation-blocked" -> governancePolicyActivationBlockedSurface(actor, correlationId);
      case "surface-governance-policy-rollback-blocked" -> governancePolicyRollbackBlockedSurface(actor, correlationId);
      case "surface-governance-policy-impact-analysis" -> governancePolicyImpactAnalysisBlockedSurface(actor, correlationId);
      default -> null;
    };
  }

  private CapabilityActionResult invitationActionResult(String status, String message, String correlationId, String invitationId, AuthContextResolver.ResolvedMe actor) {
    var traceId = "trace-useradmin-invitation-" + stableSuffix(invitationId + ":" + correlationId);
    return new CapabilityActionResult(status, message, correlationId, List.of(traceId), invitationPanelSurface(actor, correlationId));
  }

  private CapabilityActionResult accessReviewActionResult(AccessReviewTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), accessReviewSurface(actor, task, correlationId));
  }

  private Map<String, Object> invitationRow(InvitationView.InvitationRow invite) {
    return mapOf("id", invite.invitationId(), "rowType", "invitation-queue", "email", invite.targetEmail(), "displayName", invite.targetEmail(), "role", invite.requestedRoles().toString(), "status", invite.status().name().toLowerCase(), "delivery", invite.deliveryStatus().name().toLowerCase(), "deliveryAttempts", invite.deliveryAttempts(), "resendCount", invite.resendCount(), "lastDeliveryErrorSummary", invite.lastDeliveryErrorSummary(), "expiresAt", invite.expiresAt().toString(), "acceptedAt", invite.acceptedAt() == null ? null : invite.acceptedAt().toString(), "revokedAt", invite.revokedAt() == null ? null : invite.revokedAt().toString(), "canResend", invite.canResend(), "canRevoke", invite.canRevoke(), "traceId", "trace-useradmin-invitation-" + stableSuffix(invite.invitationId()));
  }

  private String latestInvitationId(AuthContextResolver.ResolvedMe actor) {
    return invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .findFirst()
        .map(InvitationView.InvitationRow::invitationId)
        .orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
  }

  private String invitationStatus(AuthContextResolver.ResolvedMe actor, String invitationId) {
    return invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(invite -> invitationId.equals(invite.invitationId()))
        .findFirst()
        .map(invite -> invite.status().name())
        .orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
  }

  private SurfaceEnvelope surfaceForAction(AuthContextResolver.ResolvedMe actor, String actionId, String correlationId) {
    return switch (actionId) {
      case "action-open-audit-trace", "action-open-trace", "action-open-agent-trace", "action-audit-trace-dashboard" -> auditTraceDashboardSurface(actor, correlationId);
      case "action-audit-trace-search" -> auditTraceSearchSurface(actor, null, correlationId);
      case "action-audit-trace-detail" -> auditTraceDetailSurface(actor, null, correlationId);
      case "action-audit-trace-timeline" -> auditTraceCorrelationTimelineSurface(actor, null, correlationId);
      case "action-audit-trace-failure-evidence" -> auditTraceFailureEvidenceSurface(actor, null, correlationId);
      case "action-audit-trace-investigation-guide" -> auditTraceInvestigationGuideSurface(actor, null, correlationId);
      case "action-audit-trace-start-summary-task" -> auditTraceSummaryTaskBlockedSurface(actor, correlationId);
      case "action-show-my-profile", "action-update-my-profile" -> myProfileSurface(actor, correlationId);
      case "action-show-my-settings", "action-update-my-settings" -> mySettingsSurface(actor, correlationId);
      case "action-sign-out" -> myAccountDashboardSurface(actor, correlationId);
      case "action-open-user-admin" -> dashboardSurface(actor, correlationId);
      case "action-open-agent-admin" -> agentAdminCatalogSurface(actor, correlationId);
      case "action-open-governance-policy", "action-governance-policy-dashboard" -> governancePolicySurface(actor, correlationId);
      case "action-governance-policy-list" -> governancePolicyInventorySurface(actor, correlationId);
      case "action-governance-policy-read" -> governancePolicyDetailSurface(actor, null, correlationId);
      case "action-governance-policy-draft-proposal", "action-governance-policy-submit-proposal" -> governancePolicyProposalSurface(actor, correlationId, "draft", "Draft proposal is inert until review, simulation, approval, and activation.");
      case "action-governance-policy-simulate", "action-simulate-policy" -> governancePolicySimulationSurface(actor, null, correlationId);
      case "action-governance-policy-decide" -> governancePolicyDecisionSurface(actor, null, correlationId);
      case "action-governance-policy-activate", "action-commit-policy" -> governancePolicyActivationBlockedSurface(actor, correlationId);
      case "action-governance-policy-rollback" -> governancePolicyRollbackBlockedSurface(actor, correlationId);
      case "action-governance-policy-start-impact-analysis" -> governancePolicyImpactAnalysisBlockedSurface(actor, correlationId);
      case "action-display-agent-catalog" -> agentAdminCatalogSurface(actor, correlationId);
      case "action-open-agent-detail" -> agentAdminDetailSurface(actor, correlationId);
      case "action-propose-prompt-diff" -> agentPromptGovernanceSurface(actor, correlationId);
      case "action-test-agent-prompt" -> agentTestConsoleSurface(actor, correlationId);
      case "action-approve-skill-manifest" -> agentSkillManifestSurface(actor, correlationId);
      case "action-simulate-tool-boundary" -> agentToolBoundarySurface(actor, correlationId);
      case "action-manage-model-ref" -> agentModelRefsSurface(actor, correlationId);
      case "action-list-agent-seed-material" -> agentSeedMaterialSurface(actor, correlationId);
      case "action-display-user-detail", "action-replace-membership-role", "action-useradmin-preview-role-change", "action-useradmin-change-member-roles" -> detailSurface(actor, correlationId);
      case "action-useradmin-disable-member", "action-useradmin-reactivate-member" -> listSurface(actor, correlationId);
      case "action-useradmin-start-access-review", "action-useradmin-read-access-review", "action-useradmin-cancel-access-review", "action-useradmin-accept-access-review-result", "action-useradmin-reject-access-review-result" -> accessReviewBlockedSurface(actor, correlationId);
      case "action-useradmin-resend-invitation", "action-useradmin-revoke-invitation", "action-invite-user" -> invitationPanelSurface(actor, correlationId);
      case "action-display-user-list" -> listSurface(actor, correlationId);
      default -> dashboardSurface(actor, correlationId);
    };
  }

  private SurfaceAction actionById(String actionId) {
    return List.of(showProfileAction(), showSettingsAction(), updateProfileAction(), updateSettingsAction(), signOutAction(), openUserAdminAction(), openAgentAdminAction(), openGovernancePolicyAction(), displayListAction(), displayDetailAction(), inviteAction(), resendInvitationAction(), revokeInvitationAction(), updateMemberStatusAction(), reactivateMemberStatusAction(), previewRoleChangeAction(), changeMemberRolesAction(), startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), acceptAccessReviewResultAction(), rejectAccessReviewResultAction(), deniedReplaceRoleAction(), traceAction(), openAuditAction(), auditTraceSearchAction(), auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceSummaryTaskBlockedAction(), governanceDashboardAction(), governanceListPoliciesAction(), governanceReadPolicyAction(), governanceDraftProposalAction(), governanceSubmitProposalAction(), governanceSimulateProposalAction(), governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), governanceStartImpactAnalysisAction(), simulatePolicyAction(), commitPolicyAction(), displayAgentCatalogAction(), openAgentDetailAction(), proposePromptDiffAction(), testPromptAction(), approveSkillManifestAction(), submitBehaviorChangeAction(), rejectBehaviorChangeAction(), activateBehaviorChangeAction(), cancelBehaviorChangeAction(), rollbackBehaviorChangeAction(), simulateToolBoundaryAction(), manageModelRefAction(), listAgentSeedMaterialAction(), openAgentTraceAction()).stream().filter(action -> actionId.equals(action.actionId())).findFirst().orElse(null);
  }

  private SurfaceEnvelope envelope(String id, String type, String title, AuthContextResolver.ResolvedMe actor, String correlationId, Map<String, Object> data, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(id, type, "v1", title, ownerForSurface(id), reusableAgentsForSurface(id), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, List.of("trace-" + id), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential")), data, actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + id, "rel", "deep-link")));
  }

  private String ownerForSurface(String surfaceId) { if (surfaceId.startsWith("surface-my-")) return MY_ACCOUNT_AGENT_ID; if (surfaceId.startsWith("surface-audit")) return AUDIT_TRACE_AGENT_ID; if (surfaceId.startsWith("surface-governance")) return GOVERNANCE_POLICY_AGENT_ID; if (surfaceId.startsWith("surface-agent")) return AGENT_ADMIN_AGENT_ID; return USER_ADMIN_AGENT_ID; }
  private List<String> reusableAgentsForSurface(String surfaceId) { if (surfaceId.startsWith("surface-audit")) return List.of(USER_ADMIN_AGENT_ID, GOVERNANCE_POLICY_AGENT_ID, AGENT_ADMIN_AGENT_ID, MY_ACCOUNT_AGENT_ID); if (surfaceId.startsWith("surface-my-")) return List.of(AUDIT_TRACE_AGENT_ID); if (surfaceId.startsWith("surface-agent")) return List.of(GOVERNANCE_POLICY_AGENT_ID, AUDIT_TRACE_AGENT_ID); if (surfaceId.startsWith("surface-governance")) return List.of(AGENT_ADMIN_AGENT_ID, AUDIT_TRACE_AGENT_ID); return List.of(AUDIT_TRACE_AGENT_ID); }

  private SurfaceAction showProfileAction() { return new SurfaceAction("action-show-my-profile", "Show user profile", "read", MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-profile", "inline"), new Audit("UserProfileDisplayed", true)); }
  private SurfaceAction showSettingsAction() { return new SurfaceAction("action-show-my-settings", "Show user settings", "read", MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-settings", "inline"), new Audit("UserSettingsDisplayed", true)); }
  private SurfaceAction updateProfileAction() { return new SurfaceAction("action-update-my-profile", "Save profile changes", "command", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY, "schema.my-account.profile.update.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-my-profile", "inline"), new Audit("UserProfileUpdateRequested", true)); }
  private SurfaceAction updateSettingsAction() { return new SurfaceAction("action-update-my-settings", "Save settings changes", "command", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY, "schema.my-account.settings.update.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-my-settings", "inline"), new Audit("UserSettingsUpdateRequested", true)); }
  private SurfaceAction signOutAction() { return new SurfaceAction("action-sign-out", "Sign out", "command", MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, null, true, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-dashboard", "inline"), new Audit("SessionSignOutRequested", true)); }
  private SurfaceAction openUserAdminAction() { return new SurfaceAction("action-open-user-admin", "Open User Admin", "shell", MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "schema.my-account.open-workstream.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-list", "deep-link"), new Audit("MyAccountOpenUserAdminRequested", true)); }
  private SurfaceAction openAgentAdminAction() { return new SurfaceAction("action-open-agent-admin", "Open Agent Admin", "shell", MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "schema.my-account.open-workstream.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "deep-link"), new Audit("MyAccountOpenAgentAdminRequested", true)); }
  private SurfaceAction openGovernancePolicyAction() { return new SurfaceAction("action-open-governance-policy", "Open Governance/Policy", "shell", MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "schema.my-account.open-workstream.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-dashboard", "deep-link"), new Audit("MyAccountOpenGovernancePolicyRequested", true)); }

  private List<Map<String, Object>> myAccountNextSteps(AuthContextResolver.ResolvedMe actor) {
    var agents = MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities());
    return agents.stream()
        .filter(agent -> !MY_ACCOUNT_AGENT_ID.equals(agent.functionalAgentId()))
        .map(agent -> mapOf("workstreamId", agent.functionalAgentId(), "label", agent.label(), "allowed", "visible".equals(agent.availability()), "blockedReason", agent.deniedReason(), "capabilityIds", agent.requiredCapabilityIds()))
        .toList();
  }

  private AuthContextResolver.ProfileSettingsUpdateResult updateOwnProfileSettings(AuthContextResolver.ResolvedMe actor, CapabilityActionRequest request) {
    if (!(request.input() instanceof Map<?, ?> input)) throw new AuthorizationException(400, "MY_ACCOUNT_UPDATE_INPUT_REQUIRED");
    var unsupported = input.keySet().stream()
        .map(String::valueOf)
        .filter(key -> !List.of("displayName", "preferredColorMode").contains(key))
        .sorted()
        .toList();
    if (!unsupported.isEmpty()) throw new AuthorizationException(403, "MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD:" + String.join(",", unsupported));
    var displayName = input.get("displayName") instanceof String value ? value : null;
    UserSettings.UiMode uiMode = null;
    if (input.get("preferredColorMode") instanceof String value && !value.isBlank()) {
      uiMode = switch (value.toLowerCase()) {
        case "light" -> UserSettings.UiMode.LIGHT;
        case "dark" -> UserSettings.UiMode.DARK;
        default -> throw new AuthorizationException(400, "MY_ACCOUNT_INVALID_COLOR_MODE");
      };
    }
    return authContextResolver.updateOwnProfileSettings(actor, displayName, uiMode, request.idempotencyKey(), request.correlationId());
  }

  private SurfaceAction displayListAction() { return new SurfaceAction("action-display-user-list", "Display user list view", "read", USERADMIN_LIST_MEMBERS, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("UserAdminListDisplayed", true)); }
  private SurfaceAction displayDetailAction() { return new SurfaceAction("action-display-user-detail", "Display user account detail", "read", USERADMIN_LIST_MEMBERS, "schema.user-admin.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("UserAdminDetailDisplayed", true)); }
  private SurfaceAction inviteAction() { return new SurfaceAction("action-invite-user", "Invite user", "command", USERADMIN_SEND_INVITATION, "schema.invitation.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-panel", "inline"), new Audit("InvitationRequested", true)); }
  private SurfaceAction resendInvitationAction() { return new SurfaceAction("action-useradmin-resend-invitation", "Resend invitation", "command", USERADMIN_RESEND_INVITATION, "schema.invitation.resend.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-panel", "inline"), new Audit("InvitationResendRequested", true)); }
  private SurfaceAction revokeInvitationAction() { return new SurfaceAction("action-useradmin-revoke-invitation", "Revoke invitation", "command", USERADMIN_REVOKE_INVITATION, "schema.invitation.revoke.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-panel", "inline"), new Audit("InvitationRevokeRequested", true)); }
  private SurfaceAction updateMemberStatusAction() { return new SurfaceAction("action-useradmin-disable-member", "Disable member", "command", USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.member-status.update.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("UserAdminMemberStatusChanged", true)); }
  private SurfaceAction reactivateMemberStatusAction() { return new SurfaceAction("action-useradmin-reactivate-member", "Reactivate member", "command", USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.member-status.update.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-list", "inline"), new Audit("UserAdminMemberStatusChanged", true)); }
  private SurfaceAction previewRoleChangeAction() { return new SurfaceAction("action-useradmin-preview-role-change", "Preview role change", "proposal", USERADMIN_PREVIEW_ROLE_CHANGE, "schema.user-admin.role-change.preview.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-role-change-preview", "inline"), new Audit("UserAdminRoleChangePreviewed", true)); }
  private SurfaceAction changeMemberRolesAction() { return new SurfaceAction("action-useradmin-change-member-roles", "Apply role change", "command", USERADMIN_CHANGE_MEMBER_ROLES, "schema.user-admin.role-change.apply.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("UserAdminMemberRolesChanged", true)); }
  private SurfaceAction startAccessReviewAction() { return new SurfaceAction("action-useradmin-start-access-review", "Start access review", "workflow", USERADMIN_ACCESS_REVIEW_START, "schema.user-admin.access-review.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review", "inline"), new Audit("UserAdminAccessReviewStarted", true)); }
  private SurfaceAction readAccessReviewAction() { return new SurfaceAction("action-useradmin-read-access-review", "Read access review", "read", USERADMIN_ACCESS_REVIEW_READ, "schema.user-admin.access-review.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-access-review", "inline"), new Audit("UserAdminAccessReviewRead", true)); }
  private SurfaceAction cancelAccessReviewAction() { return new SurfaceAction("action-useradmin-cancel-access-review", "Cancel access review", "command", USERADMIN_ACCESS_REVIEW_CANCEL, "schema.user-admin.access-review.cancel.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review", "inline"), new Audit("UserAdminAccessReviewCancelled", true)); }
  private SurfaceAction acceptAccessReviewResultAction() { return new SurfaceAction("action-useradmin-accept-access-review-result", "Accept access review result", "approval", USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT, "schema.user-admin.access-review.accept-result.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review", "inline"), new Audit("UserAdminAccessReviewResultAccepted", true)); }
  private SurfaceAction rejectAccessReviewResultAction() { return new SurfaceAction("action-useradmin-reject-access-review-result", "Reject access review result", "approval", USERADMIN_ACCESS_REVIEW_REJECT_RESULT, "schema.user-admin.access-review.reject-result.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review", "inline"), new Audit("UserAdminAccessReviewResultRejected", true)); }
  private SurfaceAction deniedReplaceRoleAction() { return new SurfaceAction("action-replace-membership-role", "Replace membership role", "command", USER_ADMIN_CAPABILITY, "schema.membership.role.replace.v1", true, false, new DisabledReason("LAST_ADMIN_DENIED", "Backend authorization denied this action: cannot remove the last tenant admin without an approved replacement."), new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-user-admin-detail-admin", "inline"), new Audit("MembershipRoleReplacementDenied", true)); }
  private SurfaceAction traceAction() { return new SurfaceAction("action-open-trace", "Open trace", "trace", "my_account.view_own_trace_refs", null, false, false, null, new Idempotency(false, null), null, new Audit("TraceOpened", true)); }
  private SurfaceAction openAuditAction() { return new SurfaceAction("action-open-audit-trace", "Open audit timeline", "trace", "my_account.view_own_trace_refs", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-dashboard", "inline"), new Audit("AuditTimelineOpened", true)); }
  private SurfaceAction auditTraceSearchAction() { return new SurfaceAction("action-audit-trace-search", "Search traces", "read", AUDIT_TRACE_SEARCH_CAPABILITY, "schema.audit-trace.search.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-search", "inline"), new Audit("AuditTraceSearchRequested", true)); }
  private SurfaceAction auditTraceDetailAction() { return new SurfaceAction("action-audit-trace-detail", "Open trace detail", "read", AUDIT_TRACE_DETAIL_CAPABILITY, "schema.audit-trace.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-detail", "inline"), new Audit("AuditTraceDetailRequested", true)); }
  private SurfaceAction auditTraceTimelineAction() { return new SurfaceAction("action-audit-trace-timeline", "Open correlation timeline", "read", AUDIT_TRACE_TIMELINE_CAPABILITY, "schema.audit-trace.timeline.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-timeline", "inline"), new Audit("AuditTraceTimelineRequested", true)); }
  private SurfaceAction auditTraceFailureEvidenceAction() { return new SurfaceAction("action-audit-trace-failure-evidence", "Open failure evidence", "read", AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY, "schema.audit-trace.failure-evidence.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-failure-evidence", "inline"), new Audit("AuditTraceFailureEvidenceRequested", true)); }
  private SurfaceAction auditTraceInvestigationGuideAction() { return new SurfaceAction("action-audit-trace-investigation-guide", "Show investigation guidance", "read", AUDIT_TRACE_GUIDE_CAPABILITY, "schema.audit-trace.investigation-guide.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-investigation-guide", "inline"), new Audit("AuditTraceInvestigationGuideRequested", true)); }
  private SurfaceAction auditTraceSummaryTaskBlockedAction() { return new SurfaceAction("action-audit-trace-start-summary-task", "Start audit summary task", "workflow", AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "schema.audit-trace.summary-task.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface("workflow-status", "surface-audit-trace-summary-task", "inline"), new Audit("AuditTraceSummaryTaskStartBlocked", true)); }
  private SurfaceAction governanceDashboardAction() { return new SurfaceAction("action-governance-policy-dashboard", "Open governance dashboard", "read", GOVERNANCE_POLICY_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-dashboard", "inline"), new Audit("GovernancePolicyDashboardDisplayed", true)); }
  private SurfaceAction governanceListPoliciesAction() { return new SurfaceAction("action-governance-policy-list", "List policy inventory", "read", GOVERNANCE_POLICY_READ_CAPABILITY, "schema.governance-policy.inventory.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-inventory", "inline"), new Audit("GovernancePolicyInventoryListed", true)); }
  private SurfaceAction governanceReadPolicyAction() { return new SurfaceAction("action-governance-policy-read", "Read policy evidence", "read", GOVERNANCE_POLICY_READ_CAPABILITY, "schema.governance-policy.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-detail", "inline"), new Audit("GovernancePolicyDetailRead", true)); }
  private SurfaceAction governanceDraftProposalAction() { return new SurfaceAction("action-governance-policy-draft-proposal", "Draft policy proposal", "proposal", GOVERNANCE_POLICY_PROPOSE_CAPABILITY, "schema.governance-policy.proposal.draft.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-proposal", "inline"), new Audit("GovernancePolicyProposalDrafted", true)); }
  private SurfaceAction governanceSubmitProposalAction() { return new SurfaceAction("action-governance-policy-submit-proposal", "Submit proposal for review", "proposal", GOVERNANCE_POLICY_PROPOSE_CAPABILITY, "schema.governance-policy.proposal.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-proposal", "inline"), new Audit("GovernancePolicyProposalSubmitted", true)); }
  private SurfaceAction governanceSimulateProposalAction() { return new SurfaceAction("action-governance-policy-simulate", "Simulate policy impact", "governance", GOVERNANCE_POLICY_SIMULATE_CAPABILITY, "schema.governance-policy.simulate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-simulation", "inline"), new Audit("GovernancePolicySimulationRequested", true)); }
  private SurfaceAction governanceDecideProposalAction() { return new SurfaceAction("action-governance-policy-decide", "Approve or reject proposal", "approval", GOVERNANCE_POLICY_APPROVE_CAPABILITY, "schema.governance-policy.proposal.decide.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-decision", "inline"), new Audit("GovernancePolicyDecisionRecorded", true)); }
  private SurfaceAction governanceActivateProposalAction() { return new SurfaceAction("action-governance-policy-activate", "Activate approved policy", "command", GOVERNANCE_POLICY_ACTIVATE_CAPABILITY, "schema.governance-policy.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-activation-blocked", "inline"), new Audit("GovernancePolicyActivationRequested", true)); }
  private SurfaceAction governanceRollbackPolicyAction() { return new SurfaceAction("action-governance-policy-rollback", "Roll back policy change", "command", GOVERNANCE_POLICY_ROLLBACK_CAPABILITY, "schema.governance-policy.rollback.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-rollback-blocked", "inline"), new Audit("GovernancePolicyRollbackRequested", true)); }
  private SurfaceAction governanceStartImpactAnalysisAction() { return new SurfaceAction("action-governance-policy-start-impact-analysis", "Start impact analysis", "workflow", GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY, "schema.governance-policy.analysis.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-impact-analysis", "inline"), new Audit("GovernancePolicyImpactAnalysisStartBlocked", true)); }
  private SurfaceAction simulatePolicyAction() { return new SurfaceAction("action-simulate-policy", "Run governance simulation", "governance", GOVERNANCE_POLICY_SIMULATE_CAPABILITY, "schema.policy.simulate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-simulation", "inline"), new Audit("PolicySimulationRequested", true)); }
  private SurfaceAction commitPolicyAction() { return new SurfaceAction("action-commit-policy", "Approve governance change", "approval", GOVERNANCE_POLICY_ACTIVATE_CAPABILITY, "schema.policy.commit.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-activation-blocked", "inline"), new Audit("PolicyCommitApprovalRequested", true)); }
  private SurfaceAction displayAgentCatalogAction() { return new SurfaceAction("action-display-agent-catalog", "Display agent catalog", "read", AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction openAgentDetailAction() { return new SurfaceAction("action-open-agent-detail", "Open agent readiness detail", "read", AGENT_ADMIN_GET_DEFINITION_CAPABILITY, "schema.agent-definition.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction proposePromptDiffAction() { return new SurfaceAction("action-propose-prompt-diff", "Propose prompt diff", "proposal", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.prompt-version.proposal.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-prompt-governance", "side-panel"), new Audit("PromptVersionDraftProposed", true)); }
  private SurfaceAction testPromptAction() { return new SurfaceAction("action-test-agent-prompt", "Run no-side-effect prompt test", "workflow", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-runtime.test.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface("workflow-status", null, "inline"), new Audit("AgentRuntimeTestRequested", true)); }
  private SurfaceAction approveSkillManifestAction() { return new SurfaceAction("action-approve-skill-manifest", "Approve manifest review", "approval", AGENT_ADMIN_REVIEW_CAPABILITY, null, true, true, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-agent-skill-manifest-diff", "inline"), new Audit("AgentSkillManifestApproved", true)); }
  private SurfaceAction submitBehaviorChangeAction() { return new SurfaceAction("action-submit-behavior-change", "Submit behavior change for review", "proposal", AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.behavior-change.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeSubmitted", true)); }
  private SurfaceAction rejectBehaviorChangeAction() { return new SurfaceAction("action-reject-behavior-change", "Reject behavior change", "approval", AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.behavior-change.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeRejected", true)); }
  private SurfaceAction activateBehaviorChangeAction() { return new SurfaceAction("action-activate-behavior-change", "Activate approved behavior change", "command", AGENT_ADMIN_ACTIVATE_CAPABILITY, "schema.agent-admin.behavior-change.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeActivated", true)); }
  private SurfaceAction cancelBehaviorChangeAction() { return new SurfaceAction("action-cancel-behavior-change", "Cancel behavior change", "command", AGENT_ADMIN_CANCEL_CAPABILITY, "schema.agent-admin.behavior-change.cancel.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeCancelled", true)); }
  private SurfaceAction rollbackBehaviorChangeAction() { return new SurfaceAction("action-rollback-behavior-change", "Rollback activated behavior change", "command", AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.behavior-change.rollback.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeRolledBack", true)); }
  private SurfaceAction simulateToolBoundaryAction() { return new SurfaceAction("action-simulate-tool-boundary", "Simulate tool boundary change", "governance", AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, "schema.tool-boundary.simulation.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("ToolBoundarySimulationRequested", true)); }
  private SurfaceAction manageModelRefAction() { return new SurfaceAction("action-manage-model-ref", "Request model ref change", "proposal", AGENT_ADMIN_ACTIVATE_CAPABILITY, null, false, false, new DisabledReason("MODEL_POLICY_DENIED", "This starter denies switching to a disabled provider alias; provider secrets remain redacted."), new Idempotency(true, "client-generated"), new ResultSurface("decision", null, "inline"), new Audit("AgentModelRefChangeDenied", true)); }
  private SurfaceAction listAgentSeedMaterialAction() { return new SurfaceAction("action-list-agent-seed-material", "List seed material", "read", AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialListed", true)); }
  private SurfaceAction openAgentTraceAction() { return new SurfaceAction("action-open-agent-trace", "Open agent work trace", "trace", "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentWorkTraceOpened", true)); }

  private boolean isActionCapabilityVisible(AuthContextResolver.ResolvedMe actor, String capabilityId) {
    if (actor.selectedContext().capabilities().contains(capabilityId) || USER_ADMIN_CAPABILITY.equals(capabilityId)) return true;
    if (capabilityId != null && capabilityId.startsWith("audit.trace.") && actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) return true;
    if (capabilityId != null && capabilityId.startsWith("governance.policy.") && actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_READ_CAPABILITY)) return actor.selectedContext().capabilities().contains(capabilityId);
    if (capabilityId != null && capabilityId.startsWith("agent_admin.")) return actor.selectedContext().capabilities().contains(capabilityId);
    return capabilityId != null && (capabilityId.startsWith("USERADMIN_") || capabilityId.startsWith("user_admin.")) && actor.selectedContext().capabilities().contains(USER_ADMIN_CAPABILITY);
  }

  private static String stringInput(Object input, String key, String fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return value; return fallback; }
  private static int intInput(Object input, String key, int fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof Number value) return value.intValue(); return fallback; }
  private static List<FoundationRole> rolesInput(Object input) { if (input instanceof Map<?, ?> map && map.get("roles") instanceof List<?> roles && !roles.isEmpty()) return roles.stream().map(String::valueOf).map(FoundationRole::valueOf).toList(); return List.of(FoundationRole.TENANT_EMPLOYEE); }
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
