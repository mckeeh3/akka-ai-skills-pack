package ai.first.application.coreapp.workstream;

import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.foundation.invitation.InvitationStatus;
import ai.first.application.coreapp.agentadmin.AgentAdminService;
import ai.first.application.coreapp.governance.GovernancePolicyImpactService;
import ai.first.application.coreapp.myaccount.MyAccountPersonalAttentionDigestService;
import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.application.coreapp.useradmin.AccessReviewAutonomousAgentRuntime;
import ai.first.application.coreapp.useradmin.AccessReviewTaskRepository;
import ai.first.application.coreapp.useradmin.FailClosedAccessReviewAutonomousAgentRuntime;
import ai.first.application.coreapp.useradmin.UserAdminAccessReviewService;
import ai.first.application.coreapp.useradmin.UserAdminService;
import ai.first.application.coreapp.useradmin.UserDirectoryView;
import ai.first.application.foundation.agent.AgentBehaviorRepository;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.WorkstreamAgentRuntimeInvoker;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.agent.BehaviorChangeProposal;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.foundation.attention.AttentionSourceRef;
import ai.first.domain.foundation.attention.AttentionSurfaceRef;
import ai.first.domain.foundation.email.EmailNotificationPreference;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import ai.first.domain.coreapp.myaccount.MyAccountNotificationCenter;
import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationChannel;
import ai.first.domain.foundation.notification.NotificationChannelRegistryEntry;
import ai.first.domain.foundation.notification.NotificationDeliveryAttempt;
import ai.first.domain.foundation.notification.NotificationExternalOutboxMessage;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationLifecycleStatus;
import ai.first.domain.foundation.notification.NotificationPreference;
import ai.first.domain.foundation.notification.NotificationPriority;
import ai.first.domain.foundation.notification.NotificationSourceRef;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.audit.AuditTraceRepository;
import ai.first.application.foundation.audit.AuditTraceService;
import ai.first.application.foundation.email.EmailNotificationService;
import ai.first.application.foundation.governance.GovernancePolicyRepository;
import ai.first.application.foundation.governance.GovernancePolicyService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.MeResponse;
import ai.first.application.foundation.identity.MeService;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InvitationView;
import ai.first.application.foundation.notification.NotificationService;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.application.foundation.workstream.WorkstreamEventRepository;
import ai.first.application.foundation.workstream.WorkstreamLogRepository;
import ai.first.application.coreapp.audit.AuditTraceSummaryAutonomousAgent;

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
  private static final String USERADMIN_DISABLE_ACCOUNT = "USERADMIN_DISABLE_ACCOUNT";
  private static final String USERADMIN_REACTIVATE_ACCOUNT = "USERADMIN_REACTIVATE_ACCOUNT";
  private static final String USERADMIN_SUPPORT_ACCESS_READ = "USERADMIN_SUPPORT_ACCESS_READ";
  private static final String USERADMIN_SUPPORT_ACCESS_GRANT = "USERADMIN_SUPPORT_ACCESS_GRANT";
  private static final String USERADMIN_SUPPORT_ACCESS_REVOKE = "USERADMIN_SUPPORT_ACCESS_REVOKE";
  private static final String USERADMIN_SUPPORT_ACCESS_EXTEND = "USERADMIN_SUPPORT_ACCESS_EXTEND";
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
  private static final String AGENT_DEFINITIONS_MANAGE_CAPABILITY = "agent.definitions.manage";
  private static final String AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY = "agent_admin.reseed_missing_defaults";
  private static final String AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY = "agent_admin.simulate_tool_boundary";
  private static final String AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY = "agent_admin.draft_behavior_change";
  private static final String AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY = "agent_admin.submit_behavior_change_for_review";
  private static final String AGENT_ADMIN_REVIEW_CAPABILITY = "agent_admin.approve_behavior_change";
  private static final String AGENT_ADMIN_REJECT_CAPABILITY = "agent_admin.reject_behavior_change";
  private static final String AGENT_ADMIN_ACTIVATE_CAPABILITY = "agent_admin.activate_behavior_change";
  private static final String AGENT_ADMIN_CANCEL_CAPABILITY = "agent_admin.cancel_behavior_change";
  private static final String AGENT_ADMIN_ROLLBACK_CAPABILITY = "agent_admin.rollback_behavior_change";
  private static final String AGENT_ADMIN_EVALUATE_CAPABILITY = "agent_admin.start_behavior_review_task";
  private static final String CORE_ACCESS_ME_CAPABILITY = "core.access.me";
  private static final String CORE_PROFILE_UPDATE_CAPABILITY = "core.profile.update";
  private static final String CORE_ACCESS_CONTEXT_SELECT_CAPABILITY = "core.access.context.select";
  private static final String ACCESS_PROFILE_CONTEXT_SURFACE_ID = "surface.access.profile.context.v1";
  private static final String MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY = "my_account.view_summary";
  private static final String MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY = "my_account.view_context";
  private static final String MY_ACCOUNT_LIST_PERSONAL_ATTENTION_CAPABILITY = "my_account.list_personal_attention";
  private static final String MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY = "my_account.update_profile_settings";
  private static final String MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY = "my_account.open_authorized_workstream";
  private static final String MY_ACCOUNT_DIGEST_START_CAPABILITY = MyAccountPersonalAttentionDigestService.START_CAPABILITY;
  private static final String MY_ACCOUNT_DIGEST_READ_CAPABILITY = MyAccountPersonalAttentionDigestService.READ_CAPABILITY;
  private static final String MY_ACCOUNT_DIGEST_CANCEL_CAPABILITY = MyAccountPersonalAttentionDigestService.CANCEL_CAPABILITY;
  private static final String MY_ACCOUNT_DIGEST_ACCEPT_CAPABILITY = MyAccountPersonalAttentionDigestService.ACCEPT_RESULT_CAPABILITY;
  private static final String MY_ACCOUNT_DIGEST_REJECT_CAPABILITY = MyAccountPersonalAttentionDigestService.REJECT_RESULT_CAPABILITY;
  private static final String NOTIFICATION_LIST_CAPABILITY = NotificationService.LIST_MY_ACCOUNT_CENTER_TOOL;
  private static final String NOTIFICATION_MARK_READ_CAPABILITY = NotificationService.MARK_READ_TOOL;
  private static final String NOTIFICATION_DISMISS_CAPABILITY = NotificationService.DISMISS_TOOL;
  private static final String NOTIFICATION_ARCHIVE_CAPABILITY = NotificationService.ARCHIVE_TOOL;
  private static final String NOTIFICATION_SNOOZE_CAPABILITY = NotificationService.SNOOZE_TOOL;
  private static final String NOTIFICATION_UPDATE_PREFERENCES_CAPABILITY = NotificationService.UPDATE_PREFERENCES_TOOL;
  private static final String NOTIFICATION_EMAIL_LIST_PREFERENCES_CAPABILITY = EmailNotificationService.LIST_PREFERENCES_TOOL;
  private static final String NOTIFICATION_EMAIL_UPDATE_PREFERENCES_CAPABILITY = EmailNotificationService.UPDATE_PREFERENCES_TOOL;
  private static final String NOTIFICATION_DELIVERY_LIST_PLATFORM_CAPABILITY = NotificationService.LIST_DELIVERY_PLATFORM_TOOL;
  private static final String NOTIFICATION_DELIVERY_EVALUATE_EXTERNAL_CAPABILITY = NotificationService.EVALUATE_EXTERNAL_DELIVERY_TOOL;
  private static final String AUDIT_TRACE_READ_CAPABILITY = "audit.trace.read";
  private static final String AUDIT_TRACE_DASHBOARD_CAPABILITY = "audit.trace.dashboard.read";
  private static final String AUDIT_TRACE_SEARCH_CAPABILITY = "audit.trace.search";
  private static final String AUDIT_TRACE_DETAIL_CAPABILITY = "audit.trace.detail.read";
  private static final String AUDIT_TRACE_TIMELINE_CAPABILITY = "audit.trace.timeline.read";
  private static final String AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY = "audit.trace.failureEvidence.read";
  private static final String AUDIT_TRACE_GUIDE_CAPABILITY = "audit.trace.investigationGuide.read";
  private static final String AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY = "audit.trace.summary_task.start";
  private static final String AUDIT_TRACE_INVESTIGATION_NOTE_CAPABILITY = "audit.trace.investigation_note.append";
  private static final String GOVERNANCE_POLICY_READ_CAPABILITY = "governance.policy.read";
  private static final String GOVERNANCE_POLICY_SIMULATE_CAPABILITY = "governance.policy.simulate";
  private static final String GOVERNANCE_POLICY_PROPOSE_CAPABILITY = "governance.policy.propose";
  private static final String GOVERNANCE_PROPOSALS_REVIEW_CAPABILITY = GovernancePolicyService.REVIEW_CAPABILITY;
  private static final String GOVERNANCE_POLICY_APPROVE_CAPABILITY = "governance.policy.approve";
  private static final String GOVERNANCE_PROPOSALS_ACTIVATE_CAPABILITY = GovernancePolicyService.ACTIVATE_PROPOSAL_CAPABILITY;
  private static final String GOVERNANCE_POLICY_ACTIVATE_CAPABILITY = "governance.policy.activate";
  private static final String GOVERNANCE_POLICY_ROLLBACK_CAPABILITY = "governance.policy.rollback";
  private static final String GOVERNANCE_OUTCOMES_RECORD_CAPABILITY = GovernancePolicyService.OUTCOMES_RECORD_CAPABILITY;
  private static final String GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY = GovernancePolicyImpactService.START_CAPABILITY;
  private final MeService meService;
  private final AuthContextResolver authContextResolver;
  private final MyAccountService myAccountService;
  private final MyAccountPersonalAttentionDigestService personalAttentionDigestService;
  private final NotificationService notificationService;
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
  private final AttentionService attentionService;
  private final WorkstreamAgentRuntimeInvoker workstreamAgentRuntimeInvoker;
  private final WorkstreamLogRepository workstreamLogRepository;
  private final WorkstreamEventRepository workstreamEventRepository;
  private final AccessReviewAutonomousAgentRuntime accessReviewAutonomousAgentRuntime;
  private final Map<String, CapabilityActionResult> idempotentActionResults = new ConcurrentHashMap<>();

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
      WorkstreamLogRepository workstreamLogRepository,
      AccessReviewTaskRepository accessReviewTaskRepository,
      AuditTraceRepository auditTraceRepository,
      GovernancePolicyRepository governancePolicyRepository,
      AttentionService attentionService,
      AttentionProducerService attentionProducerService) {
    this(meService, authContextResolver, userDirectoryView, invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, workstreamAgentRuntimeInvoker, workstreamLogRepository, accessReviewTaskRepository, auditTraceRepository, governancePolicyRepository, attentionService, attentionProducerService, null, null, new FailClosedAccessReviewAutonomousAgentRuntime(), StarterSecurityComponents.notificationService());
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
      WorkstreamLogRepository workstreamLogRepository,
      AccessReviewTaskRepository accessReviewTaskRepository,
      AuditTraceRepository auditTraceRepository,
      GovernancePolicyRepository governancePolicyRepository,
      AttentionService attentionService,
      AttentionProducerService attentionProducerService,
      WorkstreamEventPublisher workstreamEventPublisher,
      WorkstreamEventRepository workstreamEventRepository) {
    this(meService, authContextResolver, userDirectoryView, invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, workstreamAgentRuntimeInvoker, workstreamLogRepository, accessReviewTaskRepository, auditTraceRepository, governancePolicyRepository, attentionService, attentionProducerService, workstreamEventPublisher, workstreamEventRepository, new FailClosedAccessReviewAutonomousAgentRuntime(), StarterSecurityComponents.notificationService());
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
      WorkstreamLogRepository workstreamLogRepository,
      AccessReviewTaskRepository accessReviewTaskRepository,
      AuditTraceRepository auditTraceRepository,
      GovernancePolicyRepository governancePolicyRepository,
      AttentionService attentionService,
      AttentionProducerService attentionProducerService,
      WorkstreamEventPublisher workstreamEventPublisher,
      WorkstreamEventRepository workstreamEventRepository,
      AccessReviewAutonomousAgentRuntime accessReviewAutonomousAgentRuntime,
      NotificationService notificationService) {
    this.meService = meService;
    this.authContextResolver = authContextResolver;
    this.attentionService = Objects.requireNonNull(attentionService);
    this.myAccountService = new MyAccountService(authContextResolver, attentionService);
    this.personalAttentionDigestService = StarterSecurityComponents.personalAttentionDigestService();
    this.notificationService = Objects.requireNonNull(notificationService);
    this.userDirectoryView = userDirectoryView;
    this.invitationView = invitationView;
    this.userAdminService = userAdminService;
    this.accessReviewAutonomousAgentRuntime = Objects.requireNonNull(accessReviewAutonomousAgentRuntime);
    this.accessReviewService = new UserAdminAccessReviewService(Objects.requireNonNull(accessReviewTaskRepository), userAdminService, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher, this.accessReviewAutonomousAgentRuntime);
    this.invitationService = invitationService;
    this.agentBehaviorRepository = agentBehaviorRepository;
    this.agentAdminService = new AgentAdminService(agentBehaviorRepository, authContextResolver);
    this.agentRuntimeService = agentRuntimeService;
    this.workstreamAgentRuntimeInvoker = Objects.requireNonNull(workstreamAgentRuntimeInvoker);
    this.workstreamLogRepository = Objects.requireNonNull(workstreamLogRepository);
    this.workstreamEventRepository = workstreamEventRepository == null ? new EmptyWorkstreamEventRepository() : workstreamEventRepository;
    this.auditTraceService = new AuditTraceService(authContextResolver, Objects.requireNonNull(auditTraceRepository));
    this.governancePolicyService = new GovernancePolicyService(Objects.requireNonNull(governancePolicyRepository), authContextResolver, Clock.systemUTC());
  }

  public WorkstreamBootstrapResponse bootstrap(WorkosIdentity identity, String selectedContextId, String correlationId) {
    var me = meService.me(identity, selectedContextId, correlationId);
    var actor = authContextResolver.resolveMe(identity, me.selectedAuthContext().selectedContextId(), correlationId);
    return new WorkstreamBootstrapResponse(me, functionalAgentsWithBackendAttention(actor, me.functionalAgents(), correlationId), initialItems(actor, correlationId), initialSurfaces(actor, correlationId));
  }

  public List<MeResponse.FunctionalAgentSummary> functionalAgents(WorkosIdentity identity, String selectedContextId, String correlationId) {
    var me = meService.me(identity, selectedContextId, correlationId);
    var actor = authContextResolver.resolveMe(identity, me.selectedAuthContext().selectedContextId(), correlationId);
    return functionalAgentsWithBackendAttention(actor, me.functionalAgents(), correlationId);
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

  public WorkstreamShellResponse runShellRequest(WorkosIdentity identity, String selectedContextId, WorkstreamShellRequest request) {
    if (!Objects.equals(selectedContextId, request.selectedContextId())) throw new AuthorizationException(403, "CONTEXT_FORBIDDEN");
    var correlationId = firstNonBlank(request.correlationId(), "shell-request");
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    seedStarterCoreAttention(actor, correlationId);
    var requestType = firstNonBlank(request.requestType(), "show_surface");
    if (!List.of("show_surface", "open_workstream", "refresh_surface", "open_attention_item").contains(requestType)) throw new AuthorizationException(400, "SHELL_REQUEST_TYPE_UNSUPPORTED");
    var attentionOpen = "open_attention_item".equals(requestType)
        ? attentionService.openAttentionItem(actor, request.targetItemId(), correlationId)
        : null;
    if (attentionOpen != null && !"accepted".equals(attentionOpen.status())) {
      var denied = shellSystemMessageSurface(actor, MY_ACCOUNT_AGENT_ID, "TARGET_NOT_FOUND_OR_FORBIDDEN", "The requested attention item is unavailable in the selected context.", correlationId);
      var item = shellRequestItem(MY_ACCOUNT_AGENT_ID, request, correlationId, denied.surfaceId(), "blocked");
      return new WorkstreamShellResponse(normalizeShellRequest(request, MY_ACCOUNT_AGENT_ID, null, correlationId), "denied", "The requested attention item is unavailable.", correlationId, denied.traceIds(), item, denied);
    }
    var requestedAgentId = attentionOpen != null && attentionOpen.targetFunctionalAgentId() != null
        ? attentionOpen.targetFunctionalAgentId()
        : firstNonBlank(request.targetFunctionalAgentId(), request.sourceFunctionalAgentId(), MY_ACCOUNT_AGENT_ID);
    var alias = attentionOpen == null ? resolveShellSurfaceAlias(requestType, requestedAgentId, request) : null;
    var resolvedRequest = alias == null ? request : new WorkstreamShellRequest(
        requestType,
        firstNonBlank(request.origin(), "user_prompt"),
        firstNonBlank(request.displayText(), alias.canonicalPrompt()),
        alias.canonicalPrompt(),
        alias.targetAgentId(),
        alias.targetSurfaceId(),
        request.targetItemId(),
        request.sourceFunctionalAgentId(),
        request.sourceSurfaceId(),
        request.sourceActionId(),
        firstNonBlank(request.scope(), "current_workstream"),
        correlationId,
        request.selectedContextId());
    var targetAgentId = alias == null ? requestedAgentId : alias.targetAgentId();
    var targetAgent = MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities()).stream()
        .filter(agent -> targetAgentId.equals(agent.functionalAgentId()))
        .findFirst()
        .orElse(null);
    if (targetAgent == null || !"visible".equals(targetAgent.availability())) {
      var denied = shellSystemMessageSurface(actor, targetAgentId, "TARGET_NOT_FOUND_OR_FORBIDDEN", "The requested workstream or surface is unavailable in the selected context.", correlationId);
      var item = shellRequestItem(targetAgentId, resolvedRequest, correlationId, denied.surfaceId(), "blocked");
      return new WorkstreamShellResponse(normalizeShellRequest(resolvedRequest, targetAgentId, null, correlationId), "denied", "The requested workstream or surface is unavailable.", correlationId, denied.traceIds(), item, denied);
    }
    var targetSurfaceId = attentionOpen != null && attentionOpen.surfaceRef() != null
        ? attentionOpen.surfaceRef().targetSurfaceId()
        : shellTargetSurfaceId(requestType, targetAgentId, resolvedRequest.targetSurfaceId());
    if (alias == null && shouldDenyUnresolvedPromptAlias(requestType, request)) {
      var denied = shellSystemMessageSurface(actor, targetAgentId, "TARGET_NOT_FOUND_OR_FORBIDDEN", "The requested workstream or surface is unavailable in the selected context.", correlationId);
      var item = shellRequestItem(targetAgentId, request, correlationId, denied.surfaceId(), "blocked");
      return new WorkstreamShellResponse(normalizeShellRequest(request, targetAgentId, null, correlationId), "denied", "The requested workstream or surface is unavailable.", correlationId, denied.traceIds(), item, denied);
    }
    var surface = dynamicSurface(actor, targetSurfaceId, correlationId);
    if (surface == null && "refresh_surface".equals(requestType)) surface = surface(identity, selectedContextId, targetSurfaceId, correlationId);
    if (surface == null) {
      var denied = shellSystemMessageSurface(actor, targetAgentId, "TARGET_NOT_FOUND_OR_FORBIDDEN", "The requested surface is unavailable in the selected context.", correlationId);
      var item = shellRequestItem(targetAgentId, resolvedRequest, correlationId, denied.surfaceId(), "blocked");
      return new WorkstreamShellResponse(normalizeShellRequest(resolvedRequest, targetAgentId, targetSurfaceId, correlationId), "denied", "The requested surface is unavailable.", correlationId, denied.traceIds(), item, denied);
    }
    var item = shellRequestItem(surface.ownerFunctionalAgentId(), resolvedRequest, correlationId, surface.surfaceId(), "ready");
    workstreamLogRepository.appendSystemEntry(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), item, surface);
    return new WorkstreamShellResponse(normalizeShellRequest(resolvedRequest, surface.ownerFunctionalAgentId(), surface.surfaceId(), correlationId), "accepted", "Shell request resolved through backend-authoritative surface capability.", correlationId, surface.traceIds(), item, surface);
  }

  public CapabilityActionResult runAction(WorkosIdentity identity, String selectedContextId, CapabilityActionRequest request) {
    if (!Objects.equals(selectedContextId, request.selectedContextId())) throw new AuthorizationException(403, "CONTEXT_FORBIDDEN");
    var actor = authContextResolver.resolveMe(identity, selectedContextId, request.correlationId());
    var action = actionById(request.actionId());
    if (action == null || !Objects.equals(action.capabilityId(), request.capabilityId()) || !Objects.equals(action.governedToolId(), request.governedToolId()) || !Objects.equals(action.browserToolId(), request.browserToolId())) throw new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN");
    if (!isActionCapabilityVisible(actor, action.capabilityId())) throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    if (action.idempotency().required() && (request.idempotencyKey() == null || request.idempotencyKey().isBlank())) return new CapabilityActionResult("validation-error", "This action requires a client-generated idempotency key.", request.correlationId(), List.of("trace-validation-idempotency"), null);
    var actionIdempotencyKey = action.idempotency().required() && !durableActionOwnsIdempotency(request.actionId()) ? actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + request.actionId() + ":" + request.idempotencyKey() : null;
    if (actionIdempotencyKey != null && idempotentActionResults.containsKey(actionIdempotencyKey)) return idempotentActionResults.get(actionIdempotencyKey);
    if (action.disabled() != null) return new CapabilityActionResult("denied", action.disabled().message(), request.correlationId(), List.of("trace-denied-" + action.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));

    CapabilityActionResult result = null;
    if ("action-display-user-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "User detail loaded.", request.correlationId(), List.of("trace-user-admin-detail-" + stableSuffix(stringInput(request.input(), "accountId", actor.account().accountId()))), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-display-invitation-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Invitation detail loaded.", request.correlationId(), List.of("trace-user-admin-invitation-" + stableSuffix(stringInput(request.input(), "invitationId", "latest"))), invitationDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-invite-user".equals(request.actionId())) {
      var invite = invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
          request.idempotencyKey(), actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId(),
          stringInput(request.input(), "email", "new-user@example.test"), stringInput(request.input(), "displayName", "New User"),
          rolesInput(request.input()), Instant.now().plus(7, ChronoUnit.DAYS), "workstream-invite", request.correlationId()));
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
      var requestedStatus = stringInput(request.input(), "status", "action-useradmin-reactivate-member".equals(request.actionId()) ? "active" : "removed");
      var targetStatus = "action-useradmin-reactivate-member".equals(request.actionId()) ? MembershipStatus.ACTIVE : membershipStatusInputForDeactivate(requestedStatus);
      var changed = userAdminService.updateMemberStatus(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), targetStatus, stringInput(request.input(), "reason", "workstream member status change"), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(changed.status(), changed.message(), request.correlationId(), List.of(changed.traceId()), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-useradmin-permanently-remove-user".equals(request.actionId())) {
      var removed = userAdminService.permanentlyRemoveUser(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), stringInput(request.input(), "reason", "workstream permanent user removal"), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(removed.status(), removed.message(), request.correlationId(), List.of(removed.traceId()), listSurface(actor, request.correlationId()));
    } else if ("action-useradmin-disable-account".equals(request.actionId())) {
      var account = userAdminService.disableAccount(actor, stringInput(request.input(), "accountId", actor.account().accountId()), stringInput(request.input(), "reason", "workstream account disable"), request.correlationId());
      result = new CapabilityActionResult("accepted", "Account disabled by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-useradmin-account-disable-" + stableSuffix(request.correlationId())), detailSurface(actor, request.correlationId()));
    } else if ("action-useradmin-reactivate-account".equals(request.actionId())) {
      var account = userAdminService.reactivateAccount(actor, stringInput(request.input(), "accountId", actor.account().accountId()), stringInput(request.input(), "reason", "workstream account reactivate"), request.correlationId());
      result = new CapabilityActionResult("accepted", "Account reactivated by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-useradmin-account-reactivate-" + stableSuffix(request.correlationId())), detailSurface(actor, request.correlationId()));
    } else if ("action-useradmin-read-support-access".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Support-access state loaded.", request.correlationId(), List.of("trace-useradmin-support-access-read-" + stableSuffix(request.correlationId())), listSurface(actor, request.correlationId()));
    } else if ("action-useradmin-grant-support-access".equals(request.actionId()) || "action-useradmin-revoke-support-access".equals(request.actionId()) || "action-useradmin-extend-support-access".equals(request.actionId())) {
      var enabled = !"action-useradmin-revoke-support-access".equals(request.actionId());
      var expiresAt = enabled ? Instant.now().plus(8, ChronoUnit.HOURS) : null;
      var changed = userAdminService.updateSupportAccess(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), enabled, expiresAt, stringInput(request.input(), "reason", "workstream support access change"), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult("accepted", "Support access updated.", request.correlationId(), List.of("trace-useradmin-support-access-" + stableSuffix(request.idempotencyKey())), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-useradmin-start-access-review".equals(request.actionId())) {
      var task = accessReviewService.start(actor, request.idempotencyKey(), request.correlationId());
      result = accessReviewActionResult(task, task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked-runtime" : accessReviewStatus(task), task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "Access-review task failed closed; governed AutonomousAgent provider/runtime configuration is unavailable." : "Access-review Akka AutonomousAgent task accepted; backend projection remains source of truth.", request.correlationId(), actor);
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
      var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.PROMPT, "Approved revised Agent Admin prompt. Continue to require backend authorization, approval, and trace links.", List.of(), "Agent Admin UI-proposed prompt clarification", request.correlationId()));
      result = new CapabilityActionResult(proposal.status() == BehaviorChangeProposal.Status.DENIED ? "denied" : "accepted", "Prompt behavior-change proposal " + proposal.proposalId() + " recorded with status " + proposal.status().name().toLowerCase(Locale.ROOT) + "; prompt text cannot grant authority without backend approval.", request.correlationId(), List.of(proposal.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-test-agent-prompt".equals(request.actionId())) {
      var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, request.correlationId(), stringInput(request.input(), "prompt", "Summarize current Agent Admin governed-agent readiness.")));
      result = new CapabilityActionResult(prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "accepted" : "denied", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "No-side-effect Agent Admin test assembled governed prompt and loader traces." : "No-side-effect Agent Admin test failed closed before model invocation.", request.correlationId(), List.of(prompt.traceId()), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-simulate-tool-boundary".equals(request.actionId())) {
      var unsafeGrant = new ToolPermissionBoundary.ToolGrant("email.send", ToolPermissionBoundary.Category.EXTERNAL_SIDE_EFFECT, "tenant.email.send", List.of("execute"), List.of("runtime"), "HIGH", "AUTONOMOUS", true, "full_work_trace");
      var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY, null, List.of(unsafeGrant), "Agent Admin simulation of policy-blocked side-effecting tool grant", request.correlationId()));
      result = new CapabilityActionResult(proposal.status() == BehaviorChangeProposal.Status.DENIED ? "denied" : "approval-required", "Tool-boundary authority expansion simulation recorded as " + proposal.status().name().toLowerCase(Locale.ROOT) + "; side-effecting tools require retained human approval and backend ToolPermissionBoundary enforcement.", request.correlationId(), List.of(proposal.proposalId()), agentToolBoundarySurface(actor, request.correlationId()));
    } else if ("action-approve-skill-manifest".equals(request.actionId())) {
      result = new CapabilityActionResult("approval-required", "Skill manifest approval is recorded as a governed review gate; activation must use an approved backend governance command.", request.correlationId(), List.of("trace-skill-manifest-approval-required"), agentSkillManifestSurface(actor, request.correlationId()));
    } else if ("action-deactivate-agent-definition".equals(request.actionId())) {
      var changed = changeAgentDefinitionStatus(actor, stringInput(request.input(), "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), AgentLifecycleStatus.DISABLED, request.correlationId());
      result = new CapabilityActionResult(changed ? "accepted" : "no-op", changed ? "AgentDefinition deactivated through backend-governed Agent Admin lifecycle." : "AgentDefinition was already deactivated; lifecycle command was idempotent.", request.correlationId(), List.of("trace-agent-definition-deactivated-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.correlationId()));
    } else if ("action-activate-agent-definition".equals(request.actionId())) {
      var changed = changeAgentDefinitionStatus(actor, stringInput(request.input(), "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), AgentLifecycleStatus.ACTIVE, request.correlationId());
      result = new CapabilityActionResult(changed ? "accepted" : "no-op", changed ? "AgentDefinition activated through backend-governed Agent Admin lifecycle." : "AgentDefinition was already active; lifecycle command was idempotent.", request.correlationId(), List.of("trace-agent-definition-activated-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.correlationId()));
    } else if ("action-import-agent-seed-defaults".equals(request.actionId())) {
      authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY);
      var seed = new AgentBehaviorSeedLoader(agentBehaviorRepository, Clock.systemUTC()).importStarterDefaults(actor.selectedContext().tenantId(), actor.account().accountId(), request.correlationId());
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "agent_admin.seed_import.v1 created=" + seed.createdCount() + " skipped=" + seed.skippedCount(), request.correlationId());
      result = new CapabilityActionResult(seed.createdCount() == 0 ? "no-op" : "accepted", "Seed import completed with " + seed.createdCount() + " created and " + seed.skippedCount() + " skipped governed records.", request.correlationId(), List.of("trace-agent-seed-import-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, request.correlationId()));
    } else if ("action-update-my-profile".equals(request.actionId()) || "action-update-my-settings".equals(request.actionId())) {
      var update = updateOwnProfileSettings(actor, request);
      result = new CapabilityActionResult(update.changed() ? "accepted" : "no-op", update.changed() ? "My Account profile/settings changes were persisted by the backend." : "My Account profile/settings update was a no-op.", request.correlationId(), List.of("trace-my-account-profile-settings-" + stableSuffix(request.idempotencyKey())), surfaceForAction(authContextResolver.resolveMe(identity, selectedContextId, request.correlationId()), request.actionId(), request.correlationId()));
    } else if ("action-start-my-account-personal-attention-digest".equals(request.actionId())) {
      var task = personalAttentionDigestService.start(actor, new MyAccountPersonalAttentionDigestService.StartPersonalAttentionDigestCommand(request.idempotencyKey()), request.correlationId());
      result = personalAttentionDigestActionResult(task, task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "accepted", "My Account personal attention digest task is backend-governed; results are advisory, redacted, and source attention is unchanged.", request.correlationId(), actor);
    } else if ("action-read-my-account-personal-attention-digest".equals(request.actionId())) {
      var task = personalAttentionDigestService.read(actor, stringInput(request.input(), "digestTaskId", ""), request.correlationId());
      result = personalAttentionDigestActionResult(task, "accepted", "My Account personal attention digest state loaded from backend projection.", request.correlationId(), actor);
    } else if ("action-cancel-my-account-personal-attention-digest".equals(request.actionId())) {
      var task = personalAttentionDigestService.cancel(actor, stringInput(request.input(), "digestTaskId", ""), stringInput(request.input(), "reason", "workstream cancel"), request.correlationId());
      result = personalAttentionDigestActionResult(task, "accepted", "My Account personal attention digest cancellation recorded; source attention unchanged.", request.correlationId(), actor);
    } else if ("action-accept-my-account-personal-attention-digest".equals(request.actionId())) {
      var task = personalAttentionDigestService.acceptResult(actor, stringInput(request.input(), "digestTaskId", ""), stringInput(request.input(), "reason", "accepted in My Account"), request.correlationId());
      result = personalAttentionDigestActionResult(task, "accepted", "Advisory personal attention digest accepted; source attention lifecycle unchanged.", request.correlationId(), actor);
    } else if ("action-reject-my-account-personal-attention-digest".equals(request.actionId())) {
      var task = personalAttentionDigestService.rejectResult(actor, stringInput(request.input(), "digestTaskId", ""), stringInput(request.input(), "reason", "needs refresh"), request.correlationId());
      result = personalAttentionDigestActionResult(task, "accepted", "Advisory personal attention digest rejected for follow-up; source attention lifecycle unchanged.", request.correlationId(), actor);
    } else if ("action-show-my-account-notification-center".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "My Account in-app notification center loaded from backend-owned notification projection.", request.correlationId(), List.of("trace-my-account-notification-center"), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-notification-mark-read".equals(request.actionId())) {
      var item = notificationService.markRead(actor, notificationIdInput(actor, request.input(), request.correlationId()), request.correlationId());
      result = new CapabilityActionResult(item.redactionLevel().name().toLowerCase(Locale.ROOT), "Notification mark-read processed by backend-owned in-app lifecycle; source attention/task/event state unchanged.", request.correlationId(), item.traceRefs(), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-notification-dismiss".equals(request.actionId())) {
      var item = notificationService.dismiss(actor, notificationIdInput(actor, request.input(), request.correlationId()), request.correlationId());
      result = new CapabilityActionResult(item.redactionLevel().name().toLowerCase(Locale.ROOT), "Notification dismiss processed by backend-owned in-app lifecycle; source state unchanged.", request.correlationId(), item.traceRefs(), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-notification-archive".equals(request.actionId())) {
      var item = notificationService.archive(actor, notificationIdInput(actor, request.input(), request.correlationId()), request.correlationId());
      result = new CapabilityActionResult(item.redactionLevel().name().toLowerCase(Locale.ROOT), "Notification archive processed by backend-owned in-app lifecycle; source state unchanged.", request.correlationId(), item.traceRefs(), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-notification-snooze".equals(request.actionId())) {
      var item = notificationService.snooze(actor, notificationIdInput(actor, request.input(), request.correlationId()), Instant.now().plus(1, ChronoUnit.HOURS), request.correlationId());
      result = new CapabilityActionResult(item.redactionLevel().name().toLowerCase(Locale.ROOT), "Notification snooze processed by backend-owned in-app lifecycle; source state unchanged.", request.correlationId(), item.traceRefs(), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-notification-update-preferences".equals(request.actionId())) {
      var pref = notificationService.updatePreference(actor, NotificationCategory.ALL, booleanInput(request.input(), "enabled", true), NotificationPriority.INFO, null, booleanInput(request.input(), "includeReadInCenter", false), request.correlationId());
      result = new CapabilityActionResult("accepted", "In-app notification preferences updated by backend authority; email delivery remains a separate governed channel.", request.correlationId(), List.of(pref.correlationId()), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-notification-email-update-preferences".equals(request.actionId())) {
      var pref = notificationService.updateEmailPreference(actor, notificationCategoryInput(request.input(), NotificationCategory.DIGEST_READY), booleanInput(request.input(), "enabled", true), notificationPriorityInput(request.input(), NotificationPriority.INFO), null, request.correlationId());
      result = new CapabilityActionResult("accepted", "Email notification preferences updated by backend authority through notification.email.update_preferences; in-app notifications and future SMS/push/webhook channels are unchanged.", request.correlationId(), List.of(pref.correlationId()), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-notification-external-fail-closed-check".equals(request.actionId())) {
      var attempt = notificationService.evaluateExternalDelivery(actor, notificationIdInput(actor, request.input(), request.correlationId()), notificationChannelInput(request.input(), NotificationChannel.WEBHOOK), stringInput(request.input(), "destinationSummary", "redacted local/test destination"), request.correlationId());
      result = new CapabilityActionResult("blocked", "External notification provider is not configured; the governed platform captured a local/test outbox intent and did not report delivery success.", request.correlationId(), List.of(attempt.correlationId()), myAccountNotificationCenterSurface(actor, request.correlationId()));
    } else if ("action-open-user-admin".equals(request.actionId()) || "action-open-agent-admin".equals(request.actionId()) || "action-open-audit-trace".equals(request.actionId()) || "action-open-governance-policy".equals(request.actionId())) {
      var open = myAccountService.openAuthorizedWorkstream(actor, request.actionId(), request.correlationId());
      result = "accepted".equals(open.status())
          ? new CapabilityActionResult("accepted", open.message(), request.correlationId(), open.traceIds(), surfaceForAction(actor, request.actionId(), request.correlationId()))
          : new CapabilityActionResult("denied", open.message(), request.correlationId(), open.traceIds(), myAccountOpenDeniedSurface(actor, open, request.correlationId()));
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
    } else if ("action-audit-trace-start-summary-task".equals(request.actionId()) || "action-audit-trace-summary-task-start".equals(request.actionId())) {
      result = new CapabilityActionResult("blocked_provider_or_runtime", "Audit summary worker start is wired to backend-governed progress/review surfaces but fails closed when provider/runtime/tool-boundary configuration is unavailable; no deterministic summary success was fabricated.", request.correlationId(), List.of("trace-audit-summary-worker-blocked"), auditTraceSummaryTaskBlockedSurface(actor, request.correlationId()));
    } else if ("action-audit-trace-append-investigation-note".equals(request.actionId())) {
      var surface = auditTraceInvestigationNoteSurface(actor, request.input(), request.idempotencyKey(), request.correlationId());
      var item = new WorkstreamItem("item-audit-trace-note-" + AuditTraceService.stableSuffix(request.correlationId()), AUDIT_TRACE_AGENT_ID, "system_message", Instant.now().toString(), request.correlationId(), surface.traceIds(), surface.surfaceId(), "Audit/Trace investigation note", "Human-authored investigation note recorded with browser-safe redaction.", "recorded");
      workstreamLogRepository.appendSystemEntry(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), item, surface);
      result = new CapabilityActionResult("recorded", "Investigation note appended as an auditable, tenant-scoped workstream annotation; source traces remain immutable.", request.correlationId(), surface.traceIds(), surface);
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
      var simulation = governancePolicyService.simulateProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(simulation.status(), simulation.message(), request.correlationId(), simulation.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), simulation.surface(), List.of(governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), openAuditAction())));
    } else if ("action-governance-policy-decide".equals(request.actionId())) {
      var decision = governancePolicyService.decideProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(decision.status(), decision.message(), request.correlationId(), decision.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), decision.surface(), List.of(governanceActivateProposalAction(), governanceRollbackPolicyAction(), governanceOutcomeNoteAction(), openAuditAction())));
    } else if ("action-governance-policy-activate".equals(request.actionId()) || "action-commit-policy".equals(request.actionId())) {
      var activation = governancePolicyService.activateProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(activation.status(), activation.message(), request.correlationId(), activation.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), activation.surface(), List.of(governanceRollbackPolicyAction(), governanceOutcomeNoteAction(), openAuditAction())));
    } else if ("action-governance-policy-rollback".equals(request.actionId())) {
      var rollback = governancePolicyService.rollbackProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(rollback.status(), rollback.message(), request.correlationId(), rollback.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), rollback.surface(), List.of(governanceOutcomeNoteAction(), governanceListPoliciesAction(), openAuditAction())));
    } else if ("action-governance-policy-outcome-note".equals(request.actionId()) || "action-govpol-add-outcome-note".equals(request.actionId())) {
      var outcome = governancePolicyService.recordOutcomeNote(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(outcome.status(), outcome.message(), request.correlationId(), outcome.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), outcome.surface(), List.of(governanceOutcomeNoteAction(), openAuditAction())));
    } else if ("action-governance-policy-start-impact-analysis".equals(request.actionId())) {
      result = new CapabilityActionResult("blocked_provider_or_runtime", "Governance/Policy impact analysis start is backend-governed and fails closed when provider/runtime configuration is missing; no deterministic, simulated, model-less, or fake impact_ready success is returned.", request.correlationId(), List.of("trace-governance-policy-impact-analysis-blocked"), governancePolicyImpactAnalysisBlockedSurface(actor, request.correlationId()));
    }
    if (result == null) result = new CapabilityActionResult("accepted", action.label(), request.correlationId(), List.of("trace-" + request.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));
    if (actionIdempotencyKey != null) idempotentActionResults.put(actionIdempotencyKey, result);
    return result;
  }

  private boolean durableActionOwnsIdempotency(String actionId) {
    return actionId != null && (actionId.startsWith("action-start-my-account-personal-attention-digest")
        || actionId.startsWith("action-read-my-account-personal-attention-digest")
        || actionId.startsWith("action-cancel-my-account-personal-attention-digest")
        || actionId.startsWith("action-accept-my-account-personal-attention-digest")
        || actionId.startsWith("action-reject-my-account-personal-attention-digest")
        || actionId.startsWith("action-useradmin-start-access-review")
        || actionId.startsWith("action-useradmin-read-access-review")
        || actionId.startsWith("action-useradmin-cancel-access-review")
        || actionId.startsWith("action-useradmin-accept-access-review")
        || actionId.startsWith("action-useradmin-reject-access-review")
        || actionId.startsWith("action-governance-policy-")
        || "action-simulate-policy".equals(actionId)
        || "action-commit-policy".equals(actionId));
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

  /** Returns the bounded v1 workstream SSE replay/refresh batch for the selected authorized context. */
  public List<WorkstreamEvent> events(WorkosIdentity identity, String selectedContextId, String functionalAgentId, String lastEventId, String correlationId) {
    var actor = authContextResolver.resolveMe(identity, selectedContextId, correlationId);
    var allEvents = new ArrayList<WorkstreamEvent>();
    allEvents.addAll(initialEvents(actor, correlationId));
    allEvents.addAll(eventBackedRefreshEvents(actor, correlationId));
    var events = allEvents.stream().filter(event -> functionalAgentId == null || functionalAgentId.isBlank() || functionalAgentId.equals(event.functionalAgentId())).toList();
    if (lastEventId == null || lastEventId.isBlank()) return events;
    for (var index = 0; index < events.size(); index++) if (lastEventId.equals(events.get(index).eventId()) && index + 1 < events.size()) return events.subList(index + 1, events.size());
    return List.of(new WorkstreamEvent("evt-stale-replay-unavailable-999", "surface.stale", actor.selectedContext().tenantId(), actor.selectedContext().customerId(), USER_ADMIN_AGENT_ID, "surface-user-admin-dashboard", "dashboard", "v1", correlationId, List.of("trace-sse-replay-unavailable"), Instant.now().toString(), 999, mapOf("reason", "Replay from Last-Event-ID is unavailable for bounded SSE replay v1; refresh backend-owned workstream surfaces instead of treating the stream as continuously live.")));
  }

  private List<SurfaceEnvelope> initialSurfaces(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var dashboard = defaultDashboardSurface(actor, correlationId);
    return dashboard == null ? List.of() : List.of(dashboard);
  }

  private List<WorkstreamItem> initialItems(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var dashboard = defaultDashboardSurface(actor, correlationId);
    if (dashboard == null) return List.of();
    return List.of(new WorkstreamItem(
        "item-default-dashboard-" + stableSuffix(actor.selectedContext().membershipId() + ":" + dashboard.surfaceId()),
        dashboard.ownerFunctionalAgentId(),
        "surface",
        Instant.now().toString(),
        correlationId,
        dashboard.traceIds(),
        dashboard.surfaceId(),
        dashboard.title(),
        "Default dashboard loaded by backend bootstrap for the selected workstream context.",
        "ready"));
  }

  private SurfaceEnvelope defaultDashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var targetAgentId = MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities()).stream()
        .filter(agent -> "visible".equals(agent.availability()))
        .filter(agent -> actor.selectedContext().capabilities().containsAll(agent.requiredCapabilityIds()))
        .map(MeResponse.FunctionalAgentSummary::functionalAgentId)
        .findFirst()
        .orElse(null);
    if (targetAgentId == null) return null;
    return dynamicSurface(actor, shellTargetSurfaceId("open_workstream", targetAgentId, null), correlationId);
  }

  private SurfaceEnvelope myAccountDashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    seedStarterCoreAttention(actor, correlationId);
    var dashboard = myAccountService.dashboardData(actor, correlationId);
    var notificationCenter = myAccountNotificationCenterData(actor, correlationId);
    var cards = new ArrayList<>(dashboard.cards());
    cards.add(mapOf("cardId", "card-my-account-notifications", "cardKind", "notification-center", "label", "In-app notifications", "value", notificationCenter.unreadCount(), "unit", "unread notifications", "status", notificationCenter.visibleCount() == 0 ? "No notifications in the authorized center" : notificationCenter.visibleCount() + " notifications visible", "description", "Backend-owned notification projection for selected-context attention and workstream events.", "severity", notificationCenter.unreadCount() > 0 ? "warning" : "info", "surfaceId", "surface-my-account-notification-center", "actionId", "action-show-my-account-notification-center"));
    var personalAttention = myAccountService.personalAttention(actor, correlationId);
    return envelope("surface-my-account-dashboard", "dashboard", "My Account Dashboard", actor, correlationId,
        mapOf("surfaceContract", "my_account.personal_command_center.v1", "canonicalAccessProfileContextSurfaceId", ACCESS_PROFILE_CONTEXT_SURFACE_ID, "coreCapabilityAliases", List.of(CORE_ACCESS_ME_CAPABILITY, CORE_PROFILE_UPDATE_CAPABILITY, CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), "cards", cards, "sections", dashboard.sections(), "attentionItems", personalAttention, "needsAttention", personalAttention, "attentionCounters", myAccountAttentionCounters(dashboard.cards()), "attentionSource", AttentionService.LIST_MY_ACCOUNT_ITEMS_TOOL, "accountContext", mapOf("displayName", actor.profile().displayName(), "email", actor.account().displayEmail(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "roles", actor.selectedContext().roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(), "authority", dashboard.authorityBasis().primaryRoleBasis()), "quickSurfaceActionIds", dashboard.nextSteps().stream().map(step -> step.get("actionId")).filter(Objects::nonNull).toList(), "utilityActionIds", List.of("action-show-my-profile", "action-show-my-settings", "action-show-my-context", "action-show-my-account-notification-center", "action-sign-out"), "controlPanels", myAccountControlPanels(notificationCenter), "authorizedWorkstreamLinks", dashboard.nextSteps(), "notificationCenter", mapOf("surfaceId", "surface-my-account-notification-center", "surfaceContract", notificationCenter.surfaceContract(), "channel", "in_app", "unreadCount", notificationCenter.unreadCount(), "visibleCount", notificationCenter.visibleCount(), "countSource", NotificationService.LIST_MY_ACCOUNT_CENTER_TOOL, "futureEmailPush", "not implemented"), "personalAttentionDigest", mapOf("surfaceIds", List.of("surface-my-account-personal-attention-digest-progress", "surface-my-account-personal-attention-digest-result", "surface-my-account-personal-attention-digest-blocked"), "statusSource", "backend-projected MyAccountPersonalAttentionDigestTask", "noDirectMutation", true, "capabilityIds", List.of(MY_ACCOUNT_DIGEST_START_CAPABILITY, MY_ACCOUNT_DIGEST_READ_CAPABILITY, MY_ACCOUNT_DIGEST_CANCEL_CAPABILITY, MY_ACCOUNT_DIGEST_ACCEPT_CAPABILITY, MY_ACCOUNT_DIGEST_REJECT_CAPABILITY)), "nextSteps", dashboard.nextSteps(), "traceRefs", dashboard.traceRefs(), "authorityBasis", dashboard.authorityBasis(), "contextCapabilityGroups", dashboard.capabilityGroups(), "redaction", "Personal attention and notifications only include authorized sibling workstreams; hidden workstreams return not_found_or_redacted without names or counts.", "systemStates", List.of("system_message", "selected context", "authority", "tenant", "trace", "personal attention", "personal attention digest", "notification center", "trace refs", "not_found_or_redacted", "blocked_provider_or_runtime")),
        List.of(showDashboardAction(), showNotificationCenterAction(), showProfileAction(), showSettingsAction(), showContextAction(), startPersonalAttentionDigestAction(), readPersonalAttentionDigestAction(), signOutAction(), openUserAdminAction(), openAgentAdminAction(), openAuditAction(), openGovernancePolicyAction()));
  }

  private List<Map<String, Object>> myAccountAttentionCounters(List<Map<String, Object>> availableWorkstreamCards) {
    return availableWorkstreamCards.stream()
        .map(card -> mapOf(
            "counterId", "counter-" + card.get("workstreamId"),
            "label", card.get("label"),
            "value", card.get("value"),
            "severity", card.get("severity"),
            "status", card.get("status"),
            "source", AttentionService.LIST_MY_ACCOUNT_ITEMS_TOOL,
            "actionId", card.get("actionId"),
            "surfaceId", card.get("surfaceId"),
            "workstreamId", card.get("workstreamId"),
            "description", card.get("description")))
        .toList();
  }

  private List<Map<String, Object>> myAccountControlPanels(MyAccountNotificationCenter notificationCenter) {
    return List.of(
        mapOf("panelId", "panel-profile", "label", "Profile", "summary", "Maintain browser-safe identity fields. Provider-backed facts remain read-only.", "state", "self-service", "actionId", "action-show-my-profile", "surfaceId", "surface-my-profile"),
        mapOf("panelId", "panel-settings", "label", "Settings & theme", "summary", "Choose a named theme and persist personal preferences through governed settings.", "state", "self-service", "actionId", "action-show-my-settings", "surfaceId", "surface-my-settings"),
        mapOf("panelId", "panel-context", "label", "Context & authority", "summary", "Inspect selected tenant/customer, role basis, visible capabilities, and context switch targets.", "state", "authority", "actionId", "action-show-my-context", "surfaceId", "surface-my-context"),
        mapOf("panelId", "panel-notifications", "label", "Notifications", "summary", "Triage in-app notifications without mutating source work.", "state", "triage", "value", notificationCenter.visibleCount(), "actionId", "action-show-my-account-notification-center", "surfaceId", "surface-my-account-notification-center"),
        mapOf("panelId", "panel-digest", "label", "Personal digest/export", "summary", "Start or review a governed advisory digest of authorized personal attention evidence.", "state", "advisory", "actionId", "action-start-my-account-personal-attention-digest", "surfaceId", "surface-my-account-personal-attention-digest-progress"));
  }

  private SurfaceEnvelope myAccountNotificationCenterSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var center = myAccountNotificationCenterData(actor, correlationId);
    return envelope("surface-my-account-notification-center", "notification-center", "In-app notifications", actor, correlationId,
        mapOf("surfaceContract", center.surfaceContract(), "channel", "in_app", "unreadCount", center.unreadCount(), "visibleCount", center.visibleCount(), "items", center.items().stream().map(this::notificationItemMap).toList(), "preferencesSummary", center.preferencesSummary().stream().map(this::notificationPreferenceMap).toList(), "sourceSummary", center.sourceSummary(), "redaction", center.redaction().name().toLowerCase(Locale.ROOT), "traceRefs", center.traceRefs(), "correlationId", center.correlationId(), "capabilityIds", List.of(NOTIFICATION_LIST_CAPABILITY, NOTIFICATION_MARK_READ_CAPABILITY, NOTIFICATION_DISMISS_CAPABILITY, NOTIFICATION_ARCHIVE_CAPABILITY, NOTIFICATION_SNOOZE_CAPABILITY, NOTIFICATION_UPDATE_PREFERENCES_CAPABILITY)),
        List.of(showNotificationCenterAction(), markNotificationReadAction(), dismissNotificationAction(), archiveNotificationAction(), snoozeNotificationAction(), updateNotificationPreferencesAction(), openAuditAction()));
  }

  private MyAccountNotificationCenter myAccountNotificationCenterData(AuthContextResolver.ResolvedMe actor, String correlationId) {
    seedStarterCoreAttention(actor, correlationId);
    for (var item : attentionService.listMyAccountItems(actor, correlationId).personalQueue()) notificationService.projectFromAttention(actor, item, correlationId);
    if (workstreamEventRepository != null) {
      workstreamEventRepository.listTenant(actor.selectedContext().tenantId()).stream()
          .filter(event -> event.customerId() == null || Objects.equals(event.customerId(), actor.selectedContext().customerId()))
          .filter(event -> event.capabilityRefs() != null && !event.capabilityRefs().isEmpty())
          .filter(event -> actor.selectedContext().capabilities().contains(event.capabilityRefs().get(0)))
          .forEach(event -> notificationService.projectFromWorkstreamEvent(actor, event, correlationId));
    }
    return notificationService.listMyAccountCenter(actor, correlationId);
  }

  private Map<String, Object> notificationItemMap(NotificationItem item) {
    return mapOf("notificationId", item.notificationId(), "channel", item.channel().name().toLowerCase(Locale.ROOT), "title", item.title(), "summary", item.summary(), "category", item.category() == null ? null : item.category().name().toLowerCase(Locale.ROOT), "priority", item.priority() == null ? null : item.priority().name().toLowerCase(Locale.ROOT), "status", item.status().name().toLowerCase(Locale.ROOT), "origin", item.origin(), "redactionLevel", item.redactionLevel().name().toLowerCase(Locale.ROOT), "requiredCapabilityId", item.requiredCapabilityId(), "owningWorkstreamId", item.owningWorkstreamId(), "surfaceRef", item.surfaceRef(), "sourceRefs", item.sourceRefs().stream().map(this::notificationSourceRefMap).toList(), "traceRefs", item.traceRefs(), "createdAt", item.createdAt() == null ? null : item.createdAt().toString(), "updatedAt", item.updatedAt() == null ? null : item.updatedAt().toString(), "lastChangedAt", item.lastChangedAt() == null ? null : item.lastChangedAt().toString(), "readAt", item.readAt() == null ? null : item.readAt().toString(), "dismissedAt", item.dismissedAt() == null ? null : item.dismissedAt().toString(), "archivedAt", item.archivedAt() == null ? null : item.archivedAt().toString(), "snoozedUntil", item.snoozedUntil() == null ? null : item.snoozedUntil().toString());
  }

  private Map<String, Object> notificationSourceRefMap(NotificationSourceRef ref) {
    return mapOf("refType", ref.sourceType(), "refId", ref.sourceId(), "label", ref.label(), "capabilityId", ref.requiredCapabilityId(), "traceId", ref.traceId(), "correlationId", ref.correlationId());
  }

  private Map<String, Object> notificationPreferenceMap(NotificationPreference pref) {
    return mapOf("preferenceId", pref.preferenceId(), "channel", pref.channel().name().toLowerCase(Locale.ROOT), "category", pref.category().name().toLowerCase(Locale.ROOT), "enabled", pref.enabled(), "minimumPriority", pref.minimumPriority().name().toLowerCase(Locale.ROOT), "muteUntil", pref.muteUntil() == null ? null : pref.muteUntil().toString(), "includeReadInCenter", pref.includeReadInCenter(), "updatedAt", pref.updatedAt().toString(), "updatedBy", pref.updatedBy(), "correlationId", pref.correlationId());
  }

  private Map<String, Object> emailNotificationPreferenceMap(EmailNotificationPreference pref) {
    return mapOf("preferenceId", pref.preferenceId(), "channel", "email", "category", pref.category().name().toLowerCase(Locale.ROOT), "enabled", pref.enabled(), "minimumPriority", pref.minimumPriority().name().toLowerCase(Locale.ROOT), "muteUntil", pref.muteUntil() == null ? null : pref.muteUntil().toString(), "digestMode", "immediate", "provider", "resend", "deliveryBoundary", "production_resend_or_local_captured_outbox", "updatedAt", pref.updatedAt().toString(), "updatedBy", pref.updatedBy(), "correlationId", pref.correlationId());
  }

  private Map<String, Object> notificationChannelRegistryMap(NotificationChannelRegistryEntry entry) {
    return mapOf("channel", entry.channel().name().toLowerCase(Locale.ROOT), "status", entry.status().name().toLowerCase(Locale.ROOT), "providerKind", entry.providerKind(), "productionConfigured", entry.productionConfigured(), "localTestOutboxAvailable", entry.localTestOutboxAvailable(), "deliveryCapabilityId", entry.deliveryCapabilityId(), "preferenceCapabilityId", entry.preferenceCapabilityId(), "statusReason", entry.statusReason());
  }

  private Map<String, Object> notificationDeliveryAttemptMap(NotificationDeliveryAttempt attempt) {
    return mapOf("attemptId", attempt.attemptId(), "channel", attempt.channel().name().toLowerCase(Locale.ROOT), "category", attempt.category() == null ? null : attempt.category().name().toLowerCase(Locale.ROOT), "sourceNotificationId", attempt.sourceNotificationId(), "destinationSummary", attempt.destinationSummary(), "providerKind", attempt.providerKind(), "status", attempt.status().name().toLowerCase(Locale.ROOT), "safeErrorSummary", attempt.safeErrorSummary(), "outboxId", attempt.outboxId(), "traceRefs", attempt.traceRefs(), "correlationId", attempt.correlationId(), "createdAt", attempt.createdAt() == null ? null : attempt.createdAt().toString(), "updatedAt", attempt.updatedAt() == null ? null : attempt.updatedAt().toString());
  }

  private Map<String, Object> notificationExternalOutboxMap(NotificationExternalOutboxMessage message) {
    return mapOf("outboxId", message.outboxId(), "channel", message.channel().name().toLowerCase(Locale.ROOT), "destinationSummary", message.destinationSummary(), "title", message.title(), "previewText", message.previewText(), "metadata", message.metadata(), "correlationId", message.correlationId(), "createdAt", message.createdAt() == null ? null : message.createdAt().toString());
  }


  private SurfaceEnvelope myProfileSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-profile", "detail-edit", "User profile", actor, correlationId,
        mapOf("surfaceContract", "my_account.profile.self_service.v1", "profileSummary", mapOf("displayName", actor.profile().displayName(), "email", actor.account().displayEmail(), "accountStatus", actor.account().status().name().toLowerCase(Locale.ROOT)), "providerBoundarySummary", "Email and authentication facts are owned by WorkOS/AuthKit identity reconciliation and are read-only in My Account.", "recordId", actor.account().accountId() + "-profile", "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(), "recordKind", "profile", "summary", "Current signed-in user profile. Administrative role and membership changes are intentionally not editable here.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", actor.account().displayEmail(), "editable", false, "inputType", "email", "disabledReason", "Email is owned by WorkOS/AuthKit identity reconciliation."), mapOf("fieldId", "locale", "label", "Locale", "value", "en-US", "editable", false, "inputType", "select", "disabledReason", "Locale changes are deferred beyond My Account."), mapOf("fieldId", "timeZone", "label", "Time zone", "value", "America/New_York", "editable", false, "inputType", "text", "disabledReason", "Time zone changes are deferred beyond My Account.")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY, "coreCapabilityAlias", CORE_PROFILE_UPDATE_CAPABILITY), "audit", mapOf("lastEventType", "UserProfileDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-profile"))), List.of(updateProfileAction(), openAuditAction()));
  }

  private SurfaceEnvelope mySettingsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-settings", "detail-edit", "User settings", actor, correlationId,
        mapOf("surfaceContract", "my_account.preferences.self_service.v1", "settingsSummary", mapOf("preferredThemeId", actor.settings().themeId().id(), "themeModel", "named-theme-selection"), "preferredThemeId", actor.settings().themeId().id(), "availableThemes", namedThemeOptions(), "notificationPreferenceSummary", "In-app notification preferences are managed in the notification center.", "digestPreferenceSummary", "Personal digest/export requests are governed task surfaces.", "recordId", actor.account().accountId() + "-settings", "recordLabel", actor.profile().displayName() + " settings", "recordKind", "settings", "summary", "Current signed-in user preferences for the workstream shell and notifications.", "fields", List.of(mapOf("fieldId", "preferredThemeId", "label", "Theme", "value", actor.settings().themeId().id(), "editable", true, "inputType", "select", "options", namedThemeOptions()), mapOf("fieldId", "notificationDigest", "label", "Notification digest", "value", "daily", "editable", false, "inputType", "select", "disabledReason", "Notification digest is deferred beyond My Account."), mapOf("fieldId", "composerDensity", "label", "Composer density", "value", "comfortable", "editable", false, "inputType", "select", "disabledReason", "Composer density is deferred beyond My Account.")), "version", 1, "permissionState", mapOf("canEdit", true, "authoritativeCapabilityId", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY), "audit", mapOf("lastEventType", "UserSettingsDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-settings"))), List.of(updateSettingsAction(), openAuditAction()));
  }

  private SurfaceEnvelope myContextSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return contextAuthoritySurface("surface-my-context", actor, correlationId);
  }

  private SurfaceEnvelope accessProfileContextSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return contextAuthoritySurface(ACCESS_PROFILE_CONTEXT_SURFACE_ID, actor, correlationId);
  }

  private SurfaceEnvelope contextAuthoritySurface(String surfaceId, AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_VIEW_CONTEXT", "selected context detail", correlationId);
    return envelope(surfaceId, "detail-edit", "Selected context and authority", actor, correlationId,
        mapOf("surfaceContract", "my_account.context_authority.v1", "canonicalSurfaceId", "surface-my-context", "selectedContext", mapOf("selectedContextId", actor.selectedContext().membershipId(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId()), "visibleCapabilitySummary", mapOf("count", actor.selectedContext().capabilities().size(), "capabilityIds", actor.selectedContext().capabilities()), "roleSummary", actor.selectedContext().roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(), "recordId", actor.selectedContext().membershipId(), "recordLabel", actor.selectedContext().tenantId() + " selected context", "recordKind", "auth-context", "summary", "Backend-resolved selected AuthContext, active membership, role/capability basis, and available context switch targets. Context selection is performed by /api/me and protected workstream APIs using X-Selected-Context-Id; the browser cannot grant authority by editing this surface.", "fields", List.of(mapOf("fieldId", "selectedContextId", "label", "Selected context", "value", actor.selectedContext().membershipId(), "editable", false, "inputType", "text", "disabledReason", "Context changes must be requested through /api/me or protected shell APIs."), mapOf("fieldId", "tenantId", "label", "Tenant", "value", actor.selectedContext().tenantId(), "editable", false, "inputType", "text"), mapOf("fieldId", "customerId", "label", "Customer", "value", actor.selectedContext().customerId(), "editable", false, "inputType", "text"), mapOf("fieldId", "roles", "label", "Roles", "value", actor.selectedContext().roles().toString(), "editable", false, "inputType", "text"), mapOf("fieldId", "capabilityCount", "label", "Visible capabilities", "value", actor.selectedContext().capabilities().size(), "editable", false, "inputType", "number")), "availableContexts", actor.memberships().stream().filter(membership -> membership.active()).map(membership -> mapOf("selectedContextId", membership.membershipId(), "tenantId", membership.tenantId(), "customerId", membership.customerId(), "status", membership.status().name().toLowerCase(), "roleIds", membership.roles().stream().map(role -> role.name().toLowerCase().replace('_', '-')).sorted().toList(), "selectable", true, "selectionApi", "/api/me?selectedContextId=" + membership.membershipId(), "capabilityId", CORE_ACCESS_CONTEXT_SELECT_CAPABILITY)).toList(), "permissionState", mapOf("canEdit", false, "authoritativeCapabilityId", MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY, "coreCapabilityAlias", CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), "capabilityAliases", List.of(CORE_ACCESS_ME_CAPABILITY, CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), "traceRefs", myAccountService.traceRefs(actor, correlationId), "audit", mapOf("lastEventType", "AuthContextDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-context"))),
        List.of(showDashboardAction(), showProfileAction(), showSettingsAction(), selectContextAction(), openAuditAction()));
  }

  private SurfaceEnvelope myAccountOpenDeniedSurface(AuthContextResolver.ResolvedMe actor, MyAccountService.OpenWorkstreamDecision decision, String correlationId) {
    return envelope("surface-my-account-open-denied", "system_message", "Workstream unavailable", actor, correlationId,
        mapOf("status", "not_found_or_redacted", "severity", "warning", "title", "Workstream unavailable", "message", decision.message(), "capabilityId", MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "safeReasonCode", decision.safeReasonCode(), "recoverySteps", List.of("Review the selected context and authority basis in My Account.", "Ask an administrator for access if this workstream should be available."), "traceRefs", decision.traceIds(), "redaction", "target workstream details are redacted when unauthorized"),
        List.of(showProfileAction(), showSettingsAction(), openAuditAction()));
  }

  private CapabilityActionResult personalAttentionDigestActionResult(MyAccountPersonalAttentionDigestTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), personalAttentionDigestSurface(actor, task, correlationId));
  }

  private SurfaceEnvelope personalAttentionDigestSurface(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask task, String correlationId) {
    if (task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME) return personalAttentionDigestBlockedSurface(actor, task, correlationId);
    var completed = task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY || task.status() == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED || task.status() == MyAccountPersonalAttentionDigestTask.Status.REJECTED;
    return envelope(completed ? "surface-my-account-personal-attention-digest-result" : "surface-my-account-personal-attention-digest-progress", completed ? "outcome-panel" : "workflow-status", completed ? "Personal attention digest result" : "Personal attention digest progress", actor, correlationId,
        mapOf("surfaceContract", completed ? "my_account.personal_attention_digest.result.v1" : "my_account.personal_attention_digest.progress.v1", "digestTaskId", task.digestTaskId(), "autonomousAgentTaskId", task.autonomousAgentTaskId(), "status", task.status().name().toLowerCase(Locale.ROOT), "progressPercent", task.progressPercent(), "summary", task.summary(), "authorizedAttentionCount", task.authorizedAttentionCount(), "sectionRefs", task.sectionRefs(), "evidenceRefs", task.evidenceRefs(), "traceRefs", task.traceIds(), "redaction", "Authorized personal attention evidence only; hidden workstreams/items are not counted or named.", "noDirectMutation", true, "safety", "This digest is advisory. Source attention remains authoritative and source item lifecycle changes require separate governed capabilities."),
        completed ? List.of(readPersonalAttentionDigestAction(), acceptPersonalAttentionDigestAction(), rejectPersonalAttentionDigestAction(), openAuditAction()) : List.of(readPersonalAttentionDigestAction(), cancelPersonalAttentionDigestAction(), openAuditAction()));
  }

  private SurfaceEnvelope personalAttentionDigestBlockedSurface(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask task, String correlationId) {
    return envelope("surface-my-account-personal-attention-digest-blocked", "system_message", "Personal attention digest blocked", actor, correlationId,
        mapOf("surfaceContract", "my_account.personal_attention_digest.blocked.v1", "digestTaskId", task == null ? "" : task.digestTaskId(), "autonomousAgentTaskId", task == null ? "" : task.autonomousAgentTaskId(), "status", "blocked_provider_or_runtime", "severity", "blocked", "title", "Provider/runtime configuration is required", "message", task == null ? "My Account personal attention digest fails closed until backend provider/runtime configuration is available." : task.summary(), "blockerCode", task == null ? "blocked_provider_or_runtime" : task.blockerCode(), "recoverySteps", List.of("Configure the Akka AutonomousAgent provider/runtime and governed tool grants.", "Retry from My Account after readiness is restored."), "traceRefs", task == null ? List.of("trace-my-account-personal-attention-digest-blocked") : task.traceIds(), "noFakeSuccess", true, "noDirectMutation", true, "redaction", "No deterministic, fake, fixture, simulated, or model-less personal attention digest success is returned."),
        List.of(showDashboardAction(), readPersonalAttentionDigestAction(), openAuditAction()));
  }

  private SurfaceEnvelope personalAttentionDigestEmptyProgressSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-account-personal-attention-digest-progress", "workflow-status", "Personal attention digest", actor, correlationId,
        mapOf("surfaceContract", "my_account.personal_attention_digest.progress.v1", "status", "not_started", "summary", "Start a backend-governed My Account personal attention digest to summarize only authorized personal attention evidence.", "authorizedAttentionCount", 0, "traceRefs", List.of("trace-my-account-personal-attention-digest-not-started"), "redaction", "Hidden workstreams/items are not counted or named.", "noDirectMutation", true),
        List.of(startPersonalAttentionDigestAction(), openAuditAction()));
  }

  private SurfaceEnvelope dashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_VIEW_OVERVIEW, "user_admin.dashboard.v1", correlationId);
    var users = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var invites = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var pendingInvites = invites.stream().filter(WorkstreamService::isPendingInvitation).count();
    var failedInvites = invites.stream().filter(invite -> invite.deliveryStatus().name().equals("FAILED")).count();
    seedUserAdminInvitationAttention(actor, failedInvites, correlationId);
    var attentionItems = attentionMaps(attentionService.listWorkstreamItems(actor, USER_ADMIN_AGENT_ID, correlationId));
    var expiringSupport = users.stream().filter(UserDirectoryView.UserDirectoryRow::supportAccess).count();
    return envelope("surface-user-admin-dashboard", "dashboard", "User Admin Dashboard", actor, correlationId,
        mapOf("surfaceContract", "user_admin.dashboard.v1", "readiness", mapOf("directory", "backend-derived", "invitationOutbox", "backend-derived", "supportAccess", "backend-derived", "adminAudit", "backend-derived", "accessReviewWorker", "autonomous_agent_runtime_projected"), "cards", List.of(mapOf("cardId", "card-pending-invitations", "label", "Pending invitations", "value", pendingInvites, "severity", pendingInvites == 0 ? "info" : "warning"), mapOf("cardId", "card-active-users", "label", "Active users", "value", users.size(), "severity", "info"), mapOf("cardId", "card-failed-invitations", "label", "Failed invitation delivery", "value", failedInvites, "severity", failedInvites == 0 ? "info" : "warning"), mapOf("cardId", "card-support-access", "label", "Expiring support access", "value", expiringSupport, "severity", expiringSupport == 0 ? "info" : "warning"), mapOf("cardId", "card-admin-audit", "label", "Recent denied admin actions", "value", userAdminService.auditEvents(actor, 25, correlationId).stream().filter(event -> event.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.DENIED).count(), "severity", "info"), mapOf("cardId", "card-access-review", "label", "Access review items", "value", 0, "severity", "blocked_provider_or_runtime")), "attentionItems", attentionItems, "attentionSource", AttentionService.LIST_WORKSTREAM_ITEMS_TOOL, "capabilityIds", List.of(USERADMIN_VIEW_OVERVIEW, USERADMIN_LIST_MEMBERS, USERADMIN_LIST_INVITATIONS, USERADMIN_SEND_INVITATION, USERADMIN_RESEND_INVITATION, USERADMIN_REVOKE_INVITATION, USERADMIN_LIST_ROLES_CAPABILITIES, USERADMIN_UPDATE_MEMBER_STATUS, USERADMIN_DISABLE_ACCOUNT, USERADMIN_REACTIVATE_ACCOUNT, USERADMIN_SUPPORT_ACCESS_READ, USERADMIN_SUPPORT_ACCESS_GRANT, USERADMIN_SUPPORT_ACCESS_REVOKE, USERADMIN_SUPPORT_ACCESS_EXTEND, USERADMIN_ACCESS_REVIEW_START, USERADMIN_ACCESS_REVIEW_READ, USERADMIN_ACCESS_REVIEW_CANCEL, USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT, USERADMIN_ACCESS_REVIEW_REJECT_RESULT)),
        List.of(displayListAction()));
  }

  private SurfaceEnvelope listSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_MEMBERS, "user_admin.users.v1", correlationId);
    var rows = new ArrayList<Map<String, Object>>();
    var users = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var invites = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    for (var user : users) rows.add(mapOf("id", user.accountId(), "accountId", user.accountId(), "membershipId", user.membershipId(), "rowType", "active-user", "email", user.accountId(), "displayName", user.displayName(), "role", roleLabels(user.roles()), "status", user.membershipStatus().name().toLowerCase(), "supportAccess", user.supportAccess(), "supportAccessExpiresAt", user.expiresAt() == null ? "" : user.expiresAt().toString(), "traceId", "trace-useradmin-user-" + stableSuffix(user.accountId())));
    for (var invite : invites) rows.add(invitationRow(invite));
    return envelope("surface-user-admin-users", "list-search", "Users", actor, correlationId, mapOf("surfaceContract", "user_admin.users.v1", "query", "", "rows", rows, "pageInfo", mapOf("activeUserCount", users.size(), "invitationCount", invites.size(), "totalKnownCount", rows.size()), "emptyMessage", "No active users or invitations are visible in this scope."), List.of(displayDetailAction(), displayInvitationDetailAction(), inviteAction(), openAuditAction()));
  }


  private SurfaceEnvelope detailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return detailSurface(actor, null, correlationId);
  }

  private SurfaceEnvelope detailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_MEMBERS, "user_admin.user_detail.v1", correlationId);
    var accountId = stringInput(input, "accountId", actor.account().accountId());
    var membershipId = stringInput(input, "membershipId", actor.selectedContext().membershipId());
    var actorMembership = selectedMembership(actor);
    var target = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(user -> Objects.equals(user.accountId(), accountId) || Objects.equals(user.membershipId(), membershipId))
        .findFirst()
        .orElse(new UserDirectoryView.UserDirectoryRow(actor.account().accountId(), actor.profile().displayName(), actor.selectedContext().membershipId(), actor.selectedContext().roles(), MembershipStatus.ACTIVE, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actorMembership != null && actorMembership.supportAccess(), actorMembership == null ? null : actorMembership.expiresAt()));
    var roleValue = roleLabels(target.roles());
    var traceId = "trace-user-admin-detail-" + stableSuffix(target.accountId());
    return envelope("surface-user-admin-user-detail", "detail-edit", "User detail", actor, correlationId, mapOf("surfaceContract", "user_admin.user_detail.v1", "recordId", target.accountId(), "recordLabel", target.displayName(), "recordKind", "account", "summary", "Manage this user's role, status, and recent admin activity.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Name", "value", target.displayName(), "editable", true, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", target.accountId(), "editable", false, "inputType", "email", "disabledReason", "Email is managed by identity provider."), mapOf("fieldId", "membershipStatus", "label", "Status", "value", target.membershipStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleValue, "editable", false, "inputType", "text")), "version", 1, "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId()), "permissionState", mapOf("canEdit", true, "reason", "Changes are checked by backend policy before they are applied.", "authoritativeCapabilityId", USERADMIN_CHANGE_MEMBER_ROLES), "accessManagement", mapOf("advisoryNotice", "Frontend controls are advisory only; backend UserAdminService remains authoritative for membership, account, role, support-access, access-review, and audit actions.", "memberStatus", mapOf("accountStatus", "active", "membershipStatus", target.membershipStatus().name().toLowerCase(Locale.ROOT), "statusActionIds", List.of("action-useradmin-disable-member", "action-useradmin-reactivate-member", "action-useradmin-disable-account", "action-useradmin-reactivate-account"), "denialHints", List.of("disabled actor", "inactive membership", "cross-tenant", "CUSTOMER_ADMIN_TENANT_ACTION_DENIED", "SAAS_OWNER_NO_SUPPORT_ACCESS", "last-admin"), "noOpMessage", "Repeated status changes return no-op/idempotent evidence where state already matches.", "idempotencyKeySource", "client-generated", "traceLinks", List.of("trace-useradmin-status-action", traceId)), "supportAccess", mapOf("supportAccess", target.supportAccess(), "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "actionIds", List.of("action-useradmin-read-support-access", "action-useradmin-grant-support-access", "action-useradmin-revoke-support-access", "action-useradmin-extend-support-access"), "denialHints", List.of("tenant-created support grant required", "SAAS_OWNER_NO_SUPPORT_ACCESS", "support-access-expired", "missing-capability"), "traceLinks", List.of("trace-useradmin-support-access"))), "audit", mapOf("lastEventType", "UserAdminDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of(traceId, "trace-useradmin-support-access"))), List.of(displayListAction(), updateMemberStatusAction(), reactivateMemberStatusAction(), permanentlyRemoveUserAction(), disableAccountAction(), reactivateAccountAction(), grantSupportAccessAction(), revokeSupportAccessAction(), extendSupportAccessAction(), previewRoleChangeAction(), changeMemberRolesAction(), openAuditAction()));
  }

  private SurfaceEnvelope invitationDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_INVITATIONS, "user_admin.invitation_detail.v1", correlationId);
    var invitationId = stringInput(input, "invitationId", latestInvitationId(actor));
    var invite = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(row -> invitationId.equals(row.invitationId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
    return envelope("surface-user-admin-invitation-detail", "detail-edit", "Invitation detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_detail.v1", "recordId", invite.invitationId(), "recordLabel", invite.targetEmail(), "recordKind", "invitation", "summary", "Review this invitation and manage resend or revoke actions.", "actionContext", mapOf("invitationId", invite.invitationId()), "fields", List.of(mapOf("fieldId", "email", "label", "Email", "value", invite.targetEmail(), "editable", false, "inputType", "email"), mapOf("fieldId", "status", "label", "Status", "value", invite.status().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleLabels(invite.requestedRoles()), "editable", false, "inputType", "text"), mapOf("fieldId", "delivery", "label", "Delivery", "value", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "expiresAt", "label", "Expires", "value", invite.expiresAt().toString(), "editable", false, "inputType", "text")), "audit", mapOf("lastEventType", "InvitationDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-useradmin-invitation-" + stableSuffix(invite.invitationId())))),
        List.of(displayListAction(), resendInvitationAction(), revokeInvitationAction(), openAuditAction()));
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
    seedStarterCoreAttention(actor, correlationId);
    var surface = auditTraceService.dashboard(actor, correlationId);
    surface = new AuditTraceService.SurfaceData(surface.surfaceId(), surface.surfaceType(), surface.title(), surface.traceIds(), withAttentionItems(surface.data(), actor, AUDIT_TRACE_AGENT_ID, correlationId));
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
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceTimelineAction(), auditTraceInvestigationGuideAction(), auditTraceAppendInvestigationNoteAction()));
  }

  private SurfaceEnvelope auditTraceInvestigationGuideSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.investigationGuide(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSummaryTaskBlockedAction()));
  }

  private SurfaceEnvelope auditTraceInvestigationNoteSurface(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    var surface = auditTraceService.appendInvestigationNote(actor, input, idempotencyKey, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceInvestigationGuideAction()));
  }

  private SurfaceEnvelope auditTraceSummaryTaskBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "audit summary worker readiness blocked_provider_or_runtime", correlationId);
    return envelope("surface-audit-trace-summary-progress", "workflow-status", "Audit summary worker progress", actor, correlationId,
        mapOf("surfaceContract", "audit.trace.summaryProgress.v1", "workflowId", "audit-trace-summary-task", "status", "blocked_provider_or_runtime", "summary", "Manual audit summary task start is surfaced through backend-governed progress/review contracts; this starter fails closed when provider/runtime/tool-boundary configuration is unavailable.", "traceIds", List.of("trace-audit-summary-worker-blocked"), "requiredCapabilityId", AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "providerFailures", List.of("blocked_provider_or_runtime"), "blockers", List.of(mapOf("code", "blocked_provider_or_runtime", "message", "Configure the AuditTraceSummaryAutonomousAgent runtime, model provider, ToolPermissionBoundary grants, readSkill/readReferenceDoc, and auditTraceSummaryEvidence.read before model-backed summaries can complete.")), "redactionSummary", "Raw JWTs, provider credentials, hidden prompts, raw tool payloads, invitation tokens, and cross-tenant evidence are omitted.", "noDirectMutation", true, "safety", "Audit summary output cannot mutate traces, policy, users, provider configuration, authorization, redaction, or tenant filtering; no deterministic or model-less successful worker result is exposed."),
        List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceSummaryTaskBlockedAction()));
  }

  private SurfaceEnvelope auditTraceEnvelope(AuthContextResolver.ResolvedMe actor, String correlationId, AuditTraceService.SurfaceData surface, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(surface.surfaceId(), surface.surfaceType(), "v1", surface.title(), AUDIT_TRACE_AGENT_ID, reusableAgentsForSurface(surface.surfaceId()), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, surface.traceIds(), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload")), surface.data(), actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + surface.surfaceId(), "rel", "deep-link")));
  }

  private SurfaceEnvelope accessReviewSurface(AuthContextResolver.ResolvedMe actor, AccessReviewTask task, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_ACCESS_REVIEW_READ, "user_admin.access_review_task.v1", correlationId);
    return envelope("surface-user-admin-access-review-task", "workflow-status", "User Admin access review", actor, correlationId,
        mapOf("surfaceContract", "user_admin.access_review_task.v1", "workflowId", task.taskId(), "taskId", task.taskId(), "autonomousAgentTaskId", task.autonomousAgentTaskId(), "status", accessReviewStatus(task), "initiatingCapabilityId", USERADMIN_ACCESS_REVIEW_START, "scope", mapOf("scopeType", task.scopeType().name(), "tenantId", task.tenantId(), "customerId", task.customerId()), "progress", mapOf("percent", task.progressPercent(), "summary", task.summary()), "resultSummary", task.status() == AccessReviewTask.Status.COMPLETED ? task.summary() : null, "blockers", task.blockerCode() == null ? List.of() : List.of(mapOf("code", task.blockerCode(), "message", "Governed AutonomousAgent provider/runtime is not configured; the starter fails closed instead of returning model-less access-review recommendations.")), "evidenceRefs", task.evidenceRefs(), "recommendations", task.recommendationRefs(), "resultReviewState", accessReviewResultReviewState(task), "providerFailures", task.blockerCode() == null ? List.of() : List.of("blocked_provider_or_runtime"), "traceIds", task.traceIds(), "noDirectMutation", true, "safety", "access-review output cannot directly mutate memberships, invitations, roles, capabilities, authorization state, or provider configuration; User Admin human review is required before follow-up changes"),
        List.of(startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), acceptAccessReviewResultAction(), rejectAccessReviewResultAction(), traceAction()));
  }

  private SurfaceEnvelope accessReviewBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_ACCESS_REVIEW_START, "access-review autonomous task unavailable", correlationId);
    return envelope("surface-user-admin-access-review-task", "workflow-status", "User Admin access review", actor, correlationId,
        mapOf("surfaceContract", "user_admin.access_review_task.v1", "workflowId", "user-admin-access-review", "status", "blocked_provider_or_runtime", "summary", "Start creates a durable task record, but worker execution remains blocked until governed AutonomousAgent provider/runtime configuration is enabled.", "traceIds", List.of("trace-useradmin-access-review-blocked"), "requiredCapabilityId", USERADMIN_ACCESS_REVIEW_START, "noDirectMutation", true),
        List.of(startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), traceAction()));
  }

  private String accessReviewStatus(AccessReviewTask task) {
    return task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : task.status().name().toLowerCase();
  }

  private String accessReviewResultReviewState(AccessReviewTask task) {
    if (task.decision() != null && !task.decision().isBlank()) return task.decision().equals("rejected") ? "result_rejected" : "result_" + task.decision();
    return switch (task.status()) {
      case COMPLETED -> "completed_review_required";
      case ACCEPTED -> "result_accepted";
      case REJECTED -> "result_rejected";
      case CANCELLED -> "cancelled";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "blocked_provider_or_runtime";
      default -> "pending_worker_result";
    };
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
    seedStarterCoreAttention(actor, correlationId);
    var surface = governancePolicyService.dashboard(actor, correlationId);
    surface = new GovernancePolicyService.SurfaceData(surface.surfaceId(), surface.surfaceType(), surface.title(), surface.traceIds(), withAttentionItems(surface.data(), actor, GOVERNANCE_POLICY_AGENT_ID, correlationId));
    return governancePolicyEnvelope(actor, correlationId, surface, List.of(governanceDashboardAction(), governanceListPoliciesAction(), governanceDraftProposalAction(), governanceSimulateProposalAction(), governanceStartImpactAnalysisAction(), openAuditAction()));
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
    var simulation = governancePolicyService.simulateProposal(actor, input, "surface-fallback-simulation-" + stableSuffix(correlationId), correlationId);
    return governancePolicyEnvelope(actor, correlationId, simulation.surface(), List.of(governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), openAuditAction()));
  }

  private SurfaceEnvelope governancePolicyDecisionSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return envelope("surface-governance-policy-decision", "decision", "Governance decision", actor, correlationId,
        mapOf("decisionId", "decision-" + stableSuffix(correlationId), "decision", stringInput(input, "decision", "approve"), "actor", actor.account().accountId(), "authorityBasis", GOVERNANCE_POLICY_APPROVE_CAPABILITY, "rationale", stringInput(input, "rationale", "Human governance review recorded."), "result", "approval-recorded-activation-still-separate", "auditCorrelationId", correlationId),
        List.of(governanceActivateProposalAction(), governanceRollbackPolicyAction(), governanceOutcomeNoteAction(), openAuditAction()));
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
    return envelope("surface-governance-policy-impact-analysis-task", "workflow-status", "Policy impact analysis readiness", actor, correlationId,
        mapOf("surfaceContract", "governance.policy.impact_analysis.task.v1", "workflowId", "governance-policy-impact-analysis", "taskId", null, "status", "blocked_provider_or_runtime", "summary", "Policy-impact analysis is a durable AutonomousAgent worker path. This readiness surface is backend-derived and fail-closed until provider/model/runtime binding, governed evidence grants, and ToolPermissionBoundary checks are satisfied.", "readinessDecision", "provider_runtime_blocked_fail_closed", "workerSelection", mapOf("justified", true, "reason", "impact analysis spans proposal state, capability inventory, trace evidence, model/provider work, progress, cancellation, and human result review", "currentRuntime", "blocked_provider_or_runtime"), "providerFailures", List.of("blocked_provider_or_runtime"), "blockers", List.of(mapOf("code", "provider_or_model_not_configured_for_worker", "message", "The starter must fail closed rather than return deterministic/model-less successful analysis."), mapOf("code", "tool_boundary_not_granted", "message", "Worker must use read-only evidence tools and cannot gain mutation authority.")), "evidenceRefs", List.of("GovernancePolicyService.proposal", "GovernancePolicyService.simulation", "governancePolicyEvidence.read", "readSkill:governance-policy-impact-analysis", "readReferenceDoc:governance-policy-impact-analysis", "audit.trace.read"), "forbiddenEffects", List.of("approve", "activate", "rollback", "mutate policy", "mutate users", "mutate agent behavior", "change provider config", "bypass authorization"), "traceIds", List.of("trace-governance-policy-impact-analysis-blocked"), "requiredCapabilityId", GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY, "readCapabilityId", GovernancePolicyImpactService.READ_CAPABILITY, "noDirectMutation", true, "activationBlockedUntilHumanDecision", true, "noFakeSuccess", true, "targetResultSurfaceId", "surface-governance-policy-impact-analysis-result"),
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
    seedStarterCoreAttention(actor, correlationId);
    return envelope("surface-agent-admin-catalog", "list-search", "Agent Admin catalog", actor, correlationId, withAttentionItems(agentAdminService.catalog(actor, correlationId), actor, AGENT_ADMIN_AGENT_ID, correlationId), List.of(displayAgentCatalogAction(), openAgentDetailAction(), activateAgentDefinitionAction(), deactivateAgentDefinitionAction(), listAgentSeedMaterialAction(), importAgentSeedDefaultsAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-admin-detail", "detail-edit", "Agent Admin readiness detail", actor, correlationId, agentAdminService.definitionDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId), List.of(activateAgentDefinitionAction(), deactivateAgentDefinitionAction(), proposePromptDiffAction(), testPromptAction(), manageModelRefAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentPromptGovernanceSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var data = new LinkedHashMap<>(agentAdminService.promptDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId));
    data.put("behaviorDiffSurfaceContract", "surface.agent_admin.behavior_diff.v1");
    return envelope("surface-agent-prompt-governance", "governance-diff", "Prompt governance review", actor, correlationId, data, List.of(proposePromptDiffAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentSkillVersionSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-skill-version", "governance-diff", "Skill version review", actor, correlationId, agentAdminService.skillDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, null, correlationId), List.of(approveSkillManifestAction(), openAgentTraceAction()));
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
    return envelope("surface-agent-seed-material", "list-search", "Agent seed material", actor, correlationId, agentAdminService.seedMaterialDetail(actor, correlationId), List.of(importAgentSeedDefaultsAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentTestConsoleSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, correlationId, "No-side-effect Agent Admin test console"));
    var allowedSkill = agentRuntimeService.readSkill(new AgentRuntimeService.SkillReadRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, correlationId, "agent-admin.starter-guidance.v1"));
    var deniedSkill = agentRuntimeService.readSkill(new AgentRuntimeService.SkillReadRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, correlationId, "ua.role-recommendation.v1"));
    var traceIds = List.of(prompt.traceId(), allowedSkill.traceId(), deniedSkill.traceId(), "trace-agent-work-88");
    return envelope("surface-agent-test-console", "workflow-status", "No-side-effect agent test console", actor, correlationId, mapOf("surfaceContract", "agent_admin.no_side_effect_test.v1", "surfaceContractAliases", List.of("surface.agent_admin.test_console.v1"), "capabilityAliases", List.of("agent.runtime.test", "agent.read_skill"), "workflowId", "agent-runtime-test", "status", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED && allowedSkill.decision() == AgentRuntimeTrace.Decision.ALLOWED && deniedSkill.decision() == AgentRuntimeTrace.Decision.DENIED ? "completed" : "blocked", "steps", List.of(mapOf("stepId", "prompt-assembly", "label", "PromptAssemblyTrace", "status", prompt.decision().name(), "traceId", prompt.traceId()), mapOf("stepId", "assigned-skill-load", "label", "SkillLoadTrace allowed readSkill(skillId)", "status", allowedSkill.decision().name(), "traceId", allowedSkill.traceId()), mapOf("stepId", "unassigned-skill-denial", "label", "SkillLoadTrace denied unassigned skill", "status", deniedSkill.decision().name(), "traceId", deniedSkill.traceId()), mapOf("stepId", "agent-work", "label", "AgentWorkTrace", "status", "no production side effects")), "traceIds", traceIds, "noProductionSideEffects", true), List.of(testPromptAction(), openAgentTraceAction()));
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
    return List.of();
  }

  private List<WorkstreamEvent> eventBackedRefreshEvents(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var envelopes = new ArrayList<>(workstreamEventRepository.listTenant(actor.selectedContext().tenantId()));
    envelopes.sort(java.util.Comparator.comparing(WorkstreamEventEnvelope::occurredAt));
    var sequence = new AtomicInteger(100);
    return envelopes.stream()
        .filter(event -> Objects.equals(event.customerId(), actor.selectedContext().customerId()))
        .filter(event -> event.capabilityRefs().stream().anyMatch(capability -> actor.selectedContext().capabilities().contains(capability)))
        .map(event -> new WorkstreamEvent(
            event.eventId(),
            "projection.refresh.available",
            event.tenantId(),
            event.customerId(),
            event.owningWorkstreamId(),
            event.targetSurfaceId(),
            surfaceTypeForEventBackedRefresh(event.targetSurfaceId()),
            "v1",
            event.correlationId(),
            event.traceRefs(),
            event.occurredAt().toString(),
            sequence.getAndIncrement(),
            mapOf(
                "reason", "Bounded SSE replay v1 found a backend event-backed projection refresh; reload backend-owned attention/dashboard surfaces instead of trusting frontend state or assuming a continuously live stream.",
                "source", "workstream.event.delivery.refresh",
                "eventType", event.eventType(),
                "eventFamily", event.eventFamily(),
                "idempotencyKey", event.idempotencyKey(),
                "sourceRefs", event.sourceRefs().stream().map(ref -> mapOf("refType", ref.refType(), "refId", ref.refId(), "capabilityId", ref.capabilityId(), "traceId", ref.traceId())).toList())))
        .toList();
  }

  private String surfaceTypeForEventBackedRefresh(String surfaceId) {
    if (surfaceId == null || surfaceId.isBlank()) return "dashboard";
    if (surfaceId.contains("invitation-panel") || surfaceId.contains("user-admin-list") || surfaceId.contains("notification-center")) return "list-search";
    if (surfaceId.contains("access-review") || surfaceId.contains("digest") || surfaceId.contains("summary") || surfaceId.contains("impact-analysis")) return "workflow-status";
    if (surfaceId.contains("agent-admin") || surfaceId.contains("governance-policy") || surfaceId.contains("audit-trace")) return "dashboard";
    return "dashboard";
  }

  private SurfaceEnvelope dynamicSurface(AuthContextResolver.ResolvedMe actor, String surfaceId, String correlationId) {
    return switch (surfaceId) {
      case "surface-my-account-dashboard" -> myAccountDashboardSurface(actor, correlationId);
      case "surface-my-profile" -> myProfileSurface(actor, correlationId);
      case "surface-my-settings" -> mySettingsSurface(actor, correlationId);
      case "surface-my-context" -> myContextSurface(actor, correlationId);
      case ACCESS_PROFILE_CONTEXT_SURFACE_ID -> accessProfileContextSurface(actor, correlationId);
      case "surface-my-account-notification-center" -> myAccountNotificationCenterSurface(actor, correlationId);
      case "surface-my-account-personal-attention-digest-progress" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "surface-my-account-personal-attention-digest-result" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "surface-my-account-personal-attention-digest-blocked" -> personalAttentionDigestBlockedSurface(actor, null, correlationId);
      case "surface-user-admin-dashboard" -> dashboardSurface(actor, correlationId);
      case "surface-user-admin-users" -> listSurface(actor, correlationId);
      case "surface-user-admin-user-detail" -> detailSurface(actor, correlationId);
      case "surface-user-admin-invitation-detail" -> invitationDetailSurface(actor, null, correlationId);
      case "surface-user-admin-access-review-task" -> accessReviewBlockedSurface(actor, correlationId);
      case "surface-agent-admin-catalog" -> agentAdminCatalogSurface(actor, correlationId);
      case "surface-agent-admin-detail" -> agentAdminDetailSurface(actor, correlationId);
      case "surface-agent-prompt-governance" -> agentPromptGovernanceSurface(actor, correlationId);
      case "surface-agent-skill-version" -> agentSkillVersionSurface(actor, correlationId);
      case "surface-agent-skill-manifest-diff" -> agentSkillManifestSurface(actor, correlationId);
      case "surface-agent-tool-boundary-diff" -> agentToolBoundarySurface(actor, correlationId);
      case "surface-agent-model-refs" -> agentModelRefsSurface(actor, correlationId);
      case "surface-agent-seed-material" -> agentSeedMaterialSurface(actor, correlationId);
      case "surface-agent-test-console" -> agentTestConsoleSurface(actor, correlationId);
      case "surface-agent-behavior-proposal" -> agentBehaviorProposalSurface(actor, correlationId);
      case "surface-agent-admin-trace" -> agentAdminTraceSurface(actor, correlationId);
      case "surface-audit-trace-dashboard" -> auditTraceDashboardSurface(actor, correlationId);
      case "surface-audit-trace-search" -> auditTraceSearchSurface(actor, null, correlationId);
      case "surface-audit-trace-detail" -> auditTraceDetailSurface(actor, null, correlationId);
      case "surface-audit-trace-timeline", "surface-audit-timeline" -> auditTraceCorrelationTimelineSurface(actor, null, correlationId);
      case "surface-audit-trace-failure-evidence" -> auditTraceFailureEvidenceSurface(actor, null, correlationId);
      case "surface-audit-trace-investigation-guide" -> auditTraceInvestigationGuideSurface(actor, null, correlationId);
      case "surface-audit-trace-investigation-note" -> auditTraceInvestigationNoteSurface(actor, null, null, correlationId);
      case "surface-audit-trace-summary-task", "surface-audit-trace-summary-progress" -> auditTraceSummaryTaskBlockedSurface(actor, correlationId);
      case "surface-governance-policy-dashboard" -> governancePolicyDashboardSurface(actor, correlationId);
      case "surface-governance-policy-inventory" -> governancePolicyInventorySurface(actor, correlationId);
      case "surface-governance-policy-detail" -> governancePolicyDetailSurface(actor, null, correlationId);
      case "surface-governance-policy-proposal" -> governancePolicyProposalSurface(actor, correlationId, "draft", "Draft proposal is inert until review, simulation, approval, and activation.");
      case "surface-governance-policy-simulation" -> governancePolicySimulationSurface(actor, null, correlationId);
      case "surface-governance-policy-decision" -> governancePolicyDecisionSurface(actor, null, correlationId);
      case "surface-governance-policy-activation-blocked" -> governancePolicyActivationBlockedSurface(actor, correlationId);
      case "surface-governance-policy-rollback-blocked" -> governancePolicyRollbackBlockedSurface(actor, correlationId);
      case "surface-governance-policy-impact-analysis-task" -> governancePolicyImpactAnalysisBlockedSurface(actor, correlationId);
      default -> null;
    };
  }

  private List<MeResponse.FunctionalAgentSummary> functionalAgentsWithBackendAttention(AuthContextResolver.ResolvedMe actor, List<MeResponse.FunctionalAgentSummary> agents, String correlationId) {
    seedStarterCoreAttention(actor, correlationId);
    var summaries = attentionService.listRailSummaries(actor, correlationId).stream()
        .collect(Collectors.toMap(AttentionService.WorkstreamAttentionSummary::workstreamId, summary -> summary, (left, right) -> left, LinkedHashMap::new));
    return agents.stream().map(agent -> {
      var summary = summaries.get(agent.functionalAgentId());
      if (summary == null || summary.attentionCount() <= 0) return agent.withAttention(null);
      return agent.withAttention(new MeResponse.FunctionalAgentSummary.FunctionalAgentAttention(
          summary.attentionCount(),
          summary.highestSeverity().name().toLowerCase(Locale.ROOT),
          AttentionService.LIST_RAIL_SUMMARIES_TOOL));
    }).toList();
  }

  private void seedStarterCoreAttention(AuthContextResolver.ResolvedMe actor, String correlationId) {
    if (actor.selectedContext().capabilities().contains(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY)) {
      attentionService.upsertItem(actor, attentionItem(actor, "attention-agent-admin-readiness", AGENT_ADMIN_AGENT_ID, "Agent Admin provider readiness is blocked", "Model/runtime provider readiness is blocked until governed provider configuration is available.", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED, AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, "surface-agent-admin-catalog", "agent-admin-provider-readiness", correlationId), correlationId);
    }
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_READ_CAPABILITY)) {
      attentionService.upsertItem(actor, attentionItem(actor, "attention-governance-policy-approval", GOVERNANCE_POLICY_AGENT_ID, "Governance policy decision awaits authorized review", "Governance/Policy has reviewable policy approval evidence for this selected context.", AttentionCategory.GOVERNANCE_APPROVAL, AttentionSeverity.URGENT, GOVERNANCE_POLICY_READ_CAPABILITY, "surface-governance-policy-dashboard", "governance-policy-approval", correlationId), correlationId);
    }
    if (actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) {
      attentionService.upsertItem(actor, attentionItem(actor, "attention-audit-trace-failure-evidence", AUDIT_TRACE_AGENT_ID, "Audit/Trace has provider failure evidence available", "Audit/Trace has provider failure or denial evidence available for authorized investigation.", AttentionCategory.AUDIT_FAILURE_EVIDENCE, AttentionSeverity.WARNING, AUDIT_TRACE_READ_CAPABILITY, "surface-audit-trace-dashboard", "audit-trace-failure-evidence", correlationId), correlationId);
    }
  }

  private void seedUserAdminInvitationAttention(AuthContextResolver.ResolvedMe actor, long failedInvites, String correlationId) {
    if (failedInvites <= 0 || !actor.selectedContext().capabilities().contains(USER_ADMIN_CAPABILITY)) return;
    attentionService.upsertItem(actor, attentionItem(actor, "attention-user-admin-invitation-delivery", USER_ADMIN_AGENT_ID, "User Admin invitation delivery needs review", failedInvites + " invitation delivery attempt(s) need authorized review.", AttentionCategory.INVITATION_DELIVERY, AttentionSeverity.WARNING, USER_ADMIN_CAPABILITY, "surface-user-admin-users", "user-admin-invitation-delivery", correlationId), correlationId);
  }

  private AttentionItem attentionItem(AuthContextResolver.ResolvedMe actor, String itemId, String workstreamId, String title, String summary, AttentionCategory category, AttentionSeverity severity, String capabilityId, String surfaceId, String sourceId, String correlationId) {
    var now = Instant.now();
    return new AttentionItem(
        itemId,
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        workstreamId,
        title,
        summary,
        category,
        severity,
        AttentionItemStatus.OPEN,
        AttentionItem.AssigneeKind.CAPABILITY,
        capabilityId,
        capabilityId,
        new AttentionSurfaceRef(workstreamId, surfaceId, "dashboard", itemId, AttentionService.OPEN_ATTENTION_ITEM_TOOL, capabilityId),
        List.of(new AttentionSourceRef("capability", sourceId, title, capabilityId, "trace-" + sourceId, correlationId)),
        null,
        now,
        now,
        now,
        null,
        null,
        null,
        null,
        correlationId);
  }

  private Map<String, Object> withAttentionItems(Map<String, Object> data, AuthContextResolver.ResolvedMe actor, String workstreamId, String correlationId) {
    var copy = new LinkedHashMap<>(data);
    copy.put("attentionItems", attentionMaps(attentionService.listWorkstreamItems(actor, workstreamId, correlationId)));
    copy.put("attentionSource", AttentionService.LIST_WORKSTREAM_ITEMS_TOOL);
    return copy;
  }

  private List<Map<String, Object>> attentionMaps(List<AttentionItem> items) {
    return items.stream().map(item -> mapOf(
        "itemId", item.itemId(),
        "label", item.title(),
        "summary", item.summary(),
        "status", item.status().name().toLowerCase(),
        "severity", item.severity().name().toLowerCase(),
        "category", item.category().name().toLowerCase(),
        "capabilityId", item.requiredCapabilityId(),
        "governedToolId", AttentionService.OPEN_ATTENTION_ITEM_TOOL,
        "traceId", item.sourceRefs().stream().map(AttentionSourceRef::traceId).filter(Objects::nonNull).findFirst().orElse(item.correlationId()),
        "sourceWorkstreamId", item.owningWorkstreamId(),
        "surfaceRef", item.surfaceRef(),
        "redaction", item.redactionLevel().name().toLowerCase())).toList();
  }

  private String shellTargetSurfaceId(String requestType, String targetAgentId, String requestedSurfaceId) {
    if (requestedSurfaceId != null && !requestedSurfaceId.isBlank() && !"open_workstream".equals(requestType)) return requestedSurfaceId;
    return defaultDashboardSurfaceId(targetAgentId);
  }

  private String defaultDashboardSurfaceId(String targetAgentId) {
    return switch (targetAgentId) {
      case MY_ACCOUNT_AGENT_ID -> "surface-my-account-dashboard";
      case AGENT_ADMIN_AGENT_ID -> "surface-agent-admin-catalog";
      case AUDIT_TRACE_AGENT_ID -> "surface-audit-trace-dashboard";
      case GOVERNANCE_POLICY_AGENT_ID -> "surface-governance-policy-dashboard";
      default -> "surface-user-admin-dashboard";
    };
  }

  private ShellSurfaceAlias resolveShellSurfaceAlias(String requestType, String selectedAgentId, WorkstreamShellRequest request) {
    if (!("show_surface".equals(requestType) || "refresh_surface".equals(requestType))) return null;
    var text = normalizedShellAliasText(firstNonBlank(request.canonicalPrompt(), request.displayText(), ""));
    if (text.isBlank()) return null;
    if (List.of("dashboard", "show dashboard", "open dashboard", "refresh dashboard", "show command center", "open command center").contains(text)) return new ShellSurfaceAlias(selectedAgentId, defaultDashboardSurfaceId(selectedAgentId), "show dashboard");
    if (List.of("show notifications", "open notifications", "show notification center", "open notification center", "notifications").contains(text)) return new ShellSurfaceAlias(MY_ACCOUNT_AGENT_ID, "surface-my-account-notification-center", "show notifications");
    if (List.of("show users", "open users", "show user list", "open user list", "show user directory", "open user directory", "users", "show invitations", "open invitations", "invitations").contains(text)) return new ShellSurfaceAlias(USER_ADMIN_AGENT_ID, "surface-user-admin-users", "show users");
    if (List.of("show agent catalog", "open agent catalog", "show agents", "open agents", "agent catalog").contains(text)) return new ShellSurfaceAlias(AGENT_ADMIN_AGENT_ID, "surface-agent-admin-catalog", "show agent catalog");
    if (List.of("show audit timeline", "open audit timeline", "show trace timeline", "open trace timeline", "audit timeline").contains(text)) return new ShellSurfaceAlias(AUDIT_TRACE_AGENT_ID, "surface-audit-trace-timeline", "show audit timeline");
    if (List.of("show audit search", "open audit search", "show traces", "open traces", "show audit traces", "open audit traces").contains(text)) return new ShellSurfaceAlias(AUDIT_TRACE_AGENT_ID, "surface-audit-trace-search", "show audit traces");
    if (List.of("show governance policies", "open governance policies", "show policies", "open policies", "governance policies", "policies").contains(text)) return new ShellSurfaceAlias(GOVERNANCE_POLICY_AGENT_ID, "surface-governance-policy-inventory", "show governance policies");
    if (List.of("show governance dashboard", "open governance dashboard", "show policy dashboard", "open policy dashboard").contains(text)) return new ShellSurfaceAlias(GOVERNANCE_POLICY_AGENT_ID, "surface-governance-policy-dashboard", "show governance dashboard");
    return null;
  }

  private boolean shouldDenyUnresolvedPromptAlias(String requestType, WorkstreamShellRequest request) {
    if (!("show_surface".equals(requestType) || "refresh_surface".equals(requestType))) return false;
    if (request.targetSurfaceId() != null && !request.targetSurfaceId().isBlank()) return false;
    if (!"user_prompt".equals(firstNonBlank(request.origin(), "user_prompt"))) return false;
    var text = normalizedShellAliasText(firstNonBlank(request.canonicalPrompt(), request.displayText(), ""));
    return text.startsWith("show ") || text.startsWith("open ") || text.startsWith("refresh ");
  }

  private String normalizedShellAliasText(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("[.!?]+$", "").replaceAll("\\s+", " ");
  }

  private record ShellSurfaceAlias(String targetAgentId, String targetSurfaceId, String canonicalPrompt) {}

  private WorkstreamShellRequest normalizeShellRequest(WorkstreamShellRequest request, String targetAgentId, String targetSurfaceId, String correlationId) {
    var requestType = firstNonBlank(request.requestType(), "show_surface");
    var canonical = switch (requestType) {
      case "open_workstream" -> "show workstream " + targetAgentId;
      case "refresh_surface" -> "refresh surface " + targetSurfaceId;
      case "open_attention_item" -> "open attention item " + firstNonBlank(request.targetItemId(), "selected");
      default -> "show surface " + targetSurfaceId;
    };
    return new WorkstreamShellRequest(requestType, firstNonBlank(request.origin(), "user_prompt"), firstNonBlank(request.displayText(), canonical), firstNonBlank(request.canonicalPrompt(), canonical), targetAgentId, targetSurfaceId, request.targetItemId(), request.sourceFunctionalAgentId(), request.sourceSurfaceId(), request.sourceActionId(), firstNonBlank(request.scope(), "current_workstream"), correlationId, request.selectedContextId());
  }

  private WorkstreamItem shellRequestItem(String targetAgentId, WorkstreamShellRequest request, String correlationId, String surfaceId, String status) {
    var normalized = normalizeShellRequest(request, targetAgentId, surfaceId, correlationId);
    return new WorkstreamItem("item-shell-request-" + stableSuffix(correlationId + ":" + normalized.canonicalPrompt()), targetAgentId, "user-request", Instant.now().toString(), correlationId, List.of("trace-shell-request-" + stableSuffix(correlationId)), surfaceId, normalized.canonicalPrompt(), normalized.displayText(), status);
  }

  private SurfaceEnvelope shellSystemMessageSurface(AuthContextResolver.ResolvedMe actor, String targetAgentId, String code, String message, String correlationId) {
    return new SurfaceEnvelope("surface-shell-request-denied-" + stableSuffix(targetAgentId + ":" + correlationId), "system_message", "v1", "Request unavailable", targetAgentId, List.of(), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, List.of("trace-shell-request-denied-" + stableSuffix(correlationId)), Instant.now().toString(), null, mapOf("profile", "self", "omittedFieldKeys", List.of("hiddenWorkstreamId", "hiddenSurfaceId")), mapOf("severity", "forbidden", "code", code, "title", "Request unavailable", "body", message, "workstreamEntryId", "item-shell-request-denied", "relatedCapabilityId", MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "recovery", List.of(mapOf("label", "Open My Account", "actionId", "action-show-my-profile", "description", "Review selected context and authority."))), List.of(showProfileAction(), showSettingsAction()), List.of());
  }

  private CapabilityActionResult invitationActionResult(String status, String message, String correlationId, String invitationId, AuthContextResolver.ResolvedMe actor) {
    var traceId = "trace-useradmin-invitation-" + stableSuffix(invitationId + ":" + correlationId);
    return new CapabilityActionResult(status, message, correlationId, List.of(traceId), invitationDetailSurface(actor, mapOf("invitationId", invitationId), correlationId));
  }

  private CapabilityActionResult accessReviewActionResult(AccessReviewTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), accessReviewSurface(actor, task, correlationId));
  }

  private Map<String, Object> invitationRow(InvitationView.InvitationRow invite) {
    return mapOf("id", invite.invitationId(), "invitationId", invite.invitationId(), "rowType", "invitation", "email", invite.targetEmail(), "displayName", invite.targetEmail(), "role", roleLabels(invite.requestedRoles()), "status", invite.status().name().toLowerCase(), "delivery", invite.deliveryStatus().name().toLowerCase(), "expiresAt", invite.expiresAt().toString(), "canResend", invite.canResend(), "canRevoke", invite.canRevoke(), "traceId", "trace-useradmin-invitation-" + stableSuffix(invite.invitationId()));
  }

  private static boolean isPendingInvitation(InvitationView.InvitationRow invite) {
    return invite.status() != InvitationStatus.ACCEPTED && invite.status() != InvitationStatus.REVOKED && invite.status() != InvitationStatus.EXPIRED;
  }

  private String roleLabels(List<FoundationRole> roles) {
    return roles.stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', ' ')).map(label -> label.substring(0, 1).toUpperCase(Locale.ROOT) + label.substring(1)).sorted().collect(Collectors.joining(", "));
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
      case "action-audit-trace-append-investigation-note" -> auditTraceInvestigationNoteSurface(actor, null, null, correlationId);
      case "action-audit-trace-start-summary-task", "action-audit-trace-summary-task-start" -> auditTraceSummaryTaskBlockedSurface(actor, correlationId);
      case "action-show-my-account-dashboard" -> myAccountDashboardSurface(actor, correlationId);
      case "action-show-my-profile", "action-update-my-profile" -> myProfileSurface(actor, correlationId);
      case "action-show-my-settings", "action-update-my-settings" -> mySettingsSurface(actor, correlationId);
      case "action-show-my-context" -> myContextSurface(actor, correlationId);
      case "action-select-my-context" -> accessProfileContextSurface(actor, correlationId);
      case "action-show-my-account-notification-center", "action-notification-mark-read", "action-notification-dismiss", "action-notification-archive", "action-notification-snooze", "action-notification-update-preferences", "action-notification-email-update-preferences", "action-notification-external-fail-closed-check" -> myAccountNotificationCenterSurface(actor, correlationId);
      case "action-sign-out" -> myAccountDashboardSurface(actor, correlationId);
      case "action-start-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-read-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-cancel-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-accept-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-reject-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
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
      case "action-open-agent-detail", "action-activate-agent-definition", "action-deactivate-agent-definition" -> agentAdminDetailSurface(actor, correlationId);
      case "action-import-agent-seed-defaults" -> agentSeedMaterialSurface(actor, correlationId);
      case "action-propose-prompt-diff" -> agentPromptGovernanceSurface(actor, correlationId);
      case "action-test-agent-prompt" -> agentTestConsoleSurface(actor, correlationId);
      case "action-approve-skill-manifest" -> agentSkillManifestSurface(actor, correlationId);
      case "action-simulate-tool-boundary" -> agentToolBoundarySurface(actor, correlationId);
      case "action-manage-model-ref" -> agentModelRefsSurface(actor, correlationId);
      case "action-list-agent-seed-material" -> agentSeedMaterialSurface(actor, correlationId);
      case "action-display-user-detail", "action-replace-membership-role", "action-useradmin-preview-role-change", "action-useradmin-change-member-roles", "action-useradmin-disable-account", "action-useradmin-reactivate-account" -> detailSurface(actor, correlationId);
      case "action-display-invitation-detail", "action-useradmin-resend-invitation", "action-useradmin-revoke-invitation", "action-invite-user" -> invitationDetailSurface(actor, null, correlationId);
      case "action-useradmin-disable-member", "action-useradmin-reactivate-member", "action-useradmin-permanently-remove-user", "action-useradmin-read-support-access", "action-useradmin-grant-support-access", "action-useradmin-revoke-support-access", "action-useradmin-extend-support-access", "action-display-user-list" -> listSurface(actor, correlationId);
      case "action-useradmin-start-access-review", "action-useradmin-read-access-review", "action-useradmin-cancel-access-review", "action-useradmin-accept-access-review-result", "action-useradmin-reject-access-review-result" -> accessReviewBlockedSurface(actor, correlationId);
      default -> dashboardSurface(actor, correlationId);
    };
  }

  private SurfaceAction actionById(String actionId) {
    return List.of(showDashboardAction(), showNotificationCenterAction(), markNotificationReadAction(), dismissNotificationAction(), archiveNotificationAction(), snoozeNotificationAction(), updateNotificationPreferencesAction(), updateEmailNotificationPreferencesAction(), externalNotificationFailClosedAction(), showProfileAction(), showSettingsAction(), showContextAction(), selectContextAction(), updateProfileAction(), updateSettingsAction(), signOutAction(), startPersonalAttentionDigestAction(), readPersonalAttentionDigestAction(), cancelPersonalAttentionDigestAction(), acceptPersonalAttentionDigestAction(), rejectPersonalAttentionDigestAction(), openUserAdminAction(), openAgentAdminAction(), openGovernancePolicyAction(), displayListAction(), displayDetailAction(), displayInvitationDetailAction(), inviteAction(), resendInvitationAction(), revokeInvitationAction(), updateMemberStatusAction(), reactivateMemberStatusAction(), permanentlyRemoveUserAction(), disableAccountAction(), reactivateAccountAction(), readSupportAccessAction(), grantSupportAccessAction(), revokeSupportAccessAction(), extendSupportAccessAction(), previewRoleChangeAction(), changeMemberRolesAction(), startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), acceptAccessReviewResultAction(), rejectAccessReviewResultAction(), deniedReplaceRoleAction(), traceAction(), openAuditAction(), auditTraceSearchAction(), auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSummaryTaskBlockedAction(), governanceDashboardAction(), governanceListPoliciesAction(), governanceReadPolicyAction(), governanceDraftProposalAction(), governanceSubmitProposalAction(), governanceSimulateProposalAction(), governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), governanceOutcomeNoteAction(), governanceStartImpactAnalysisAction(), simulatePolicyAction(), commitPolicyAction(), displayAgentCatalogAction(), openAgentDetailAction(), activateAgentDefinitionAction(), deactivateAgentDefinitionAction(), importAgentSeedDefaultsAction(), proposePromptDiffAction(), testPromptAction(), approveSkillManifestAction(), submitBehaviorChangeAction(), rejectBehaviorChangeAction(), activateBehaviorChangeAction(), cancelBehaviorChangeAction(), rollbackBehaviorChangeAction(), simulateToolBoundaryAction(), manageModelRefAction(), listAgentSeedMaterialAction(), openAgentTraceAction()).stream().filter(action -> actionId.equals(action.actionId())).findFirst().orElse(null);
  }

  private SurfaceEnvelope envelope(String id, String type, String title, AuthContextResolver.ResolvedMe actor, String correlationId, Map<String, Object> data, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(id, type, "v1", title, ownerForSurface(id), reusableAgentsForSurface(id), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, List.of("trace-" + id), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawInvitationToken", "rawJwt", "rawProviderCredential")), data, actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + id, "rel", "deep-link")));
  }

  private String ownerForSurface(String surfaceId) { if (surfaceId.startsWith("surface-my-") || ACCESS_PROFILE_CONTEXT_SURFACE_ID.equals(surfaceId)) return MY_ACCOUNT_AGENT_ID; if (surfaceId.startsWith("surface-audit")) return AUDIT_TRACE_AGENT_ID; if (surfaceId.startsWith("surface-governance")) return GOVERNANCE_POLICY_AGENT_ID; if (surfaceId.startsWith("surface-agent")) return AGENT_ADMIN_AGENT_ID; return USER_ADMIN_AGENT_ID; }
  private List<String> reusableAgentsForSurface(String surfaceId) { if (surfaceId.startsWith("surface-audit")) return List.of(USER_ADMIN_AGENT_ID, GOVERNANCE_POLICY_AGENT_ID, AGENT_ADMIN_AGENT_ID, MY_ACCOUNT_AGENT_ID); if (surfaceId.startsWith("surface-my-") || ACCESS_PROFILE_CONTEXT_SURFACE_ID.equals(surfaceId)) return List.of(AUDIT_TRACE_AGENT_ID); if (surfaceId.startsWith("surface-agent")) return List.of(GOVERNANCE_POLICY_AGENT_ID, AUDIT_TRACE_AGENT_ID); if (surfaceId.startsWith("surface-governance")) return List.of(AGENT_ADMIN_AGENT_ID, AUDIT_TRACE_AGENT_ID); return List.of(AUDIT_TRACE_AGENT_ID); }

  private String browserToolId(String actionId) { return actionId; }

  private String governedToolId(String capabilityId) { return capabilityId; }

  private Membership selectedMembership(AuthContextResolver.ResolvedMe actor) {
    return actor.memberships().stream()
        .filter(membership -> actor.selectedContext().membershipId().equals(membership.membershipId()))
        .findFirst()
        .orElse(null);
  }

  private SurfaceAction showDashboardAction() { return new SurfaceAction("action-show-my-account-dashboard", "Refresh My Account summary", "read", browserToolId("action-show-my-account-dashboard"), governedToolId(MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY), MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-dashboard", "inline"), new Audit("MyAccountDashboardDisplayed", true)); }
  private SurfaceAction showNotificationCenterAction() { return new SurfaceAction("action-show-my-account-notification-center", "Open in-app notification center", "read", browserToolId("action-show-my-account-notification-center"), governedToolId(NOTIFICATION_LIST_CAPABILITY), NOTIFICATION_LIST_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("MyAccountNotificationCenterDisplayed", true)); }
  private SurfaceAction markNotificationReadAction() { return new SurfaceAction("action-notification-mark-read", "Mark notification read", "command", browserToolId("action-notification-mark-read"), governedToolId(NOTIFICATION_MARK_READ_CAPABILITY), NOTIFICATION_MARK_READ_CAPABILITY, "schema.notification.mark-read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("NotificationMarkedRead", true)); }
  private SurfaceAction dismissNotificationAction() { return new SurfaceAction("action-notification-dismiss", "Dismiss notification", "command", browserToolId("action-notification-dismiss"), governedToolId(NOTIFICATION_DISMISS_CAPABILITY), NOTIFICATION_DISMISS_CAPABILITY, "schema.notification.dismiss.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("NotificationDismissed", true)); }
  private SurfaceAction archiveNotificationAction() { return new SurfaceAction("action-notification-archive", "Archive notification", "command", browserToolId("action-notification-archive"), governedToolId(NOTIFICATION_ARCHIVE_CAPABILITY), NOTIFICATION_ARCHIVE_CAPABILITY, "schema.notification.archive.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("NotificationArchived", true)); }
  private SurfaceAction snoozeNotificationAction() { return new SurfaceAction("action-notification-snooze", "Snooze notification", "command", browserToolId("action-notification-snooze"), governedToolId(NOTIFICATION_SNOOZE_CAPABILITY), NOTIFICATION_SNOOZE_CAPABILITY, "schema.notification.snooze.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("NotificationSnoozed", true)); }
  private SurfaceAction updateNotificationPreferencesAction() { return new SurfaceAction("action-notification-update-preferences", "Update in-app notification preferences", "command", browserToolId("action-notification-update-preferences"), governedToolId(NOTIFICATION_UPDATE_PREFERENCES_CAPABILITY), NOTIFICATION_UPDATE_PREFERENCES_CAPABILITY, "schema.notification.preferences.update.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("NotificationPreferencesUpdated", true)); }
  private SurfaceAction updateEmailNotificationPreferencesAction() { return new SurfaceAction("action-notification-email-update-preferences", "Update email notification preferences", "command", browserToolId("action-notification-email-update-preferences"), governedToolId(NOTIFICATION_EMAIL_UPDATE_PREFERENCES_CAPABILITY), NOTIFICATION_EMAIL_UPDATE_PREFERENCES_CAPABILITY, "schema.notification.email.preferences.update.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("EmailNotificationPreferencesUpdated", true)); }
  private SurfaceAction externalNotificationFailClosedAction() { return new SurfaceAction("action-notification-external-fail-closed-check", "Check external delivery fail-closed seam", "command", browserToolId("action-notification-external-fail-closed-check"), governedToolId(NOTIFICATION_DELIVERY_EVALUATE_EXTERNAL_CAPABILITY), NOTIFICATION_DELIVERY_EVALUATE_EXTERNAL_CAPABILITY, "schema.notification.external.fail-closed-check.v1", false, false, null, new Idempotency(true, "channel+notificationId"), new ResultSurface(null, "surface-my-account-notification-center", "inline"), new Audit("NotificationExternalDeliveryBlockedProviderUnconfigured", true)); }
  private SurfaceAction showProfileAction() { return new SurfaceAction("action-show-my-profile", "Show user profile", "read", browserToolId("action-show-my-profile"), governedToolId(MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY), MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-profile", "inline"), new Audit("UserProfileDisplayed", true)); }
  private SurfaceAction showSettingsAction() { return new SurfaceAction("action-show-my-settings", "Show user settings", "read", browserToolId("action-show-my-settings"), governedToolId(MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY), MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-settings", "inline"), new Audit("UserSettingsDisplayed", true)); }
  private List<Map<String, Object>> namedThemeOptions() {
    return List.of(
        mapOf("value", "aurora-light", "themeId", "aurora-light", "label", "Aurora Light", "name", "Aurora Light", "tone", "light"),
        mapOf("value", "cobalt-light", "themeId", "cobalt-light", "label", "Cobalt Light", "name", "Cobalt Light", "tone", "light"),
        mapOf("value", "obsidian-dark", "themeId", "obsidian-dark", "label", "Obsidian Dark", "name", "Obsidian Dark", "tone", "dark"),
        mapOf("value", "midnight-dark", "themeId", "midnight-dark", "label", "Midnight Dark", "name", "Midnight Dark", "tone", "dark"),
        mapOf("value", "dark-night", "themeId", "dark-night", "label", "Dark Night", "name", "Dark Night", "tone", "dark"));
  }

  private SurfaceAction showContextAction() { return new SurfaceAction("action-show-my-context", "Show selected context", "read", browserToolId("action-show-my-context"), governedToolId(MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY), MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-context", "inline"), new Audit("AuthContextDisplayed", true)); }
  private SurfaceAction selectContextAction() { return new SurfaceAction("action-select-my-context", "Select authorized context", "surface-request", browserToolId("action-select-my-context"), governedToolId(CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), CORE_ACCESS_CONTEXT_SELECT_CAPABILITY, "schema.core.access.context.select.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, ACCESS_PROFILE_CONTEXT_SURFACE_ID, "inline"), new Audit("AuthContextSelectRequested", true)); }
  private SurfaceAction updateProfileAction() { return new SurfaceAction("action-update-my-profile", "Save profile changes", "command", browserToolId("action-update-my-profile"), governedToolId(MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY), MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY, "schema.my-account.profile.update.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-my-profile", "inline"), new Audit("UserProfileUpdateRequested", true)); }
  private SurfaceAction updateSettingsAction() { return new SurfaceAction("action-update-my-settings", "Save settings changes", "command", browserToolId("action-update-my-settings"), governedToolId(MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY), MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY, "schema.my-account.settings.update.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-my-settings", "inline"), new Audit("UserSettingsUpdateRequested", true)); }
  private SurfaceAction signOutAction() { return new SurfaceAction("action-sign-out", "Sign out", "command", browserToolId("action-sign-out"), governedToolId(MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY), MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, null, true, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-dashboard", "inline"), new Audit("SessionSignOutRequested", true)); }
  private SurfaceAction startPersonalAttentionDigestAction() { return new SurfaceAction("action-start-my-account-personal-attention-digest", "Start personal attention digest", "command", browserToolId("action-start-my-account-personal-attention-digest"), governedToolId(MY_ACCOUNT_DIGEST_START_CAPABILITY), MY_ACCOUNT_DIGEST_START_CAPABILITY, "schema.my-account.personal-attention-digest.start.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-my-account-personal-attention-digest-progress", "inline"), new Audit("MyAccountPersonalAttentionDigestStarted", true)); }
  private SurfaceAction readPersonalAttentionDigestAction() { return new SurfaceAction("action-read-my-account-personal-attention-digest", "Refresh digest state", "read", browserToolId("action-read-my-account-personal-attention-digest"), governedToolId(MY_ACCOUNT_DIGEST_READ_CAPABILITY), MY_ACCOUNT_DIGEST_READ_CAPABILITY, "schema.my-account.personal-attention-digest.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-personal-attention-digest-progress", "inline"), new Audit("MyAccountPersonalAttentionDigestRead", true)); }
  private SurfaceAction cancelPersonalAttentionDigestAction() { return new SurfaceAction("action-cancel-my-account-personal-attention-digest", "Cancel digest", "command", browserToolId("action-cancel-my-account-personal-attention-digest"), governedToolId(MY_ACCOUNT_DIGEST_CANCEL_CAPABILITY), MY_ACCOUNT_DIGEST_CANCEL_CAPABILITY, "schema.my-account.personal-attention-digest.cancel.v1", true, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-personal-attention-digest-progress", "inline"), new Audit("MyAccountPersonalAttentionDigestCancelled", true)); }
  private SurfaceAction acceptPersonalAttentionDigestAction() { return new SurfaceAction("action-accept-my-account-personal-attention-digest", "Accept advisory digest", "command", browserToolId("action-accept-my-account-personal-attention-digest"), governedToolId(MY_ACCOUNT_DIGEST_ACCEPT_CAPABILITY), MY_ACCOUNT_DIGEST_ACCEPT_CAPABILITY, "schema.my-account.personal-attention-digest.accept.v1", true, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-personal-attention-digest-result", "inline"), new Audit("MyAccountPersonalAttentionDigestAccepted", true)); }
  private SurfaceAction rejectPersonalAttentionDigestAction() { return new SurfaceAction("action-reject-my-account-personal-attention-digest", "Reject advisory digest", "command", browserToolId("action-reject-my-account-personal-attention-digest"), governedToolId(MY_ACCOUNT_DIGEST_REJECT_CAPABILITY), MY_ACCOUNT_DIGEST_REJECT_CAPABILITY, "schema.my-account.personal-attention-digest.reject.v1", true, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-account-personal-attention-digest-result", "inline"), new Audit("MyAccountPersonalAttentionDigestRejected", true)); }
  private SurfaceAction openUserAdminAction() { return new SurfaceAction("action-open-user-admin", "Open User Admin", "surface-request", browserToolId("action-open-user-admin"), governedToolId(MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY), MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "schema.my-account.open-workstream.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "deep-link"), new Audit("MyAccountOpenUserAdminRequested", true)); }
  private SurfaceAction openAgentAdminAction() { return new SurfaceAction("action-open-agent-admin", "Open Agent Admin", "surface-request", browserToolId("action-open-agent-admin"), governedToolId(MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY), MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "schema.my-account.open-workstream.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "deep-link"), new Audit("MyAccountOpenAgentAdminRequested", true)); }
  private SurfaceAction openGovernancePolicyAction() { return new SurfaceAction("action-open-governance-policy", "Open Governance/Policy", "surface-request", browserToolId("action-open-governance-policy"), governedToolId(MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY), MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "schema.my-account.open-workstream.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-dashboard", "deep-link"), new Audit("MyAccountOpenGovernancePolicyRequested", true)); }

  private AuthContextResolver.ProfileSettingsUpdateResult updateOwnProfileSettings(AuthContextResolver.ResolvedMe actor, CapabilityActionRequest request) {
    if (!(request.input() instanceof Map<?, ?> input)) throw new AuthorizationException(400, "MY_ACCOUNT_UPDATE_INPUT_REQUIRED");
    var unsupported = input.keySet().stream()
        .map(String::valueOf)
        .filter(key -> !List.of("displayName", "preferredThemeId").contains(key))
        .sorted()
        .toList();
    if (!unsupported.isEmpty()) throw new AuthorizationException(403, "MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD:" + String.join(",", unsupported));
    var displayName = input.get("displayName") instanceof String value ? value : null;
    UserSettings.ThemeId themeId = null;
    if (input.get("preferredThemeId") instanceof String value && !value.isBlank()) {
      try {
        themeId = UserSettings.ThemeId.fromId(value);
      } catch (IllegalArgumentException invalid) {
        throw new AuthorizationException(400, "MY_ACCOUNT_INVALID_THEME_ID");
      }
    }
    return myAccountService.updateProfileSettings(actor, displayName, themeId, request.idempotencyKey(), request.correlationId());
  }

  private SurfaceAction displayListAction() { return new SurfaceAction("action-display-user-list", "Show users", "read", browserToolId("action-display-user-list"), governedToolId(USERADMIN_LIST_MEMBERS), USERADMIN_LIST_MEMBERS, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminListDisplayed", true)); }
  private SurfaceAction displayDetailAction() { return new SurfaceAction("action-display-user-detail", "View user", "read", browserToolId("action-display-user-detail"), governedToolId(USERADMIN_LIST_MEMBERS), USERADMIN_LIST_MEMBERS, "schema.user-admin.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminDetailDisplayed", true)); }
  private SurfaceAction displayInvitationDetailAction() { return new SurfaceAction("action-display-invitation-detail", "View invitation", "read", browserToolId("action-display-invitation-detail"), governedToolId(USERADMIN_LIST_INVITATIONS), USERADMIN_LIST_INVITATIONS, "schema.user-admin.invitation-detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationDetailDisplayed", true)); }
  private SurfaceAction inviteAction() { return new SurfaceAction("action-invite-user", "Invite user", "command", browserToolId("action-invite-user"), governedToolId(USERADMIN_SEND_INVITATION), USERADMIN_SEND_INVITATION, "schema.invitation.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationRequested", true)); }
  private SurfaceAction resendInvitationAction() { return new SurfaceAction("action-useradmin-resend-invitation", "Resend invitation", "command", browserToolId("action-useradmin-resend-invitation"), governedToolId(USERADMIN_RESEND_INVITATION), USERADMIN_RESEND_INVITATION, "schema.invitation.resend.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationResendRequested", true)); }
  private SurfaceAction revokeInvitationAction() { return new SurfaceAction("action-useradmin-revoke-invitation", "Revoke invitation", "command", browserToolId("action-useradmin-revoke-invitation"), governedToolId(USERADMIN_REVOKE_INVITATION), USERADMIN_REVOKE_INVITATION, "schema.invitation.revoke.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationRevokeRequested", true)); }
  private SurfaceAction updateMemberStatusAction() { return new SurfaceAction("action-useradmin-disable-member", "Deactivate user", "command", browserToolId("action-useradmin-disable-member"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.member-status.update.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberStatusChanged", true)); }
  private SurfaceAction reactivateMemberStatusAction() { return new SurfaceAction("action-useradmin-reactivate-member", "Reactivate user", "command", browserToolId("action-useradmin-reactivate-member"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.member-status.update.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberStatusChanged", true)); }
  private SurfaceAction permanentlyRemoveUserAction() { return new SurfaceAction("action-useradmin-permanently-remove-user", "Permanently remove user", "command", browserToolId("action-useradmin-permanently-remove-user"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.user-remove.permanent.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminUserPermanentlyRemoved", true)); }
  private SurfaceAction disableAccountAction() { return new SurfaceAction("action-useradmin-disable-account", "Disable account", "command", browserToolId("action-useradmin-disable-account"), governedToolId(USERADMIN_DISABLE_ACCOUNT), USERADMIN_DISABLE_ACCOUNT, "schema.user-admin.account.disable.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminAccountDisabled", true)); }
  private SurfaceAction reactivateAccountAction() { return new SurfaceAction("action-useradmin-reactivate-account", "Reactivate account", "command", browserToolId("action-useradmin-reactivate-account"), governedToolId(USERADMIN_REACTIVATE_ACCOUNT), USERADMIN_REACTIVATE_ACCOUNT, "schema.user-admin.account.reactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminAccountReactivated", true)); }
  private SurfaceAction readSupportAccessAction() { return new SurfaceAction("action-useradmin-read-support-access", "Read support access", "read", browserToolId("action-useradmin-read-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_READ), USERADMIN_SUPPORT_ACCESS_READ, "schema.user-admin.support-access.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminSupportAccessRead", true)); }
  private SurfaceAction grantSupportAccessAction() { return new SurfaceAction("action-useradmin-grant-support-access", "Grant support access", "command", browserToolId("action-useradmin-grant-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_GRANT), USERADMIN_SUPPORT_ACCESS_GRANT, "schema.user-admin.support-access.grant.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessGranted", true)); }
  private SurfaceAction revokeSupportAccessAction() { return new SurfaceAction("action-useradmin-revoke-support-access", "Revoke support access", "command", browserToolId("action-useradmin-revoke-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_REVOKE), USERADMIN_SUPPORT_ACCESS_REVOKE, "schema.user-admin.support-access.revoke.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessRevoked", true)); }
  private SurfaceAction extendSupportAccessAction() { return new SurfaceAction("action-useradmin-extend-support-access", "Extend support access", "command", browserToolId("action-useradmin-extend-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_EXTEND), USERADMIN_SUPPORT_ACCESS_EXTEND, "schema.user-admin.support-access.extend.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessExtended", true)); }
  private SurfaceAction previewRoleChangeAction() { return new SurfaceAction("action-useradmin-preview-role-change", "Preview role change", "proposal", browserToolId("action-useradmin-preview-role-change"), governedToolId(USERADMIN_PREVIEW_ROLE_CHANGE), USERADMIN_PREVIEW_ROLE_CHANGE, "schema.user-admin.role-change.preview.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-role-change-preview", "inline"), new Audit("UserAdminRoleChangePreviewed", true)); }
  private SurfaceAction changeMemberRolesAction() { return new SurfaceAction("action-useradmin-change-member-roles", "Change role", "command", browserToolId("action-useradmin-change-member-roles"), governedToolId(USERADMIN_CHANGE_MEMBER_ROLES), USERADMIN_CHANGE_MEMBER_ROLES, "schema.user-admin.role-change.apply.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberRolesChanged", true)); }
  private SurfaceAction startAccessReviewAction() { return new SurfaceAction("action-useradmin-start-access-review", "Start access review", "workflow", browserToolId("action-useradmin-start-access-review"), governedToolId(USERADMIN_ACCESS_REVIEW_START), USERADMIN_ACCESS_REVIEW_START, "schema.user-admin.access-review.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewStarted", true)); }
  private SurfaceAction readAccessReviewAction() { return new SurfaceAction("action-useradmin-read-access-review", "Read access review", "read", browserToolId("action-useradmin-read-access-review"), governedToolId(USERADMIN_ACCESS_REVIEW_READ), USERADMIN_ACCESS_REVIEW_READ, "schema.user-admin.access-review.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewRead", true)); }
  private SurfaceAction cancelAccessReviewAction() { return new SurfaceAction("action-useradmin-cancel-access-review", "Cancel access review", "command", browserToolId("action-useradmin-cancel-access-review"), governedToolId(USERADMIN_ACCESS_REVIEW_CANCEL), USERADMIN_ACCESS_REVIEW_CANCEL, "schema.user-admin.access-review.cancel.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewCancelled", true)); }
  private SurfaceAction acceptAccessReviewResultAction() { return new SurfaceAction("action-useradmin-accept-access-review-result", "Accept access review result", "approval", browserToolId("action-useradmin-accept-access-review-result"), governedToolId(USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT), USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT, "schema.user-admin.access-review.accept-result.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewResultAccepted", true)); }
  private SurfaceAction rejectAccessReviewResultAction() { return new SurfaceAction("action-useradmin-reject-access-review-result", "Reject access review result", "approval", browserToolId("action-useradmin-reject-access-review-result"), governedToolId(USERADMIN_ACCESS_REVIEW_REJECT_RESULT), USERADMIN_ACCESS_REVIEW_REJECT_RESULT, "schema.user-admin.access-review.reject-result.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewResultRejected", true)); }
  private SurfaceAction deniedReplaceRoleAction() { return new SurfaceAction("action-replace-membership-role", "Replace membership role", "command", browserToolId("action-replace-membership-role"), governedToolId(USER_ADMIN_CAPABILITY), USER_ADMIN_CAPABILITY, "schema.membership.role.replace.v1", true, false, new DisabledReason("LAST_ADMIN_DENIED", "Backend authorization denied this action: cannot remove the last tenant admin without an approved replacement."), new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("MembershipRoleReplacementDenied", true)); }
  private SurfaceAction traceAction() { return new SurfaceAction("action-open-trace", "Open trace", "trace", browserToolId("action-open-trace"), governedToolId("my_account.view_own_trace_refs"), "my_account.view_own_trace_refs", null, false, false, null, new Idempotency(false, null), null, new Audit("TraceOpened", true)); }
  private SurfaceAction openAuditAction() { return new SurfaceAction("action-open-audit-trace", "Open audit timeline", "trace", browserToolId("action-open-audit-trace"), governedToolId("my_account.view_own_trace_refs"), "my_account.view_own_trace_refs", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-dashboard", "inline"), new Audit("AuditTimelineOpened", true)); }
  private SurfaceAction auditTraceSearchAction() { return new SurfaceAction("action-audit-trace-search", "Search traces", "read", browserToolId("action-audit-trace-search"), governedToolId(AUDIT_TRACE_SEARCH_CAPABILITY), AUDIT_TRACE_SEARCH_CAPABILITY, "schema.audit-trace.search.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-search", "inline"), new Audit("AuditTraceSearchRequested", true)); }
  private SurfaceAction auditTraceDetailAction() { return new SurfaceAction("action-audit-trace-detail", "Open trace detail", "read", browserToolId("action-audit-trace-detail"), governedToolId(AUDIT_TRACE_DETAIL_CAPABILITY), AUDIT_TRACE_DETAIL_CAPABILITY, "schema.audit-trace.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-detail", "inline"), new Audit("AuditTraceDetailRequested", true)); }
  private SurfaceAction auditTraceTimelineAction() { return new SurfaceAction("action-audit-trace-timeline", "Open correlation timeline", "read", browserToolId("action-audit-trace-timeline"), governedToolId(AUDIT_TRACE_TIMELINE_CAPABILITY), AUDIT_TRACE_TIMELINE_CAPABILITY, "schema.audit-trace.timeline.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-timeline", "inline"), new Audit("AuditTraceTimelineRequested", true)); }
  private SurfaceAction auditTraceFailureEvidenceAction() { return new SurfaceAction("action-audit-trace-failure-evidence", "Open failure evidence", "read", browserToolId("action-audit-trace-failure-evidence"), governedToolId(AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY), AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY, "schema.audit-trace.failure-evidence.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-failure-evidence", "inline"), new Audit("AuditTraceFailureEvidenceRequested", true)); }
  private SurfaceAction auditTraceInvestigationGuideAction() { return new SurfaceAction("action-audit-trace-investigation-guide", "Show investigation guidance", "read", browserToolId("action-audit-trace-investigation-guide"), governedToolId(AUDIT_TRACE_GUIDE_CAPABILITY), AUDIT_TRACE_GUIDE_CAPABILITY, "schema.audit-trace.investigation-guide.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-investigation-guide", "inline"), new Audit("AuditTraceInvestigationGuideRequested", true)); }
  private SurfaceAction auditTraceAppendInvestigationNoteAction() { return new SurfaceAction("action-audit-trace-append-investigation-note", "Append investigation note", "command", browserToolId("action-audit-trace-append-investigation-note"), governedToolId(AUDIT_TRACE_INVESTIGATION_NOTE_CAPABILITY), AUDIT_TRACE_INVESTIGATION_NOTE_CAPABILITY, "schema.audit-trace.investigation-note.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface("system-message", "surface-audit-trace-investigation-note", "inline"), new Audit("AuditTraceInvestigationNoteAppended", true)); }
  private SurfaceAction auditTraceSummaryTaskBlockedAction() { return new SurfaceAction("action-audit-trace-summary-task-start", "Start audit summary task", "workflow", browserToolId("action-audit-trace-summary-task-start"), governedToolId("audit.trace.summaryTask.start"), AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "schema.audit-trace.summary-task.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface("workflow-status", "surface-audit-trace-summary-progress", "inline"), new Audit("AuditTraceSummaryTaskStartBlocked", true)); }
  private SurfaceAction governanceDashboardAction() { return new SurfaceAction("action-governance-policy-dashboard", "Open governance dashboard", "read", browserToolId("action-governance-policy-dashboard"), governedToolId(GOVERNANCE_POLICY_READ_CAPABILITY), GOVERNANCE_POLICY_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-dashboard", "inline"), new Audit("GovernancePolicyDashboardDisplayed", true)); }
  private SurfaceAction governanceListPoliciesAction() { return new SurfaceAction("action-governance-policy-list", "List policy inventory", "read", browserToolId("action-governance-policy-list"), governedToolId(GOVERNANCE_POLICY_READ_CAPABILITY), GOVERNANCE_POLICY_READ_CAPABILITY, "schema.governance-policy.inventory.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-inventory", "inline"), new Audit("GovernancePolicyInventoryListed", true)); }
  private SurfaceAction governanceReadPolicyAction() { return new SurfaceAction("action-governance-policy-read", "Read policy evidence", "read", browserToolId("action-governance-policy-read"), governedToolId(GOVERNANCE_POLICY_READ_CAPABILITY), GOVERNANCE_POLICY_READ_CAPABILITY, "schema.governance-policy.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-detail", "inline"), new Audit("GovernancePolicyDetailRead", true)); }
  private SurfaceAction governanceDraftProposalAction() { return new SurfaceAction("action-governance-policy-draft-proposal", "Draft policy proposal", "proposal", browserToolId("action-governance-policy-draft-proposal"), governedToolId(GOVERNANCE_POLICY_PROPOSE_CAPABILITY), GOVERNANCE_POLICY_PROPOSE_CAPABILITY, "schema.governance-policy.proposal.draft.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-proposal", "inline"), new Audit("GovernancePolicyProposalDrafted", true)); }
  private SurfaceAction governanceSubmitProposalAction() { return new SurfaceAction("action-governance-policy-submit-proposal", "Submit proposal for review", "proposal", browserToolId("action-governance-policy-submit-proposal"), governedToolId(GOVERNANCE_POLICY_PROPOSE_CAPABILITY), GOVERNANCE_POLICY_PROPOSE_CAPABILITY, "schema.governance-policy.proposal.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-proposal", "inline"), new Audit("GovernancePolicyProposalSubmitted", true)); }
  private SurfaceAction governanceSimulateProposalAction() { return new SurfaceAction("action-governance-policy-simulate", "Simulate policy impact", "governance", browserToolId("action-governance-policy-simulate"), governedToolId(GOVERNANCE_POLICY_SIMULATE_CAPABILITY), GOVERNANCE_POLICY_SIMULATE_CAPABILITY, "schema.governance-policy.simulate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-simulation", "inline"), new Audit("GovernancePolicySimulationRequested", true)); }
  private SurfaceAction governanceDecideProposalAction() { return new SurfaceAction("action-governance-policy-decide", "Approve, reject, or request changes", "approval", browserToolId("action-governance-policy-decide"), governedToolId(GOVERNANCE_PROPOSALS_REVIEW_CAPABILITY), GOVERNANCE_POLICY_APPROVE_CAPABILITY, "schema.governance-policy.proposal.decide.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-decision", "inline"), new Audit("GovernancePolicyDecisionRecorded", true)); }
  private SurfaceAction governanceActivateProposalAction() { return new SurfaceAction("action-governance-policy-activate", "Activate approved policy", "command", browserToolId("action-governance-policy-activate"), governedToolId(GOVERNANCE_PROPOSALS_ACTIVATE_CAPABILITY), GOVERNANCE_POLICY_ACTIVATE_CAPABILITY, "schema.governance-policy.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-activation-blocked", "inline"), new Audit("GovernancePolicyActivationRequested", true)); }
  private SurfaceAction governanceRollbackPolicyAction() { return new SurfaceAction("action-governance-policy-rollback", "Roll back policy change", "command", browserToolId("action-governance-policy-rollback"), governedToolId(GOVERNANCE_PROPOSALS_ACTIVATE_CAPABILITY), GOVERNANCE_POLICY_ROLLBACK_CAPABILITY, "schema.governance-policy.rollback.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-rollback-blocked", "inline"), new Audit("GovernancePolicyRollbackRequested", true)); }
  private SurfaceAction governanceOutcomeNoteAction() { return new SurfaceAction("action-governance-policy-outcome-note", "Add outcome note", "command", browserToolId("action-governance-policy-outcome-note"), governedToolId(GOVERNANCE_OUTCOMES_RECORD_CAPABILITY), GOVERNANCE_OUTCOMES_RECORD_CAPABILITY, "schema.governance-policy.outcome-note.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-decision", "inline"), new Audit("GovernancePolicyOutcomeNoteRecorded", true)); }
  private SurfaceAction governanceStartImpactAnalysisAction() { return new SurfaceAction("action-governance-policy-start-impact-analysis", "Start impact analysis", "workflow", browserToolId("action-governance-policy-start-impact-analysis"), governedToolId(GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY), GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY, "schema.governance-policy.impact-analysis.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-impact-analysis-task", "inline"), new Audit("GovernancePolicyImpactAnalysisStartedOrBlocked", true)); }
  private SurfaceAction simulatePolicyAction() { return new SurfaceAction("action-simulate-policy", "Run governance simulation", "governance", browserToolId("action-simulate-policy"), governedToolId(GOVERNANCE_POLICY_SIMULATE_CAPABILITY), GOVERNANCE_POLICY_SIMULATE_CAPABILITY, "schema.policy.simulate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-simulation", "inline"), new Audit("PolicySimulationRequested", true)); }
  private SurfaceAction commitPolicyAction() { return new SurfaceAction("action-commit-policy", "Approve governance change", "approval", browserToolId("action-commit-policy"), governedToolId(GOVERNANCE_POLICY_ACTIVATE_CAPABILITY), GOVERNANCE_POLICY_ACTIVATE_CAPABILITY, "schema.policy.commit.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-activation-blocked", "inline"), new Audit("PolicyCommitApprovalRequested", true)); }
  private boolean changeAgentDefinitionStatus(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, AgentLifecycleStatus status, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), AGENT_DEFINITIONS_MANAGE_CAPABILITY);
    var current = agentBehaviorRepository.agentDefinition(actor.selectedContext().tenantId(), agentDefinitionId).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    if (current.status() == AgentLifecycleStatus.ARCHIVED) throw new AuthorizationException(403, "AGENT_DEFINITION_ARCHIVED");
    if (current.status() == status) return false;
    var updated = new AgentDefinition(
        current.tenantId(), current.agentDefinitionId(), current.displayName(), current.description(), current.placement(), current.functionalAreaId(), current.authorityLevel(), status,
        current.promptDocumentId(), current.activePromptVersion(), current.skillManifestId(), current.activeSkillManifestVersion(), current.referenceManifestId(), current.activeReferenceManifestVersion(), current.toolBoundaryId(), current.activeToolBoundaryVersion(), current.modelConfigRefId(), current.modelPolicyRefId(), current.runtimeClassRef(), current.traceRequirements(), current.seedProvenance(), current.createdAt(), Instant.now());
    agentBehaviorRepository.saveAgentDefinition(updated);
    authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.definition_lifecycle.v1 " + agentDefinitionId + " -> " + status.name().toLowerCase(Locale.ROOT), correlationId);
    return true;
  }

  private SurfaceAction displayAgentCatalogAction() { return new SurfaceAction("action-display-agent-catalog", "Display agent catalog", "read", browserToolId("action-display-agent-catalog"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction openAgentDetailAction() { return new SurfaceAction("action-open-agent-detail", "Open agent readiness detail", "read", browserToolId("action-open-agent-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, "schema.agent-definition.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction activateAgentDefinitionAction() { return new SurfaceAction("action-activate-agent-definition", "Activate AgentDefinition", "command", browserToolId("action-activate-agent-definition"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionActivated", true)); }
  private SurfaceAction deactivateAgentDefinitionAction() { return new SurfaceAction("action-deactivate-agent-definition", "Deactivate AgentDefinition", "command", browserToolId("action-deactivate-agent-definition"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.deactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDeactivated", true)); }
  private SurfaceAction importAgentSeedDefaultsAction() { return new SurfaceAction("action-import-agent-seed-defaults", "Import missing seed defaults", "workflow", browserToolId("action-import-agent-seed-defaults"), governedToolId(AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "schema.agent-seed.import-defaults.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedDefaultsImported", true)); }
  private SurfaceAction proposePromptDiffAction() { return new SurfaceAction("action-propose-prompt-diff", "Propose prompt diff", "proposal", browserToolId("action-propose-prompt-diff"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.prompt-version.proposal.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-prompt-governance", "side-panel"), new Audit("PromptVersionDraftProposed", true)); }
  private SurfaceAction testPromptAction() { return new SurfaceAction("action-test-agent-prompt", "Run no-side-effect prompt test", "workflow", browserToolId("action-test-agent-prompt"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-runtime.test.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface("workflow-status", null, "inline"), new Audit("AgentRuntimeTestRequested", true)); }
  private SurfaceAction approveSkillManifestAction() { return new SurfaceAction("action-approve-skill-manifest", "Approve manifest review", "approval", browserToolId("action-approve-skill-manifest"), governedToolId(AGENT_ADMIN_REVIEW_CAPABILITY), AGENT_ADMIN_REVIEW_CAPABILITY, null, true, true, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-agent-skill-manifest-diff", "inline"), new Audit("AgentSkillManifestApproved", true)); }
  private SurfaceAction submitBehaviorChangeAction() { return new SurfaceAction("action-submit-behavior-change", "Submit behavior change for review", "proposal", browserToolId("action-submit-behavior-change"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.behavior-change.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeSubmitted", true)); }
  private SurfaceAction rejectBehaviorChangeAction() { return new SurfaceAction("action-reject-behavior-change", "Reject behavior change", "approval", browserToolId("action-reject-behavior-change"), governedToolId(AGENT_ADMIN_REJECT_CAPABILITY), AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.behavior-change.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeRejected", true)); }
  private SurfaceAction activateBehaviorChangeAction() { return new SurfaceAction("action-activate-behavior-change", "Activate approved behavior change", "command", browserToolId("action-activate-behavior-change"), governedToolId(AGENT_ADMIN_ACTIVATE_CAPABILITY), AGENT_ADMIN_ACTIVATE_CAPABILITY, "schema.agent-admin.behavior-change.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeActivated", true)); }
  private SurfaceAction cancelBehaviorChangeAction() { return new SurfaceAction("action-cancel-behavior-change", "Cancel behavior change", "command", browserToolId("action-cancel-behavior-change"), governedToolId(AGENT_ADMIN_CANCEL_CAPABILITY), AGENT_ADMIN_CANCEL_CAPABILITY, "schema.agent-admin.behavior-change.cancel.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeCancelled", true)); }
  private SurfaceAction rollbackBehaviorChangeAction() { return new SurfaceAction("action-rollback-behavior-change", "Rollback activated behavior change", "command", browserToolId("action-rollback-behavior-change"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.behavior-change.rollback.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeRolledBack", true)); }
  private SurfaceAction simulateToolBoundaryAction() { return new SurfaceAction("action-simulate-tool-boundary", "Simulate tool boundary change", "governance", browserToolId("action-simulate-tool-boundary"), governedToolId(AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, "schema.tool-boundary.simulation.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("ToolBoundarySimulationRequested", true)); }
  private SurfaceAction manageModelRefAction() { return new SurfaceAction("action-manage-model-ref", "Request model ref change", "proposal", browserToolId("action-manage-model-ref"), governedToolId(AGENT_ADMIN_ACTIVATE_CAPABILITY), AGENT_ADMIN_ACTIVATE_CAPABILITY, null, false, false, new DisabledReason("MODEL_POLICY_DENIED", "This starter denies switching to a disabled provider alias; provider secrets remain redacted."), new Idempotency(true, "client-generated"), new ResultSurface("decision", null, "inline"), new Audit("AgentModelRefChangeDenied", true)); }
  private SurfaceAction listAgentSeedMaterialAction() { return new SurfaceAction("action-list-agent-seed-material", "List seed material", "read", browserToolId("action-list-agent-seed-material"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialListed", true)); }
  private SurfaceAction openAgentTraceAction() { return new SurfaceAction("action-open-agent-trace", "Open agent work trace", "trace", browserToolId("action-open-agent-trace"), governedToolId("audit.trace.read"), "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentWorkTraceOpened", true)); }

  private boolean isActionCapabilityVisible(AuthContextResolver.ResolvedMe actor, String capabilityId) {
    if (actor.selectedContext().capabilities().contains(capabilityId) || USER_ADMIN_CAPABILITY.equals(capabilityId)) return true;
    if (CORE_ACCESS_ME_CAPABILITY.equals(capabilityId)) return true;
    if (CORE_PROFILE_UPDATE_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY) || actor.selectedContext().capabilities().contains("profile.update");
    if (CORE_ACCESS_CONTEXT_SELECT_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY);
    if (capabilityId != null && capabilityId.startsWith("audit.trace.") && actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) return true;
    if (capabilityId != null && capabilityId.startsWith("governance.policy.") && actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_READ_CAPABILITY)) return actor.selectedContext().capabilities().contains(capabilityId);
    if (capabilityId != null && (capabilityId.startsWith("agent_admin.") || capabilityId.startsWith("agent."))) return actor.selectedContext().capabilities().contains(capabilityId);
    if (capabilityId != null && capabilityId.startsWith("notification.")) return actor.selectedContext().capabilities().contains(capabilityId);
    return capabilityId != null && (capabilityId.startsWith("USERADMIN_") || capabilityId.startsWith("user_admin.")) && actor.selectedContext().capabilities().contains(USER_ADMIN_CAPABILITY);
  }

  private String notificationIdInput(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) { var requested = stringInput(input, "notificationId", null); if (requested != null) return requested; return myAccountNotificationCenterData(actor, correlationId).items().stream().findFirst().map(NotificationItem::notificationId).orElse("missing-notification"); }
  private static String stringInput(Object input, String key, String fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return value; return fallback; }
  private static boolean booleanInput(Object input, String key, boolean fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof Boolean value) return value; return fallback; }
  private static int intInput(Object input, String key, int fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof Number value) return value.intValue(); return fallback; }
  private static List<FoundationRole> rolesInput(Object input) {
    if (input instanceof Map<?, ?> map) {
      if (map.get("roles") instanceof List<?> roles && !roles.isEmpty()) return roles.stream().map(String::valueOf).map(FoundationRole::valueOf).toList();
      if (map.get("roles") instanceof String role && !role.isBlank()) return List.of(FoundationRole.valueOf(role));
      if (map.get("role") instanceof String role && !role.isBlank()) return List.of(FoundationRole.valueOf(role));
    }
    return List.of(FoundationRole.TENANT_EMPLOYEE);
  }
  private static MembershipStatus membershipStatusInputForDeactivate(String value) {
    var normalized = Objects.requireNonNullElse(value, "removed").trim().toUpperCase(Locale.ROOT).replace('-', '_');
    if (normalized.equals("ACTIVE")) return MembershipStatus.REMOVED;
    if (normalized.equals("DEACTIVATED") || normalized.equals("DELETED")) return MembershipStatus.REMOVED;
    return MembershipStatus.valueOf(normalized);
  }
  private static NotificationCategory notificationCategoryInput(Object input, NotificationCategory fallback) { var value = stringInput(input, "category", null); if (value == null) return fallback; try { return NotificationCategory.valueOf(value.trim().toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ignored) { return fallback; } }
  private static NotificationPriority notificationPriorityInput(Object input, NotificationPriority fallback) { var value = stringInput(input, "minimumPriority", null); if (value == null) return fallback; try { return NotificationPriority.valueOf(value.trim().toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ignored) { return fallback; } }
  private static NotificationChannel notificationChannelInput(Object input, NotificationChannel fallback) { var value = stringInput(input, "channel", null); if (value == null) return fallback; try { return NotificationChannel.valueOf(value.trim().toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ignored) { return fallback; } }
  private static String firstNonBlank(String... values) { for (var value : values) if (value != null && !value.isBlank()) return value; return null; }
  private static String stableSuffix(String value) { return Integer.toUnsignedString(Objects.requireNonNullElse(value, "workstream-message").hashCode(), 36); }
  private static Map<String, Object> mapOf(Object... values) { var map = new LinkedHashMap<String, Object>(); for (int i = 0; i + 1 < values.length; i += 2) map.put(String.valueOf(values[i]), values[i + 1]); return map; }

  public record WorkstreamBootstrapResponse(MeResponse me, List<MeResponse.FunctionalAgentSummary> functionalAgents, List<WorkstreamItem> items, List<SurfaceEnvelope> surfaces) {}
  public record WorkstreamItem(String itemId, String functionalAgentId, String kind, String createdAt, String correlationId, List<String> traceIds, String surfaceId, String title, String body, String status) {}
  public record SurfaceEnvelope(String surfaceId, String surfaceType, String surfaceVersion, String title, String ownerFunctionalAgentId, List<String> reusableByFunctionalAgentIds, Map<String, Object> authContext, String correlationId, List<String> traceIds, String generatedAt, Map<String, Object> stale, Map<String, Object> redaction, Map<String, Object> data, List<SurfaceAction> actions, List<Map<String, Object>> links) {}
  public record SurfaceAction(String actionId, String label, String intent, String browserToolId, String governedToolId, String capabilityId, String inputSchemaRef, boolean requiresConfirmation, boolean requiresApproval, DisabledReason disabled, Idempotency idempotency, ResultSurface resultSurface, Audit audit) {}
  public record DisabledReason(String reasonCode, String message) {}
  public record Idempotency(boolean required, String keySource) {}
  public record ResultSurface(String appendSurfaceType, String updateSurfaceId, String openPlacement) {}
  public record Audit(String eventType, boolean traceRequired) {}
  public record CapabilityActionRequest(String actionId, String browserToolId, String governedToolId, String capabilityId, Object input, String idempotencyKey, String selectedContextId, String surfaceId, String correlationId) {}
  public record CapabilityActionResult(String status, String message, String correlationId, List<String> traceIds, SurfaceEnvelope resultSurface) {}
  public record WorkstreamShellRequest(String requestType, String origin, String displayText, String canonicalPrompt, String targetFunctionalAgentId, String targetSurfaceId, String targetItemId, String sourceFunctionalAgentId, String sourceSurfaceId, String sourceActionId, String scope, String correlationId, String selectedContextId) {}
  public record WorkstreamShellResponse(WorkstreamShellRequest request, String status, String message, String correlationId, List<String> traceIds, WorkstreamItem requestItem, SurfaceEnvelope resultSurface) {}
  public record WorkstreamMessageRequest(String selectedContextId, String functionalAgentId, String prompt, String correlationId, String idempotencyKey) {}
  public record WorkstreamMessageResponse(String correlationId, String idempotencyKey, WorkstreamItem userItem, WorkstreamItem agentItem, SurfaceEnvelope surface) {}
  public record WorkstreamEvent(String eventId, String eventType, String tenantId, String customerId, String functionalAgentId, String surfaceId, String surfaceType, String surfaceVersion, String correlationId, List<String> traceIds, String occurredAt, Integer sequence, Map<String, Object> patch) {}

  private static final class EmptyWorkstreamEventRepository implements WorkstreamEventRepository {
    public WorkstreamEventEnvelope publish(WorkstreamEventEnvelope event) { return event; }
    public java.util.Optional<WorkstreamEventEnvelope> find(String tenantId, String eventId) { return java.util.Optional.empty(); }
    public java.util.Optional<WorkstreamEventEnvelope> findByIdempotencyKey(String tenantId, String idempotencyKey) { return java.util.Optional.empty(); }
    public List<WorkstreamEventEnvelope> listTenant(String tenantId) { return List.of(); }
  }
}
