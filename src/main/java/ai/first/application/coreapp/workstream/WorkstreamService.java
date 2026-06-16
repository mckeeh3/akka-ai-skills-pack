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
import ai.first.application.coreapp.agentadmin.AgentAdminPromptRiskReviewService;
import ai.first.application.coreapp.agentadmin.PromptRiskReviewTaskRepository;
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
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.foundation.attention.AttentionSourceRef;
import ai.first.domain.foundation.attention.AttentionSurfaceRef;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.email.EmailNotificationPreference;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
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
import java.util.Optional;
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
  private static final String AGENT_ADMIN_AGENT_ID = "agent-admin-agent";
  private static final String USER_ADMIN_CAPABILITY = "secure-tenant-user-foundation";
  private static final String SAAS_OWNER_TENANT_READ_CAPABILITY = "saas_owner.tenant.read";
  private static final String SAAS_OWNER_TENANT_MANAGE_CAPABILITY = "saas_owner.tenant.manage";
  private static final String SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY = "saas_owner.organization.list";
  private static final String SAAS_OWNER_ORGANIZATION_READ_CAPABILITY = "saas_owner.organization.read";
  private static final String SAAS_OWNER_ORGANIZATION_MANAGE_CAPABILITY = "saas_owner.organization.manage";
  private static final String SAAS_OWNER_USER_MANAGE_CAPABILITY = "saas_owner.user.manage";
  private static final String SAAS_OWNER_ADMIN_LIST_CAPABILITY = "saas_owner.admin.list";
  private static final String SAAS_OWNER_ADMIN_INVITE_CAPABILITY = "saas_owner.admin.invite";
  private static final String SAAS_OWNER_ADMIN_MANAGE_CAPABILITY = "saas_owner.admin.manage";
  private static final String SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY = "saas_owner.organization_admin.list";
  private static final String SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY = "saas_owner.organization_admin.invite";
  private static final String SAAS_OWNER_ORGANIZATION_ADMIN_MANAGE_CAPABILITY = "saas_owner.organization_admin.manage";
  private static final String TENANT_CUSTOMER_LIST_CAPABILITY = "tenant.customer.list";
  private static final String TENANT_CUSTOMER_READ_CAPABILITY = "tenant.customer.read";
  private static final String TENANT_CUSTOMER_CREATE_CAPABILITY = "tenant.customer.create";
  private static final String TENANT_CUSTOMER_RENAME_CAPABILITY = "tenant.customer.rename";
  private static final String TENANT_CUSTOMER_SUSPEND_CAPABILITY = "tenant.customer.suspend";
  private static final String TENANT_CUSTOMER_REACTIVATE_CAPABILITY = "tenant.customer.reactivate";
  private static final String TENANT_CUSTOMER_ADMIN_LIST_CAPABILITY = "tenant.customer_admin.list";
  private static final String TENANT_CUSTOMER_ADMIN_INVITE_CAPABILITY = "tenant.customer_admin.invite";
  private static final String TENANT_CUSTOMER_ADMIN_MANAGE_CAPABILITY = "tenant.customer_admin.manage";
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
  private static final String USERADMIN_IDENTITY_RELINK_REQUEST = "user_admin.identity_relink.request";
  private static final String USERADMIN_IDENTITY_RELINK_READ = "user_admin.identity_relink.review";
  private static final String USERADMIN_IDENTITY_RELINK_APPROVE = "user_admin.identity_relink.approve";
  private static final String USERADMIN_IDENTITY_RELINK_DENY = "user_admin.identity_relink.deny";
  private static final String USERADMIN_IDENTITY_RELINK_COMPLETE = "user_admin.identity_relink.complete";
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
  private static final String AUDIT_TRACE_EXPORT_REQUEST_CAPABILITY = "audit.trace.export.request";
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
  private static final String GOVERNANCE_POLICY_ANALYSIS_READ_CAPABILITY = GovernancePolicyImpactService.READ_CAPABILITY;
  private static final String GOVERNANCE_POLICY_ANALYSIS_CANCEL_CAPABILITY = GovernancePolicyImpactService.CANCEL_CAPABILITY;
  private static final String GOVERNANCE_POLICY_ANALYSIS_ACCEPT_CAPABILITY = GovernancePolicyImpactService.ACCEPT_RESULT_CAPABILITY;
  private static final String GOVERNANCE_POLICY_ANALYSIS_REJECT_CAPABILITY = GovernancePolicyImpactService.REJECT_RESULT_CAPABILITY;
  private static final String GOVERNANCE_POLICY_ANALYSIS_REQUEST_CHANGES_CAPABILITY = GovernancePolicyImpactService.REQUEST_CHANGES_CAPABILITY;
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
  private final AgentAdminPromptRiskReviewService promptRiskReviewService;
  private final AuditTraceService auditTraceService;
  private final GovernancePolicyService governancePolicyService;
  private final GovernancePolicyImpactService governancePolicyImpactService;
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
    this.promptRiskReviewService = new AgentAdminPromptRiskReviewService(new InMemoryPromptRiskReviewTaskRepository(), authContextResolver, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher);
    this.agentRuntimeService = agentRuntimeService;
    this.workstreamAgentRuntimeInvoker = Objects.requireNonNull(workstreamAgentRuntimeInvoker);
    this.workstreamLogRepository = Objects.requireNonNull(workstreamLogRepository);
    this.workstreamEventRepository = workstreamEventRepository == null ? new EmptyWorkstreamEventRepository() : workstreamEventRepository;
    this.auditTraceService = new AuditTraceService(authContextResolver, Objects.requireNonNull(auditTraceRepository));
    var governanceRepository = Objects.requireNonNull(governancePolicyRepository);
    this.governancePolicyService = new GovernancePolicyService(governanceRepository, authContextResolver, Clock.systemUTC(), attentionProducerService);
    this.governancePolicyImpactService = new GovernancePolicyImpactService(StarterSecurityComponents.governancePolicyImpactTaskRepository(), governanceRepository, authContextResolver, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher, new ai.first.application.coreapp.governance.FailClosedGovernancePolicyImpactAutonomousAgentRuntime());
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
    if (!isActionCapabilityVisible(actor, action.capabilityId())) {
      if (isGovernancePolicyAction(request.actionId())) return governancePolicySystemMessageResult(actor, request.actionId(), "CAPABILITY_FORBIDDEN", governancePolicySafeDenialMessage("CAPABILITY_FORBIDDEN"), request.correlationId());
      throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    }
    if (action.idempotency().required() && (request.idempotencyKey() == null || request.idempotencyKey().isBlank())) {
      if (isGovernancePolicyAction(request.actionId())) return governancePolicySystemMessageResult(actor, request.actionId(), "idempotency-key-required", governancePolicySafeDenialMessage("idempotency-key-required"), request.correlationId());
      return new CapabilityActionResult("validation-error", "This action requires a client-generated idempotency key.", request.correlationId(), List.of("trace-validation-idempotency"), null);
    }
    var actionIdempotencyKey = action.idempotency().required() && !durableActionOwnsIdempotency(request.actionId()) ? actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + request.actionId() + ":" + request.idempotencyKey() : null;
    if (actionIdempotencyKey != null && idempotentActionResults.containsKey(actionIdempotencyKey)) return idempotentActionResults.get(actionIdempotencyKey);
    if (action.disabled() != null) return new CapabilityActionResult("denied", action.disabled().message(), request.correlationId(), List.of("trace-denied-" + action.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));

    CapabilityActionResult result = null;
    try {
    if ("action-user-admin-show-saas-owner-admins".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "SaaS Owner Admin directory loaded for app-owner administration.", request.correlationId(), List.of("trace-saas-owner-admins-" + stableSuffix(request.correlationId())), saasOwnerAdminsSurface(actor, request.correlationId()));
    } else if ("action-open-saas-owner-admin-invitation-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "SaaS Owner Admin invitation surface loaded.", request.correlationId(), List.of("trace-saas-owner-admin-invite-" + stableSuffix(request.correlationId())), saasOwnerAdminInvitationCreateSurface(actor, request.correlationId()));
    } else if ("action-display-organization-admin".equals(request.actionId()) || "action-user-admin-show-organizations".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Directory loaded for SaaS Owner Organization lifecycle administration.", request.correlationId(), List.of("trace-organization-admin-" + stableSuffix(request.correlationId())), organizationAdminSurface(actor, request.correlationId()));
    } else if ("action-open-organization-admin-invitation-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Admin invitation/bootstrap surface loaded.", request.correlationId(), List.of("trace-organization-admin-invite-" + stableSuffix(request.correlationId())), organizationAdminInvitationCreateSurface(actor, request.correlationId()));
    } else if ("action-user-admin-show-customers".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer Directory loaded for selected Organization/Tenant administration.", request.correlationId(), List.of("trace-customer-directory-" + stableSuffix(request.correlationId())), customerDirectorySurface(actor, request.correlationId()));
    } else if ("action-customer-read".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer detail loaded through backend-authoritative Customer surface graph.", request.correlationId(), List.of("trace-customer-read-" + stableSuffix(request.correlationId())), customerDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer create surface loaded.", request.correlationId(), List.of("trace-customer-create-" + stableSuffix(request.correlationId())), customerCreateSurface(actor, request.correlationId()));
    } else if ("action-open-customer-rename".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer rename surface loaded.", request.correlationId(), List.of("trace-customer-rename-" + stableSuffix(request.correlationId())), customerRenameSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-suspend".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer suspend confirmation surface loaded.", request.correlationId(), List.of("trace-customer-suspend-" + stableSuffix(request.correlationId())), customerSuspendSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-reactivate".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer reactivate confirmation surface loaded.", request.correlationId(), List.of("trace-customer-reactivate-" + stableSuffix(request.correlationId())), customerReactivateSurface(actor, request.input(), request.correlationId()));
    } else if ("action-user-admin-show-customer-admins".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer Admin directory loaded for the selected Customer.", request.correlationId(), List.of("trace-customer-admins-" + stableSuffix(request.correlationId())), customerAdminsSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-admin-invitation-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer Admin invitation/bootstrap surface loaded for the selected Customer.", request.correlationId(), List.of("trace-customer-admin-invite-" + stableSuffix(request.correlationId())), customerAdminInvitationCreateSurface(actor, request.input(), request.correlationId()));
    } else if ("action-customer-admin-invite".equals(request.actionId())) {
      var customer = activeCustomerInviteTargetDetail(actor, request.input(), request.correlationId());
      var invite = invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
          request.idempotencyKey(), ScopeType.CUSTOMER, actor.selectedContext().tenantId(), customer.customer().customerId(),
          stringInput(request.input(), "email", "new-customer-admin@example.test"), stringInput(request.input(), "displayName", "New Customer Admin"),
          customerAdminRolesInput(request.input()), Instant.now().plus(7, ChronoUnit.DAYS), stringInput(request.input(), "reason", "workstream-customer-admin-invite"), request.correlationId()));
      result = new CapabilityActionResult("accepted", "Customer Admin invitation queued for the selected Customer by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-customer-admin-invite-created-" + stableSuffix(invite.invitationId())), customerAdminInvitationDetailSurface(actor, customer, invite, request.correlationId()));
    } else if (List.of("action-customer-create", "action-customer-rename", "action-customer-suspend", "action-customer-reactivate").contains(request.actionId())) {
      var customerResult = runCustomerLifecycleAction(actor, request.actionId(), request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(customerResult.status(), customerResult.message(), request.correlationId(), customerResult.traceRefs(), customerDetailSurface(actor, customerResult.customer(), request.correlationId()));
    } else if ("action-organization-list".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Directory refreshed through the canonical workstream action path.", request.correlationId(), List.of("trace-organization-list-" + stableSuffix(request.correlationId())), organizationDirectorySurface(actor, request.input(), request.correlationId()));
    } else if (request.actionId().startsWith("action-open-organization-")) {
      result = openOrganizationTaskSurface(actor, request.actionId(), request.input(), request.correlationId());
    } else if ("action-organization-read".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization detail loaded through backend-authoritative Organization surface graph.", request.correlationId(), List.of("trace-organization-read-" + stableSuffix(request.correlationId())), organizationSurface(actor, request.correlationId(), "surface-user-admin-organization-detail", "show-inspection", "Organization Detail", "user_admin.organization_detail.v1", withOrganizationBranchReturn(List.of(openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), openAuditAction(), openOrganizationAdminInvitationCreateAction())), readOrganizationDetail(actor, request.input(), request.correlationId())));
    } else if (List.of("action-organization-create", "action-organization-rename", "action-organization-suspend", "action-organization-reactivate").contains(request.actionId())) {
      var organizationResult = runOrganizationLifecycleAction(actor, request.actionId(), request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(organizationResult.status(), organizationResult.message(), request.correlationId(), List.of(organizationResult.traceId()), organizationSurface(actor, request.correlationId(), "surface-user-admin-organization-detail", "show-inspection", "Organization Detail", "user_admin.organization_detail.v1", withOrganizationBranchReturn(List.of(openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), openAuditAction(), openOrganizationAdminInvitationCreateAction())), organizationResult.organization()));
    } else if ("action-display-user-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "User detail loaded.", request.correlationId(), List.of("trace-user-admin-detail-" + stableSuffix(stringInput(request.input(), "accountId", actor.account().accountId()))), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-display-invitation-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Invitation detail loaded.", request.correlationId(), List.of("trace-user-admin-invitation-" + stableSuffix(stringInput(request.input(), "invitationId", "latest"))), invitationDetailSurface(actor, request.input(), request.correlationId()));
    } else if (request.actionId().startsWith("action-open-useradmin-")) {
      result = openUserAdminTaskSurface(actor, request.actionId(), request.input(), request.correlationId());
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
      result = new CapabilityActionResult(changed.status(), changed.message(), request.correlationId(), List.of(changed.traceId()), detailSurface(actor, request.input(), request.correlationId()));
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
      result = new CapabilityActionResult("accepted", "Account disabled by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-useradmin-account-disable-" + stableSuffix(request.correlationId())), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-useradmin-reactivate-account".equals(request.actionId())) {
      var account = userAdminService.reactivateAccount(actor, stringInput(request.input(), "accountId", actor.account().accountId()), stringInput(request.input(), "reason", "workstream account reactivate"), request.correlationId());
      result = new CapabilityActionResult("accepted", "Account reactivated by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-useradmin-account-reactivate-" + stableSuffix(request.correlationId())), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-useradmin-read-support-access".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Support-access state loaded.", request.correlationId(), List.of("trace-useradmin-support-access-read-" + stableSuffix(request.correlationId())), listSurface(actor, request.input(), request.correlationId()));
    } else if ("action-useradmin-grant-support-access".equals(request.actionId()) || "action-useradmin-revoke-support-access".equals(request.actionId()) || "action-useradmin-extend-support-access".equals(request.actionId())) {
      var enabled = !"action-useradmin-revoke-support-access".equals(request.actionId());
      var expiresAt = enabled ? Instant.now().plus(8, ChronoUnit.HOURS) : null;
      var changed = userAdminService.updateSupportAccess(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), enabled, expiresAt, stringInput(request.input(), "reason", "workstream support access change"), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult("accepted", "Support access updated.", request.correlationId(), List.of("trace-useradmin-support-access-" + stableSuffix(request.idempotencyKey())), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-display-user-list".equals(request.actionId()) || "action-user-admin-show-users".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "User Admin directory loaded with backend-authorized filters.", request.correlationId(), List.of("trace-user-admin-list-" + stableSuffix(request.correlationId())), listSurface(actor, request.input(), request.correlationId()));
    } else if ("action-useradmin-start-access-review".equals(request.actionId())) {
      var task = accessReviewService.start(actor, request.idempotencyKey(), request.correlationId());
      result = accessReviewActionResult(task, task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked-runtime" : accessReviewStatus(task), task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "Access-review task failed closed; governed AutonomousAgent provider/runtime configuration is unavailable." : "Access-review Akka AutonomousAgent task accepted; backend projection remains source of truth.", request.correlationId(), actor);
    } else if ("action-useradmin-read-access-review".equals(request.actionId())) {
      var taskId = stringInput(request.input(), "taskId", "");
      result = taskId.isBlank()
          ? new CapabilityActionResult("accepted", "Access-review status surface loaded; no task id was selected from the dashboard queue.", request.correlationId(), List.of("trace-useradmin-access-review-status-" + stableSuffix(request.correlationId())), accessReviewBlockedSurface(actor, request.correlationId()))
          : accessReviewActionResult(accessReviewService.read(actor, taskId, request.correlationId()), "accepted", "Access-review task read through backend-authoritative User Admin capability.", request.correlationId(), actor);
    } else if ("action-useradmin-cancel-access-review".equals(request.actionId())) {
      var task = accessReviewService.cancel(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "workstream cancel"), request.correlationId());
      result = accessReviewActionResult(task, "accepted", "Access-review task cancellation recorded; access state unchanged.", request.correlationId(), actor);
    } else if ("action-useradmin-accept-access-review-result".equals(request.actionId())) {
      var task = accessReviewService.acceptResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "accepted by User Admin"), request.correlationId());
      result = accessReviewActionResult(task, "accepted", "Access-review result accepted as human review evidence; access state unchanged.", request.correlationId(), actor);
    } else if ("action-useradmin-reject-access-review-result".equals(request.actionId())) {
      var task = accessReviewService.rejectResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "rejected by User Admin"), request.correlationId());
      result = accessReviewActionResult(task, "accepted", "Access-review result rejected as human review evidence; access state unchanged.", request.correlationId(), actor);
    } else if ("action-useradmin-request-identity-relink".equals(request.actionId())) {
      var recovery = userAdminService.requestIdentityRelink(actor, stringInput(request.input(), "accountId", actor.account().accountId()), stringInput(request.input(), "reason", "User Admin identity exception recovery request"), request.idempotencyKey(), request.correlationId());
      result = identityRelinkActionResult(recovery, recovery.status(), recovery.message(), request.correlationId(), actor);
    } else if ("action-useradmin-read-identity-relink".equals(request.actionId())) {
      var recovery = userAdminService.readIdentityRelink(actor, stringInput(request.input(), "recoveryId", stringInput(request.input(), "accountId", actor.account().accountId())), request.correlationId());
      result = identityRelinkActionResult(recovery, "accepted", "Identity recovery loaded through durable backend lifecycle state.", request.correlationId(), actor);
    } else if ("action-useradmin-approve-identity-relink".equals(request.actionId())) {
      var recovery = userAdminService.approveIdentityRelink(actor, stringInput(request.input(), "recoveryId", stringInput(request.input(), "accountId", actor.account().accountId())), stringInput(request.input(), "reason", "Approved by User Admin"), stringInput(request.input(), "approvalRef", ""), request.idempotencyKey(), request.correlationId());
      result = identityRelinkActionResult(recovery, recovery.status(), recovery.message(), request.correlationId(), actor);
    } else if ("action-useradmin-deny-identity-relink".equals(request.actionId())) {
      var recovery = userAdminService.denyIdentityRelink(actor, stringInput(request.input(), "recoveryId", stringInput(request.input(), "accountId", actor.account().accountId())), stringInput(request.input(), "reason", "Denied by User Admin"), request.idempotencyKey(), request.correlationId());
      result = identityRelinkActionResult(recovery, recovery.status(), recovery.message(), request.correlationId(), actor);
    } else if ("action-useradmin-complete-identity-relink".equals(request.actionId())) {
      var recovery = userAdminService.completeIdentityRelink(actor, stringInput(request.input(), "accountId", stringInput(request.input(), "recoveryId", actor.account().accountId())), stringInput(request.input(), "approvalRef", ""), request.idempotencyKey(), request.correlationId());
      result = identityRelinkActionResult(recovery, recovery.status(), recovery.message(), request.correlationId(), actor);
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
    } else if ("action-agentadmin-start-prompt-risk-review".equals(request.actionId())) {
      var task = promptRiskReviewService.start(actor, new AgentAdminPromptRiskReviewService.StartPromptRiskReviewCommand(
          stringInput(request.input(), "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
          stringInput(request.input(), "proposalId", "proposal-agent-admin-prompt-001"),
          List.of(new PromptRiskReviewTask.BehaviorArtifactDelta(PromptRiskReviewTask.ArtifactKind.PROMPT_DOCUMENT, "prompt-agent-admin-system", 1, 2, "Review redacted prompt behavior changes for authority expansion, secret exposure, and tool-boundary bypass language.", "redacted-diff-agent-admin-prompt", "sha256:before-redacted", "sha256:after-redacted")),
          List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"),
          request.idempotencyKey()), request.correlationId());
      result = promptRiskReviewActionResult(task, task.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "accepted", "Prompt-risk review task is backend-governed; result is advisory and cannot activate behavior artifacts.", request.correlationId(), actor);
    } else if ("action-agentadmin-read-prompt-risk-review".equals(request.actionId())) {
      var taskId = stringInput(request.input(), "taskId", "");
      result = taskId.isBlank()
          ? new CapabilityActionResult("accepted", "Prompt-risk review status surface loaded; no task id was selected from the dashboard queue.", request.correlationId(), List.of("trace-agent-admin-prompt-risk-status-" + stableSuffix(request.correlationId())), agentPromptRiskReviewEmptySurface(actor, request.correlationId()))
          : promptRiskReviewActionResult(promptRiskReviewService.read(actor, taskId, request.correlationId()), "accepted", "Prompt-risk review state loaded from backend projection.", request.correlationId(), actor);
    } else if ("action-agentadmin-cancel-prompt-risk-review".equals(request.actionId())) {
      var task = promptRiskReviewService.cancel(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "workstream cancel"), request.correlationId());
      result = promptRiskReviewActionResult(task, "accepted", "Prompt-risk review cancellation recorded; behavior artifacts unchanged.", request.correlationId(), actor);
    } else if ("action-agentadmin-accept-prompt-risk-review-result".equals(request.actionId())) {
      var task = promptRiskReviewService.acceptResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "accepted by Agent Admin"), request.correlationId());
      result = promptRiskReviewActionResult(task, "accepted", "Prompt-risk result accepted as human review evidence; no behavior artifact was activated.", request.correlationId(), actor);
    } else if ("action-agentadmin-reject-prompt-risk-review-result".equals(request.actionId())) {
      var task = promptRiskReviewService.rejectResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "rejected by Agent Admin"), request.correlationId());
      result = promptRiskReviewActionResult(task, "accepted", "Prompt-risk result rejected as human review evidence; behavior artifacts unchanged.", request.correlationId(), actor);
    } else if ("action-deactivate-agent-definition".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.deactivation_confirmation.v1 preview", request.correlationId());
      result = new CapabilityActionResult("approval-required", "AgentDefinition deactivation requires a separate confirmation surface before any lifecycle mutation.", request.correlationId(), List.of("trace-agent-definition-deactivation-confirmation-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "deactivate", false));
    } else if ("action-activate-agent-definition".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.activation_confirmation.v1 preview", request.correlationId());
      result = new CapabilityActionResult("approval-required", "AgentDefinition activation requires a separate confirmation surface before any lifecycle mutation.", request.correlationId(), List.of("trace-agent-definition-activation-confirmation-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "activate", false));
    } else if ("action-import-agent-seed-defaults".equals(request.actionId())) {
      authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY);
      var seed = new AgentBehaviorSeedLoader(agentBehaviorRepository, Clock.systemUTC()).importStarterDefaults(actor.selectedContext().tenantId(), actor.account().accountId(), request.correlationId());
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "agent_admin.seed_import.v1 created=" + seed.createdCount() + " skipped=" + seed.skippedCount(), request.correlationId());
      result = new CapabilityActionResult(seed.createdCount() == 0 ? "no-op" : "accepted", "Seed import completed with " + seed.createdCount() + " created and " + seed.skippedCount() + " skipped governed records.", request.correlationId(), List.of("trace-agent-seed-import-" + stableSuffix(request.correlationId())), agentSeedImportConfirmationSurface(actor, request.correlationId(), Math.toIntExact(seed.createdCount()), Math.toIntExact(seed.skippedCount())));
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
    } else if ("action-audit-trace-request-redacted-export".equals(request.actionId())) {
      result = auditTraceReadResult(actor, "Redacted export request opened as a policy-gated decision surface; no unredacted browser export was produced.", request.correlationId(), auditTraceExportRequestSurface(actor, request.input(), request.idempotencyKey(), request.correlationId()));
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
    } else if ("action-governance-policy-start-impact-analysis".equals(request.actionId()) || "action-govpol-start-impact-analysis".equals(request.actionId())) {
      result = startGovernancePolicyImpactAnalysis(actor, request);
    } else if ("action-governance-policy-read-impact-analysis".equals(request.actionId()) || "action-govpol-read-impact-analysis".equals(request.actionId())) {
      var surface = governancePolicyImpactService.taskSurface(actor, stringInput(request.input(), "impactTaskId", stringInput(request.input(), "taskId", "")), request.correlationId());
      result = new CapabilityActionResult("accepted", "Governance/Policy impact analysis task loaded from backend lifecycle state.", request.correlationId(), surface.traceIds(), governancePolicyImpactEnvelope(actor, request.correlationId(), surface, governanceImpactTaskActions()));
    } else if ("action-governance-policy-cancel-impact-analysis".equals(request.actionId()) || "action-govpol-cancel-impact-analysis".equals(request.actionId())) {
      var task = governancePolicyImpactService.cancel(actor, stringInput(request.input(), "impactTaskId", stringInput(request.input(), "taskId", "")), stringInput(request.input(), "reason", "Cancelled by authorized Governance/Policy reviewer."), request.correlationId());
      var surface = governancePolicyImpactService.taskSurface(actor, task.impactTaskId(), request.correlationId());
      result = new CapabilityActionResult("accepted", "Governance/Policy impact analysis task cancelled; policy proposal unchanged.", request.correlationId(), surface.traceIds(), governancePolicyImpactEnvelope(actor, request.correlationId(), surface, governanceImpactTaskActions()));
    } else if ("action-governance-policy-accept-impact-result".equals(request.actionId()) || "action-govpol-accept-impact-result".equals(request.actionId())) {
      result = decideGovernancePolicyImpactResult(actor, request, "accept");
    } else if ("action-governance-policy-reject-impact-result".equals(request.actionId()) || "action-govpol-reject-impact-result".equals(request.actionId())) {
      result = decideGovernancePolicyImpactResult(actor, request, "reject");
    } else if ("action-governance-policy-request-impact-changes".equals(request.actionId()) || "action-govpol-request-impact-changes".equals(request.actionId())) {
      result = decideGovernancePolicyImpactResult(actor, request, "request_changes");
    }
    } catch (AuthorizationException denied) {
      if (isUserAdminAction(request.actionId())) result = userAdminSystemMessageResult(actor, request.actionId(), denied.reasonCode(), userAdminSafeDenialMessage(denied.reasonCode()), request.correlationId());
      else if (isGovernancePolicyAction(request.actionId())) result = governancePolicySystemMessageResult(actor, request.actionId(), denied.reasonCode(), governancePolicySafeDenialMessage(denied.reasonCode()), request.correlationId());
      else throw denied;
    }
    if (result == null) result = new CapabilityActionResult("accepted", action.label(), request.correlationId(), List.of("trace-" + request.actionId()), surfaceForAction(actor, request.actionId(), request.correlationId()));
    if (actionIdempotencyKey != null) idempotentActionResults.put(actionIdempotencyKey, result);
    return result;
  }

  private boolean isUserAdminAction(String actionId) {
    return actionId != null && (actionId.startsWith("action-useradmin-")
        || actionId.startsWith("action-open-useradmin-")
        || actionId.startsWith("action-display-user")
        || actionId.startsWith("action-display-invitation")
        || actionId.startsWith("action-invite-user")
        || actionId.startsWith("action-user-admin-show")
        || actionId.startsWith("action-organization")
        || actionId.startsWith("action-open-organization")
        || actionId.startsWith("action-customer")
        || actionId.startsWith("action-open-customer")
        || actionId.equals("action-display-organization-admin"));
  }

  private boolean isGovernancePolicyAction(String actionId) {
    return actionId != null && (actionId.startsWith("action-governance-policy-") || actionId.startsWith("action-govpol-") || actionId.equals("action-simulate-policy") || actionId.equals("action-commit-policy") || actionId.equals("action-open-governance-policy"));
  }

  private CapabilityActionResult userAdminSystemMessageResult(AuthContextResolver.ResolvedMe actor, String actionId, String code, String message, String correlationId) {
    var surface = userAdminSystemMessageSurface(actor, code, message, actionId, correlationId);
    var status = code != null && (code.contains("no-op") || code.contains("already")) ? "no-op" : code != null && (code.contains("validation") || code.contains("required") || code.contains("unsupported")) ? "validation-error" : "denied";
    return new CapabilityActionResult(status, message, correlationId, surface.traceIds(), surface);
  }

  private String userAdminSafeDenialMessage(String code) {
    var normalized = firstNonBlank(code, "TARGET_NOT_FOUND_OR_FORBIDDEN");
    if (normalized.contains("last-admin")) return "This change is blocked because it would remove the final required admin for the selected scope.";
    if (normalized.contains("self-disable") || normalized.contains("self-admin-role-removal")) return "This self-action is blocked by backend User Admin policy for the selected scope.";
    if (normalized.contains("idempotency")) return "This action requires a client-generated idempotency key before it can be processed safely.";
    if (normalized.contains("not-found") || normalized.contains("forbidden") || normalized.contains("mismatch")) return "The requested User Admin target is unavailable in the selected context.";
    if (normalized.contains("provider") || normalized.contains("runtime")) return "The requested operation is blocked until governed provider/runtime configuration is available.";
    if (normalized.contains("unsupported")) return "This User Admin action is not supported for the selected state.";
    return "The requested User Admin action could not be completed in the selected context.";
  }

  private SurfaceEnvelope userAdminSystemMessageSurface(AuthContextResolver.ResolvedMe actor, String code, String message, String sourceActionId, String correlationId) {
    return new SurfaceEnvelope("surface-user-admin-system-message", "system-message", "v1", "User Admin action unavailable", USER_ADMIN_AGENT_ID, List.of(AUDIT_TRACE_AGENT_ID), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, List.of("trace-useradmin-system-message-" + stableSuffix(correlationId + ":" + firstNonBlank(sourceActionId, code))), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("hiddenUserId", "hiddenMembershipId", "rawInvitationToken", "rawJwt", "rawProviderCredential", "providerPayload")), mapOf("surfaceContract", "user_admin.system_message.v1", "severity", "forbidden", "reasonCode", firstNonBlank(code, "TARGET_NOT_FOUND_OR_FORBIDDEN"), "code", firstNonBlank(code, "TARGET_NOT_FOUND_OR_FORBIDDEN"), "title", "User Admin action unavailable", "message", message, "body", message, "sourceActionId", sourceActionId, "branchNavigation", userBranchNavigation(correlationId), "selectedScopeLabel", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "recoverySteps", List.of("Return to the User Directory", "Refresh the selected context", "Open authorized audit evidence if available"), "traceRefs", List.of("trace-useradmin-system-message-" + stableSuffix(correlationId + ":" + firstNonBlank(sourceActionId, code))), "correlationId", correlationId, "redactionNote", "Hidden users, memberships, invitations, provider payloads, raw JWTs, and cross-scope facts are redacted.", "noFakeSuccess", true), withUserBranchReturn(List.of(openAuditAction())) , List.of());
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
        actor.selectedContext().tenantId(), runtimeAgentDefinitionId(request.functionalAgentId()), actor.selectedContext(), requestCorrelationId, request.prompt()));
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


  private String runtimeAgentDefinitionId(String functionalAgentId) {
    return AGENT_ADMIN_AGENT_ID.equals(functionalAgentId) ? AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID : functionalAgentId;
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
        mapOf("surfaceContract", "my_account.personal_command_center.v1", "canonicalAccessProfileContextSurfaceId", ACCESS_PROFILE_CONTEXT_SURFACE_ID, "coreCapabilityAliases", List.of(CORE_ACCESS_ME_CAPABILITY, CORE_PROFILE_UPDATE_CAPABILITY, CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), "cards", cards, "sections", dashboard.sections(), "attentionItems", personalAttention, "needsAttention", personalAttention, "attentionCounters", myAccountAttentionCounters(dashboard.cards()), "attentionSource", AttentionService.LIST_MY_ACCOUNT_ITEMS_TOOL, "accountContext", mapOf("displayName", actor.profile().displayName(), "email", actor.account().displayEmail(), "tenantLabel", "Current organization", "customerLabel", actor.selectedContext().customerId() == null ? "Tenant scope" : "Selected customer", "selectedContextLabel", "Current signed-in context", "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "roles", actor.selectedContext().roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(), "authority", dashboard.authorityBasis().primaryRoleBasis()), "quickSurfaceActionIds", dashboard.nextSteps().stream().map(step -> step.get("actionId")).filter(Objects::nonNull).toList(), "utilityActionIds", List.of("action-show-my-profile", "action-show-my-settings", "action-show-my-context", "action-show-my-account-notification-center", "action-sign-out"), "controlPanels", myAccountControlPanels(notificationCenter), "authorizedWorkstreamLinks", dashboard.nextSteps(), "notificationCenter", mapOf("surfaceId", "surface-my-account-notification-center", "surfaceContract", notificationCenter.surfaceContract(), "channel", "in_app", "unreadCount", notificationCenter.unreadCount(), "visibleCount", notificationCenter.visibleCount(), "countSource", NotificationService.LIST_MY_ACCOUNT_CENTER_TOOL), "personalAttentionDigest", mapOf("surfaceIds", List.of("surface-my-account-personal-attention-digest-progress", "surface-my-account-personal-attention-digest-result", "surface-my-account-personal-attention-digest-blocked"), "statusSource", "backend-projected MyAccountPersonalAttentionDigestTask", "noDirectMutation", true, "capabilityIds", List.of(MY_ACCOUNT_DIGEST_START_CAPABILITY, MY_ACCOUNT_DIGEST_READ_CAPABILITY, MY_ACCOUNT_DIGEST_CANCEL_CAPABILITY, MY_ACCOUNT_DIGEST_ACCEPT_CAPABILITY, MY_ACCOUNT_DIGEST_REJECT_CAPABILITY)), "nextSteps", dashboard.nextSteps(), "traceRefs", dashboard.traceRefs(), "authorityBasis", dashboard.authorityBasis(), "contextCapabilityGroups", dashboard.capabilityGroups(), "redaction", "Personal attention and notifications only include authorized sibling workstreams; hidden workstreams return not_found_or_redacted without names or counts.", "systemStates", List.of("system_message", "selected context", "authority", "tenant", "trace", "personal attention", "personal attention digest", "notification center", "trace refs", "not_found_or_redacted", "blocked_provider_or_runtime")),
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
            "targetSurfaceId", card.get("surfaceId"),
            "workstreamId", card.get("workstreamId"),
            "requiredCapabilityId", card.get("requiredCapabilityId"),
            "redaction", card.getOrDefault("redaction", "Authorized in this context"),
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
      workstreamEventRepository.listTenant(workstreamEventTenantId(actor.selectedContext())).stream()
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
        mapOf("surfaceContract", "my_account.context_authority.v1", "canonicalSurfaceId", "surface-my-context", "selectedContext", mapOf("selectedContextId", actor.selectedContext().membershipId(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId()), "visibleCapabilitySummary", mapOf("count", actor.selectedContext().capabilities().size(), "capabilityIds", actor.selectedContext().capabilities()), "roleSummary", actor.selectedContext().roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(), "recordId", actor.selectedContext().membershipId(), "recordLabel", actor.selectedContext().tenantId() + " selected context", "recordKind", "auth-context", "summary", "Backend-resolved selected AuthContext, active membership, role/capability basis, and available context switch targets. Context selection is performed by /api/me and protected workstream APIs using X-Selected-Context-Id; the browser cannot grant authority by editing this surface.", "fields", List.of(mapOf("fieldId", "selectedContextId", "label", "Selected context", "value", actor.selectedContext().membershipId(), "editable", false, "inputType", "text", "disabledReason", "Context changes must be requested through /api/me or protected shell APIs."), mapOf("fieldId", "tenantId", "label", "Tenant", "value", actor.selectedContext().tenantId(), "editable", false, "inputType", "text"), mapOf("fieldId", "customerId", "label", "Customer", "value", actor.selectedContext().customerId(), "editable", false, "inputType", "text"), mapOf("fieldId", "roles", "label", "Roles", "value", actor.selectedContext().roles().toString(), "editable", false, "inputType", "text"), mapOf("fieldId", "capabilityCount", "label", "Visible capabilities", "value", actor.selectedContext().capabilities().size(), "editable", false, "inputType", "number")), "availableContexts", actor.memberships().stream().filter(membership -> membership.active()).map(membership -> mapOf("selectedContextId", membership.membershipId(), "tenantId", membership.tenantId(), "customerId", membership.customerId(), "status", membership.status().name().toLowerCase(), "roleIds", membership.roles().stream().map(role -> role.name().toLowerCase().replace('_', '-')).sorted().toList(), "selectable", true, "selected", actor.selectedContext().membershipId().equals(membership.membershipId()), "selectionApi", "/api/me?selectedContextId=" + membership.membershipId(), "actionId", "action-select-my-context", "capabilityId", CORE_ACCESS_CONTEXT_SELECT_CAPABILITY, "staleImpact", "Switching context refreshes the shell, workstream counters, traces, notifications, and any open structured surfaces.")).toList(), "permissionState", mapOf("canEdit", false, "authoritativeCapabilityId", MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY, "coreCapabilityAlias", CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), "capabilityAliases", List.of(CORE_ACCESS_ME_CAPABILITY, CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), "traceRefs", myAccountService.traceRefs(actor, correlationId), "audit", mapOf("lastEventType", "AuthContextDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-my-context"))),
        List.of(showDashboardAction(), showProfileAction(), showSettingsAction(), selectContextAction(), openAuditAction()));
  }

  private SurfaceEnvelope myAccountOpenDeniedSurface(AuthContextResolver.ResolvedMe actor, MyAccountService.OpenWorkstreamDecision decision, String correlationId) {
    return envelope("surface-my-account-open-denied", "system_message", "Workstream unavailable", actor, correlationId,
        mapOf("surfaceContract", "my_account.open_denied.v1", "status", "not_found_or_redacted", "severity", "warning", "title", "Workstream unavailable", "message", decision.message(), "capabilityId", MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "safeReasonCode", decision.safeReasonCode(), "recoverySteps", List.of("Review the selected context and authority basis in My Account.", "Ask an administrator for access if this workstream should be available."), "traceRefs", decision.traceIds(), "redaction", "target workstream details are redacted when unauthorized"),
        List.of(showProfileAction(), showSettingsAction(), openAuditAction()));
  }

  private CapabilityActionResult personalAttentionDigestActionResult(MyAccountPersonalAttentionDigestTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), personalAttentionDigestSurface(actor, task, correlationId));
  }

  private SurfaceEnvelope personalAttentionDigestSurface(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask task, String correlationId) {
    if (task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME) return personalAttentionDigestBlockedSurface(actor, task, correlationId);
    var completed = task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY || task.status() == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED || task.status() == MyAccountPersonalAttentionDigestTask.Status.REJECTED;
    return envelope(completed ? "surface-my-account-personal-attention-digest-result" : "surface-my-account-personal-attention-digest-progress", completed ? "outcome-panel" : "workflow-status", completed ? "Personal attention digest result" : "Personal attention digest progress", actor, correlationId,
        mapOf("surfaceContract", completed ? "my_account.personal_attention_digest.result.v1" : "my_account.personal_attention_digest.progress.v1", "digestTaskId", task.digestTaskId(), "autonomousAgentTaskId", task.autonomousAgentTaskId(), "status", task.status().name().toLowerCase(Locale.ROOT), "phase", task.status().name().toLowerCase(Locale.ROOT).replace('_', '-'), "progressPercent", task.progressPercent(), "summary", task.summary(), "authorizedAttentionCount", task.authorizedAttentionCount(), "sectionRefs", task.sectionRefs(), "evidenceRefs", digestEvidenceRefs(task), "materialEvents", digestEvidenceRefs(task), "recommendations", digestRecommendations(task), "omissions", mapOf("redactionSummary", "Hidden workstreams/items are omitted and not counted."), "authorizedSourceCounts", mapOf("attention", task.authorizedAttentionCount()), "sourceSurfaceRefs", digestSourceSurfaceRefs(task), "decisionState", task.status().name().toLowerCase(Locale.ROOT), "progressEvents", digestProgressEvents(task), "traceRefs", task.traceIds(), "redaction", "Authorized personal attention evidence only; hidden workstreams/items are not counted or named.", "noDirectMutation", true, "safety", "This digest is advisory. Source attention remains authoritative and source item lifecycle changes require separate governed capabilities."),
        completed ? List.of(readPersonalAttentionDigestAction(), acceptPersonalAttentionDigestAction(), rejectPersonalAttentionDigestAction(), openAuditAction()) : List.of(readPersonalAttentionDigestAction(), cancelPersonalAttentionDigestAction(), openAuditAction()));
  }

  private List<Map<String, Object>> digestEvidenceRefs(MyAccountPersonalAttentionDigestTask task) {
    return task.evidenceRefs().stream()
        .map(ref -> mapOf(
            "refId", ref,
            "label", digestRefLabel(ref),
            "summary", digestRefSummary(ref),
            "traceId", task.traceIds().isEmpty() ? null : task.traceIds().get(0)))
        .toList();
  }

  private List<Map<String, Object>> digestRecommendations(MyAccountPersonalAttentionDigestTask task) {
    if (task.sectionRefs().isEmpty()) return List.of(mapOf("recommendationId", "review-authorized-source-work", "label", "Review authorized source work", "summary", "Open source workstreams through their governed counters before resolving any source attention.", "risk", "low", "confidence", "backend-projected"));
    return task.sectionRefs().stream()
        .map(section -> mapOf("recommendationId", "recommendation-" + stableSuffix(section), "label", digestRefLabel(section), "summary", "Advisory digest section; source lifecycle changes require separate governed source-workstream actions.", "risk", "low", "confidence", "backend-projected"))
        .toList();
  }

  private List<Map<String, Object>> digestSourceSurfaceRefs(MyAccountPersonalAttentionDigestTask task) {
    return task.evidenceRefs().stream()
        .filter(ref -> ref.startsWith("attention_item:"))
        .map(ref -> mapOf("refId", ref, "label", "Authorized attention item", "targetSurfaceType", "source-workstream", "requiresReauthorization", true))
        .toList();
  }

  private List<Map<String, Object>> digestProgressEvents(MyAccountPersonalAttentionDigestTask task) {
    var events = new ArrayList<Map<String, Object>>();
    events.add(mapOf("eventId", "digest-task-created", "label", "Digest task accepted", "status", "queued", "summary", "Backend created the governed personal digest task record."));
    if (task.autonomousAgentTaskId() != null && !task.autonomousAgentTaskId().isBlank()) events.add(mapOf("eventId", "autonomous-agent-task", "label", "Autonomous task linked", "status", task.status().name().toLowerCase(Locale.ROOT), "summary", "Provider/runtime projection controls progress; no source attention is mutated."));
    if (task.blockerCode() != null && !task.blockerCode().isBlank()) events.add(mapOf("eventId", "digest-blocked", "label", "Fail-closed blocker", "status", "blocked_provider_or_runtime", "summary", task.blockerCode()));
    if (task.terminal() || task.resultDecisionAllowed()) events.add(mapOf("eventId", "digest-review", "label", "Review state reached", "status", task.status().name().toLowerCase(Locale.ROOT), "summary", "Human review can accept or reject the advisory digest without changing source work."));
    return List.copyOf(events);
  }

  private String digestRefLabel(String ref) {
    if (ref == null) return "Digest evidence";
    if (ref.startsWith("attention_item:")) return "Authorized attention item";
    if (ref.startsWith("capability:")) return "Authorized capability read";
    if (ref.startsWith("readSkill:")) return "Approved skill guidance";
    if (ref.startsWith("readReferenceDoc:")) return "Approved reference document";
    return ref.replace('_', ' ').replace(':', ' ');
  }

  private String digestRefSummary(String ref) {
    if (ref == null) return "Browser-safe digest evidence.";
    if (ref.startsWith("attention_item:")) return "Included only because the source attention item was authorized in the selected context.";
    if (ref.startsWith("capability:")) return "Backend capability used to read authorized personal evidence.";
    if (ref.startsWith("readSkill:") || ref.startsWith("readReferenceDoc:")) return "Behavior guidance loaded through governed runtime boundaries.";
    return "Browser-safe digest evidence.";
  }

  private SurfaceEnvelope personalAttentionDigestBlockedSurface(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask task, String correlationId) {
    return envelope("surface-my-account-personal-attention-digest-blocked", "system_message", "Personal attention digest blocked", actor, correlationId,
        mapOf("surfaceContract", "my_account.personal_attention_digest.blocked.v1", "digestTaskId", task == null ? "" : task.digestTaskId(), "autonomousAgentTaskId", task == null ? "" : task.autonomousAgentTaskId(), "status", "blocked_provider_or_runtime", "severity", "blocked", "title", "Provider/runtime configuration is required", "message", task == null ? "My Account personal attention digest fails closed until backend provider/runtime configuration is available." : task.summary(), "blockerCode", task == null ? "blocked_provider_or_runtime" : task.blockerCode(), "recoverySteps", List.of("Configure the Akka AutonomousAgent provider/runtime and governed tool grants.", "Retry from My Account after readiness is restored."), "traceRefs", task == null ? List.of("trace-my-account-personal-attention-digest-blocked") : task.traceIds(), "noFakeSuccess", true, "noDirectMutation", true, "redaction", "No deterministic, fake, fixture, simulated, or model-less personal attention digest success is returned."),
        List.of(showDashboardAction(), readPersonalAttentionDigestAction(), openAuditAction()));
  }

  private SurfaceEnvelope personalAttentionDigestEmptyProgressSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-account-personal-attention-digest-progress", "workflow-status", "Personal attention digest", actor, correlationId,
        mapOf("surfaceContract", "my_account.personal_attention_digest.progress.v1", "status", "not_started", "phase", "not-started", "progressEvents", List.of("ready-to-start"), "summary", "Start a backend-governed My Account personal attention digest to summarize only authorized personal attention evidence.", "authorizedAttentionCount", 0, "traceRefs", List.of("trace-my-account-personal-attention-digest-not-started"), "redaction", "Hidden workstreams/items are not counted or named.", "noDirectMutation", true),
        List.of(startPersonalAttentionDigestAction(), openAuditAction()));
  }

  private SurfaceEnvelope dashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var layer = userAdminLayer(actor);
    authContextResolver.appendProtectedReadTrace(actor, layer.readCapability(), layer.surfaceContract(), correlationId);
    var users = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var invites = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var pendingInvites = invites.stream().filter(WorkstreamService::isPendingInvitation).count();
    var failedInvites = invites.stream().filter(invite -> invite.deliveryStatus().name().equals("FAILED")).count();
    seedUserAdminInvitationAttention(actor, failedInvites, correlationId);
    var attentionItems = attentionMaps(attentionService.listWorkstreamItems(actor, USER_ADMIN_AGENT_ID, correlationId));
    var expiringSupport = users.stream().filter(UserDirectoryView.UserDirectoryRow::supportAccess).count();
    var deniedAdminActions = userAdminService.auditEvents(actor, 25, correlationId).stream().filter(event -> event.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.DENIED).count();
    var actions = layer.actions();
    return envelope(layer.surfaceId(), "dashboard", layer.title(), actor, correlationId,
        mapOf(
            "surfaceContract", layer.surfaceContract(),
            "adminLayer", layer.layerId(),
            "scopeLabel", layer.scopeLabel(),
            "boundaryNotice", layer.boundaryNotice(),
            "readiness", mapOf("directory", "backend-derived", "invitationOutbox", "backend-derived", "supportAccess", "backend-derived", "adminAudit", "backend-derived", "accessReviewWorker", "autonomous_agent_runtime_projected", "organizationAdmin", layer.saasOwner() ? "backend-derived" : "not_applicable"),
            "cards", List.of(
                mapOf("cardId", "card-pending-invitations", "label", "Pending invitations", "value", pendingInvites, "severity", pendingInvites == 0 ? "info" : "warning", "surfaceId", "surface-user-admin-users", "actionId", "action-display-user-list"),
                mapOf("cardId", "card-active-users", "label", layer.activeUsersLabel(), "value", users.size(), "severity", "info", "surfaceId", "surface-user-admin-users", "actionId", "action-display-user-list"),
                mapOf("cardId", "card-failed-invitations", "label", "Failed invitation delivery", "value", failedInvites, "severity", failedInvites == 0 ? "info" : "warning", "surfaceId", "surface-user-admin-users", "actionId", "action-display-user-list"),
                mapOf("cardId", "card-support-access", "label", "Expiring support access", "value", expiringSupport, "severity", expiringSupport == 0 ? "info" : "warning", "surfaceId", "surface-user-admin-users", "actionId", "action-display-user-list"),
                mapOf("cardId", "card-admin-audit", "label", "Recent denied admin actions", "value", deniedAdminActions, "severity", "info", "surfaceId", "surface-audit-trace-dashboard", "actionId", "action-open-audit-trace"),
                mapOf("cardId", "card-access-review", "label", "Access review items", "value", 0, "severity", "blocked_provider_or_runtime", "surfaceId", "surface-user-admin-access-review-task", "actionId", "action-useradmin-read-access-review")),
            "attentionCounts", userAdminAttentionCounts(pendingInvites, failedInvites, expiringSupport, deniedAdminActions, correlationId),
            "administeredPopulations", userAdminAdministeredPopulations(layer, users, invites, correlationId),
            "attentionItems", attentionItems,
            "attentionSource", AttentionService.LIST_WORKSTREAM_ITEMS_TOOL,
            "accountContext", mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "roles", actor.selectedContext().roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(), "authority", layer.scopeLabel()),
            "selectedAuthContext", mapOf("selectedContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId()),
            "adminLevel", layer.layerId(),
            "hero", mapOf("title", layer.title(), "scopeLabel", layer.scopeLabel(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "adminLevel", layer.layerId(), "administeredPopulationLabels", userAdminAdministeredPopulations(layer, users, invites, correlationId).stream().map(population -> String.valueOf(population.get("label"))).toList(), "supportAccessState", expiringSupport == 0 ? "No visible support grants are expiring." : expiringSupport + " visible support grant(s) are active or expiring.", "redactionSummary", "Hidden users, hidden counts, cross-scope evidence, provider internals, tokens, and raw correlation mechanics are omitted from the default dashboard.", "traceRefs", List.of("trace-" + layer.surfaceId()), "correlationId", correlationId),
            "authorityBasis", "Backend-authored User Admin dashboard variant of surface-user-admin-dashboard; unavailable actions and hidden counts are omitted by selected AuthContext.",
            "canonicalSurface", mapOf("canonicalSurfaceId", "surface-user-admin-dashboard", "canonicalSurfaceType", "dashboard", "runtimeVariantSurfaceId", layer.surfaceId(), "compatibility", "role-specific dashboard variant"),
            "defaultView", mapOf("summary", layer.boundaryNotice(), "diagnosticMetadataVisible", false),
            "diagnosticMetadata", mapOf("visibility", "role-gated", "traceRefs", List.of("trace-" + layer.surfaceId()), "omittedFromDefault", List.of("rawCapabilityDiagnostics", "correlationMechanics", "internalProviderState")),
            "sections", layer.sections(),
            "navigationTree", mapOf("trunkSurfaceId", "surface-user-admin-dashboard", "branchActions", actions.stream().filter(action -> List.of("surface-user-admin-saas-owner-admins", "surface-user-admin-users", "surface-user-admin-organization-directory", "surface-user-admin-customer-directory").contains(action.resultSurface() == null ? null : action.resultSurface().updateSurfaceId())).map(this::navigationActionMap).toList(), "authorization", "Branch actions are backend-authored from the selected AuthContext; unauthorized SaaS Owner, Organization, or Customer branches are omitted."),
            "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(),
            "capabilityIds", layer.capabilityIds()),
        actions);
  }

  private UserAdminLayer userAdminLayer(AuthContextResolver.ResolvedMe actor) {
    return switch (actor.selectedContext().scopeType()) {
      case SAAS_OWNER -> new UserAdminLayer(
          "saas-owner", "surface-user-admin-saas-owner-dashboard", "user_admin.saas_owner_dashboard.v1", "SaaS Owner Admin Dashboard", "SaaS Owner scope", true,
          SAAS_OWNER_USER_MANAGE_CAPABILITY,
          "Platform administrators",
          "Administer tenant/organization lifecycle and platform-level user administration. This does not grant tenant application-data access, provider secret access, support internals, or billing-derived authority.",
          List.of(SAAS_OWNER_ADMIN_LIST_CAPABILITY, SAAS_OWNER_ADMIN_INVITE_CAPABILITY, SAAS_OWNER_ADMIN_MANAGE_CAPABILITY, SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, SAAS_OWNER_ORGANIZATION_READ_CAPABILITY, SAAS_OWNER_TENANT_READ_CAPABILITY, SAAS_OWNER_TENANT_MANAGE_CAPABILITY, SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY, SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY, SAAS_OWNER_ORGANIZATION_ADMIN_MANAGE_CAPABILITY, SAAS_OWNER_USER_MANAGE_CAPABILITY, "saas_owner.audit.read"),
          List.of(showSaasOwnerAdminsAction(), showOrganizationsAction(), showUsersAction(), displayListAction(), openSaasOwnerAdminInvitationCreateAction(), openInvitationCreateAction(), openAuditAction(), readAccessReviewAction()));
      case CUSTOMER -> new UserAdminLayer(
          "customer", "surface-user-admin-customer-dashboard", "user_admin.customer_dashboard.v1", "Customer Admin Dashboard", "Customer scope", false,
          "customer.user.read",
          "Customer users",
          "Administer users, invitations, roles, audit, and access review only for the selected customer within its tenant.",
          List.of("customer.user.read", "customer.user.manage", "customer.role.manage", "customer.invitation.manage", "customer.audit.read", "customer.access_review.manage"),
          List.of(showUsersAction(), displayListAction(), openInvitationCreateAction(), readSupportAccessAction(), openAuditAction(), startAccessReviewAction(), readAccessReviewAction()));
      default -> new UserAdminLayer(
          "tenant", "surface-user-admin-tenant-dashboard", "user_admin.tenant_dashboard.v1", "Tenant Admin Dashboard", "Tenant scope", false,
          USER_ADMIN_CAPABILITY,
          "Tenant users",
          "Administer tenant users, invitations, roles, support access, audit, and access review for the selected tenant.",
          List.of(USER_ADMIN_CAPABILITY, TENANT_CUSTOMER_LIST_CAPABILITY, TENANT_CUSTOMER_READ_CAPABILITY, TENANT_CUSTOMER_CREATE_CAPABILITY, TENANT_CUSTOMER_RENAME_CAPABILITY, TENANT_CUSTOMER_SUSPEND_CAPABILITY, TENANT_CUSTOMER_REACTIVATE_CAPABILITY, TENANT_CUSTOMER_ADMIN_LIST_CAPABILITY, TENANT_CUSTOMER_ADMIN_INVITE_CAPABILITY, TENANT_CUSTOMER_ADMIN_MANAGE_CAPABILITY, "tenant.user.read", "tenant.user.manage", "tenant.role.manage", "tenant.invitation.manage", "tenant.support_access.manage", "tenant.audit.read", "tenant.access_review.manage"),
          List.of(showCustomersAction(), showUsersAction(), displayListAction(), openCustomerCreateAction(), openInvitationCreateAction(), readSupportAccessAction(), openAuditAction(), startAccessReviewAction(), readAccessReviewAction()));
    };
  }

  private SurfaceEnvelope organizationAdminSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return organizationDirectorySurface(actor, correlationId);
  }

  private SurfaceEnvelope organizationDirectorySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return organizationDirectorySurface(actor, null, correlationId);
  }

  private SurfaceEnvelope organizationDirectorySurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-directory", "list-search", "Organization Directory", "user_admin.organization_directory.v1", List.of(organizationListAction(), organizationReadAction(), openOrganizationCreateAction(), openAuditAction()), null, stringInput(input, "query", ""), stringInput(input, "status", ""));
  }

  private SurfaceEnvelope organizationDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-detail", "show-inspection", "Organization Detail", "user_admin.organization_detail.v1", withOrganizationBranchReturn(List.of(openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), openOrganizationAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-create", "create-form", "Create Organization", "user_admin.organization_create.v1", withOrganizationBranchReturn(List.of(organizationCreateAction())));
  }

  private SurfaceEnvelope organizationRenameSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-rename", "edit-form", "Rename Organization", "user_admin.organization_rename.v1", withOrganizationBranchReturn(List.of(organizationRenameAction())));
  }

  private SurfaceEnvelope organizationRenameSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-rename", "edit-form", "Rename Organization", "user_admin.organization_rename.v1", withOrganizationBranchReturn(List.of(organizationRenameAction())), readOrganizationDetail(actor, input, correlationId));
  }

  private SurfaceEnvelope organizationSuspendSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-suspend-confirmation", "destructive-lifecycle-confirmation", "Suspend Organization", "user_admin.organization_suspend_confirmation.v1", withOrganizationBranchReturn(List.of(organizationSuspendAction())));
  }

  private SurfaceEnvelope organizationSuspendSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    var detail = readOrganizationDetail(actor, input, correlationId);
    requireOrganizationLifecycleAction(detail, "suspend", correlationId);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-suspend-confirmation", "destructive-lifecycle-confirmation", "Suspend Organization", "user_admin.organization_suspend_confirmation.v1", withOrganizationBranchReturn(List.of(organizationSuspendAction())), detail);
  }

  private SurfaceEnvelope organizationReactivateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-reactivate-confirmation", "lifecycle-confirmation", "Reactivate Organization", "user_admin.organization_reactivate_confirmation.v1", withOrganizationBranchReturn(List.of(organizationReactivateAction())));
  }

  private SurfaceEnvelope organizationReactivateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    var detail = readOrganizationDetail(actor, input, correlationId);
    requireOrganizationLifecycleAction(detail, "reactivate", correlationId);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-reactivate-confirmation", "lifecycle-confirmation", "Reactivate Organization", "user_admin.organization_reactivate_confirmation.v1", withOrganizationBranchReturn(List.of(organizationReactivateAction())), detail);
  }

  private SurfaceEnvelope organizationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String surfaceType, String title, String contract, List<SurfaceAction> actions) {
    return organizationSurface(actor, correlationId, surfaceId, surfaceType, title, contract, actions, null);
  }

  private SurfaceEnvelope organizationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String surfaceType, String title, String contract, List<SurfaceAction> actions, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail) {
    return organizationSurface(actor, correlationId, surfaceId, surfaceType, title, contract, actions, detail, "", "");
  }

  private SurfaceEnvelope organizationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String surfaceType, String title, String contract, List<SurfaceAction> actions, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, String query, String status) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_READ_CAPABILITY);
    var service = StarterSecurityComponents.saasOwnerOrganizationAdminService();
    var listResult = detail == null ? service.listOrganizations(actor, query, status, correlationId) : null;
    var boundary = detail == null ? listResult.safeBoundaryNotice() : detail.safeBoundaryNotice();
    var organizations = detail == null
        ? listResult.organizations().stream().map(this::organizationSummaryMap).toList()
        : List.of(organizationSummaryMap(detail.organization()));
    var traceRefs = detail == null ? List.of(listResult.traceId()) : detail.traceRefs();
    var data = mapOf(
        "surfaceContract", contract,
        "scopeLabel", "SaaS Owner scope",
        "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
        "authorityBasis", "Backend checks selected AuthContext and saas_owner.organization.* mapped to internal tenant lifecycle capabilities; browser state cannot grant Organization authority.",
        "branchNavigation", organizationBranchNavigation(correlationId),
        "boundaryNotice", boundary,
        "safeBoundaryNotice", boundary,
        "traceRefs", traceRefs,
        "correlationId", correlationId,
        "redaction", List.of("tenant-app-data-redacted", "provider-secrets-redacted", "billing-authority-redacted", "support-access-internals-redacted", "hidden-counts-redacted"),
        "organizations", organizations,
        "filters", mapOf("query", query == null ? "" : query, "status", status == null ? "" : status),
        "pageInfo", mapOf("visibleCount", organizations.size()),
        "systemStates", List.of(organizations.isEmpty() ? "empty" : "ready"),
        "emptyMessage", "No Organizations are visible for this backend-authorized filter.");
    if (detail != null) data.put("organizationDetail", organizationDetailMap(detail));
    return envelope(surfaceId, surfaceType, title, actor, correlationId, data, actions);
  }

  private SurfaceEnvelope saasOwnerAdminsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_USER_MANAGE_CAPABILITY);
    var rows = userDirectoryView.list(actor, ScopeType.SAAS_OWNER, actor.selectedContext().tenantId(), null).stream()
        .map(user -> mapOf("id", user.membershipId(), "accountId", user.accountId(), "membershipId", user.membershipId(), "rowType", "saas-owner-admin", "targetObjectType", "saas-owner-admin", "targetSurfaceId", "surface-user-admin-user-detail", "targetSurfaceType", "show-inspection", "openActionId", "action-display-user-detail", "displayName", user.displayName(), "email", user.accountId(), "role", roleLabels(user.roles()), "status", user.membershipStatus().name().toLowerCase(Locale.ROOT), "traceRefs", List.of("trace-saas-owner-admin-" + stableSuffix(user.membershipId())), "redactionState", "visible"))
        .toList();
    return envelope("surface-user-admin-saas-owner-admins", "list-search", "SaaS Owner Admins", actor, correlationId,
        mapOf("surfaceContract", "user_admin.saas_owner_admins.v1", "branchNavigation", saasOwnerAdminBranchNavigation(correlationId), "query", "", "rows", rows, "filters", mapOf("scope", "saas-owner", "backendAuthored", true), "pageInfo", mapOf("visibleCount", rows.size()), "redaction", List.of("raw-provider-ids-redacted", "tenant-customer-data-redacted", "hidden-app-owner-counts-redacted"), "emptyMessage", "No SaaS Owner Admin users are visible in this selected scope.", "boundaryNotice", "SaaS Owner Admin management is app-owner scoped and does not grant tenant/customer application-data, support-access, or billing authority."),
        List.of(showSaasOwnerAdminsAction(), displayDetailAction(), openSaasOwnerAdminInvitationCreateAction(), openAuditAction()));
  }

  private SurfaceEnvelope saasOwnerAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_USER_MANAGE_CAPABILITY);
    return envelope("surface-user-admin-saas-owner-admin-invitation-create", "create-form", "Invite SaaS Owner Admin", actor, correlationId,
        mapOf("surfaceContract", "user_admin.saas_owner_admin_invitation_create.v1", "branchNavigation", saasOwnerAdminBranchNavigation(correlationId), "recordKind", "saas-owner-admin-invitation", "summary", "Invite another app-owner administrator. Backend authorization enforces SaaS Owner scope, SAAS_OWNER_ADMIN role validation, last-owner protections, idempotency, outbox readiness, and audit.", "draft", mapOf("email", "", "displayName", "", "roles", List.of("SAAS_OWNER_ADMIN")), "roleOptions", List.of(mapOf("roleId", "SAAS_OWNER_ADMIN", "label", "SaaS Owner Admin")), "policyOptions", mapOf("roles", List.of(mapOf("roleId", "SAAS_OWNER_ADMIN", "label", "SaaS Owner Admin")), "idempotency", "client-generated", "outboxReadiness", "backend-derived"), "idempotencyKeyHint", "client-generated", "traceRefs", List.of("trace-saas-owner-admin-invite-" + stableSuffix(correlationId)), "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "tenant-customer-data-redacted")),
        withSaasOwnerAdminBranchReturn(List.of(inviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationAdminsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_READ_CAPABILITY);
    return adminSubjectSurface(actor, correlationId, "surface-user-admin-organization-admins", "Organization Admins", "user_admin.organization_admins.v1", organizationBranchNavigation(correlationId), "TENANT_ADMIN", "Organization Admin users and invitations for the selected Organization/Tenant appear here when a target Organization is selected. Rows route to inspection/task surfaces and never expose tenant application data.", withOrganizationBranchReturn(List.of(openOrganizationAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    return roleScopedInvitationSurface(actor, correlationId, "surface-user-admin-organization-admin-invitation-create", "Invite Organization Admin", "user_admin.organization_admin_invitation_create.v1", organizationBranchNavigation(correlationId), "TENANT_ADMIN", "Bootstrap or invite a TENANT_ADMIN for the selected Organization after the Organization exists. Provider/outbox failures return system-message without fake success.", withOrganizationBranchReturn(List.of(inviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_READ_CAPABILITY);
    return scopedAdminDetailSurface(actor, correlationId, "surface-user-admin-organization-admin-detail", "Organization Admin Detail", "user_admin.organization_admin_detail.v1", organizationBranchNavigation(correlationId), "Organization Admin membership/invitation inspection. Role, status, resend, revoke, and audit changes route to dedicated User Admin task surfaces.", withOrganizationBranchReturn(List.of(openMembershipStatusConfirmationAction(), previewRoleChangeAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerDirectorySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var result = StarterSecurityComponents.tenantCustomerAdminService().listCustomers(actor, "", "", correlationId);
    var rows = result.customers().stream().map(this::customerRowMap).toList();
    return envelope("surface-user-admin-customer-directory", "list-search", "Customer Directory", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_directory.v1", "branchNavigation", customerBranchNavigation(correlationId), "query", "", "rows", rows, "customers", rows, "filters", mapOf("tenantId", actor.selectedContext().tenantId(), "backendAuthored", true), "pageInfo", mapOf("visibleCount", rows.size()), "boundaryNotice", result.safeBoundaryNotice(), "safeBoundaryNotice", result.safeBoundaryNotice(), "traceRefs", result.traceRefs(), "correlationId", result.correlationId(), "redaction", List.of("sibling-customers-redacted", "tenant-app-data-redacted", "provider-secrets-redacted"), "emptyMessage", "No Customers are visible in this selected Organization/Tenant scope."),
        List.of(showCustomersAction(), customerReadAction(), openCustomerCreateAction(), openAuditAction()));
  }

  private SurfaceEnvelope customerDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return scopedAdminDetailSurface(actor, correlationId, "surface-user-admin-customer-detail", "Customer Detail", "user_admin.customer_detail.v1", customerBranchNavigation(correlationId), "Customer lifecycle inspection. Create, rename, suspend/archive, reactivate, and Customer Admin management route through dedicated backend-authorized task surfaces.", withCustomerBranchReturn(List.of(openCustomerRenameAction(), openCustomerSuspendAction(), openCustomerReactivateAction(), showCustomerAdminsAction(), openCustomerAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var detail = StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, stringInput(input, "customerId", ""), correlationId);
    return customerDetailSurface(actor, detail, correlationId);
  }

  private SurfaceEnvelope customerDetailSurface(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail detail, String correlationId) {
    return envelope("surface-user-admin-customer-detail", "show-inspection", "Customer Detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_detail.v1", "branchNavigation", customerBranchNavigation(correlationId), "recordId", detail.customer().customerId(), "recordLabel", detail.customer().customerName(), "recordKind", "customer", "summary", "Customer lifecycle inspection. Consequential Customer work and Customer Admin management open dedicated backend-authorized surfaces.", "customerDetail", customerDetailMap(detail), "fields", List.of(mapOf("fieldId", "customerId", "label", "Customer", "value", detail.customer().customerId(), "editable", false, "inputType", "text"), mapOf("fieldId", "customerName", "label", "Name", "value", detail.customer().customerName(), "editable", false, "inputType", "text"), mapOf("fieldId", "status", "label", "Status", "value", detail.customer().status(), "editable", false, "inputType", "text")), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Inspection only; consequential work opens dedicated backend-authorized task surfaces."), "traceRefs", detail.traceRefs(), "correlationId", detail.correlationId(), "redaction", List.of("sibling-customers-redacted", "tenant-app-data-redacted", "provider-secrets-redacted")),
        withCustomerBranchReturn(List.of(openCustomerRenameAction(), openCustomerSuspendAction(), openCustomerReactivateAction(), showCustomerAdminsAction(), openCustomerAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-create", "Create Customer", "user_admin.customer_create.v1", "Create a Customer inside the selected Organization/Tenant with idempotency, audit, and sibling-customer redaction.", withCustomerBranchReturn(List.of(customerCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerRenameSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-rename", "Rename Customer", "user_admin.customer_rename.v1", "Update Customer display profile only. Backend handles stale, conflict, no-op, forbidden, and audit results.", withCustomerBranchReturn(List.of(customerRenameAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerRenameSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var detail = StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), correlationId);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-rename", "Rename Customer", "user_admin.customer_rename.v1", "Update Customer display profile only. Backend handles stale, conflict, no-op, forbidden, and audit results.", withCustomerBranchReturn(List.of(customerRenameAction(), openAuditAction())), detail);
  }

  private SurfaceEnvelope customerSuspendSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-suspend-confirmation", "Suspend Customer", "user_admin.customer_suspend_confirmation.v1", "Suspend/archive a Customer boundary after reason and confirmation. Tenant application data and sibling-customer facts remain hidden.", withCustomerBranchReturn(List.of(customerSuspendAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerSuspendSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var detail = StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), correlationId);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-suspend-confirmation", "Suspend Customer", "user_admin.customer_suspend_confirmation.v1", "Suspend/archive a Customer boundary after reason and confirmation. Tenant application data and sibling-customer facts remain hidden.", withCustomerBranchReturn(List.of(customerSuspendAction(), openAuditAction())), detail);
  }

  private SurfaceEnvelope customerReactivateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-reactivate-confirmation", "Reactivate Customer", "user_admin.customer_reactivate_confirmation.v1", "Reactivate a Customer boundary with idempotency, audit, and safe no-op handling.", withCustomerBranchReturn(List.of(customerReactivateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerReactivateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var detail = StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), correlationId);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-reactivate-confirmation", "Reactivate Customer", "user_admin.customer_reactivate_confirmation.v1", "Reactivate a Customer boundary with idempotency, audit, and safe no-op handling.", withCustomerBranchReturn(List.of(customerReactivateAction(), openAuditAction())), detail);
  }

  private SurfaceEnvelope customerAdminsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return adminSubjectSurface(actor, correlationId, "surface-user-admin-customer-admins", "Customer Admins", "user_admin.customer_admins.v1", customerBranchNavigation(correlationId), "CUSTOMER_ADMIN", "Customer Admin users and invitations require one selected Customer target. Open this branch from a Customer detail surface so backend-authored target proof is carried forward.", withCustomerBranchReturn(List.of(openCustomerAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminsSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    var detail = activeCustomerListTargetDetail(actor, input, correlationId);
    var target = customerTargetMap(actor, detail, correlationId);
    return envelope("surface-user-admin-customer-admins", "list-search", "Customer Admins", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_admins.v1", "branchNavigation", customerBranchNavigation(correlationId), "customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "targetScopeProof", target, "query", "", "rows", List.of(), "admins", List.of(), "invitations", List.of(), "filters", mapOf("role", "CUSTOMER_ADMIN", "customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "backendAuthored", true), "pageInfo", mapOf("visibleCount", 0), "summary", "Customer Admin users and invitations for " + detail.customer().customerName() + ". Rows route through backend-authored detail/task surfaces and never expose sibling-customer or tenant-wide authority.", "safeBoundaryNotice", detail.safeBoundaryNotice(), "branchRootSurfaceId", "surface-user-admin-customer-directory", "branchReturnActionId", "action-user-admin-show-customers", "traceRefs", detail.traceRefs(), "correlationId", correlationId, "redaction", List.of("hidden-users-redacted", "sibling-customers-redacted", "provider-payload-redacted", "raw-invitation-token-redacted"), "emptyMessage", "No Customer Admins are visible for this selected Customer yet."),
        withCustomerBranchReturn(List.of(openCustomerAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return roleScopedInvitationSurface(actor, correlationId, "surface-user-admin-customer-admin-invitation-create", "Invite Customer Admin", "user_admin.customer_admin_invitation_create.v1", customerBranchNavigation(correlationId), "CUSTOMER_ADMIN", "Bootstrap or invite a CUSTOMER_ADMIN for a selected Customer after the Customer exists. Open this form from Customer detail so backend-authored target proof is carried forward. Provider/outbox failures return system-message without fake success.", withCustomerBranchReturn(List.of(customerAdminInviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    var detail = activeCustomerInviteTargetDetail(actor, input, correlationId);
    var target = customerTargetMap(actor, detail, correlationId);
    return envelope("surface-user-admin-customer-admin-invitation-create", "create-form", "Invite Customer Admin", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_admin_invitation_create.v1", "branchNavigation", customerBranchNavigation(correlationId), "customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "targetScopeProof", target, "targetScope", mapOf("scopeType", ScopeType.CUSTOMER.name(), "tenantId", actor.selectedContext().tenantId(), "customerId", detail.customer().customerId()), "summary", "Bootstrap or invite a CUSTOMER_ADMIN for " + detail.customer().customerName() + ". The workstream submit path always targets ScopeType.CUSTOMER for this Customer.", "recordKind", "customer-admin-invitation", "recordId", detail.customer().customerId(), "recordLabel", detail.customer().customerName(), "draft", mapOf("email", "", "displayName", "", "roles", List.of("CUSTOMER_ADMIN"), "customerId", detail.customer().customerId()), "roleOptions", List.of(mapOf("roleId", "CUSTOMER_ADMIN", "label", "CUSTOMER ADMIN")), "policyOptions", mapOf("roles", List.of(mapOf("roleId", "CUSTOMER_ADMIN", "label", "CUSTOMER ADMIN")), "idempotency", "client-generated", "outboxReadiness", "backend-derived"), "idempotencyKeyHint", "client-generated", "outboxReadiness", "backend-derived", "boundaryNotice", detail.safeBoundaryNotice(), "traceRefs", detail.traceRefs(), "correlationId", correlationId, "redaction", List.of("raw-token-redacted", "provider-payload-redacted", "sibling-customers-redacted")),
        withCustomerBranchReturn(List.of(customerAdminInviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminInvitationDetailSurface(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail customer, Invitation invite, String correlationId) {
    return envelope("surface-user-admin-invitation-detail", "show-inspection", "Customer Admin invitation detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_detail.v1", "branchNavigation", customerBranchNavigation(correlationId), "recordId", invite.invitationId(), "recordLabel", invite.normalizedEmail(), "recordKind", "customer-admin-invitation", "customerId", customer.customer().customerId(), "customerName", customer.customer().customerName(), "targetScope", mapOf("scopeType", invite.scopeType().name(), "tenantId", invite.tenantId(), "customerId", invite.customerId()), "status", invite.status().name().toLowerCase(Locale.ROOT), "summary", "Customer Admin invitation was created in the selected Customer scope. Raw invitation tokens and provider payloads are redacted.", "fields", List.of(mapOf("fieldId", "email", "label", "Email", "value", invite.normalizedEmail(), "editable", false, "inputType", "email"), mapOf("fieldId", "scope", "label", "Scope", "value", invite.scopeType().name(), "editable", false, "inputType", "text"), mapOf("fieldId", "customerId", "label", "Customer", "value", invite.customerId(), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleLabels(invite.requestedRoles()), "editable", false, "inputType", "text")), "traceRefs", List.of("trace-customer-admin-invitation-" + stableSuffix(invite.invitationId())), "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "sibling-customers-redacted")),
        withCustomerBranchReturn(List.of(openCustomerAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return scopedAdminDetailSurface(actor, correlationId, "surface-user-admin-customer-admin-detail", "Customer Admin Detail", "user_admin.customer_admin_detail.v1", customerBranchNavigation(correlationId), "Customer Admin membership/invitation inspection. Role, status, resend, revoke, and audit changes route to dedicated User Admin task surfaces.", withCustomerBranchReturn(List.of(openMembershipStatusConfirmationAction(), previewRoleChangeAction(), openAuditAction())));
  }

  private ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail activeCustomerListTargetDetail(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return StarterSecurityComponents.tenantCustomerAdminService().requireActiveCustomerForCustomerAdminList(actor, customerIdInput(input), correlationId);
  }

  private ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail activeCustomerInviteTargetDetail(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return StarterSecurityComponents.tenantCustomerAdminService().requireActiveCustomerForCustomerAdminInvite(actor, customerIdInput(input), correlationId);
  }

  private String customerIdInput(Object input) {
    var customerId = stringInput(input, "customerId", stringInput(input, "recordId", ""));
    if (customerId == null || customerId.isBlank()) throw new AuthorizationException(400, "customer-id-required");
    return customerId;
  }

  private Map<String, Object> customerTargetMap(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail detail, String correlationId) {
    return mapOf("scopeType", ScopeType.CUSTOMER.name(), "tenantId", actor.selectedContext().tenantId(), "customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "status", detail.customer().status(), "source", "backend-authored-customer-detail", "correlationId", correlationId, "traceRefs", detail.traceRefs());
  }

  private SurfaceEnvelope adminSubjectSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String title, String contract, Map<String, Object> branchNavigation, String role, String summary, List<SurfaceAction> actions) {
    return envelope(surfaceId, "list-search", title, actor, correlationId,
        mapOf("surfaceContract", contract, "branchNavigation", branchNavigation, "query", "", "rows", List.of(), "filters", mapOf("role", role, "backendAuthored", true), "pageInfo", mapOf("visibleCount", 0), "summary", summary, "redaction", List.of("hidden-users-redacted", "provider-payload-redacted", "raw-invitation-token-redacted"), "emptyMessage", "No " + title + " are visible for this selected target yet."), actions);
  }

  private SurfaceEnvelope roleScopedInvitationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String title, String contract, Map<String, Object> branchNavigation, String role, String summary, List<SurfaceAction> actions) {
    return envelope(surfaceId, "create-form", title, actor, correlationId,
        mapOf("surfaceContract", contract, "branchNavigation", branchNavigation, "summary", summary, "draft", mapOf("email", "", "displayName", "", "roles", List.of(role)), "roleOptions", List.of(mapOf("roleId", role, "label", role.replace('_', ' '))), "policyOptions", mapOf("roles", List.of(mapOf("roleId", role, "label", role.replace('_', ' '))), "idempotency", "client-generated", "outboxReadiness", "backend-derived"), "idempotencyKeyHint", "client-generated", "redaction", List.of("raw-token-redacted", "provider-payload-redacted", "hidden-scope-redacted"), "traceRefs", List.of("trace-" + surfaceId + "-" + stableSuffix(correlationId))), actions);
  }

  private SurfaceEnvelope scopedAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String title, String contract, Map<String, Object> branchNavigation, String summary, List<SurfaceAction> actions) {
    return envelope(surfaceId, "show-inspection", title, actor, correlationId,
        mapOf("surfaceContract", contract, "branchNavigation", branchNavigation, "recordId", stringInput(null, "recordId", surfaceId), "recordLabel", title, "recordKind", "admin-scope", "summary", summary, "fields", List.of(mapOf("fieldId", "selectedContext", "label", "Selected context", "value", actor.selectedContext().membershipId(), "editable", false, "inputType", "text"), mapOf("fieldId", "scope", "label", "Scope", "value", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text")), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Inspection only; consequential work opens dedicated backend-authorized task surfaces."), "traceRefs", List.of("trace-" + surfaceId + "-" + stableSuffix(correlationId)), "redaction", List.of("hidden-targets-redacted", "provider-payload-redacted")), actions);
  }

  private SurfaceEnvelope customerTaskSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String title, String contract, String summary, List<SurfaceAction> actions) {
    return customerTaskSurface(actor, correlationId, surfaceId, title, contract, summary, actions, null);
  }

  private SurfaceEnvelope customerTaskSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String surfaceId, String title, String contract, String summary, List<SurfaceAction> actions, ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail detail) {
    var draftName = detail == null ? "" : detail.customer().customerName();
    var data = mapOf("surfaceContract", contract, "branchNavigation", customerBranchNavigation(correlationId), "summary", summary, "recordKind", "customer", "recordId", detail == null ? "" : detail.customer().customerId(), "recordLabel", detail == null ? title : detail.customer().customerName(), "draft", mapOf("customerName", draftName, "reason", ""), "reasonRequired", surfaceId.contains("suspend"), "confirmationRequired", surfaceId.contains("suspend") || surfaceId.contains("reactivate"), "idempotencyKeyHint", "client-generated", "traceRefs", detail == null ? List.of("trace-" + surfaceId + "-" + stableSuffix(correlationId)) : detail.traceRefs(), "redaction", List.of("sibling-customers-redacted", "tenant-app-data-redacted", "provider-secrets-redacted"));
    if (detail != null) data.put("customerDetail", customerDetailMap(detail));
    return envelope(surfaceId, surfaceId.contains("suspend") ? "destructive-lifecycle-confirmation" : surfaceId.contains("reactivate") ? "lifecycle-confirmation" : surfaceId.contains("rename") ? "edit-form" : "create-form", title, actor, correlationId, data, actions);
  }

  private ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerActionResult runCustomerLifecycleAction(AuthContextResolver.ResolvedMe actor, String actionId, Object input, String idempotencyKey, String correlationId) {
    var service = StarterSecurityComponents.tenantCustomerAdminService();
    return switch (actionId) {
      case "action-customer-create" -> service.createCustomer(actor, stringInput(input, "customerName", ""), idempotencyKey, stringInput(input, "reason", "customer-created"), correlationId);
      case "action-customer-rename" -> service.renameCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), stringInput(input, "customerName", ""), idempotencyKey, stringInput(input, "reason", "customer-renamed"), correlationId);
      case "action-customer-suspend" -> service.suspendCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), stringInput(input, "reason", "customer-suspended"), idempotencyKey, correlationId);
      case "action-customer-reactivate" -> service.reactivateCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), stringInput(input, "reason", "customer-reactivated"), idempotencyKey, correlationId);
      default -> throw new AuthorizationException(404, "target-not-found-or-forbidden");
    };
  }

  private Map<String, Object> customerRowMap(ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerSummary customer) {
    return mapOf("id", customer.customerId(), "customerId", customer.customerId(), "customerName", customer.customerName(), "status", customer.status(), "rowType", "customer", "targetObjectType", "customer", "targetSurfaceId", "surface-user-admin-customer-detail", "targetSurfaceType", "show-inspection", "openActionId", "action-customer-read", "safeLifecycleSummary", customer.status().equals("active") ? "Active Customer boundary" : "Suspended Customer boundary", "traceRefs", customer.traceRefs(), "redactionState", "visible");
  }

  private Map<String, Object> customerDetailMap(ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail detail) {
    return mapOf("customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "status", detail.customer().status(), "safeBoundaryNotice", detail.safeBoundaryNotice(), "visibleActions", detail.visibleActions(), "recentAuditEvents", detail.recentAuditEvents(), "traceRefs", detail.traceRefs(), "correlationId", detail.correlationId());
  }

  private List<SurfaceAction> withOrganizationBranchReturn(List<SurfaceAction> actions) {
    var result = new ArrayList<SurfaceAction>();
    result.add(showOrganizationsAction());
    result.addAll(actions);
    return result;
  }

  private List<SurfaceAction> withSaasOwnerAdminBranchReturn(List<SurfaceAction> actions) {
    var result = new ArrayList<SurfaceAction>();
    result.add(showSaasOwnerAdminsAction());
    result.addAll(actions);
    return result;
  }

  private List<SurfaceAction> withCustomerBranchReturn(List<SurfaceAction> actions) {
    var result = new ArrayList<SurfaceAction>();
    result.add(showCustomersAction());
    result.addAll(actions);
    return result;
  }

  private List<SurfaceAction> withUserBranchReturn(List<SurfaceAction> actions) {
    var result = new ArrayList<SurfaceAction>();
    result.add(showUsersAction());
    result.addAll(actions);
    return result;
  }

  private Map<String, Object> organizationBranchNavigation(String correlationId) {
    return mapOf("branchRootSurfaceId", "surface-user-admin-organization-directory", "branchReturnActionId", "action-user-admin-show-organizations", "branchReturnLabel", "Back to organizations", "browserToolId", "user-admin.show-organizations", "governedToolId", "manage-organizations", "capabilityId", SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, "safeFilterPreservation", "backend-authored-only", "traceRefs", List.of("trace-organization-branch-return-" + stableSuffix(correlationId)), "correlationId", correlationId);
  }

  private Map<String, Object> saasOwnerAdminBranchNavigation(String correlationId) {
    return mapOf("branchRootSurfaceId", "surface-user-admin-saas-owner-admins", "branchReturnActionId", "action-user-admin-show-saas-owner-admins", "branchReturnLabel", "Back to SaaS Owner Admins", "browserToolId", "user-admin.show-saas-owner-admins", "governedToolId", "manage-saas-owner-admins", "capabilityId", SAAS_OWNER_ADMIN_LIST_CAPABILITY, "safeFilterPreservation", "backend-authored-only", "traceRefs", List.of("trace-saas-owner-admin-branch-return-" + stableSuffix(correlationId)), "correlationId", correlationId);
  }

  private Map<String, Object> customerBranchNavigation(String correlationId) {
    return mapOf("branchRootSurfaceId", "surface-user-admin-customer-directory", "branchReturnActionId", "action-user-admin-show-customers", "branchReturnLabel", "Back to customers", "browserToolId", "user-admin.show-customers", "governedToolId", "manage-customers", "capabilityId", TENANT_CUSTOMER_LIST_CAPABILITY, "safeFilterPreservation", "backend-authored-only", "traceRefs", List.of("trace-customer-branch-return-" + stableSuffix(correlationId)), "correlationId", correlationId);
  }

  private Map<String, Object> userBranchNavigation(String correlationId) {
    return mapOf("branchRootSurfaceId", "surface-user-admin-users", "branchReturnActionId", "action-user-admin-show-users", "branchReturnLabel", "Show users", "browserToolId", "user-admin.show-users", "governedToolId", "search-user-directory", "capabilityId", USERADMIN_LIST_MEMBERS, "safeFilterPreservation", "backend-authored-only", "traceRefs", List.of("trace-user-branch-return-" + stableSuffix(correlationId)), "correlationId", correlationId);
  }

  private Map<String, Object> navigationActionMap(SurfaceAction action) {
    return mapOf("actionId", action.actionId(), "label", action.label(), "capabilityId", action.capabilityId(), "browserToolId", action.browserToolId(), "governedToolId", action.governedToolId(), "targetSurfaceId", action.resultSurface() == null ? null : action.resultSurface().updateSurfaceId());
  }

  private Map<String, Object> authorizedActionMap(SurfaceAction action) {
    return mapOf("actionId", action.actionId(), "label", action.label(), "capabilityId", action.capabilityId(), "browserToolId", action.browserToolId(), "governedToolId", action.governedToolId(), "administeredPopulationType", administeredPopulationTypeForAction(action), "attentionType", attentionTypeForAction(action), "requiresIdempotency", action.idempotency().required(), "idempotencyKeySource", action.idempotency().keySource(), "resultSurfaceId", action.resultSurface() == null ? null : action.resultSurface().updateSurfaceId(), "approvalRequired", action.requiresApproval(), "nextStepGuidance", action.requiresApproval() ? "Opens a dedicated approval or confirmation surface before any state changes." : "Opens or refreshes the backend-authorized surface for this selected context.");
  }

  private String administeredPopulationTypeForAction(SurfaceAction action) {
    return switch (action.actionId()) {
      case "action-user-admin-show-saas-owner-admins", "action-open-saas-owner-admin-invitation-create" -> "app_admin_users";
      case "action-user-admin-show-organizations", "action-display-organization-admin" -> "organizations";
      case "action-user-admin-show-customers", "action-open-customer-create" -> "customers";
      default -> "visible_user_admin_population";
    };
  }

  private String attentionTypeForAction(SurfaceAction action) {
    if (action.resultSurface() == null || action.resultSurface().updateSurfaceId() == null) return null;
    return switch (action.resultSurface().updateSurfaceId()) {
      case "surface-user-admin-invitation-create", "surface-user-admin-invitation-detail" -> "invitation_lifecycle";
      case "surface-user-admin-access-review-task" -> "access_review";
      case "surface-audit-trace-dashboard" -> "admin_audit";
      default -> null;
    };
  }

  private List<Map<String, Object>> userAdminAttentionCounts(long pendingInvites, long failedInvites, long expiringSupport, long deniedAdminActions, String correlationId) {
    return List.of(
        attentionCount("pending_invitations", "Pending invitations", pendingInvites, pendingInvites == 0 ? "info" : "warning", "surface-user-admin-users", "action-display-user-list", USERADMIN_LIST_INVITATIONS, correlationId),
        attentionCount("failed_invitation_delivery", "Failed invitation delivery", failedInvites, failedInvites == 0 ? "info" : "warning", "surface-user-admin-users", "action-display-user-list", USERADMIN_LIST_INVITATIONS, correlationId),
        attentionCount("support_access_expiring", "Expiring support access", expiringSupport, expiringSupport == 0 ? "info" : "warning", "surface-user-admin-users", "action-display-user-list", USERADMIN_SUPPORT_ACCESS_READ, correlationId),
        attentionCount("recent_denied_admin_actions", "Recent denied admin actions", deniedAdminActions, "info", "surface-audit-trace-dashboard", "action-open-audit-trace", AUDIT_TRACE_READ_CAPABILITY, correlationId),
        attentionCount("access_review_blocked_runtime", "Access review runtime readiness", 0, "blocked_provider_or_runtime", "surface-user-admin-access-review-task", "action-useradmin-read-access-review", USERADMIN_ACCESS_REVIEW_READ, correlationId));
  }

  private Map<String, Object> attentionCount(String type, String label, long count, String severity, String targetSurfaceId, String openActionId, String capabilityId, String correlationId) {
    return mapOf("attentionType", type, "label", label, "count", count, "severity", severity, "statusText", count == 0 ? "No visible items need attention." : count + " visible item(s) need review.", "targetSurfaceId", targetSurfaceId, "openActionId", openActionId, "filter", mapOf("attentionType", type), "sourceCapabilityId", capabilityId, "traceRefs", List.of("trace-useradmin-attention-" + type + "-" + stableSuffix(correlationId)), "redactionState", "visible-scope-only");
  }

  private List<Map<String, Object>> userAdminAdministeredPopulations(UserAdminLayer layer, List<UserDirectoryView.UserDirectoryRow> users, List<InvitationView.InvitationRow> invites, String correlationId) {
    var populations = new ArrayList<Map<String, Object>>();
    populations.add(mapOf("populationType", layer.saasOwner() ? "app_admin_users" : "tenant_employees", "label", layer.activeUsersLabel(), "visibleCount", users.size(), "attentionCount", invites.stream().filter(WorkstreamService::isPendingInvitation).count(), "activeCount", users.stream().filter(user -> user.membershipStatus() == MembershipStatus.ACTIVE).count(), "pendingInvitationCount", invites.stream().filter(WorkstreamService::isPendingInvitation).count(), "suspendedOrDisabledCount", users.stream().filter(user -> user.membershipStatus() != MembershipStatus.ACTIVE).count(), "staleOrExpiredCount", invites.stream().filter(invite -> invite.status() == InvitationStatus.EXPIRED).count(), "reviewCount", 0, "roleCoverageSummary", "Backend-authored visible roles only", "targetSurfaceId", "surface-user-admin-users", "openActionId", "action-display-user-list", "capabilityIds", List.of(USERADMIN_LIST_MEMBERS, layer.readCapability()), "traceRefs", List.of("trace-useradmin-population-" + stableSuffix(correlationId))));
    if (layer.saasOwner()) {
      populations.add(mapOf("populationType", "organizations", "label", "Organizations", "visibleCount", 0, "attentionCount", 0, "roleCoverageSummary", "Open the Organization Directory for backend-authorized counts.", "targetSurfaceId", "surface-user-admin-organization-directory", "openActionId", "action-user-admin-show-organizations", "capabilityIds", List.of(SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY), "traceRefs", List.of("trace-useradmin-population-organizations-" + stableSuffix(correlationId))));
      populations.add(mapOf("populationType", "organization_admin_users", "label", "Organization Admins", "visibleCount", 0, "attentionCount", 0, "roleCoverageSummary", "Open an Organization detail to bootstrap or manage Organization Admins.", "targetSurfaceId", "surface-user-admin-organization-admins", "openActionId", "action-user-admin-show-organizations", "capabilityIds", List.of(SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY), "traceRefs", List.of("trace-useradmin-population-organization-admins-" + stableSuffix(correlationId))));
    }
    return populations;
  }

  private SurfaceEnvelope listSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return listSurface(actor, null, correlationId);
  }

  private SurfaceEnvelope listSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_MEMBERS, "user_admin.users.v1", correlationId);
    var rows = new ArrayList<Map<String, Object>>();
    var users = userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var invites = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    for (var user : users) rows.add(userDirectoryRow(user));
    for (var invite : invites) rows.add(invitationRow(invite));
    var query = stringInput(input, "query", "").trim();
    var visibleRows = query.isBlank() ? rows : rows.stream().filter(row -> rowMatchesQuery(row, query)).toList();
    return envelope("surface-user-admin-users", "list-search", "Users", actor, correlationId, mapOf("surfaceContract", "user_admin.users.v1", "branchNavigation", userBranchNavigation(correlationId), "query", query, "rows", visibleRows, "filters", mapOf("query", query, "backendAuthored", true, "selectedScopeOnly", true), "pageInfo", mapOf("activeUserCount", users.size(), "invitationCount", invites.size(), "totalKnownCount", rows.size(), "visibleCount", visibleRows.size()), "createAction", mapOf("actionId", "action-open-useradmin-invitation-create", "targetSurfaceId", "surface-user-admin-invitation-create", "capabilityId", USERADMIN_SEND_INVITATION), "redaction", List.of("hidden-users-redacted", "cross-scope-counts-redacted"), "diagnosticMetadata", mapOf("visibility", "role-gated", "traceRefs", List.of("trace-surface-user-admin-users")), "emptyMessage", query.isBlank() ? "No active users or invitations are visible in this scope." : "No visible users or invitations matched this backend-authorized search."), List.of(showUsersAction(), displayDetailAction(), displayInvitationDetailAction(), openInvitationCreateAction(), openAuditAction()));
  }

  private boolean rowMatchesQuery(Map<String, Object> row, String query) {
    var normalized = query.toLowerCase(Locale.ROOT);
    return row.entrySet().stream()
        .filter(entry -> List.of("accountId", "membershipId", "invitationId", "email", "displayName", "role", "status", "delivery", "targetObjectType", "rowType").contains(entry.getKey()))
        .map(entry -> Objects.toString(entry.getValue(), "").toLowerCase(Locale.ROOT))
        .anyMatch(value -> value.contains(normalized));
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
    return envelope("surface-user-admin-user-detail", "show-inspection", "User detail", actor, correlationId, mapOf("surfaceContract", "user_admin.user_detail.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", target.accountId(), "recordLabel", target.displayName(), "recordKind", "account", "summary", "Inspect this user's browser-safe account, membership, role, support-access, access-review, identity, and audit state; consequential changes open dedicated task surfaces.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Name", "value", target.displayName(), "editable", false, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", target.accountId(), "editable", false, "inputType", "email", "disabledReason", "Email is managed by identity provider."), mapOf("fieldId", "membershipStatus", "label", "Status", "value", target.membershipStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleValue, "editable", false, "inputType", "text")), "version", 1, "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId()), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Detail is inspection-only; backend task surfaces reauthorize every consequential change.", "authoritativeCapabilityId", USERADMIN_LIST_MEMBERS), "taskEntryPoints", List.of(mapOf("label", "Change membership or account status", "actionId", "action-open-useradmin-membership-status-confirmation", "targetSurfaceId", "surface-user-admin-membership-status-confirmation"), mapOf("label", "Preview role change", "actionId", "action-useradmin-preview-role-change", "targetSurfaceId", "surface-user-admin-role-change-preview"), mapOf("label", "Grant or extend support access", "actionId", "action-open-useradmin-support-access-grant", "targetSurfaceId", "surface-user-admin-support-access-grant"), mapOf("label", "Revoke support access", "actionId", "action-open-useradmin-support-access-revoke-confirmation", "targetSurfaceId", "surface-user-admin-support-access-revoke-confirmation"), mapOf("label", "Review identity exception", "actionId", "action-open-useradmin-identity-exception-review", "targetSurfaceId", "surface-user-admin-identity-exception-review"), mapOf("label", "Start or open access review", "actionId", "action-useradmin-read-access-review", "targetSurfaceId", "surface-user-admin-access-review-task")), "accessManagement", mapOf("advisoryNotice", "Frontend controls are advisory only; backend UserAdminService remains authoritative for membership, account, role, support-access, access-review, and audit actions.", "memberStatus", mapOf("accountStatus", "active", "membershipStatus", target.membershipStatus().name().toLowerCase(Locale.ROOT), "statusTaskSurfaceId", "surface-user-admin-membership-status-confirmation", "denialHints", List.of("disabled actor", "inactive membership", "cross-tenant", "CUSTOMER_ADMIN_TENANT_ACTION_DENIED", "SAAS_OWNER_NO_SUPPORT_ACCESS", "last-admin"), "noOpMessage", "Repeated status changes return no-op/idempotent evidence where state already matches.", "idempotencyKeySource", "client-generated", "traceLinks", List.of("trace-useradmin-status-action", traceId)), "supportAccess", mapOf("supportAccess", target.supportAccess(), "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "grantTaskSurfaceId", "surface-user-admin-support-access-grant", "revokeTaskSurfaceId", "surface-user-admin-support-access-revoke-confirmation", "denialHints", List.of("tenant-created support grant required", "SAAS_OWNER_NO_SUPPORT_ACCESS", "support-access-expired", "missing-capability"), "traceLinks", List.of("trace-useradmin-support-access"))), "audit", mapOf("lastEventType", "UserAdminDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of(traceId, "trace-useradmin-support-access"))), withUserBranchReturn(List.of(openMembershipStatusConfirmationAction(), openSupportAccessGrantAction(), openSupportAccessRevokeConfirmationAction(), openIdentityExceptionReviewAction(), previewRoleChangeAction(), readAccessReviewAction(), openAuditAction())));
  }

  private SurfaceEnvelope invitationDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_INVITATIONS, "user_admin.invitation_detail.v1", correlationId);
    var invitationId = stringInput(input, "invitationId", latestInvitationId(actor));
    var invite = invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(row -> invitationId.equals(row.invitationId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
    return envelope("surface-user-admin-invitation-detail", "show-inspection", "Invitation detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_detail.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", invite.invitationId(), "recordLabel", invite.targetEmail(), "recordKind", "invitation", "status", invitationSurfaceStatus(invite), "summary", "Inspect this invitation lifecycle and provider-backed delivery state; resend and revoke open dedicated confirmation surfaces before backend commands run.", "actionContext", mapOf("invitationId", invite.invitationId()), "taskEntryPoints", List.of(mapOf("label", "Open resend confirmation", "actionId", "action-open-useradmin-invitation-resend-confirmation", "targetSurfaceId", "surface-user-admin-invitation-resend-confirmation"), mapOf("label", "Open revoke confirmation", "actionId", "action-open-useradmin-invitation-revoke-confirmation", "targetSurfaceId", "surface-user-admin-invitation-revoke-confirmation")), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Invitation detail is inspection-only and never resends or revokes inline."), "fields", List.of(mapOf("fieldId", "email", "label", "Email", "value", invite.targetEmail(), "editable", false, "inputType", "email"), mapOf("fieldId", "status", "label", "Status", "value", invite.status().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleLabels(invite.requestedRoles()), "editable", false, "inputType", "text"), mapOf("fieldId", "delivery", "label", "Delivery", "value", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "deliveryAttempts", "label", "Delivery attempts", "value", String.valueOf(invite.deliveryAttempts()), "editable", false, "inputType", "text"), mapOf("fieldId", "expiresAt", "label", "Expires", "value", invite.expiresAt().toString(), "editable", false, "inputType", "text")), "deliveryState", invitationDeliveryState(invite, correlationId), "recoverySteps", invitationRecoverySteps(invite), "systemStates", invitationSystemStates(invite), "noFakeSuccess", invite.deliveryStatus() == EmailDeliveryStatus.FAILED, "providerBlockedSystemMessage", invite.deliveryStatus() == EmailDeliveryStatus.FAILED ? mapOf("surfaceContract", "user_admin.system_message.v1", "status", "blocked_provider_or_runtime", "safeReasonCode", firstNonBlank(invite.lastDeliveryErrorSummary(), "provider-or-outbox-delivery-failed"), "message", "Invitation delivery failed closed; use the resend confirmation task only after backend provider/outbox readiness is restored.") : null, "audit", mapOf("lastEventType", "InvitationDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of("trace-useradmin-invitation-" + stableSuffix(invite.invitationId())))),
        withUserBranchReturn(List.of(openInvitationResendConfirmationAction(), openInvitationRevokeConfirmationAction(), openAuditAction())));
  }


  private SurfaceEnvelope roleChangePreviewSurface(AuthContextResolver.ResolvedMe actor, UserAdminService.RoleChangePreview preview, String correlationId) {
    return envelope("surface-user-admin-role-change-preview", "decision-card", "Role change preview", actor, correlationId,
        mapOf("surfaceContract", "user_admin.role_change_preview.v1", "branchNavigation", userBranchNavigation(correlationId), "status", preview.allowed() ? preview.noOp() ? "no-op" : "ready" : "denied", "message", preview.message(), "capabilityDelta", preview.capabilityDelta(), "affectedWorkstreams", preview.affectedWorkstreams(), "policyHints", preview.policyHints(), "lastAdminImpact", preview.lastAdminImpact(), "traceLinks", List.of(preview.traceId()), "systemStates", List.of("system_message", "last-admin", "self-disable", "idempotency")),
        withUserBranchReturn(List.of(changeMemberRolesAction(), traceAction())));
  }

  private SurfaceEnvelope invitationCreateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_SEND_INVITATION, "user_admin.invitation_create.v1", correlationId);
    return envelope("surface-user-admin-invitation-create", "create-form", "Invite user", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_create.v1", "branchNavigation", userBranchNavigation(correlationId), "status", "ready", "summary", "Create a scoped invitation through the backend InvitationService. Submission requires a client idempotency key and returns invitation detail or a safe validation/denial result.", "targetScope", mapOf("scopeType", actor.selectedContext().scopeType().name(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId()), "draft", mapOf("email", stringInput(input, "email", ""), "displayName", stringInput(input, "displayName", ""), "roles", rolesInput(input).stream().map(FoundationRole::name).toList()), "roleOptions", roleOptionsForSelectedContext(actor), "policyOptions", mapOf("expiryOptions", invitationExpiryOptions(), "idempotency", "client-generated", "outboxReadiness", "backend-derived"), "validationMessages", List.of(), "idempotencyKeyHint", "client-generated", "outboxBoundary", "Invitation email delivery is queued through the backend outbox/provider path; tokens and provider payloads are never exposed.", "traceRefs", List.of("trace-useradmin-invitation-create-" + stableSuffix(correlationId)), "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "raw-jwt-redacted")),
        withUserBranchReturn(List.of(inviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope invitationResendConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var invite = invitationForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_RESEND_INVITATION, "user_admin.invitation_resend_confirmation.v1", correlationId);
    return envelope("surface-user-admin-invitation-resend-confirmation", "lifecycle-confirmation", "Resend invitation", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_resend_confirmation.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", invite.invitationId(), "recordLabel", invite.targetEmail(), "recordKind", "invitation", "status", invite.canResend() ? "ready" : "no-op", "confirmationCopy", invite.canResend() ? "Resend a scoped invitation email through the governed backend outbox." : "This invitation is not eligible for resend in the selected context.", "actionContext", mapOf("invitationId", invite.invitationId()), "reasonRequired", false, "idempotencyKeyHint", "client-generated", "delivery", invitationDeliveryState(invite, correlationId), "deliveryState", invitationDeliveryState(invite, correlationId), "recoverySteps", invitationRecoverySteps(invite), "systemStates", invitationSystemStates(invite), "traceRefs", List.of("trace-useradmin-invitation-resend-confirmation-" + stableSuffix(invite.invitationId() + correlationId)), "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "email-body-redacted", "provider-secret-redacted")),
        withUserBranchReturn(List.of(resendInvitationAction(), displayInvitationDetailAction(), openAuditAction())));
  }

  private SurfaceEnvelope invitationRevokeConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var invite = invitationForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_REVOKE_INVITATION, "user_admin.invitation_revoke_confirmation.v1", correlationId);
    return envelope("surface-user-admin-invitation-revoke-confirmation", "destructive-lifecycle-confirmation", "Revoke invitation", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_revoke_confirmation.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", invite.invitationId(), "recordLabel", invite.targetEmail(), "recordKind", "invitation", "status", invite.canRevoke() ? "ready" : "no-op", "consequenceCopy", invite.canRevoke() ? "Revoking prevents this invitation from being accepted. Existing accounts or memberships are not changed by this action." : "This invitation cannot be revoked from its current lifecycle state.", "actionContext", mapOf("invitationId", invite.invitationId()), "reasonRequired", true, "idempotencyKeyHint", "client-generated", "deliveryState", invitationDeliveryState(invite, correlationId), "recoverySteps", invitationRecoverySteps(invite), "systemStates", invitationSystemStates(invite), "traceRefs", List.of("trace-useradmin-invitation-revoke-confirmation-" + stableSuffix(invite.invitationId() + correlationId)), "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "raw-jwt-redacted")),
        withUserBranchReturn(List.of(revokeInvitationAction(), displayInvitationDetailAction(), openAuditAction())));
  }

  private SurfaceEnvelope membershipStatusConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var target = userForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_UPDATE_MEMBER_STATUS, "user_admin.membership_status_confirmation.v1", correlationId);
    return envelope("surface-user-admin-membership-status-confirmation", "destructive-lifecycle-confirmation", "Confirm membership status", actor, correlationId,
        mapOf("surfaceContract", "user_admin.membership_status_confirmation.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", target.membershipId(), "recordLabel", target.displayName(), "recordKind", "membership", "currentStatus", target.membershipStatus().name().toLowerCase(Locale.ROOT), "proposedStatus", stringInput(input, "status", "removed"), "statusOptions", membershipStatusOptions(), "consequenceCopy", "Membership/account lifecycle changes are reauthorized by UserAdminService and enforce self-action, last-admin, scope, idempotency, and audit guardrails before changing state.", "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId()), "reasonRequired", true, "confirmationRequired", true, "idempotencyKeyHint", "client-generated", "denialHints", List.of("self-disable-denied", "last-admin-denied", "scope-forbidden", "idempotency-key-required"), "traceRefs", List.of("trace-useradmin-membership-status-confirmation-" + stableSuffix(target.membershipId() + correlationId)), "correlationId", correlationId, "redaction", List.of("cross-scope-users-redacted", "raw-jwt-redacted")),
        withUserBranchReturn(List.of(updateMemberStatusAction(), reactivateMemberStatusAction(), disableAccountAction(), reactivateAccountAction(), displayDetailAction(), openAuditAction())));
  }

  private SurfaceEnvelope supportAccessGrantSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var target = userForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_SUPPORT_ACCESS_GRANT, "user_admin.support_access_grant.v1", correlationId);
    return envelope("surface-user-admin-support-access-grant", "create-form", "Grant support access", actor, correlationId,
        mapOf("surfaceContract", "user_admin.support_access_grant.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", target.membershipId(), "recordLabel", target.displayName(), "recordKind", "support-access", "currentSupportAccess", target.supportAccess(), "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "summary", "Grant or extend time-boxed support access through backend UserAdminService; SaaS Owner support access remains denied without explicit tenant-created support grant authority.", "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId()), "purposeRequired", true, "supportExpiryOptions", supportExpiryOptions(), "purposeOptions", List.of("incident-response", "customer-requested-support", "access-review-remediation"), "policyOptions", mapOf("maxDurationHours", 8, "requiresReason", true, "requiresBackendApproval", true), "idempotencyKeyHint", "client-generated", "approverPolicy", "backend-authorized selected AuthContext and support-access capability", "traceRefs", List.of("trace-useradmin-support-access-grant-surface-" + stableSuffix(target.membershipId() + correlationId)), "correlationId", correlationId, "redaction", List.of("support-provider-internals-redacted", "raw-jwt-redacted")),
        withUserBranchReturn(List.of(grantSupportAccessAction(), extendSupportAccessAction(), displayDetailAction(), openAuditAction())));
  }

  private SurfaceEnvelope supportAccessRevokeConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var target = userForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_SUPPORT_ACCESS_REVOKE, "user_admin.support_access_revoke_confirmation.v1", correlationId);
    return envelope("surface-user-admin-support-access-revoke-confirmation", "destructive-lifecycle-confirmation", "Revoke support access", actor, correlationId,
        mapOf("surfaceContract", "user_admin.support_access_revoke_confirmation.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", target.membershipId(), "recordLabel", target.displayName(), "recordKind", "support-access", "currentSupportAccess", target.supportAccess(), "consequenceCopy", "Revoking support access removes the time-boxed support grant only; it does not change ordinary membership, roles, or tenant/customer data.", "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId()), "reasonRequired", true, "confirmationRequired", true, "idempotencyKeyHint", "client-generated", "traceRefs", List.of("trace-useradmin-support-access-revoke-surface-" + stableSuffix(target.membershipId() + correlationId)), "correlationId", correlationId, "redaction", List.of("support-provider-internals-redacted", "raw-jwt-redacted")),
        withUserBranchReturn(List.of(revokeSupportAccessAction(), displayDetailAction(), openAuditAction())));
  }

  private SurfaceEnvelope identityExceptionReviewSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var target = userForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_IDENTITY_RELINK_READ, "user_admin.identity_exception_review.v1", correlationId);
    UserAdminService.IdentityRelinkResult recovery = null;
    try {
      recovery = userAdminService.readIdentityRelink(actor, stringInput(input, "recoveryId", target.accountId()), correlationId);
    } catch (AuthorizationException missing) {
      if (!String.valueOf(missing.getMessage()).contains("identity-recovery-not-found-or-forbidden")) throw missing;
    }
    var lifecycleStatus = recovery == null ? "not_started" : recovery.lifecycleStatus();
    var status = switch (lifecycleStatus) {
      case "needs-review" -> "approval-required";
      case "approved-for-recovery" -> "waiting-for-completion";
      case "completed" -> "completed";
      case "denied" -> "denied";
      default -> "request-required";
    };
    var recoveryId = recovery == null ? null : recovery.recoveryId();
    var traceRefs = recovery == null ? List.of("trace-useradmin-identity-exception-review-" + stableSuffix(target.accountId() + correlationId)) : List.of(recovery.traceId());
    var evidenceRefs = recovery == null ? List.of("account:" + target.accountId(), "membership:" + target.membershipId(), "provider-boundary:redacted") : recovery.evidenceRefs();
    return envelope("surface-user-admin-identity-exception-review", "decision-card", "Identity exception review", actor, correlationId,
        mapOf("surfaceContract", "user_admin.identity_exception_review.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", target.accountId(), "recordLabel", target.displayName(), "recordKind", "identity-exception", "recoveryId", recoveryId, "lifecycleStatus", lifecycleStatus, "status", status, "summary", recovery == null ? "No durable recovery is open yet. Request recovery to create backend lifecycle state before review, approval, denial, or completion." : recovery.message(), "risk", "provider-boundary", "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId(), "recoveryId", recoveryId == null ? "" : recoveryId, "identityLinkState", "browser-safe"), "workflowStatus", mapOf("lifecycleStatus", lifecycleStatus, "reviewDecision", status, "recoveryBlockedUntilApproved", recovery == null || lifecycleStatus.equals("needs-review"), "completionAllowedAfterApproval", lifecycleStatus.equals("approved-for-recovery")), "evidenceRefs", evidenceRefs, "recoveryOptions", List.of("request", "approve", "deny", "complete", "return-to-user-detail", "open-audit-evidence"), "noDirectMutation", true, "providerBoundary", "WorkOS subject details, raw JWTs, and provider payloads are redacted from the browser surface.", "traceRefs", traceRefs, "correlationId", correlationId, "redaction", recovery == null ? List.of("workos-subject-redacted", "raw-jwt-redacted", "provider-payload-redacted") : recovery.redactions()),
        withUserBranchReturn(List.of(requestIdentityRelinkAction(), readIdentityRelinkAction(), approveIdentityRelinkAction(), denyIdentityRelinkAction(), completeIdentityRelinkAction(), displayDetailAction(), openAuditAction(), traceAction())));
  }

  private UserDirectoryView.UserDirectoryRow userForTaskSurface(AuthContextResolver.ResolvedMe actor, Object input) {
    var accountId = stringInput(input, "accountId", actor.account().accountId());
    var membershipId = stringInput(input, "membershipId", actor.selectedContext().membershipId());
    return userDirectoryView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(user -> Objects.equals(user.accountId(), accountId) || Objects.equals(user.membershipId(), membershipId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "user-not-found-or-forbidden"));
  }

  private InvitationView.InvitationRow invitationForTaskSurface(AuthContextResolver.ResolvedMe actor, Object input) {
    var invitationId = stringInput(input, "invitationId", latestInvitationId(actor));
    return invitationView.list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(row -> invitationId.equals(row.invitationId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
  }

  private CapabilityActionResult openUserAdminTaskSurface(AuthContextResolver.ResolvedMe actor, String actionId, Object input, String correlationId) {
    try {
      var surface = switch (actionId) {
        case "action-open-useradmin-invitation-create" -> invitationCreateSurface(actor, input, correlationId);
        case "action-open-useradmin-invitation-resend-confirmation" -> invitationResendConfirmationSurface(actor, input, correlationId);
        case "action-open-useradmin-invitation-revoke-confirmation" -> invitationRevokeConfirmationSurface(actor, input, correlationId);
        case "action-open-useradmin-membership-status-confirmation" -> membershipStatusConfirmationSurface(actor, input, correlationId);
        case "action-open-useradmin-support-access-grant" -> supportAccessGrantSurface(actor, input, correlationId);
        case "action-open-useradmin-support-access-revoke-confirmation" -> supportAccessRevokeConfirmationSurface(actor, input, correlationId);
        case "action-open-useradmin-identity-exception-review" -> identityExceptionReviewSurface(actor, input, correlationId);
        default -> null;
      };
      if (surface == null) return null;
      return new CapabilityActionResult("accepted", surface.title() + " opened through backend-authoritative User Admin surface graph.", correlationId, surface.traceIds(), surface);
    } catch (AuthorizationException denied) {
      return new CapabilityActionResult("denied", "User Admin task surface is unavailable for the selected context or target.", correlationId, List.of("trace-useradmin-task-open-denied-" + stableSuffix(correlationId)), shellSystemMessageSurface(actor, USER_ADMIN_AGENT_ID, denied.getMessage(), "User Admin task surface is unavailable for the selected context or target.", correlationId));
    }
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
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction()));
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
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceTimelineAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction()));
  }

  private SurfaceEnvelope auditTraceInvestigationGuideSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.investigationGuide(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSummaryTaskBlockedAction()));
  }

  private SurfaceEnvelope auditTraceExportRequestSurface(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    var surface = auditTraceService.requestRedactedExport(actor, input, idempotencyKey, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction()));
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
    var status = accessReviewStatus(task);
    var reviewState = accessReviewResultReviewState(task);
    var blockers = task.blockerCode() == null ? List.of() : List.of(mapOf("code", task.blockerCode(), "message", "Governed AutonomousAgent provider/runtime is not configured; the starter fails closed instead of returning model-less access-review recommendations."));
    var providerFailures = task.blockerCode() == null ? List.of() : List.of("blocked_provider_or_runtime");
    var traceLinks = accessReviewTraceLinks(task.traceIds(), correlationId, task.blockerCode() != null);
    return envelope("surface-user-admin-access-review-task", "workflow-status", "User Admin access review", actor, correlationId,
        mapOf("surfaceContract", "user_admin.access_review_task.v1", "branchNavigation", userBranchNavigation(correlationId), "workflowId", task.taskId(), "taskId", task.taskId(), "autonomousAgentTaskId", task.autonomousAgentTaskId(), "status", status, "initiatingCapabilityId", USERADMIN_ACCESS_REVIEW_START, "requiredCapabilityId", USERADMIN_ACCESS_REVIEW_READ, "scope", mapOf("scopeType", task.scopeType().name(), "tenantId", task.tenantId(), "customerId", task.customerId()), "progress", mapOf("percent", task.progressPercent(), "summary", task.summary()), "resultSummary", task.status() == AccessReviewTask.Status.COMPLETED || task.status() == AccessReviewTask.Status.ACCEPTED || task.status() == AccessReviewTask.Status.REJECTED ? task.summary() : null, "blockers", blockers, "evidenceRefs", task.evidenceRefs(), "recommendations", task.recommendationRefs(), "resultReviewState", reviewState, "providerFailures", providerFailures, "traceIds", task.traceIds(), "traceLinks", traceLinks, "modelToolDataPolicyUsage", accessReviewUsageSummary(task, traceLinks), "accessReview", mapOf("surfaceContract", "user_admin.access_review_task.v1", "taskId", task.taskId(), "lifecycleState", status, "progressPercent", task.progressPercent(), "blockers", blockers, "evidenceRefs", task.evidenceRefs(), "recommendations", task.recommendationRefs(), "providerFailures", providerFailures, "resultReviewState", reviewState, "noDirectMutation", true, "safety", "Access-review recommendations are advisory; human accept/reject records review evidence only.", "traceIds", task.traceIds(), "traceLinks", traceLinks), "noDirectMutation", true, "safety", "access-review output cannot directly mutate memberships, invitations, roles, capabilities, authorization state, or provider configuration; User Admin human review is required before follow-up changes"),
        withUserBranchReturn(List.of(startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), acceptAccessReviewResultAction(), rejectAccessReviewResultAction(), traceAction(), openAuditAction())));
  }

  private SurfaceEnvelope accessReviewBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_ACCESS_REVIEW_START, "access-review autonomous task unavailable", correlationId);
    var traceIds = List.of("trace-useradmin-access-review-blocked");
    var traceLinks = accessReviewTraceLinks(traceIds, correlationId, true);
    var blockers = List.of(mapOf("code", "blocked_provider_or_runtime", "message", "Configure governed AutonomousAgent runtime, model provider, ToolPermissionBoundary grants, readSkill/readReferenceDoc, and userAdminEvidence.read before model-backed access reviews can complete."));
    return envelope("surface-user-admin-access-review-task", "workflow-status", "User Admin access review", actor, correlationId,
        mapOf("surfaceContract", "user_admin.access_review_task.v1", "branchNavigation", userBranchNavigation(correlationId), "workflowId", "user-admin-access-review", "status", "blocked_provider_or_runtime", "summary", "Start creates a durable task record, but worker execution remains blocked until governed AutonomousAgent provider/runtime configuration is enabled.", "traceIds", traceIds, "traceLinks", traceLinks, "modelToolDataPolicyUsage", accessReviewUsageSummary(null, traceLinks), "requiredCapabilityId", USERADMIN_ACCESS_REVIEW_START, "providerFailures", List.of("blocked_provider_or_runtime"), "blockers", blockers, "resultReviewState", "blocked_provider_or_runtime", "accessReview", mapOf("surfaceContract", "user_admin.access_review_task.v1", "taskId", "user-admin-access-review", "lifecycleState", "blocked_provider_or_runtime", "progressPercent", 0, "blockers", blockers, "evidenceRefs", List.of("userAdminEvidence.read", "readSkill:user-admin-access-review", "readReferenceDoc:user-admin-access-review"), "recommendations", List.of(), "providerFailures", List.of("blocked_provider_or_runtime"), "resultReviewState", "blocked_provider_or_runtime", "noDirectMutation", true, "safety", "No deterministic, fixture, simulated, or model-less access-review success is exposed.", "traceIds", traceIds, "traceLinks", traceLinks), "noDirectMutation", true, "noFakeSuccess", true, "safety", "Access-review output cannot directly mutate access; configure the model-backed runtime before recommendations are available."),
        withUserBranchReturn(List.of(startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), traceAction(), openAuditAction())));
  }

  private List<Map<String, Object>> accessReviewTraceLinks(List<String> traceIds, String correlationId, boolean blocked) {
    var safeTraceIds = traceIds == null || traceIds.isEmpty() ? List.of("trace-useradmin-access-review-" + stableSuffix(correlationId)) : traceIds;
    var links = new ArrayList<Map<String, Object>>();
    for (var traceId : safeTraceIds) links.add(mapOf("traceId", traceId, "category", blocked ? "model-runtime-blocker" : "agent-work", "label", blocked ? "Provider/model/tool-boundary blocked" : "Access-review agent work trace", "summary", blocked ? "Fail-closed provider/runtime readiness, tool-boundary, skill/reference loader, and evidence-access decision summary; prompts and provider secrets are redacted." : "Safe model, tool, data, and policy usage summary for the access-review AutonomousAgent; raw prompts, provider payloads, hidden evidence, and secrets are redacted.", "targetSurfaceId", "surface-audit-trace-detail", "correlationId", correlationId, "redaction", "prompt/provider/tool payloads redacted"));
    return links;
  }

  private Map<String, Object> accessReviewUsageSummary(AccessReviewTask task, List<Map<String, Object>> traceLinks) {
    return mapOf("model", task != null && task.status() == AccessReviewTask.Status.COMPLETED ? "governed Akka AutonomousAgent model response summarized" : "provider/model readiness checked and failed closed when unavailable", "tools", List.of("userAdminEvidence.read", "readSkill:user-admin-access-review", "readReferenceDoc:user-admin-access-review"), "data", "selected tenant/customer scoped user-admin evidence only", "policy", "ToolPermissionBoundary and human-review no-direct-mutation policy", "traceLinks", traceLinks, "redaction", "raw prompts, raw tool payloads, provider credentials, JWTs, hidden tenant/customer evidence, and access secrets are omitted");
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

  private CapabilityActionResult startGovernancePolicyImpactAnalysis(AuthContextResolver.ResolvedMe actor, CapabilityActionRequest request) {
    var proposalId = stringInput(request.input(), "proposalId", null);
    if (proposalId == null || proposalId.isBlank()) {
      return governancePolicySystemMessageResult(actor, request.actionId(), "governance-impact-proposal-required", "Select a tenant-scoped policy proposal before starting impact analysis.", request.correlationId());
    }
    try {
      var task = governancePolicyImpactService.start(actor, new GovernancePolicyImpactService.StartGovernancePolicyImpactCommand(
          proposalId,
          stringInput(request.input(), "targetPolicyId", null),
          stringInput(request.input(), "evidenceRequest", "Analyze policy proposal impact using governed read-only evidence."),
          listStringInput(request.input(), "affectedCapabilityIds"),
          listStringInput(request.input(), "affectedArtifactRefs"),
          listStringInput(request.input(), "evidenceRefs"),
          firstNonBlank(request.idempotencyKey(), stringInput(request.input(), "idempotencyKey", null))),
          request.correlationId());
      var surface = governancePolicyImpactService.taskSurface(actor, task.impactTaskId(), request.correlationId());
      var status = task.status().name().toLowerCase(Locale.ROOT);
      return new CapabilityActionResult(status, "Governance/Policy impact analysis entered backend lifecycle state " + status + "; no policy state was changed.", request.correlationId(), surface.traceIds(), governancePolicyImpactEnvelope(actor, request.correlationId(), surface, governanceImpactTaskActions()));
    } catch (AuthorizationException missingProposal) {
      if (!"governance-policy-proposal-not-found-or-forbidden".equals(missingProposal.reasonCode())) throw missingProposal;
      return new CapabilityActionResult("blocked_provider_or_runtime", "Governance/Policy impact analysis start is backend-governed and fails closed when provider/runtime configuration is missing; no deterministic, simulated, model-less, or fake impact_ready success is returned.", request.correlationId(), List.of("trace-governance-policy-impact-analysis-blocked"), governancePolicyImpactAnalysisBlockedSurface(actor, request.correlationId()));
    }
  }

  private CapabilityActionResult decideGovernancePolicyImpactResult(AuthContextResolver.ResolvedMe actor, CapabilityActionRequest request, String decision) {
    var impactTaskId = stringInput(request.input(), "impactTaskId", stringInput(request.input(), "taskId", ""));
    var reason = stringInput(request.input(), "reason", "Governance/Policy impact result disposition recorded by authorized reviewer.");
    if ("accept".equals(decision)) governancePolicyImpactService.acceptResult(actor, impactTaskId, reason, request.correlationId());
    else if ("reject".equals(decision)) governancePolicyImpactService.rejectResult(actor, impactTaskId, reason, request.correlationId());
    else governancePolicyImpactService.requestChanges(actor, impactTaskId, reason, request.correlationId());
    var surface = governancePolicyImpactService.resultSurface(actor, impactTaskId, request.correlationId());
    return new CapabilityActionResult("approval-required", "Governance/Policy impact result disposition recorded as advisory evidence only; activation remains a separate policy command.", request.correlationId(), surface.traceIds(), governancePolicyImpactEnvelope(actor, request.correlationId(), surface, governanceImpactResultActions()));
  }

  private SurfaceEnvelope governancePolicyImpactEnvelope(AuthContextResolver.ResolvedMe actor, String correlationId, GovernancePolicyImpactService.SurfaceData surface, List<SurfaceAction> actions) {
    return new SurfaceEnvelope(surface.surfaceId(), surface.surfaceType(), "v1", surface.title(), GOVERNANCE_POLICY_AGENT_ID, reusableAgentsForSurface(surface.surfaceId()), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, surface.traceIds(), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload", "providerSecret")), surface.data(), actions, List.of(mapOf("label", "Open surface", "href", "/ui?surfaceId=" + surface.surfaceId(), "rel", "deep-link")));
  }

  private List<SurfaceAction> governanceImpactTaskActions() {
    return List.of(governanceReadImpactAnalysisAction(), governanceCancelImpactAnalysisAction(), openAuditAction());
  }

  private List<SurfaceAction> governanceImpactResultActions() {
    return List.of(governanceAcceptImpactResultAction(), governanceRejectImpactResultAction(), governanceRequestImpactChangesAction(), openAuditAction());
  }

  private CapabilityActionResult governancePolicySystemMessageResult(AuthContextResolver.ResolvedMe actor, String actionId, String reasonCode, String message, String correlationId) {
    var surface = envelope("surface-governance-policy-system-message", "system-message", "Governance/Policy action blocked", actor, correlationId,
        mapOf("surfaceContract", "governance.policy.system_message.v1", "status", "forbidden", "severity", "warning", "title", "Governance/Policy action blocked", "message", message, "safeReasonCode", reasonCode, "capabilityId", actionId, "traceRefs", List.of("trace-governance-policy-denial-" + stableSuffix(correlationId)), "noFakeSuccess", true, "noDirectMutation", true, "redaction", "browser-safe denial; protected data omitted"),
        List.of(governanceDashboardAction(), openAuditAction()));
    return new CapabilityActionResult("denied", message, correlationId, surface.traceIds(), surface);
  }

  private String governancePolicySafeDenialMessage(String reasonCode) {
    if (reasonCode == null || reasonCode.isBlank()) return "Backend authorization denied this Governance/Policy action for the selected context.";
    if (reasonCode.contains("idempotency")) return "This Governance/Policy action requires a valid idempotency key before any side effect can be considered.";
    if (reasonCode.contains("proposal")) return "Select an authorized tenant-scoped policy proposal before continuing.";
    if (reasonCode.contains("capability") || reasonCode.contains("forbidden")) return "Backend authorization denied this Governance/Policy action for the selected context.";
    return "Governance/Policy action blocked: " + reasonCode.replace('-', ' ').replace('_', ' ') + ".";
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


  private List<Map<String, Object>> attentionItemsFor(AuthContextResolver.ResolvedMe actor, String workstreamId, String correlationId) {
    return attentionMaps(attentionService.listWorkstreamItems(actor, workstreamId, correlationId));
  }

  private Map<String, Object> actionEntry(SurfaceAction action) {
    return mapOf(
        "actionId", action.actionId(),
        "label", action.label(),
        "governedToolId", action.governedToolId(),
        "capabilityId", action.capabilityId(),
        "resultSurfaceId", action.resultSurface() == null ? null : action.resultSurface().updateSurfaceId(),
        "approvalRequired", action.requiresApproval(),
        "denialHint", action.disabled() == null ? null : action.disabled().message());
  }

  private SurfaceEnvelope agentAdminDashboardSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    seedStarterCoreAttention(actor, correlationId);
    var attention = attentionMaps(attentionService.listWorkstreamItems(actor, AGENT_ADMIN_AGENT_ID, correlationId));
    return envelope("surface-agent-admin-dashboard", "dashboard", "Agent Admin command center", actor, correlationId,
        mapOf("surfaceContract", "agent_admin.dashboard.v1",
            "cards", List.of(
                mapOf("cardId", "agent-admin-card-provider", "label", "Provider readiness", "value", "Ready", "status", "Model refs configured; secrets redacted", "severity", "info", "actionId", "action-display-agent-catalog", "targetSurfaceId", "surface-agent-admin-catalog"),
                mapOf("cardId", "agent-admin-card-approvals", "label", "Behavior approvals", "value", 1, "status", "Human review required before activation", "severity", "blocked", "actionId", "action-submit-behavior-change", "targetSurfaceId", "surface-agent-behavior-proposal"),
                mapOf("cardId", "agent-admin-card-tool-boundary", "label", "Tool-boundary risks", "value", 1, "status", "Side-effecting grant denied until separate review", "severity", "urgent", "actionId", "action-simulate-tool-boundary", "targetSurfaceId", "surface-agent-tool-boundary-diff"),
                mapOf("cardId", "agent-admin-card-seed", "label", "Seed material", "value", 3, "status", "Starter defaults visible; tenant overrides preserved", "severity", "info", "actionId", "action-list-agent-seed-material", "targetSurfaceId", "surface-agent-seed-material")),
            "attentionQueues", List.of(
                mapOf("queueId", "provider-readiness", "label", "Provider/model readiness", "count", 0, "severity", "info", "statusText", "Open readiness detail", "sourceCapabilityId", AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, "targetSurfaceId", "surface-agent-admin-catalog", "actionId", "action-display-agent-catalog", "traceRefs", List.of("trace-agent-admin-catalog"), "redaction", "provider secrets redacted"),
                mapOf("queueId", "behavior-approval", "label", "Behavior proposals awaiting human decision", "count", 1, "severity", "blocked", "statusText", "Approval required", "sourceCapabilityId", AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "targetSurfaceId", "surface-agent-behavior-proposal", "actionId", "action-submit-behavior-change", "traceRefs", List.of("trace-agent-admin-behavior-review"), "redaction", "raw prompt/skill bodies omitted"),
                mapOf("queueId", "tool-boundary-risk", "label", "Risky tool-boundary expansion attempts", "count", 1, "severity", "urgent", "statusText", "Simulation denied side effect", "sourceCapabilityId", AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, "targetSurfaceId", "surface-agent-tool-boundary-diff", "actionId", "action-simulate-tool-boundary", "traceRefs", List.of("trace-agent-admin-tool-denied-email-send"), "redaction", "tool output omitted"),
                mapOf("queueId", "prompt-risk-review", "label", "Prompt-risk autonomous review results", "count", 0, "severity", "warning", "statusText", "Open prompt-risk review status", "sourceCapabilityId", AgentAdminPromptRiskReviewService.READ_CAPABILITY, "targetSurfaceId", "surface-agent-admin-prompt-risk-review", "actionId", "action-agentadmin-read-prompt-risk-review", "traceRefs", List.of("trace-prompt-risk-model-call-001"), "redaction", "browser-safe finding summaries only")),
            "authorizedActions", List.of(
                authorizedActionMap(displayAgentCatalogAction()), authorizedActionMap(proposePromptDiffAction()), authorizedActionMap(testPromptAction()), authorizedActionMap(startPromptRiskReviewAction()), authorizedActionMap(listAgentSeedMaterialAction()), authorizedActionMap(openAgentTraceAction())),
            "attentionItems", attention,
            "recentActivity", List.of(mapOf("activityId", "activity-agent-admin-protected-read", "label", "Catalog read protected by selected AuthContext", "summary", "Scoped AgentDefinition projection returns browser-safe readiness summaries.", "traceId", "trace-agent-admin-catalog")),
            "hero", mapOf("title", "Govern managed agents safely", "scopeLabel", "Tenant Admin · selected customer scope", "scopeType", actor.selectedContext().scopeType().name(), "adminLevel", "Agent steward", "redactionSummary", "Provider secrets, raw prompts, raw skills, raw references, hidden authority, and cross-tenant evidence are omitted."),
            "readiness", "ready_with_attention",
            "capabilityIds", List.of(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, AgentAdminPromptRiskReviewService.READ_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY),
            "redaction", mapOf("browserSafe", true, "omittedFieldKeys", List.of("rawPromptBody", "rawSkillBody", "rawReferenceBody", "providerCredentialValue", "rawJwt"), "previewLimitChars", 220),
            "traceRefs", List.of("trace-agent-admin-dashboard", "trace-agent-admin-catalog"),
            "systemStates", List.of("loading", "empty", "forbidden", "stale", "partial-data", "blocked_provider_or_runtime")),
        List.of(displayAgentAdminDashboardAction(), displayAgentCatalogAction(), proposePromptDiffAction(), submitBehaviorChangeAction(), simulateToolBoundaryAction(), listAgentSeedMaterialAction(), testPromptAction(), startPromptRiskReviewAction(), readPromptRiskReviewAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentAdminCatalogSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    seedStarterCoreAttention(actor, correlationId);
    return envelope("surface-agent-admin-catalog", "list-search", "Agent Admin catalog", actor, correlationId, withAttentionItems(agentAdminService.catalog(actor, correlationId), actor, AGENT_ADMIN_AGENT_ID, correlationId), List.of(displayAgentAdminDashboardAction(), displayAgentCatalogAction(), openAgentDetailAction(), activateAgentDefinitionAction(), deactivateAgentDefinitionAction(), listAgentSeedMaterialAction(), importAgentSeedDefaultsAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-admin-detail", "detail-edit", "Agent Admin readiness detail", actor, correlationId, agentAdminService.definitionDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId), List.of(activateAgentDefinitionAction(), deactivateAgentDefinitionAction(), proposePromptDiffAction(), testPromptAction(), manageModelRefAction(), openAgentTraceAction()));
  }

  private Map<String, Object> governanceDiffData(String surfaceContract, String proposalId, String lifecycleState, String risk, String beforeSummary, String afterSummary, List<Map<String, Object>> changes, List<String> traceLinks) {
    return mapOf(
        "surfaceContract", surfaceContract,
        "proposalId", proposalId,
        "lifecycleState", lifecycleState,
        "riskClassification", risk,
        "requiredApproval", "Human Agent Admin review and backend policy approval are required before activation.",
        "simulationSummary", "Backend simulation preserves tenant scope, redaction, provider fail-closed behavior, and ToolPermissionBoundary checks.",
        "activationStatus", "not active until separately approved and activated",
        "beforeSummary", beforeSummary,
        "afterSummary", afterSummary,
        "changes", changes,
        "traceLinks", traceLinks,
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", List.of("rawPromptBody", "rawSkillBody", "rawReferenceBody", "providerCredentialValue", "rawJwt")),
        "noDirectMutation", true);
  }

  private SurfaceEnvelope agentPromptGovernanceSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var prompt = agentAdminService.promptDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId);
    return envelope("surface-agent-prompt-governance", "governance-diff", "Prompt governance review", actor, correlationId,
        governanceDiffData("agent_admin.prompt_version.v1", "proposal-agent-admin-prompt-001", "draft", "medium",
            "Active prompt remains backend-authorized; raw prompt body is hidden and only a redacted preview is available.",
            "Draft prompt wording can be proposed for review, but activation remains a separate human/backend-governed action.",
            List.of(
                mapOf("path", "redactedPreview", "before", prompt.get("redactedPreview"), "after", "Proposed wording keeps authority, redaction, and trace requirements explicit.", "impact", "Browser-safe preview only; raw prompt text remains omitted."),
                mapOf("path", "lifecycle", "before", prompt.get("status"), "after", "approval-required", "impact", "No prompt, model output, or frontend state can activate behavior directly.")),
            List.of("trace-agent-admin-prompt-" + stableSuffix(correlationId))),
        List.of(proposePromptDiffAction(), submitBehaviorChangeAction(), testPromptAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentSkillVersionSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var skill = agentAdminService.skillDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, null, correlationId);
    return envelope("surface-agent-skill-version", "governance-diff", "Skill version review", actor, correlationId,
        governanceDiffData("agent_admin.skill_version.v1", "proposal-agent-admin-skill-001", "in_review", "medium",
            "Current assigned skill is loaded only through governed readSkill after manifest and ToolPermissionBoundary checks.",
            "Reviewed skill changes preserve compact manifest loading and browser-safe redacted previews.",
            List.of(mapOf("path", "readSkill", "before", skill.get("stableSkillId"), "after", "assigned-only", "impact", "Unassigned skill loads remain denied and traced.")),
            List.of("trace-agent-admin-skill-" + stableSuffix(correlationId))),
        List.of(approveSkillManifestAction(), submitBehaviorChangeAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentSkillManifestSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var manifest = agentAdminService.manifestDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId);
    return envelope("surface-agent-skill-manifest-diff", "governance-diff", "Skill and reference manifest review", actor, correlationId,
        governanceDiffData("agent_admin.manifest.v1", "proposal-agent-admin-manifest-001", "in_review", "high",
            "Current compact skill/reference manifests allow assigned readSkill/readReferenceDoc loads only.",
            "Proposed manifest changes require human review and preserve compact manifest and loader-denial traces.",
            List.of(
                mapOf("path", "skillManifest", "before", manifest.get("skillManifest"), "after", "reviewed compact manifest", "impact", "Full skill bodies remain governed and browser-hidden."),
                mapOf("path", "referenceManifest", "before", manifest.get("referenceManifest"), "after", "reviewed compact reference manifest", "impact", "Reference evidence access broadening requires review.")),
            List.of("trace-agent-admin-manifest-" + stableSuffix(correlationId))),
        List.of(approveSkillManifestAction(), submitBehaviorChangeAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentToolBoundarySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var boundary = agentAdminService.toolBoundaryDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId);
    return envelope("surface-agent-tool-boundary-diff", "governance-diff", "Tool boundary simulation review", actor, correlationId,
        governanceDiffData("agent_admin.tool_boundary.v1", "proposal-agent-admin-tool-boundary-001", "blocked", "critical",
            "Current ToolPermissionBoundary allows only scoped read-only Agent Admin evidence and governed loaders.",
            "Requested side-effecting grants are denied or approval-required by backend simulation before any activation.",
            List.of(
                mapOf("path", "grants", "before", boundary.get("grants"), "after", "side-effecting grant requested", "impact", "External side effects stay blocked without separate approval, idempotency, and trace policy."),
                mapOf("path", "simulation.result", "before", "not run", "after", "TOOL_BOUNDARY_DENIED", "impact", "Frontend cannot bypass backend ToolPermissionBoundary enforcement.")),
            List.of("trace-agent-admin-tool-boundary-" + stableSuffix(correlationId))),
        List.of(simulateToolBoundaryAction(), submitBehaviorChangeAction(), openAgentTraceAction()));
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


  private SurfaceEnvelope agentLifecycleConfirmationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String lifecycleAction, boolean changed) {
    var activation = "activate".equals(lifecycleAction) || "activated".equals(lifecycleAction);
    var normalizedAction = activation ? "activate" : "deactivate";
    return envelope(activation ? "surface-agent-activation-confirmation" : "surface-agent-deactivation-confirmation", "lifecycle-confirmation", activation ? "Confirm agent activation" : "Confirm agent deactivation", actor, correlationId,
        mapOf(
            "surfaceContract", activation ? "agent_admin.activation_confirmation.v1" : "agent_admin.deactivation_confirmation.v1",
            "recordId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
            "recordLabel", "Agent Admin Agent",
            "lifecycleAction", normalizedAction,
            "currentStatus", activation ? "inactive_or_pending" : "active_or_pending",
            "proposedStatus", activation ? "active" : "deactivated",
            "impactSummary", activation ? "Activation would enable runtime invocation only after backend approval, provider readiness, rollback metadata, idempotency, and trace checks pass." : "Deactivation would disable runtime invocation and governed loader access only after backend authorization, idempotency, and trace checks pass.",
            "approvalState", "approval required before mutation",
            "policyBasis", "managed-agent-governance lifecycle policy; no model output or frontend state may commit this directly",
            "idempotencyKeyHint", "client-generated lifecycle key bound to AgentDefinition and proposal context",
            "disabledReason", changed ? null : "Confirmation preview only: this surface is returned before mutation so the user can review impact and policy evidence.",
            "evidenceRefs", List.of("AgentDefinition", "managed-agent-governance", "AgentWorkTrace"),
            "traceRefs", List.of("trace-agent-definition-" + normalizedAction + "-confirmation-" + stableSuffix(correlationId)),
            "actionContext", mapOf("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
            "noDirectMutation", true,
            "redaction", "Provider secrets, raw prompts, raw skills, raw references, and hidden authority remain omitted."),
        List.of(openAgentDetailAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentRollbackConfirmationSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-rollback-confirmation", "lifecycle-confirmation", "Confirm agent behavior rollback", actor, correlationId,
        mapOf(
            "surfaceContract", "agent_admin.rollback_confirmation.v1",
            "recordId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
            "recordLabel", "Agent Admin Agent",
            "lifecycleAction", "rollback",
            "currentStatus", "activated_proposal",
            "proposedStatus", "previous_active_version",
            "impactSummary", "Rollback restores a prior active behavior snapshot only after backend metadata, authorization, idempotency, and trace checks pass.",
            "approvalState", "rollback metadata required",
            "policyBasis", "managed-agent-governance rollback policy",
            "idempotencyKeyHint", "server-issued rollback-safe key preferred",
            "disabledReason", "Rollback requires activated proposal metadata and backend command authority.",
            "evidenceRefs", List.of("rollback snapshot", "activation audit event"),
            "traceRefs", List.of("trace-agent-definition-rollback-confirmation-" + stableSuffix(correlationId)),
            "actionContext", mapOf("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
            "noDirectMutation", true,
            "redaction", "Provider secrets, raw prompts, raw skills, raw references, and hidden authority remain omitted."),
        List.of(openAgentDetailAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentSeedImportConfirmationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, int createdCount, int skippedCount) {
    return envelope("surface-agent-seed-import-confirmation", "lifecycle-confirmation", "Agent seed import confirmation", actor, correlationId,
        mapOf("surfaceContract", "agent_admin.seed_import_confirmation.v1", "status", createdCount == 0 ? "no-op" : "success", "summary", "Seed import preserved tenant customizations and only created missing starter defaults.", "createdCount", createdCount, "skippedCount", skippedCount, "safeReason", "Raw prompt, skill, reference, provider credential, and hidden override content are never browser-visible.", "traceRefs", List.of("trace-agent-seed-import-" + stableSuffix(correlationId)), "targetSurfaceId", "surface-agent-seed-material"),
        List.of(listAgentSeedMaterialAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentPromptRiskReviewEmptySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-agent-admin-prompt-risk-review", "workflow-status", "Prompt-risk review status", actor, correlationId,
        mapOf("surfaceContract", "agent_admin.prompt_risk_review_task.v1", "workflowId", "agent-admin-prompt-risk-review", "status", "empty", "summary", "No prompt-risk review task is selected. Start a governed review from Agent Admin before accepting or rejecting advisory findings.", "resultReviewStates", List.of("pending_worker_result", "completed_review_required", "result_accepted", "result_rejected", "cancelled"), "resultReviewState", "pending_worker_result", "noDirectMutation", true, "activationBlockedUntilHumanDecision", true, "providerFailures", List.of("blocked_provider_or_runtime if provider/runtime is unavailable"), "evidenceRefs", List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"), "traceIds", List.of("trace-agent-admin-prompt-risk-status-" + stableSuffix(correlationId))),
        List.of(startPromptRiskReviewAction(), readPromptRiskReviewAction(), cancelPromptRiskReviewAction(), acceptPromptRiskReviewAction(), rejectPromptRiskReviewAction(), openAgentTraceAction()));
  }

  private CapabilityActionResult promptRiskReviewActionResult(PromptRiskReviewTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), promptRiskReviewSurface(actor, task, correlationId));
  }

  private SurfaceEnvelope promptRiskReviewSurface(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask task, String correlationId) {
    return envelope("surface-agent-admin-prompt-risk-review", "workflow-status", "Prompt-risk review status", actor, correlationId,
        mapOf("surfaceContract", "agent_admin.prompt_risk_review_task.v1", "workflowId", "agent-admin-prompt-risk-review", "taskId", task.taskId(), "autonomousAgentTaskId", task.autonomousAgentTaskId(), "status", task.status().name().toLowerCase(Locale.ROOT), "summary", task.summary(), "progress", mapOf("percent", task.progressPercent(), "summary", task.summary()), "resultReviewStates", List.of("pending_worker_result", "completed_review_required", "result_accepted", "result_rejected", "cancelled"), "resultReviewState", promptRiskResultReviewState(task), "noDirectMutation", true, "activationBlockedUntilHumanDecision", true, "requiredCapabilityId", AgentAdminPromptRiskReviewService.READ_CAPABILITY, "providerFailures", task.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? List.of(firstNonBlank(task.blockerCode(), "blocked_provider_or_runtime")) : List.of(), "blockers", task.blockerCode() == null ? List.of() : List.of(mapOf("code", task.blockerCode(), "message", task.summary())), "evidenceRefs", task.evidenceRefs(), "findingRefs", task.findingRefs(), "recommendations", List.of(mapOf("recommendationId", "prompt-risk-human-review", "label", "Human review required before activation", "risk", "medium", "confidence", "backend-derived", "summary", "Accepting advisory findings creates review evidence only; activation remains a separate governed behavior-change decision.")), "traceIds", task.traceIds(), "traceLinks", task.traceIds().stream().map(traceId -> mapOf("traceId", traceId, "targetSurfaceId", "surface-agent-admin-trace", "label", "Prompt-risk trace", "summary", "Role-gated runtime evidence")).toList()),
        List.of(startPromptRiskReviewAction(), readPromptRiskReviewAction(), cancelPromptRiskReviewAction(), acceptPromptRiskReviewAction(), rejectPromptRiskReviewAction(), openAgentTraceAction()));
  }

  private String promptRiskResultReviewState(PromptRiskReviewTask task) {
    return switch (task.status()) {
      case COMPLETED_REVIEW_REQUIRED -> "completed_review_required";
      case ACCEPTED -> "result_accepted";
      case REJECTED -> "result_rejected";
      case CANCELLED -> "cancelled";
      default -> "pending_worker_result";
    };
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
    var envelopes = new ArrayList<>(workstreamEventRepository.listTenant(workstreamEventTenantId(actor.selectedContext())));
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

  private record UserAdminLayer(String layerId, String surfaceId, String surfaceContract, String title, String scopeLabel, boolean saasOwner, String readCapability, String activeUsersLabel, String boundaryNotice, List<String> capabilityIds, List<SurfaceAction> actions) {
    List<Map<String, Object>> sections() {
      return saasOwner
          ? List.of(
              mapOf("sectionId", "section-organization-admin", "label", "Organization Admin", "summary", "Create, list, rename, suspend, and reactivate tenant/organization lifecycle boundaries without exposing tenant app data."),
              mapOf("sectionId", "section-platform-users", "label", "Platform user administration", "summary", "Manage SaaS Owner account visibility and audit-safe platform administration."))
          : List.of(
              mapOf("sectionId", "section-users", "label", scopeLabel + " users", "summary", "Manage users, invitations, roles, status, support access, and scoped access review."),
              mapOf("sectionId", "section-audit", "label", scopeLabel + " audit evidence", "summary", "Review denied actions, traces, and access-review evidence for the selected context."));
    }
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
      case "surface-user-admin-dashboard", "surface-user-admin-saas-owner-dashboard", "surface-user-admin-tenant-dashboard", "surface-user-admin-customer-dashboard" -> dashboardSurface(actor, correlationId);
      case "surface-user-admin-saas-owner-admins" -> saasOwnerAdminsSurface(actor, correlationId);
      case "surface-user-admin-saas-owner-admin-invitation-create" -> saasOwnerAdminInvitationCreateSurface(actor, correlationId);
      case "surface-user-admin-organization-directory" -> organizationDirectorySurface(actor, correlationId);
      case "surface-user-admin-organization-detail" -> organizationDetailSurface(actor, correlationId);
      case "surface-user-admin-organization-create" -> organizationCreateSurface(actor, correlationId);
      case "surface-user-admin-organization-rename" -> organizationRenameSurface(actor, correlationId);
      case "surface-user-admin-organization-suspend-confirmation" -> organizationSuspendSurface(actor, correlationId);
      case "surface-user-admin-organization-reactivate-confirmation" -> organizationReactivateSurface(actor, correlationId);
      case "surface-user-admin-organization-admins" -> organizationAdminsSurface(actor, correlationId);
      case "surface-user-admin-organization-admin-invitation-create" -> organizationAdminInvitationCreateSurface(actor, correlationId);
      case "surface-user-admin-organization-admin-detail" -> organizationAdminDetailSurface(actor, correlationId);
      case "surface-user-admin-customer-directory" -> customerDirectorySurface(actor, correlationId);
      case "surface-user-admin-customer-detail" -> customerDetailSurface(actor, correlationId);
      case "surface-user-admin-customer-create" -> customerCreateSurface(actor, correlationId);
      case "surface-user-admin-customer-rename" -> customerRenameSurface(actor, correlationId);
      case "surface-user-admin-customer-suspend-confirmation" -> customerSuspendSurface(actor, correlationId);
      case "surface-user-admin-customer-reactivate-confirmation" -> customerReactivateSurface(actor, correlationId);
      case "surface-user-admin-customer-admins" -> customerAdminsSurface(actor, correlationId);
      case "surface-user-admin-customer-admin-invitation-create" -> customerAdminInvitationCreateSurface(actor, correlationId);
      case "surface-user-admin-customer-admin-detail" -> customerAdminDetailSurface(actor, correlationId);
      case "surface-user-admin-users" -> listSurface(actor, correlationId);
      case "surface-user-admin-user-detail" -> detailSurface(actor, correlationId);
      case "surface-user-admin-invitation-create" -> invitationCreateSurface(actor, null, correlationId);
      case "surface-user-admin-invitation-detail" -> invitationDetailSurface(actor, null, correlationId);
      case "surface-user-admin-invitation-resend-confirmation" -> invitationResendConfirmationSurface(actor, null, correlationId);
      case "surface-user-admin-invitation-revoke-confirmation" -> invitationRevokeConfirmationSurface(actor, null, correlationId);
      case "surface-user-admin-membership-status-confirmation" -> membershipStatusConfirmationSurface(actor, null, correlationId);
      case "surface-user-admin-support-access-grant" -> supportAccessGrantSurface(actor, null, correlationId);
      case "surface-user-admin-support-access-revoke-confirmation" -> supportAccessRevokeConfirmationSurface(actor, null, correlationId);
      case "surface-user-admin-access-review-task" -> accessReviewBlockedSurface(actor, correlationId);
      case "surface-user-admin-identity-exception-review" -> identityExceptionReviewSurface(actor, null, correlationId);
      case "surface-agent-admin-dashboard" -> agentAdminDashboardSurface(actor, correlationId);
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
      case "surface-agent-admin-prompt-risk-review" -> agentPromptRiskReviewEmptySurface(actor, correlationId);
      case "surface-agent-activation-confirmation", "surface-agent-definition-activation-confirmation" -> agentLifecycleConfirmationSurface(actor, correlationId, "activate", false);
      case "surface-agent-deactivation-confirmation", "surface-agent-definition-deactivation-confirmation" -> agentLifecycleConfirmationSurface(actor, correlationId, "deactivate", false);
      case "surface-agent-rollback-confirmation" -> agentRollbackConfirmationSurface(actor, correlationId);
      case "surface-agent-seed-import-confirmation" -> agentSeedImportConfirmationSurface(actor, correlationId, 0, 0);
      case "surface-audit-trace-dashboard" -> auditTraceDashboardSurface(actor, correlationId);
      case "surface-audit-trace-search" -> auditTraceSearchSurface(actor, null, correlationId);
      case "surface-audit-trace-detail" -> auditTraceDetailSurface(actor, null, correlationId);
      case "surface-audit-trace-timeline", "surface-audit-timeline" -> auditTraceCorrelationTimelineSurface(actor, null, correlationId);
      case "surface-audit-trace-failure-evidence" -> auditTraceFailureEvidenceSurface(actor, null, correlationId);
      case "surface-audit-trace-investigation-guide" -> auditTraceInvestigationGuideSurface(actor, null, correlationId);
      case "surface-audit-trace-investigation-note" -> auditTraceInvestigationNoteSurface(actor, null, null, correlationId);
      case "surface-audit-trace-export-request" -> auditTraceExportRequestSurface(actor, null, "surface-open-" + stableSuffix(correlationId), correlationId);
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
      case AGENT_ADMIN_AGENT_ID -> "surface-agent-admin-dashboard";
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
    if (List.of("show saas owner admins", "open saas owner admins", "show app admins", "open app admins", "saas owner admins", "app admins").contains(text)) return new ShellSurfaceAlias(USER_ADMIN_AGENT_ID, "surface-user-admin-saas-owner-admins", "show saas owner admins");
    if (List.of("show organizations", "open organizations", "show organization directory", "open organization directory", "organizations").contains(text)) return new ShellSurfaceAlias(USER_ADMIN_AGENT_ID, "surface-user-admin-organization-directory", "show organizations");
    if (List.of("show customers", "open customers", "show customer directory", "open customer directory", "customers").contains(text)) return new ShellSurfaceAlias(USER_ADMIN_AGENT_ID, "surface-user-admin-customer-directory", "show customers");
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
    var detail = invitationDetailSurface(actor, mapOf("invitationId", invitationId), correlationId);
    var resultStatus = Objects.equals(detail.data().get("status"), "blocked_provider_or_runtime") ? "blocked-runtime" : status;
    var resultMessage = resultStatus.equals(status) ? message : message + " Delivery is blocked by provider/outbox readiness; review the typed invitation detail surface for safe recovery.";
    return new CapabilityActionResult(resultStatus, resultMessage, correlationId, List.of(traceId), detail);
  }

  private CapabilityActionResult accessReviewActionResult(AccessReviewTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), accessReviewSurface(actor, task, correlationId));
  }

  private CapabilityActionResult identityRelinkActionResult(UserAdminService.IdentityRelinkResult recovery, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, List.of(recovery.traceId()), identityExceptionReviewSurface(actor, mapOf("accountId", recovery.accountId(), "recoveryId", recovery.recoveryId()), correlationId));
  }

  private Map<String, Object> userDirectoryRow(UserDirectoryView.UserDirectoryRow user) {
    return mapOf("id", user.accountId(), "accountId", user.accountId(), "membershipId", user.membershipId(), "rowType", "active-user", "targetObjectType", "account", "targetSurfaceId", "surface-user-admin-user-detail", "targetSurfaceType", "show-inspection", "openActionId", "action-display-user-detail", "activation", mapOf("actionId", "action-display-user-detail", "targetSurfaceId", "surface-user-admin-user-detail", "targetObjectType", "account", "safeActionContext", mapOf("accountId", user.accountId(), "membershipId", user.membershipId())), "eligibility", mapOf("canOpen", true, "canMutateInline", false, "redactionState", "visible"), "safeActionContext", mapOf("accountId", user.accountId(), "membershipId", user.membershipId()), "email", user.accountId(), "displayName", user.displayName(), "role", roleLabels(user.roles()), "status", user.membershipStatus().name().toLowerCase(), "supportAccess", user.supportAccess(), "supportAccessExpiresAt", user.expiresAt() == null ? "" : user.expiresAt().toString(), "traceId", "trace-useradmin-user-" + stableSuffix(user.accountId()), "traceRefs", List.of("trace-useradmin-user-" + stableSuffix(user.accountId())), "redactionState", "visible");
  }

  private Map<String, Object> invitationRow(InvitationView.InvitationRow invite) {
    return mapOf("id", invite.invitationId(), "invitationId", invite.invitationId(), "rowType", "invitation", "targetObjectType", "invitation", "targetSurfaceId", "surface-user-admin-invitation-detail", "targetSurfaceType", "show-inspection", "openActionId", "action-display-invitation-detail", "activation", mapOf("actionId", "action-display-invitation-detail", "targetSurfaceId", "surface-user-admin-invitation-detail", "targetObjectType", "invitation", "safeActionContext", mapOf("invitationId", invite.invitationId())), "eligibility", mapOf("canOpen", true, "canResend", invite.canResend(), "canRevoke", invite.canRevoke(), "canMutateInline", false, "redactionState", "visible"), "safeActionContext", mapOf("invitationId", invite.invitationId()), "email", invite.targetEmail(), "displayName", invite.targetEmail(), "role", roleLabels(invite.requestedRoles()), "status", invite.status().name().toLowerCase(), "delivery", invite.deliveryStatus().name().toLowerCase(), "expiresAt", invite.expiresAt().toString(), "canResend", invite.canResend(), "canRevoke", invite.canRevoke(), "traceId", "trace-useradmin-invitation-" + stableSuffix(invite.invitationId()), "traceRefs", List.of("trace-useradmin-invitation-" + stableSuffix(invite.invitationId())), "redactionState", "visible");
  }

  private List<Map<String, Object>> roleOptionsForSelectedContext(AuthContextResolver.ResolvedMe actor) {
    var roles = actor.selectedContext().scopeType() == ScopeType.SAAS_OWNER
        ? List.of(FoundationRole.SAAS_OWNER_ADMIN)
        : actor.selectedContext().scopeType() == ScopeType.CUSTOMER
            ? List.of(FoundationRole.CUSTOMER_ADMIN, FoundationRole.CUSTOMER_USER)
            : List.of(FoundationRole.TENANT_ADMIN, FoundationRole.TENANT_EMPLOYEE, FoundationRole.AUDITOR);
    return roles.stream().map(role -> mapOf("roleId", role.name(), "label", roleLabel(role), "capabilitySummary", "Backend-authorized option for the selected AuthContext", "requiresApproval", role == FoundationRole.SAAS_OWNER_ADMIN || role == FoundationRole.TENANT_ADMIN)).toList();
  }

  private List<Map<String, Object>> invitationExpiryOptions() {
    return List.of(mapOf("optionId", "seven-days", "label", "7 days", "durationDays", 7), mapOf("optionId", "fourteen-days", "label", "14 days", "durationDays", 14));
  }

  private List<Map<String, Object>> supportExpiryOptions() {
    return List.of(mapOf("optionId", "two-hours", "label", "2 hours", "durationHours", 2), mapOf("optionId", "eight-hours", "label", "8 hours", "durationHours", 8));
  }

  private List<Map<String, Object>> membershipStatusOptions() {
    return List.of(mapOf("status", "active", "label", "Reactivate", "actionId", "action-useradmin-reactivate-member"), mapOf("status", "removed", "label", "Deactivate", "actionId", "action-useradmin-disable-member"));
  }

  private static boolean isPendingInvitation(InvitationView.InvitationRow invite) {
    return invite.status() != InvitationStatus.ACCEPTED && invite.status() != InvitationStatus.REVOKED && invite.status() != InvitationStatus.EXPIRED;
  }

  private String roleLabels(List<FoundationRole> roles) {
    return roles.stream().map(this::roleLabel).sorted().collect(Collectors.joining(", "));
  }

  private String roleLabel(FoundationRole role) {
    var label = role.name().toLowerCase(Locale.ROOT).replace('_', ' ');
    return label.substring(0, 1).toUpperCase(Locale.ROOT) + label.substring(1);
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

  private String invitationSurfaceStatus(InvitationView.InvitationRow invite) {
    return invite.deliveryStatus() == EmailDeliveryStatus.FAILED ? "blocked_provider_or_runtime" : invite.status().name().toLowerCase(Locale.ROOT);
  }

  private Map<String, Object> invitationDeliveryState(InvitationView.InvitationRow invite, String correlationId) {
    var failed = invite.deliveryStatus() == EmailDeliveryStatus.FAILED;
    return mapOf(
        "currentStatus", invite.deliveryStatus().name().toLowerCase(Locale.ROOT),
        "invitationStatus", invite.status().name().toLowerCase(Locale.ROOT),
        "attempts", invite.deliveryAttempts(),
        "resendCount", invite.resendCount(),
        "lastSafeError", failed ? firstNonBlank(invite.lastDeliveryErrorSummary(), "provider-or-outbox-delivery-failed") : null,
        "retryEligible", invite.canResend(),
        "recoverySurfaceId", invite.canResend() ? "surface-user-admin-invitation-resend-confirmation" : "surface-user-admin-system-message",
        "recoveryActionId", invite.canResend() ? "action-open-useradmin-invitation-resend-confirmation" : null,
        "providerReadiness", failed ? "blocked_provider_or_runtime" : "ready_or_captured",
        "providerBoundary", "Provider message ids, raw Resend payloads, email bodies, tokens, and secrets are redacted from browser surfaces.",
        "traceRefs", List.of("trace-useradmin-invitation-delivery-" + stableSuffix(invite.invitationId() + correlationId)));
  }

  private List<String> invitationRecoverySteps(InvitationView.InvitationRow invite) {
    if (invite.deliveryStatus() == EmailDeliveryStatus.FAILED) {
      return invite.canResend()
          ? List.of("Verify backend Resend/outbox configuration with an authorized operator.", "Use the resend confirmation task to queue a retry after configuration is restored.", "Open audit evidence with the correlation id; raw tokens and provider payloads remain redacted.")
          : List.of("Review the invitation lifecycle state before retrying.", "Create a new scoped invitation if policy allows and this invitation is no longer actionable.", "Open audit evidence with the correlation id; raw tokens and provider payloads remain redacted.");
    }
    return List.of("Use resend or revoke task surfaces when lifecycle policy allows.", "Open audit evidence for delivery attempts and safe provider/outbox summaries.");
  }

  private List<String> invitationSystemStates(InvitationView.InvitationRow invite) {
    var states = new ArrayList<String>();
    states.add(invite.status().name().toLowerCase(Locale.ROOT));
    states.add("delivery_" + invite.deliveryStatus().name().toLowerCase(Locale.ROOT));
    if (invite.deliveryStatus() == EmailDeliveryStatus.FAILED) states.add("provider_blocked");
    if (!invite.canResend()) states.add("resend_unavailable");
    if (!invite.canRevoke()) states.add("revoke_unavailable");
    states.add("system_message_ready");
    return states;
  }

  private ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail readOrganizationDetail(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return StarterSecurityComponents.saasOwnerOrganizationAdminService().readOrganization(actor, stringInput(input, "organizationId", ""), correlationId);
  }

  private ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationActionResult runOrganizationLifecycleAction(AuthContextResolver.ResolvedMe actor, String actionId, Object input, String idempotencyKey, String correlationId) {
    var service = StarterSecurityComponents.saasOwnerOrganizationAdminService();
    return switch (actionId) {
      case "action-organization-create" -> service.createOrganization(actor, stringInput(input, "organizationName", ""), idempotencyKey, stringInput(input, "reason", "organization-created"), correlationId);
      case "action-organization-rename" -> service.renameOrganization(actor, stringInput(input, "organizationId", stringInput(input, "recordId", "")), stringInput(input, "organizationName", ""), idempotencyKey, stringInput(input, "reason", "organization-renamed"), correlationId);
      case "action-organization-suspend" -> service.suspendOrganization(actor, stringInput(input, "organizationId", stringInput(input, "recordId", "")), stringInput(input, "reason", "organization-suspended"), idempotencyKey, correlationId);
      case "action-organization-reactivate" -> service.reactivateOrganization(actor, stringInput(input, "organizationId", stringInput(input, "recordId", "")), stringInput(input, "reason", "organization-reactivated"), idempotencyKey, correlationId);
      default -> throw new AuthorizationException(404, "target-not-found-or-forbidden");
    };
  }

  private void requireOrganizationLifecycleAction(ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, String action, String correlationId) {
    if (!detail.visibleActions().contains(action)) {
      throw new AuthorizationException(409, "organization-lifecycle-action-unavailable:" + action + ":" + correlationId);
    }
  }

  private Map<String, Object> organizationSummaryMap(ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationSummary organization) {
    return mapOf("organizationId", organization.organizationId(), "organizationName", organization.organizationName(), "status", organization.status(), "safeLifecycleSummary", organization.status().equals("active") ? "Active Tenant boundary" : "Suspended Tenant boundary", "actionAvailability", organization.status().equals("active") ? List.of("rename", "suspend") : List.of("rename", "reactivate"), "traceRefs", organization.traceRefs());
  }

  private Map<String, Object> organizationDetailMap(ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail) {
    return mapOf("organizationId", detail.organization().organizationId(), "organizationName", detail.organization().organizationName(), "status", detail.organization().status(), "safeBoundaryNotice", detail.safeBoundaryNotice(), "visibleActions", detail.visibleActions(), "recentAuditEvents", List.of(), "traceRefs", detail.traceRefs(), "correlationId", detail.correlationId());
  }

  private CapabilityActionResult openOrganizationTaskSurface(AuthContextResolver.ResolvedMe actor, String actionId, Object input, String correlationId) {
    try {
      var surface = switch (actionId) {
        case "action-open-organization-create" -> organizationCreateSurface(actor, correlationId);
        case "action-open-organization-rename" -> organizationRenameSurface(actor, input, correlationId);
        case "action-open-organization-suspend" -> organizationSuspendSurface(actor, input, correlationId);
        case "action-open-organization-reactivate" -> organizationReactivateSurface(actor, input, correlationId);
        default -> null;
      };
      if (surface == null) return null;
      return new CapabilityActionResult("accepted", surface.title() + " opened through backend-authoritative Organization surface graph.", correlationId, surface.traceIds(), surface);
    } catch (AuthorizationException denied) {
      return new CapabilityActionResult("denied", "Organization lifecycle surface is unavailable for the selected Organization state or authorization context.", correlationId, List.of("trace-organization-open-denied-" + stableSuffix(correlationId)), shellSystemMessageSurface(actor, USER_ADMIN_AGENT_ID, denied.getMessage(), "Organization lifecycle surface is unavailable for the selected Organization state or authorization context.", correlationId));
    }
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
      case "action-audit-trace-request-redacted-export" -> auditTraceExportRequestSurface(actor, null, "surface-action-" + stableSuffix(correlationId), correlationId);
      case "action-audit-trace-start-summary-task", "action-audit-trace-summary-task-start" -> auditTraceSummaryTaskBlockedSurface(actor, correlationId);
      case "action-show-my-account-dashboard" -> myAccountDashboardSurface(actor, correlationId);
      case "action-show-my-profile", "action-update-my-profile" -> myProfileSurface(actor, correlationId);
      case "action-show-my-settings", "action-update-my-settings" -> mySettingsSurface(actor, correlationId);
      case "action-show-my-context" -> myContextSurface(actor, correlationId);
      case "action-select-my-context" -> accessProfileContextSurface(actor, correlationId);
      case "action-show-my-account-notification-center", "action-notification-mark-read", "action-notification-dismiss", "action-notification-archive", "action-notification-snooze", "action-notification-update-preferences" -> myAccountNotificationCenterSurface(actor, correlationId);
      case "action-sign-out" -> myAccountDashboardSurface(actor, correlationId);
      case "action-start-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-read-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-cancel-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-accept-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-reject-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-open-user-admin" -> dashboardSurface(actor, correlationId);
      case "action-open-agent-admin", "action-display-agent-admin-dashboard" -> agentAdminDashboardSurface(actor, correlationId);
      case "action-open-governance-policy", "action-governance-policy-dashboard" -> governancePolicySurface(actor, correlationId);
      case "action-governance-policy-list" -> governancePolicyInventorySurface(actor, correlationId);
      case "action-governance-policy-read" -> governancePolicyDetailSurface(actor, null, correlationId);
      case "action-governance-policy-draft-proposal", "action-governance-policy-submit-proposal" -> governancePolicyProposalSurface(actor, correlationId, "draft", "Draft proposal is inert until review, simulation, approval, and activation.");
      case "action-governance-policy-simulate", "action-simulate-policy" -> governancePolicySimulationSurface(actor, null, correlationId);
      case "action-governance-policy-decide" -> governancePolicyDecisionSurface(actor, null, correlationId);
      case "action-governance-policy-activate", "action-commit-policy" -> governancePolicyActivationBlockedSurface(actor, correlationId);
      case "action-governance-policy-rollback" -> governancePolicyRollbackBlockedSurface(actor, correlationId);
      case "action-governance-policy-start-impact-analysis", "action-governance-policy-read-impact-analysis", "action-governance-policy-cancel-impact-analysis" -> governancePolicyImpactAnalysisBlockedSurface(actor, correlationId);
      case "action-governance-policy-accept-impact-result", "action-governance-policy-reject-impact-result", "action-governance-policy-request-impact-changes" -> governancePolicyImpactAnalysisBlockedSurface(actor, correlationId);
      case "action-display-agent-catalog" -> agentAdminCatalogSurface(actor, correlationId);
      case "action-open-agent-detail" -> agentAdminDetailSurface(actor, correlationId);
      case "action-activate-agent-definition" -> agentLifecycleConfirmationSurface(actor, correlationId, "activate", false);
      case "action-deactivate-agent-definition" -> agentLifecycleConfirmationSurface(actor, correlationId, "deactivate", false);
      case "action-import-agent-seed-defaults" -> agentSeedImportConfirmationSurface(actor, correlationId, 0, 0);
      case "action-propose-prompt-diff" -> agentPromptGovernanceSurface(actor, correlationId);
      case "action-test-agent-prompt" -> agentTestConsoleSurface(actor, correlationId);
      case "action-approve-skill-manifest" -> agentSkillManifestSurface(actor, correlationId);
      case "action-simulate-tool-boundary" -> agentToolBoundarySurface(actor, correlationId);
      case "action-manage-model-ref" -> agentModelRefsSurface(actor, correlationId);
      case "action-list-agent-seed-material" -> agentSeedMaterialSurface(actor, correlationId);
      case "action-agentadmin-start-prompt-risk-review", "action-agentadmin-read-prompt-risk-review", "action-agentadmin-cancel-prompt-risk-review", "action-agentadmin-accept-prompt-risk-review-result", "action-agentadmin-reject-prompt-risk-review-result" -> agentPromptRiskReviewEmptySurface(actor, correlationId);
      case "action-user-admin-show-saas-owner-admins" -> saasOwnerAdminsSurface(actor, correlationId);
      case "action-open-saas-owner-admin-invitation-create" -> saasOwnerAdminInvitationCreateSurface(actor, correlationId);
      case "action-display-organization-admin", "action-user-admin-show-organizations" -> organizationAdminSurface(actor, correlationId);
      case "action-open-organization-admin-invitation-create" -> organizationAdminInvitationCreateSurface(actor, correlationId);
      case "action-user-admin-show-customers" -> customerDirectorySurface(actor, correlationId);
      case "action-customer-read" -> customerDetailSurface(actor, correlationId);
      case "action-open-customer-create" -> customerCreateSurface(actor, correlationId);
      case "action-open-customer-rename" -> customerRenameSurface(actor, correlationId);
      case "action-open-customer-suspend" -> customerSuspendSurface(actor, correlationId);
      case "action-open-customer-reactivate" -> customerReactivateSurface(actor, correlationId);
      case "action-user-admin-show-customer-admins" -> customerAdminsSurface(actor, correlationId);
      case "action-open-customer-admin-invitation-create" -> customerAdminInvitationCreateSurface(actor, correlationId);
      case "action-customer-admin-invite" -> invitationDetailSurface(actor, null, correlationId);
      case "action-customer-create", "action-customer-rename", "action-customer-suspend", "action-customer-reactivate" -> customerDetailSurface(actor, correlationId);
      case "action-open-organization-create" -> organizationCreateSurface(actor, correlationId);
      case "action-open-organization-rename" -> organizationRenameSurface(actor, correlationId);
      case "action-open-organization-suspend" -> organizationSuspendSurface(actor, correlationId);
      case "action-open-organization-reactivate" -> organizationReactivateSurface(actor, correlationId);
      case "action-display-user-detail", "action-replace-membership-role", "action-useradmin-preview-role-change", "action-useradmin-change-member-roles", "action-useradmin-disable-account", "action-useradmin-reactivate-account" -> detailSurface(actor, correlationId);
      case "action-open-useradmin-invitation-create" -> invitationCreateSurface(actor, null, correlationId);
      case "action-display-invitation-detail", "action-invite-user" -> invitationDetailSurface(actor, null, correlationId);
      case "action-open-useradmin-invitation-resend-confirmation" -> invitationResendConfirmationSurface(actor, null, correlationId);
      case "action-open-useradmin-invitation-revoke-confirmation" -> invitationRevokeConfirmationSurface(actor, null, correlationId);
      case "action-useradmin-resend-invitation", "action-useradmin-revoke-invitation" -> invitationDetailSurface(actor, null, correlationId);
      case "action-open-useradmin-membership-status-confirmation" -> membershipStatusConfirmationSurface(actor, null, correlationId);
      case "action-open-useradmin-support-access-grant" -> supportAccessGrantSurface(actor, null, correlationId);
      case "action-open-useradmin-support-access-revoke-confirmation" -> supportAccessRevokeConfirmationSurface(actor, null, correlationId);
      case "action-open-useradmin-identity-exception-review", "action-useradmin-request-identity-relink", "action-useradmin-read-identity-relink", "action-useradmin-approve-identity-relink", "action-useradmin-deny-identity-relink", "action-useradmin-complete-identity-relink" -> identityExceptionReviewSurface(actor, null, correlationId);
      case "action-useradmin-disable-member", "action-useradmin-reactivate-member", "action-useradmin-permanently-remove-user", "action-useradmin-read-support-access", "action-useradmin-grant-support-access", "action-useradmin-revoke-support-access", "action-useradmin-extend-support-access", "action-display-user-list", "action-user-admin-show-users" -> listSurface(actor, correlationId);
      case "action-useradmin-start-access-review", "action-useradmin-read-access-review", "action-useradmin-cancel-access-review", "action-useradmin-accept-access-review-result", "action-useradmin-reject-access-review-result" -> accessReviewBlockedSurface(actor, correlationId);
      default -> dashboardSurface(actor, correlationId);
    };
  }

  private SurfaceAction actionById(String actionId) {
    return List.of(showDashboardAction(), showNotificationCenterAction(), markNotificationReadAction(), dismissNotificationAction(), archiveNotificationAction(), snoozeNotificationAction(), updateNotificationPreferencesAction(), showProfileAction(), showSettingsAction(), showContextAction(), selectContextAction(), updateProfileAction(), updateSettingsAction(), signOutAction(), startPersonalAttentionDigestAction(), readPersonalAttentionDigestAction(), cancelPersonalAttentionDigestAction(), acceptPersonalAttentionDigestAction(), rejectPersonalAttentionDigestAction(), openUserAdminAction(), openAgentAdminAction(), openGovernancePolicyAction(), showSaasOwnerAdminsAction(), openSaasOwnerAdminInvitationCreateAction(), displayOrganizationAdminAction(), showOrganizationsAction(), openOrganizationCreateAction(), openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), organizationListAction(), organizationReadAction(), organizationCreateAction(), organizationRenameAction(), organizationSuspendAction(), organizationReactivateAction(), openOrganizationAdminInvitationCreateAction(), showCustomersAction(), customerReadAction(), openCustomerCreateAction(), openCustomerRenameAction(), openCustomerSuspendAction(), openCustomerReactivateAction(), customerCreateAction(), customerRenameAction(), customerSuspendAction(), customerReactivateAction(), showCustomerAdminsAction(), openCustomerAdminInvitationCreateAction(), customerAdminInviteAction(), displayListAction(), showUsersAction(), displayDetailAction(), displayInvitationDetailAction(), openInvitationCreateAction(), openInvitationResendConfirmationAction(), openInvitationRevokeConfirmationAction(), openMembershipStatusConfirmationAction(), openSupportAccessGrantAction(), openSupportAccessRevokeConfirmationAction(), openIdentityExceptionReviewAction(), requestIdentityRelinkAction(), readIdentityRelinkAction(), approveIdentityRelinkAction(), denyIdentityRelinkAction(), completeIdentityRelinkAction(), inviteAction(), resendInvitationAction(), revokeInvitationAction(), updateMemberStatusAction(), reactivateMemberStatusAction(), permanentlyRemoveUserAction(), disableAccountAction(), reactivateAccountAction(), readSupportAccessAction(), grantSupportAccessAction(), revokeSupportAccessAction(), extendSupportAccessAction(), previewRoleChangeAction(), changeMemberRolesAction(), startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), acceptAccessReviewResultAction(), rejectAccessReviewResultAction(), deniedReplaceRoleAction(), traceAction(), openAuditAction(), auditTraceSearchAction(), auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSummaryTaskBlockedAction(), governanceDashboardAction(), governanceListPoliciesAction(), governanceReadPolicyAction(), governanceDraftProposalAction(), governanceSubmitProposalAction(), governanceSimulateProposalAction(), governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), governanceOutcomeNoteAction(), governanceStartImpactAnalysisAction(), governanceReadImpactAnalysisAction(), governanceCancelImpactAnalysisAction(), governanceAcceptImpactResultAction(), governanceRejectImpactResultAction(), governanceRequestImpactChangesAction(), simulatePolicyAction(), commitPolicyAction(), displayAgentAdminDashboardAction(), displayAgentCatalogAction(), openAgentDetailAction(), activateAgentDefinitionAction(), deactivateAgentDefinitionAction(), importAgentSeedDefaultsAction(), proposePromptDiffAction(), testPromptAction(), approveSkillManifestAction(), submitBehaviorChangeAction(), rejectBehaviorChangeAction(), activateBehaviorChangeAction(), cancelBehaviorChangeAction(), rollbackBehaviorChangeAction(), simulateToolBoundaryAction(), manageModelRefAction(), listAgentSeedMaterialAction(), startPromptRiskReviewAction(), readPromptRiskReviewAction(), cancelPromptRiskReviewAction(), acceptPromptRiskReviewAction(), rejectPromptRiskReviewAction(), openAgentTraceAction()).stream().filter(action -> actionId.equals(action.actionId())).findFirst().orElse(null);
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
  private SurfaceAction openAgentAdminAction() { return new SurfaceAction("action-open-agent-admin", "Open Agent Admin", "surface-request", browserToolId("action-open-agent-admin"), governedToolId(MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY), MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY, "schema.my-account.open-workstream.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-dashboard", "deep-link"), new Audit("MyAccountOpenAgentAdminRequested", true)); }
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

  private SurfaceAction showSaasOwnerAdminsAction() { return new SurfaceAction("action-user-admin-show-saas-owner-admins", "Show SaaS Owner Admins", "surface-request", "user-admin.show-saas-owner-admins", "manage-saas-owner-admins", SAAS_OWNER_ADMIN_LIST_CAPABILITY, "schema.saas-owner-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-saas-owner-admins", "inline"), new Audit("SaasOwnerAdminsDisplayed", true)); }
  private SurfaceAction openSaasOwnerAdminInvitationCreateAction() { return new SurfaceAction("action-open-saas-owner-admin-invitation-create", "Invite SaaS Owner Admin", "surface-request", "user-admin.open-saas-owner-admin-invite", "manage-saas-owner-admins", SAAS_OWNER_ADMIN_INVITE_CAPABILITY, "schema.saas-owner-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-saas-owner-admin-invitation-create", "inline"), new Audit("SaasOwnerAdminInvitationCreateDisplayed", true)); }
  private SurfaceAction displayOrganizationAdminAction() { return new SurfaceAction("action-display-organization-admin", "Show organizations", "surface-request", browserToolId("action-display-organization-admin"), governedToolId(SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY), SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, "schema.organization-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-directory", "inline"), new Audit("OrganizationDirectoryDisplayed", true)); }
  private SurfaceAction showOrganizationsAction() { return new SurfaceAction("action-user-admin-show-organizations", "Show organizations", "surface-request", "user-admin.show-organizations", "manage-organizations", SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, "schema.organization-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-directory", "inline"), new Audit("OrganizationDirectoryDisplayed", true)); }
  private SurfaceAction openOrganizationCreateAction() { return new SurfaceAction("action-open-organization-create", "Open Organization create form", "surface-request", browserToolId("action-open-organization-create"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-create", "inline"), new Audit("OrganizationCreateFormDisplayed", true)); }
  private SurfaceAction openOrganizationRenameAction() { return new SurfaceAction("action-open-organization-rename", "Open Organization rename form", "surface-request", browserToolId("action-open-organization-rename"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.rename.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-rename", "inline"), new Audit("OrganizationRenameFormDisplayed", true)); }
  private SurfaceAction openOrganizationSuspendAction() { return new SurfaceAction("action-open-organization-suspend", "Open Organization suspend confirmation", "surface-request", browserToolId("action-open-organization-suspend"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.suspend.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-suspend-confirmation", "inline"), new Audit("OrganizationSuspendConfirmationDisplayed", true)); }
  private SurfaceAction openOrganizationReactivateAction() { return new SurfaceAction("action-open-organization-reactivate", "Open Organization reactivate confirmation", "surface-request", browserToolId("action-open-organization-reactivate"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.reactivate.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-reactivate-confirmation", "inline"), new Audit("OrganizationReactivateConfirmationDisplayed", true)); }
  private SurfaceAction organizationListAction() { return new SurfaceAction("action-organization-list", "Refresh Organizations", "read", browserToolId("action-organization-list"), governedToolId(SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY), SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, "schema.organization-admin.list.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-directory", "inline"), new Audit("OrganizationListRequested", true)); }
  private SurfaceAction organizationReadAction() { return new SurfaceAction("action-organization-read", "Read Organization", "read", browserToolId("action-organization-read"), governedToolId(SAAS_OWNER_ORGANIZATION_READ_CAPABILITY), SAAS_OWNER_ORGANIZATION_READ_CAPABILITY, "schema.organization-admin.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationReadRequested", true)); }
  private SurfaceAction organizationCreateAction() { return new SurfaceAction("action-organization-create", "Create Organization", "command", browserToolId("action-organization-create"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationCreateRequested", true)); }
  private SurfaceAction organizationRenameAction() { return new SurfaceAction("action-organization-rename", "Rename Organization", "command", browserToolId("action-organization-rename"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.rename.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationRenameRequested", true)); }
  private SurfaceAction organizationSuspendAction() { return new SurfaceAction("action-organization-suspend", "Suspend Organization", "command", browserToolId("action-organization-suspend"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.suspend.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationSuspendRequested", true)); }
  private SurfaceAction organizationReactivateAction() { return new SurfaceAction("action-organization-reactivate", "Reactivate Organization", "command", browserToolId("action-organization-reactivate"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.reactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationReactivateRequested", true)); }
  private SurfaceAction openOrganizationAdminInvitationCreateAction() { return new SurfaceAction("action-open-organization-admin-invitation-create", "Invite Organization Admin", "surface-request", "user-admin.open-organization-admin-invite", "manage-organization-admins", SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY, "schema.organization-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-admin-invitation-create", "inline"), new Audit("OrganizationAdminInvitationCreateDisplayed", true)); }
  private SurfaceAction showCustomersAction() { return new SurfaceAction("action-user-admin-show-customers", "Show customers", "surface-request", "user-admin.show-customers", "manage-customers", TENANT_CUSTOMER_LIST_CAPABILITY, "schema.customer-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-directory", "inline"), new Audit("CustomerDirectoryDisplayed", true)); }
  private SurfaceAction customerReadAction() { return new SurfaceAction("action-customer-read", "Read Customer", "read", "user-admin.read-customer", "manage-customers", TENANT_CUSTOMER_READ_CAPABILITY, "schema.customer-admin.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerReadRequested", true)); }
  private SurfaceAction openCustomerCreateAction() { return new SurfaceAction("action-open-customer-create", "Create Customer", "surface-request", "user-admin.open-customer-create", "manage-customers", TENANT_CUSTOMER_CREATE_CAPABILITY, "schema.customer-admin.create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-create", "inline"), new Audit("CustomerCreateFormDisplayed", true)); }
  private SurfaceAction openCustomerRenameAction() { return new SurfaceAction("action-open-customer-rename", "Rename Customer", "surface-request", "user-admin.open-customer-rename", "manage-customers", TENANT_CUSTOMER_RENAME_CAPABILITY, "schema.customer-admin.rename.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-rename", "inline"), new Audit("CustomerRenameFormDisplayed", true)); }
  private SurfaceAction openCustomerSuspendAction() { return new SurfaceAction("action-open-customer-suspend", "Open Customer suspend confirmation", "surface-request", "user-admin.open-customer-suspend", "manage-customers", TENANT_CUSTOMER_SUSPEND_CAPABILITY, "schema.customer-admin.suspend.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-suspend-confirmation", "inline"), new Audit("CustomerSuspendConfirmationDisplayed", true)); }
  private SurfaceAction openCustomerReactivateAction() { return new SurfaceAction("action-open-customer-reactivate", "Open Customer reactivate confirmation", "surface-request", "user-admin.open-customer-reactivate", "manage-customers", TENANT_CUSTOMER_REACTIVATE_CAPABILITY, "schema.customer-admin.reactivate.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-reactivate-confirmation", "inline"), new Audit("CustomerReactivateConfirmationDisplayed", true)); }
  private SurfaceAction customerCreateAction() { return new SurfaceAction("action-customer-create", "Create Customer", "command", "user-admin.create-customer", "manage-customers", TENANT_CUSTOMER_CREATE_CAPABILITY, "schema.customer-admin.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerCreateRequested", true)); }
  private SurfaceAction customerRenameAction() { return new SurfaceAction("action-customer-rename", "Rename Customer", "command", "user-admin.rename-customer", "manage-customers", TENANT_CUSTOMER_RENAME_CAPABILITY, "schema.customer-admin.rename.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerRenameRequested", true)); }
  private SurfaceAction customerSuspendAction() { return new SurfaceAction("action-customer-suspend", "Suspend Customer", "command", "user-admin.suspend-customer", "manage-customers", TENANT_CUSTOMER_SUSPEND_CAPABILITY, "schema.customer-admin.suspend.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerSuspendRequested", true)); }
  private SurfaceAction customerReactivateAction() { return new SurfaceAction("action-customer-reactivate", "Reactivate Customer", "command", "user-admin.reactivate-customer", "manage-customers", TENANT_CUSTOMER_REACTIVATE_CAPABILITY, "schema.customer-admin.reactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerReactivateRequested", true)); }
  private SurfaceAction showCustomerAdminsAction() { return new SurfaceAction("action-user-admin-show-customer-admins", "Show Customer Admins", "surface-request", "user-admin.show-customer-admins", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_LIST_CAPABILITY, "schema.customer-admins.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-admins", "inline"), new Audit("CustomerAdminsDisplayed", true)); }
  private SurfaceAction openCustomerAdminInvitationCreateAction() { return new SurfaceAction("action-open-customer-admin-invitation-create", "Invite Customer Admin", "surface-request", "user-admin.open-customer-admin-invite", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_INVITE_CAPABILITY, "schema.customer-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-admin-invitation-create", "inline"), new Audit("CustomerAdminInvitationCreateDisplayed", true)); }
  private SurfaceAction customerAdminInviteAction() { return new SurfaceAction("action-customer-admin-invite", "Create Customer Admin invitation", "command", "user-admin.invite-customer-admin", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_INVITE_CAPABILITY, "schema.customer-admin.invitation-create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("CustomerAdminInvitationRequested", true)); }
  private SurfaceAction displayListAction() { return new SurfaceAction("action-display-user-list", "Show users", "read", browserToolId("action-display-user-list"), governedToolId(USERADMIN_LIST_MEMBERS), USERADMIN_LIST_MEMBERS, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminListDisplayed", true)); }
  private SurfaceAction showUsersAction() { return new SurfaceAction("action-user-admin-show-users", "Show users", "read", "user-admin.show-users", "search-user-directory", USERADMIN_LIST_MEMBERS, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminBranchReturnDisplayed", true)); }
  private SurfaceAction displayDetailAction() { return new SurfaceAction("action-display-user-detail", "View user", "read", browserToolId("action-display-user-detail"), governedToolId(USERADMIN_LIST_MEMBERS), USERADMIN_LIST_MEMBERS, "schema.user-admin.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminDetailDisplayed", true)); }
  private SurfaceAction displayInvitationDetailAction() { return new SurfaceAction("action-display-invitation-detail", "View invitation", "read", browserToolId("action-display-invitation-detail"), governedToolId(USERADMIN_LIST_INVITATIONS), USERADMIN_LIST_INVITATIONS, "schema.user-admin.invitation-detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationDetailDisplayed", true)); }
  private SurfaceAction openInvitationCreateAction() { return new SurfaceAction("action-open-useradmin-invitation-create", "Invite user", "surface-request", browserToolId("action-open-useradmin-invitation-create"), governedToolId(USERADMIN_SEND_INVITATION), USERADMIN_SEND_INVITATION, "schema.user-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-create", "inline"), new Audit("InvitationCreateFormDisplayed", true)); }
  private SurfaceAction openInvitationResendConfirmationAction() { return new SurfaceAction("action-open-useradmin-invitation-resend-confirmation", "Open resend confirmation", "surface-request", browserToolId("action-open-useradmin-invitation-resend-confirmation"), governedToolId(USERADMIN_RESEND_INVITATION), USERADMIN_RESEND_INVITATION, "schema.user-admin.invitation-resend.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-resend-confirmation", "inline"), new Audit("InvitationResendConfirmationDisplayed", true)); }
  private SurfaceAction openInvitationRevokeConfirmationAction() { return new SurfaceAction("action-open-useradmin-invitation-revoke-confirmation", "Open revoke confirmation", "surface-request", browserToolId("action-open-useradmin-invitation-revoke-confirmation"), governedToolId(USERADMIN_REVOKE_INVITATION), USERADMIN_REVOKE_INVITATION, "schema.user-admin.invitation-revoke.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-revoke-confirmation", "inline"), new Audit("InvitationRevokeConfirmationDisplayed", true)); }
  private SurfaceAction openMembershipStatusConfirmationAction() { return new SurfaceAction("action-open-useradmin-membership-status-confirmation", "Open status confirmation", "surface-request", browserToolId("action-open-useradmin-membership-status-confirmation"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.membership-status.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-membership-status-confirmation", "inline"), new Audit("MembershipStatusConfirmationDisplayed", true)); }
  private SurfaceAction openSupportAccessGrantAction() { return new SurfaceAction("action-open-useradmin-support-access-grant", "Open support access grant", "surface-request", browserToolId("action-open-useradmin-support-access-grant"), governedToolId(USERADMIN_SUPPORT_ACCESS_GRANT), USERADMIN_SUPPORT_ACCESS_GRANT, "schema.user-admin.support-access-grant.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-support-access-grant", "inline"), new Audit("SupportAccessGrantFormDisplayed", true)); }
  private SurfaceAction openSupportAccessRevokeConfirmationAction() { return new SurfaceAction("action-open-useradmin-support-access-revoke-confirmation", "Open support access revoke confirmation", "surface-request", browserToolId("action-open-useradmin-support-access-revoke-confirmation"), governedToolId(USERADMIN_SUPPORT_ACCESS_REVOKE), USERADMIN_SUPPORT_ACCESS_REVOKE, "schema.user-admin.support-access-revoke.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-support-access-revoke-confirmation", "inline"), new Audit("SupportAccessRevokeConfirmationDisplayed", true)); }
  private SurfaceAction openIdentityExceptionReviewAction() { return new SurfaceAction("action-open-useradmin-identity-exception-review", "Open identity exception review", "surface-request", browserToolId("action-open-useradmin-identity-exception-review"), governedToolId(USERADMIN_IDENTITY_RELINK_READ), USERADMIN_IDENTITY_RELINK_READ, "schema.user-admin.identity-exception.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityExceptionReviewDisplayed", true)); }
  private SurfaceAction requestIdentityRelinkAction() { return new SurfaceAction("action-useradmin-request-identity-relink", "Request identity recovery", "command", browserToolId("action-useradmin-request-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_REQUEST), USERADMIN_IDENTITY_RELINK_REQUEST, "schema.user-admin.identity-relink.request.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkRequested", true)); }
  private SurfaceAction readIdentityRelinkAction() { return new SurfaceAction("action-useradmin-read-identity-relink", "Refresh identity recovery", "read", browserToolId("action-useradmin-read-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_READ), USERADMIN_IDENTITY_RELINK_READ, "schema.user-admin.identity-relink.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkRead", true)); }
  private SurfaceAction approveIdentityRelinkAction() { return new SurfaceAction("action-useradmin-approve-identity-relink", "Approve identity recovery", "approval", browserToolId("action-useradmin-approve-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_APPROVE), USERADMIN_IDENTITY_RELINK_APPROVE, "schema.user-admin.identity-relink.approve.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkApproved", true)); }
  private SurfaceAction denyIdentityRelinkAction() { return new SurfaceAction("action-useradmin-deny-identity-relink", "Deny identity recovery", "approval", browserToolId("action-useradmin-deny-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_DENY), USERADMIN_IDENTITY_RELINK_DENY, "schema.user-admin.identity-relink.deny.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkDenied", true)); }
  private SurfaceAction completeIdentityRelinkAction() { return new SurfaceAction("action-useradmin-complete-identity-relink", "Complete identity recovery", "command", browserToolId("action-useradmin-complete-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_COMPLETE), USERADMIN_IDENTITY_RELINK_COMPLETE, "schema.user-admin.identity-relink.complete.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkCompleted", true)); }
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
  private SurfaceAction auditTraceExportRequestAction() { return new SurfaceAction("action-audit-trace-request-redacted-export", "Request redacted export", "approval", browserToolId("action-audit-trace-request-redacted-export"), governedToolId(AUDIT_TRACE_EXPORT_REQUEST_CAPABILITY), AUDIT_TRACE_EXPORT_REQUEST_CAPABILITY, "schema.audit-trace.export-request.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface("decision-card", "surface-audit-trace-export-request", "inline"), new Audit("AuditTraceRedactedExportRequested", true)); }
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
  private SurfaceAction governanceOutcomeNoteAction() { return new SurfaceAction("action-governance-policy-outcome-note", "Add outcome note", "command", browserToolId("action-governance-policy-outcome-note"), governedToolId(GOVERNANCE_OUTCOMES_RECORD_CAPABILITY), GOVERNANCE_OUTCOMES_RECORD_CAPABILITY, "schema.governance-policy.outcome-note.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-outcome", "inline"), new Audit("GovernancePolicyOutcomeNoteRecorded", true)); }
  private SurfaceAction governanceStartImpactAnalysisAction() { return new SurfaceAction("action-governance-policy-start-impact-analysis", "Start impact analysis", "workflow", browserToolId("action-governance-policy-start-impact-analysis"), governedToolId(GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY), GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY, "schema.governance-policy.impact-analysis.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-impact-analysis-task", "inline"), new Audit("GovernancePolicyImpactAnalysisStartedOrBlocked", true)); }
  private SurfaceAction governanceReadImpactAnalysisAction() { return new SurfaceAction("action-governance-policy-read-impact-analysis", "Read impact analysis", "read", browserToolId("action-governance-policy-read-impact-analysis"), governedToolId(GOVERNANCE_POLICY_ANALYSIS_READ_CAPABILITY), GOVERNANCE_POLICY_ANALYSIS_READ_CAPABILITY, "schema.governance-policy.impact-analysis.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-impact-analysis-task", "inline"), new Audit("GovernancePolicyImpactAnalysisRead", true)); }
  private SurfaceAction governanceCancelImpactAnalysisAction() { return new SurfaceAction("action-governance-policy-cancel-impact-analysis", "Cancel impact analysis", "command", browserToolId("action-governance-policy-cancel-impact-analysis"), governedToolId(GOVERNANCE_POLICY_ANALYSIS_CANCEL_CAPABILITY), GOVERNANCE_POLICY_ANALYSIS_CANCEL_CAPABILITY, "schema.governance-policy.impact-analysis.cancel.v1", true, false, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-governance-policy-impact-analysis-task", "inline"), new Audit("GovernancePolicyImpactAnalysisCancelled", true)); }
  private SurfaceAction governanceAcceptImpactResultAction() { return new SurfaceAction("action-governance-policy-accept-impact-result", "Accept advisory impact result", "approval", browserToolId("action-governance-policy-accept-impact-result"), governedToolId(GOVERNANCE_POLICY_ANALYSIS_ACCEPT_CAPABILITY), GOVERNANCE_POLICY_ANALYSIS_ACCEPT_CAPABILITY, "schema.governance-policy.impact-analysis.accept.v1", true, true, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-governance-policy-impact-analysis-result", "inline"), new Audit("GovernancePolicyImpactResultAccepted", true)); }
  private SurfaceAction governanceRejectImpactResultAction() { return new SurfaceAction("action-governance-policy-reject-impact-result", "Reject advisory impact result", "approval", browserToolId("action-governance-policy-reject-impact-result"), governedToolId(GOVERNANCE_POLICY_ANALYSIS_REJECT_CAPABILITY), GOVERNANCE_POLICY_ANALYSIS_REJECT_CAPABILITY, "schema.governance-policy.impact-analysis.reject.v1", true, true, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-governance-policy-impact-analysis-result", "inline"), new Audit("GovernancePolicyImpactResultRejected", true)); }
  private SurfaceAction governanceRequestImpactChangesAction() { return new SurfaceAction("action-governance-policy-request-impact-changes", "Request impact analysis changes", "approval", browserToolId("action-governance-policy-request-impact-changes"), governedToolId(GOVERNANCE_POLICY_ANALYSIS_REQUEST_CHANGES_CAPABILITY), GOVERNANCE_POLICY_ANALYSIS_REQUEST_CHANGES_CAPABILITY, "schema.governance-policy.impact-analysis.request-changes.v1", true, true, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-governance-policy-impact-analysis-result", "inline"), new Audit("GovernancePolicyImpactChangesRequested", true)); }
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

  private SurfaceAction displayAgentAdminDashboardAction() { return new SurfaceAction("action-display-agent-admin-dashboard", "Open Agent Admin dashboard", "read", browserToolId("action-display-agent-admin-dashboard"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-dashboard", "inline"), new Audit("AgentAdminDashboardDisplayed", true)); }
  private SurfaceAction displayAgentCatalogAction() { return new SurfaceAction("action-display-agent-catalog", "Display agent catalog", "read", browserToolId("action-display-agent-catalog"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction openAgentDetailAction() { return new SurfaceAction("action-open-agent-detail", "Open agent readiness detail", "read", browserToolId("action-open-agent-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, "schema.agent-definition.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction activateAgentDefinitionAction() { return new SurfaceAction("action-activate-agent-definition", "Review AgentDefinition activation", "surface-request", browserToolId("action-activate-agent-definition"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-activation-confirmation", "inline"), new Audit("AgentDefinitionActivationConfirmationDisplayed", true)); }
  private SurfaceAction deactivateAgentDefinitionAction() { return new SurfaceAction("action-deactivate-agent-definition", "Review AgentDefinition deactivation", "surface-request", browserToolId("action-deactivate-agent-definition"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.deactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-deactivation-confirmation", "inline"), new Audit("AgentDefinitionDeactivationConfirmationDisplayed", true)); }
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
  private SurfaceAction startPromptRiskReviewAction() { return new SurfaceAction("action-agentadmin-start-prompt-risk-review", "Start prompt-risk review", "workflow", browserToolId("action-agentadmin-start-prompt-risk-review"), governedToolId(AgentAdminPromptRiskReviewService.START_CAPABILITY), AgentAdminPromptRiskReviewService.START_CAPABILITY, "schema.agent-admin.prompt-risk-review.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewStarted", true)); }
  private SurfaceAction readPromptRiskReviewAction() { return new SurfaceAction("action-agentadmin-read-prompt-risk-review", "Read prompt-risk review", "read", browserToolId("action-agentadmin-read-prompt-risk-review"), governedToolId(AgentAdminPromptRiskReviewService.READ_CAPABILITY), AgentAdminPromptRiskReviewService.READ_CAPABILITY, "schema.agent-admin.prompt-risk-review.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewRead", true)); }
  private SurfaceAction cancelPromptRiskReviewAction() { return new SurfaceAction("action-agentadmin-cancel-prompt-risk-review", "Cancel prompt-risk review", "command", browserToolId("action-agentadmin-cancel-prompt-risk-review"), governedToolId(AgentAdminPromptRiskReviewService.CANCEL_CAPABILITY), AgentAdminPromptRiskReviewService.CANCEL_CAPABILITY, "schema.agent-admin.prompt-risk-review.cancel.v1", true, true, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewCancelled", true)); }
  private SurfaceAction acceptPromptRiskReviewAction() { return new SurfaceAction("action-agentadmin-accept-prompt-risk-review-result", "Accept prompt-risk review result", "approval", browserToolId("action-agentadmin-accept-prompt-risk-review-result"), governedToolId(AgentAdminPromptRiskReviewService.ACCEPT_RESULT_CAPABILITY), AgentAdminPromptRiskReviewService.ACCEPT_RESULT_CAPABILITY, "schema.agent-admin.prompt-risk-review.accept.v1", true, true, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewAccepted", true)); }
  private SurfaceAction rejectPromptRiskReviewAction() { return new SurfaceAction("action-agentadmin-reject-prompt-risk-review-result", "Reject prompt-risk review result", "approval", browserToolId("action-agentadmin-reject-prompt-risk-review-result"), governedToolId(AgentAdminPromptRiskReviewService.REJECT_RESULT_CAPABILITY), AgentAdminPromptRiskReviewService.REJECT_RESULT_CAPABILITY, "schema.agent-admin.prompt-risk-review.reject.v1", true, true, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewRejected", true)); }
  private SurfaceAction openAgentTraceAction() { return new SurfaceAction("action-open-agent-trace", "Open agent work trace", "trace", browserToolId("action-open-agent-trace"), governedToolId("audit.trace.read"), "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentWorkTraceOpened", true)); }

  private static final class InMemoryPromptRiskReviewTaskRepository implements PromptRiskReviewTaskRepository {
    private final Map<String, PromptRiskReviewTask> tasks = new ConcurrentHashMap<>();
    private final Map<String, String> idempotencyIndex = new ConcurrentHashMap<>();
    public Optional<PromptRiskReviewTask> find(String taskId) { return Optional.ofNullable(tasks.get(taskId)); }
    public Optional<PromptRiskReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { return Optional.ofNullable(idempotencyIndex.get(tenantId + ":" + accountId + ":" + idempotencyKey)).map(tasks::get); }
    public PromptRiskReviewTask save(PromptRiskReviewTask task) { tasks.put(task.taskId(), task); idempotencyIndex.put(task.tenantId() + ":" + task.startedByAccountId() + ":" + task.idempotencyKey(), task.taskId()); return task; }
  }

  private boolean isActionCapabilityVisible(AuthContextResolver.ResolvedMe actor, String capabilityId) {
    if (actor.selectedContext().capabilities().contains(capabilityId) || USER_ADMIN_CAPABILITY.equals(capabilityId)) return true;
    if (SAAS_OWNER_ADMIN_LIST_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ADMIN_INVITE_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ADMIN_MANAGE_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(SAAS_OWNER_USER_MANAGE_CAPABILITY);
    if (SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_READ_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(SAAS_OWNER_TENANT_READ_CAPABILITY);
    if (SAAS_OWNER_ORGANIZATION_MANAGE_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_ADMIN_MANAGE_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    if (capabilityId != null && (capabilityId.startsWith("tenant.customer") || capabilityId.startsWith("tenant.customer_admin"))) return actor.selectedContext().capabilities().contains(USER_ADMIN_CAPABILITY) || actor.selectedContext().capabilities().contains("tenant.customer.manage") || actor.selectedContext().capabilities().contains("tenant.user.manage");
    if (CORE_ACCESS_ME_CAPABILITY.equals(capabilityId)) return true;
    if (CORE_PROFILE_UPDATE_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY) || actor.selectedContext().capabilities().contains("profile.update");
    if (CORE_ACCESS_CONTEXT_SELECT_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY);
    if (capabilityId != null && capabilityId.startsWith("audit.trace.") && actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) return true;
    if (capabilityId != null && capabilityId.startsWith("governance.policy.") && actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_READ_CAPABILITY)) return actor.selectedContext().capabilities().contains(capabilityId);
    if (capabilityId != null && (capabilityId.startsWith("agent_admin.") || capabilityId.startsWith("agent."))) return actor.selectedContext().capabilities().contains(capabilityId);
    if (capabilityId != null && capabilityId.startsWith("notification.")) return actor.selectedContext().capabilities().contains(capabilityId);
    return capabilityId != null && (capabilityId.startsWith("USERADMIN_") || capabilityId.startsWith("user_admin."))
        && (actor.selectedContext().capabilities().contains(USER_ADMIN_CAPABILITY)
            || actor.selectedContext().capabilities().contains(SAAS_OWNER_USER_MANAGE_CAPABILITY)
            || actor.selectedContext().capabilities().contains("tenant.user.manage")
            || actor.selectedContext().capabilities().contains("tenant.user.read")
            || actor.selectedContext().capabilities().contains("customer.user.manage")
            || actor.selectedContext().capabilities().contains("customer.user.read"));
  }

  private String notificationIdInput(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) { var requested = stringInput(input, "notificationId", null); if (requested != null) return requested; return myAccountNotificationCenterData(actor, correlationId).items().stream().findFirst().map(NotificationItem::notificationId).orElse("missing-notification"); }
  private static String stringInput(Object input, String key, String fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return value; return fallback; }
  private static List<String> listStringInput(Object input, String key) {
    if (input instanceof Map<?, ?> map && map.get(key) instanceof List<?> values) return values.stream().filter(String.class::isInstance).map(String.class::cast).filter(value -> !value.isBlank()).toList();
    if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return List.of(value);
    return List.of();
  }
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
  private static List<FoundationRole> customerAdminRolesInput(Object input) {
    if (input instanceof Map<?, ?> map) {
      if (map.get("roles") instanceof List<?> roles && !roles.isEmpty()) return roles.stream().map(String::valueOf).map(FoundationRole::valueOf).toList();
      if (map.get("roles") instanceof String role && !role.isBlank()) return List.of(FoundationRole.valueOf(role));
      if (map.get("role") instanceof String role && !role.isBlank()) return List.of(FoundationRole.valueOf(role));
    }
    return List.of(FoundationRole.CUSTOMER_ADMIN);
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
  private static String workstreamEventTenantId(AuthContext authContext) {
    return authContext.scopeType() == ScopeType.SAAS_OWNER && (authContext.tenantId() == null || authContext.tenantId().isBlank())
        ? WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID
        : authContext.tenantId();
  }

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
