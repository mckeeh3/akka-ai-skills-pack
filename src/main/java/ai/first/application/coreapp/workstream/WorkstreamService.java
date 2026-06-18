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
import ai.first.application.coreapp.agentadmin.FailClosedPromptRiskAutonomousAgentRuntime;
import ai.first.application.coreapp.agentadmin.PromptRiskAutonomousAgentRuntime;
import ai.first.application.coreapp.agentadmin.PromptRiskReviewTaskRepository;
import ai.first.application.coreapp.audit.AuditTraceSummaryService;
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
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
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
import java.time.format.DateTimeParseException;
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
import java.util.stream.Stream;
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
  private static final String SAAS_OWNER_ORGANIZATION_RENAME_CAPABILITY = "saas_owner.organization.rename";
  private static final String SAAS_OWNER_ORGANIZATION_SUSPEND_CAPABILITY = "saas_owner.organization.suspend";
  private static final String SAAS_OWNER_ORGANIZATION_REACTIVATE_CAPABILITY = "saas_owner.organization.reactivate";
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
  private static final String AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY = AuditTraceSummaryService.START_CAPABILITY;
  private static final String AUDIT_TRACE_SUMMARY_TASK_REVIEW_CAPABILITY = AuditTraceSummaryService.REVIEW_CAPABILITY;
  private static final String AUDIT_TRACE_SUMMARY_TASK_ACCEPT_CAPABILITY = AuditTraceSummaryService.ACCEPT_CAPABILITY;
  private static final String AUDIT_TRACE_SUMMARY_TASK_REJECT_CAPABILITY = AuditTraceSummaryService.REJECT_CAPABILITY;
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
  private final AuditTraceSummaryService auditTraceSummaryService;
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
    this(meService, authContextResolver, userDirectoryView, invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, workstreamAgentRuntimeInvoker, workstreamLogRepository, accessReviewTaskRepository, auditTraceRepository, governancePolicyRepository, attentionService, attentionProducerService, workstreamEventPublisher, workstreamEventRepository, accessReviewAutonomousAgentRuntime, new InMemoryPromptRiskReviewTaskRepository(), new FailClosedPromptRiskAutonomousAgentRuntime(), notificationService);
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
      PromptRiskReviewTaskRepository promptRiskReviewTaskRepository,
      PromptRiskAutonomousAgentRuntime promptRiskAutonomousAgentRuntime,
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
    this.promptRiskReviewService = new AgentAdminPromptRiskReviewService(Objects.requireNonNull(promptRiskReviewTaskRepository), authContextResolver, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher, Objects.requireNonNull(promptRiskAutonomousAgentRuntime));
    this.agentRuntimeService = agentRuntimeService;
    this.workstreamAgentRuntimeInvoker = Objects.requireNonNull(workstreamAgentRuntimeInvoker);
    this.workstreamLogRepository = Objects.requireNonNull(workstreamLogRepository);
    this.workstreamEventRepository = workstreamEventRepository == null ? new EmptyWorkstreamEventRepository() : workstreamEventRepository;
    this.auditTraceService = new AuditTraceService(authContextResolver, Objects.requireNonNull(auditTraceRepository));
    this.auditTraceSummaryService = StarterSecurityComponents.auditTraceSummaryService();
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
      if (isUserAdminAction(request.actionId())) return userAdminSystemMessageResult(actor, request.actionId(), "idempotency-key-required", userAdminSafeDenialMessage("idempotency-key-required"), request.correlationId());
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
    } else if ("action-submit-saas-owner-admin-invitation".equals(request.actionId())) {
      var requestedRoles = rolesInput(request.input());
      if (requestedRoles.isEmpty()) requestedRoles = List.of(FoundationRole.SAAS_OWNER_ADMIN);
      if (!requestedRoles.equals(List.of(FoundationRole.SAAS_OWNER_ADMIN))) {
        result = new CapabilityActionResult("validation-error", "SaaS Owner Admin invitations may only request the SAAS_OWNER_ADMIN app-owner role.", request.correlationId(), List.of("trace-saas-owner-admin-invite-validation-" + stableSuffix(request.correlationId())), saasOwnerAdminInvitationCreateSurface(actor, request.correlationId()));
      } else {
        var invite = invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
            request.idempotencyKey(), ScopeType.SAAS_OWNER, actor.selectedContext().tenantId(), null,
            stringInput(request.input(), "email", "new-owner-admin@example.test"), stringInput(request.input(), "displayName", "New SaaS Owner Admin"),
            requestedRoles, Instant.now().plus(7, ChronoUnit.DAYS), stringInput(request.input(), "reason", "saas-owner-admin-invite"), request.correlationId()));
        result = invitationActionResult("accepted", "SaaS Owner Admin invitation queued through the backend outbox/provider boundary.", request.correlationId(), invite, actor);
      }
    } else if ("action-display-organization-admin".equals(request.actionId()) || "action-user-admin-show-organizations".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Directory loaded for SaaS Owner Organization lifecycle administration.", request.correlationId(), List.of("trace-organization-admin-" + stableSuffix(request.correlationId())), organizationAdminSurface(actor, request.correlationId()));
    } else if ("action-user-admin-show-organization-admins".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Admin directory loaded for the selected visible Organization.", request.correlationId(), List.of("trace-organization-admins-" + stableSuffix(request.correlationId())), organizationAdminsSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-organization-admin-invitation-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Admin invitation/bootstrap surface loaded.", request.correlationId(), List.of("trace-organization-admin-invite-" + stableSuffix(request.correlationId())), organizationAdminInvitationCreateSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-organization-admin-detail".equals(request.actionId()) || "action-open-organization-admin-invitation-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Admin detail loaded through backend-authoritative Organization Admin surface graph.", request.correlationId(), List.of("trace-organization-admin-detail-" + stableSuffix(request.correlationId())), organizationAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-submit-organization-admin-invitation".equals(request.actionId())) {
      var detail = readOrganizationDetail(actor, request.input(), request.correlationId());
      var requestedRoles = rolesInput(request.input());
      if (requestedRoles.isEmpty()) requestedRoles = List.of(FoundationRole.TENANT_ADMIN);
      if (!requestedRoles.equals(List.of(FoundationRole.TENANT_ADMIN))) {
        result = new CapabilityActionResult("validation-error", "Organization Admin invitations may only request the TENANT_ADMIN role for the selected Organization.", request.correlationId(), List.of("trace-organization-admin-invite-validation-" + stableSuffix(request.correlationId())), organizationAdminInvitationCreateSurface(actor, request.input(), request.correlationId()));
      } else {
        var organization = detail.organization();
        var invite = invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
            request.idempotencyKey(), ScopeType.TENANT, organization.organizationId(), null,
            stringInput(request.input(), "email", "new-organization-admin@example.test"), stringInput(request.input(), "displayName", "New Organization Admin"),
            requestedRoles, Instant.now().plus(7, ChronoUnit.DAYS), stringInput(request.input(), "reason", "organization-admin-invite"), request.correlationId()));
        result = organizationAdminInvitationActionResult("accepted", "Organization Admin invitation queued through the backend outbox/provider boundary for the selected Organization.", request.correlationId(), invite, actor, detail);
      }
    } else if ("action-user-admin-show-customers".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer Directory loaded for selected Organization/Tenant administration.", request.correlationId(), List.of("trace-customer-directory-" + stableSuffix(request.correlationId())), customerDirectorySurface(actor, request.input(), request.correlationId()));
    } else if ("action-customer-read".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer detail loaded through backend-authoritative Customer surface graph.", request.correlationId(), List.of("trace-customer-read-" + stableSuffix(request.correlationId())), customerDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer create surface loaded.", request.correlationId(), List.of("trace-customer-create-" + stableSuffix(request.correlationId())), customerCreateSurface(actor, request.correlationId()));
    } else if ("action-open-customer-rename".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer rename surface loaded.", request.correlationId(), List.of("trace-customer-rename-" + stableSuffix(request.correlationId())), customerRenameSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-suspend".equals(request.actionId()) || "action-open-customer-suspend-confirmation".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer suspend confirmation surface loaded.", request.correlationId(), List.of("trace-customer-suspend-" + stableSuffix(request.correlationId())), customerSuspendSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-reactivate".equals(request.actionId()) || "action-open-customer-reactivate-confirmation".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer reactivate confirmation surface loaded.", request.correlationId(), List.of("trace-customer-reactivate-" + stableSuffix(request.correlationId())), customerReactivateSurface(actor, request.input(), request.correlationId()));
    } else if ("action-user-admin-show-customer-admins".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer Admin directory loaded for the selected Customer.", request.correlationId(), List.of("trace-customer-admins-" + stableSuffix(request.correlationId())), customerAdminsSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-admin-invitation-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer Admin invitation/bootstrap surface loaded for the selected Customer.", request.correlationId(), List.of("trace-customer-admin-invite-" + stableSuffix(request.correlationId())), customerAdminInvitationCreateSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-customer-admin-detail".equals(request.actionId()) || "action-open-customer-admin-invitation-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Customer Admin detail loaded through backend-authoritative Customer surface graph.", request.correlationId(), List.of("trace-customer-admin-detail-" + stableSuffix(request.correlationId())), customerAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-customer-admin-invite".equals(request.actionId())) {
      var customer = activeCustomerInviteTargetDetail(actor, request.input(), request.correlationId());
      var requestedRoles = customerAdminRolesInput(request.input());
      if (!requestedRoles.equals(List.of(FoundationRole.CUSTOMER_ADMIN))) {
        result = new CapabilityActionResult("validation-error", "Customer Admin invitations may only request the CUSTOMER_ADMIN role for the selected Customer.", request.correlationId(), List.of("trace-customer-admin-invite-validation-" + stableSuffix(request.correlationId())), customerAdminInvitationCreateSurface(actor, request.input(), request.correlationId()));
      } else {
        var invite = invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
            request.idempotencyKey(), ScopeType.CUSTOMER, actor.selectedContext().tenantId(), customer.customer().customerId(),
            stringInput(request.input(), "email", "new-customer-admin@example.test"), stringInput(request.input(), "displayName", "New Customer Admin"),
            requestedRoles, Instant.now().plus(7, ChronoUnit.DAYS), stringInput(request.input(), "reason", "workstream-customer-admin-invite"), request.correlationId()));
        result = new CapabilityActionResult("accepted", "Customer Admin invitation queued for the selected Customer by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-customer-admin-invite-created-" + stableSuffix(invite.invitationId())), customerAdminInvitationDetailSurface(actor, customer, invite, request.correlationId()));
      }
    } else if (List.of("action-submit-customer-create", "action-customer-create", "action-submit-customer-rename", "action-customer-rename", "action-customer-suspend", "action-customer-reactivate").contains(request.actionId())) {
      var customerResult = runCustomerLifecycleAction(actor, request.actionId(), request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(customerResult.status(), customerResult.message(), request.correlationId(), customerResult.traceRefs(), customerDetailSurface(actor, customerResult.customer(), request.correlationId()));
    } else if ("action-organization-list".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization Directory refreshed through the canonical workstream action path.", request.correlationId(), List.of("trace-organization-list-" + stableSuffix(request.correlationId())), organizationDirectorySurface(actor, request.input(), request.correlationId()));
    } else if (request.actionId().startsWith("action-open-organization-")) {
      result = openOrganizationTaskSurface(actor, request.actionId(), request.input(), request.correlationId());
    } else if ("action-organization-read".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Organization detail loaded through backend-authoritative Organization surface graph.", request.correlationId(), List.of("trace-organization-read-" + stableSuffix(request.correlationId())), organizationSurface(actor, request.correlationId(), "surface-user-admin-organization-detail", "show-inspection", "Organization Detail", "user_admin.organization_detail.v1", withOrganizationBranchReturn(List.of(openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), showOrganizationAdminsAction(), openAuditAction(), openOrganizationAdminInvitationCreateAction())), readOrganizationDetail(actor, request.input(), request.correlationId())));
    } else if (List.of("action-submit-organization-create", "action-organization-create", "action-submit-organization-rename", "action-organization-rename", "action-organization-suspend", "action-organization-reactivate").contains(request.actionId())) {
      var organizationResult = runOrganizationLifecycleAction(actor, request.actionId(), request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(organizationResult.status(), organizationResult.message(), request.correlationId(), List.of(organizationResult.traceId()), organizationSurface(actor, request.correlationId(), "surface-user-admin-organization-detail", "show-inspection", "Organization Detail", "user_admin.organization_detail.v1", withOrganizationBranchReturn(List.of(openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), showOrganizationAdminsAction(), openAuditAction(), openOrganizationAdminInvitationCreateAction())), organizationResult.organization()));
    } else if ("action-display-user-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "User detail loaded.", request.correlationId(), List.of("trace-user-admin-detail-" + stableSuffix(stringInput(request.input(), "accountId", actor.account().accountId()))), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-display-invitation-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Invitation detail loaded.", request.correlationId(), List.of("trace-user-admin-invitation-" + stableSuffix(stringInput(request.input(), "invitationId", "latest"))), invitationDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-user-admin-invitation-create".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Invitation create surface loaded through the canonical User Admin create-form action.", request.correlationId(), List.of("trace-useradmin-invitation-create-" + stableSuffix(request.correlationId())), invitationCreateSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-user-admin-invitation-revoke-confirmation".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Invitation revoke confirmation loaded through the canonical User Admin destructive lifecycle action.", request.correlationId(), List.of("trace-useradmin-invitation-revoke-confirmation-" + stableSuffix(request.correlationId())), invitationRevokeConfirmationSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-user-admin-membership-status-confirmation".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Membership status confirmation loaded through the canonical User Admin destructive lifecycle action.", request.correlationId(), List.of("trace-useradmin-membership-status-confirmation-" + stableSuffix(request.correlationId())), membershipStatusConfirmationSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-user-admin-support-access-grant".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Support access grant create-form loaded through the canonical User Admin task action.", request.correlationId(), List.of("trace-useradmin-support-access-grant-" + stableSuffix(request.correlationId())), supportAccessGrantSurface(actor, request.input(), request.correlationId()));
    } else if (request.actionId().startsWith("action-open-useradmin-")) {
      result = openUserAdminTaskSurface(actor, request.actionId(), request.input(), request.correlationId());
    } else if ("action-invite-user".equals(request.actionId()) || "action-submit-user-admin-invitation".equals(request.actionId())) {
      var targetScope = "surface-user-admin-organization-admin-invitation-create".equals(request.surfaceId()) ? ScopeType.TENANT : actor.selectedContext().scopeType();
      var targetTenantId = targetScope == ScopeType.TENANT
          ? stringInput(request.input(), "tenantId", stringInput(request.input(), "organizationId", actor.selectedContext().tenantId()))
          : actor.selectedContext().tenantId();
      var targetCustomerId = targetScope == ScopeType.TENANT ? null : actor.selectedContext().customerId();
      var invite = invitationService.createInvitation(actor, new InvitationService.CreateInvitationRequest(
          request.idempotencyKey(), targetScope, targetTenantId, targetCustomerId,
          stringInput(request.input(), "email", "new-user@example.test"), stringInput(request.input(), "displayName", "New User"),
          rolesInput(request.input()), Instant.now().plus(7, ChronoUnit.DAYS), stringInput(request.input(), "reason", "workstream-invite"), request.correlationId()));
      result = invitationActionResult("accepted", "Invitation queued by backend-authoritative User Admin capability.", request.correlationId(), invite, actor);
    } else if ("action-useradmin-resend-invitation".equals(request.actionId())) {
      var invite = invitationService.resend(actor, stringInput(request.input(), "invitationId", latestInvitationId(actor)), request.idempotencyKey(), stringInput(request.input(), "reason", "workstream resend"), request.correlationId());
      result = invitationActionResult("accepted", "Invitation resend queued by backend-authoritative User Admin capability.", request.correlationId(), invite.invitationId(), actor);
    } else if ("action-useradmin-revoke-invitation".equals(request.actionId()) || "action-confirm-user-admin-invitation-revoke".equals(request.actionId())) {
      var invitationId = stringInput(request.input(), "invitationId", latestInvitationId(actor));
      var alreadyRevoked = invitationStatus(actor, invitationId).equals("REVOKED");
      var invite = invitationService.revoke(actor, invitationId, stringInput(request.input(), "reason", "workstream revoke"), request.correlationId());
      result = invitationActionResult(alreadyRevoked ? "no-op" : "accepted", "Invitation revoke processed by backend-authoritative User Admin capability.", request.correlationId(), invite.invitationId(), actor);
    } else if ("action-useradmin-preview-role-change".equals(request.actionId()) || "action-open-user-admin-role-change-preview".equals(request.actionId())) {
      var membershipId = rolePreviewMembershipId(actor, request.input());
      var proposedRoles = rolePreviewRoles(actor, request.input(), membershipId);
      var preview = userAdminService.previewRoleChange(actor, membershipId, proposedRoles, stringInput(request.input(), "reason", "workstream role preview"), request.correlationId());
      result = new CapabilityActionResult(preview.allowed() ? (preview.noOp() ? "no-op" : "accepted") : "denied", preview.message(), request.correlationId(), List.of(preview.traceId()), roleChangePreviewSurface(actor, preview, membershipId, proposedRoles, request.correlationId()));
    } else if ("action-useradmin-change-member-roles".equals(request.actionId()) || "action-commit-user-admin-role-change".equals(request.actionId())) {
      var changed = userAdminService.changeMemberRoles(actor, rolePreviewMembershipId(actor, request.input()), rolePreviewRoles(actor, request.input(), rolePreviewMembershipId(actor, request.input())), stringInput(request.input(), "reason", "workstream role change"), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(changed.status(), changed.message(), request.correlationId(), List.of(changed.traceId()), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-revise-user-admin-role-change".equals(request.actionId()) || "action-open-user-admin-user-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned to user detail through backend-authorized role-change branch navigation.", request.correlationId(), List.of("trace-useradmin-role-change-revise-" + stableSuffix(request.correlationId())), detailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-useradmin-disable-member".equals(request.actionId()) || "action-useradmin-reactivate-member".equals(request.actionId()) || "action-confirm-user-admin-membership-status-change".equals(request.actionId())) {
      var requestedStatus = stringInput(request.input(), "status", membershipActionDefaultStatus(request.actionId()));
      var targetStatus = membershipStatusInput(requestedStatus);
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
    } else if ("action-validate-user-admin-support-access-grant".equals(request.actionId())) {
      result = validateSupportAccessGrant(actor, request);
    } else if ("action-useradmin-grant-support-access".equals(request.actionId()) || "action-useradmin-revoke-support-access".equals(request.actionId()) || "action-confirm-user-admin-support-access-revoke".equals(request.actionId()) || "action-useradmin-extend-support-access".equals(request.actionId()) || "action-submit-user-admin-support-access-grant".equals(request.actionId())) {
      result = updateSupportAccess(actor, request);
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
      result = new CapabilityActionResult(prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "accepted" : "denied", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "No-side-effect Agent Admin test assembled governed prompt and loader traces; provider/model success is still fail-closed unless configured." : "No-side-effect Agent Admin test failed closed before model invocation.", request.correlationId(), List.of(prompt.traceId()), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-test-console-refresh".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Test console refreshed from backend-owned advisory runtime evidence; no behavior state changed.", request.correlationId(), List.of("trace-agent-test-console-refresh-" + stableSuffix(request.correlationId())), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-test-console-run".equals(request.actionId())) {
      var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, request.correlationId(), stringInput(request.input(), "prompt", "Run no-side-effect Agent Admin runtime readiness test.")));
      result = new CapabilityActionResult(prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "blocked_provider_or_runtime" : "denied", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "No-side-effect test recorded prompt/loader/boundary evidence, then failed closed because no configured provider/model smoke is available; no fixture/model-less success was counted." : "No-side-effect test was denied by backend runtime authorization before provider/model invocation.", request.correlationId(), List.of(prompt.traceId()), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-test-console-read-result".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Latest no-side-effect test result read without rerunning provider/tool work or changing behavior artifacts.", request.correlationId(), List.of("trace-agent-test-console-read-result-" + stableSuffix(request.correlationId())), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-test-console-open-proposal".equals(request.actionId())) {
      result = new CapabilityActionResult("denied", "Behavior proposal route is disabled until provider/runtime evidence exists; advisory output cannot activate artifacts directly.", request.correlationId(), List.of("trace-agent-test-console-proposal-disabled-" + stableSuffix(request.correlationId())), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-test-console-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Test-console trace opened with Agent Admin/Audit authorization and browser-safe redaction.", request.correlationId(), List.of("trace-agent-test-console-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-test-console-back-to-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned to backend-authorized managed-agent detail from the no-side-effect test console.", request.correlationId(), List.of("trace-agent-test-console-detail-return-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-prompt-governance-refresh".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Prompt governance refreshed from backend-owned redacted prompt evidence; no behavior state changed.", request.correlationId(), List.of("trace-agent-prompt-governance-refresh-" + stableSuffix(request.correlationId())), agentPromptGovernanceSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-governance-simulate".equals(request.actionId())) {
      var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, request.correlationId(), stringInput(request.input(), "prompt", "Simulate prompt-governance behavior without side effects.")));
      result = new CapabilityActionResult(prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "accepted" : "blocked_provider_or_runtime", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "Prompt-governance simulation assembled redacted governed runtime evidence with no production side effects." : "Prompt-governance simulation failed closed before provider/model invocation.", request.correlationId(), List.of(prompt.traceId()), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-governance-submit-review".equals(request.actionId())) {
      var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.PROMPT, "Submitted redacted Agent Admin prompt governance change. Backend authorization, approval, trace, and no-direct-activation boundaries remain mandatory.", List.of(), "Agent Admin prompt governance review submission", request.correlationId()));
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      result = new CapabilityActionResult(submitted.status() == BehaviorChangeProposal.Status.DENIED ? "denied" : "approval-required", "Prompt governance review " + submitted.proposalId() + " submitted to the behavior proposal surface; active prompt behavior remains unchanged.", request.correlationId(), List.of(submitted.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-governance-approve".equals(request.actionId())) {
      var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.PROMPT, "Approved redacted Agent Admin prompt governance change. Activation remains a separate backend lifecycle command.", List.of(), "Agent Admin prompt governance approval preview", request.correlationId()));
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      var approved = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW ? agentRuntimeService.approveProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), request.correlationId()) : submitted;
      result = new CapabilityActionResult(approved.status() == BehaviorChangeProposal.Status.APPROVED ? "approval-required" : "denied", "Prompt governance approval recorded as behavior proposal evidence only; activation remains blocked until a separate confirmation surface.", request.correlationId(), List.of(approved.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-governance-reject".equals(request.actionId())) {
      var reason = stringInput(request.input(), "reason", "");
      if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Prompt governance rejection requires a human-readable reason.", request.correlationId(), List.of("trace-agent-prompt-governance-reject-validation"), agentPromptGovernanceSurface(actor, request.correlationId()));
      } else {
        var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.PROMPT, "Rejected redacted Agent Admin prompt governance change.", List.of(), "Agent Admin prompt governance rejection preview", request.correlationId()));
        var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
        var rejected = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW || submitted.status() == BehaviorChangeProposal.Status.APPROVED ? agentRuntimeService.rejectProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), reason, request.correlationId()) : submitted;
        result = new CapabilityActionResult(rejected.status() == BehaviorChangeProposal.Status.REJECTED ? "accepted" : "denied", "Prompt governance rejection recorded through backend behavior proposal review; active behavior unchanged.", request.correlationId(), List.of(rejected.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-prompt-governance-open-risk-review".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Prompt-risk review status opened from prompt governance; model-backed review remains fail-closed until provider/runtime readiness exists.", request.correlationId(), List.of("trace-agent-prompt-governance-risk-review-" + stableSuffix(request.correlationId())), agentPromptRiskReviewEmptySurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-governance-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Prompt governance trace opened with Agent Admin/Audit authorization and browser-safe redaction.", request.correlationId(), List.of("trace-agent-prompt-governance-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-governance-back-to-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned to backend-authorized managed-agent detail from prompt governance.", request.correlationId(), List.of("trace-agent-prompt-governance-detail-return-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-skill-manifest-refresh".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Skill manifest review refreshed from backend-owned compact manifest and reference evidence; no behavior state changed.", request.correlationId(), List.of("trace-agent-skill-manifest-refresh-" + stableSuffix(request.correlationId())), agentSkillManifestSurface(actor, request.correlationId()));
    } else if ("action-agent-skill-manifest-simulate".equals(request.actionId())) {
      var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, request.correlationId(), stringInput(request.input(), "prompt", "Simulate skill/reference manifest behavior without side effects.")));
      result = new CapabilityActionResult(prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "accepted" : "blocked_provider_or_runtime", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "Skill manifest simulation assembled redacted governed runtime evidence with no production side effects." : "Skill manifest simulation failed closed before provider/model invocation.", request.correlationId(), List.of(prompt.traceId()), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-skill-manifest-submit-review".equals(request.actionId())) {
      var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.SKILL_MANIFEST, "Submitted redacted Agent Admin skill/reference manifest change. Backend authorization, approval, trace, and no-direct-activation boundaries remain mandatory.", List.of(), "Agent Admin skill manifest review submission", request.correlationId()));
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      result = new CapabilityActionResult(submitted.status() == BehaviorChangeProposal.Status.DENIED ? "denied" : "approval-required", "Skill manifest review " + submitted.proposalId() + " submitted to the behavior proposal surface; active manifest and reference behavior remain unchanged.", request.correlationId(), List.of(submitted.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-skill-manifest-approve".equals(request.actionId())) {
      var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.SKILL_MANIFEST, "Approved redacted Agent Admin skill/reference manifest change. Activation remains a separate backend lifecycle command.", List.of(), "Agent Admin skill manifest approval preview", request.correlationId()));
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      var approved = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW ? agentRuntimeService.approveProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), request.correlationId()) : submitted;
      result = new CapabilityActionResult(approved.status() == BehaviorChangeProposal.Status.APPROVED ? "approval-required" : "denied", "Skill manifest approval recorded as behavior proposal evidence only; activation remains blocked until a separate confirmation surface.", request.correlationId(), List.of(approved.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-skill-manifest-reject".equals(request.actionId())) {
      var reason = stringInput(request.input(), "reason", "");
      if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Skill manifest rejection requires a human-readable reason.", request.correlationId(), List.of("trace-agent-skill-manifest-reject-validation"), agentSkillManifestSurface(actor, request.correlationId()));
      } else {
        var proposal = agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.SKILL_MANIFEST, "Rejected redacted Agent Admin skill/reference manifest change.", List.of(), "Agent Admin skill manifest rejection preview", request.correlationId()));
        var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
        var rejected = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW || submitted.status() == BehaviorChangeProposal.Status.APPROVED ? agentRuntimeService.rejectProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), reason, request.correlationId()) : submitted;
        result = new CapabilityActionResult(rejected.status() == BehaviorChangeProposal.Status.REJECTED ? "accepted" : "denied", "Skill manifest rejection recorded through backend behavior proposal review; active manifest and reference behavior unchanged.", request.correlationId(), List.of(rejected.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-skill-manifest-open-tool-boundary".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Related tool-boundary review opened from skill manifest governance with ToolPermissionBoundary evidence preserved.", request.correlationId(), List.of("trace-agent-skill-manifest-tool-boundary-" + stableSuffix(request.correlationId())), agentToolBoundarySurface(actor, request.correlationId()));
    } else if ("action-agent-skill-manifest-open-model-refs".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Related model-reference inspection opened from skill manifest governance with provider credentials redacted.", request.correlationId(), List.of("trace-agent-skill-manifest-model-refs-" + stableSuffix(request.correlationId())), agentModelRefsSurface(actor, request.correlationId()));
    } else if ("action-agent-skill-manifest-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Skill manifest trace opened with Agent Admin/Audit authorization and browser-safe redaction.", request.correlationId(), List.of("trace-agent-skill-manifest-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-skill-manifest-back-to-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned to backend-authorized managed-agent detail from skill manifest governance.", request.correlationId(), List.of("trace-agent-skill-manifest-detail-return-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-tool-boundary-refresh".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Tool-boundary review refreshed from backend-owned ToolPermissionBoundary evidence; no behavior state changed.", request.correlationId(), List.of("trace-agent-tool-boundary-refresh-" + stableSuffix(request.correlationId())), agentToolBoundarySurface(actor, request.correlationId()));
    } else if ("action-agent-tool-boundary-simulate".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "No-side-effect tool-boundary simulation opened the backend runtime test console; ToolPermissionBoundary denials and provider fail-closed copy remain visible.", request.correlationId(), List.of("trace-agent-tool-boundary-simulate-" + stableSuffix(request.correlationId())), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-tool-boundary-submit-review".equals(request.actionId())) {
      var proposal = proposeToolBoundaryChange(actor, request.correlationId(), "Submitted redacted Agent Admin tool-boundary change for human review.");
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      result = new CapabilityActionResult(submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW ? "approval-required" : "denied", "Tool-boundary review submitted as behavior proposal evidence only; active ToolPermissionBoundary grants remain unchanged.", request.correlationId(), List.of(submitted.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-tool-boundary-approve".equals(request.actionId())) {
      var proposal = proposeToolBoundaryChange(actor, request.correlationId(), "Approval preview for redacted Agent Admin tool-boundary change.");
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      var approved = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW ? agentRuntimeService.approveProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), request.correlationId()) : submitted;
      result = new CapabilityActionResult(approved.status() == BehaviorChangeProposal.Status.APPROVED ? "approval-required" : "denied", "Tool-boundary approval recorded as behavior proposal evidence only; activation remains blocked until a separate confirmation surface.", request.correlationId(), List.of(approved.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-tool-boundary-reject".equals(request.actionId())) {
      var reason = stringInput(request.input(), "reason", "");
      if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Tool-boundary rejection requires a human-readable reason.", request.correlationId(), List.of("trace-agent-tool-boundary-reject-validation"), agentToolBoundarySurface(actor, request.correlationId()));
      } else {
        var proposal = proposeToolBoundaryChange(actor, request.correlationId(), "Rejected redacted Agent Admin tool-boundary change.");
        var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
        var rejected = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW || submitted.status() == BehaviorChangeProposal.Status.APPROVED ? agentRuntimeService.rejectProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), reason, request.correlationId()) : submitted;
        result = new CapabilityActionResult(rejected.status() == BehaviorChangeProposal.Status.REJECTED ? "accepted" : "denied", "Tool-boundary rejection recorded; active ToolPermissionBoundary grants and tenant scope remain unchanged.", request.correlationId(), List.of(rejected.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-tool-boundary-open-model-refs".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Related model-reference inspection opened from tool-boundary governance with provider credentials redacted.", request.correlationId(), List.of("trace-agent-tool-boundary-model-refs-" + stableSuffix(request.correlationId())), agentModelRefsSurface(actor, request.correlationId()));
    } else if ("action-agent-tool-boundary-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Tool-boundary trace opened with Agent Admin/Audit authorization and browser-safe redaction.", request.correlationId(), List.of("trace-agent-tool-boundary-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-tool-boundary-back-to-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned to backend-authorized managed-agent detail from tool-boundary governance.", request.correlationId(), List.of("trace-agent-tool-boundary-detail-return-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-model-refs-refresh".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Model-reference review refreshed from backend-owned ModelConfigRef and provider-readiness evidence; no behavior state changed.", request.correlationId(), List.of("trace-agent-model-refs-refresh-" + stableSuffix(request.correlationId())), agentModelRefsSurface(actor, request.correlationId()));
    } else if ("action-agent-model-refs-run-test".equals(request.actionId())) {
      result = new CapabilityActionResult("blocked_provider_or_runtime", "Model-reference readiness test opened the no-side-effect test console and failed closed; this runtime has no model-provider smoke configured and no fixture/model-less success was counted.", request.correlationId(), List.of("trace-agent-model-refs-provider-fail-closed-" + stableSuffix(request.correlationId())), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-model-refs-submit-review".equals(request.actionId())) {
      var proposal = proposeModelRefChange(actor, request.correlationId(), "Submitted redacted Agent Admin model-reference change for human review.");
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      result = new CapabilityActionResult(submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW ? "approval-required" : "denied", "Model-reference review submitted as behavior proposal evidence only; active model/provider routing remains unchanged.", request.correlationId(), List.of(submitted.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-model-refs-approve".equals(request.actionId())) {
      var proposal = proposeModelRefChange(actor, request.correlationId(), "Approval preview for redacted Agent Admin model-reference change.");
      var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
      var approved = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW ? agentRuntimeService.approveProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), request.correlationId()) : submitted;
      result = new CapabilityActionResult(approved.status() == BehaviorChangeProposal.Status.APPROVED ? "approval-required" : "denied", "Model-reference approval recorded as behavior proposal evidence only; activation remains blocked until a separate confirmation surface.", request.correlationId(), List.of(approved.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-model-refs-reject".equals(request.actionId())) {
      var reason = stringInput(request.input(), "reason", "");
      if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Model-reference rejection requires a human-readable reason.", request.correlationId(), List.of("trace-agent-model-refs-reject-validation"), agentModelRefsSurface(actor, request.correlationId()));
      } else {
        var proposal = proposeModelRefChange(actor, request.correlationId(), "Rejected redacted Agent Admin model-reference change.");
        var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
        var rejected = submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW || submitted.status() == BehaviorChangeProposal.Status.APPROVED ? agentRuntimeService.rejectProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), reason, request.correlationId()) : submitted;
        result = new CapabilityActionResult(rejected.status() == BehaviorChangeProposal.Status.REJECTED ? "accepted" : "denied", "Model-reference rejection recorded through backend behavior proposal review; active model/provider routing remains unchanged.", request.correlationId(), List.of(rejected.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-model-refs-open-prompt-governance".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Related prompt governance opened from model-reference review with raw prompt text omitted.", request.correlationId(), List.of("trace-agent-model-refs-prompt-governance-" + stableSuffix(request.correlationId())), agentPromptGovernanceSurface(actor, request.correlationId()));
    } else if ("action-agent-model-refs-open-tool-boundary".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Related tool-boundary review opened from model-reference governance with ToolPermissionBoundary evidence preserved.", request.correlationId(), List.of("trace-agent-model-refs-tool-boundary-" + stableSuffix(request.correlationId())), agentToolBoundarySurface(actor, request.correlationId()));
    } else if ("action-agent-model-refs-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Model-reference trace opened with Agent Admin/Audit authorization and browser-safe redaction.", request.correlationId(), List.of("trace-agent-model-refs-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-model-refs-back-to-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned to backend-authorized managed-agent detail from model-reference governance.", request.correlationId(), List.of("trace-agent-model-refs-detail-return-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-simulate-tool-boundary".equals(request.actionId())) {
      var proposal = proposeToolBoundaryChange(actor, request.correlationId(), "Agent Admin simulation of policy-blocked side-effecting tool grant");
      result = new CapabilityActionResult(proposal.status() == BehaviorChangeProposal.Status.DENIED ? "denied" : "approval-required", "Tool-boundary authority expansion simulation recorded as " + proposal.status().name().toLowerCase(Locale.ROOT) + "; side-effecting tools require retained human approval and backend ToolPermissionBoundary enforcement.", request.correlationId(), List.of(proposal.proposalId()), agentToolBoundarySurface(actor, request.correlationId()));
    } else if ("action-approve-skill-manifest".equals(request.actionId())) {
      result = new CapabilityActionResult("approval-required", "Skill manifest approval is recorded as a governed review gate; activation must use an approved backend governance command.", request.correlationId(), List.of("trace-skill-manifest-approval-required"), agentSkillManifestSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-risk-review-start".equals(request.actionId()) || "action-agentadmin-start-prompt-risk-review".equals(request.actionId())) {
      var task = promptRiskReviewService.start(actor, new AgentAdminPromptRiskReviewService.StartPromptRiskReviewCommand(
          stringInput(request.input(), "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
          stringInput(request.input(), "proposalId", "proposal-agent-admin-prompt-001"),
          List.of(new PromptRiskReviewTask.BehaviorArtifactDelta(PromptRiskReviewTask.ArtifactKind.PROMPT_DOCUMENT, "prompt-agent-admin-system", 1, 2, "Review redacted prompt behavior changes for authority expansion, secret exposure, and tool-boundary bypass language.", "redacted-diff-agent-admin-prompt", "sha256:before-redacted", "sha256:after-redacted")),
          List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"),
          request.idempotencyKey()), request.correlationId());
      result = promptRiskReviewActionResult(task, task.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "accepted", "Prompt-risk review task is backend-governed; result is advisory and cannot activate behavior artifacts.", request.correlationId(), actor);
    } else if ("action-agent-prompt-risk-review-read".equals(request.actionId()) || "action-agentadmin-read-prompt-risk-review".equals(request.actionId())) {
      var taskId = stringInput(request.input(), "taskId", "");
      result = taskId.isBlank()
          ? new CapabilityActionResult("accepted", "Prompt-risk review status surface loaded; no task id was selected from the dashboard queue.", request.correlationId(), List.of("trace-agent-admin-prompt-risk-status-" + stableSuffix(request.correlationId())), agentPromptRiskReviewEmptySurface(actor, request.correlationId()))
          : promptRiskReviewActionResult(promptRiskReviewService.read(actor, taskId, request.correlationId()), "accepted", "Prompt-risk review state loaded from backend projection.", request.correlationId(), actor);
    } else if ("action-agent-prompt-risk-review-cancel".equals(request.actionId()) || "action-agentadmin-cancel-prompt-risk-review".equals(request.actionId())) {
      var task = promptRiskReviewService.cancel(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "workstream cancel"), request.correlationId());
      result = promptRiskReviewActionResult(task, "accepted", "Prompt-risk review cancellation recorded; behavior artifacts unchanged.", request.correlationId(), actor);
    } else if ("action-agent-prompt-risk-review-accept".equals(request.actionId()) || "action-agentadmin-accept-prompt-risk-review-result".equals(request.actionId())) {
      var task = promptRiskReviewService.acceptResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "accepted by Agent Admin"), request.correlationId());
      result = new CapabilityActionResult("accepted", "Prompt-risk result accepted as human review evidence; no behavior artifact was activated and human behavior-proposal review remains required.", request.correlationId(), task.traceIds(), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-risk-review-reject".equals(request.actionId()) || "action-agentadmin-reject-prompt-risk-review-result".equals(request.actionId())) {
      var task = promptRiskReviewService.rejectResult(actor, stringInput(request.input(), "taskId", ""), stringInput(request.input(), "reason", "rejected by Agent Admin"), request.correlationId());
      result = new CapabilityActionResult("accepted", "Prompt-risk result rejected as human review evidence; behavior artifacts unchanged and human behavior-proposal review remains required.", request.correlationId(), task.traceIds(), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-risk-review-open-source".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned from prompt-risk review to the backend-authorized source surface with row/proposal context preserved when visible.", request.correlationId(), List.of("trace-agent-prompt-risk-review-source-" + stableSuffix(request.correlationId())), agentPromptGovernanceSurface(actor, request.correlationId()));
    } else if ("action-agent-prompt-risk-review-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Prompt-risk review trace opened with Agent Admin/Audit authorization and browser-safe provider/prompt redaction.", request.correlationId(), List.of("trace-agent-prompt-risk-review-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-admin-refresh-catalog".equals(request.actionId()) || "action-agent-admin-search-catalog".equals(request.actionId()) || "action-display-agent-catalog".equals(request.actionId()) || "action-agent-admin-open-catalog".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Managed agent catalog loaded with backend-authorized filters and browser-safe redaction.", request.correlationId(), List.of("trace-agent-admin-catalog-" + stableSuffix(request.correlationId())), agentAdminCatalogSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-admin-reset-catalog-filters".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Managed agent catalog filters reset by the backend; no managed-agent state changed.", request.correlationId(), List.of("trace-agent-admin-catalog-reset-" + stableSuffix(request.correlationId())), agentAdminCatalogSurface(actor, Map.of(), request.correlationId()));
    } else if ("action-agent-admin-catalog-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Catalog trace opened with Agent Admin/Audit authorization and redacted evidence.", request.correlationId(), List.of("trace-agent-admin-catalog-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-seed-material-refresh".equals(request.actionId()) || "action-agent-seed-material-search".equals(request.actionId()) || "action-list-agent-seed-material".equals(request.actionId()) || "action-agent-admin-open-seed-material".equals(request.actionId())) {
      var status = "action-agent-seed-material-refresh".equals(request.actionId()) || "action-list-agent-seed-material".equals(request.actionId()) ? "no-op" : "accepted";
      result = new CapabilityActionResult(status, "Seed material loaded through backend-authorized Agent Admin seed discovery with browser-safe provenance and customization-preservation summaries.", request.correlationId(), List.of("trace-agent-seed-material-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-seed-material-reset-filters".equals(request.actionId())) {
      result = new CapabilityActionResult("no-op", "Seed material filters reset by the backend; no seed package, tenant override, or managed-agent state changed.", request.correlationId(), List.of("trace-agent-seed-material-reset-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, Map.of(), request.correlationId()));
    } else if ("action-agent-seed-material-open-provenance".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Seed provenance opened on the backend-owned surface with raw seed contents, tenant overrides, provider secrets, and hidden-scope evidence redacted.", request.correlationId(), List.of("trace-agent-seed-material-provenance-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-seed-material-prepare-import".equals(request.actionId())) {
      var preparedInput = mergeInput(request.input(), Map.of("workflowStatus", "prepared"));
      result = new CapabilityActionResult("accepted", "Customization-preserving seed import plan prepared without mutating active behavior, lifecycle state, source packages, or tenant overrides.", request.correlationId(), List.of("trace-agent-seed-import-prepared-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, preparedInput, request.correlationId()));
    } else if ("action-agent-seed-material-start-import".equals(request.actionId())) {
      var acknowledgement = stringInput(request.input(), "acknowledgement", "");
      if (!"IMPORT".equalsIgnoreCase(acknowledgement)) {
        var preparedInput = mergeInput(request.input(), Map.of("workflowStatus", "prepared"));
        result = new CapabilityActionResult("validation-error", "Seed import requires acknowledgement=IMPORT before the backend may create missing starter defaults; no mutation occurred.", request.correlationId(), List.of("trace-agent-seed-import-ack-required-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, preparedInput, request.correlationId()));
      } else {
        authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY);
        var seed = new AgentBehaviorSeedLoader(agentBehaviorRepository, Clock.systemUTC()).importStarterDefaults(actor.selectedContext().tenantId(), actor.account().accountId(), request.correlationId());
        authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "agent_admin.seed_material.v1 start_import created=" + seed.createdCount() + " skipped=" + seed.skippedCount(), request.correlationId());
        var completedInput = mergeInput(request.input(), Map.of("workflowStatus", "completed"));
        result = new CapabilityActionResult(seed.createdCount() == 0 ? "no-op" : "accepted", "Seed import completed with " + seed.createdCount() + " created and " + seed.skippedCount() + " skipped governed records; tenant customizations and active behavior were preserved.", request.correlationId(), List.of("trace-agent-seed-import-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, completedInput, request.correlationId()));
      }
    } else if ("action-agent-seed-material-cancel-import".equals(request.actionId())) {
      authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY);
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "agent_admin.seed_material.v1 cancel_import no mutation", request.correlationId());
      var cancelledInput = mergeInput(request.input(), Map.of("workflowStatus", "cancelled"));
      result = new CapabilityActionResult("no-op", "No running seed import task was visible; cancellation was idempotent and did not delete source seed material or tenant customizations.", request.correlationId(), List.of("trace-agent-seed-import-cancel-" + stableSuffix(request.correlationId())), agentSeedMaterialSurface(actor, cancelledInput, request.correlationId()));
    } else if ("action-agent-seed-material-open-agent-detail".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Target managed-agent detail opened from seed material through backend authorization and visible row context.", request.correlationId(), List.of("trace-agent-seed-material-open-detail-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-seed-material-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Seed-material trace opened with Agent Admin/Audit authorization and browser-safe redaction.", request.correlationId(), List.of("trace-agent-seed-material-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-seed-material-back-to-source".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned from seed material to a backend-authorized Agent Admin source surface with safe filters.", request.correlationId(), List.of("trace-agent-seed-material-source-return-" + stableSuffix(request.correlationId())), agentAdminCatalogSurface(actor, request.input(), request.correlationId()));
    } else if ("action-open-agent-detail".equals(request.actionId()) || "action-agent-detail-refresh".equals(request.actionId())) {
      var status = "action-agent-detail-refresh".equals(request.actionId()) ? "no-op" : "accepted";
      var message = "action-agent-detail-refresh".equals(request.actionId())
          ? "Managed-agent detail refreshed by the backend; no behavior or lifecycle state changed."
          : "Managed-agent detail opened from a backend-visible catalog row.";
      result = new CapabilityActionResult(status, message, request.correlationId(), List.of("trace-agent-admin-detail-" + stableSuffix(stringInput(request.input(), "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID))), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-detail-back-to-catalog".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Returned to the backend-authorized Agent Admin catalog with safe filters.", request.correlationId(), List.of("trace-agent-admin-detail-catalog-return-" + stableSuffix(request.correlationId())), agentAdminCatalogSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-detail-open-prompt-governance".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Prompt governance opened for the selected managed agent with raw prompt text omitted.", request.correlationId(), List.of("trace-agent-detail-prompt-governance-" + stableSuffix(request.correlationId())), agentPromptGovernanceSurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-skill-manifest".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Skill and reference manifest review opened with compact manifests only.", request.correlationId(), List.of("trace-agent-detail-skill-manifest-" + stableSuffix(request.correlationId())), agentSkillManifestSurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-tool-boundary".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Tool-boundary review opened with governed ToolPermissionBoundary evidence.", request.correlationId(), List.of("trace-agent-detail-tool-boundary-" + stableSuffix(request.correlationId())), agentToolBoundarySurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-model-refs".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Model reference inspection opened with provider credentials redacted.", request.correlationId(), List.of("trace-agent-detail-model-refs-" + stableSuffix(request.correlationId())), agentModelRefsSurface(actor, request.correlationId()));
    } else if ("action-agent-detail-run-test".equals(request.actionId())) {
      var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), stringInput(request.input(), "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, request.correlationId(), stringInput(request.input(), "prompt", "Summarize current Agent Admin governed-agent readiness.")));
      result = new CapabilityActionResult(prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "accepted" : "denied", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "No-side-effect managed-agent test assembled governed prompt and loader traces." : "No-side-effect managed-agent test failed closed before model invocation.", request.correlationId(), List.of(prompt.traceId()), agentTestConsoleSurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-prompt-risk-review".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Prompt-risk review status opened; model-backed review remains fail-closed until provider/runtime readiness exists.", request.correlationId(), List.of("trace-agent-detail-prompt-risk-" + stableSuffix(request.correlationId())), agentPromptRiskReviewEmptySurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-activation".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.activation_confirmation.v1 preview", request.correlationId());
      result = new CapabilityActionResult("approval-required", "AgentDefinition activation requires a separate confirmation surface before any lifecycle mutation.", request.correlationId(), List.of("trace-agent-definition-activation-confirmation-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "activate", false));
    } else if ("action-agent-activation-refresh".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.activation_confirmation.v1 refresh", request.correlationId());
      result = new CapabilityActionResult("no-op", "Activation confirmation refreshed from backend-owned approval, provider, idempotency, and trace state; no lifecycle mutation occurred.", request.correlationId(), List.of("trace-agent-activation-refresh-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "activate", false));
    } else if ("action-agent-activation-confirm".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_ACTIVATE_CAPABILITY, "agent_admin.activation_confirmation.v1 confirm blocked before mutation", request.correlationId());
      var acknowledged = booleanInput(request.input(), "acknowledged", false) || "ACTIVATE".equals(rawStringInput(request.input(), "acknowledgement", ""));
      if (!acknowledged) {
        result = new CapabilityActionResult("validation-error", "Activation confirmation requires the explicit ACTIVATE acknowledgement before backend policy can evaluate the request.", request.correlationId(), List.of("trace-agent-activation-acknowledgement-required-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "activate", false));
      } else {
        result = new CapabilityActionResult("approval-required", "Activation was not applied: no approved provider-ready behavior proposal/version is visible for this selected AuthContext, so the backend failed closed with no lifecycle mutation.", request.correlationId(), List.of("trace-agent-activation-provider-fail-closed-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "activate", false));
      }
    } else if ("action-agent-activation-cancel".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.activation_confirmation.v1 cancel no mutation", request.correlationId());
      result = new CapabilityActionResult("accepted", "Activation confirmation cancelled; managed-agent lifecycle state was not changed.", request.correlationId(), List.of("trace-agent-activation-cancel-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-activation-open-proposal".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Approved behavior proposal decision surface opened with activation prerequisites still backend-authoritative and browser-safe.", request.correlationId(), List.of("trace-agent-activation-open-proposal-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-activation-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Activation trace opened with Agent Admin/Audit authorization and raw prompt, provider, and hidden-scope evidence redacted.", request.correlationId(), List.of("trace-agent-activation-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-deactivation".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.deactivation_confirmation.v1 preview", request.correlationId());
      result = new CapabilityActionResult("approval-required", "AgentDefinition deactivation requires a separate confirmation surface before any lifecycle mutation.", request.correlationId(), List.of("trace-agent-definition-deactivation-confirmation-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "deactivate", false));
    } else if ("action-agent-deactivation-refresh".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.deactivation_confirmation.v1 refresh", request.correlationId());
      result = new CapabilityActionResult("no-op", "Deactivation confirmation refreshed from backend-owned lifecycle, policy, idempotency, and trace state; no lifecycle mutation occurred.", request.correlationId(), List.of("trace-agent-deactivation-refresh-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "deactivate", false));
    } else if ("action-agent-deactivation-confirm".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.deactivation_confirmation.v1 confirm", request.correlationId());
      var acknowledged = booleanInput(request.input(), "acknowledged", false) || "DEACTIVATE".equals(rawStringInput(request.input(), "acknowledgement", ""));
      var reason = rawStringInput(request.input(), "reason", rawStringInput(request.input(), "reasonCategory", "")).trim();
      if (!acknowledged) {
        result = new CapabilityActionResult("validation-error", "Deactivation confirmation requires the explicit DEACTIVATE acknowledgement before backend policy can evaluate the request.", request.correlationId(), List.of("trace-agent-deactivation-acknowledgement-required-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "deactivate", false));
      } else if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Deactivation confirmation requires a browser-safe admin reason before any lifecycle mutation.", request.correlationId(), List.of("trace-agent-deactivation-reason-required-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "deactivate", false));
      } else {
        var changed = changeAgentDefinitionStatus(actor, stringInput(request.input(), "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), AgentLifecycleStatus.DISABLED, request.correlationId());
        result = new CapabilityActionResult(changed ? "accepted" : "no-op", changed ? "Managed agent was deactivated by the backend with governed capability, idempotency, and trace evidence; no prompt, skill, reference, provider, or tenant override artifacts were deleted." : "Managed agent was already deactivated for this selected AuthContext; repeated confirmation was idempotent and no source artifacts were changed.", request.correlationId(), List.of("trace-agent-deactivation-confirm-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
      }
    } else if ("action-agent-deactivation-cancel".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_DEFINITIONS_MANAGE_CAPABILITY, "agent_admin.deactivation_confirmation.v1 cancel no mutation", request.correlationId());
      result = new CapabilityActionResult("accepted", "Deactivation confirmation cancelled; managed-agent lifecycle state was not changed.", request.correlationId(), List.of("trace-agent-deactivation-cancel-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-deactivation-open-proposal".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Related lifecycle proposal decision surface opened with deactivation prerequisites still backend-authoritative and browser-safe.", request.correlationId(), List.of("trace-agent-deactivation-open-proposal-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-deactivation-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Deactivation trace opened with Agent Admin/Audit authorization and raw prompt, provider, and hidden-scope evidence redacted.", request.correlationId(), List.of("trace-agent-deactivation-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-rollback".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_ROLLBACK_CAPABILITY, "agent_admin.rollback_confirmation.v1 preview", request.correlationId());
      result = new CapabilityActionResult("approval-required", "Rollback requires backend-visible activated proposal metadata and opens a separate confirmation surface.", request.correlationId(), List.of("trace-agent-detail-rollback-" + stableSuffix(request.correlationId())), agentRollbackConfirmationSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-rollback-refresh".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_ROLLBACK_CAPABILITY, "agent_admin.rollback_confirmation.v1 refresh", request.correlationId());
      result = new CapabilityActionResult("no-op", "Rollback confirmation refreshed from backend-owned activated proposal metadata, policy, idempotency, and trace state; no lifecycle or artifact mutation occurred.", request.correlationId(), List.of("trace-agent-rollback-refresh-" + stableSuffix(request.correlationId())), agentRollbackConfirmationSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-rollback-confirm".equals(request.actionId()) || "action-rollback-behavior-change".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_ROLLBACK_CAPABILITY, "agent_admin.rollback_confirmation.v1 confirm", request.correlationId());
      var acknowledged = booleanInput(request.input(), "acknowledged", false) || "ROLLBACK".equals(rawStringInput(request.input(), "acknowledgement", ""));
      var reason = rawStringInput(request.input(), "reason", rawStringInput(request.input(), "reasonCategory", "")).trim();
      var proposalId = rollbackProposalId(actor, request.input());
      if (!acknowledged) {
        result = new CapabilityActionResult("validation-error", "Rollback confirmation requires the explicit ROLLBACK acknowledgement before backend policy can evaluate the request.", request.correlationId(), List.of("trace-agent-rollback-acknowledgement-required-" + stableSuffix(request.correlationId())), agentRollbackConfirmationSurface(actor, request.input(), request.correlationId()));
      } else if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Rollback confirmation requires a browser-safe admin reason before any lifecycle or artifact mutation.", request.correlationId(), List.of("trace-agent-rollback-reason-required-" + stableSuffix(request.correlationId())), agentRollbackConfirmationSurface(actor, request.input(), request.correlationId()));
      } else if (proposalId.isBlank()) {
        result = new CapabilityActionResult("approval-required", "Rollback was not applied: no activated behavior proposal with rollback metadata is visible for this selected AuthContext, so the backend failed closed with no artifact mutation.", request.correlationId(), List.of("trace-agent-rollback-metadata-missing-" + stableSuffix(request.correlationId())), agentRollbackConfirmationSurface(actor, request.input(), request.correlationId()));
      } else {
        try {
          var before = agentRuntimeService.proposals().stream().filter(proposal -> proposal.proposalId().equals(proposalId)).findFirst().orElse(null);
          var rolledBack = agentRuntimeService.rollbackProposal(actor.selectedContext(), actor.selectedContext().tenantId(), proposalId, request.correlationId());
          var changed = before == null || before.status() == BehaviorChangeProposal.Status.ACTIVATED;
          result = new CapabilityActionResult(changed ? "accepted" : "no-op", changed ? "Activated behavior proposal was rolled back by the backend with governed capability, idempotency, and trace evidence; no prompt, skill, reference, provider credential, source artifact, or tenant override was deleted." : "Behavior proposal was already rolled back for this selected AuthContext; repeated confirmation was idempotent and no source artifacts were changed.", request.correlationId(), List.of("trace-agent-rollback-confirm-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, mapOf("agentDefinitionId", rolledBack.agentDefinitionId()), request.correlationId()));
        } catch (AuthorizationException denied) {
          result = new CapabilityActionResult("approval-required", "Rollback was not applied: " + denied.reasonCode() + "; backend policy failed closed with no artifact mutation or fabricated rollback success.", request.correlationId(), List.of("trace-agent-rollback-denied-" + stableSuffix(request.correlationId())), agentRollbackConfirmationSurface(actor, request.input(), request.correlationId()));
        }
      }
    } else if ("action-agent-rollback-cancel".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_ROLLBACK_CAPABILITY, "agent_admin.rollback_confirmation.v1 cancel no mutation", request.correlationId());
      result = new CapabilityActionResult("accepted", "Rollback confirmation cancelled; managed-agent behavior and source artifacts were not changed.", request.correlationId(), List.of("trace-agent-rollback-cancel-" + stableSuffix(request.correlationId())), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-rollback-open-proposal".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Activated behavior proposal decision surface opened with rollback prerequisites still backend-authoritative and browser-safe.", request.correlationId(), List.of("trace-agent-rollback-open-proposal-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-rollback-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Rollback trace opened with Agent Admin/Audit authorization and raw prompt, provider, and hidden-scope evidence redacted.", request.correlationId(), List.of("trace-agent-rollback-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-behavior-proposal-refresh".equals(request.actionId())) {
      authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "agent_admin.behavior_proposal.v1 refresh", request.correlationId());
      result = new CapabilityActionResult("no-op", "Behavior proposal decision refreshed from backend-owned proposal, evidence, provider, idempotency, and trace state; no source artifact or lifecycle mutation occurred.", request.correlationId(), List.of("trace-agent-behavior-proposal-refresh-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
    } else if ("action-agent-behavior-proposal-submit".equals(request.actionId())) {
      var proposal = behaviorProposalInput(actor, request.input());
      if (proposal == null) {
        result = new CapabilityActionResult("validation-error", "Behavior proposal submit requires a visible draft/proposed proposal in the selected AuthContext; hidden or cross-scope proposals are not enumerated.", request.correlationId(), List.of("trace-agent-behavior-proposal-submit-missing-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      } else {
        var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
        result = new CapabilityActionResult(submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW ? "approval-required" : "no-op", "Behavior proposal submit recorded through backend authorization; active behavior, provider configuration, and lifecycle state remain unchanged.", request.correlationId(), List.of(submitted.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-behavior-proposal-approve".equals(request.actionId())) {
      var proposal = behaviorProposalInput(actor, request.input());
      if (proposal == null) {
        result = new CapabilityActionResult("validation-error", "Behavior proposal approval requires a visible in-review proposal in the selected AuthContext.", request.correlationId(), List.of("trace-agent-behavior-proposal-approve-missing-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      } else {
        authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_REVIEW_CAPABILITY, "agent_admin.behavior_proposal.v1 approve blocked_provider_or_runtime", request.correlationId());
        result = new CapabilityActionResult("blocked_provider_or_runtime", "Behavior proposal approval was not recorded: provider/runtime evidence is not configured in this local runtime, so the backend failed closed with no fabricated model success and no activation.", request.correlationId(), List.of("trace-agent-behavior-proposal-approve-provider-blocked-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-behavior-proposal-reject".equals(request.actionId())) {
      var reason = stringInput(request.input(), "reason", "").trim();
      var proposal = behaviorProposalInput(actor, request.input());
      if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Behavior proposal rejection requires a browser-safe human-readable reason.", request.correlationId(), List.of("trace-agent-behavior-proposal-reject-reason-required-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      } else if (proposal == null) {
        result = new CapabilityActionResult("validation-error", "Behavior proposal rejection requires a visible proposal in the selected AuthContext.", request.correlationId(), List.of("trace-agent-behavior-proposal-reject-missing-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      } else {
        var submitted = proposal.status() == BehaviorChangeProposal.Status.PROPOSED ? agentRuntimeService.submitProposalForReview(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), request.correlationId()) : proposal;
        var rejected = (submitted.status() == BehaviorChangeProposal.Status.IN_REVIEW || submitted.status() == BehaviorChangeProposal.Status.APPROVED) ? agentRuntimeService.rejectProposal(actor.selectedContext(), actor.selectedContext().tenantId(), submitted.proposalId(), reason, request.correlationId()) : submitted;
        result = new CapabilityActionResult(rejected.status() == BehaviorChangeProposal.Status.REJECTED ? "accepted" : "conflict", "Behavior proposal rejection recorded through backend review; active behavior, lifecycle state, and source artifacts remain unchanged.", request.correlationId(), List.of(rejected.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-behavior-proposal-defer".equals(request.actionId())) {
      var reason = stringInput(request.input(), "reason", stringInput(request.input(), "followUpCategory", "")).trim();
      var proposal = behaviorProposalInput(actor, request.input());
      if (reason.isBlank()) {
        result = new CapabilityActionResult("validation-error", "Behavior proposal defer requires a reason or follow-up category.", request.correlationId(), List.of("trace-agent-behavior-proposal-defer-reason-required-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      } else if (proposal == null) {
        result = new CapabilityActionResult("validation-error", "Behavior proposal defer requires a visible proposal in the selected AuthContext.", request.correlationId(), List.of("trace-agent-behavior-proposal-defer-missing-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      } else {
        var deferred = agentRuntimeService.deferProposal(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), reason, request.correlationId());
        result = new CapabilityActionResult("approval-required", "Behavior proposal deferred for follow-up through backend authorization; no approval, activation, deletion, or provider configuration change occurred.", request.correlationId(), List.of(deferred.proposalId()), agentBehaviorProposalSurface(actor, request.correlationId()));
      }
    } else if ("action-agent-behavior-proposal-cancel".equals(request.actionId())) {
      var proposal = behaviorProposalInput(actor, request.input());
      var reason = stringInput(request.input(), "reason", "cancelled from behavior proposal surface");
      if (proposal == null) {
        result = new CapabilityActionResult("validation-error", "Behavior proposal cancellation requires a visible proposal in the selected AuthContext.", request.correlationId(), List.of("trace-agent-behavior-proposal-cancel-missing-" + stableSuffix(request.correlationId())), agentBehaviorProposalSurface(actor, request.correlationId()));
      } else {
        var cancelled = agentRuntimeService.cancelProposal(actor.selectedContext(), actor.selectedContext().tenantId(), proposal.proposalId(), reason, request.correlationId());
        result = new CapabilityActionResult(cancelled.status() == BehaviorChangeProposal.Status.CANCELLED ? "accepted" : "no-op", "Behavior proposal interaction cancelled through backend authorization; source artifacts and lifecycle state remain unchanged.", request.correlationId(), List.of(cancelled.proposalId()), agentAdminDetailSurface(actor, request.input(), request.correlationId()));
      }
    } else if ("action-agent-behavior-proposal-open-activation".equals(request.actionId())) {
      result = new CapabilityActionResult("approval-required", "Behavior proposal activation route opened only as a separate confirmation surface; provider/runtime readiness remains fail-closed and no activation occurred.", request.correlationId(), List.of("trace-agent-behavior-proposal-open-activation-" + stableSuffix(request.correlationId())), agentLifecycleConfirmationSurface(actor, request.correlationId(), "activate", false));
    } else if ("action-agent-behavior-proposal-open-rollback".equals(request.actionId())) {
      result = new CapabilityActionResult("approval-required", "Behavior proposal rollback route opened only as a separate confirmation surface with backend rollback metadata checks; no rollback occurred.", request.correlationId(), List.of("trace-agent-behavior-proposal-open-rollback-" + stableSuffix(request.correlationId())), agentRollbackConfirmationSurface(actor, request.input(), request.correlationId()));
    } else if ("action-agent-behavior-proposal-open-source".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Behavior proposal source review surface opened with backend authorization and browser-safe row context.", request.correlationId(), List.of("trace-agent-behavior-proposal-open-source-" + stableSuffix(request.correlationId())), agentPromptGovernanceSurface(actor, request.correlationId()));
    } else if ("action-agent-behavior-proposal-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Behavior proposal trace opened with Agent Admin/Audit authorization and raw prompt, provider, tool, and hidden-scope evidence redacted.", request.correlationId(), List.of("trace-agent-behavior-proposal-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
    } else if ("action-agent-detail-open-trace".equals(request.actionId())) {
      result = new CapabilityActionResult("accepted", "Detail trace opened with Agent Admin/Audit authorization and redacted evidence.", request.correlationId(), List.of("trace-agent-detail-open-trace-" + stableSuffix(request.correlationId())), agentAdminTraceSurface(actor, request.correlationId()));
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
    } else if ("action-select-my-context".equals(request.actionId())) {
      var requestedContextId = stringInput(request.input(), "selectedContextId", selectedContextId);
      var selectedActor = authContextResolver.resolveMe(identity, requestedContextId, request.correlationId());
      var sameContext = Objects.equals(actor.selectedContext().membershipId(), selectedActor.selectedContext().membershipId());
      result = new CapabilityActionResult(
          sameContext ? "no-op" : "accepted",
          sameContext ? "Selected AuthContext was already active; no authority changed." : "Selected AuthContext refreshed through backend-authoritative context selection.",
          request.correlationId(),
          List.of("trace-my-account-context-select-" + stableSuffix(request.correlationId())),
          myContextSurface(selectedActor, request.correlationId()));
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
      var task = personalAttentionDigestService.rejectResult(actor, stringInput(request.input(), "digestTaskId", ""), stringInput(request.input(), "reason", ""), request.correlationId());
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
          : new CapabilityActionResult("denied", open.message(), request.correlationId(), open.traceIds(), myAccountOpenDeniedSurface(actor, open, request.correlationId(), request.actionId()));
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
      var command = new AuditTraceSummaryService.StartAuditTraceSummaryCommand(
          instantInput(request.input(), "windowStart"),
          instantInput(request.input(), "windowEnd"),
          listStringInput(request.input(), "evidenceCategories"),
          firstNonBlank(request.idempotencyKey(), stringInput(request.input(), "idempotencyKey", null), request.correlationId()));
      var task = auditTraceSummaryService.start(actor, command, request.correlationId());
      var surface = auditTraceSummaryProgressSurface(actor, task, request.correlationId());
      result = new CapabilityActionResult(auditTraceSummaryStatus(task), "Audit summary worker entered backend lifecycle state " + auditTraceSummaryStatus(task) + "; no deterministic or model-less summary success was fabricated.", request.correlationId(), surface.traceIds(), surface);
    } else if ("action-audit-trace-summary-task-read".equals(request.actionId())) {
      var task = auditTraceSummaryService.read(actor, stringInput(request.input(), "summaryTaskId", stringInput(request.input(), "taskId", "")), request.correlationId());
      var surface = auditTraceSummaryProgressSurface(actor, task, request.correlationId());
      result = new CapabilityActionResult(auditTraceSummaryStatus(task), "Audit summary worker progress refreshed from backend-retained task state.", request.correlationId(), surface.traceIds(), surface);
    } else if ("action-audit-trace-summary-review".equals(request.actionId())) {
      var task = auditTraceSummaryService.review(actor, stringInput(request.input(), "summaryTaskId", stringInput(request.input(), "taskId", "")), request.correlationId());
      var surface = auditTraceSummaryReviewSurface(actor, task, request.correlationId());
      result = new CapabilityActionResult(Objects.toString(surface.data().get("status"), auditTraceSummaryStatus(task)), "Audit summary review opened from backend-retained model-backed task state; source traces and policy remain immutable.", request.correlationId(), surface.traceIds(), surface);
    } else if ("action-audit-trace-summary-accept".equals(request.actionId())) {
      var task = auditTraceSummaryService.acceptResult(actor, stringInput(request.input(), "summaryTaskId", stringInput(request.input(), "taskId", "")), stringInput(request.input(), "reason", "accepted by Audit/Trace reviewer"), request.correlationId());
      var surface = auditTraceSummaryReviewSurface(actor, task, request.correlationId());
      result = new CapabilityActionResult(auditTraceSummaryStatus(task), "Audit summary accepted as advisory review evidence only; source traces, policy, exports, notes, and authorization are unchanged.", request.correlationId(), surface.traceIds(), surface);
    } else if ("action-audit-trace-summary-reject".equals(request.actionId())) {
      var task = auditTraceSummaryService.rejectResult(actor, stringInput(request.input(), "summaryTaskId", stringInput(request.input(), "taskId", "")), stringInput(request.input(), "reason", ""), request.correlationId());
      var surface = auditTraceSummaryReviewSurface(actor, task, request.correlationId());
      result = new CapabilityActionResult(auditTraceSummaryStatus(task), "Audit summary rejected as advisory review evidence only; source traces, policy, exports, notes, and authorization are unchanged.", request.correlationId(), surface.traceIds(), surface);
    } else if ("action-audit-trace-append-investigation-note".equals(request.actionId())) {
      var surface = auditTraceInvestigationNoteSurface(actor, request.input(), request.idempotencyKey(), request.correlationId());
      var noteStatus = Objects.toString(surface.data().get("status"), "recorded");
      var recorded = "recorded".equals(noteStatus) || "no_op_idempotent_replay".equals(noteStatus);
      if (recorded) {
        var item = new WorkstreamItem("item-audit-trace-note-" + AuditTraceService.stableSuffix(request.idempotencyKey() == null || request.idempotencyKey().isBlank() ? request.correlationId() : request.idempotencyKey()), AUDIT_TRACE_AGENT_ID, "system_message", Instant.now().toString(), request.correlationId(), surface.traceIds(), surface.surfaceId(), "Audit/Trace investigation note", "Human-authored investigation note recorded with browser-safe redaction.", "recorded");
        workstreamLogRepository.appendSystemEntry(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), item, surface);
      }
      var message = recorded ? "Investigation note appended as an auditable, tenant-scoped workstream annotation; source traces remain immutable." : Objects.toString(surface.data().get("message"), "Investigation note request did not record an annotation.");
      result = new CapabilityActionResult(noteStatus, message, request.correlationId(), surface.traceIds(), surface);
    } else if ("action-governance-policy-dashboard".equals(request.actionId())) {
      result = governancePolicyReadResult(actor, "Governance/Policy dashboard loaded from GovernancePolicyService for the selected AuthContext.", request.correlationId(), governancePolicyDashboardSurface(actor, request.correlationId()));
    } else if ("action-governance-policy-list".equals(request.actionId())) {
      result = governancePolicyReadResult(actor, "Governance/Policy inventory loaded from GovernancePolicyService for the selected AuthContext.", request.correlationId(), governancePolicyInventorySurface(actor, request.input(), request.correlationId()));
    } else if ("action-governance-policy-read".equals(request.actionId())) {
      result = governancePolicyReadResult(actor, "Governance/Policy policy detail loaded from GovernancePolicyService for the selected AuthContext.", request.correlationId(), governancePolicyDetailSurface(actor, request.input(), request.correlationId()));
    } else if ("action-governance-policy-draft-proposal".equals(request.actionId())) {
      var draft = governancePolicyService.draftProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(draft.status(), draft.message(), request.correlationId(), draft.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), draft.surface(), governanceProposalActions(actor)));
    } else if ("action-governance-policy-submit-proposal".equals(request.actionId())) {
      var submit = governancePolicyService.submitProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(submit.status(), submit.message(), request.correlationId(), submit.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), submit.surface(), governanceProposalActions(actor)));
    } else if ("action-governance-policy-simulate".equals(request.actionId()) || "action-simulate-policy".equals(request.actionId())) {
      var simulation = governancePolicyService.simulateProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(simulation.status(), simulation.message(), request.correlationId(), simulation.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), simulation.surface(), governanceSimulationActions(actor)));
    } else if ("action-governance-policy-decide".equals(request.actionId())) {
      var decision = governancePolicyService.decideProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(decision.status(), decision.message(), request.correlationId(), decision.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), decision.surface(), governanceDecisionActions(actor)));
    } else if ("action-governance-policy-activate".equals(request.actionId()) || "action-commit-policy".equals(request.actionId())) {
      var activation = governancePolicyService.activateProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(activation.status(), activation.message(), request.correlationId(), activation.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), activation.surface(), governanceDecisionActions(actor)));
    } else if ("action-governance-policy-rollback".equals(request.actionId())) {
      var rollback = governancePolicyService.rollbackProposal(actor, request.input(), request.idempotencyKey(), request.correlationId());
      result = new CapabilityActionResult(rollback.status(), rollback.message(), request.correlationId(), rollback.traceIds(), governancePolicyEnvelope(actor, request.correlationId(), rollback.surface(), governanceDecisionActions(actor)));
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
        || actionId.equals("action-user-admin-return-dashboard")
        || actionId.startsWith("action-organization")
        || actionId.startsWith("action-open-organization")
        || actionId.startsWith("action-customer")
        || actionId.startsWith("action-open-customer")
        || actionId.equals("action-submit-customer-create")
        || actionId.equals("action-submit-customer-rename")
        || actionId.equals("action-submit-organization-create")
        || actionId.equals("action-submit-organization-rename")
        || actionId.equals("action-submit-organization-admin-invitation")
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
    var reasonCode = firstNonBlank(code, "not_found_or_redacted");
    var normalizedReason = reasonCode.toLowerCase(Locale.ROOT).replace("target_not_found_or_forbidden", "not_found_or_redacted").replace("target-not-found-or-forbidden", "not_found_or_redacted");
    var traceId = "trace-useradmin-system-message-" + stableSuffix(correlationId + ":" + firstNonBlank(sourceActionId, reasonCode));
    var scopeType = actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT);
    var scopeLabel = switch (actor.selectedContext().scopeType()) {
      case SAAS_OWNER -> "SaaS Owner scope";
      case CUSTOMER -> "selected customer";
      default -> "selected tenant";
    };
    var sourceAction = firstNonBlank(sourceActionId, "direct protected recovery load");
    return new SurfaceEnvelope("surface-user-admin-system-message", "system-message", "v1", "User Admin action unavailable", USER_ADMIN_AGENT_ID, List.of(AUDIT_TRACE_AGENT_ID), mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities()), correlationId, List.of(traceId), Instant.now().toString(), null, mapOf("profile", "tenant-admin", "omittedFieldKeys", List.of("hiddenUserId", "hiddenMembershipId", "hiddenOrganizationId", "hiddenCustomerId", "rawInvitationToken", "rawJwt", "rawProviderCredential", "providerPayload", "modelPrompt", "toolArguments", "rawIdempotencyKey")), mapOf(
        "surfaceContract", "user_admin.system_message.v1",
        "selectedAuthContext", mapOf("selectedContextId", actor.selectedContext().membershipId(), "scopeType", scopeType, "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId()),
        "scopeLabel", scopeLabel,
        "scopeType", scopeType,
        "authorityBasis", "protected workstream API resolved and redacted the selected AuthContext before returning recovery guidance",
        "status", normalizedReason.contains("validation") || normalizedReason.contains("required") || normalizedReason.contains("unsupported") ? "validation-error" : normalizedReason.contains("no_op") || normalizedReason.contains("no-op") || normalizedReason.contains("already") ? "no-op" : normalizedReason.contains("provider") || normalizedReason.contains("outbox") || normalizedReason.contains("model") || normalizedReason.contains("runtime") ? "blocked_provider_or_runtime" : "not_found_or_redacted",
        "severity", normalizedReason.contains("provider") || normalizedReason.contains("outbox") || normalizedReason.contains("model") || normalizedReason.contains("runtime") ? "warning" : "forbidden",
        "reasonCode", normalizedReason,
        "safeReasonCode", normalizedReason,
        "code", reasonCode,
        "category", normalizedReason,
        "title", "User Admin action unavailable",
        "summary", message,
        "message", message,
        "body", message,
        "sourceActionId", sourceActionId,
        "requestedTargetSummary", "The requested User Admin target is hidden, stale, forbidden, invalid, no-op, or unavailable for the selected context.",
        "branchNavigation", userBranchNavigation(correlationId),
        "branchRootSurfaceId", "surface-user-admin-users",
        "branchReturnActionId", "action-user-admin-show-users",
        "branchReturnLabel", "Show users",
        "selectedScopeLabel", scopeType,
        "systemStates", List.of("ready", "forbidden", "not_found_or_redacted", "validation-error", "no-op", "blocked_provider_or_runtime"),
        "lastResult", mapOf("sourceActionId", sourceAction, "resultCategory", normalizedReason, "sideEffect", "none", "noEnumeration", true),
        "messageModel", mapOf("reasonCode", normalizedReason, "category", normalizedReason, "severity", "warning", "title", "User Admin action unavailable", "summary", message, "userSafeExplanation", message, "recoveryHeading", "Recovery steps", "noEnumeration", true, "noFakeSuccess", true, "noDirectMutation", true, "retryEligible", false, "supportEscalationEligible", false),
        "recoverySteps", List.of("Return to the User Admin dashboard", "Return to a safe User Directory branch when authorized", "Refresh the selected context", "Open authorized audit evidence if available"),
        "availableActions", List.of("action-user-admin-return-dashboard", "action-user-admin-show-users", "action-open-audit-trace"),
        "readinessSummary", mapOf("state", "fail-closed-or-not-applicable", "retryEligible", false, "adminRecoveryHint", "Use the authorized dashboard, branch return, or audit action; provider/outbox/model/tool details stay redacted.", "noFakeSuccess", true),
        "validationSummary", mapOf("safeFieldGuidance", "Validation, stale, conflict, and no-op outcomes are reported without hidden target enumeration.", "noDirectMutation", true),
        "traceRefs", List.of(traceId),
        "correlationId", correlationId,
        "redaction", "Hidden users, Organizations, Customers, memberships, invitations, provider/model/tool payloads, raw JWTs, raw idempotency keys, and sibling-scope facts are redacted.",
        "redactionNote", "Hidden users, Organizations, Customers, memberships, invitations, provider/model/tool payloads, raw JWTs, raw idempotency keys, and sibling-scope facts are redacted.",
        "noEnumeration", true,
        "noFakeSuccess", true,
        "noDirectMutation", true), List.of(userAdminReturnDashboardAction(), showUsersAction(), openAuditAction()), List.of(mapOf("label", "Open recovery trace", "href", "/ui?traceId=" + traceId, "rel", "trace")));
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
        || actionId.startsWith("action-agent-prompt-risk-review-")
        || actionId.startsWith("action-agentadmin-") && actionId.contains("prompt-risk-review")
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
    authContextResolver.requireCapability(actor.selectedContext(), MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_PROFILE_SELF_SERVICE_READ", "surface-my-profile", correlationId);
    var traceRefs = myAccountService.traceRefs(actor, correlationId);
    return envelope("surface-my-profile", "detail-edit", "User profile", actor, correlationId,
        mapOf(
            "surfaceContract", "my_account.profile.self_service.v1",
            "profileSummary", mapOf(
                "accountId", actor.account().accountId(),
                "email", actor.account().displayEmail(),
                "displayName", actor.profile().displayName(),
                "accountStatus", actor.account().status().name().toLowerCase(Locale.ROOT),
                "selectedContextLabel", actor.selectedContext().tenantId() + (actor.selectedContext().customerId() == null ? "" : " / " + actor.selectedContext().customerId()),
                "avatarInitials", initials(actor.profile().displayName(), actor.account().displayEmail())),
            "providerBoundarySummary", "Email, authentication identifiers, and provider-backed facts are owned by WorkOS/AuthKit identity reconciliation and are read-only in My Account; raw provider payloads and secrets are never exposed.",
            "recordId", actor.account().accountId() + "-profile",
            "recordLabel", actor.profile().displayName() + " · " + actor.account().displayEmail(),
            "recordKind", "profile",
            "summary", "Current signed-in user profile. Administrative role, membership, account-status, and provider-backed changes are intentionally not editable here.",
            "fields", List.of(
                mapOf("fieldId", "displayName", "label", "Display name", "value", actor.profile().displayName(), "editable", true, "inputType", "text", "required", true, "helperText", "Shown in workstream surfaces and audit-safe user summaries.", "constraints", mapOf("minLength", 1, "maxLength", 120), "lastSavedValue", actor.profile().displayName()),
                mapOf("fieldId", "email", "label", "Email", "value", actor.account().displayEmail(), "editable", false, "inputType", "email", "helperText", "Provider-backed sign-in email.", "disabledReason", "Email is owned by WorkOS/AuthKit identity reconciliation."),
                mapOf("fieldId", "accountStatus", "label", "Account status", "value", actor.account().status().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text", "helperText", "Visible browser-safe account state only.", "disabledReason", "Account status changes require User Admin capabilities, not My Account self-service."),
                mapOf("fieldId", "locale", "label", "Locale", "value", "en-US", "editable", false, "inputType", "select", "helperText", "Read-only until locale preference persistence is implemented.", "disabledReason", "Locale changes are deferred beyond My Account."),
                mapOf("fieldId", "timeZone", "label", "Time zone", "value", "America/New_York", "editable", false, "inputType", "text", "helperText", "Read-only until timezone preference persistence is implemented.", "disabledReason", "Time zone changes are deferred beyond My Account.")),
            "version", 1,
            "permissionState", mapOf("canEdit", true, "reason", "Only changed editable self-service fields are submitted; unsupported fields are denied before mutation.", "authoritativeCapabilityId", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY, "coreCapabilityAlias", CORE_PROFILE_UPDATE_CAPABILITY, "saveActionId", "action-update-my-profile"),
            "traceRefs", traceRefs,
            "redaction", mapOf("profile", "self-service-profile", "omittedFieldKeys", List.of("rawJwt", "sessionToken", "providerPayload", "providerSecret", "hiddenMemberships", "roleAssignments")),
            "audit", mapOf("lastEventType", "UserProfileDisplayed", "lastActor", actor.profile().displayName(), "traceIds", traceRefs.stream().map(MyAccountService.TraceRef::traceId).toList()))
        , List.of(updateProfileAction(), openAuditAction()));
  }

  private String initials(String displayName, String email) {
    var source = displayName == null || displayName.isBlank() ? email : displayName;
    if (source == null || source.isBlank()) return "??";
    var parts = source.trim().split("\\s+");
    if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
    return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
  }

  private SurfaceEnvelope mySettingsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var traceRefs = myAccountService.traceRefs(actor, correlationId);
    return envelope("surface-my-settings", "detail-edit", "User settings", actor, correlationId,
        mapOf(
            "surfaceContract", "my_account.preferences.self_service.v1",
            "settingsSummary", mapOf("preferredThemeId", actor.settings().themeId().id(), "locale", actor.settings().locale(), "timeZone", actor.settings().timeZone(), "themeModel", "named-theme-selection", "lastSavedAt", "current-backend-state"),
            "preferredThemeId", actor.settings().themeId().id(),
            "availableThemes", namedThemeOptions(),
            "locale", actor.settings().locale(),
            "localeOptions", List.of("en-US", "en-GB", "fr-FR", "es-ES"),
            "timeZone", actor.settings().timeZone(),
            "timeZoneOptions", List.of("America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles", "Europe/London", "Europe/Paris", "UTC"),
            "notificationPreferenceSummary", mapOf("channel", "in_app", "visibleCategoriesOnly", true, "summary", "Open the notification center to tune visible in-app categories; external delivery/provider controls are not exposed here."),
            "digestPreferenceSummary", mapOf("summary", "Personal digest/export requests are governed task surfaces and do not mutate source attention."),
            "recordId", actor.account().accountId() + "-settings",
            "recordLabel", actor.profile().displayName() + " settings",
            "recordKind", "settings",
            "summary", "Current signed-in user preferences for named theme, locale, timezone, and in-app notification entry points. Tenant branding, provider delivery, model configuration, and authorization changes are intentionally not editable here.",
            "fields", List.of(
                mapOf("fieldId", "preferredThemeId", "label", "Named theme", "value", actor.settings().themeId().id(), "editable", true, "inputType", "select", "required", true, "helperText", "Preview is browser-local until Save/Confirm persists this named theme.", "constraints", mapOf("allowedThemeIds", namedThemeOptions().stream().map(option -> option.get("themeId")).toList()), "lastSavedValue", actor.settings().themeId().id(), "options", namedThemeOptions()),
                mapOf("fieldId", "locale", "label", "Locale", "value", actor.settings().locale(), "editable", true, "inputType", "select", "required", true, "helperText", "Personal locale preference for browser-safe workstream copy and formatting.", "lastSavedValue", actor.settings().locale(), "options", List.of(mapOf("value", "en-US", "label", "English (US)"), mapOf("value", "en-GB", "label", "English (UK)"), mapOf("value", "fr-FR", "label", "French (France)"), mapOf("value", "es-ES", "label", "Spanish (Spain)"))),
                mapOf("fieldId", "timeZone", "label", "Time zone", "value", actor.settings().timeZone(), "editable", true, "inputType", "select", "required", true, "helperText", "Personal timezone used for visible timestamps and digest scheduling hints.", "lastSavedValue", actor.settings().timeZone(), "options", List.of(mapOf("value", "America/New_York", "label", "America/New_York"), mapOf("value", "America/Chicago", "label", "America/Chicago"), mapOf("value", "America/Denver", "label", "America/Denver"), mapOf("value", "America/Los_Angeles", "label", "America/Los_Angeles"), mapOf("value", "Europe/London", "label", "Europe/London"), mapOf("value", "Europe/Paris", "label", "Europe/Paris"), mapOf("value", "UTC", "label", "UTC"))),
                mapOf("fieldId", "notificationCategories", "label", "Notification categories", "value", "visible in-app categories only", "editable", false, "inputType", "text", "disabledReason", "Open the notification center to tune visible in-app notification categories; external delivery/provider controls stay out of My Account settings.")),
            "version", 1,
            "permissionState", mapOf("canEdit", true, "reason", "Only changed editable personal preference fields are submitted; unsupported authorization/provider/tenant fields are denied before mutation.", "authoritativeCapabilityId", MY_ACCOUNT_UPDATE_SETTINGS_CAPABILITY, "saveActionId", "action-update-my-settings"),
            "traceRefs", traceRefs,
            "redaction", mapOf("settings", "self-service-preferences", "omittedFieldKeys", List.of("rawJwt", "sessionToken", "providerPayload", "providerSecret", "hiddenCategories", "tenantBranding", "modelProviderConfig", "arbitraryCss")),
            "audit", mapOf("lastEventType", "UserSettingsDisplayed", "lastActor", actor.profile().displayName(), "traceIds", traceRefs.stream().map(MyAccountService.TraceRef::traceId).toList())), List.of(updateSettingsAction(), showNotificationCenterAction(), openAuditAction()));
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
    var selected = selectedMembership(actor);
    var selectedContextLabel = contextLabel(actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var traceRefs = myAccountService.traceRefs(actor, correlationId);
    return envelope(surfaceId, "detail-edit", "Selected context and authority", actor, correlationId,
        mapOf(
            "surfaceContract", "my_account.context_authority.v1",
            "canonicalSurfaceId", "surface-my-context",
            "selectedContext", mapOf(
                "contextId", actor.selectedContext().membershipId(),
                "selectedContextId", actor.selectedContext().membershipId(),
                "contextType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
                "tenantId", actor.selectedContext().tenantId(),
                "tenantLabel", actor.selectedContext().tenantId(),
                "customerId", actor.selectedContext().customerId(),
                "customerLabel", actor.selectedContext().customerId(),
                "displayLabel", selectedContextLabel,
                "accountDisplayName", actor.profile().displayName(),
                "accountEmail", actor.account().displayEmail(),
                "membershipId", actor.selectedContext().membershipId(),
                "membershipStatus", selected == null ? "active" : selected.status().name().toLowerCase(Locale.ROOT),
                "selectedAt", Instant.now().toString(),
                "freshness", "backend-resolved",
                "stale", false),
            "authorityBasis", mapOf(
                "primaryRoleBasis", "active membership in selected context",
                "roleLabels", roleIds(actor.selectedContext().roles()),
                "capabilityCount", actor.selectedContext().capabilities().size(),
                "redactionProfile", "BROWSER_SAFE",
                "supportAccess", supportAccessSummary(selected),
                "explanation", "The selected AuthContext governs workstream visibility, browser actions, trace access, and agent behavior; browser state cannot grant authority."),
            "roleSummary", roleIds(actor.selectedContext().roles()),
            "visibleCapabilitySummary", mapOf(
                "count", actor.selectedContext().capabilities().size(),
                "categories", capabilityCategories(actor.selectedContext().capabilities()),
                "capabilityIds", actor.selectedContext().capabilities(),
                "diagnosticIdsVisible", true),
            "supportAccess", supportAccessSummary(selected),
            "recordId", actor.selectedContext().membershipId(),
            "recordLabel", selectedContextLabel + " selected context",
            "recordKind", "auth-context",
            "summary", "Backend-resolved selected AuthContext, active membership, role/capability basis, and available context switch targets. Context selection is performed by /api/me and protected workstream APIs using X-Selected-Context-Id; the browser cannot grant authority by editing this surface.",
            "fields", List.of(
                mapOf("fieldId", "selectedContextId", "label", "Selected context", "value", actor.selectedContext().membershipId(), "editable", false, "inputType", "text", "disabledReason", "Context changes must be requested through /api/me or protected shell APIs."),
                mapOf("fieldId", "tenantId", "label", "Tenant", "value", actor.selectedContext().tenantId(), "editable", false, "inputType", "text"),
                mapOf("fieldId", "customerId", "label", "Customer", "value", actor.selectedContext().customerId(), "editable", false, "inputType", "text"),
                mapOf("fieldId", "roles", "label", "Roles", "value", String.join(", ", roleIds(actor.selectedContext().roles())), "editable", false, "inputType", "text"),
                mapOf("fieldId", "capabilityCount", "label", "Visible capabilities", "value", actor.selectedContext().capabilities().size(), "editable", false, "inputType", "number"),
                mapOf("fieldId", "supportAccess", "label", "Support access", "value", selected != null && selected.supportAccess() ? "active" : "not visible", "editable", false, "inputType", "text", "disabledReason", "Support-access grants are managed by User Admin, not My Account.")),
            "availableContexts", actor.memberships().stream().filter(Membership::active).map(membership -> mapOf(
                "contextId", membership.membershipId(),
                "selectedContextId", membership.membershipId(),
                "contextType", membership.scopeType().name().toLowerCase(Locale.ROOT),
                "tenantId", membership.tenantId(),
                "tenantLabel", membership.tenantId(),
                "customerId", membership.customerId(),
                "customerLabel", membership.customerId(),
                "displayLabel", contextLabel(membership.tenantId(), membership.customerId()),
                "membershipStatus", membership.status().name().toLowerCase(Locale.ROOT),
                "status", membership.status().name().toLowerCase(Locale.ROOT),
                "roleLabels", roleIds(membership.roles()),
                "roleIds", roleIds(membership.roles()),
                "selectable", membership.active(),
                "selected", actor.selectedContext().membershipId().equals(membership.membershipId()),
                "stale", !actor.selectedContext().membershipId().equals(membership.membershipId()),
                "denialHint", membership.active() ? null : "not_found_or_redacted",
                "selectionApi", "/api/me?selectedContextId=" + membership.membershipId(),
                "selectionActionId", "action-select-my-context",
                "actionId", "action-select-my-context",
                "capabilityId", CORE_ACCESS_CONTEXT_SELECT_CAPABILITY,
                "lastUsed", actor.selectedContext().membershipId().equals(membership.membershipId()) ? "current" : null,
                "recommended", actor.selectedContext().membershipId().equals(membership.membershipId()),
                "staleImpact", "Switching context refreshes the shell, workstream counters, traces, notifications, and any open structured surfaces.")).toList(),
            "permissionState", mapOf("canEdit", false, "reason", "Context selection is a backend AuthContext operation, not an editable role/capability form.", "authoritativeCapabilityId", MY_ACCOUNT_VIEW_CONTEXT_CAPABILITY, "coreCapabilityAlias", CORE_ACCESS_CONTEXT_SELECT_CAPABILITY, "selectActionId", "action-select-my-context"),
            "capabilityAliases", List.of(CORE_ACCESS_ME_CAPABILITY, CORE_ACCESS_CONTEXT_SELECT_CAPABILITY),
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", mapOf("profile", "context-authority", "omittedFieldKeys", List.of("rawJwt", "sessionToken", "providerPayload", "providerSecret", "hiddenContexts", "hiddenRoleInternals", "crossTenantCounts"), "hiddenContextBehavior", "not_found_or_redacted"),
            "stateHints", List.of("ready", "submitting", "forbidden", "not_found_or_redacted", "stale/reconnect", "no-membership", "disabled-account", "no-op", "failure"),
            "audit", mapOf("lastEventType", "AuthContextDisplayed", "lastActor", actor.profile().displayName(), "traceIds", traceRefs.stream().map(MyAccountService.TraceRef::traceId).toList()))
        , List.of(showDashboardAction(), showProfileAction(), showSettingsAction(), selectContextAction(), openAuditAction()));
  }

  private String contextLabel(String tenantId, String customerId) {
    return tenantId + (customerId == null || customerId.isBlank() ? " · Tenant scope" : " · Customer " + customerId);
  }

  private List<String> roleIds(List<FoundationRole> roles) {
    return roles.stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList();
  }

  private Map<String, Object> supportAccessSummary(Membership membership) {
    return mapOf(
        "state", membership != null && membership.supportAccess() ? "active" : "not-visible",
        "active", membership != null && membership.supportAccess(),
        "expiresAt", membership == null || membership.expiresAt() == null ? null : membership.expiresAt().toString(),
        "recoveryHint", "Support-access grants are administered through User Admin and are never editable from My Account.");
  }

  private List<Map<String, Object>> capabilityCategories(List<String> capabilities) {
    return Stream.of(
            mapOf("categoryId", "my-account", "label", "My Account", "count", capabilities.stream().filter(capability -> capability.startsWith("my_account.")).count()),
            mapOf("categoryId", "core-access", "label", "Core access", "count", capabilities.stream().filter(capability -> capability.startsWith("core.") || capability.startsWith("profile.")).count()),
            mapOf("categoryId", "workstreams", "label", "Authorized workstreams", "count", capabilities.stream().filter(capability -> capability.contains(".") && !capability.startsWith("my_account.") && !capability.startsWith("core.") && !capability.startsWith("profile.")).count()))
        .filter(category -> ((Number) category.get("count")).longValue() > 0)
        .toList();
  }

  private SurfaceEnvelope myAccountOpenDeniedSurface(AuthContextResolver.ResolvedMe actor, MyAccountService.OpenWorkstreamDecision decision, String correlationId) {
    return myAccountOpenDeniedSurface(actor, decision, correlationId, null);
  }

  private SurfaceEnvelope myAccountOpenDeniedSurface(AuthContextResolver.ResolvedMe actor, MyAccountService.OpenWorkstreamDecision decision, String correlationId, String sourceActionId) {
    var traceIds = decision.traceIds() == null || decision.traceIds().isEmpty() ? List.of("trace-my-account-open-denied-" + stableSuffix(correlationId)) : decision.traceIds();
    var safeReasonCode = firstNonBlank(decision.safeReasonCode(), "not_available_in_selected_context");
    var actions = new ArrayList<SurfaceAction>();
    actions.add(showDashboardAction());
    actions.add(showContextAction());
    if (isActionCapabilityVisible(actor, openAuditAction().capabilityId())) actions.add(openAuditAction());
    var availableActions = actions.stream()
        .map(action -> mapOf(
            "actionId", action.actionId(),
            "label", action.label(),
            "capabilityId", action.capabilityId(),
            "resultSurface", action.resultSurface().updateSurfaceId(),
            "enabled", isActionCapabilityVisible(actor, action.capabilityId()),
            "disabledReason", isActionCapabilityVisible(actor, action.capabilityId()) ? "" : "Recovery action is not authorized in the selected context.",
            "correlationBehavior", "uses request correlation id and backend authorization"))
        .toList();
    return envelope("surface-my-account-open-denied", "system_message", "Workstream unavailable", actor, correlationId,
        mapOf(
            "surfaceContract", "my_account.open_denied.v1",
            "status", "not_found_or_redacted",
            "decision", "not_found_or_redacted",
            "safeReasonCode", safeReasonCode,
            "severity", "warning",
            "title", "Workstream unavailable",
            "message", decision.message(),
            "accountContext", mapOf("accountId", actor.account().accountId(), "email", actor.account().displayEmail(), "displayName", actor.profile().displayName(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "membershipStatus", "active", "redactionLevel", "browser-safe"),
            "requestedTargetSummary", mapOf("targetKind", "workstream", "label", "Requested destination", "visibility", "redacted", "noEnumeration", true),
            "sourceAction", sourceActionId == null || sourceActionId.isBlank() ? null : mapOf("actionId", "previous action", "label", "Previous action", "visible", false),
            "capabilityId", MY_ACCOUNT_OPEN_WORKSTREAM_CAPABILITY,
            "recoverySteps", List.of("Return to the My Account dashboard for authorized workstreams.", "Refresh selected context and authority basis in My Account.", "Ask an administrator for access if this workstream should be available.", "Open related trace evidence only when authorized."),
            "recoveryStepDetails", List.of(
                mapOf("stepId", "return-dashboard", "label", "Return to My Account", "description", "Reload the backend-owned My Account dashboard in the current selected context.", "enabled", true, "actionId", "action-show-my-account-dashboard", "changesSelectedContext", false),
                mapOf("stepId", "refresh-context", "label", "Refresh selected context", "description", "Re-resolve browser-safe context, membership, and capability summaries.", "enabled", true, "actionId", "action-show-my-context", "changesSelectedContext", false),
                mapOf("stepId", "request-access-guidance", "label", "Request access guidance", "description", "No self-service request-access workflow is configured for this protected target.", "enabled", false, "disabledReason", "Request-access guidance is unavailable in this foundation runtime path.", "changesSelectedContext", false),
                mapOf("stepId", "open-related-trace", "label", "Open related trace", "description", "Role-gated Audit/Trace evidence may be opened when authorized.", "enabled", isActionCapabilityVisible(actor, openAuditAction().capabilityId()), "actionId", "action-open-audit-trace", "changesSelectedContext", false)),
            "availableActions", availableActions,
            "traceRefs", traceIds,
            "correlationId", correlationId,
            "redaction", "Hidden workstream names, required roles, missing capabilities, protected target ids, provider records, raw JWT/session data, and cross-tenant/customer facts are not enumerated.",
            "noEnumeration", true,
            "safety", mapOf("sanitized", true, "redactionNote", "No raw JWTs, provider secrets, hidden target names, missing roles, stack traces, or fixture target data are rendered."),
            "trace", mapOf("correlationId", correlationId, "traceIds", traceIds)),
        actions);
  }

  private CapabilityActionResult personalAttentionDigestActionResult(MyAccountPersonalAttentionDigestTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), personalAttentionDigestSurface(actor, task, correlationId));
  }

  private SurfaceEnvelope personalAttentionDigestSurface(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask task, String correlationId) {
    if (task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME) return personalAttentionDigestBlockedSurface(actor, task, correlationId);
    var completed = task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY || task.status() == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED || task.status() == MyAccountPersonalAttentionDigestTask.Status.REJECTED;
    return envelope(completed ? "surface-my-account-personal-attention-digest-result" : "surface-my-account-personal-attention-digest-progress", completed ? "outcome-panel" : "workflow-status", completed ? "Personal attention digest result" : "Personal attention digest progress", actor, correlationId,
        mapOf("surfaceContract", completed ? "my_account.personal_attention_digest.result.v1" : "my_account.personal_attention_digest.progress.v1", "digestTaskId", task.digestTaskId(), "autonomousAgentTaskId", task.autonomousAgentTaskId(), "accountContext", mapOf("accountId", actor.account().accountId(), "email", actor.account().displayEmail(), "displayName", actor.profile().displayName(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "redactionLevel", "browser-safe"), "status", task.status().name().toLowerCase(Locale.ROOT), "phase", task.status().name().toLowerCase(Locale.ROOT).replace('_', '-'), "progressPercent", task.progressPercent(), "summary", task.summary(), "reviewStatus", digestReviewStatus(task), "authorizedAttentionCount", task.authorizedAttentionCount(), "sectionRefs", task.sectionRefs(), "evidenceRefs", digestEvidenceRefs(task), "materialEvents", digestEvidenceRefs(task), "pendingDecisions", digestPendingDecisions(task), "recommendations", digestRecommendations(task), "omissions", digestOmissions(task), "redactionSummary", digestOmissions(task), "authorizedSourceCounts", mapOf("visibleAttentionCount", task.authorizedAttentionCount(), "visibleWorkstreamCount", task.authorizedAttentionCount() == 0 ? 0 : 1, "materialEventCount", task.evidenceRefs().size(), "pendingDecisionCount", task.resultDecisionAllowed() ? 1 : 0, "omittedRedactedCount", "aggregate-only"), "evidenceWindow", mapOf("scope", "selected AuthContext", "startedAt", task.createdAt().toString(), "updatedAt", task.updatedAt().toString(), "hiddenSources", "omitted without names"), "confidenceNotes", List.of("backend-projected", "authorized-evidence-only"), "qualityNotes", List.of(task.blockerCode() == null ? "source attention remains authoritative" : "provider/runtime quality limited: " + task.blockerCode()), "sourceSurfaceRefs", digestSourceSurfaceRefs(task), "decisionState", task.status().name().toLowerCase(Locale.ROOT), "progressEvents", digestProgressEvents(task), "availableActions", completed ? List.of("action-read-my-account-personal-attention-digest", "action-accept-my-account-personal-attention-digest", "action-reject-my-account-personal-attention-digest") : List.of("action-read-my-account-personal-attention-digest", "action-cancel-my-account-personal-attention-digest"), "traceRefs", task.traceIds(), "redaction", "Authorized personal attention evidence only; hidden workstreams/items are not counted or named.", "noDirectMutation", true, "advisoryOnly", true, "safety", "This digest is advisory. Source attention remains authoritative and source item lifecycle changes require separate governed capabilities."),
        completed ? List.of(readPersonalAttentionDigestAction(), acceptPersonalAttentionDigestAction(), rejectPersonalAttentionDigestAction(), openAuditAction()) : List.of(readPersonalAttentionDigestAction(), cancelPersonalAttentionDigestAction(), openAuditAction()));
  }

  private Map<String, Object> digestReviewStatus(MyAccountPersonalAttentionDigestTask task) {
    var decisionState = task.status().name().toLowerCase(Locale.ROOT);
    var reviewed = task.status() == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED || task.status() == MyAccountPersonalAttentionDigestTask.Status.REJECTED;
    return mapOf(
        "state", decisionState,
        "label", reviewed ? "Advisory digest " + decisionState : task.resultDecisionAllowed() ? "Review required" : "Not ready for review",
        "freshness", task.updatedAt().toString(),
        "resultVersion", task.digestTaskId() + ":" + task.updatedAt().toEpochMilli(),
        "advisoryOnly", true,
        "decisionReason", task.decisionReason(),
        "staleHint", reviewed ? "Further review actions are idempotent for the recorded decision and conflict for different terminal decisions." : "Refresh before accepting or rejecting if a newer digest exists.");
  }

  private List<Map<String, Object>> digestPendingDecisions(MyAccountPersonalAttentionDigestTask task) {
    if (!task.resultDecisionAllowed()) return List.of();
    return List.of(mapOf(
        "decisionId", "review-" + task.digestTaskId(),
        "title", "Accept or reject the advisory personal attention digest",
        "decisionType", "advisory_review",
        "requiredCapabilitySummary", "My Account personal attention digest accept/reject",
        "redactionNote", "Review decision records only the advisory outcome; source attention is unchanged."));
  }

  private Map<String, Object> digestOmissions(MyAccountPersonalAttentionDigestTask task) {
    return mapOf(
        "visibleSourceCount", task.authorizedAttentionCount(),
        "omittedRedactedCountSummary", "Hidden, cross-tenant, stale, provider-unavailable, or unauthorized sources are omitted without names.",
        "reasonCategories", List.of("hidden_source", "not_authorized", "provider_unavailable", "stale_source", "quality_limited"),
        "recoveryGuidance", "Open authorized source workstreams or Audit/Trace links for details; unavailable sources remain redacted.");
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
    var taskVisible = task != null;
    var blockerCode = taskVisible && task.blockerCode() != null && !task.blockerCode().isBlank() ? task.blockerCode() : "provider_or_model_not_configured";
    var traceRefs = taskVisible && !task.traceIds().isEmpty() ? task.traceIds() : List.of("trace-my-account-personal-attention-digest-blocked-" + stableSuffix(correlationId));
    return envelope("surface-my-account-personal-attention-digest-blocked", "system_message", "Personal attention digest blocked", actor, correlationId,
        mapOf(
            "surfaceContract", "my_account.personal_attention_digest.blocked.v1",
            "digestTaskId", taskVisible ? task.digestTaskId() : "",
            "autonomousAgentTaskId", taskVisible ? task.autonomousAgentTaskId() : "",
            "accountContext", mapOf("accountId", actor.account().accountId(), "email", actor.account().displayEmail(), "displayName", actor.profile().displayName(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId(), "redactionLevel", "browser-safe"),
            "status", "blocked_provider_or_runtime",
            "severity", "blocked",
            "blockerCode", blockerCode,
            "blockerCategory", blockerCode.contains("tool") ? "tool_boundary" : "provider_runtime_readiness",
            "title", "Provider/runtime configuration is required",
            "message", taskVisible ? task.summary() : "My Account personal attention digest fails closed until backend provider/runtime configuration is available.",
            "blockedAt", taskVisible ? task.updatedAt().toString() : "not_applicable",
            "lastAttemptedPhase", taskVisible ? task.status().name().toLowerCase(Locale.ROOT).replace('_', '-') : "readiness-check",
            "retryEligibility", mapOf("state", "retry_requires_admin_readiness", "explanation", "Retry remains disabled until the backend provider/runtime and governed tool grants are ready."),
            "recoverySteps", List.of(
                mapOf("stepId", "return-dashboard", "label", "Return to My Account", "description", "Go back to the My Account dashboard while provider/runtime readiness is restored.", "actorType", "user", "actionId", "action-show-my-account-dashboard"),
                mapOf("stepId", "retry-after-readiness", "label", "Retry after readiness is restored", "description", "Use refresh/read or start a replacement digest only after backend readiness is available.", "actorType", "user", "actionId", "action-read-my-account-personal-attention-digest", "disabledReason", "Provider/runtime readiness is currently fail-closed."),
                mapOf("stepId", "admin-readiness", "label", "Request provider/runtime readiness", "description", "An administrator must configure the Akka AutonomousAgent runtime/provider and governed tool grants before digest generation can succeed.", "actorType", "admin")),
            "adminReadinessHints", List.of(
                mapOf("hintId", "autonomous-agent-runtime", "state", "unavailable", "summary", "Akka AutonomousAgent runtime/provider readiness is not available to this governed digest path."),
                mapOf("hintId", "governed-tools", "state", "unavailable", "summary", "Required digest read/start tool grants must remain fail-closed until explicitly configured.")),
            "requiredCapabilityIds", List.of(MY_ACCOUNT_DIGEST_READ_CAPABILITY, MY_ACCOUNT_DIGEST_START_CAPABILITY, MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY),
            "requiredToolIds", List.of("read-personal-digest-task", "request-personal-digest-export"),
            "providerReadiness", mapOf("state", "unconfigured", "safeExplanation", "No approved model/provider readiness is available for normal digest generation."),
            "runtimeReadiness", mapOf("state", "unavailable", "safeExplanation", "The governed autonomous runtime path returned a fail-closed blocker instead of fixture or model-less success."),
            "evidenceWindow", mapOf("scope", "selected AuthContext", "visibleAttentionCount", taskVisible ? task.authorizedAttentionCount() : 0, "visibleSourceCount", taskVisible && task.authorizedAttentionCount() > 0 ? 1 : 0, "omittedRedactedCount", "aggregate-only", "noSourceAttentionMutated", true),
            "availableActions", List.of(
                mapOf("actionId", "action-read-my-account-personal-attention-digest", "label", "Refresh digest state", "capabilityId", MY_ACCOUNT_DIGEST_READ_CAPABILITY, "enabled", taskVisible, "disabledReason", taskVisible ? "" : "No authorized digest task is selected.", "targetSurfaceId", "surface-my-account-personal-attention-digest-progress", "correlationBehavior", "uses request correlation id"),
                mapOf("actionId", "action-start-my-account-personal-attention-digest", "label", "Start replacement digest after readiness", "capabilityId", MY_ACCOUNT_DIGEST_START_CAPABILITY, "enabled", false, "disabledReason", "Provider/runtime readiness is fail-closed.", "targetSurfaceId", "surface-my-account-personal-attention-digest-blocked", "correlationBehavior", "idempotent start returns blocked until readiness exists"),
                mapOf("actionId", "action-show-my-account-dashboard", "label", "Return to My Account", "capabilityId", MY_ACCOUNT_VIEW_SUMMARY_CAPABILITY, "enabled", true, "targetSurfaceId", "surface-my-account-dashboard", "correlationBehavior", "dashboard read is trace-linked")),
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", "No deterministic, fake, fixture, simulated, or model-less personal attention digest success is returned; provider/model/tool details are browser-safe summaries only.",
            "noFakeSuccess", true,
            "noDirectMutation", true),
        List.of(showDashboardAction(), readPersonalAttentionDigestAction(), startPersonalAttentionDigestAction(), openAuditAction()));
  }

  private SurfaceEnvelope personalAttentionDigestEmptyResultSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return envelope("surface-my-account-personal-attention-digest-result", "outcome-panel", "Personal attention digest result", actor, correlationId,
        mapOf("surfaceContract", "my_account.personal_attention_digest.result.v1", "decisionState", "not_found_or_redacted", "reviewStatus", mapOf("state", "not_found_or_redacted", "label", "No completed digest result is selected", "advisoryOnly", true), "summary", "No completed personal attention digest result is available until a backend-governed task reaches review-required state.", "recommendations", List.of(), "materialEvents", List.of(), "pendingDecisions", List.of(), "omissions", mapOf("reasonCategories", List.of("no_result_selected"), "recoveryGuidance", "Start or refresh personal digest progress from My Account."), "redactionSummary", "No hidden task ids, workstreams, or source items are enumerated.", "authorizedSourceCounts", mapOf("visibleAttentionCount", 0, "omittedRedactedCount", 0), "evidenceWindow", mapOf("scope", "selected AuthContext", "state", "empty"), "confidenceNotes", List.of("no completed backend task selected"), "qualityNotes", List.of("normal runtime does not return fixture or model-less digest success"), "sourceSurfaceRefs", List.of(), "availableActions", List.of("action-start-my-account-personal-attention-digest", "action-read-my-account-personal-attention-digest"), "traceRefs", List.of("trace-my-account-personal-attention-digest-result-empty"), "correlationId", correlationId, "redaction", "No result details are exposed without an authorized completed task.", "advisoryOnly", true, "noDirectMutation", true),
        List.of(startPersonalAttentionDigestAction(), readPersonalAttentionDigestAction(), openAuditAction()));
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
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-detail", "show-inspection", "Organization Detail", "user_admin.organization_detail.v1", withOrganizationBranchReturn(List.of(openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), showOrganizationAdminsAction(), openOrganizationAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "user_admin.organization_create.v1", correlationId);
    var branchNavigation = organizationBranchNavigation(correlationId);
    var traceRefs = List.of("trace-organization-create-form-" + stableSuffix(correlationId));
    return envelope("surface-user-admin-organization-create", "create-form", "Create Organization", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.organization_create.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext and saas_owner.organization.create mapped to internal Tenant lifecycle management before form load and submit; browser state cannot grant Organization authority.",
            "branchNavigation", branchNavigation,
            "branchRootSurfaceId", branchNavigation.get("branchRootSurfaceId"),
            "branchReturnActionId", branchNavigation.get("branchReturnActionId"),
            "branchReturnLabel", branchNavigation.get("branchReturnLabel"),
            "recordKind", "organization",
            "summary", "Create a customer-facing Organization backed by an isolated Tenant boundary. Organization Admin bootstrap remains a separate backend-authorized task surface.",
            "formState", "ready",
            "validationMessages", List.of(),
            "draft", mapOf("organizationName", "", "reason", ""),
            "form", mapOf("organizationNameDraft", "", "reasonDraft", "", "submitLabel", "Create Organization", "submitActionId", "action-submit-organization-create", "cancelActionId", "action-user-admin-show-organizations", "idempotencyKeyHint", "client-generated"),
            "validationPolicy", mapOf("organizationNameRules", List.of("2 to 120 characters", "browser-safe Organization display name only"), "reasonRequired", false, "duplicateNamePolicy", "visible duplicate names return no-op/detail rather than creating a second visible Organization", "maxLength", 120, "allowedCharacterSummary", "User-facing Organization display name; raw tenant ids and provider ids are not accepted from the browser.", "idempotentReplayPolicy", "Client-generated idempotency keys return the same browser-safe Organization detail on replay.", "traceRefs", traceRefs),
            "creationBoundary", mapOf("createsIsolatedTenantBoundary", true, "initialLifecycleStatus", "active", "tenantApplicationDataExcluded", true, "organizationAdminBootstrapSeparateSurfaceId", "surface-user-admin-organization-admin-invitation-create", "traceRefs", traceRefs),
            "authorizedActions", List.of("action-open-organization-create", "action-submit-organization-create", "action-user-admin-show-organizations", "action-open-audit-trace", "action-user-admin-return-dashboard"),
            "availableTaskActions", withOrganizationBranchReturn(List.of(submitOrganizationCreateAction(), openAuditAction())).stream().map(this::surfaceActionSummary).toList(),
            "systemStates", List.of("ready"),
            "lastResult", mapOf("status", "ready", "message", "Organization create form loaded through protected workstream runtime path.", "correlationId", correlationId),
            "idempotencyKeyHint", "client-generated",
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", List.of("tenant-app-data-redacted", "customer-facts-redacted", "hidden-organization-counts-redacted", "raw-workos-ids-redacted", "raw-jwt-redacted", "provider-secrets-redacted", "billing-authority-redacted", "support-access-internals-redacted", "raw-idempotency-redacted"),
            "boundaryNotice", "Organization creation manages the isolated Tenant lifecycle boundary only; it does not expose tenant/customer application data, provider secrets, support access internals, billing authority, or Organization Admin bootstrap inline."),
        withOrganizationBranchReturn(List.of(submitOrganizationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationRenameSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_ORGANIZATION_RENAME_CAPABILITY, "user_admin.organization_rename.v1", correlationId);
    var branchNavigation = organizationBranchNavigation(correlationId);
    var traceRefs = List.of("trace-organization-rename-form-missing-target-" + stableSuffix(correlationId));
    return envelope("surface-user-admin-organization-rename", "edit-form", "Rename Organization", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.organization_rename.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext and saas_owner.organization.rename mapped to internal tenant lifecycle management before form load and submit; browser state cannot grant Organization authority.",
            "branchNavigation", branchNavigation,
            "branchRootSurfaceId", branchNavigation.get("branchRootSurfaceId"),
            "branchReturnActionId", branchNavigation.get("branchReturnActionId"),
            "branchReturnLabel", branchNavigation.get("branchReturnLabel"),
            "detailReturnActionId", "action-organization-read",
            "boundaryNotice", "Open rename from a visible Organization detail so the backend can bind the edit form to exactly one Organization/Tenant boundary.",
            "safeBoundaryNotice", "Organization rename updates the display label only and does not expose tenant/customer application data, billing, provider state, or support-access internals.",
            "organizations", List.of(),
            "formState", "missing-target",
            "validationMessages", List.of("Choose a visible Organization detail before renaming."),
            "form", mapOf("currentOrganizationName", "", "proposedOrganizationNameDraft", "", "reasonDraft", "", "submitLabel", "Rename Organization", "submitActionId", "action-submit-organization-rename", "cancelActionId", "action-organization-read", "idempotencyKeyHint", "client-generated", "disabledReason", "missing-visible-organization"),
            "validationPolicy", organizationRenameValidationPolicy(traceRefs),
            "changePreview", mapOf("currentDisplayName", "", "proposedDisplayName", "", "visibleImpactSummary", "No Organization is selected; no rename can run from this direct form load.", "tenantApplicationDataExcluded", true, "customersUnaffectedByRename", true, "traceRefs", traceRefs),
            "authorizedActions", withOrganizationBranchReturn(List.of(submitOrganizationRenameAction(), organizationReadAction(), openAuditAction())).stream().map(SurfaceAction::actionId).toList(),
            "availableTaskActions", withOrganizationBranchReturn(List.of(submitOrganizationRenameAction(), organizationReadAction(), openAuditAction())).stream().map(this::surfaceActionSummary).toList(),
            "systemStates", List.of("missing-target"),
            "lastResult", mapOf("status", "missing-target", "message", "Organization rename requires a visible Organization target loaded through the protected detail-to-rename action path.", "correlationId", correlationId, "traceRefs", traceRefs, "noFakeSuccess", true),
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", List.of("tenant-app-data-redacted", "customer-facts-redacted", "hidden-organization-counts-redacted", "raw-workos-ids-redacted", "raw-jwt-redacted", "provider-secrets-redacted", "billing-authority-redacted", "support-access-internals-redacted", "raw-idempotency-redacted")),
        withOrganizationBranchReturn(List.of(submitOrganizationRenameAction(), organizationReadAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationRenameSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_ORGANIZATION_RENAME_CAPABILITY, "user_admin.organization_rename.v1", correlationId);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-rename", "edit-form", "Rename Organization", "user_admin.organization_rename.v1", withOrganizationBranchReturn(List.of(submitOrganizationRenameAction(), organizationReadAction(), openAuditAction())), readOrganizationDetail(actor, input, correlationId));
  }

  private SurfaceEnvelope organizationSuspendSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_ORGANIZATION_SUSPEND_CAPABILITY, "user_admin.organization_suspend_confirmation.v1", correlationId);
    var branchNavigation = organizationBranchNavigation(correlationId);
    var traceRefs = List.of("trace-organization-suspend-confirmation-missing-target-" + stableSuffix(correlationId));
    return envelope("surface-user-admin-organization-suspend-confirmation", "destructive-lifecycle-confirmation", "Suspend Organization", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.organization_suspend_confirmation.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext and saas_owner.organization.suspend mapped to internal tenant lifecycle management before confirmation load and submit; browser state cannot grant Organization authority.",
            "branchNavigation", branchNavigation,
            "branchRootSurfaceId", branchNavigation.get("branchRootSurfaceId"),
            "branchReturnActionId", branchNavigation.get("branchReturnActionId"),
            "branchReturnLabel", branchNavigation.get("branchReturnLabel"),
            "detailReturnActionId", "action-organization-read",
            "boundaryNotice", "Open suspend from a visible active Organization detail so the backend can bind the destructive confirmation to exactly one Organization/Tenant boundary.",
            "safeBoundaryNotice", "Organization suspension changes only the Tenant lifecycle boundary and does not expose tenant/customer application data, billing, provider state, Organization Admin roles, or support-access internals.",
            "organizations", List.of(),
            "formState", "missing-target",
            "validationMessages", List.of("Choose a visible active Organization detail before suspending."),
            "confirmation", mapOf("consequenceCopy", "Suspending changes only the selected Organization/Tenant lifecycle boundary; tenant application data, Customer records, billing/provider settings, Organization Admin roles, and support access stay outside this action.", "confirmationPhrase", "SUSPEND", "confirmationRequired", true, "reasonDraft", "", "reasonRequired", true, "submitLabel", "Suspend Organization", "submitActionId", "action-organization-suspend", "cancelActionId", "action-organization-read", "idempotencyKeyHint", "client-generated", "freshnessVersionHint", "detail-trace-boundary", "disabledReason", "missing-visible-active-organization"),
            "suspensionEligibility", mapOf("currentStatus", "unknown", "allowedStatuses", List.of("active"), "blockedReason", "missing-visible-active-organization", "approvalRequired", false, "traceRefs", traceRefs),
            "authorizedActions", withOrganizationBranchReturn(List.of(organizationSuspendAction(), organizationReadAction(), openAuditAction())).stream().map(SurfaceAction::actionId).toList(),
            "availableTaskActions", withOrganizationBranchReturn(List.of(organizationSuspendAction(), organizationReadAction(), openAuditAction())).stream().map(this::surfaceActionSummary).toList(),
            "systemStates", List.of("missing-target"),
            "lastResult", mapOf("status", "missing-target", "message", "Organization suspension requires a visible active Organization target loaded through the protected detail-to-suspend action path.", "correlationId", correlationId, "traceRefs", traceRefs, "noFakeSuccess", true),
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", List.of("tenant-app-data-redacted", "customer-facts-redacted", "hidden-organization-counts-redacted", "raw-workos-ids-redacted", "raw-jwt-redacted", "provider-secrets-redacted", "billing-authority-redacted", "support-access-internals-redacted", "raw-idempotency-redacted")),
        withOrganizationBranchReturn(List.of(organizationSuspendAction(), organizationReadAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationSuspendSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_ORGANIZATION_SUSPEND_CAPABILITY, "user_admin.organization_suspend_confirmation.v1", correlationId);
    var detail = readOrganizationDetail(actor, input, correlationId);
    requireOrganizationLifecycleAction(detail, "suspend", correlationId);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-suspend-confirmation", "destructive-lifecycle-confirmation", "Suspend Organization", "user_admin.organization_suspend_confirmation.v1", withOrganizationBranchReturn(List.of(organizationSuspendAction(), organizationReadAction(), openAuditAction())), detail);
  }

  private SurfaceEnvelope organizationReactivateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_ORGANIZATION_REACTIVATE_CAPABILITY, "user_admin.organization_reactivate_confirmation.v1", correlationId);
    var branchNavigation = organizationBranchNavigation(correlationId);
    var traceRefs = List.of("trace-organization-reactivate-confirmation-missing-target-" + stableSuffix(correlationId));
    return envelope("surface-user-admin-organization-reactivate-confirmation", "lifecycle-confirmation", "Reactivate Organization", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.organization_reactivate_confirmation.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext and saas_owner.organization.reactivate mapped to internal tenant lifecycle management before confirmation load and submit; browser state cannot grant Organization authority.",
            "branchNavigation", branchNavigation,
            "branchRootSurfaceId", branchNavigation.get("branchRootSurfaceId"),
            "branchReturnActionId", branchNavigation.get("branchReturnActionId"),
            "branchReturnLabel", branchNavigation.get("branchReturnLabel"),
            "detailReturnActionId", "action-organization-read",
            "boundaryNotice", "Open reactivate from a visible suspended Organization detail so the backend can bind the lifecycle confirmation to exactly one Organization/Tenant boundary.",
            "safeBoundaryNotice", "Organization reactivation changes only the Tenant lifecycle boundary and does not expose tenant/customer application data, billing, provider state, Organization Admin roles, or support-access internals.",
            "organizations", List.of(),
            "formState", "missing-target",
            "validationMessages", List.of("Choose a visible suspended Organization detail before reactivating."),
            "confirmation", mapOf("consequenceCopy", "Reactivation changes only the selected Organization/Tenant lifecycle boundary back to active administration; tenant application data, Customer records, billing/provider settings, Organization Admin roles, and support access stay outside this action.", "confirmationPhrase", "REACTIVATE", "confirmationRequired", true, "reasonDraft", "", "reasonRequired", true, "submitLabel", "Reactivate Organization", "submitActionId", "action-organization-reactivate", "cancelActionId", "action-organization-read", "idempotencyKeyHint", "client-generated", "freshnessVersionHint", "detail-trace-boundary", "disabledReason", "missing-visible-suspended-organization"),
            "reactivationEligibility", mapOf("currentStatus", "unknown", "allowedStatuses", List.of("suspended"), "blockedReason", "missing-visible-suspended-organization", "approvalRequired", false, "traceRefs", traceRefs),
            "authorizedActions", withOrganizationBranchReturn(List.of(organizationReactivateAction(), organizationReadAction(), openAuditAction())).stream().map(SurfaceAction::actionId).toList(),
            "availableTaskActions", withOrganizationBranchReturn(List.of(organizationReactivateAction(), organizationReadAction(), openAuditAction())).stream().map(this::surfaceActionSummary).toList(),
            "systemStates", List.of("missing-target"),
            "lastResult", mapOf("status", "missing-target", "message", "Organization reactivation requires a visible suspended Organization target loaded through the protected detail-to-reactivate action path.", "correlationId", correlationId, "traceRefs", traceRefs, "noFakeSuccess", true),
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", List.of("tenant-app-data-redacted", "customer-facts-redacted", "hidden-organization-counts-redacted", "raw-workos-ids-redacted", "raw-jwt-redacted", "provider-secrets-redacted", "billing-authority-redacted", "support-access-internals-redacted", "raw-idempotency-redacted")),
        withOrganizationBranchReturn(List.of(organizationReactivateAction(), organizationReadAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationReactivateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_ORGANIZATION_REACTIVATE_CAPABILITY, "user_admin.organization_reactivate_confirmation.v1", correlationId);
    var detail = readOrganizationDetail(actor, input, correlationId);
    requireOrganizationLifecycleAction(detail, "reactivate", correlationId);
    return organizationSurface(actor, correlationId, "surface-user-admin-organization-reactivate-confirmation", "lifecycle-confirmation", "Reactivate Organization", "user_admin.organization_reactivate_confirmation.v1", withOrganizationBranchReturn(List.of(organizationReactivateAction(), organizationReadAction(), openAuditAction())), detail);
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
    var activeOrganizationCount = organizations.stream().filter(organization -> "active".equals(organization.get("status"))).count();
    var suspendedOrganizationCount = organizations.stream().filter(organization -> "suspended".equals(organization.get("status"))).count();
    var branchNavigation = organizationBranchNavigation(correlationId);
    var data = mapOf(
        "surfaceContract", contract,
        "scopeLabel", "SaaS Owner scope",
        "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
        "authorityBasis", "Backend checks selected AuthContext and saas_owner.organization.* mapped to internal tenant lifecycle capabilities; browser state cannot grant Organization authority.",
        "branchNavigation", branchNavigation,
        "branchRootSurfaceId", branchNavigation.get("branchRootSurfaceId"),
        "branchReturnActionId", branchNavigation.get("branchReturnActionId"),
        "branchReturnLabel", branchNavigation.get("branchReturnLabel"),
        "boundaryNotice", boundary,
        "safeBoundaryNotice", boundary,
        "query", query == null ? "" : query,
        "traceRefs", traceRefs,
        "correlationId", correlationId,
        "redaction", List.of("tenant-app-data-redacted", "provider-secrets-redacted", "billing-authority-redacted", "support-access-internals-redacted", "hidden-counts-redacted"),
        "summary", mapOf("visibleOrganizationCount", organizations.size(), "activeOrganizationCount", activeOrganizationCount, "suspendedOrganizationCount", suspendedOrganizationCount, "pendingSetupCount", 0, "organizationAdminAttentionCount", 0, "invitationAttentionCount", 0, "providerBlockedCount", 0, "outboxBlockedCount", 0, "traceRefs", traceRefs),
        "organizations", organizations,
        "filters", mapOf("query", query == null ? "" : query, "status", status == null ? "" : status, "backendAuthored", true),
        "sort", mapOf("default", "backend-policy"),
        "pageInfo", mapOf("visibleCount", organizations.size(), "hasMore", false),
        "authorizedActions", actions.stream().map(SurfaceAction::actionId).toList(),
        "availableTaskActions", actions.stream().map(this::surfaceActionSummary).toList(),
        "systemStates", List.of(organizations.isEmpty() ? "empty" : "ready"),
        "emptyMessage", "No Organizations are visible for this backend-authorized filter.",
        "forbiddenMessage", "Unauthorized or hidden Organizations return a safe system message without enumeration.",
        "lastResult", mapOf("status", organizations.isEmpty() ? "empty" : "ready", "message", "Organization Directory loaded through backend-authorized workstream runtime path.", "correlationId", correlationId));
    if (detail != null) data.put("organizationDetail", organizationDetailMap(detail));
    if ("surface-user-admin-organization-rename".equals(surfaceId) && detail != null) addOrganizationRenameFormData(data, detail, traceRefs, correlationId);
    if ("surface-user-admin-organization-suspend-confirmation".equals(surfaceId) && detail != null) addOrganizationSuspendFormData(data, detail, traceRefs, correlationId);
    if ("surface-user-admin-organization-reactivate-confirmation".equals(surfaceId) && detail != null) addOrganizationReactivateFormData(data, detail, traceRefs, correlationId);
    return envelope(surfaceId, surfaceType, title, actor, correlationId, data, actions);
  }

  private void addOrganizationRenameFormData(Map<String, Object> data, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, List<String> traceRefs, String correlationId) {
    var organization = detail.organization();
    data.put("organizationId", organization.organizationId());
    data.put("organizationName", organization.organizationName());
    data.put("organizationStatus", organization.status());
    data.put("detailReturnActionId", "action-organization-read");
    data.put("formState", "ready");
    data.put("validationMessages", List.of());
    data.put("form", mapOf(
        "currentOrganizationName", organization.organizationName(),
        "organizationNameDraft", organization.organizationName(),
        "proposedOrganizationNameDraft", organization.organizationName(),
        "reasonDraft", "",
        "submitLabel", "Rename Organization",
        "submitActionId", "action-submit-organization-rename",
        "cancelActionId", "action-organization-read",
        "idempotencyKeyHint", "client-generated",
        "freshnessVersionHint", "detail-trace-boundary"));
    data.put("validationPolicy", organizationRenameValidationPolicy(traceRefs));
    data.put("changePreview", mapOf(
        "currentDisplayName", organization.organizationName(),
        "proposedDisplayName", organization.organizationName(),
        "visibleImpactSummary", "Rename updates the Organization display label only; Tenant isolation, Customers, Organization Admin roles, billing, provider state, support access, and tenant application data are not exposed or edited.",
        "tenantApplicationDataExcluded", true,
        "customersUnaffectedByRename", true,
        "traceRefs", traceRefs));
    data.put("lastResult", mapOf("status", "ready", "message", "Organization rename form loaded through protected detail-to-rename runtime path.", "correlationId", correlationId, "traceRefs", traceRefs));
  }

  private Map<String, Object> organizationRenameValidationPolicy(List<String> traceRefs) {
    return mapOf(
        "organizationNameRules", List.of("2 to 120 characters", "browser-safe Organization display name only", "must be distinct from visible Organization names"),
        "reasonRequired", false,
        "noOpPolicy", "Unchanged display names return a no-op detail result with audit/work trace evidence.",
        "duplicateNamePolicy", "Visible duplicate Organization names are rejected as validation/conflict without renaming; hidden duplicates use no-enumeration recovery.",
        "maxLength", 120,
        "allowedCharacterSummary", "User-facing Organization display name; raw tenant ids, provider ids, billing identifiers, and customer data are not accepted from the browser.",
        "freshnessRequired", true,
        "idempotentReplayPolicy", "Client-generated idempotency keys preserve replay safety for the selected visible Organization.",
        "traceRefs", traceRefs);
  }

  private void addOrganizationSuspendFormData(Map<String, Object> data, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, List<String> traceRefs, String correlationId) {
    var organization = detail.organization();
    data.put("organizationId", organization.organizationId());
    data.put("organizationName", organization.organizationName());
    data.put("organizationStatus", organization.status());
    data.put("detailReturnActionId", "action-organization-read");
    data.put("formState", "ready");
    data.put("validationMessages", List.of());
    data.put("confirmation", mapOf(
        "consequenceCopy", "Suspending changes only the selected Organization/Tenant lifecycle boundary; tenant application data, Customer records, billing/provider settings, Organization Admin roles, and support access stay outside this action.",
        "confirmationPhrase", "SUSPEND",
        "confirmationRequired", true,
        "reasonDraft", "",
        "reasonRequired", true,
        "submitLabel", "Suspend Organization",
        "submitActionId", "action-organization-suspend",
        "cancelActionId", "action-organization-read",
        "idempotencyKeyHint", "client-generated",
        "freshnessVersionHint", "detail-trace-boundary"));
    data.put("suspensionEligibility", mapOf(
        "currentStatus", organization.status(),
        "allowedStatuses", List.of("active"),
        "blockedReason", organization.status().equals("active") ? null : "unsupported-status",
        "lastAdminOrBootstrapRisk", false,
        "openCustomerOrAdminWorkWarning", "Suspension changes only the Organization/Tenant lifecycle boundary and does not expose Customer records or mutate Organization Admin roles inline.",
        "providerOrOutboxReadinessSummary", "No provider, billing, or outbox mutation is required for this Tenant lifecycle boundary action.",
        "approvalRequired", false,
        "traceRefs", traceRefs));
    data.put("lastResult", mapOf("status", "ready", "message", "Organization suspend confirmation loaded through protected detail-to-suspend runtime path.", "correlationId", correlationId, "traceRefs", traceRefs));
  }

  private void addOrganizationReactivateFormData(Map<String, Object> data, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, List<String> traceRefs, String correlationId) {
    var organization = detail.organization();
    data.put("organizationId", organization.organizationId());
    data.put("organizationName", organization.organizationName());
    data.put("organizationStatus", organization.status());
    data.put("detailReturnActionId", "action-organization-read");
    data.put("formState", "ready");
    data.put("validationMessages", List.of());
    data.put("confirmation", mapOf(
        "consequenceCopy", "Reactivation changes only the selected Organization/Tenant lifecycle boundary back to active administration; tenant application data, Customer records, billing/provider settings, Organization Admin roles, and support access stay outside this action.",
        "confirmationPhrase", "REACTIVATE",
        "confirmationRequired", true,
        "reasonDraft", "",
        "reasonRequired", true,
        "submitLabel", "Reactivate Organization",
        "submitActionId", "action-organization-reactivate",
        "cancelActionId", "action-organization-read",
        "idempotencyKeyHint", "client-generated",
        "freshnessVersionHint", "detail-trace-boundary"));
    data.put("reactivationEligibility", mapOf(
        "currentStatus", organization.status(),
        "allowedStatuses", List.of("suspended"),
        "blockedReason", organization.status().equals("suspended") ? null : "unsupported-status",
        "unresolvedSuspensionReasonSummary", "Browser-safe suspension reason details are available only through authorized audit evidence.",
        "providerOrOutboxReadinessSummary", "No provider, billing, or outbox mutation is required for this Tenant lifecycle boundary action.",
        "approvalRequired", false,
        "traceRefs", traceRefs));
    data.put("lastResult", mapOf("status", "ready", "message", "Organization reactivate confirmation loaded through protected detail-to-reactivate runtime path.", "correlationId", correlationId, "traceRefs", traceRefs));
  }

  private SurfaceEnvelope saasOwnerAdminsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_USER_MANAGE_CAPABILITY);
    var adminRows = userDirectoryView.list(actor, ScopeType.SAAS_OWNER, actor.selectedContext().tenantId(), null).stream()
        .map(user -> mapOf("id", user.membershipId(), "rowId", user.membershipId(), "recordKind", "admin_membership", "accountId", user.accountId(), "membershipId", user.membershipId(), "rowType", "saas-owner-admin", "targetObjectType", "saas-owner-admin", "targetSurfaceId", "surface-user-admin-user-detail", "targetSurfaceType", "show-inspection", "targetActionId", "action-display-user-detail", "openActionId", "action-display-user-detail", "displayName", user.displayName(), "email", user.accountId(), "roles", roleLabels(user.roles()), "role", roleLabels(user.roles()), "status", user.membershipStatus().name().toLowerCase(Locale.ROOT), "attentionBadges", List.of(), "actionAvailability", List.of("open-detail", "open-audit"), "traceRefs", List.of("trace-saas-owner-admin-" + stableSuffix(user.membershipId())), "redactionState", "visible"))
        .toList();
    var invitationRows = invitationView.list(actor, ScopeType.SAAS_OWNER, null, null).stream()
        .map(invitation -> mapOf("id", invitation.invitationId(), "rowId", invitation.invitationId(), "recordKind", "admin_invitation", "invitationId", invitation.invitationId(), "rowType", "saas-owner-admin-invitation", "targetObjectType", "saas-owner-admin-invitation", "targetSurfaceId", "surface-user-admin-invitation-detail", "targetSurfaceType", "show-inspection", "targetActionId", "action-display-invitation-detail", "openActionId", "action-display-invitation-detail", "displayName", invitation.targetEmail(), "email", invitation.targetEmail(), "roles", roleLabels(invitation.requestedRoles()), "role", roleLabels(invitation.requestedRoles()), "status", invitation.status().name().toLowerCase(Locale.ROOT), "invitationStatus", invitation.status().name().toLowerCase(Locale.ROOT), "deliveryStatus", invitation.deliveryStatus().name().toLowerCase(Locale.ROOT), "attentionBadges", invitation.canResend() ? List.of("resend-available") : List.of(), "actionAvailability", List.of("open-invitation-detail", "open-audit"), "traceRefs", List.of("trace-saas-owner-admin-invitation-" + stableSuffix(invitation.invitationId())), "redactionState", "invitation-token-redacted"))
        .toList();
    var rows = new ArrayList<Map<String, Object>>();
    rows.addAll(adminRows);
    rows.addAll(invitationRows);
    var activeAdminCount = adminRows.stream().filter(row -> "active".equals(row.get("status"))).count();
    var pendingInvitationCount = invitationRows.stream().filter(row -> "pending".equals(row.get("invitationStatus"))).count();
    var expiredInvitationCount = invitationRows.stream().filter(row -> "expired".equals(row.get("invitationStatus"))).count();
    var providerBlockedCount = invitationRows.stream().filter(row -> String.valueOf(row.get("deliveryStatus")).contains("failed") || String.valueOf(row.get("deliveryStatus")).contains("blocked")).count();
    return envelope("surface-user-admin-saas-owner-admins", "list-search", "SaaS Owner Admins", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.saas_owner_admins.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext and saas_owner.admin.list mapped to app-owner user-management capability; browser filters cannot grant authority.",
            "branchNavigation", saasOwnerAdminBranchNavigation(correlationId),
            "branchRootSurfaceId", "surface-user-admin-saas-owner-admins",
            "branchReturnActionId", "action-user-admin-show-saas-owner-admins",
            "branchReturnLabel", "Back to SaaS Owner Admins",
            "query", "",
            "rows", rows,
            "summary", mapOf("visibleAdminCount", adminRows.size(), "visibleInvitationCount", invitationRows.size(), "activeAdminCount", activeAdminCount, "pendingInvitationCount", pendingInvitationCount, "expiredInvitationCount", expiredInvitationCount, "lastOwnerAdminRiskCount", 0, "outboxBlockedCount", providerBlockedCount, "providerBlockedCount", providerBlockedCount, "reviewNeededCount", 0, "traceRefs", List.of("trace-saas-owner-admins-summary-" + stableSuffix(correlationId))),
            "filters", mapOf("scope", "saas-owner", "query", "", "status", List.of("active", "pending", "expired", "revoked"), "role", List.of("SAAS_OWNER_ADMIN"), "invitationStatus", List.of("pending", "expired", "accepted", "revoked"), "attentionState", List.of("resend-available", "provider-blocked", "last-owner-risk"), "backendAuthored", true),
            "sort", mapOf("default", "backend-policy"),
            "pageInfo", mapOf("visibleCount", rows.size(), "hasMore", false),
            "authorizedActions", List.of("action-user-admin-show-saas-owner-admins", "action-open-saas-owner-admin-invitation-create", "action-display-user-detail", "action-display-invitation-detail", "action-open-admin-audit"),
            "systemStates", List.of(rows.isEmpty() ? "empty" : "ready"),
            "lastResult", mapOf("status", "ready", "message", "SaaS Owner Admin list loaded through backend-authorized workstream runtime path.", "correlationId", correlationId),
            "redaction", List.of("raw-workos-ids-redacted", "raw-jwt-redacted", "invitation-token-redacted", "provider-payload-redacted", "tenant-customer-data-redacted", "hidden-app-owner-counts-redacted"),
            "emptyMessage", "No SaaS Owner Admin users or invitations are visible in this selected scope.",
            "boundaryNotice", "SaaS Owner Admin management is app-owner scoped and does not grant tenant/customer application-data, support-access, billing, provider, or model authority."),
        List.of(showSaasOwnerAdminsAction(), displayDetailAction(), displayInvitationDetailAction(), openSaasOwnerAdminInvitationCreateAction(), openAuditAction()));
  }

  private SurfaceEnvelope saasOwnerAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_USER_MANAGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, SAAS_OWNER_ADMIN_INVITE_CAPABILITY, "user_admin.saas_owner_admin_invitation_create.v1", correlationId);
    var roleOption = mapOf("roleId", "SAAS_OWNER_ADMIN", "value", "SAAS_OWNER_ADMIN", "label", "SaaS Owner Admin", "capabilitySummary", "App-owner administration only; does not grant tenant/customer application-data, billing, provider, or model authority.", "requiresApproval", false);
    return envelope("surface-user-admin-saas-owner-admin-invitation-create", "create-form", "Invite SaaS Owner Admin", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.saas_owner_admin_invitation_create.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext and saas_owner.admin.invite mapped to app-owner user-management capability before form load and submit.",
            "branchNavigation", saasOwnerAdminBranchNavigation(correlationId),
            "branchRootSurfaceId", "surface-user-admin-saas-owner-admins",
            "branchReturnActionId", "action-user-admin-show-saas-owner-admins",
            "branchReturnLabel", "Back to SaaS Owner Admins",
            "recordKind", "saas-owner-admin-invitation",
            "summary", "Invite another app-owner administrator. Backend authorization enforces SaaS Owner scope, SAAS_OWNER_ADMIN role validation, idempotency, outbox/provider readiness, and audit before any invitation is created.",
            "formState", "ready",
            "draft", mapOf("email", "", "displayName", "", "roles", List.of("SAAS_OWNER_ADMIN"), "reason", ""),
            "form", mapOf("emailDraft", "", "displayNameDraft", "", "reasonDraft", "", "targetRoleOptions", List.of(roleOption), "selectedTargetRole", "SAAS_OWNER_ADMIN", "idempotencyKeyHint", "client-generated", "submitLabel", "Invite SaaS Owner Admin", "cancelActionId", "action-user-admin-show-saas-owner-admins", "submitActionId", "action-submit-saas-owner-admin-invitation"),
            "roleOptions", List.of(roleOption),
            "policyContext", mapOf("lastOwnerAdminRisk", false, "duplicateOpenInvitePolicy", "reuse-visible-open-invitation", "approvalRequired", false, "traceRefs", List.of("trace-saas-owner-admin-policy-" + stableSuffix(correlationId))),
            "policyOptions", mapOf("roles", List.of(roleOption), "idempotency", "client-generated", "outboxReadiness", "backend-derived"),
            "deliveryReadiness", mapOf("outboxStatus", "queued-through-backend", "providerStatus", "configured-or-fail-closed", "retryEligible", true, "failClosedMessage", "Provider/outbox failures return a typed blocked detail or system-message surface; fake delivery success is forbidden.", "traceRefs", List.of("trace-saas-owner-admin-delivery-readiness-" + stableSuffix(correlationId))),
            "authorizedActions", List.of("action-submit-saas-owner-admin-invitation", "action-user-admin-show-saas-owner-admins", "action-open-saas-owner-admin-audit", "action-user-admin-return-dashboard"),
            "systemStates", List.of("ready"),
            "lastResult", mapOf("status", "ready", "message", "SaaS Owner Admin invitation form loaded through protected workstream runtime path.", "correlationId", correlationId),
            "idempotencyKeyHint", "client-generated",
            "traceRefs", List.of("trace-saas-owner-admin-invite-" + stableSuffix(correlationId)),
            "correlationId", correlationId,
            "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "outbox-secret-redacted", "raw-jwt-redacted", "hidden-app-owner-counts-redacted", "tenant-customer-data-redacted"),
            "boundaryNotice", "SaaS Owner Admin invitations are app-owner scoped and do not expose tenant/customer application data, invitation tokens, provider payloads, or hidden app-owner population details."),
        withSaasOwnerAdminBranchReturn(List.of(submitSaasOwnerAdminInvitationAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationAdminsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_READ_CAPABILITY);
    var branchNavigation = organizationBranchNavigation(correlationId);
    return envelope("surface-user-admin-organization-admins", "list-search", "Organization Admins", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.organization_admins.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext, visible Organization read authority, and saas_owner.organization_admin.list before loading rows; browser filters cannot grant Organization Admin authority.",
            "branchNavigation", branchNavigation,
            "branchRootSurfaceId", branchNavigation.get("branchRootSurfaceId"),
            "branchReturnActionId", branchNavigation.get("branchReturnActionId"),
            "branchReturnLabel", branchNavigation.get("branchReturnLabel"),
            "detailReturnActionId", "action-organization-read",
            "query", "",
            "rows", List.of(),
            "admins", List.of(),
            "invitations", List.of(),
            "filters", mapOf("role", "TENANT_ADMIN", "query", "", "membershipStatus", List.of("active", "suspended", "removed"), "invitationStatus", List.of("pending", "expired", "accepted", "revoked"), "attentionState", List.of("first-admin-bootstrap", "last-admin-risk", "provider-blocked"), "backendAuthored", true),
            "sort", mapOf("default", "backend-policy"),
            "pageInfo", mapOf("visibleCount", 0, "hasMore", false),
            "summary", "Select an Organization before listing Organization Admin users and invitations. Rows are backend-authored and never expose tenant application data.",
            "adminSummary", mapOf("visibleAdminCount", 0, "activeAdminCount", 0, "suspendedOrDisabledAdminCount", 0, "pendingInvitationCount", 0, "expiredInvitationCount", 0, "deliveryFailureCount", 0, "firstAdminBootstrapEligible", false, "lastAdminRiskCount", 0, "providerBlockedCount", 0, "outboxBlockedCount", 0, "traceRefs", List.of("trace-organization-admins-summary-" + stableSuffix(correlationId))),
            "authorizedActions", List.of("action-user-admin-show-organization-admins", "action-open-organization-admin-invitation-create", "action-open-organization-admin-detail", "action-open-organization-admin-invitation-detail", "action-organization-read", "action-open-audit-trace"),
            "availableTaskActions", organizationAdminListActions().stream().map(this::surfaceActionSummary).toList(),
            "systemStates", List.of("missing-target"),
            "lastResult", mapOf("status", "missing-target", "message", "Open Organization Admins from a visible Organization detail so the backend can include selected Organization scope proof.", "correlationId", correlationId),
            "traceRefs", List.of("trace-organization-admins-missing-target-" + stableSuffix(correlationId)),
            "correlationId", correlationId,
            "redaction", List.of("tenant-app-data-redacted", "customer-facts-redacted", "raw-workos-ids-redacted", "raw-jwt-redacted", "invitation-token-redacted", "provider-payload-redacted", "hidden-organization-admin-counts-redacted"),
            "emptyMessage", "Select a visible Organization before loading Organization Admins.",
            "boundaryNotice", "Organization Admin management is SaaS Owner scoped to one selected Organization/Tenant and does not expose tenant application data, customers, provider secrets, billing, support internals, or hidden admin counts."),
        organizationAdminListActions());
  }

  private SurfaceEnvelope organizationAdminsSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_READ_CAPABILITY);
    var detail = readOrganizationDetail(actor, input, correlationId);
    var organization = detail.organization();
    var organizationId = organization.organizationId();
    var adminRows = userDirectoryView.list(actor, ScopeType.TENANT, organizationId, null).stream()
        .filter(user -> user.roles().contains(FoundationRole.TENANT_ADMIN))
        .map(user -> mapOf(
            "id", user.membershipId(),
            "rowId", user.membershipId(),
            "recordKind", "organization_admin_membership",
            "accountId", user.accountId(),
            "membershipId", user.membershipId(),
            "tenantId", organizationId,
            "organizationId", organizationId,
            "organizationName", organization.organizationName(),
            "rowType", "organization-admin",
            "targetObjectType", "organization-admin-membership",
            "targetSurfaceId", "surface-user-admin-organization-admin-detail",
            "targetSurfaceType", "show-inspection",
            "targetActionId", "action-open-organization-admin-detail",
            "openActionId", "action-open-organization-admin-detail",
            "displayName", user.displayName(),
            "email", user.accountId(),
            "roles", roleLabels(user.roles()),
            "role", roleLabels(user.roles()),
            "status", user.membershipStatus().name().toLowerCase(Locale.ROOT),
            "lastAdminRisk", false,
            "attentionBadges", user.membershipStatus() == MembershipStatus.ACTIVE ? List.of() : List.of("membership-not-active"),
            "actionAvailability", List.of("open-detail", "open-audit"),
            "safeActionContext", mapOf("organizationId", organizationId, "tenantId", organizationId, "membershipId", user.membershipId(), "accountId", user.accountId()),
            "traceRefs", List.of("trace-organization-admin-membership-" + stableSuffix(user.membershipId())),
            "redactionState", "visible"))
        .toList();
    var invitationRows = invitationView.list(actor, ScopeType.TENANT, organizationId, null).stream()
        .filter(invitation -> invitation.requestedRoles().contains(FoundationRole.TENANT_ADMIN))
        .map(invitation -> mapOf(
            "id", invitation.invitationId(),
            "rowId", invitation.invitationId(),
            "recordKind", "organization_admin_invitation",
            "invitationId", invitation.invitationId(),
            "tenantId", organizationId,
            "organizationId", organizationId,
            "organizationName", organization.organizationName(),
            "rowType", "organization-admin-invitation",
            "targetObjectType", "organization-admin-invitation",
            "targetSurfaceId", "surface-user-admin-organization-admin-detail",
            "targetSurfaceType", "show-inspection",
            "targetActionId", "action-open-organization-admin-invitation-detail",
            "openActionId", "action-open-organization-admin-invitation-detail",
            "displayName", invitation.targetEmail(),
            "email", invitation.targetEmail(),
            "roles", roleLabels(invitation.requestedRoles()),
            "role", roleLabels(invitation.requestedRoles()),
            "status", invitation.status().name().toLowerCase(Locale.ROOT),
            "invitationStatus", invitation.status().name().toLowerCase(Locale.ROOT),
            "deliveryStatus", invitation.deliveryStatus().name().toLowerCase(Locale.ROOT),
            "lastAdminRisk", false,
            "attentionBadges", invitation.canResend() ? List.of("resend-available") : List.of(),
            "actionAvailability", List.of("open-invitation-detail", "open-audit"),
            "safeActionContext", mapOf("organizationId", organizationId, "tenantId", organizationId, "invitationId", invitation.invitationId()),
            "traceRefs", List.of("trace-organization-admin-invitation-" + stableSuffix(invitation.invitationId())),
            "redactionState", "invitation-token-redacted"))
        .toList();
    var rows = new ArrayList<Map<String, Object>>();
    rows.addAll(adminRows);
    rows.addAll(invitationRows);
    var activeAdminCount = adminRows.stream().filter(row -> "active".equals(row.get("status"))).count();
    var suspendedOrDisabledAdminCount = adminRows.size() - activeAdminCount;
    var pendingInvitationCount = invitationRows.stream().filter(row -> "pending".equals(row.get("invitationStatus"))).count();
    var expiredInvitationCount = invitationRows.stream().filter(row -> "expired".equals(row.get("invitationStatus"))).count();
    var providerBlockedCount = invitationRows.stream().filter(row -> String.valueOf(row.get("deliveryStatus")).contains("failed") || String.valueOf(row.get("deliveryStatus")).contains("blocked")).count();
    var branchNavigation = organizationBranchNavigation(correlationId);
    var traceRefs = Stream.concat(detail.traceRefs().stream(), Stream.of("trace-organization-admins-" + stableSuffix(correlationId))).toList();
    return envelope("surface-user-admin-organization-admins", "list-search", "Organization Admins", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.organization_admins.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext, visible Organization read authority, and saas_owner.organization_admin.list before listing TENANT_ADMIN memberships/invitations; browser filters cannot grant authority.",
            "branchNavigation", branchNavigation,
            "branchRootSurfaceId", branchNavigation.get("branchRootSurfaceId"),
            "branchReturnActionId", branchNavigation.get("branchReturnActionId"),
            "branchReturnLabel", branchNavigation.get("branchReturnLabel"),
            "detailReturnActionId", "action-organization-read",
            "recordId", organizationId,
            "recordLabel", organization.organizationName(),
            "recordKind", "organization",
            "tenantId", organizationId,
            "organizationId", organizationId,
            "organizationName", organization.organizationName(),
            "organizationStatus", organization.status(),
            "targetScope", mapOf("scopeType", ScopeType.TENANT.name(), "tenantId", organizationId, "organizationId", organizationId, "organizationName", organization.organizationName(), "source", "backend-authored-organization-detail", "correlationId", correlationId, "traceRefs", detail.traceRefs()),
            "query", "",
            "rows", rows,
            "admins", adminRows,
            "invitations", invitationRows,
            "filters", mapOf("role", "TENANT_ADMIN", "query", "", "membershipStatus", List.of("active", "suspended", "removed"), "invitationStatus", List.of("pending", "expired", "accepted", "revoked"), "attentionState", List.of("first-admin-bootstrap", "last-admin-risk", "provider-blocked"), "backendAuthored", true),
            "sort", mapOf("default", "backend-policy"),
            "pageInfo", mapOf("visibleCount", rows.size(), "hasMore", false),
            "summary", "Organization Admin users and invitations visible for the selected Organization. Rows route through backend-authored actions and never expose tenant application data.",
            "adminSummary", mapOf("visibleAdminCount", adminRows.size(), "activeAdminCount", activeAdminCount, "suspendedOrDisabledAdminCount", suspendedOrDisabledAdminCount, "pendingInvitationCount", pendingInvitationCount, "expiredInvitationCount", expiredInvitationCount, "deliveryFailureCount", providerBlockedCount, "firstAdminBootstrapEligible", activeAdminCount == 0, "lastAdminRiskCount", activeAdminCount == 1 ? 1 : 0, "providerBlockedCount", providerBlockedCount, "outboxBlockedCount", providerBlockedCount, "traceRefs", traceRefs),
            "authorizedActions", List.of("action-user-admin-show-organization-admins", "action-open-organization-admin-invitation-create", "action-open-organization-admin-detail", "action-open-organization-admin-invitation-detail", "action-organization-read", "action-open-audit-trace"),
            "availableTaskActions", organizationAdminListActions().stream().map(this::surfaceActionSummary).toList(),
            "systemStates", List.of(rows.isEmpty() ? "empty" : "ready"),
            "lastResult", mapOf("status", rows.isEmpty() ? "empty" : "ready", "message", "Organization Admin list loaded through protected workstream runtime path with backend-owned Organization scope proof.", "correlationId", correlationId),
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", List.of("tenant-app-data-redacted", "customer-facts-redacted", "raw-workos-ids-redacted", "raw-jwt-redacted", "invitation-token-redacted", "provider-payload-redacted", "hidden-organization-admin-counts-redacted"),
            "emptyMessage", "No Organization Admin users or invitations are visible for this selected Organization.",
            "boundaryNotice", "Organization Admin management is SaaS Owner scoped to one selected Organization/Tenant and does not expose tenant application data, customers, provider secrets, billing, support internals, or hidden admin counts."),
        organizationAdminListActions());
  }

  private List<SurfaceAction> organizationAdminListActions() {
    return withOrganizationBranchReturn(List.of(organizationReadAction(), openOrganizationAdminDetailAction(), openOrganizationAdminInvitationDetailAction(), openOrganizationAdminInvitationCreateAction(), openAuditAction()));
  }

  private SurfaceEnvelope organizationAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    if (!isActionCapabilityVisible(actor, SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY)) throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    return roleScopedInvitationSurface(actor, correlationId, "surface-user-admin-organization-admin-invitation-create", "Invite Organization Admin", "user_admin.organization_admin_invitation_create.v1", organizationBranchNavigation(correlationId), "TENANT_ADMIN", "Bootstrap or invite a TENANT_ADMIN for the selected Organization after the Organization exists. Provider/outbox failures return system-message without fake success.", withOrganizationBranchReturn(List.of(organizationAdminInviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope organizationAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    if (!isActionCapabilityVisible(actor, SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY)) throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    var detail = readOrganizationDetail(actor, input, correlationId);
    var organization = detail.organization();
    var surface = roleScopedInvitationSurface(actor, correlationId, "surface-user-admin-organization-admin-invitation-create", "Invite Organization Admin", "user_admin.organization_admin_invitation_create.v1", organizationBranchNavigation(correlationId), "TENANT_ADMIN", "Bootstrap or invite a TENANT_ADMIN for the selected Organization after the Organization exists. Provider/outbox failures return system-message without fake success.", withOrganizationBranchReturn(List.of(organizationAdminInviteAction(), openAuditAction())));
    surface.data().put("recordId", organization.organizationId());
    surface.data().put("recordLabel", organization.organizationName());
    surface.data().put("recordKind", "organization");
    surface.data().put("tenantId", organization.organizationId());
    surface.data().put("organizationId", organization.organizationId());
    surface.data().put("organizationName", organization.organizationName());
    surface.data().put("targetScope", mapOf("scopeType", ScopeType.TENANT.name(), "tenantId", organization.organizationId(), "organizationId", organization.organizationId(), "organizationName", organization.organizationName(), "source", "backend-authored-organization-detail", "correlationId", correlationId, "traceRefs", detail.traceRefs()));
    surface.data().put("traceRefs", detail.traceRefs());
    return surface;
  }

  private SurfaceEnvelope organizationAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SAAS_OWNER_TENANT_READ_CAPABILITY);
    return envelope("surface-user-admin-organization-admin-detail", "show-inspection", "Organization Admin Detail", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.organization_admin_detail.v1",
            "scopeLabel", "SaaS Owner scope",
            "scopeType", "saas_owner",
            "authorityBasis", "Backend checks selected SaaS Owner AuthContext plus a visible Organization Admin membership or invitation target before returning target detail.",
            "branchNavigation", organizationBranchNavigation(correlationId),
            "branchRootSurfaceId", "surface-user-admin-organization-directory",
            "branchReturnActionId", "action-user-admin-show-organizations",
            "branchReturnLabel", "Back to organizations",
            "adminListReturnActionId", "action-user-admin-show-organization-admins",
            "organizationDetailReturnActionId", "action-organization-read",
            "recordId", "missing-target",
            "recordLabel", "Select an Organization Admin",
            "recordKind", "organization-admin-target-missing",
            "summary", "Select a visible Organization Admin membership or invitation from the Organization Admins list before inspecting detail. Consequential role, status, resend, revoke, and audit work opens dedicated backend-authorized task surfaces.",
            "systemStates", List.of("missing-target"),
            "lastResult", mapOf("status", "missing-target", "message", "Select an Organization Admin membership or invitation from a backend-authorized Organization Admins list before opening detail.", "correlationId", correlationId),
            "fields", List.of(mapOf("fieldId", "selectedContext", "label", "Selected context", "value", actor.selectedContext().membershipId(), "editable", false, "inputType", "text")),
            "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", false, "reason", "No target was selected; detail actions require a backend-visible Organization Admin target."),
            "traceRefs", List.of("trace-organization-admin-detail-missing-target-" + stableSuffix(correlationId)),
            "correlationId", correlationId,
            "redaction", List.of("hidden-organization-admin-counts-redacted", "tenant-app-data-redacted", "provider-payload-redacted", "raw-jwt-redacted", "invitation-token-redacted"),
            "boundaryNotice", "Organization Admin detail is SaaS Owner scoped and does not expose tenant application data, customers, provider secrets, billing, support internals, hidden roles, or hidden admin counts."),
        withOrganizationBranchReturn(List.of(openAuditAction())));
  }

  private SurfaceEnvelope organizationAdminDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    if (!isActionCapabilityVisible(actor, SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY)) throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    var detail = readOrganizationDetail(actor, input, correlationId);
    var organization = detail.organization();
    var membershipId = stringInput(input, "membershipId", "");
    var accountId = stringInput(input, "accountId", "");
    var invitationId = stringInput(input, "invitationId", "");
    if (!membershipId.isBlank() || !accountId.isBlank()) {
      var target = userDirectoryView.list(actor, ScopeType.TENANT, organization.organizationId(), null).stream()
          .filter(user -> user.roles().contains(FoundationRole.TENANT_ADMIN))
          .filter(user -> Objects.equals(user.membershipId(), membershipId) || Objects.equals(user.accountId(), accountId))
          .findFirst()
          .orElseThrow(() -> new AuthorizationException(404, "organization-admin-target-not-found-or-forbidden"));
      return organizationAdminMembershipDetailSurface(actor, detail, target, correlationId);
    }
    if (!invitationId.isBlank()) {
      var invite = invitationView.list(actor, ScopeType.TENANT, organization.organizationId(), null).stream()
          .filter(row -> row.requestedRoles().contains(FoundationRole.TENANT_ADMIN))
          .filter(row -> Objects.equals(row.invitationId(), invitationId))
          .findFirst()
          .orElseThrow(() -> new AuthorizationException(404, "organization-admin-target-not-found-or-forbidden"));
      return organizationAdminInvitationDetailSurface(actor, detail, invite, correlationId);
    }
    throw new AuthorizationException(404, "organization-admin-target-required");
  }

  private SurfaceEnvelope organizationAdminMembershipDetailSurface(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, UserDirectoryView.UserDirectoryRow target, String correlationId) {
    var organization = detail.organization();
    var activeAdminCount = userDirectoryView.list(actor, ScopeType.TENANT, organization.organizationId(), null).stream()
        .filter(user -> user.roles().contains(FoundationRole.TENANT_ADMIN))
        .filter(user -> user.membershipStatus() == MembershipStatus.ACTIVE)
        .count();
    var lastAdminRisk = target.membershipStatus() == MembershipStatus.ACTIVE && activeAdminCount <= 1;
    var traceRefs = Stream.concat(detail.traceRefs().stream(), Stream.of("trace-organization-admin-detail-membership-" + stableSuffix(target.membershipId()))).toList();
    var adminTarget = mapOf(
        "recordKind", "organization_admin_membership",
        "adminAccountId", target.accountId(),
        "membershipId", target.membershipId(),
        "displayName", target.displayName(),
        "email", target.accountId(),
        "organizationAdminStatus", target.membershipStatus().name().toLowerCase(Locale.ROOT),
        "roles", roleLabels(target.roles()),
        "roleLabel", roleLabels(target.roles()),
        "membershipStatus", target.membershipStatus().name().toLowerCase(Locale.ROOT),
        "accountStatus", "active",
        "lastActivitySummary", "Visible Organization Admin membership in selected Organization scope.",
        "traceRefs", traceRefs,
        "redactionState", "visible");
    return envelope("surface-user-admin-organization-admin-detail", "show-inspection", "Organization Admin Detail", actor, correlationId,
        organizationAdminDetailData(actor, detail, "organization_admin_membership", target.membershipId(), target.displayName(), adminTarget,
            List.of(
                mapOf("fieldId", "organization", "label", "Organization", "value", organization.organizationName(), "editable", false, "inputType", "text"),
                mapOf("fieldId", "displayName", "label", "Name", "value", target.displayName(), "editable", false, "inputType", "text"),
                mapOf("fieldId", "email", "label", "Email", "value", target.accountId(), "editable", false, "inputType", "email"),
                mapOf("fieldId", "role", "label", "Role", "value", roleLabels(target.roles()), "editable", false, "inputType", "text"),
                mapOf("fieldId", "membershipStatus", "label", "Membership status", "value", target.membershipStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text")),
            mapOf("lastAdminRisk", lastAdminRisk, "selfActionRisk", Objects.equals(actor.account().accountId(), target.accountId()), "roleReplacementEligible", true, "lifecycleChangeEligible", !lastAdminRisk, "invitationResendEligible", false, "invitationRevokeEligible", false, "providerBlockedCount", 0, "outboxBlockedCount", 0, "approvalRequired", lastAdminRisk, "disabledReason", lastAdminRisk ? "Last active Organization Admin changes require a dedicated protected policy path and cannot mutate inline." : null, "traceRefs", traceRefs),
            traceRefs,
            correlationId,
            mapOf("accountId", target.accountId(), "membershipId", target.membershipId(), "organizationId", organization.organizationId(), "tenantId", organization.organizationId()),
            List.of("action-open-user-admin-role-change-preview", "action-open-user-admin-membership-status-confirmation", "action-user-admin-show-organization-admins", "action-organization-read", "action-open-organization-admin-audit")),
        organizationAdminDetailActions());
  }

  private SurfaceEnvelope organizationAdminInvitationDetailSurface(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, InvitationView.InvitationRow invite, String correlationId) {
    var organization = detail.organization();
    var traceRefs = Stream.concat(detail.traceRefs().stream(), Stream.of("trace-organization-admin-detail-invitation-" + stableSuffix(invite.invitationId()))).toList();
    var deliveryBlocked = String.valueOf(invite.deliveryStatus()).toLowerCase(Locale.ROOT).contains("failed") || String.valueOf(invite.deliveryStatus()).toLowerCase(Locale.ROOT).contains("blocked");
    var adminTarget = mapOf(
        "recordKind", "organization_admin_invitation",
        "invitationId", invite.invitationId(),
        "displayName", invite.targetEmail(),
        "email", invite.targetEmail(),
        "organizationAdminStatus", invite.status().name().toLowerCase(Locale.ROOT),
        "roles", roleLabels(invite.requestedRoles()),
        "roleLabel", roleLabels(invite.requestedRoles()),
        "invitationStatus", invite.status().name().toLowerCase(Locale.ROOT),
        "deliveryStatus", invite.deliveryStatus().name().toLowerCase(Locale.ROOT),
        "acceptedAt", invite.acceptedAt() == null ? null : invite.acceptedAt().toString(),
        "expiresAt", invite.expiresAt() == null ? null : invite.expiresAt().toString(),
        "traceRefs", traceRefs,
        "redactionState", "invitation-token-redacted");
    return envelope("surface-user-admin-organization-admin-detail", "show-inspection", "Organization Admin Invitation Detail", actor, correlationId,
        organizationAdminDetailData(actor, detail, "organization_admin_invitation", invite.invitationId(), invite.targetEmail(), adminTarget,
            List.of(
                mapOf("fieldId", "organization", "label", "Organization", "value", organization.organizationName(), "editable", false, "inputType", "text"),
                mapOf("fieldId", "email", "label", "Email", "value", invite.targetEmail(), "editable", false, "inputType", "email"),
                mapOf("fieldId", "role", "label", "Requested role", "value", roleLabels(invite.requestedRoles()), "editable", false, "inputType", "text"),
                mapOf("fieldId", "invitationStatus", "label", "Invitation status", "value", invite.status().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"),
                mapOf("fieldId", "deliveryStatus", "label", "Delivery status", "value", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text")),
            mapOf("lastAdminRisk", false, "selfActionRisk", false, "roleReplacementEligible", false, "lifecycleChangeEligible", false, "invitationResendEligible", invite.canResend(), "invitationRevokeEligible", invite.canRevoke(), "duplicateOpenInviteSummary", "Visible Organization Admin invitation in selected Organization scope.", "providerBlockedCount", deliveryBlocked ? 1 : 0, "outboxBlockedCount", deliveryBlocked ? 1 : 0, "approvalRequired", false, "disabledReason", deliveryBlocked ? "Provider/outbox readiness is fail-closed; no fake delivery success is shown." : null, "traceRefs", traceRefs),
            traceRefs,
            correlationId,
            mapOf("invitationId", invite.invitationId(), "organizationId", organization.organizationId(), "tenantId", organization.organizationId()),
            List.of("action-open-user-admin-invitation-resend-confirmation", "action-open-user-admin-invitation-revoke-confirmation", "action-user-admin-show-organization-admins", "action-organization-read", "action-open-organization-admin-audit")),
        organizationAdminDetailActions());
  }

  private Map<String, Object> organizationAdminDetailData(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, String recordKind, String recordId, String recordLabel, Map<String, Object> adminTarget, List<Map<String, Object>> fields, Map<String, Object> policySummary, List<String> traceRefs, String correlationId, Map<String, Object> actionContext, List<String> authorizedActions) {
    var organization = detail.organization();
    return mapOf(
        "surfaceContract", "user_admin.organization_admin_detail.v1",
        "scopeLabel", "SaaS Owner scope",
        "scopeType", "saas_owner",
        "authorityBasis", "Backend checked selected SaaS Owner AuthContext, visible Organization/Tenant boundary, and saas_owner.organization_admin.list before returning this Organization Admin target.",
        "branchNavigation", organizationBranchNavigation(correlationId),
        "branchRootSurfaceId", "surface-user-admin-organization-directory",
        "branchReturnActionId", "action-user-admin-show-organizations",
        "branchReturnLabel", "Back to organizations",
        "adminListReturnActionId", "action-user-admin-show-organization-admins",
        "organizationDetailReturnActionId", "action-organization-read",
        "recordId", recordId,
        "recordLabel", recordLabel,
        "recordKind", recordKind,
        "tenantId", organization.organizationId(),
        "organizationId", organization.organizationId(),
        "organizationName", organization.organizationName(),
        "organizationStatus", organization.status(),
        "targetScope", mapOf("scopeType", ScopeType.TENANT.name(), "tenantId", organization.organizationId(), "organizationId", organization.organizationId(), "organizationName", organization.organizationName(), "source", "backend-authored-organization-admin-detail", "traceRefs", detail.traceRefs()),
        "adminTarget", adminTarget,
        "policySummary", policySummary,
        "taskEntryPoints", organizationAdminDetailTaskEntryPoints(recordKind, actionContext),
        "actionContext", actionContext,
        "fields", fields,
        "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Organization Admin detail is inspection-only; consequential work routes to dedicated backend-authorized task surfaces."),
        "auditSummary", mapOf("recentEvents", List.of("OrganizationAdminDetailDisplayed"), "traceRefs", traceRefs, "redaction", "browser-safe audit excerpts only"),
        "authorizedActions", authorizedActions,
        "systemStates", List.of("ready"),
        "lastResult", mapOf("status", "ready", "message", "Organization Admin detail loaded through protected workstream runtime path with backend-owned Organization scope proof.", "correlationId", correlationId),
        "traceRefs", traceRefs,
        "correlationId", correlationId,
        "redaction", List.of("tenant-app-data-redacted", "customer-facts-redacted", "raw-workos-ids-redacted", "raw-jwt-redacted", "invitation-token-redacted", "provider-payload-redacted", "hidden-organization-admin-counts-redacted"),
        "boundaryNotice", "Organization Admin detail is SaaS Owner scoped to one selected Organization/Tenant and does not expose tenant application data, customers, provider secrets, billing, support internals, hidden roles, or hidden admin counts.");
  }

  private List<Map<String, Object>> organizationAdminDetailTaskEntryPoints(String recordKind, Map<String, Object> actionContext) {
    var common = new ArrayList<Map<String, Object>>();
    common.add(mapOf("actionId", "action-user-admin-show-organization-admins", "label", "Back to Organization Admins", "purpose", "Return to the backend-authorized Organization Admin list.", "targetSurfaceId", "surface-user-admin-organization-admins", "enabled", true, "safeActionContext", actionContext));
    common.add(mapOf("actionId", "action-organization-read", "label", "Back to Organization detail", "purpose", "Return to the selected Organization detail.", "targetSurfaceId", "surface-user-admin-organization-detail", "enabled", true, "safeActionContext", actionContext));
    if ("organization_admin_membership".equals(recordKind)) {
      common.add(mapOf("actionId", "action-open-user-admin-role-change-preview", "label", "Open role-change preview", "purpose", "Route role replacement to the dedicated role preview surface; no inline mutation occurs here.", "targetSurfaceId", "surface-user-admin-role-change-preview", "enabled", true, "safeActionContext", actionContext));
      common.add(mapOf("actionId", "action-open-user-admin-membership-status-confirmation", "label", "Open membership lifecycle confirmation", "purpose", "Route suspend/reactivate/remove work to the dedicated lifecycle confirmation surface with last-admin protections.", "targetSurfaceId", "surface-user-admin-membership-status-confirmation", "enabled", true, "safeActionContext", actionContext));
    } else {
      common.add(mapOf("actionId", "action-open-user-admin-invitation-resend-confirmation", "label", "Open resend confirmation", "purpose", "Route resend work to the dedicated invitation lifecycle surface with provider/outbox fail-closed handling.", "targetSurfaceId", "surface-user-admin-invitation-resend-confirmation", "enabled", true, "safeActionContext", actionContext));
      common.add(mapOf("actionId", "action-open-user-admin-invitation-revoke-confirmation", "label", "Open revoke confirmation", "purpose", "Route revoke work to the dedicated destructive lifecycle surface with idempotency and audit.", "targetSurfaceId", "surface-user-admin-invitation-revoke-confirmation", "enabled", true, "safeActionContext", actionContext));
    }
    common.add(mapOf("actionId", "action-open-organization-admin-audit", "label", "Open audit evidence", "purpose", "Open authorized Audit/Trace evidence or safe redacted recovery.", "targetSurfaceId", "surface-audit-trace-dashboard", "enabled", true, "safeActionContext", actionContext));
    return common;
  }

  private List<SurfaceAction> organizationAdminDetailActions() {
    return withOrganizationBranchReturn(List.of(showOrganizationAdminsAction(), organizationReadAction(), openAuditAction()));
  }

  private SurfaceEnvelope customerDirectorySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return customerDirectorySurface(actor, null, correlationId);
  }

  private SurfaceEnvelope customerDirectorySurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var query = stringInput(input, "query", "");
    var status = stringInput(input, "status", "");
    var result = StarterSecurityComponents.tenantCustomerAdminService().listCustomers(actor, query, status, correlationId);
    var rows = result.customers().stream().map(this::customerRowMap).toList();
    var normalizedStatus = status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    return envelope("surface-user-admin-customer-directory", "list-search", "Customer Directory", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_directory.v1", "branchNavigation", customerBranchNavigation(correlationId), "query", query == null ? "" : query.trim(), "status", normalizedStatus, "rows", rows, "customers", rows, "filters", mapOf("tenantId", actor.selectedContext().tenantId(), "query", query == null ? "" : query.trim(), "status", normalizedStatus, "backendAuthored", true), "pageInfo", mapOf("visibleCount", rows.size()), "boundaryNotice", result.safeBoundaryNotice(), "safeBoundaryNotice", result.safeBoundaryNotice(), "traceRefs", result.traceRefs(), "correlationId", result.correlationId(), "redaction", List.of("sibling-customers-redacted", "tenant-app-data-redacted", "provider-secrets-redacted"), "emptyMessage", rows.isEmpty() ? "No visible Customers match the current backend-authorized filters in this selected Organization/Tenant scope." : "No Customers are visible in this selected Organization/Tenant scope."),
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
    authContextResolver.requireCapability(actor.selectedContext(), TENANT_CUSTOMER_CREATE_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-create", "Create Customer", "user_admin.customer_create.v1", "Create a Customer inside the selected Organization/Tenant with idempotency, audit, and sibling-customer redaction.", withCustomerBranchReturn(List.of(submitCustomerCreateAction(), customerCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerRenameSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), TENANT_CUSTOMER_RENAME_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-rename", "Rename Customer", "user_admin.customer_rename.v1", "Update Customer display profile only. Backend handles stale, conflict, no-op, forbidden, and audit results.", withCustomerBranchReturn(List.of(submitCustomerRenameAction(), customerRenameAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerRenameSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var detail = StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), correlationId);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-rename", "Rename Customer", "user_admin.customer_rename.v1", "Update Customer display profile only. Backend handles stale, conflict, no-op, forbidden, and audit results.", withCustomerBranchReturn(List.of(submitCustomerRenameAction(), customerRenameAction(), openAuditAction())), detail);
  }

  private SurfaceEnvelope customerSuspendSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), TENANT_CUSTOMER_SUSPEND_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-suspend-confirmation", "Suspend Customer", "user_admin.customer_suspend_confirmation.v1", "Suspend/archive a Customer boundary after reason and confirmation. Tenant application data and sibling-customer facts remain hidden.", withCustomerBranchReturn(List.of(customerSuspendAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerSuspendSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), TENANT_CUSTOMER_SUSPEND_CAPABILITY);
    var detail = StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), correlationId);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-suspend-confirmation", "Suspend Customer", "user_admin.customer_suspend_confirmation.v1", "Suspend/archive a Customer boundary after reason and confirmation. Tenant application data and sibling-customer facts remain hidden.", withCustomerBranchReturn(List.of(customerSuspendAction(), openAuditAction())), detail);
  }

  private SurfaceEnvelope customerReactivateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), TENANT_CUSTOMER_REACTIVATE_CAPABILITY);
    return customerTaskSurface(actor, correlationId, "surface-user-admin-customer-reactivate-confirmation", "Reactivate Customer", "user_admin.customer_reactivate_confirmation.v1", "Reactivate a Customer boundary with idempotency, audit, and safe no-op handling.", withCustomerBranchReturn(List.of(customerReactivateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerReactivateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), TENANT_CUSTOMER_REACTIVATE_CAPABILITY);
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
    var admins = userAdminService.listUsers(actor, ScopeType.CUSTOMER, actor.selectedContext().tenantId(), detail.customer().customerId()).stream()
        .filter(row -> row.roles().contains(FoundationRole.CUSTOMER_ADMIN))
        .map(this::customerAdminRowMap)
        .toList();
    var invitations = invitationView.list(actor, ScopeType.CUSTOMER, actor.selectedContext().tenantId(), detail.customer().customerId()).stream()
        .filter(row -> row.requestedRoles().contains(FoundationRole.CUSTOMER_ADMIN))
        .map(this::customerAdminInvitationRowMap)
        .toList();
    var rows = Stream.concat(admins.stream(), invitations.stream()).toList();
    return envelope("surface-user-admin-customer-admins", "list-search", "Customer Admins", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_admins.v1", "branchNavigation", customerBranchNavigation(correlationId), "customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "targetScopeProof", target, "query", "", "rows", rows, "admins", admins, "invitations", invitations, "filters", mapOf("role", "CUSTOMER_ADMIN", "customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "backendAuthored", true), "pageInfo", mapOf("visibleCount", rows.size()), "summary", "Customer Admin users and invitations for " + detail.customer().customerName() + ". Rows route through backend-authored detail/task surfaces and never expose sibling-customer or tenant-wide authority.", "safeBoundaryNotice", detail.safeBoundaryNotice(), "branchRootSurfaceId", "surface-user-admin-customer-directory", "branchReturnActionId", "action-user-admin-show-customers", "adminListReturnActionId", "action-user-admin-show-customer-admins", "detailReturnActionId", "action-customer-read", "traceRefs", detail.traceRefs(), "correlationId", correlationId, "redaction", List.of("hidden-users-redacted", "sibling-customers-redacted", "provider-payload-redacted", "raw-invitation-token-redacted"), "emptyMessage", "No Customer Admins are visible for this selected Customer yet."),
        withCustomerBranchReturn(List.of(openCustomerAdminDetailAction(), openCustomerAdminInvitationDetailAction(), openCustomerAdminInvitationCreateAction(), customerReadAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return roleScopedInvitationSurface(actor, correlationId, "surface-user-admin-customer-admin-invitation-create", "Invite Customer Admin", "user_admin.customer_admin_invitation_create.v1", customerBranchNavigation(correlationId), "CUSTOMER_ADMIN", "Bootstrap or invite a CUSTOMER_ADMIN for a selected Customer after the Customer exists. Open this form from Customer detail so backend-authored target proof is carried forward. Provider/outbox failures return system-message without fake success.", withCustomerBranchReturn(List.of(customerAdminInviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminInvitationCreateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    var detail = activeCustomerInviteTargetDetail(actor, input, correlationId);
    var target = customerTargetMap(actor, detail, correlationId);
    var traceRefs = detail.traceRefs();
    var roleOptions = List.of(mapOf("roleId", "CUSTOMER_ADMIN", "label", "CUSTOMER ADMIN"));
    var form = mapOf("emailDraft", "", "displayNameDraft", "", "reasonDraft", "", "targetRoleOptions", List.of("CUSTOMER_ADMIN"), "selectedTargetRole", "CUSTOMER_ADMIN", "idempotencyKeyHint", "client-generated", "submitLabel", "Create Customer Admin invitation", "cancelActionId", "action-user-admin-show-customer-admins", "submitActionId", "action-customer-admin-invite");
    var policyContext = mapOf("firstAdminBootstrapEligible", true, "duplicateOpenInvitePolicy", "reuse-visible-open-invite-else-no-enumeration", "approvalRequired", false, "customerLifecycleAllowsInvite", "active".equals(detail.customer().status()), "traceRefs", traceRefs);
    var deliveryReadiness = mapOf("outboxStatus", "queued-on-submit", "providerStatus", "configured-or-outbox-queued", "retryEligible", true, "failClosedMessage", "Provider/outbox failures return system-message without fake success.", "traceRefs", traceRefs);
    return envelope("surface-user-admin-customer-admin-invitation-create", "create-form", "Invite Customer Admin", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_admin_invitation_create.v1", "scopeLabel", detail.customer().customerName(), "scopeType", "tenant", "tenantId", actor.selectedContext().tenantId(), "customerId", detail.customer().customerId(), "customerName", detail.customer().customerName(), "customerStatus", detail.customer().status(), "authorityBasis", "selected Organization/Tenant Admin AuthContext with tenant.customer_admin.invite", "branchNavigation", customerBranchNavigation(correlationId), "branchRootSurfaceId", "surface-user-admin-customer-directory", "branchReturnActionId", "action-user-admin-show-customers", "branchReturnLabel", "Back to customers", "adminListReturnActionId", "action-user-admin-show-customer-admins", "detailReturnActionId", "action-customer-read", "targetScopeProof", target, "targetScope", mapOf("scopeType", ScopeType.CUSTOMER.name(), "tenantId", actor.selectedContext().tenantId(), "customerId", detail.customer().customerId()), "summary", "Bootstrap or invite a CUSTOMER_ADMIN for " + detail.customer().customerName() + ". The workstream submit path always targets ScopeType.CUSTOMER for this Customer.", "recordKind", "customer-admin-invitation", "recordId", detail.customer().customerId(), "recordLabel", detail.customer().customerName(), "formState", "ready", "validationMessages", List.of(), "systemStates", List.of(), "lastResult", mapOf("status", "ready"), "form", form, "draft", mapOf("email", "", "displayName", "", "roles", List.of("CUSTOMER_ADMIN"), "customerId", detail.customer().customerId()), "roleOptions", roleOptions, "policyContext", policyContext, "policyOptions", mapOf("roles", roleOptions, "idempotency", "client-generated", "outboxReadiness", "backend-derived"), "deliveryReadiness", deliveryReadiness, "authorizedActions", List.of("action-open-customer-admin-invitation-create", "action-customer-admin-invite", "action-user-admin-show-customer-admins", "action-customer-read", "action-user-admin-show-customers", "action-open-audit-trace"), "idempotencyKeyHint", "client-generated", "outboxReadiness", "backend-derived", "boundaryNotice", detail.safeBoundaryNotice(), "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("raw-token-redacted", "provider-payload-redacted", "sibling-customers-redacted")),
        withCustomerBranchReturn(List.of(customerAdminInviteAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminInvitationDetailSurface(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail customer, Invitation invite, String correlationId) {
    return envelope("surface-user-admin-invitation-detail", "show-inspection", "Customer Admin invitation detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_detail.v1", "branchNavigation", customerBranchNavigation(correlationId), "recordId", invite.invitationId(), "recordLabel", invite.normalizedEmail(), "recordKind", "customer-admin-invitation", "customerId", customer.customer().customerId(), "customerName", customer.customer().customerName(), "targetScope", mapOf("scopeType", invite.scopeType().name(), "tenantId", invite.tenantId(), "customerId", invite.customerId()), "status", invite.status().name().toLowerCase(Locale.ROOT), "summary", "Customer Admin invitation was created in the selected Customer scope. Raw invitation tokens and provider payloads are redacted.", "fields", List.of(mapOf("fieldId", "email", "label", "Email", "value", invite.normalizedEmail(), "editable", false, "inputType", "email"), mapOf("fieldId", "scope", "label", "Scope", "value", invite.scopeType().name(), "editable", false, "inputType", "text"), mapOf("fieldId", "customerId", "label", "Customer", "value", invite.customerId(), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleLabels(invite.requestedRoles()), "editable", false, "inputType", "text")), "traceRefs", List.of("trace-customer-admin-invitation-" + stableSuffix(invite.invitationId())), "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "sibling-customers-redacted")),
        withCustomerBranchReturn(List.of(openCustomerAdminInvitationCreateAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), USER_ADMIN_CAPABILITY);
    return scopedAdminDetailSurface(actor, correlationId, "surface-user-admin-customer-admin-detail", "Customer Admin Detail", "user_admin.customer_admin_detail.v1", customerBranchNavigation(correlationId), "Customer Admin membership/invitation inspection. Role, status, resend, revoke, and audit changes route to dedicated User Admin task surfaces.", withCustomerBranchReturn(List.of(showCustomerAdminsAction(), customerReadAction(), openMembershipStatusConfirmationAction(), previewRoleChangeAction(), openInvitationResendConfirmationAction(), openInvitationRevokeConfirmationAction(), openAuditAction())));
  }

  private SurfaceEnvelope customerAdminDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var detail = activeCustomerListTargetDetail(actor, input, correlationId);
    var target = customerTargetMap(actor, detail, correlationId);
    var invitationId = stringInput(input, "invitationId", "");
    if (!invitationId.isBlank() || "customer-admin-invitation".equals(stringInput(input, "targetObjectType", ""))) {
      var invite = invitationView.list(actor, ScopeType.CUSTOMER, actor.selectedContext().tenantId(), detail.customer().customerId()).stream()
          .filter(row -> row.requestedRoles().contains(FoundationRole.CUSTOMER_ADMIN))
          .filter(row -> invitationId.isBlank() || invitationId.equals(row.invitationId()))
          .findFirst()
          .orElseThrow(() -> new AuthorizationException(404, "target-not-found-or-forbidden"));
      return customerAdminDetailSurface(actor, detail, target, invite, correlationId);
    }
    var accountId = stringInput(input, "accountId", "");
    var membershipId = stringInput(input, "membershipId", stringInput(input, "recordId", ""));
    var member = userAdminService.listUsers(actor, ScopeType.CUSTOMER, actor.selectedContext().tenantId(), detail.customer().customerId()).stream()
        .filter(row -> row.roles().contains(FoundationRole.CUSTOMER_ADMIN))
        .filter(row -> (!membershipId.isBlank() && membershipId.equals(row.membershipId())) || (!accountId.isBlank() && accountId.equals(row.accountId())))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "target-not-found-or-forbidden"));
    return customerAdminDetailSurface(actor, detail, target, member, correlationId);
  }

  private SurfaceEnvelope customerAdminDetailSurface(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail customer, Map<String, Object> targetScopeProof, UserAdminService.UserDirectoryRow member, String correlationId) {
    var traceRefs = List.of("trace-customer-admin-detail-" + stableSuffix(member.membershipId() + correlationId));
    var actions = withCustomerBranchReturn(List.of(showCustomerAdminsAction(), customerReadAction(), openUserAdminMembershipStatusConfirmationAction(), openUserAdminRoleChangePreviewAction(), openAuditAction()));
    return envelope("surface-user-admin-customer-admin-detail", "show-inspection", "Customer Admin Detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_admin_detail.v1", "branchNavigation", customerBranchNavigation(correlationId), "scopeLabel", customer.customer().customerName(), "scopeType", "tenant", "tenantId", actor.selectedContext().tenantId(), "customerId", customer.customer().customerId(), "customerName", customer.customer().customerName(), "customerStatus", customer.customer().status(), "authorityBasis", "selected Organization/Tenant Admin AuthContext with tenant.customer_admin.list", "branchRootSurfaceId", "surface-user-admin-customer-directory", "branchReturnActionId", "action-user-admin-show-customers", "branchReturnLabel", "Back to customers", "adminListReturnActionId", "action-user-admin-show-customer-admins", "detailReturnActionId", "action-customer-read", "targetScopeProof", targetScopeProof, "targetScope", mapOf("scopeType", ScopeType.CUSTOMER.name(), "tenantId", actor.selectedContext().tenantId(), "customerId", customer.customer().customerId()), "recordId", member.membershipId(), "recordLabel", member.displayName(), "recordKind", "customer-admin-membership", "status", member.status().name().toLowerCase(Locale.ROOT), "summary", "Read-only Customer Admin membership inspection for " + customer.customer().customerName() + ". Role and lifecycle work opens dedicated backend-authorized task surfaces; no inline mutation occurs.", "adminTarget", mapOf("recordKind", "customer_admin_membership", "accountId", member.accountId(), "membershipId", member.membershipId(), "displayName", member.displayName(), "roles", member.roles().stream().map(Enum::name).toList(), "status", member.status().name().toLowerCase(Locale.ROOT), "customerId", customer.customer().customerId(), "tenantId", actor.selectedContext().tenantId(), "redactionState", "visible"), "policyContext", mapOf("lastCustomerAdminRiskSummary", "backend task surfaces re-evaluate last-admin policy before mutation", "approvalRequired", true, "inlineMutationAllowed", false, "traceRefs", traceRefs), "taskEntryPoints", List.of(mapOf("label", "Preview role replacement", "actionId", "action-open-user-admin-role-change-preview", "targetSurfaceId", "surface-user-admin-role-change-preview"), mapOf("label", "Open membership lifecycle confirmation", "actionId", "action-open-user-admin-membership-status-confirmation", "targetSurfaceId", "surface-user-admin-membership-status-confirmation")), "availableTaskActions", actions.stream().map(this::authorizedActionMap).toList(), "fields", List.of(mapOf("fieldId", "displayName", "label", "Name", "value", member.displayName(), "editable", false, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", member.accountId(), "editable", false, "inputType", "email"), mapOf("fieldId", "membershipStatus", "label", "Status", "value", member.status().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleLabels(member.roles()), "editable", false, "inputType", "text"), mapOf("fieldId", "customer", "label", "Customer", "value", customer.customer().customerName(), "editable", false, "inputType", "text")), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Inspection only; Customer Admin membership changes route through dedicated task surfaces with backend authorization."), "auditSummary", mapOf("lastEventType", "CustomerAdminDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", traceRefs, "redactionSummary", "raw JWTs, provider ids, sibling-customer facts, tenant app data, hidden roles, and policy internals are omitted"), "systemStates", List.of("ready", "forbidden-hidden-not-found", "stale-conflict", "provider-outbox-blocked", "no-op"), "boundaryNotice", customer.safeBoundaryNotice(), "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("sibling-customers-redacted", "tenant-app-data-redacted", "hidden-admins-redacted", "provider-payload-redacted", "raw-jwt-redacted")),
        actions);
  }

  private SurfaceEnvelope customerAdminDetailSurface(AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail customer, Map<String, Object> targetScopeProof, InvitationView.InvitationRow invite, String correlationId) {
    var traceRefs = List.of("trace-customer-admin-invitation-detail-" + stableSuffix(invite.invitationId() + correlationId));
    var actions = withCustomerBranchReturn(List.of(showCustomerAdminsAction(), customerReadAction(), openInvitationResendConfirmationAction(), openInvitationRevokeConfirmationAction(), openAuditAction()));
    return envelope("surface-user-admin-customer-admin-detail", "show-inspection", "Customer Admin Detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.customer_admin_detail.v1", "branchNavigation", customerBranchNavigation(correlationId), "scopeLabel", customer.customer().customerName(), "scopeType", "tenant", "tenantId", actor.selectedContext().tenantId(), "customerId", customer.customer().customerId(), "customerName", customer.customer().customerName(), "customerStatus", customer.customer().status(), "authorityBasis", "selected Organization/Tenant Admin AuthContext with tenant.customer_admin.list", "branchRootSurfaceId", "surface-user-admin-customer-directory", "branchReturnActionId", "action-user-admin-show-customers", "branchReturnLabel", "Back to customers", "adminListReturnActionId", "action-user-admin-show-customer-admins", "detailReturnActionId", "action-customer-read", "targetScopeProof", targetScopeProof, "targetScope", mapOf("scopeType", ScopeType.CUSTOMER.name(), "tenantId", actor.selectedContext().tenantId(), "customerId", customer.customer().customerId()), "recordId", invite.invitationId(), "recordLabel", invite.targetEmail(), "recordKind", "customer-admin-invitation", "status", invite.status().name().toLowerCase(Locale.ROOT), "summary", "Read-only Customer Admin invitation inspection for " + customer.customer().customerName() + ". Resend and revoke work opens dedicated backend-authorized confirmation surfaces; no raw token or provider payload is exposed.", "adminTarget", mapOf("recordKind", "customer_admin_invitation", "invitationId", invite.invitationId(), "targetEmail", invite.targetEmail(), "roles", invite.requestedRoles().stream().map(Enum::name).toList(), "invitationStatus", invite.status().name().toLowerCase(Locale.ROOT), "deliveryStatus", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "customerId", customer.customer().customerId(), "tenantId", actor.selectedContext().tenantId(), "redactionState", "raw-token-redacted"), "deliveryReadiness", invitationDeliveryState(invite, correlationId), "policyContext", mapOf("duplicateOpenInvitePolicy", "reuse-visible-open-invitation-or-safe-no-enumeration", "approvalRequired", false, "inlineMutationAllowed", false, "traceRefs", traceRefs), "taskEntryPoints", List.of(mapOf("label", "Open resend confirmation", "actionId", "action-open-useradmin-invitation-resend-confirmation", "targetSurfaceId", "surface-user-admin-invitation-resend-confirmation"), mapOf("label", "Open revoke confirmation", "actionId", "action-open-useradmin-invitation-revoke-confirmation", "targetSurfaceId", "surface-user-admin-invitation-revoke-confirmation")), "availableTaskActions", actions.stream().map(this::authorizedActionMap).toList(), "fields", List.of(mapOf("fieldId", "email", "label", "Email", "value", invite.targetEmail(), "editable", false, "inputType", "email"), mapOf("fieldId", "invitationStatus", "label", "Invitation status", "value", invite.status().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "deliveryStatus", "label", "Delivery", "value", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleLabels(invite.requestedRoles()), "editable", false, "inputType", "text"), mapOf("fieldId", "customer", "label", "Customer", "value", customer.customer().customerName(), "editable", false, "inputType", "text")), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Inspection only; invitation resend/revoke route through dedicated confirmation surfaces."), "auditSummary", mapOf("lastEventType", "CustomerAdminInvitationDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", traceRefs, "redactionSummary", "invitation tokens, provider payloads, raw JWTs, sibling-customer facts, and hidden targets are omitted"), "systemStates", List.of("ready", "forbidden-hidden-not-found", "stale-conflict", "provider-outbox-blocked", "no-op"), "boundaryNotice", customer.safeBoundaryNotice(), "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "sibling-customers-redacted", "tenant-app-data-redacted", "raw-jwt-redacted"), "noFakeSuccess", invite.deliveryStatus() == EmailDeliveryStatus.FAILED),
        actions);
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
    var traceRefs = detail == null ? List.of("trace-" + surfaceId + "-" + stableSuffix(correlationId)) : detail.traceRefs();
    var data = mapOf("surfaceContract", contract, "branchNavigation", customerBranchNavigation(correlationId), "summary", summary, "recordKind", "customer", "recordId", detail == null ? "" : detail.customer().customerId(), "recordLabel", detail == null ? title : detail.customer().customerName(), "draft", mapOf("customerName", draftName, "reason", ""), "reasonRequired", surfaceId.contains("suspend"), "confirmationRequired", surfaceId.contains("suspend") || surfaceId.contains("reactivate"), "idempotencyKeyHint", "client-generated", "traceRefs", traceRefs, "redaction", List.of("sibling-customers-redacted", "tenant-app-data-redacted", "provider-secrets-redacted"));
    if ("surface-user-admin-customer-create".equals(surfaceId)) {
      data.put("scopeLabel", "Selected Organization/Tenant");
      data.put("scopeType", "tenant");
      data.put("tenantId", actor.selectedContext().tenantId());
      data.put("authorityBasis", "selected Organization/Tenant Admin AuthContext with tenant.customer.create");
      data.put("branchRootSurfaceId", "surface-user-admin-customer-directory");
      data.put("branchReturnActionId", "action-user-admin-show-customers");
      data.put("branchReturnLabel", "Back to customers");
      data.put("formState", "ready");
      data.put("validationMessages", List.of());
      data.put("boundaryNotice", "Create a Customer boundary inside the selected Organization/Tenant. Customer Admin bootstrap, billing, provider settings, sibling Customers, and tenant application data remain separate and redacted.");
      data.put("form", mapOf("customerNameDraft", "", "reasonDraft", "", "submitLabel", "Create Customer", "submitActionId", "action-submit-customer-create", "cancelActionId", "action-user-admin-show-customers", "idempotencyKeyHint", "client-generated"));
      data.put("validationPolicy", mapOf("customerNameRules", "2-120 characters after trimming", "reasonRequired", false, "duplicateNamePolicy", "idempotent replay returns the visible created Customer when authorized; hidden duplicates use no-enumeration recovery", "maxLength", 120, "allowedCharacterSummary", "browser-safe display name characters only", "idempotentReplayPolicy", "client-generated key scoped to selected Organization/Tenant", "traceRefs", traceRefs));
      data.put("creationBoundary", mapOf("selectedTenantBoundaryLabel", "Selected Organization/Tenant", "createsCustomerBoundary", true, "initialLifecycleStatus", "active", "tenantApplicationDataExcluded", true, "customerAdminBootstrapSeparateSurfaceId", "surface-user-admin-customer-admin-invitation-create", "traceRefs", traceRefs));
      data.put("systemStates", List.of("ready", "validation-error", "duplicate-no-op", "idempotent-replay", "forbidden-hidden-not-found", "stale-conflict", "provider-runtime-blocked"));
      data.put("lastResult", null);
    }
    if ("surface-user-admin-customer-rename".equals(surfaceId)) {
      data.put("scopeLabel", "Selected Organization/Tenant");
      data.put("scopeType", "tenant");
      data.put("tenantId", actor.selectedContext().tenantId());
      data.put("customerId", detail == null ? "" : detail.customer().customerId());
      data.put("customerName", draftName);
      data.put("customerStatus", detail == null ? "" : detail.customer().status());
      data.put("authorityBasis", "selected Organization/Tenant Admin AuthContext with tenant.customer.rename");
      data.put("branchRootSurfaceId", "surface-user-admin-customer-directory");
      data.put("branchReturnActionId", "action-user-admin-show-customers");
      data.put("branchReturnLabel", "Back to customers");
      data.put("detailReturnActionId", "action-customer-read");
      data.put("formState", detail == null ? "missing-target" : "ready");
      data.put("validationMessages", detail == null ? List.of("Choose a visible Customer detail before renaming.") : List.of());
      data.put("boundaryNotice", "Rename updates only the Customer administration display label. Customer Admin roles, lifecycle, billing, provider settings, sibling Customers, downstream business profiles, and tenant application data remain separate and redacted.");
      data.put("form", mapOf("currentCustomerName", draftName, "proposedCustomerNameDraft", draftName, "reasonDraft", "", "submitLabel", "Rename Customer", "submitActionId", "action-submit-customer-rename", "cancelActionId", "action-customer-read", "idempotencyKeyHint", "client-generated", "freshnessVersionHint", "detail-trace-boundary", "disabledReason", detail == null ? "missing-visible-customer" : ""));
      data.put("validationPolicy", mapOf("customerNameRules", "2-120 characters after trimming", "reasonRequired", false, "noOpPolicy", "unchanged names return a traceable no-op detail refresh", "duplicateNamePolicy", "visible duplicates return validation/conflict recovery; hidden duplicates use no-enumeration recovery", "maxLength", 120, "allowedCharacterSummary", "browser-safe display name characters only", "freshnessRequired", true, "idempotentReplayPolicy", "client-generated key scoped to selected Organization/Tenant and Customer", "traceRefs", traceRefs));
      data.put("changePreview", mapOf("currentDisplayName", draftName, "proposedDisplayName", draftName, "visibleImpactSummary", detail == null ? "No Customer is selected; no rename can run from this direct form load." : "Updates the Customer administration label only; Customer Admin memberships, lifecycle, downstream business profiles, sibling Customers, and tenant application data are unaffected.", "tenantApplicationDataExcluded", true, "customerAdminMembershipsUnaffectedByRename", true, "downstreamBusinessProfileExcluded", true, "traceRefs", traceRefs));
      data.put("authorizedActions", actions.stream().map(SurfaceAction::actionId).toList());
      data.put("availableTaskActions", actions.stream().map(this::surfaceActionSummary).toList());
      data.put("systemStates", List.of("ready", "missing-target", "validation-error", "no-op", "idempotent-replay", "duplicate-visible", "hidden-duplicate", "forbidden-hidden-not-found", "stale-conflict", "runtime-blocked"));
      data.put("lastResult", detail == null ? mapOf("status", "missing-target", "message", "Customer rename requires a visible Customer target loaded through the protected detail-to-rename action path.", "correlationId", correlationId, "traceRefs", traceRefs, "noFakeSuccess", true) : null);
    }
    if ("surface-user-admin-customer-suspend-confirmation".equals(surfaceId)) {
      var currentStatus = detail == null ? "" : detail.customer().status();
      data.put("scopeLabel", detail == null ? "Selected Organization/Tenant" : detail.customer().customerName());
      data.put("scopeType", "tenant");
      data.put("tenantId", actor.selectedContext().tenantId());
      data.put("customerId", detail == null ? "" : detail.customer().customerId());
      data.put("customerName", detail == null ? "" : detail.customer().customerName());
      data.put("currentStatus", currentStatus);
      data.put("customerStatus", currentStatus);
      data.put("authorityBasis", "selected Organization/Tenant Admin AuthContext with tenant.customer.suspend");
      data.put("branchRootSurfaceId", "surface-user-admin-customer-directory");
      data.put("branchReturnActionId", "action-user-admin-show-customers");
      data.put("branchReturnLabel", "Back to customers");
      data.put("detailReturnActionId", "action-customer-read");
      data.put("formState", detail == null ? "missing-target" : "ready");
      data.put("validationMessages", detail == null ? List.of("Choose a visible active Customer detail before suspension.") : List.of());
      data.put("boundaryNotice", "Suspension changes only the selected Customer administration lifecycle boundary. Customer Admin memberships, invitations, billing/provider settings, downstream business records, tenant application data, and sibling Customers remain separate and redacted.");
      data.put("confirmation", mapOf("consequenceCopy", detail == null ? "No Customer is selected; no suspension can run from this direct confirmation load." : "Suspend/archive " + detail.customer().customerName() + " inside the selected Organization/Tenant boundary without deleting the Customer or exposing sibling-customer facts.", "confirmationPhrase", "SUSPEND", "confirmationRequired", true, "reasonDraft", "", "reasonRequired", true, "submitLabel", "Suspend Customer", "submitActionId", "action-customer-suspend", "cancelActionId", "action-customer-read", "idempotencyKeyHint", "client-generated", "freshnessVersionHint", "detail-trace-boundary", "disabledReason", detail == null ? "missing-visible-customer" : ""));
      data.put("suspensionEligibility", mapOf("currentStatus", currentStatus, "allowedStatuses", List.of("active"), "blockedReason", detail == null ? "missing-visible-customer" : "active".equals(currentStatus) ? "" : "already-suspended", "customerAdminReadinessWarning", "Customer Admin memberships and invitations are not changed by this Customer lifecycle action.", "openInvitationWarning", "Open invitations are not exposed or mutated by this Customer suspension surface.", "approvalRequired", false, "providerOrOutboxReadinessSummary", "No external provider or outbox success is fabricated by Customer suspension.", "traceRefs", traceRefs));
      data.put("authorizedActions", actions.stream().map(SurfaceAction::actionId).toList());
      data.put("availableTaskActions", actions.stream().map(this::surfaceActionSummary).toList());
      data.put("systemStates", List.of("ready", "missing-target", "validation-error", "submitting", "success", "no-op", "idempotent-replay", "approval-required", "policy-blocked", "forbidden-hidden-not-found", "stale-conflict", "runtime-blocked"));
      data.put("lastResult", detail == null ? mapOf("status", "missing-target", "message", "Customer suspension requires a visible active Customer target loaded through the protected detail-to-suspend action path.", "correlationId", correlationId, "traceRefs", traceRefs, "noFakeSuccess", true) : null);
    }
    if ("surface-user-admin-customer-reactivate-confirmation".equals(surfaceId)) {
      var currentStatus = detail == null ? "" : detail.customer().status();
      data.put("scopeLabel", detail == null ? "Selected Organization/Tenant" : detail.customer().customerName());
      data.put("scopeType", "tenant");
      data.put("tenantId", actor.selectedContext().tenantId());
      data.put("customerId", detail == null ? "" : detail.customer().customerId());
      data.put("customerName", detail == null ? "" : detail.customer().customerName());
      data.put("currentStatus", currentStatus);
      data.put("customerStatus", currentStatus);
      data.put("authorityBasis", "selected Organization/Tenant Admin AuthContext with tenant.customer.reactivate");
      data.put("branchRootSurfaceId", "surface-user-admin-customer-directory");
      data.put("branchReturnActionId", "action-user-admin-show-customers");
      data.put("branchReturnLabel", "Back to customers");
      data.put("detailReturnActionId", "action-customer-read");
      data.put("formState", detail == null ? "missing-target" : "ready");
      data.put("validationMessages", detail == null ? List.of("Choose a visible suspended Customer detail before reactivation.") : List.of());
      data.put("boundaryNotice", "Reactivation changes only the selected Customer administration lifecycle boundary. Customer Admin memberships, invitations, billing/provider settings, downstream business records, tenant application data, and sibling Customers remain separate and redacted.");
      data.put("confirmation", mapOf("outcomeCopy", detail == null ? "No Customer is selected; no reactivation can run from this direct confirmation load." : "Reactivate " + detail.customer().customerName() + " inside the selected Organization/Tenant boundary without changing Customer Admin roles, invitations, billing/provider settings, or tenant application data.", "confirmationPhrase", "REACTIVATE", "confirmationRequired", true, "reasonDraft", "", "reasonRequired", false, "submitLabel", "Reactivate Customer", "submitActionId", "action-customer-reactivate", "cancelActionId", "action-customer-read", "idempotencyKeyHint", "client-generated", "freshnessVersionHint", "detail-trace-boundary", "disabledReason", detail == null ? "missing-visible-customer" : ""));
      data.put("reactivationEligibility", mapOf("currentStatus", currentStatus, "allowedStatuses", List.of("suspended"), "blockedReason", detail == null ? "missing-visible-customer" : "suspended".equals(currentStatus) ? "" : "already-active", "customerAdminReadinessWarning", "Customer Admin memberships and invitations are not changed by this Customer lifecycle action.", "pendingSetupWarning", "Customer Admin bootstrap and setup tasks remain separate after reactivation.", "approvalRequired", false, "providerOrOutboxReadinessSummary", "No external provider or outbox success is fabricated by Customer reactivation.", "traceRefs", traceRefs));
      data.put("authorizedActions", actions.stream().map(SurfaceAction::actionId).toList());
      data.put("availableTaskActions", actions.stream().map(this::surfaceActionSummary).toList());
      data.put("systemStates", List.of("ready", "missing-target", "validation-error", "submitting", "success", "no-op", "idempotent-replay", "approval-required", "policy-blocked", "forbidden-hidden-not-found", "stale-conflict", "runtime-blocked"));
      data.put("lastResult", detail == null ? mapOf("status", "missing-target", "message", "Customer reactivation requires a visible suspended Customer target loaded through the protected detail-to-reactivate action path.", "correlationId", correlationId, "traceRefs", traceRefs, "noFakeSuccess", true) : null);
    }
    if (detail != null) data.put("customerDetail", customerDetailMap(detail));
    return envelope(surfaceId, surfaceId.contains("suspend") ? "destructive-lifecycle-confirmation" : surfaceId.contains("reactivate") ? "lifecycle-confirmation" : surfaceId.contains("rename") ? "edit-form" : "create-form", title, actor, correlationId, data, actions);
  }

  private ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerActionResult runCustomerLifecycleAction(AuthContextResolver.ResolvedMe actor, String actionId, Object input, String idempotencyKey, String correlationId) {
    var service = StarterSecurityComponents.tenantCustomerAdminService();
    return switch (actionId) {
      case "action-submit-customer-create", "action-customer-create" -> service.createCustomer(actor, stringInput(input, "customerName", ""), idempotencyKey, stringInput(input, "reason", "customer-created"), correlationId);
      case "action-submit-customer-rename", "action-customer-rename" -> service.renameCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), stringInput(input, "customerName", ""), idempotencyKey, stringInput(input, "reason", "customer-renamed"), correlationId);
      case "action-customer-suspend" -> {
        var confirmation = stringInput(input, "confirmation", stringInput(input, "confirmationPhrase", ""));
        if (!"SUSPEND".equals(confirmation.trim())) throw new AuthorizationException(400, "confirmation-required");
        yield service.suspendCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), stringInput(input, "reason", "customer-suspended"), idempotencyKey, correlationId);
      }
      case "action-customer-reactivate" -> service.reactivateCustomer(actor, stringInput(input, "customerId", stringInput(input, "recordId", "")), stringInput(input, "reason", "customer-reactivated"), idempotencyKey, correlationId);
      default -> throw new AuthorizationException(404, "target-not-found-or-forbidden");
    };
  }

  private Map<String, Object> customerRowMap(ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerSummary customer) {
    return mapOf("id", customer.customerId(), "customerId", customer.customerId(), "customerName", customer.customerName(), "status", customer.status(), "rowType", "customer", "targetObjectType", "customer", "targetSurfaceId", "surface-user-admin-customer-detail", "targetSurfaceType", "show-inspection", "openActionId", "action-customer-read", "safeLifecycleSummary", customer.status().equals("active") ? "Active Customer boundary" : "Suspended Customer boundary", "traceRefs", customer.traceRefs(), "redactionState", "visible");
  }

  private Map<String, Object> customerAdminRowMap(UserAdminService.UserDirectoryRow row) {
    return mapOf("id", row.membershipId(), "accountId", row.accountId(), "membershipId", row.membershipId(), "displayName", row.displayName(), "email", row.accountId(), "customerId", row.customerId(), "tenantId", row.tenantId(), "role", "CUSTOMER_ADMIN", "roles", row.roles().stream().map(Enum::name).toList(), "status", row.status().name().toLowerCase(Locale.ROOT), "rowType", "customer-admin-membership", "targetObjectType", "customer-admin", "targetSurfaceId", "surface-user-admin-customer-admin-detail", "targetSurfaceType", "show-inspection", "targetActionId", "action-open-customer-admin-detail", "openActionId", "action-open-customer-admin-detail", "safeActionContext", mapOf("accountId", row.accountId(), "membershipId", row.membershipId(), "customerId", row.customerId()), "visibleActions", List.of("read", "role-preview", "lifecycle"), "traceRefs", List.of("trace-customer-admin-membership-" + stableSuffix(row.membershipId())), "redactionState", "visible");
  }

  private Map<String, Object> customerAdminInvitationRowMap(InvitationView.InvitationRow row) {
    return mapOf("id", row.invitationId(), "invitationId", row.invitationId(), "displayName", row.targetEmail(), "email", row.targetEmail(), "customerId", row.customerId(), "tenantId", row.tenantId(), "role", "CUSTOMER_ADMIN", "roles", row.requestedRoles().stream().map(Enum::name).toList(), "status", row.status().name().toLowerCase(Locale.ROOT), "deliveryStatus", row.deliveryStatus().name().toLowerCase(Locale.ROOT), "rowType", "customer-admin-invitation", "targetObjectType", "customer-admin-invitation", "targetSurfaceId", "surface-user-admin-customer-admin-detail", "targetSurfaceType", "show-inspection", "targetActionId", "action-open-customer-admin-invitation-detail", "openActionId", "action-open-customer-admin-invitation-detail", "safeActionContext", mapOf("invitationId", row.invitationId(), "customerId", row.customerId()), "visibleActions", List.of("read", "resend", "revoke"), "traceRefs", List.of("trace-customer-admin-invitation-" + stableSuffix(row.invitationId())), "redactionState", "raw-token-redacted");
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
    return envelope("surface-user-admin-user-detail", "show-inspection", "User detail", actor, correlationId, mapOf("surfaceContract", "user_admin.user_detail.v1", "branchNavigation", userBranchNavigation(correlationId), "recordId", target.accountId(), "recordLabel", target.displayName(), "recordKind", "account", "summary", "Inspect this user's browser-safe account, membership, role, support-access, access-review, identity, and audit state; consequential changes open dedicated task surfaces.", "fields", List.of(mapOf("fieldId", "displayName", "label", "Name", "value", target.displayName(), "editable", false, "inputType", "text"), mapOf("fieldId", "email", "label", "Email", "value", target.accountId(), "editable", false, "inputType", "email", "disabledReason", "Email is managed by identity provider."), mapOf("fieldId", "membershipStatus", "label", "Status", "value", target.membershipStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleValue, "editable", false, "inputType", "text")), "version", 1, "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId()), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Detail is inspection-only; backend task surfaces reauthorize every consequential change.", "authoritativeCapabilityId", USERADMIN_LIST_MEMBERS), "taskEntryPoints", List.of(mapOf("label", "Change membership or account status", "actionId", "action-open-useradmin-membership-status-confirmation", "targetSurfaceId", "surface-user-admin-membership-status-confirmation"), mapOf("label", "Preview role change", "actionId", "action-useradmin-preview-role-change", "targetSurfaceId", "surface-user-admin-role-change-preview"), mapOf("label", "Grant or extend support access", "actionId", "action-open-useradmin-support-access-grant", "targetSurfaceId", "surface-user-admin-support-access-grant"), mapOf("label", "Revoke support access", "actionId", "action-open-user-admin-support-access-revoke-confirmation", "targetSurfaceId", "surface-user-admin-support-access-revoke-confirmation"), mapOf("label", "Review identity exception", "actionId", "action-open-useradmin-identity-exception-review", "targetSurfaceId", "surface-user-admin-identity-exception-review"), mapOf("label", "Start or open access review", "actionId", "action-useradmin-read-access-review", "targetSurfaceId", "surface-user-admin-access-review-task")), "accessManagement", mapOf("advisoryNotice", "Frontend controls are advisory only; backend UserAdminService remains authoritative for membership, account, role, support-access, access-review, and audit actions.", "memberStatus", mapOf("accountStatus", "active", "membershipStatus", target.membershipStatus().name().toLowerCase(Locale.ROOT), "statusTaskSurfaceId", "surface-user-admin-membership-status-confirmation", "denialHints", List.of("disabled actor", "inactive membership", "cross-tenant", "CUSTOMER_ADMIN_TENANT_ACTION_DENIED", "SAAS_OWNER_NO_SUPPORT_ACCESS", "last-admin"), "noOpMessage", "Repeated status changes return no-op/idempotent evidence where state already matches.", "idempotencyKeySource", "client-generated", "traceLinks", List.of("trace-useradmin-status-action", traceId)), "supportAccess", mapOf("supportAccess", target.supportAccess(), "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "grantTaskSurfaceId", "surface-user-admin-support-access-grant", "revokeTaskSurfaceId", "surface-user-admin-support-access-revoke-confirmation", "denialHints", List.of("tenant-created support grant required", "SAAS_OWNER_NO_SUPPORT_ACCESS", "support-access-expired", "missing-capability"), "traceLinks", List.of("trace-useradmin-support-access"))), "audit", mapOf("lastEventType", "UserAdminDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", List.of(traceId, "trace-useradmin-support-access"))), withUserBranchReturn(List.of(openMembershipStatusConfirmationAction(), openUserAdminMembershipStatusConfirmationAction(), openSupportAccessGrantAction(), openSupportAccessRevokeConfirmationAction(), openUserAdminSupportAccessRevokeConfirmationAction(), openIdentityExceptionReviewAction(), previewRoleChangeAction(), readAccessReviewAction(), openAuditAction())));
  }

  private SurfaceEnvelope invitationDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return invitationDetailSurface(actor, input, correlationId, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
  }

  private SurfaceEnvelope invitationDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId, ScopeType scopeType, String tenantId, String customerId) {
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_LIST_INVITATIONS, "user_admin.invitation_detail.v1", correlationId);
    var invitationId = stringInput(input, "invitationId", latestInvitationId(actor));
    var invite = invitationView.list(actor, scopeType, tenantId, customerId).stream()
        .filter(row -> invitationId.equals(row.invitationId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
    return invitationDetailSurfaceFromRow(actor, invite, correlationId);
  }

  private SurfaceEnvelope invitationDetailSurface(AuthContextResolver.ResolvedMe actor, Invitation invite, String correlationId) {
    var row = new InvitationView.InvitationRow(invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(), invite.status(), invite.deliveryStatus(), invite.deliveryAttempts(), invite.resendCount(), invite.lastDeliveryErrorSummary(), invite.expiresAt(), invite.createdAt(), invite.acceptedAt(), invite.revokedAt(), invite.createdByAccountId(), invite.resendable(), !invite.terminal());
    return invitationDetailSurfaceFromRow(actor, row, correlationId);
  }

  private SurfaceEnvelope invitationDetailSurfaceFromRow(AuthContextResolver.ResolvedMe actor, InvitationView.InvitationRow invite, String correlationId) {
    var traceRefs = List.of("trace-useradmin-invitation-" + stableSuffix(invite.invitationId()));
    var deliveryStatus = invitationDeliveryState(invite, correlationId);
    var recoverySteps = invitationRecoverySteps(invite);
    var lifecycleStatus = mapOf(
        "currentState", invite.status().name().toLowerCase(Locale.ROOT),
        "terminal", !invite.canResend() && !invite.canRevoke(),
        "acceptedAt", invite.acceptedAt() == null ? null : invite.acceptedAt().toString(),
        "revokedAt", invite.revokedAt() == null ? null : invite.revokedAt().toString(),
        "recoveryGuidance", recoverySteps,
        "traceRefs", traceRefs);
    var eligibility = mapOf(
        "canResend", invite.canResend(),
        "resendDisabledReason", invite.canResend() ? null : "Invitation lifecycle state is not resendable in the selected authorized scope.",
        "canRevoke", invite.canRevoke(),
        "revokeDisabledReason", invite.canRevoke() ? null : "Invitation lifecycle state is not revocable in the selected authorized scope.",
        "canOpenAcceptedUser", invite.status() == InvitationStatus.ACCEPTED,
        "canOpenAudit", true,
        "approvalRequired", false,
        "reasonRequired", false,
        "idempotencyHint", "client-generated for lifecycle confirmation commands",
        "traceRefs", traceRefs);
    var taskEntryPoints = List.of(
        mapOf("label", "Open resend confirmation", "actionId", "action-open-useradmin-invitation-resend-confirmation", "targetSurfaceId", "surface-user-admin-invitation-resend-confirmation"),
        mapOf("label", "Open revoke confirmation", "actionId", "action-open-useradmin-invitation-revoke-confirmation", "targetSurfaceId", "surface-user-admin-invitation-revoke-confirmation"));
    var actions = withUserBranchReturn(List.of(openInvitationResendConfirmationAction(), openInvitationRevokeConfirmationAction(), openAuditAction()));
    return envelope("surface-user-admin-invitation-detail", "show-inspection", "Invitation detail", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_detail.v1", "branchNavigation", userBranchNavigation(correlationId), "branchRootSurfaceId", "surface-user-admin-users", "branchReturnActionId", "action-user-admin-show-users", "branchReturnLabel", "Show users", "recordId", invite.invitationId(), "recordLabel", invite.targetEmail(), "recordKind", "invitation", "scopeType", invite.scopeType().name().toLowerCase(Locale.ROOT), "scopeLabel", invite.scopeType().name().toLowerCase(Locale.ROOT), "status", invitationSurfaceStatus(invite), "summary", "Inspect this invitation lifecycle and provider-backed delivery state; resend and revoke open dedicated confirmation surfaces before backend commands run.", "boundaryNotice", "Invitation tokens, email bodies, raw JWTs, provider payloads, and hidden scope facts are omitted from this browser surface.", "actionContext", mapOf("invitationId", invite.invitationId()), "invitationSummary", mapOf("targetEmail", invite.targetEmail(), "targetScopeType", invite.scopeType().name().toLowerCase(Locale.ROOT), "tenantId", invite.tenantId(), "customerId", invite.customerId(), "requestedRoleLabels", roleLabels(invite.requestedRoles()), "invitationStatus", invitationSurfaceStatus(invite), "createdAt", invite.createdAt().toString(), "acceptedAt", invite.acceptedAt() == null ? null : invite.acceptedAt().toString(), "expiresAt", invite.expiresAt().toString(), "revokedAt", invite.revokedAt() == null ? null : invite.revokedAt().toString(), "inviterSummary", "authorized-admin", "freshness", "backend-derived"), "deliveryStatus", deliveryStatus, "deliveryState", deliveryStatus, "lifecycleStatus", lifecycleStatus, "eligibility", eligibility, "taskEntryPoints", taskEntryPoints, "availableTaskActions", actions.stream().map(this::authorizedActionMap).toList(), "permissionState", mapOf("canMutateInline", false, "canOpenTaskSurfaces", true, "reason", "Invitation detail is inspection-only and never resends or revokes inline."), "fields", List.of(mapOf("fieldId", "email", "label", "Email", "value", invite.targetEmail(), "editable", false, "inputType", "email"), mapOf("fieldId", "status", "label", "Status", "value", invite.status().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "role", "label", "Role", "value", roleLabels(invite.requestedRoles()), "editable", false, "inputType", "text"), mapOf("fieldId", "delivery", "label", "Delivery", "value", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "editable", false, "inputType", "text"), mapOf("fieldId", "deliveryAttempts", "label", "Delivery attempts", "value", String.valueOf(invite.deliveryAttempts()), "editable", false, "inputType", "text"), mapOf("fieldId", "expiresAt", "label", "Expires", "value", invite.expiresAt().toString(), "editable", false, "inputType", "text")), "recoverySteps", recoverySteps, "systemStates", invitationSystemStates(invite), "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "email-body-redacted", "raw-jwt-redacted", "hidden-scope-facts-redacted"), "noFakeSuccess", invite.deliveryStatus() == EmailDeliveryStatus.FAILED, "providerBlockedSystemMessage", invite.deliveryStatus() == EmailDeliveryStatus.FAILED ? mapOf("surfaceContract", "user_admin.system_message.v1", "status", "blocked_provider_or_runtime", "safeReasonCode", firstNonBlank(invite.lastDeliveryErrorSummary(), "provider-or-outbox-delivery-failed"), "message", "Invitation delivery failed closed; use the resend confirmation task only after backend provider/outbox readiness is restored.") : null, "audit", mapOf("lastEventType", "InvitationDetailDisplayed", "lastActor", actor.profile().displayName(), "traceIds", traceRefs)),
        actions);
  }


  private SurfaceEnvelope roleChangePreviewSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var membershipId = rolePreviewMembershipId(actor, input);
    var proposedRoles = rolePreviewRoles(actor, input, membershipId);
    var preview = userAdminService.previewRoleChange(actor, membershipId, proposedRoles, stringInput(input, "reason", "protected direct role-preview load"), correlationId);
    return roleChangePreviewSurface(actor, preview, membershipId, proposedRoles, correlationId);
  }

  private SurfaceEnvelope roleChangePreviewSurface(AuthContextResolver.ResolvedMe actor, UserAdminService.RoleChangePreview preview, String membershipId, List<FoundationRole> proposedRoles, String correlationId) {
    var target = userAdminService.listUsers(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(row -> row.membershipId().equals(membershipId))
        .findFirst()
        .orElse(null);
    var currentRoles = target == null ? actor.selectedContext().roles() : target.roles();
    var traceRefs = List.of(preview.traceId());
    var status = preview.allowed() ? preview.noOp() ? "no-op" : "ready" : "denied";
    var actions = withUserBranchReturn(List.of(openUserAdminRoleChangePreviewAction(), commitUserAdminRoleChangeAction(), reviseUserAdminRoleChangeAction(), openUserAdminMembershipStatusConfirmationAction(), openAuditAction()));
    return envelope("surface-user-admin-role-change-preview", "decision-card", "Role change preview", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.role_change_preview.v1",
            "branchNavigation", userBranchNavigation(correlationId),
            "branchRootSurfaceId", "surface-user-admin-users",
            "branchReturnActionId", "action-user-admin-show-users",
            "branchReturnLabel", "Show users",
            "recordId", membershipId,
            "recordKind", "role_change_preview",
            "recordLabel", target == null ? "Selected membership" : target.displayName(),
            "scopeLabel", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
            "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
            "authorityBasis", mapOf("previewCapabilityId", USERADMIN_PREVIEW_ROLE_CHANGE, "commitCapabilityId", USERADMIN_CHANGE_MEMBER_ROLES, "selectedContextId", actor.selectedContext().membershipId()),
            "status", status,
            "message", preview.message(),
            "targetSummary", mapOf("accountId", target == null ? actor.account().accountId() : target.accountId(), "membershipId", membershipId, "displayName", target == null ? actor.profile().displayName() : target.displayName(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "currentMembershipStatus", target == null ? "active" : target.status().name().toLowerCase(Locale.ROOT), "currentRoleLabels", roleLabels(currentRoles), "freshness", "backend-derived"),
            "roleChangeProposal", mapOf("previewId", "role-preview-" + stableSuffix(correlationId), "currentRoleLabels", roleLabels(currentRoles), "proposedRoleLabels", roleLabels(proposedRoles), "addedRoles", roleLabels(proposedRoles.stream().filter(role -> !currentRoles.contains(role)).toList()), "removedRoles", roleLabels(currentRoles.stream().filter(role -> !proposedRoles.contains(role)).toList()), "unchangedRoles", roleLabels(currentRoles.stream().filter(proposedRoles::contains).toList()), "requestedBySummary", actor.profile().displayName(), "requestedReason", "backend-authorized role preview", "proposedEffectiveScope", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "previewVersion", 1, "idempotencyHint", "client-generated on commit", "traceRefs", traceRefs),
            "capabilityDelta", mapOf("added", preview.capabilityDelta().stream().filter(value -> value.startsWith("adds:")).toList(), "removed", preview.capabilityDelta().stream().filter(value -> value.startsWith("removes:")).toList(), "unchanged", preview.capabilityDelta().stream().filter(value -> !value.startsWith("adds:") && !value.startsWith("removes:")).toList(), "addedCapabilitiesByWorkstream", preview.capabilityDelta().stream().filter(value -> value.startsWith("adds:")).toList(), "removedCapabilitiesByWorkstream", preview.capabilityDelta().stream().filter(value -> value.startsWith("removes:")).toList(), "unchangedCapabilitySummary", List.of("unchanged scoped read/audit capabilities remain backend-authorized only"), "affectedWorkstreams", preview.affectedWorkstreams(), "browserToolImpact", List.of("User Admin task actions are reauthorized server-side"), "adminAuthorityDelta", preview.message(), "riskLevel", preview.allowed() ? preview.noOp() ? "none" : "medium" : "blocked", "traceRefs", traceRefs),
            "affectedWorkstreams", preview.affectedWorkstreams(),
            "policyDecision", mapOf("canCommit", preview.allowed() && !preview.noOp(), "approvalRequired", true, "approvalState", preview.allowed() ? "human-review-before-commit" : "blocked", "blockedReason", preview.allowed() ? null : preview.message(), "lastAdminImpact", preview.lastAdminImpact(), "selfActionRisk", preview.message().contains("self") ? "blocked" : "none-detected", "escalationRisk", preview.allowed() && !preview.noOp() ? "requires-backend-commit-authorization" : "none", "policySummary", "Backend UserAdminService enforces assignable roles, self-admin-role removal, last-admin, selected AuthContext, and audit requirements before commit.", "alternatives", List.of("Return to user detail", "Revise proposed roles", "Open membership status confirmation for lifecycle work"), "traceRefs", traceRefs),
            "decisionEvidence", mapOf("evidenceWindow", "current selected AuthContext and visible membership", "currentAccessEvidence", roleLabels(currentRoles), "proposedAccessEvidence", roleLabels(proposedRoles), "recentAdminEvents", List.of("preview trace recorded"), "denialOrNoOpHistory", preview.noOp() ? List.of("requested roles already match current assignment") : List.of(), "redactionSummary", "raw JWTs, provider ids, hidden roles, policy internals, and sibling-scope facts are omitted", "auditDrilldownActionId", "action-open-user-admin-audit"),
            "confirmationForm", mapOf("reasonDraft", "", "reasonRequired", true, "confirmationTextRequired", false, "submitLabel", "Commit role change", "reviseActionId", "action-revise-user-admin-role-change", "cancelActionId", "action-open-user-admin-user-detail", "submitActionId", "action-commit-user-admin-role-change", "idempotencyHint", "client-generated", "disabledReason", preview.allowed() && !preview.noOp() ? null : preview.message()),
            "policyHints", preview.policyHints(),
            "lastAdminImpact", preview.lastAdminImpact(),
            "traceLinks", traceRefs,
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "systemStates", List.of("validation-error", "approval-required", "system-message", "last-admin-risk", "self-action-risk", "no-op", "idempotent-replay", "stale-conflict", "hidden-not-found"),
            "redaction", List.of("raw-jwt-redacted", "provider-payload-redacted", "private-profile-redacted", "hidden-role-redacted", "sibling-scope-facts-redacted", "raw-policy-redacted"),
            "boundaryNotice", "Role proposal, capability delta, and commit eligibility are backend-derived for the selected AuthContext; hidden users, hidden roles, provider payloads, raw policy internals, and sibling-scope facts are redacted.",
            "authorizedActions", actions.stream().map(this::authorizedActionMap).toList()),
        actions);
  }

  private SurfaceEnvelope invitationCreateSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    if (!isActionCapabilityVisible(actor, USERADMIN_SEND_INVITATION)) throw new AuthorizationException(403, "CAPABILITY_FORBIDDEN");
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_SEND_INVITATION, "user_admin.invitation_create.v1", correlationId);
    var roles = rolesInput(input).stream().map(FoundationRole::name).toList();
    var roleOptions = roleOptionsForSelectedContext(actor);
    var expiryOptions = invitationExpiryOptions();
    var targetScope = mapOf("scopeType", actor.selectedContext().scopeType().name(), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId());
    var traceRefs = List.of("trace-useradmin-invitation-create-" + stableSuffix(correlationId));
    var actions = withUserBranchReturn(List.of(submitUserAdminInvitationAction(), inviteAction(), openAuditAction()));
    return envelope("surface-user-admin-invitation-create", "create-form", "Invite user", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.invitation_create.v1",
            "branchNavigation", userBranchNavigation(correlationId),
            "branchRootSurfaceId", "surface-user-admin-users",
            "branchReturnActionId", "action-user-admin-show-users",
            "branchReturnLabel", "Show users",
            "status", "ready",
            "formState", "ready",
            "summary", "Create a scoped invitation through the backend InvitationService. Submission requires a client idempotency key and returns invitation detail or a safe validation/denial result.",
            "targetScope", targetScope,
            "scopeSummary", mapOf("scopeType", actor.selectedContext().scopeType().name(), "scopeLabel", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "inviteAuthority", USERADMIN_SEND_INVITATION, "freshness", "current-selected-context"),
            "draft", mapOf("email", stringInput(input, "email", ""), "displayName", stringInput(input, "displayName", ""), "roles", roles),
            "form", mapOf("emailDraft", stringInput(input, "email", ""), "displayNameDraft", stringInput(input, "displayName", ""), "reasonDraft", stringInput(input, "reason", ""), "targetRoleOptions", roleOptions, "selectedTargetRoles", roles.isEmpty() ? List.of() : roles, "expiryOptions", expiryOptions, "selectedExpiry", "7d", "idempotencyKeyHint", "client-generated", "submitLabel", "Create invitation", "cancelActionId", "action-user-admin-show-users", "submitActionId", "action-submit-user-admin-invitation"),
            "roleOptions", roleOptions,
            "policyOptions", mapOf("expiryOptions", expiryOptions, "idempotency", "client-generated", "outboxReadiness", "backend-derived"),
            "policyContext", mapOf("duplicateOpenInvitePolicy", "reuse-visible-open-invitation-or-safe-no-enumeration", "selfInvitePolicy", "denied", "reasonRequired", false, "approvalRequired", false, "traceRefs", traceRefs),
            "deliveryReadiness", mapOf("outboxStatus", "backend-derived", "providerStatus", "fail-closed-if-unconfigured", "retryEligible", true, "expectedDeliveryChannel", "in_app_or_email", "noFakeSuccess", true, "traceRefs", traceRefs),
            "validationMessages", List.of(),
            "systemStates", List.of("validation-error", "duplicate-no-op", "provider-outbox-blocked", "forbidden-hidden-not-found", "stale-conflict"),
            "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(),
            "idempotencyKeyHint", "client-generated",
            "outboxBoundary", "Invitation email delivery is queued through the backend outbox/provider path; tokens and provider payloads are never exposed.",
            "boundaryNotice", "Role, customer, and expiry options are backend-authored for the selected scope; hidden users, sibling scopes, provider payloads, and invitation tokens are redacted.",
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "raw-jwt-redacted", "hidden-roles-redacted", "sibling-scope-facts-redacted")),
        actions);
  }

  private SurfaceEnvelope invitationResendConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var invite = invitationForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_RESEND_INVITATION, "user_admin.invitation_resend_confirmation.v1", correlationId);
    var traceRefs = List.of("trace-useradmin-invitation-resend-confirmation-" + stableSuffix(invite.invitationId() + correlationId));
    var deliveryState = invitationDeliveryState(invite, correlationId);
    var canResend = invite.canResend();
    var resendEligibility = mapOf(
        "canResend", canResend,
        "disabledReason", canResend ? null : "Invitation lifecycle state is not resendable in the selected authorized scope.",
        "terminalState", invite.status().name().toLowerCase(Locale.ROOT),
        "staleVersion", false,
        "retryWindow", invite.expiresAt().toString(),
        "duplicateOpenInviteSummary", "backend-detects-visible-duplicates-or-returns-safe-no-enumeration",
        "approvalRequired", false,
        "reasonRequired", false,
        "traceRefs", traceRefs);
    var actions = withUserBranchReturn(List.of(resendInvitationAction(), displayInvitationDetailAction(), openAuditAction()));
    return envelope("surface-user-admin-invitation-resend-confirmation", "lifecycle-confirmation", "Resend invitation", actor, correlationId,
        mapOf(
            "surfaceContract", "user_admin.invitation_resend_confirmation.v1",
            "branchNavigation", userBranchNavigation(correlationId),
            "branchRootSurfaceId", "surface-user-admin-users",
            "branchReturnActionId", "action-user-admin-show-users",
            "branchReturnLabel", "Show users",
            "recordId", invite.invitationId(),
            "recordLabel", invite.targetEmail(),
            "recordKind", "invitation",
            "status", canResend ? "ready" : "no-op",
            "summary", "Confirm one scoped invitation resend through backend authorization, idempotency, outbox/provider fail-closed checks, audit trace, and refreshed invitation detail routing.",
            "confirmationCopy", canResend ? "Resend this visible invitation through the governed backend outbox after rechecking eligibility." : "This invitation is not eligible for resend in the selected context.",
            "invitationSummary", mapOf("targetEmail", invite.targetEmail(), "targetScopeType", invite.scopeType().name().toLowerCase(Locale.ROOT), "tenantId", invite.tenantId(), "customerId", invite.customerId(), "requestedRoleLabels", roleLabels(invite.requestedRoles()), "invitationStatus", invite.status().name().toLowerCase(Locale.ROOT), "deliveryStatus", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "expiresAt", invite.expiresAt().toString(), "freshness", "backend-derived"),
            "resendEligibility", resendEligibility,
            "deliveryReadiness", mapOf("outboxStatus", deliveryState.get("providerReadiness"), "providerStatus", deliveryState.get("providerReadiness"), "retryEligible", canResend, "failClosedMessage", invite.deliveryStatus() == EmailDeliveryStatus.FAILED ? firstNonBlank(invite.lastDeliveryErrorSummary(), "provider-or-outbox-delivery-failed") : null, "noFakeSuccess", true, "traceRefs", traceRefs),
            "confirmationForm", mapOf("reasonDraft", "", "reasonRequired", false, "confirmationRequired", true, "submitLabel", "Resend invitation", "submitActionId", "action-useradmin-resend-invitation", "cancelActionId", "action-display-invitation-detail", "idempotencyKeyHint", "client-generated"),
            "actionContext", mapOf("invitationId", invite.invitationId()),
            "reasonRequired", false,
            "confirmationRequired", true,
            "idempotencyKeyHint", "client-generated",
            "delivery", deliveryState,
            "deliveryState", deliveryState,
            "recoverySteps", invitationRecoverySteps(invite),
            "systemStates", invitationSystemStates(invite),
            "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(),
            "traceRefs", traceRefs,
            "correlationId", correlationId,
            "redaction", List.of("invitation-token-redacted", "email-body-redacted", "provider-payload-redacted", "provider-secret-redacted", "raw-jwt-redacted", "hidden-scope-facts-redacted"),
            "noFakeSuccess", true,
            "noDirectMutation", true,
            "boundaryNotice", "The browser surface never exposes invitation tokens, full email bodies, raw JWTs, provider payloads, provider secrets, hidden scopes, or sibling-customer facts."),
        actions);
  }

  private SurfaceEnvelope invitationRevokeConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var invite = invitationForTaskSurface(actor, input);
    var canRevoke = invite.canRevoke();
    var traceRefs = List.of("trace-useradmin-invitation-revoke-confirmation-" + stableSuffix(invite.invitationId() + correlationId));
    var deliveryState = invitationDeliveryState(invite, correlationId);
    var eligibility = mapOf(
        "canRevoke", canRevoke,
        "disabledReason", canRevoke ? null : "Invitation lifecycle state is not revocable in the selected authorized scope.",
        "terminalState", !canRevoke,
        "acceptedAccountSummary", invite.acceptedAt() == null ? null : "Invitation was already accepted; use authorized user or membership lifecycle tasks instead.",
        "reasonRequired", true,
        "confirmationRequired", true,
        "traceRefs", traceRefs);
    var consequenceSummary = mapOf(
        "consequenceCopy", canRevoke ? "Revoking prevents this invitation from being accepted. Existing accounts or memberships are not changed by this action." : "This invitation cannot be revoked from its current lifecycle state.",
        "affectedAccessCopy", "Only this invitation's future acceptance path changes; existing accounts, memberships, roles, and support grants are not directly mutated.",
        "deliveryCancellationCopy", "Any queued delivery state is represented through backend invitation lifecycle state; provider payloads and email bodies remain redacted.",
        "noDirectMembershipMutation", true,
        "noFakeSuccess", true,
        "recoveryGuidance", invitationRecoverySteps(invite),
        "traceRefs", traceRefs);
    var actions = withUserBranchReturn(List.of(revokeInvitationAction(), confirmUserAdminInvitationRevokeAction(), displayInvitationDetailAction(), showUsersAction(), openAuditAction()));
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_REVOKE_INVITATION, "user_admin.invitation_revoke_confirmation.v1", correlationId);
    return envelope("surface-user-admin-invitation-revoke-confirmation", "destructive-lifecycle-confirmation", "Revoke invitation", actor, correlationId,
        mapOf("surfaceContract", "user_admin.invitation_revoke_confirmation.v1", "branchNavigation", userBranchNavigation(correlationId), "branchRootSurfaceId", "surface-user-admin-users", "branchReturnActionId", "action-user-admin-show-users", "branchReturnLabel", "Show users", "recordId", invite.invitationId(), "recordLabel", invite.targetEmail(), "recordKind", "invitation", "scopeType", invite.scopeType().name().toLowerCase(Locale.ROOT), "scopeLabel", invite.scopeType().name().toLowerCase(Locale.ROOT), "status", canRevoke ? "ready" : "no-op", "summary", "Confirm one visible invitation revocation through backend authorization, reason capture, idempotency, audit trace, and refreshed invitation detail routing.", "invitationSummary", mapOf("targetEmail", invite.targetEmail(), "targetScopeType", invite.scopeType().name().toLowerCase(Locale.ROOT), "tenantId", invite.tenantId(), "customerId", invite.customerId(), "requestedRoleLabels", roleLabels(invite.requestedRoles()), "invitationStatus", invite.status().name().toLowerCase(Locale.ROOT), "deliveryStatus", invite.deliveryStatus().name().toLowerCase(Locale.ROOT), "createdAt", invite.createdAt().toString(), "acceptedAt", invite.acceptedAt() == null ? null : invite.acceptedAt().toString(), "expiresAt", invite.expiresAt().toString(), "revokedAt", invite.revokedAt() == null ? null : invite.revokedAt().toString(), "inviterSummary", "authorized-admin", "freshness", "backend-derived"), "revokeEligibility", eligibility, "consequenceSummary", consequenceSummary, "consequenceCopy", consequenceSummary.get("consequenceCopy"), "confirmationForm", mapOf("reasonDraft", "", "reasonRequired", true, "confirmationRequired", true, "confirmationTextPrompt", "Confirm that this invitation should no longer be accepted.", "submitLabel", "Revoke invitation", "submitActionId", "action-confirm-user-admin-invitation-revoke", "cancelActionId", "action-display-invitation-detail", "idempotencyHint", "client-generated"), "actionContext", mapOf("invitationId", invite.invitationId()), "reasonRequired", true, "confirmationRequired", true, "idempotencyKeyHint", "client-generated", "deliveryState", deliveryState, "recoverySteps", invitationRecoverySteps(invite), "systemStates", invitationSystemStates(invite), "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(), "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("invitation-token-redacted", "email-body-redacted", "provider-payload-redacted", "provider-secret-redacted", "raw-jwt-redacted", "hidden-scope-facts-redacted"), "noFakeSuccess", true, "noDirectMutation", true, "boundaryNotice", "The browser surface never exposes invitation tokens, full email bodies, raw JWTs, provider payloads, provider secrets, hidden scopes, or sibling-customer facts."),
        actions);
  }

  private SurfaceEnvelope membershipStatusConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var target = userForTaskSurface(actor, input);
    var currentStatus = target.membershipStatus().name().toLowerCase(Locale.ROOT);
    var proposedStatus = stringInput(input, "status", target.membershipStatus() == MembershipStatus.ACTIVE ? "removed" : "active");
    var operation = requestedMembershipOperation(proposedStatus, target.membershipStatus());
    var traceRefs = List.of("trace-useradmin-membership-status-confirmation-" + stableSuffix(target.membershipId() + correlationId));
    var actions = withUserBranchReturn(List.of(confirmMembershipStatusChangeAction(), updateMemberStatusAction(), reactivateMemberStatusAction(), disableAccountAction(), reactivateAccountAction(), displayDetailAction(), showUsersAction(), previewRoleChangeAction(), openAuditAction()));
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_UPDATE_MEMBER_STATUS, "user_admin.membership_status_confirmation.v1", correlationId);
    return envelope("surface-user-admin-membership-status-confirmation", "destructive-lifecycle-confirmation", "Confirm membership status", actor, correlationId,
        mapOf("surfaceContract", "user_admin.membership_status_confirmation.v1", "branchNavigation", userBranchNavigation(correlationId), "branchRootSurfaceId", "surface-user-admin-users", "branchReturnActionId", "action-display-user-detail", "branchReturnLabel", "Back to user detail", "recordId", target.membershipId(), "recordLabel", target.displayName(), "recordKind", "membership", "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "scopeLabel", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "currentStatus", currentStatus, "proposedStatus", proposedStatus, "statusOptions", membershipStatusOptions(), "targetSummary", mapOf("displayName", target.displayName(), "email", target.accountId(), "accountId", target.accountId(), "membershipId", target.membershipId(), "selectedScopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "currentAccountStatus", "active", "currentMembershipStatus", currentStatus, "roleLabels", roleLabels(target.roles()), "supportAccessSummary", target.supportAccess() ? "active" : "not-active", "freshness", "backend-derived"), "proposedLifecycleChange", mapOf("requestedOperation", operation, "currentState", currentStatus, "proposedState", proposedStatus, "destructive", !"active".equalsIgnoreCase(proposedStatus), "reversible", true, "affectsSignIn", false, "affectsSelectedScopeOnly", true, "roleUnchanged", true, "invitationUnchanged", true, "traceRefs", traceRefs), "eligibility", mapOf("canSubmit", true, "lastAdminRisk", false, "selfActionRisk", actor.account().accountId().equals(target.accountId()) && !"active".equalsIgnoreCase(proposedStatus), "approvalRequired", false, "reasonRequired", true, "confirmationRequired", true, "policySummary", "Backend reauthorizes selected AuthContext, visibility, self-action, last-admin, idempotency, and current status before mutation.", "traceRefs", traceRefs), "consequenceSummary", mapOf("consequenceCopy", "Membership/account lifecycle changes are reauthorized by UserAdminService and enforce self-action, last-admin, scope, idempotency, and audit guardrails before changing state.", "affectedAccessCopy", "Only the visible membership lifecycle state changes; roles, support access, invitations, identity provider state, and access-review recommendations are unchanged.", "downstreamWorkstreamImpact", List.of("Selected scope access may be disabled until reactivated."), "recoveryGuidance", List.of("Use Back to user detail to review current lifecycle state.", "Use Role preview when role/capability changes are intended instead of lifecycle changes."), "noRoleMutation", true, "noSupportAccessMutation", true, "noInvitationMutation", true, "noDirectAccessReviewMutation", true, "noFakeSuccess", true, "traceRefs", traceRefs), "consequenceCopy", "Membership/account lifecycle changes are reauthorized by UserAdminService and enforce self-action, last-admin, scope, idempotency, and audit guardrails before changing state.", "confirmationForm", mapOf("reasonDraft", "", "reasonRequired", true, "confirmationRequired", true, "confirmationTextPrompt", "Confirm this visible membership lifecycle change.", "submitLabel", "Confirm status change", "submitActionId", "action-confirm-user-admin-membership-status-change", "cancelActionId", "action-display-user-detail", "idempotencyHint", "client-generated"), "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId(), "status", proposedStatus), "reasonRequired", true, "confirmationRequired", true, "idempotencyKeyHint", "client-generated", "systemStates", List.of("ready", "validation-error", "stale", "last-admin-denied", "self-action-denied", "no-op", "hidden-not-found"), "denialHints", List.of("self-disable-denied", "last-admin-denied", "scope-forbidden", "idempotency-key-required"), "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(), "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("cross-scope-users-redacted", "raw-jwt-redacted", "provider-payload-redacted", "private-profile-redacted", "hidden-role-redacted"), "noFakeSuccess", true, "noDirectMutation", true, "boundaryNotice", "The browser surface never exposes raw JWTs, provider payloads, private profile settings, hidden roles/capabilities, or sibling-scope facts."),
        actions);
  }

  private SurfaceEnvelope supportAccessGrantSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var target = userForTaskSurface(actor, input);
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_SUPPORT_ACCESS_GRANT, "user_admin.support_access_grant.v1", correlationId);
    var traceRefs = List.of("trace-useradmin-support-access-grant-surface-" + stableSuffix(target.membershipId() + correlationId));
    return envelope("surface-user-admin-support-access-grant", "create-form", "Grant support access", actor, correlationId,
        mapOf("surfaceContract", "user_admin.support_access_grant.v1", "branchNavigation", userBranchNavigation(correlationId), "branchRootSurfaceId", "surface-user-admin-users", "branchReturnActionId", "action-display-user-detail", "branchReturnLabel", "Back to user detail", "recordId", target.membershipId(), "recordLabel", target.displayName(), "recordKind", "support_access_grant", "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "scopeLabel", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "targetSummary", mapOf("displayName", target.displayName(), "email", target.accountId(), "accountId", target.accountId(), "membershipId", target.membershipId(), "selectedScopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "roleLabels", roleLabels(target.roles()), "supportAccessSummary", target.supportAccess() ? "active" : "not-active", "freshness", "backend-derived"), "currentSupportAccess", mapOf("active", target.supportAccess(), "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "version", target.membershipId() + ":" + (target.expiresAt() == null ? "none" : target.expiresAt().toString()), "traceRefs", traceRefs), "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "summary", "Grant or extend time-boxed support access through backend UserAdminService; SaaS Owner support access remains denied without explicit tenant-created support grant authority.", "grantRequestForm", mapOf("purposeDraft", "", "purposeRequired", true, "expiryHoursOptions", supportExpiryOptions(), "submitActionId", "action-submit-user-admin-support-access-grant", "validateActionId", "action-validate-user-admin-support-access-grant", "cancelActionId", "action-display-user-detail", "idempotencyHint", "client-generated"), "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId()), "purposeRequired", true, "supportExpiryOptions", supportExpiryOptions(), "purposeOptions", List.of("incident-response", "customer-requested-support", "access-review-remediation"), "policyContext", mapOf("maxDurationHours", 8, "requiresReason", true, "approvalRequired", true, "separationOfDuties", true, "policySummary", "Backend reauthorizes selected AuthContext, target visibility, purpose, expiry, idempotency, and support-access capability before mutation.", "traceRefs", traceRefs), "policyOptions", mapOf("maxDurationHours", 8, "requiresReason", true, "requiresBackendApproval", true), "decisionEvidence", mapOf("noRoleMutation", true, "noMembershipLifecycleMutation", true, "noInvitationMutation", true, "noIdentityProviderMutation", true, "noDirectAccessReviewMutation", true, "noFakeSuccess", true, "traceRefs", traceRefs), "authorizedActions", withUserBranchReturn(List.of(validateSupportAccessGrantAction(), submitSupportAccessGrantAction(), grantSupportAccessAction(), extendSupportAccessAction(), displayDetailAction(), showUsersAction(), openAuditAction())).stream().map(this::authorizedActionMap).toList(), "systemStates", List.of("ready", "validation-error", "stale", "conflict", "approval-required", "policy-blocked", "no-op", "hidden-not-found"), "validationMessages", List.of(), "idempotencyKeyHint", "client-generated", "approverPolicy", "backend-authorized selected AuthContext and support-access capability", "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("support-provider-internals-redacted", "raw-jwt-redacted", "private-profile-redacted", "hidden-grant-redacted", "sibling-scope-redacted"), "noDirectMutation", true, "boundaryNotice", "The browser support-access form never exposes raw JWTs, provider payloads, private profile settings, hidden grants, or sibling-scope facts."),
        withUserBranchReturn(List.of(validateSupportAccessGrantAction(), submitSupportAccessGrantAction(), grantSupportAccessAction(), extendSupportAccessAction(), displayDetailAction(), openAuditAction())));
  }

  private SurfaceEnvelope supportAccessRevokeConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var target = userForTaskSurface(actor, input);
    var traceRefs = List.of("trace-useradmin-support-access-revoke-surface-" + stableSuffix(target.membershipId() + correlationId));
    var active = target.supportAccess();
    var actions = withUserBranchReturn(List.of(confirmUserAdminSupportAccessRevokeAction(), revokeSupportAccessAction(), displayDetailAction(), showUsersAction(), openAuditAction()));
    authContextResolver.appendProtectedReadTrace(actor, USERADMIN_SUPPORT_ACCESS_REVOKE, "user_admin.support_access_revoke_confirmation.v1", correlationId);
    return envelope("surface-user-admin-support-access-revoke-confirmation", "destructive-lifecycle-confirmation", "Revoke support access", actor, correlationId,
        mapOf("surfaceContract", "user_admin.support_access_revoke_confirmation.v1", "branchNavigation", userBranchNavigation(correlationId), "branchRootSurfaceId", "surface-user-admin-users", "branchReturnActionId", "action-display-user-detail", "branchReturnLabel", "Back to user detail", "recordId", target.membershipId(), "recordLabel", target.displayName(), "recordKind", "support_access_revoke", "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "scopeLabel", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "targetSummary", mapOf("displayName", target.displayName(), "email", target.accountId(), "accountId", target.accountId(), "membershipId", target.membershipId(), "selectedScopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "roleLabels", roleLabels(target.roles()), "supportAccessSummary", active ? "active" : "not-active", "freshness", "backend-derived"), "activeSupportGrant", mapOf("active", active, "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "version", target.membershipId() + ":" + (target.expiresAt() == null ? "none" : target.expiresAt().toString()), "traceRefs", traceRefs), "currentSupportAccess", mapOf("active", active, "expiresAt", target.expiresAt() == null ? null : target.expiresAt().toString(), "version", target.membershipId() + ":" + (target.expiresAt() == null ? "none" : target.expiresAt().toString()), "traceRefs", traceRefs), "status", active ? "ready" : "no-op", "summary", active ? "Confirm revocation of this visible time-boxed support grant through backend UserAdminService authorization and audit." : "No active support grant is visible for this target; confirming revoke returns refreshed user detail with no-op evidence.", "consequenceSummary", mapOf("consequenceCopy", "Revoking support access removes the time-boxed support grant only; it does not change ordinary membership, roles, invitations, identity provider state, access-review recommendations, or tenant/customer data.", "affectedAccessCopy", "Only the selected target's support grant changes after backend reauthorization and idempotency checks.", "recoveryGuidance", List.of("Use Back to user detail to verify current support-access state.", "Use Grant support access if a new time-boxed grant is required later."), "noRoleMutation", true, "noMembershipLifecycleMutation", true, "noInvitationMutation", true, "noIdentityProviderMutation", true, "noDirectAccessReviewMutation", true, "noFakeSuccess", true, "traceRefs", traceRefs), "consequenceCopy", "Revoking support access removes the time-boxed support grant only; it does not change ordinary membership, roles, invitations, identity provider state, access-review recommendations, or tenant/customer data.", "eligibility", mapOf("canSubmit", active, "alreadyRevokedNoOp", !active, "approvalRequired", false, "reasonRequired", true, "confirmationRequired", true, "policySummary", "Backend reauthorizes selected AuthContext, target visibility, active grant state, reason, idempotency, separation-of-duties, and support-access capability before mutation.", "traceRefs", traceRefs), "confirmationForm", mapOf("reasonDraft", "", "reasonRequired", true, "confirmationRequired", true, "confirmationTextPrompt", "Confirm this visible support-access grant should be revoked.", "submitLabel", "Revoke support access", "submitActionId", "action-confirm-user-admin-support-access-revoke", "cancelActionId", "action-display-user-detail", "idempotencyHint", "client-generated"), "actionContext", mapOf("accountId", target.accountId(), "membershipId", target.membershipId(), "activeSupportGrantVersion", target.membershipId() + ":" + (target.expiresAt() == null ? "none" : target.expiresAt().toString())), "reasonRequired", true, "confirmationRequired", true, "idempotencyKeyHint", "client-generated", "systemStates", List.of("ready", "validation-error", "stale", "conflict", "already-revoked-no-op", "approval-required", "policy-blocked", "hidden-not-found"), "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(), "traceRefs", traceRefs, "correlationId", correlationId, "redaction", List.of("support-provider-internals-redacted", "raw-jwt-redacted", "private-profile-redacted", "hidden-grant-redacted", "sibling-scope-redacted"), "noFakeSuccess", true, "noDirectMutation", true, "boundaryNotice", "The browser support-access revoke confirmation never exposes raw JWTs, provider payloads, private profile settings, hidden grants, or sibling-scope facts."),
        actions);
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
    var customerId = stringInput(input, "customerId", "");
    var targetScope = actor.selectedContext().scopeType() == ScopeType.TENANT && !customerId.isBlank() ? ScopeType.CUSTOMER : actor.selectedContext().scopeType();
    var targetCustomerId = targetScope == ScopeType.CUSTOMER ? customerId : actor.selectedContext().customerId();
    return userDirectoryView.list(actor, targetScope, actor.selectedContext().tenantId(), targetCustomerId).stream()
        .filter(user -> Objects.equals(user.accountId(), accountId) || Objects.equals(user.membershipId(), membershipId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "user-not-found-or-forbidden"));
  }

  private InvitationView.InvitationRow invitationForTaskSurface(AuthContextResolver.ResolvedMe actor, Object input) {
    var invitationId = stringInput(input, "invitationId", latestInvitationId(actor));
    var customerId = stringInput(input, "customerId", "");
    var targetScope = actor.selectedContext().scopeType() == ScopeType.TENANT && !customerId.isBlank() ? ScopeType.CUSTOMER : actor.selectedContext().scopeType();
    var targetCustomerId = targetScope == ScopeType.CUSTOMER ? customerId : actor.selectedContext().customerId();
    return invitationView.list(actor, targetScope, actor.selectedContext().tenantId(), targetCustomerId).stream()
        .filter(row -> invitationId.equals(row.invitationId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
  }

  private CapabilityActionResult openUserAdminTaskSurface(AuthContextResolver.ResolvedMe actor, String actionId, Object input, String correlationId) {
    try {
      var surface = switch (actionId) {
        case "action-open-useradmin-invitation-create" -> invitationCreateSurface(actor, input, correlationId);
        case "action-open-useradmin-invitation-resend-confirmation" -> invitationResendConfirmationSurface(actor, input, correlationId);
        case "action-open-useradmin-invitation-revoke-confirmation", "action-open-user-admin-invitation-revoke-confirmation" -> invitationRevokeConfirmationSurface(actor, input, correlationId);
        case "action-open-useradmin-membership-status-confirmation", "action-open-user-admin-membership-status-confirmation" -> membershipStatusConfirmationSurface(actor, input, correlationId);
        case "action-open-useradmin-support-access-grant", "action-open-user-admin-support-access-grant" -> supportAccessGrantSurface(actor, input, correlationId);
        case "action-open-useradmin-support-access-revoke-confirmation", "action-open-user-admin-support-access-revoke-confirmation" -> supportAccessRevokeConfirmationSurface(actor, input, correlationId);
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
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDashboardAction(), auditTraceSearchAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceSummaryTaskBlockedAction()));
  }

  private SurfaceEnvelope auditTraceSearchSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.search(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceSearchAction(), auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceDashboardAction()));
  }

  private SurfaceEnvelope auditTraceDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.detail(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSearchAction(), auditTraceDashboardAction()));
  }

  private SurfaceEnvelope auditTraceCorrelationTimelineSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.timeline(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDetailAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSearchAction(), auditTraceDashboardAction()));
  }

  private SurfaceEnvelope auditTraceFailureEvidenceSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.failureEvidence(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSearchAction(), auditTraceDashboardAction()));
  }

  private SurfaceEnvelope auditTraceInvestigationGuideSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var surface = auditTraceService.investigationGuide(actor, input, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSearchAction(), auditTraceDashboardAction(), auditTraceSummaryTaskBlockedAction()));
  }

  private SurfaceEnvelope auditTraceExportRequestSurface(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    var surface = auditTraceService.requestRedactedExport(actor, input, idempotencyKey, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceExportRequestAction(), auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSearchAction(), auditTraceDashboardAction()));
  }

  private SurfaceEnvelope auditTraceInvestigationNoteSurface(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    var surface = auditTraceService.appendInvestigationNote(actor, input, idempotencyKey, correlationId);
    return auditTraceEnvelope(actor, correlationId, surface, List.of(auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceSearchAction(), auditTraceDashboardAction()));
  }

  private SurfaceEnvelope auditTraceSummaryTaskBlockedSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "audit summary worker readiness blocked_provider_or_runtime", correlationId);
    return envelope("surface-audit-trace-summary-progress", "workflow-status", "Audit summary worker progress", actor, correlationId,
        mapOf("surfaceContract", "audit.trace.summaryProgress.v1", "surfaceId", "surface-audit-trace-summary-progress", "workflowId", "audit-trace-summary-task", "status", "blocked_provider_or_runtime", "readiness", "blocked-provider-or-runtime", "summary", "Manual audit summary task start is surfaced through backend-governed progress/review contracts; direct refresh fails closed until a backend-retained task is selected.", "summaryTask", mapOf("status", "draft", "expectedResultSurfaceId", "surface-audit-trace-summary-review"), "providerRuntime", mapOf("readiness", "runtime_blocked", "failClosed", "Configure the AuditTraceSummaryAutonomousAgent runtime, model provider, ToolPermissionBoundary grants, readSkill/readReferenceDoc, and auditTraceSummaryEvidence.read before model-backed summaries can complete."), "traceIds", List.of("trace-audit-summary-worker-blocked"), "traceRefs", List.of("trace-audit-summary-worker-blocked"), "requiredCapabilityId", AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "providerFailures", List.of("blocked_provider_or_runtime"), "blockers", List.of(mapOf("code", "blocked_provider_or_runtime", "message", "Start the summary through the backend governed action with an idempotency key; missing provider/runtime/tool-boundary configuration fails closed with no fake success.", "retryEligible", true, "traceRefs", List.of("trace-audit-summary-worker-blocked"))), "redactionSummary", "Raw JWTs, provider credentials, hidden prompts, raw model output, raw tool payloads, invitation tokens, raw task ids, and cross-tenant evidence are omitted.", "allowedActions", auditTraceSummaryAllowedActions("blocked_provider_or_runtime"), "disabledActions", List.of(mapOf("actionId", "action-audit-trace-summary-review", "reason", "review_not_ready", "recovery", "Start and complete a real model-backed summary before review is available.")), "noDirectMutation", true, "noFakeSuccess", true, "safety", "Audit summary output cannot mutate traces, policy, users, provider configuration, authorization, redaction, or tenant filtering; no deterministic or model-less successful worker result is exposed."),
        auditTraceSummaryProgressActions(false));
  }

  private SurfaceEnvelope auditTraceSummaryProgressSurface(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask task, String correlationId) {
    var status = auditTraceSummaryStatus(task);
    var blocked = task.status() == AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME || task.status() == AuditTraceSummaryTask.Status.FAILED;
    var readyForReview = task.status() == AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED;
    var traceIds = task.traceIds().isEmpty() ? List.of("trace-audit-summary-task-" + stableSuffix(task.taskId() + ":" + correlationId)) : task.traceIds();
    var blockers = task.blockerCode() == null ? List.of() : List.of(mapOf("code", task.blockerCode(), "message", task.summary(), "retryEligible", task.status() == AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, "relatedActionId", "action-audit-trace-failure-evidence", "traceRefs", traceIds));
    var traceLinks = traceIds.stream().map(traceId -> mapOf("traceId", traceId, "category", blocked ? "model-runtime-blocker" : "agent-work", "label", blocked ? "Audit summary provider/runtime blocker" : "Audit summary worker trace", "targetSurfaceId", "surface-audit-trace-detail", "correlationId", correlationId, "redaction", "raw prompts, model output, provider payloads, tool payloads, JWTs, hidden evidence, and secrets are omitted")).toList();
    return envelope("surface-audit-trace-summary-progress", "workflow-status", "Audit summary worker progress", actor, correlationId,
        mapOf(
            "surfaceContract", "audit.trace.summaryProgress.v1",
            "surfaceId", "surface-audit-trace-summary-progress",
            "workflowId", task.taskId(),
            "summaryTaskId", task.taskId(),
            "autonomousAgentTaskId", task.autonomousAgentTaskId(),
            "status", status,
            "readiness", blocked ? "blocked-provider-or-runtime" : readyForReview ? "ready-for-review" : "running",
            "summary", task.summary(),
            "summaryTask", mapOf("taskId", task.taskId(), "safeTaskHandle", task.taskId(), "status", status, "submittedByAccountId", task.startedByAccountId(), "submittedByMembershipId", task.startedByMembershipId(), "createdAt", task.createdAt().toString(), "updatedAt", task.updatedAt().toString(), "expectedResultSurfaceId", "surface-audit-trace-summary-review", "idempotencyReplaySafe", true),
            "sourceScope", mapOf("tenantId", task.tenantId(), "customerId", task.customerId(), "scopeKind", task.customerId() == null ? "tenant" : "customer", "evidenceCategories", task.evidenceCategories(), "windowStart", task.windowStart().toString(), "windowEnd", task.windowEnd().toString(), "retainedEvidence", "redacted browser-safe evidence refs only"),
            "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities(), "requiredCapabilityIds", List.of(AuditTraceSummaryService.START_CAPABILITY, AuditTraceSummaryService.READ_CAPABILITY), "redaction", "missing capabilities, hidden policy internals, raw provider/model/tool payloads, prompts, and cross-scope evidence are not exposed"),
            "workerProgress", mapOf("stage", status, "percent", task.progressPercent(), "currentStep", task.summary(), "lastSafeEventTime", task.updatedAt().toString(), "retryEligible", blocked, "expectedNextUserAction", readyForReview ? "Open the summary review surface" : blocked ? "Inspect blocker evidence or retry after provider/runtime configuration" : "Refresh summary task progress"),
            "providerRuntime", mapOf("readiness", blocked ? "runtime_blocked" : "configured_or_running", "failClosed", blocked ? "Provider/runtime/tool-boundary configuration failed closed; no deterministic or model-less successful worker result is exposed." : "Backend task state is authoritative; review opens only after a real model-backed summary is retained."),
            "blockers", blockers,
            "evidenceSummary", mapOf("evidenceRefs", task.evidenceRefs(), "findingRefs", task.findingRefs(), "traceRefs", traceIds, "traceLinks", traceLinks, "redaction", "Evidence refs are safe labels only; raw evidence bodies, prompts, model output, provider/tool payloads, hidden tenant/customer facts, and secrets are omitted."),
            "traceIds", traceIds,
            "traceRefs", traceIds,
            "traceLinks", traceLinks,
            "providerFailures", blocked ? List.of(firstNonBlank(task.blockerCode(), "blocked_provider_or_runtime")) : List.of(),
            "allowedActions", auditTraceSummaryAllowedActions(status),
            "disabledActions", readyForReview ? List.of() : List.of(mapOf("actionId", "action-audit-trace-summary-review", "reason", blocked ? "provider_runtime_blocked" : "waiting_for_worker", "recovery", blocked ? "Resolve provider/runtime/tool-boundary blocker and retry through the backend start action." : "Refresh progress until the retained model-backed task completes.")),
            "recovery", blocked ? "Resolve provider/runtime/tool-boundary configuration, then retry through the governed backend start action with a fresh idempotency key." : readyForReview ? "Open the review surface to inspect retained advisory findings before any accept/reject decision." : "Refresh the retained task; the browser must not infer progress from timers or cached state.",
            "redactionSummary", "Raw JWTs, provider credentials, hidden prompts, raw model output, raw tool payloads, raw task ids, invitation tokens, and cross-tenant evidence are omitted.",
            "noDirectMutation", true,
            "noFakeSuccess", blocked),
        auditTraceSummaryProgressActions(true));
  }

  private SurfaceEnvelope auditTraceSummaryReviewNotReadySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), AUDIT_TRACE_SUMMARY_TASK_REVIEW_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, AUDIT_TRACE_SUMMARY_TASK_REVIEW_CAPABILITY, "audit.trace.summaryReview.v1 review_not_ready direct refresh", correlationId);
    var traceIds = List.of("trace-audit-summary-review-not-ready-" + stableSuffix(correlationId));
    return envelope("surface-audit-trace-summary-review", "decision-card", "Audit summary review", actor, correlationId,
        mapOf(
            "surfaceContract", "audit.trace.summaryReview.v1",
            "surfaceId", "surface-audit-trace-summary-review",
            "status", "review_not_ready",
            "readiness", "review-not-ready",
            "summaryTask", mapOf("status", "review_not_ready", "expectedSourceSurfaceId", "surface-audit-trace-summary-progress", "safeTaskHandle", "not-selected"),
            "reviewState", mapOf("decision", "not_ready", "immutableAdvisoryOnly", true, "furtherReviewChangesAllowed", false),
            "advisorySummary", mapOf("status", "not_ready", "message", "Open review from a retained completed model-backed Audit/Trace summary task; no cached, fixture, provider-bypassed, or model-less summary is accepted."),
            "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "requiredCapabilityIds", List.of(AuditTraceSummaryService.REVIEW_CAPABILITY), "redaction", "Missing capabilities, hidden task ids, raw prompts, provider/model/tool payloads, hidden evidence counts, and cross-scope details are omitted."),
            "qualityNotes", mapOf("modelBackedRequired", true, "providerBypassAllowed", false, "noFakeSuccess", true),
            "availableActions", auditTraceSummaryReviewActions(false).stream().map(this::surfaceActionSummary).toList(),
            "disabledActions", List.of(mapOf("actionId", "action-audit-trace-summary-accept", "reason", "review_not_ready"), mapOf("actionId", "action-audit-trace-summary-reject", "reason", "review_not_ready")),
            "recovery", "Start or refresh the governed summary worker, then open review only after a real model-backed summary is retained.",
            "traceIds", traceIds,
            "traceRefs", traceIds,
            "redactionSummary", "Raw JWTs, provider credentials, hidden prompts, raw model output, raw tool payloads, raw task ids, invitation tokens, and cross-tenant evidence are omitted.",
            "noDirectMutation", true,
            "noFakeSuccess", true),
        auditTraceSummaryReviewActions(false));
  }

  private SurfaceEnvelope auditTraceSummaryReviewSurface(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask task, String correlationId) {
    var status = auditTraceSummaryReviewStatus(task);
    var reviewable = task.status() == AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED;
    var decided = task.status() == AuditTraceSummaryTask.Status.ACCEPTED || task.status() == AuditTraceSummaryTask.Status.REJECTED;
    var ready = reviewable || decided;
    var traceIds = task.traceIds().isEmpty() ? List.of("trace-audit-summary-review-" + stableSuffix(task.taskId() + ":" + correlationId)) : task.traceIds();
    var traceLinks = traceIds.stream().map(traceId -> mapOf("traceId", traceId, "category", ready ? "model-backed-summary-review" : "summary-review-not-ready", "label", ready ? "Audit summary review trace" : "Audit summary unavailable for review", "targetSurfaceId", "surface-audit-trace-detail", "correlationId", correlationId, "redaction", "raw prompts, raw model output, provider payloads, tool payloads, hidden evidence, and secrets are omitted")).toList();
    var reviewActions = auditTraceSummaryReviewActions(reviewable);
    return envelope("surface-audit-trace-summary-review", "decision-card", "Audit summary review", actor, correlationId,
        mapOf(
            "surfaceContract", "audit.trace.summaryReview.v1",
            "surfaceId", "surface-audit-trace-summary-review",
            "summaryTaskId", task.taskId(),
            "autonomousAgentTaskId", task.autonomousAgentTaskId(),
            "status", status,
            "readiness", ready ? status : "review-not-ready",
            "summaryTask", mapOf("taskId", task.taskId(), "safeTaskHandle", task.taskId(), "status", status, "submittedByAccountId", task.startedByAccountId(), "submittedByMembershipId", task.startedByMembershipId(), "createdAt", task.createdAt().toString(), "updatedAt", task.updatedAt().toString(), "sourceSurfaceId", "surface-audit-trace-summary-progress", "reviewSurfaceId", "surface-audit-trace-summary-review", "idempotencyReplaySafe", true),
            "reviewState", mapOf("decision", task.decision() == null ? (reviewable ? "unreviewed" : "not_ready") : task.decision(), "reviewerLabel", task.decision() == null ? null : "Authorized Audit/Trace reviewer", "reviewedAt", decided ? task.updatedAt().toString() : null, "safeDecisionReason", task.decisionReason(), "immutableAdvisoryOnly", true, "furtherReviewChangesAllowed", reviewable),
            "advisorySummary", mapOf("narrative", ready ? task.summary() : "Review is not available until the governed model-backed summary task completes.", "findingRefs", ready ? task.findingRefs() : List.of(), "riskConfidence", ready ? "advisory model-backed redacted findings require human judgment" : "not_ready", "noRawModelOutput", true, "noPromptOrProviderPayload", true),
            "sourceScope", mapOf("tenantId", task.tenantId(), "customerId", task.customerId(), "scopeKind", task.customerId() == null ? "tenant" : "customer", "evidenceCategories", task.evidenceCategories(), "windowStart", task.windowStart().toString(), "windowEnd", task.windowEnd().toString(), "visibleSourceCount", task.evidenceRefs().size(), "retainedEvidence", "redacted browser-safe evidence refs only"),
            "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", actor.selectedContext().capabilities(), "requiredCapabilityIds", List.of(AuditTraceSummaryService.REVIEW_CAPABILITY, AuditTraceSummaryService.ACCEPT_CAPABILITY, AuditTraceSummaryService.REJECT_CAPABILITY), "redaction", "Missing capabilities, hidden policy internals, raw provider/model/tool payloads, prompts, hidden task ids, and cross-scope evidence are not exposed"),
            "evidenceSummary", mapOf("evidenceRefs", task.evidenceRefs(), "findingRefs", task.findingRefs(), "traceRefs", traceIds, "traceLinks", traceLinks, "redaction", "Evidence refs are safe labels only; source links must be reopened through Audit/Trace authorization."),
            "omissions", mapOf("omittedFieldKeys", List.of("rawJwt", "rawPrompt", "rawModelOutput", "providerCredentials", "toolPayload", "hiddenEvidenceIds", "crossScopeEvidence"), "hiddenCountPolicy", "do-not-enumerate-hidden-evidence"),
            "qualityNotes", mapOf("modelBackedRequired", true, "providerBypassAllowed", false, "toolBoundaryOutcome", task.blockerCode() == null ? "passed_or_not_applicable" : task.blockerCode(), "partialData", false, "noFakeSuccess", !ready),
            "decisionForm", mapOf("allowedDecisionValues", List.of("accepted", "rejected"), "acceptActionId", "action-audit-trace-summary-accept", "rejectActionId", "action-audit-trace-summary-reject", "rejectionReasonRequired", true, "idempotencyKeySource", "client-generated", "advisoryOnly", true, "noSourceMutation", true),
            "availableActions", reviewActions.stream().map(this::surfaceActionSummary).toList(),
            "disabledActions", reviewable ? List.of() : List.of(mapOf("actionId", "action-audit-trace-summary-accept", "reason", decided ? "already_reviewed" : "review_not_ready"), mapOf("actionId", "action-audit-trace-summary-reject", "reason", decided ? "already_reviewed" : "review_not_ready")),
            "recovery", reviewable ? "Review the retained advisory summary, then accept or reject it as evidence only." : decided ? "Review decision evidence is retained; source traces and policy remain unchanged." : "Refresh progress until the retained model-backed task completes.",
            "traceIds", traceIds,
            "traceRefs", traceIds,
            "traceLinks", traceLinks,
            "redactionSummary", "Raw JWTs, provider credentials, hidden prompts, raw model output beyond the retained redacted advisory summary, raw tool payloads, raw task ids, invitation tokens, and cross-tenant evidence are omitted.",
            "noDirectMutation", true,
            "noFakeSuccess", !ready),
        reviewActions);
  }

  private String auditTraceSummaryReviewStatus(AuditTraceSummaryTask task) {
    return switch (task.status()) {
      case COMPLETED_REVIEW_REQUIRED -> "ready_for_review";
      case ACCEPTED -> "accepted";
      case REJECTED -> "rejected";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "blocked_provider_or_runtime";
      case FAILED -> "failed";
      case CANCELLED -> "not_found_or_redacted";
      default -> "review_not_ready";
    };
  }

  private List<Map<String, Object>> auditTraceSummaryAllowedActions(String status) {
    var actions = new ArrayList<Map<String, Object>>();
    actions.add(mapOf("actionId", "action-audit-trace-summary-task-start", "label", "Start or retry summary task", "browserToolId", "action-audit-trace-summary-task-start", "governedToolId", "audit.trace.summary_task.start", "capabilityId", AuditTraceSummaryService.START_CAPABILITY, "resultSurfaceId", "surface-audit-trace-summary-progress", "reason", "Backend validates scope, idempotency, provider/runtime readiness, and tool boundaries before starting or failing closed."));
    actions.add(mapOf("actionId", "action-audit-trace-summary-task-read", "label", "Refresh summary task", "browserToolId", "action-audit-trace-summary-task-read", "governedToolId", AuditTraceSummaryService.READ_CAPABILITY, "capabilityId", AuditTraceSummaryService.READ_CAPABILITY, "resultSurfaceId", "surface-audit-trace-summary-progress", "reason", "Reads retained backend task state and recomputes selected-context authorization."));
    actions.add(mapOf("actionId", "action-audit-trace-failure-evidence", "label", "Inspect blocker evidence", "browserToolId", "action-audit-trace-failure-evidence", "governedToolId", AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY, "capabilityId", AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-failure-evidence", "reason", "Reauthorizes fail-closed evidence without exposing provider secrets or prompt/tool payloads."));
    if ("completed_review_required".equals(status)) actions.add(mapOf("actionId", "action-audit-trace-summary-review", "label", "Open summary review", "browserToolId", "action-audit-trace-summary-review", "governedToolId", AuditTraceSummaryService.REVIEW_CAPABILITY, "capabilityId", AuditTraceSummaryService.REVIEW_CAPABILITY, "resultSurfaceId", "surface-audit-trace-summary-review", "reason", "Available only after a real model-backed summary is retained."));
    return actions;
  }

  private List<SurfaceAction> auditTraceSummaryProgressActions(boolean includeRead) {
    var actions = new ArrayList<SurfaceAction>();
    actions.add(auditTraceSummaryTaskBlockedAction());
    if (includeRead) actions.add(auditTraceSummaryTaskReadAction());
    actions.add(auditTraceSummaryReviewAction());
    actions.add(auditTraceFailureEvidenceAction());
    actions.add(auditTraceDetailAction());
    actions.add(auditTraceTimelineAction());
    actions.add(auditTraceInvestigationGuideAction());
    actions.add(auditTraceExportRequestAction());
    actions.add(auditTraceAppendInvestigationNoteAction());
    actions.add(auditTraceSearchAction());
    actions.add(auditTraceDashboardAction());
    return List.copyOf(actions);
  }

  private List<SurfaceAction> auditTraceSummaryReviewActions(boolean includeDecisionActions) {
    var actions = new ArrayList<SurfaceAction>();
    actions.add(auditTraceSummaryTaskReadAction());
    if (includeDecisionActions) {
      actions.add(auditTraceSummaryAcceptAction());
      actions.add(auditTraceSummaryRejectAction());
    }
    actions.add(auditTraceDetailAction());
    actions.add(auditTraceTimelineAction());
    actions.add(auditTraceFailureEvidenceAction());
    actions.add(auditTraceInvestigationGuideAction());
    actions.add(auditTraceExportRequestAction());
    actions.add(auditTraceAppendInvestigationNoteAction());
    actions.add(auditTraceSearchAction());
    actions.add(auditTraceDashboardAction());
    return List.copyOf(actions);
  }

  private String auditTraceSummaryStatus(AuditTraceSummaryTask task) {
    return task.status().name().toLowerCase(Locale.ROOT);
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
    return governancePolicyInventorySurface(actor, Map.of(), correlationId);
  }

  private SurfaceEnvelope governancePolicyInventorySurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var actions = new java.util.ArrayList<SurfaceAction>();
    actions.add(governanceListPoliciesAction());
    actions.add(governanceReadPolicyAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_PROPOSE_CAPABILITY)) actions.add(governanceDraftProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_SIMULATE_CAPABILITY)) actions.add(governanceSimulateProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_APPROVE_CAPABILITY)) actions.add(governanceDecideProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY)) actions.add(governanceStartImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_READ_CAPABILITY)) actions.add(governanceReadImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_OUTCOMES_RECORD_CAPABILITY)) actions.add(governanceOutcomeNoteAction());
    if (actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) actions.add(openAuditAction());
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.inventory(actor, input, correlationId), List.copyOf(actions));
  }

  private Map<String, Object> governancePolicyRow(String id, String name, String status, String source, List<String> capabilityIds, String traceId) {
    return mapOf("policyId", id, "name", name, "type", "governance", "status", status, "affectedCapabilityIds", capabilityIds, "sourceArtifact", source, "lastChangeTraceId", traceId);
  }

  private SurfaceEnvelope governancePolicyDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.detail(actor, input, correlationId), List.of(governanceListPoliciesAction(), governanceDraftProposalAction(), governanceSimulateProposalAction(), openAuditAction()));
  }

  private List<SurfaceAction> governanceProposalActions(AuthContextResolver.ResolvedMe actor) {
    var actions = new java.util.ArrayList<SurfaceAction>();
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_PROPOSE_CAPABILITY)) {
      actions.add(governanceDraftProposalAction());
      actions.add(governanceSubmitProposalAction());
    }
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_SIMULATE_CAPABILITY)) actions.add(governanceSimulateProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_APPROVE_CAPABILITY)) actions.add(governanceDecideProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY)) actions.add(governanceStartImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_READ_CAPABILITY)) actions.add(governanceReadImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_OUTCOMES_RECORD_CAPABILITY)) actions.add(governanceOutcomeNoteAction());
    if (actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) actions.add(openAuditAction());
    return List.copyOf(actions);
  }

  private SurfaceEnvelope governancePolicyProposalSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String state, String summary) {
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.readProposal(actor, mapOf("rationale", summary, "title", "Governance policy proposal"), correlationId), governanceProposalActions(actor));
  }

  private List<SurfaceAction> governanceSimulationActions(AuthContextResolver.ResolvedMe actor) {
    var actions = new java.util.ArrayList<SurfaceAction>();
    actions.add(governanceReadPolicyAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_SIMULATE_CAPABILITY)) actions.add(governanceSimulateProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_APPROVE_CAPABILITY)) actions.add(governanceDecideProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY)) actions.add(governanceStartImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_READ_CAPABILITY)) actions.add(governanceReadImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_OUTCOMES_RECORD_CAPABILITY)) actions.add(governanceOutcomeNoteAction());
    if (actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) actions.add(openAuditAction());
    return List.copyOf(actions);
  }

  private SurfaceEnvelope governancePolicySimulationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateGovernancePolicyInputScope(actor, input, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, GOVERNANCE_POLICY_READ_CAPABILITY, "proposal simulation evidence read", correlationId);
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.readSimulation(actor, input, correlationId), governanceSimulationActions(actor));
  }

  private List<SurfaceAction> governanceDecisionActions(AuthContextResolver.ResolvedMe actor) {
    var actions = new java.util.ArrayList<SurfaceAction>();
    actions.add(governanceReadPolicyAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_APPROVE_CAPABILITY)) actions.add(governanceDecideProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ACTIVATE_CAPABILITY)) actions.add(governanceActivateProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ROLLBACK_CAPABILITY)) actions.add(governanceRollbackPolicyAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_SIMULATE_CAPABILITY)) actions.add(governanceSimulateProposalAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_START_CAPABILITY)) actions.add(governanceStartImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_POLICY_ANALYSIS_READ_CAPABILITY)) actions.add(governanceReadImpactAnalysisAction());
    if (actor.selectedContext().capabilities().contains(GOVERNANCE_OUTCOMES_RECORD_CAPABILITY)) actions.add(governanceOutcomeNoteAction());
    if (actor.selectedContext().capabilities().contains(AUDIT_TRACE_READ_CAPABILITY)) actions.add(openAuditAction());
    return List.copyOf(actions);
  }

  private SurfaceEnvelope governancePolicyDecisionSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateGovernancePolicyInputScope(actor, input, correlationId);
    return governancePolicyEnvelope(actor, correlationId, governancePolicyService.readDecision(actor, input, correlationId), governanceDecisionActions(actor));
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
    authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, "agent_admin.dashboard.v1", correlationId);
    seedStarterCoreAttention(actor, correlationId);
    var attention = attentionMaps(attentionService.listWorkstreamItems(actor, AGENT_ADMIN_AGENT_ID, correlationId));
    var actions = List.of(
        agentAdminRefreshDashboardAction(), agentAdminOpenCatalogAction(), agentAdminOpenBehaviorProposalsAction(), agentAdminOpenPromptRiskReviewAction(), agentAdminOpenSeedMaterialAction(),
        agentAdminOpenManifestDriftAction(), agentAdminOpenToolBoundaryAction(), agentAdminOpenModelRefsAction(), agentAdminOpenTraceAction(),
        displayAgentAdminDashboardAction(), displayAgentCatalogAction(), proposePromptDiffAction(), submitBehaviorChangeAction(), simulateToolBoundaryAction(), listAgentSeedMaterialAction(), testPromptAction(), startPromptRiskReviewAction(), readPromptRiskReviewAction(), openAgentTraceAction());
    return envelope("surface-agent-admin-dashboard", "dashboard", "Agent Admin command center", actor, correlationId,
        mapOf("surfaceContract", "agent_admin.dashboard.v1",
            "surfaceSummary", mapOf("id", "surface-agent-admin-dashboard", "title", "Agent Admin command center", "type", "dashboard", "contract", "agent_admin.dashboard.v1", "owningWorkstream", "Agent Admin", "owningAgent", AGENT_ADMIN_AGENT_ID, "selectedScopeLabel", contextLabel(actor.selectedContext().tenantId(), actor.selectedContext().customerId()), "readinessState", "ready_with_attention", "lastRefreshedAt", Instant.now().toString()),
            "scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "organizationDisplayName", contextLabel(actor.selectedContext().tenantId(), null), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true, "customerIdentifiersOmittedUnlessAlreadyAuthorized", actor.selectedContext().customerId() == null),
            "cards", List.of(
                mapOf("cardId", "agent-admin-card-provider", "label", "Provider/model readiness", "value", "Fail closed", "status", "Open model references; provider secrets and raw errors stay redacted", "severity", "blocked_provider_or_runtime", "actionId", "action-agent-admin-open-model-refs", "targetSurfaceId", "surface-agent-model-refs"),
                mapOf("cardId", "agent-admin-card-approvals", "label", "Behavior approvals", "value", 1, "status", "Human review required before activation", "severity", "blocked", "actionId", "action-agent-admin-open-behavior-proposals", "targetSurfaceId", "surface-agent-behavior-proposal"),
                mapOf("cardId", "agent-admin-card-tool-boundary", "label", "Tool-boundary risks", "value", 1, "status", "Side-effecting grant denied until separate review", "severity", "urgent", "actionId", "action-agent-admin-open-tool-boundary", "targetSurfaceId", "surface-agent-tool-boundary-diff"),
                mapOf("cardId", "agent-admin-card-seed", "label", "Seed material", "value", 3, "status", "Starter defaults visible; tenant overrides preserved", "severity", "info", "actionId", "action-agent-admin-open-seed-material", "targetSurfaceId", "surface-agent-seed-material")),
            "attentionSections", List.of(
                actionEntry(agentAdminOpenModelRefsAction()), actionEntry(agentAdminOpenBehaviorProposalsAction()), actionEntry(agentAdminOpenPromptRiskReviewAction()), actionEntry(agentAdminOpenManifestDriftAction()), actionEntry(agentAdminOpenToolBoundaryAction()), actionEntry(agentAdminOpenSeedMaterialAction()), actionEntry(agentAdminOpenTraceAction())),
            "attentionQueues", List.of(
                mapOf("queueId", "provider-readiness", "label", "Provider/model readiness", "count", 1, "severity", "blocked_provider_or_runtime", "statusText", "Provider/runtime readiness fails closed", "sourceCapabilityId", AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, "targetSurfaceId", "surface-agent-model-refs", "actionId", "action-agent-admin-open-model-refs", "traceRefs", List.of("trace-agent-admin-model-refs"), "redaction", "provider secrets and raw provider errors redacted"),
                mapOf("queueId", "behavior-approval", "label", "Behavior proposals awaiting human decision", "count", 1, "severity", "blocked", "statusText", "Approval required", "sourceCapabilityId", AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "targetSurfaceId", "surface-agent-behavior-proposal", "actionId", "action-agent-admin-open-behavior-proposals", "traceRefs", List.of("trace-agent-admin-behavior-review"), "redaction", "raw prompt/skill bodies omitted"),
                mapOf("queueId", "tool-boundary-risk", "label", "Risky tool-boundary expansion attempts", "count", 1, "severity", "urgent", "statusText", "Simulation denied side effect", "sourceCapabilityId", AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, "targetSurfaceId", "surface-agent-tool-boundary-diff", "actionId", "action-agent-admin-open-tool-boundary", "traceRefs", List.of("trace-agent-admin-tool-denied-email-send"), "redaction", "tool inputs and outputs omitted"),
                mapOf("queueId", "prompt-risk-review", "label", "Prompt-risk autonomous review results", "count", 0, "severity", "warning", "statusText", "Open prompt-risk review status", "sourceCapabilityId", AgentAdminPromptRiskReviewService.READ_CAPABILITY, "targetSurfaceId", "surface-agent-admin-prompt-risk-review", "actionId", "action-agent-admin-open-prompt-risk-review", "traceRefs", List.of("trace-prompt-risk-model-call-001"), "redaction", "browser-safe finding summaries only")),
            "readinessSummary", mapOf("managedAgentCount", 5, "lifecycle", mapOf("active", 5, "draft", 0), "providerModelReadiness", "blocked_provider_or_runtime", "promptRiskReviewReadiness", "deferred_until_provider_runtime_configured", "noFakeSuccess", true),
            "approvalQueueCounts", mapOf("proposal", 1, "highRiskPromptToolModelChange", 1, "activation", 0, "rollback", 0, "deferredReview", 1),
            "manifestDrift", List.of(mapOf("impactedAgent", "Agent Admin", "driftSeverity", "warning", "reviewTargetSurfaceId", "surface-agent-skill-manifest-diff", "summary", "Skill/reference manifests require governed review before activation.")),
            "loaderDenialState", List.of(mapOf("category", "tool_permission_boundary", "affectedAgent", "Agent Admin", "recoveryTargetSurfaceId", "surface-agent-tool-boundary-diff", "summary", "Side-effecting loader/tool expansion remains denied and traced.")),
            "authorityExpansionAttempts", List.of(mapOf("category", "tool_boundary", "approvalState", "approval-required", "policyReason", "High-risk side-effecting authority requires human review.", "proposalTargetSurfaceId", "surface-agent-behavior-proposal")),
            "seedImportReadiness", mapOf("status", "ready", "blockedReasons", List.of(), "preservesTenantOverrides", true, "targetSurfaceId", "surface-agent-seed-material"),
            "providerModelStatus", mapOf("category", "blocked_provider_or_runtime", "failClosedReason", "Provider/model readiness is surfaced without fabricated success until real configuration is available.", "targetSurfaceId", "surface-agent-model-refs", "secretsVisible", false),
            "safeRedactionSummary", mapOf("prompts", "redacted", "skills", "redacted", "references", "redacted", "providerData", "redacted", "hiddenTenantsCustomers", "omitted", "privilegedTraceDetails", "role-gated"),
            "authorizedActions", List.of(
                authorizedActionMap(agentAdminOpenCatalogAction()), authorizedActionMap(agentAdminOpenBehaviorProposalsAction()), authorizedActionMap(agentAdminOpenPromptRiskReviewAction()), authorizedActionMap(agentAdminOpenSeedMaterialAction()), authorizedActionMap(agentAdminOpenManifestDriftAction()), authorizedActionMap(agentAdminOpenToolBoundaryAction()), authorizedActionMap(agentAdminOpenModelRefsAction()), authorizedActionMap(agentAdminOpenTraceAction()), authorizedActionMap(agentAdminRefreshDashboardAction())),
            "attentionItems", attention,
            "recentActivity", List.of(mapOf("activityId", "activity-agent-admin-protected-read", "label", "Dashboard read protected by selected AuthContext", "summary", "Scoped Agent Admin projection returns browser-safe readiness, queue, redaction, and routing summaries.", "traceId", "trace-agent-admin-dashboard")),
            "hero", mapOf("title", "Govern managed agents safely", "scopeLabel", "Tenant Admin · selected AuthContext", "scopeType", actor.selectedContext().scopeType().name(), "adminLevel", "Agent steward", "redactionSummary", "Provider secrets, raw prompts, raw skills, raw references, hidden authority, hidden customers, and cross-tenant evidence are omitted."),
            "readiness", "ready_with_attention",
            "capabilityIds", List.of(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, AGENT_ADMIN_GET_MANIFEST_CAPABILITY, AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, AgentAdminPromptRiskReviewService.READ_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY),
            "redaction", mapOf("browserSafe", true, "omittedFieldKeys", List.of("rawPromptBody", "rawSkillBody", "rawReferenceBody", "providerCredentialValue", "rawProviderError", "rawJwt", "hiddenTenantId", "hiddenCustomerId"), "previewLimitChars", 220),
            "traceRefs", List.of("trace-agent-admin-dashboard", "trace-agent-admin-catalog", "trace-agent-admin-model-refs"),
            "systemStates", List.of("loading", "empty", "ready", "submitting", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "approval-required", "validation-error", "no-op", "failure")),
        actions);
  }

  private SurfaceEnvelope agentAdminCatalogSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return agentAdminCatalogSurface(actor, Map.of(), correlationId);
  }

  private SurfaceEnvelope agentAdminCatalogSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    seedStarterCoreAttention(actor, correlationId);
    var actions = List.of(displayAgentAdminDashboardAction(), displayAgentCatalogAction(), agentAdminRefreshCatalogAction(), agentAdminSearchCatalogAction(), agentAdminResetCatalogFiltersAction(), openAgentDetailAction(), agentAdminCatalogOpenTraceAction(), openAgentTraceAction());
    var data = withAttentionItems(agentAdminService.catalog(actor, inputMap(input), correlationId), actor, AGENT_ADMIN_AGENT_ID, correlationId);
    data.put("authorizedActions", actions.stream().map(this::authorizedActionMap).toList());
    return envelope("surface-agent-admin-catalog", "list-search", "Agent Admin catalog", actor, correlationId, data, actions);
  }

  private SurfaceEnvelope agentAdminDetailSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return agentAdminDetailSurface(actor, null, correlationId);
  }

  private SurfaceEnvelope agentAdminDetailSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var agentDefinitionId = stringInput(input, "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID);
    var actions = List.of(
        agentDetailRefreshAction(),
        agentDetailOpenPromptGovernanceAction(),
        agentDetailOpenSkillManifestAction(),
        agentDetailOpenToolBoundaryAction(),
        agentDetailOpenModelRefsAction(),
        agentDetailRunTestAction(),
        agentDetailOpenPromptRiskReviewAction(),
        agentDetailOpenActivationAction(),
        agentDetailOpenDeactivationAction(),
        agentDetailOpenRollbackAction(),
        agentDetailOpenTraceAction(),
        agentDetailBackToCatalogAction());
    var data = agentAdminService.definitionDetail(actor, agentDefinitionId, correlationId);
    data.put("authorizedActions", actions.stream().map(this::authorizedActionMap).toList());
    return envelope("surface-agent-admin-detail", "show-inspection", "Agent Admin readiness detail", actor, correlationId, data, actions);
  }

  private BehaviorChangeProposal proposeToolBoundaryChange(AuthContextResolver.ResolvedMe actor, String correlationId, String summary) {
    var unsafeGrant = new ToolPermissionBoundary.ToolGrant("email.send", ToolPermissionBoundary.Category.EXTERNAL_SIDE_EFFECT, "tenant.email.send", List.of("execute"), List.of("runtime"), "HIGH", "AUTONOMOUS", true, "full_work_trace");
    return agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY, null, List.of(unsafeGrant), summary, correlationId));
  }

  private BehaviorChangeProposal proposeModelRefChange(AuthContextResolver.ResolvedMe actor, String correlationId, String summary) {
    return agentRuntimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), BehaviorChangeProposal.TargetArtifact.MODEL_REF, "Redacted model-reference routing proposal. Provider credentials, raw provider responses, prompts, skills, references, and hidden scopes remain omitted; activation requires separate backend lifecycle confirmation.", List.of(), summary, correlationId));
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
    var actions = promptGovernanceActions();
    var data = governanceDiffData("agent_admin.prompt_governance.v1", "proposal-agent-admin-prompt-001", "draft", "medium",
        "Active prompt remains backend-authorized; raw prompt body is hidden and only a redacted preview is available.",
        "Draft prompt wording can be submitted for review, approved, or rejected through backend-governed behavior proposal actions; activation remains a separate lifecycle surface.",
        List.of(
            mapOf("path", "redactedPromptDiff.redactedPreview", "before", prompt.get("redactedPreview"), "after", "Proposed wording keeps authority, redaction, provider readiness, and trace requirements explicit.", "impact", "Browser-safe preview only; raw prompt text remains omitted."),
            mapOf("path", "reviewState.lifecycle", "before", prompt.get("status"), "after", "approval-required", "impact", "No prompt, model output, or frontend state can activate behavior directly."),
            mapOf("path", "impactSummary.providerRuntimeReadiness", "before", "not asserted", "after", "blocked_provider_or_runtime until real model/provider configuration is available", "impact", "Simulation and prompt-risk routes fail closed instead of fabricating provider success.")),
        List.of("trace-agent-admin-prompt-" + stableSuffix(correlationId), "trace-agent-admin-prompt-governance-" + stableSuffix(correlationId)));
    data.put("surfaceContractAliases", List.of("agent_admin.prompt_version.v1", "surface.agent_admin.prompt_versions.v1"));
    data.put("capabilityAliases", List.of("agent.prompts.govern", AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY));
    data.put("governanceSummary", mapOf(
        "surfaceId", "surface-agent-prompt-governance",
        "title", "Prompt governance review",
        "type", "governance-diff / show-inspection",
        "contract", "agent_admin.prompt_governance.v1",
        "managedAgentDisplayName", "Agent Admin Agent",
        "shortPurpose", "Review redacted prompt behavior changes without exposing raw prompt text.",
        "activePromptVersionLabel", prompt.get("recordLabel"),
        "proposedPromptVersionLabel", "proposal-agent-admin-prompt-001",
        "proposalState", "draft",
        "reviewState", "approval-required",
        "riskClass", "medium",
        "approvalRequirement", "Human Agent Admin review and backend policy approval are required before activation.",
        "providerModelReadinessCategory", "blocked_provider_or_runtime",
        "lastChangedOrReviewed", prompt.get("changeSummary"),
        "lastRefreshed", Instant.now().toString(),
        "noDirectActivation", true));
    data.put("scopeSummary", mapOf(
        "selectedContextId", actor.selectedContext().membershipId(),
        "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
        "tenantId", actor.selectedContext().tenantId(),
        "customerId", actor.selectedContext().customerId(),
        "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(),
        "governanceAuthorized", true,
        "visibilityDecision", "visible"));
    data.put("redactedPromptDiff", mapOf(
        "beforeSummary", "Current prompt preview is redacted and backend-owned.",
        "afterSummary", "Proposed changes remain summarized until review.",
        "redactedPreview", prompt.get("redactedPreview"),
        "intentBehaviorDeltas", List.of("Clarifies backend authorization, approval, trace, and provider-fail-closed requirements."),
        "systemInstructionCategoryChanges", List.of("governance", "authorization", "traceability"),
        "retrievalToolUseImplications", List.of("No additional governed loader tools are granted from this surface."),
        "policyRubricImpacts", List.of("Human review required", "No direct activation", "Raw prompt text omitted"),
        "omittedSectionCounts", mapOf("rawPromptBody", 1, "skillReferenceBodies", 0, "providerCredentials", 0),
        "redactionNotes", "Raw prompt body, full skill/reference bodies, provider secrets, raw loader inputs, and JWT/session data are omitted.",
        "conflictVersionDrift", false));
    data.put("impactSummary", mapOf(
        "userVisibleBehaviorImpact", "Prompt-behavior copy and governance guardrails may change only after behavior proposal review.",
        "authorityToolDataBoundaryChanges", "No authority, tool, or data-boundary expansion is granted by this surface.",
        "affectedWorkstreams", List.of("Agent Admin", "Governance/Policy", "Audit/Trace"),
        "downstreamBlockers", List.of("approval-required", "activation-separate-surface", "provider-readiness-fail-closed"),
        "rollbackAvailability", "Requires activated proposal metadata before rollback is available.",
        "providerRuntimeReadiness", "blocked_provider_or_runtime",
        "simulationOrRiskReviewRequired", true));
    data.put("riskAndEvidence", mapOf(
        "riskClass", "medium",
        "policyReadableReasons", List.of("Prompt text cannot grant capability or tenant scope.", "Review and activation are separate backend-authorized steps."),
        "confidenceQualityNotes", List.of("Redacted preview only", "Provider-backed prompt-risk findings require real provider/runtime configuration."),
        "promptRiskReviewStatus", "blocked_provider_or_runtime",
        "safeEvidenceRefs", List.of(prompt.get("checksum"), "PromptAssemblyTrace", "AgentWorkTrace"),
        "recentRelatedDenials", List.of("raw-prompt-browser-exposure-denied", "direct-activation-denied"),
        "requiredHumanReviewReasons", List.of("behavior-change", "prompt-governance", "authority-boundary")));
    data.put("reviewState", mapOf(
        "allowedDecisions", List.of("submit-review", "approve-after-review", "reject-with-reason"),
        "disabledDecisions", List.of("activate-directly", "edit-raw-prompt", "change-provider-config"),
        "idempotencyState", "client-generated keys required for submit/approve/reject decisions",
        "approvalRequired", true,
        "rejectionReasonRequired", true,
        "conflictState", "none",
        "nextRequiredSurface", "surface-agent-behavior-proposal",
        "noDirectMutation", true));
    data.put("authorizedActions", actions.stream().map(this::authorizedActionMap).toList());
    data.put("safeRedactionSummary", mapOf(
        "rawPromptText", "omitted",
        "skillReferenceBodies", "omitted",
        "providerCredentials", "omitted",
        "modelInternals", "omitted",
        "hiddenScopes", "omitted",
        "rawTraceEvidence", "role-gated",
        "loaderToolInputs", "omitted",
        "bearerTokens", "omitted",
        "internalStackTraces", "omitted"));
    data.put("diagnostics", mapOf(
        "managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "promptDocumentRef", prompt.get("recordId"),
        "proposalId", "proposal-agent-admin-prompt-001",
        "capabilityIds", List.of(AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY, AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, AGENT_ADMIN_REVIEW_CAPABILITY, AGENT_ADMIN_REJECT_CAPABILITY, AgentAdminPromptRiskReviewService.READ_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY),
        "redactedTraceIds", data.get("traceLinks"),
        "correlationId", correlationId,
        "redactionProfile", "browser-safe-prompt-governance"));
    data.put("states", List.of("loading", "ready", "empty-no-active-proposal", "submitting", "simulating", "approval-required", "review-submitted", "review-approved", "review-rejected", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "no-op", "failure"));
    data.put("noDirectActivation", true);
    return envelope("surface-agent-prompt-governance", "governance-diff", "Prompt governance review", actor, correlationId, data, actions);
  }

  private List<SurfaceAction> promptGovernanceActions() {
    return List.of(
        agentPromptGovernanceRefreshAction(),
        agentPromptGovernanceSimulateAction(),
        agentPromptGovernanceSubmitReviewAction(),
        agentPromptGovernanceApproveAction(),
        agentPromptGovernanceRejectAction(),
        agentPromptGovernanceOpenRiskReviewAction(),
        agentPromptGovernanceOpenTraceAction(),
        agentPromptGovernanceBackToDetailAction());
  }

  private List<SurfaceAction> skillManifestActions() {
    return List.of(
        agentSkillManifestRefreshAction(),
        agentSkillManifestSimulateAction(),
        agentSkillManifestSubmitReviewAction(),
        agentSkillManifestApproveAction(),
        agentSkillManifestRejectAction(),
        agentSkillManifestOpenToolBoundaryAction(),
        agentSkillManifestOpenModelRefsAction(),
        agentSkillManifestOpenTraceAction(),
        agentSkillManifestBackToDetailAction());
  }

  private List<SurfaceAction> toolBoundaryActions() {
    return List.of(
        agentToolBoundaryRefreshAction(),
        agentToolBoundarySimulateAction(),
        agentToolBoundarySubmitReviewAction(),
        agentToolBoundaryApproveAction(),
        agentToolBoundaryRejectAction(),
        agentToolBoundaryOpenModelRefsAction(),
        agentToolBoundaryOpenTraceAction(),
        agentToolBoundaryBackToDetailAction());
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
    var actions = skillManifestActions();
    var data = governanceDiffData("agent_admin.skill_manifest_diff.v1", "proposal-agent-admin-manifest-001", "in_review", "high",
        "Current compact skill/reference manifests allow assigned readSkill/readReferenceDoc loads only; raw skill and reference bodies are omitted from the browser.",
        "Proposed manifest/reference changes require human review, preserve governed-loader denials, and cannot activate behavior without a separate lifecycle decision.",
        List.of(
            mapOf("path", "redactedManifestDiff.skillManifest", "before", manifest.get("skillManifest"), "after", "reviewed compact skill manifest", "impact", "Full skill bodies remain governed and browser-hidden."),
            mapOf("path", "redactedManifestDiff.referenceManifest", "before", manifest.get("referenceManifest"), "after", "reviewed compact reference manifest", "impact", "Reference evidence access broadening requires review and trace evidence."),
            mapOf("path", "reviewState.lifecycle", "before", "manifest/reference drift", "after", "approval-required", "impact", "No manifest, reference bundle, model output, or frontend state can activate behavior directly."),
            mapOf("path", "impactSummary.providerRuntimeReadiness", "before", "not asserted", "after", "blocked_provider_or_runtime until real model/provider configuration is available", "impact", "Simulation routes fail closed instead of fabricating provider success.")),
        List.of("trace-agent-admin-manifest-" + stableSuffix(correlationId), "trace-agent-admin-manifest-governance-" + stableSuffix(correlationId)));
    data.put("surfaceContractAliases", List.of("agent_admin.manifest.v1", "surface.agent_admin.manifest_detail.v1"));
    data.put("capabilityAliases", List.of("agent.manifests.manage", "agent.skills.govern", AGENT_ADMIN_GET_MANIFEST_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY));
    data.put("manifestDiffSummary", mapOf(
        "surfaceId", "surface-agent-skill-manifest-diff",
        "title", "Skill manifest and reference diff review",
        "type", "governance-diff / show-inspection",
        "contract", "agent_admin.skill_manifest_diff.v1",
        "managedAgentDisplayName", "Agent Admin Agent",
        "shortPurpose", "Review compact skill/reference manifest changes without exposing raw bodies or hidden scope details.",
        "activeManifestVersionLabel", manifest.get("recordId"),
        "proposedManifestVersionLabel", "proposal-agent-admin-manifest-001",
        "proposalOrDriftState", "in_review",
        "reviewState", "approval-required",
        "riskClass", "high",
        "approvalRequirement", "Human Agent Admin review and backend policy approval are required before activation.",
        "providerRuntimeReadinessCategory", "blocked_provider_or_runtime",
        "lastChangedOrReviewed", "seed manifest/checksum evidence loaded from backend repository",
        "lastRefreshed", Instant.now().toString(),
        "noDirectActivation", true));
    data.put("scopeSummary", mapOf(
        "selectedContextId", actor.selectedContext().membershipId(),
        "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
        "tenantId", actor.selectedContext().tenantId(),
        "customerId", actor.selectedContext().customerId(),
        "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(),
        "governanceAuthorized", true,
        "visibilityDecision", "visible"));
    data.put("redactedManifestDiff", mapOf(
        "beforeSummary", "Current compact skill and reference manifests are backend-owned and redacted.",
        "afterSummary", "Proposed changes remain summarized until behavior proposal review.",
        "skillManifest", manifest.get("skillManifest"),
        "referenceManifest", manifest.get("referenceManifest"),
        "skillChanges", List.of("compact assigned-skill entries only", "raw skill implementations omitted", "unassigned skill loads remain denied"),
        "referenceBundleCategoryChanges", List.of("compact reference entries only", "full reference documents omitted", "evidence source broadening requires review"),
        "loaderEvidenceSourceCategories", List.of("SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"),
        "promptAssemblyImplications", List.of("Manifest changes affect prompt assembly only after separate approval and activation."),
        "omittedSectionCounts", mapOf("rawSkillBodies", 1, "rawReferenceBodies", 1, "promptBodies", 1, "providerCredentials", 0),
        "redactionNotes", "Raw skill/reference bodies, prompt text, loader inputs, provider credentials, hidden scopes, and bearer tokens are omitted.",
        "conflictVersionDrift", false));
    data.put("impactSummary", mapOf(
        "userVisibleBehaviorImpact", "Skill/reference availability may change only after behavior proposal review and separate activation.",
        "authorityToolDataBoundaryChanges", "No authority, tool, data, or evidence boundary expansion is granted by this review surface.",
        "affectedWorkstreams", List.of("Agent Admin", "Governance/Policy", "Audit/Trace"),
        "downstreamProposalLifecycleBlockers", List.of("approval-required", "activation-separate-surface", "provider-readiness-fail-closed"),
        "rollbackAvailability", "Requires activated proposal metadata before rollback is available.",
        "providerRuntimeReadiness", "blocked_provider_or_runtime",
        "simulationPromptRiskOrHumanApprovalRequired", true));
    data.put("riskAndEvidence", mapOf(
        "riskClass", "high",
        "policyReadableReasons", List.of("Manifest/reference changes can affect loader scope and evidence use.", "Review and activation are separate backend-authorized steps."),
        "confidenceQualityNotes", List.of("Compact manifest checksums and entries only", "Provider-backed simulation requires real provider/runtime configuration."),
        "recentRelatedGovernedLoaderDenials", List.of("unassigned-skill-load-denied", "reference-evidence-scope-expansion-denied"),
        "safeEvidenceRefs", List.of("AgentSkillManifest", "AgentReferenceManifest", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"),
        "requiredHumanReviewReasons", List.of("manifest-change", "reference-bundle-change", "authority-or-evidence-boundary")));
    data.put("reviewState", mapOf(
        "allowedDecisions", List.of("submit-review", "approve-after-review", "reject-with-reason"),
        "disabledDecisions", List.of("activate-directly", "edit-raw-skill", "edit-raw-reference", "change-provider-config", "expand-tool-boundary"),
        "idempotencyState", "client-generated keys required for submit/approve/reject decisions",
        "approvalRequired", true,
        "rejectionReasonRequired", true,
        "conflictState", "none",
        "nextRequiredSurface", "surface-agent-behavior-proposal",
        "noDirectMutation", true));
    data.put("authorizedActions", actions.stream().map(this::authorizedActionMap).toList());
    data.put("safeRedactionSummary", mapOf(
        "rawSkillReferenceBodies", "omitted",
        "promptText", "omitted",
        "providerCredentials", "omitted",
        "modelInternals", "omitted",
        "hiddenScopes", "omitted",
        "rawTraceEvidence", "role-gated",
        "loaderToolInputs", "omitted",
        "bearerTokens", "omitted",
        "internalStackTraces", "omitted"));
    data.put("diagnostics", mapOf(
        "managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "manifestRecordRef", manifest.get("recordId"),
        "proposalId", "proposal-agent-admin-manifest-001",
        "capabilityIds", List.of(AGENT_ADMIN_GET_MANIFEST_CAPABILITY, AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, AGENT_ADMIN_REVIEW_CAPABILITY, AGENT_ADMIN_REJECT_CAPABILITY, AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY),
        "redactedTraceIds", data.get("traceLinks"),
        "correlationId", correlationId,
        "redactionProfile", "browser-safe-skill-manifest-governance"));
    data.put("states", List.of("loading", "ready", "empty-no-active-manifest-change", "submitting", "simulating", "approval-required", "review-submitted", "review-approved", "review-rejected", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "no-op", "failure"));
    data.put("noDirectActivation", true);
    return envelope("surface-agent-skill-manifest-diff", "governance-diff", "Skill and reference manifest review", actor, correlationId, data, actions);
  }

  private SurfaceEnvelope agentToolBoundarySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var boundary = agentAdminService.toolBoundaryDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId);
    var actions = toolBoundaryActions();
    var data = governanceDiffData("agent_admin.tool_boundary_diff.v1", "proposal-agent-admin-tool-boundary-001", "blocked", "critical",
        "Current ToolPermissionBoundary allows only scoped read-only Agent Admin evidence and governed loaders.",
        "Requested side-effecting grants are denied or approval-required by backend simulation before any activation.",
        List.of(
            mapOf("path", "redactedToolBoundaryDiff.grants", "before", boundary.get("grants"), "after", "side-effecting grant requested", "impact", "External side effects stay blocked without separate approval, idempotency, and trace policy."),
            mapOf("path", "riskAndEvidence.toolPermissionBoundaryDecision", "before", "active grants enforce read-only evidence access", "after", "TOOL_BOUNDARY_DENIED for side-effecting expansion", "impact", "Frontend controls cannot bypass backend ToolPermissionBoundary enforcement."),
            mapOf("path", "impactSummary.providerRuntimeReadiness", "before", "not asserted", "after", "blocked_provider_or_runtime until real model/provider configuration is available", "impact", "Simulation routes fail closed instead of fabricating provider success.")),
        List.of("trace-agent-admin-tool-boundary-" + stableSuffix(correlationId), "trace-agent-admin-tool-denied-" + stableSuffix(correlationId)));
    data.put("surfaceContractAliases", List.of("agent_admin.tool_boundary.v1", "surface.agent_admin.tool_boundary.v1"));
    data.put("capabilityAliases", List.of("agent.tool_boundaries.manage", AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY));
    data.put("toolBoundarySummary", mapOf(
        "surfaceId", "surface-agent-tool-boundary-diff",
        "title", "Tool boundary simulation review",
        "type", "governance-diff / show-inspection",
        "contract", "agent_admin.tool_boundary_diff.v1",
        "managedAgentDisplayName", "Agent Admin Agent",
        "shortPurpose", "Review governed tool, data-boundary, and ToolPermissionBoundary changes without exposing raw tool inputs or hidden scope details.",
        "activeBoundaryVersionLabel", boundary.get("recordId") + "@" + boundary.get("version"),
        "proposedBoundaryVersionLabel", "proposal-agent-admin-tool-boundary-001",
        "proposalOrDriftState", "blocked",
        "reviewState", "approval-required",
        "riskClass", "critical",
        "approvalRequirement", "Human Agent Admin review and backend policy approval are required before activation.",
        "providerRuntimeReadinessCategory", "blocked_provider_or_runtime",
        "lastChangedOrReviewed", "seed tool-boundary checksum evidence loaded from backend repository",
        "lastRefreshed", Instant.now().toString(),
        "noDirectActivation", true));
    data.put("scopeSummary", mapOf(
        "selectedContextId", actor.selectedContext().membershipId(),
        "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
        "tenantId", actor.selectedContext().tenantId(),
        "customerId", actor.selectedContext().customerId(),
        "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(),
        "governanceAuthorized", true,
        "visibilityDecision", "visible"));
    data.put("redactedToolBoundaryDiff", mapOf(
        "beforeSummary", "Current ToolPermissionBoundary grants are backend-owned and redacted to safe capability/category summaries.",
        "afterSummary", "Proposed side-effecting expansion remains denied or approval-required until behavior proposal review and separate activation.",
        "activeGrants", boundary.get("grants"),
        "proposedGrantSummary", List.of("external side-effect email.send execute grant remains blocked", "loader/evidence reads stay scoped to assigned manifests and references"),
        "permissionChanges", List.of("added external side-effect operation class denied", "no tenant/customer scope expansion granted", "ToolPermissionBoundary denial semantics preserved"),
        "dataEvidenceSourceCategories", List.of("AgentWorkTrace", "SkillLoadTrace", "ReferenceLoadTrace", "ToolPermissionBoundary"),
        "loaderToolAccessChanges", List.of("side-effecting tool execution disabled in no-side-effect simulation", "raw loader inputs and outputs omitted"),
        "omittedSectionCounts", mapOf("rawToolInputsOutputs", 1, "promptBodies", 1, "skillReferenceBodies", 2, "providerCredentials", 0, "hiddenScopeIdentifiers", 0),
        "redactionNotes", "Raw tool inputs/outputs, prompt text, skill/reference bodies, provider credentials, hidden scopes, bearer tokens, and policy internals are omitted.",
        "conflictVersionDrift", false));
    data.put("impactSummary", mapOf(
        "userVisibleBehaviorImpact", "A side-effecting tool grant would change what an agent can do only after review, approval, and separate activation.",
        "authorityExpansionContraction", "Authority expansion is blocked in this starter unless backend policy, human review, idempotency, and trace evidence all pass.",
        "affectedWorkstreams", List.of("Agent Admin", "Governance/Policy", "Audit/Trace"),
        "downstreamProposalLifecycleBlockers", List.of("approval-required", "activation-separate-surface", "provider-readiness-fail-closed", "tool-boundary-denied"),
        "rollbackAvailability", "Requires activated proposal metadata before rollback is available.",
        "providerRuntimeReadiness", "blocked_provider_or_runtime",
        "simulationPromptRiskModelReferenceOrHumanApprovalRequired", true));
    data.put("riskAndEvidence", mapOf(
        "riskClass", "critical",
        "policyReadableReasons", List.of("Side-effecting tool grants can send data outside the selected tenant scope.", "ToolPermissionBoundary checks must remain backend-authoritative."),
        "confidenceQualityNotes", List.of("Active grant checksum and redacted grant categories loaded from repository", "Provider-backed simulation requires real provider/runtime configuration."),
        "recentGovernedLoaderOrBoundaryDenials", List.of("TOOL_BOUNDARY_DENIED: email.send", "unassigned-skill-load-denied"),
        "safeSimulationEvidenceRefs", List.of("ToolPermissionBoundary", "AgentWorkTrace", "AgentRuntimeTest", "BehaviorChangeProposal"),
        "requiredHumanReviewReasons", List.of("authority-expansion", "external-side-effect", "tenant-scope-impact"),
        "toolPermissionBoundaryDecisionSummary", "Side-effecting tool execution remains denied; browser action ids and diagnostics are advisory only."));
    data.put("reviewState", mapOf(
        "allowedDecisions", List.of("simulate", "submit-review", "approve-after-review", "reject-with-reason"),
        "disabledDecisions", List.of("activate-directly", "execute-raw-tool", "edit-tool-grants-inline", "change-provider-config", "delete-source-artifact"),
        "idempotencyState", "client-generated keys required for submit/approve/reject decisions",
        "approvalRequired", true,
        "rejectionReasonRequired", true,
        "conflictState", "none",
        "providerRuntimeBlockedState", "blocked_provider_or_runtime",
        "nextRequiredSurface", "surface-agent-behavior-proposal",
        "noDirectMutation", true));
    data.put("authorizedActions", actions.stream().map(this::authorizedActionMap).toList());
    data.put("safeRedactionSummary", mapOf(
        "rawToolInputsOutputs", "omitted",
        "promptText", "omitted",
        "skillReferenceBodies", "omitted",
        "providerCredentials", "omitted",
        "modelInternals", "omitted",
        "hiddenScopes", "omitted",
        "rawTraceEvidence", "role-gated",
        "bearerTokens", "omitted",
        "policyInternals", "role-gated",
        "internalStackTraces", "omitted"));
    data.put("diagnostics", mapOf(
        "managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "toolBoundaryVersionId", boundary.get("recordId") + "@" + boundary.get("version"),
        "proposalId", "proposal-agent-admin-tool-boundary-001",
        "capabilityIds", List.of(AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, AGENT_ADMIN_REVIEW_CAPABILITY, AGENT_ADMIN_REJECT_CAPABILITY, AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY),
        "redactedTraceIds", data.get("traceLinks"),
        "correlationId", correlationId,
        "redactionProfile", "browser-safe-tool-boundary-governance"));
    data.put("states", List.of("loading", "ready", "empty-no-active-tool-boundary-change", "submitting", "simulating", "approval-required", "review-submitted", "review-approved", "review-rejected", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "tool-boundary-denied", "no-op", "failure"));
    data.put("noDirectActivation", true);
    return envelope("surface-agent-tool-boundary-diff", "governance-diff", "Tool boundary simulation review", actor, correlationId, data, actions);
  }

  private SurfaceEnvelope agentModelRefsSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var data = new LinkedHashMap<>(agentAdminService.modelRefDetail(actor, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, correlationId));
    var provider = mapValue(data.get("readiness"));
    var status = String.valueOf(provider.getOrDefault("status", data.getOrDefault("status", "blocked_provider_or_runtime")));
    var providerReady = "ready".equals(status);
    var activeLabel = String.valueOf(data.getOrDefault("displayName", data.get("recordId")));
    var providerAlias = String.valueOf(data.getOrDefault("providerAlias", "redacted-provider-alias"));
    var actions = List.of(
        agentModelRefsRefreshAction(),
        agentModelRefsRunTestAction(providerReady),
        agentModelRefsSubmitReviewAction(),
        agentModelRefsApproveAction(),
        agentModelRefsRejectAction(),
        agentModelRefsOpenPromptGovernanceAction(),
        agentModelRefsOpenToolBoundaryAction(),
        agentModelRefsOpenTraceAction(),
        agentModelRefsBackToDetailAction());
    data.put("proposalId", "proposal-agent-admin-model-refs-001");
    data.put("lifecycleState", "blocked");
    data.put("riskClassification", "high");
    data.put("requiredApproval", "Human approval is required before any model-reference change can affect runtime behavior; activation remains a separate lifecycle decision.");
    data.put("simulationSummary", providerReady ? "Provider/model alias is readable with credentials redacted; readiness tests remain no-side-effect." : "Provider/runtime readiness is fail-closed; no fixture or model-less success is counted.");
    data.put("activationStatus", "approval-required");
    data.put("beforeSummary", "Active model reference " + activeLabel + " uses provider alias " + providerAlias + " with provider credentials and raw responses omitted.");
    data.put("afterSummary", "Proposed model-reference changes require behavior-proposal review, provider/runtime readiness evidence, no-side-effect testing, and separate activation before use.");
    data.put("changes", List.of(
        mapOf("path", "modelReference.providerAlias", "before", providerAlias, "after", "reviewed-provider-alias", "impact", "Provider routing cannot change without backend authorization and human review."),
        mapOf("path", "providerReadiness.status", "before", status, "after", providerReady ? "ready-with-redaction" : "blocked_provider_or_runtime", "impact", "Readiness must fail closed and never claim fixture/model-less success."),
        mapOf("path", "runtimeBoundary.noDirectActivation", "before", "true", "after", "true", "impact", "This surface cannot activate behavior or edit provider configuration directly.")));
    data.put("simulation", mapOf(
        "affectedCapabilities", List.of(AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY),
        "expectedAllows", List.of("read redacted ModelConfigRef", "open trace", "return to detail"),
        "expectedDenials", List.of("provider credential entry", "raw provider response", "direct activation", "cross-tenant model reference"),
        "warnings", providerReady ? List.of("Provider credentials remain backend-only.") : List.of("blocked_provider_or_runtime", "noFakeSuccess"),
        "confidence", providerReady ? "backend-readiness-redacted" : "fail-closed",
        "evidenceTraceIds", data.get("traceLinks")));
    data.put("modelReferenceSummary", mapOf("surfaceId", "surface-agent-model-refs", "title", "Model reference proposal/review", "type", "governance-diff / show-inspection", "contract", "agent_admin.model_refs.v1", "selectedManagedAgent", "Agent Admin Agent", "activeModelReferenceLabel", activeLabel, "proposedModelReferenceLabel", "review-required", "providerReadinessCategory", status, "runtimeReadinessCategory", status, "proposalState", "approval-required", "reviewState", "pending-human-review", "riskClass", "high", "approvalRequired", true, "lastRefreshedAt", Instant.now().toString(), "noDirectActivation", true));
    data.put("scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "organizationDisplayName", contextLabel(actor.selectedContext().tenantId(), null), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true));
    data.put("redactedModelReferenceDiff", mapOf("modelAliasBefore", activeLabel, "modelAliasAfter", "reviewed-provider-alias", "providerCategory", "browser-safe-alias-only", "routingProfile", "tenant-scoped managed-agent runtime", "temperatureLimitClass", "policy-controlled", "retrievalReferenceImplications", "Prompt/skill/reference bodies remain omitted.", "toolCompatibilityNotes", "ToolPermissionBoundary remains backend-authoritative.", "dataBoundaryEffects", "No cross-tenant/customer visibility.", "fallbackDegradationBehavior", providerReady ? "ready-with-redaction" : "provider-fail-closed", "omittedSectionCount", 8, "redactionNotes", List.of("provider credentials omitted", "raw provider errors omitted", "raw prompt/skill/reference bodies omitted", "hidden scopes omitted"), "conflictState", "none"));
    data.put("providerReadiness", mapOf("category", status, "configuredState", status, "failClosedReason", provider.getOrDefault("safeReason", "Provider/runtime readiness must be verified before model-backed success."), "runtimeDependencyStatus", status, "modelAvailabilityStatus", status, "recoveryRoutes", List.of("action-agent-model-refs-run-test", "action-agent-model-refs-open-trace", "action-agent-model-refs-back-to-detail"), "noFakeSuccess", !providerReady));
    data.put("impactSummary", mapOf("expectedUserVisibleBehaviorImpact", "Model/provider routing changes can alter managed-agent answers and must be reviewed before activation.", "authorityToolDataBoundaryChanges", "No authority expansion from browser state; backend capabilities and ToolPermissionBoundary remain authoritative.", "affectedWorkstreams", List.of("Agent Admin", "Governance/Policy", "Audit/Trace"), "downstreamProposalLifecycleBlockers", List.of("approval-required", "provider-readiness", "activation-separate-surface"), "rollbackAvailability", "Requires activated proposal metadata before rollback is available.", "providerRuntimeReadiness", status, "simulationPromptRiskToolBoundaryOrHumanApprovalRequired", true));
    data.put("riskAndEvidence", mapOf("riskClass", "high", "policyReadableReasons", List.of("Model/provider changes can alter generated behavior.", "Provider readiness must not be faked by fixture or model-less output."), "confidenceQualityNotes", List.of("ModelConfigRef loaded through backend AgentAdminService", "Provider credentials and raw provider responses remain redacted."), "modelBoundaryConcerns", List.of("provider alias only", "runtime binding readiness", "no raw provider error in browser"), "recentProviderRuntimeFailuresOrDenials", providerReady ? List.of() : List.of("blocked_provider_or_runtime"), "safeEvidenceRefs", List.of("ModelConfigRef", "AgentDefinition", "AgentWorkTrace"), "requiredHumanReviewReasons", List.of("model-provider-routing", "runtime-readiness", "tenant-scope-impact")));
    data.put("reviewState", mapOf("allowedDecisions", List.of("refresh", "submit-review", "approve-after-review", "reject-with-reason", "open-prompt-governance", "open-tool-boundary", "open-trace", "back-to-detail"), "disabledDecisions", List.of("activate-directly", "edit-provider-credentials", "raw-model-config-edit", "provider-secret-entry", "cross-scope-routing"), "idempotencyState", "client-generated keys required for submit/approve/reject decisions", "approvalRequired", true, "rejectionReasonRequired", true, "conflictState", "none", "providerRuntimeBlockedState", providerReady ? "ready" : "blocked_provider_or_runtime", "nextRequiredSurface", "surface-agent-behavior-proposal", "noDirectMutation", true));
    data.put("safeRedactionSummary", mapOf("providerCredentials", "omitted", "rawProviderModelResponses", "omitted", "rawPromptText", "omitted", "skillReferenceBodies", "omitted", "hiddenScopes", "omitted", "rawTraceEvidence", "role-gated", "bearerTokens", "omitted", "internalStackTraces", "omitted", "privilegedPolicyDiagnostics", "role-gated"));
    data.put("diagnostics", mapOf("managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "activeModelReferenceId", data.get("recordId"), "providerModelAlias", providerAlias, "runtimeBindingLabel", "managed-agent-runtime", "proposalId", "proposal-agent-admin-model-refs-001", "capabilityIds", List.of(AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, AGENT_ADMIN_REVIEW_CAPABILITY, AGENT_ADMIN_REJECT_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY), "redactedTraceIds", data.get("traceLinks"), "correlationId", correlationId, "providerReadinessReasonCode", status, "redactionProfile", "browser-safe-model-reference-governance"));
    data.put("authorizedActions", actions.stream().map(this::authorizedActionMap).toList());
    data.put("states", List.of("loading", "ready", "empty-no-active-model-reference-change", "submitting", "testing", "approval-required", "review-submitted", "review-approved", "review-rejected", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "no-op", "failure"));
    data.put("noFakeSuccess", !providerReady);
    data.put("noDirectActivation", true);
    return envelope("surface-agent-model-refs", "governance-diff", "Model reference proposal/review", actor, correlationId, data, actions);
  }

  private SurfaceEnvelope agentSeedMaterialSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return agentSeedMaterialSurface(actor, null, correlationId);
  }

  private SurfaceEnvelope agentSeedMaterialSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var actions = List.of(
        agentSeedMaterialRefreshAction(),
        agentSeedMaterialSearchAction(),
        agentSeedMaterialResetFiltersAction(),
        agentSeedMaterialOpenProvenanceAction(),
        agentSeedMaterialPrepareImportAction(),
        agentSeedMaterialStartImportAction(),
        agentSeedMaterialCancelImportAction(),
        agentSeedMaterialOpenAgentDetailAction(),
        agentSeedMaterialOpenTraceAction(),
        agentSeedMaterialBackToSourceAction());
    return envelope("surface-agent-seed-material", "list-search", "Agent seed material", actor, correlationId, agentAdminService.seedMaterialDetail(actor, input, correlationId), actions);
  }

  private SurfaceEnvelope agentTestConsoleSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "agent_admin.test_console.v1", correlationId);
    var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, correlationId, "No-side-effect Agent Admin test console"));
    var allowedSkill = agentRuntimeService.readSkill(new AgentRuntimeService.SkillReadRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, correlationId, "agent-admin.starter-guidance.v1"));
    var deniedSkill = agentRuntimeService.readSkill(new AgentRuntimeService.SkillReadRequest(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, actor.selectedContext(), "test", AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, correlationId, "ua.role-recommendation.v1"));
    var traceIds = List.of(prompt.traceId(), allowedSkill.traceId(), deniedSkill.traceId(), "trace-agent-work-88");
    var providerBlocked = true;
    var workflowStatus = providerBlocked ? "blocked-provider-or-runtime" : "completed-advisory";
    var actions = List.of(
        agentTestConsoleRefreshAction(),
        agentTestConsoleRunAction(),
        agentTestConsoleReadResultAction(),
        agentTestConsoleOpenProposalAction(false),
        agentTestConsoleOpenTraceAction(),
        agentTestConsoleBackToDetailAction());
    return envelope("surface-agent-test-console", "workflow-status", "No-side-effect agent test console", actor, correlationId,
        mapOf(
            "surfaceContract", "agent_admin.test_console.v1",
            "capabilityAliases", List.of("agent.runtime.test", "agent.read_skill", "agent_admin.draft_behavior_change"),
            "workflowId", "agent-runtime-test",
            "testSummary", mapOf(
                "surfaceId", "surface-agent-test-console",
                "title", "No-side-effect runtime test surface",
                "type", "workflow-status",
                "contract", "agent_admin.test_console.v1",
                "selectedManagedAgent", "Agent Admin Agent",
                "shortPurpose", "Advisory managed-agent runtime test with no behavior activation or artifact mutation.",
                "requestedTestCategory", "detail-readiness",
                "taskStatus", workflowStatus,
                "statusReason", "Prompt assembly, governed-loader checks, and ToolPermissionBoundary denial evidence are backend-derived; provider/model invocation remains fail-closed until configured.",
                "startedAt", Instant.now().toString(),
                "finishedAt", providerBlocked ? null : Instant.now().toString(),
                "lastRefreshedAt", Instant.now().toString(),
                "noDirectMutationNotice", "Running or reading this test never activates behavior, edits prompts/skills/references, expands tool boundaries, or approves proposals.",
                "nextRecommendedHumanRoute", providerBlocked ? "Review provider/model readiness before drafting a behavior proposal." : "Open a separate behavior proposal for human review."),
            "scopeSummary", mapOf(
                "selectedAuthContextId", actor.selectedContext().membershipId(),
                "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
                "tenantId", actor.selectedContext().tenantId(),
                "organizationDisplayName", contextLabel(actor.selectedContext().tenantId(), actor.selectedContext().customerId()),
                "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(),
                "governanceAuthorized", true,
                "visibilityDecision", "visible"),
            "runtimeReadiness", mapOf(
                "providerModelRuntimeConfigurationCategory", "blocked_provider_or_runtime",
                "failClosedReasonCode", "blocked_provider_or_runtime",
                "recoveryCopy", "Configure an approved model/provider runtime before counting a model-backed no-side-effect test as successful.",
                "noFakeSuccess", true,
                "fixtureModelLessResultsAccepted", false),
            "safeResultSummary", mapOf(
                "advisoryResultOutcome", workflowStatus,
                "behaviorEvidenceFindings", List.of("PromptAssemblyTrace emitted with compact manifests only", "Assigned skill read succeeded through governed readSkill", "Unassigned skill read was denied by ToolPermissionBoundary semantics"),
                "policyReadableReasons", List.of("Backend AuthContext and tenant capability checks passed", "Browser row/action ids are advisory", "Provider/model success is not fabricated"),
                "confidenceQualityNotes", List.of("Trace ids are redacted and browser-safe", "Raw prompts, skills, references, provider responses, evidence documents, and loader inputs are omitted"),
                "humanReviewRequired", true,
                "proposalCanBeDraftedOrUpdated", false,
                "proposalDisabledReason", "blocked_provider_or_runtime"),
            "loaderAndBoundaryChecks", List.of(
                mapOf("checkId", "prompt-assembly", "categoryLabel", "PromptAssemblyTrace", "decision", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "allowed" : "denied", "reasonSummary", prompt.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "Compact prompt, skill, reference, model-binding, and AuthContext summary assembled server-side." : prompt.safeDenialReason(), "affectedArtifactCategory", "prompt", "redactionNote", "raw prompt body omitted", "traceId", prompt.traceId()),
                mapOf("checkId", "assigned-skill-load", "categoryLabel", "SkillLoadTrace assigned skill", "decision", allowedSkill.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "allowed" : "denied", "reasonSummary", allowedSkill.decision() == AgentRuntimeTrace.Decision.ALLOWED ? "Assigned active skill was readable through governed loader path." : allowedSkill.safeDenialReason(), "affectedArtifactCategory", "skill", "redactionNote", "skill body omitted from normal browser payload", "traceId", allowedSkill.traceId()),
                mapOf("checkId", "unassigned-skill-denial", "categoryLabel", "ToolPermissionBoundary denial", "decision", deniedSkill.decision() == AgentRuntimeTrace.Decision.DENIED ? "denied" : "allowed", "reasonSummary", "Unassigned User Admin skill remains unavailable to the Agent Admin test runtime.", "affectedArtifactCategory", "tool-boundary", "redactionNote", "denial reason is safe and non-enumerating", "targetSurfaceId", "surface-agent-tool-boundary-diff", "traceId", deniedSkill.traceId()),
                mapOf("checkId", "provider-model-runtime", "categoryLabel", "Provider/model runtime", "decision", "blocked_provider_or_runtime", "reasonSummary", "No configured provider/model smoke is counted as success in this local runtime.", "affectedArtifactCategory", "model-reference", "redactionNote", "provider credentials and raw provider errors omitted", "targetSurfaceId", "surface-agent-model-refs")),
            "proposalRoute", mapOf("targetSurfaceId", "surface-agent-behavior-proposal", "proposalUpdateStatus", "disabled", "disabledReason", "blocked_provider_or_runtime", "noDirectActivationCopy", "A proposal route cannot activate artifacts directly and remains disabled until provider/runtime evidence exists."),
            "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(),
            "safeRedactionSummary", mapOf("rawPromptText", "omitted", "rawSkillReferenceBodies", "omitted", "providerCredentials", "omitted", "rawProviderModelResponses", "omitted", "hiddenScopes", "omitted", "rawTraceEvidence", "role-gated", "loaderToolInputsOutputs", "omitted", "bearerTokens", "omitted", "internalStackTraces", "omitted", "privilegedPolicyDiagnostics", "role-gated"),
            "diagnostics", mapOf("managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "sourceSurfaceActionId", "action-agent-detail-run-test", "requestedArtifactVersionLabels", List.of("prompt-active", "skill-manifest-active", "tool-boundary-active", "model-ref-active"), "traceKinds", List.of("PromptAssemblyTrace", "SkillLoadTrace", "AgentWorkTrace"), "redactedTraceIds", traceIds, "correlationId", correlationId, "redactionProfile", "browser-safe-test-console"),
            "states", List.of("loading", "ready", "queued", "running", "completed-advisory", "blocked-provider-or-runtime", "blocked-tool-boundary", "skipped-no-visible-artifact", "validation-error", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "no-op", "proposal-route-disabled", "failure"),
            "traceIds", traceIds,
            "noProductionSideEffects", true,
            "noDirectMutation", true,
            "noFakeSuccess", true),
        actions);
  }

  private SurfaceEnvelope agentBehaviorProposalSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "agent_admin.behavior_proposal.v1", correlationId);
    var proposals = visibleBehaviorProposals(actor);
    var selected = proposals.stream().findFirst().orElse(null);
    var agent = agentBehaviorRepository.agentDefinition(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElse(null);
    var traceRefs = List.of("trace-agent-behavior-proposal-" + stableSuffix(correlationId));
    var noPending = selected == null;
    var selectedStatus = noPending ? "empty-no-pending-proposal" : selected.status().name().toLowerCase(Locale.ROOT);
    var providerReady = false;
    var approved = selected != null && selected.status() == BehaviorChangeProposal.Status.APPROVED;
    var activated = selected != null && selected.status() == BehaviorChangeProposal.Status.ACTIVATED;
    var actions = List.of(
        agentBehaviorProposalRefreshAction(),
        agentBehaviorProposalSubmitAction(noPending || selected.status() != BehaviorChangeProposal.Status.PROPOSED),
        agentBehaviorProposalApproveAction(noPending || selected.status() != BehaviorChangeProposal.Status.IN_REVIEW || !providerReady),
        agentBehaviorProposalRejectAction(noPending || (selected.status() != BehaviorChangeProposal.Status.IN_REVIEW && selected.status() != BehaviorChangeProposal.Status.APPROVED)),
        agentBehaviorProposalDeferAction(noPending || selected.status() == BehaviorChangeProposal.Status.ACTIVATED || selected.status() == BehaviorChangeProposal.Status.ROLLED_BACK),
        agentBehaviorProposalCancelAction(noPending || selected.status() == BehaviorChangeProposal.Status.ACTIVATED || selected.status() == BehaviorChangeProposal.Status.ROLLED_BACK || selected.status() == BehaviorChangeProposal.Status.CANCELLED || selected.status() == BehaviorChangeProposal.Status.DENIED),
        agentBehaviorProposalOpenActivationAction(!approved || !providerReady),
        agentBehaviorProposalOpenRollbackAction(!activated),
        agentBehaviorProposalOpenSourceAction(),
        agentBehaviorProposalOpenTraceAction());
    var allowedActionMaps = actions.stream().filter(action -> action.disabled() == null).map(this::authorizedActionMap).toList();
    var disabledActionMaps = actions.stream()
        .filter(action -> action.disabled() != null)
        .map(action -> mapOf("actionId", action.actionId(), "label", action.label(), "reason", action.disabled().message(), "reasonCode", action.disabled().reasonCode()))
        .toList();
    var proposalMaps = proposals.stream().map(this::behaviorProposalMap).toList();
    var recommendationOutcome = noPending
        ? "no_pending_proposal"
        : switch (selected.status()) {
          case PROPOSED -> "submit_for_review";
          case IN_REVIEW -> providerReady ? "approve" : "needs_more_evidence";
          case APPROVED -> providerReady ? "ready_for_activation" : "needs_more_evidence";
          case ACTIVATED -> "rollback_available";
          case REJECTED, DENIED -> "reject";
          case DEFERRED, CANCELLED -> "defer";
          case ROLLED_BACK -> "rollback_available";
        };
    var data = mapOf(
        "surfaceContract", "agent_admin.behavior_proposal.v1",
        "decisionId", selected == null ? "decision-behavior-proposal-empty" : selected.proposalId(),
        "recommendation", noPending ? "No pending behavior proposal is visible in this selected AuthContext." : "Human decision is required before any managed-agent behavior can activate; provider-backed success is fail-closed until runtime readiness exists.",
        "riskScore", selected == null ? "not_scored" : selected.riskClassification(),
        "confidenceScore", providerReady ? "provider-evidence-present" : "blocked_provider_or_runtime",
        "risk", selected == null ? "none" : selected.riskClassification(),
        "impact", selected == null ? "No source artifact, lifecycle, provider configuration, tenant override, or tool boundary is changed by opening this decision surface." : "Decision-state changes are backend-authorized and never directly activate prompts, skills, references, model refs, tool boundaries, lifecycle state, or provider configuration.",
        "affectedTarget", selected == null ? AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID : selected.targetArtifact().name().toLowerCase(Locale.ROOT),
        "policyBasis", "managed-agent-governance with backend AuthContext, tenant scope, proposal/version visibility, provider/runtime, and trace checks",
        "idempotencyKeySource", "client-generated for submit/approve/reject/defer/cancel; refresh/source/trace reads are idempotent",
        "activationBlocker", approved && !providerReady ? "blocked_provider_or_runtime" : null,
        "noDirectMutation", true,
        "traceLinks", traceRefs,
        "proposalSummary", mapOf(
            "surfaceId", "surface-agent-behavior-proposal",
            "title", "Behavior proposal decision card",
            "type", "decision-card",
            "contract", "agent_admin.behavior_proposal.v1",
            "proposalLabel", noPending ? "No visible behavior proposal" : selected.proposalId(),
            "selectedManagedAgentDisplayName", agent == null ? "Agent Admin Agent" : agent.displayName(),
            "sourceSurfaceActionCategory", selected == null ? "dashboard-queue" : selected.targetArtifact().name().toLowerCase(Locale.ROOT).replace('_', '-'),
            "proposalStatus", selectedStatus,
            "decisionStatus", selectedStatus,
            "riskClass", selected == null ? "none" : selected.riskClassification(),
            "confidenceCategory", providerReady ? "runtime-evidence-present" : "blocked_provider_or_runtime",
            "approvalRequirement", "human approval plus separate activation confirmation required",
            "lifecycleImpact", activated ? "activated-behavior-has-separate-rollback-route" : "no lifecycle mutation from this surface",
            "currentVersionLabel", selected == null ? "not_applicable" : selected.targetArtifactId(),
            "proposedVersionLabel", selected == null ? "not_applicable" : "proposal-" + stableSuffix(selected.proposalId()),
            "lastChangedAt", selected == null ? Instant.now().toString() : selected.createdAt().toString(),
            "lastReviewedAt", selected == null || selected.reviewedAt() == null ? null : selected.reviewedAt().toString(),
            "lastRefreshedAt", Instant.now().toString(),
            "noDirectActivationNotice", "Approval records decision evidence only; activation and rollback require separate governed confirmation surfaces."),
        "scopeSummary", mapOf(
            "selectedAuthContextId", actor.selectedContext().membershipId(),
            "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT),
            "tenantDisplayName", actor.selectedContext().tenantId(),
            "organizationDisplayName", actor.selectedContext().tenantId(),
            "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(),
            "governanceAuthorized", true,
            "visibilityDecision", noPending ? "empty-no-pending-proposal" : "visible"),
        "recommendation", mapOf(
            "outcome", recommendationOutcome,
            "rationale", noPending ? "No visible proposal is currently queued; hidden or cross-scope proposals are not enumerated." : "Decision must stay human-reviewed, traceable, provider/runtime-aware, and separate from lifecycle activation.",
            "requiredReviewerCategory", "tenant-or-organization-admin-with-managed-agent-governance",
            "confidenceQualityNotes", List.of("Browser proposal ids are advisory", "Provider/model success is not fabricated", "Raw prompts, skills, references, tool inputs, provider responses, and hidden scopes are omitted"),
            "dependsOnProviderRuntimeEvidence", true,
            "providerRuntimeReadiness", providerReady ? "configured" : "blocked_provider_or_runtime",
            "noFakeSuccess", true),
        "evidenceSummary", List.of(
            mapOf("evidenceId", "proposal-queue", "label", "Proposal queue", "status", noPending ? "empty" : selectedStatus, "summary", proposals.size() + " browser-safe proposal(s) visible for this selected tenant/AuthContext", "redactionNote", "proposal ids only; raw prompt/skill/reference/provider/tool evidence omitted"),
            mapOf("evidenceId", "provider-runtime", "label", "Provider/runtime readiness", "status", "blocked_provider_or_runtime", "summary", "No provider/model success is counted for this local runtime; activation and provider-dependent approval stay fail-closed.", "noFakeSuccess", true, "redactionNote", "provider credentials and raw provider errors omitted"),
            mapOf("evidenceId", "trace", "label", "Work trace", "status", "recorded-on-read-and-action", "summary", "Reads, decisions, denials, and route handoffs emit browser-safe trace refs and correlation ids.", "traceRefs", traceRefs, "redactionNote", "raw trace evidence remains role-gated")),
        "riskAndImpact", mapOf(
            "userVisibleBehaviorChanges", selected == null ? List.of() : List.of("Proposed " + selected.targetArtifact().name().toLowerCase(Locale.ROOT).replace('_', '-') + " behavior change remains inactive until approved and separately activated."),
            "affectedWorkstreamsSurfaces", List.of("Agent Admin", "surface-agent-admin-detail", "surface-agent-behavior-proposal", "surface-agent-activation-confirmation", "surface-agent-rollback-confirmation"),
            "authorityToolDataBoundaryImpact", "ToolPermissionBoundary and tenant scope remain backend-authoritative; no authority expansion is committed here.",
            "tenantOrganizationVisibilityImpact", "Only selected tenant/organization AuthContext proposals are visible; customer-scoped and cross-tenant proposals are denied or redacted.",
            "downstreamLifecycleBlockers", List.of("provider_runtime_readiness", "tool_boundary_evidence", "separate_activation_confirmation", "idempotency_key"),
            "rollbackDeactivationAvailability", activated ? "rollback route available with backend metadata" : "rollback disabled until an activated proposal exists",
            "alternativesConsidered", List.of("reject", "defer", "cancel", "return-to-source"),
            "expectedUserOutcome", "Admin can review or route the proposal without exposing secrets or mutating source artifacts.",
            "noDeleteNoCredentialChangeCopy", "This surface cannot delete source artifacts, tenant overrides, or provider credentials."),
        "decisionState", mapOf(
            "allowedDecisions", allowedActionMaps.stream().map(action -> action.get("actionId")).toList(),
            "disabledDecisions", disabledActionMaps,
            "requiredAcknowledgementFields", List.of("reason for reject/defer/cancel where required", "client idempotency key for decisions"),
            "idempotencyNoOpState", "repeat submit/decision actions return no-op or validation without duplicate lifecycle mutation",
            "approvalRequiredState", noPending ? "empty-no-pending-proposal" : "approval-required",
            "conflictStaleVersionState", "conflict for stale proposal/source version",
            "providerFailClosedState", "blocked_provider_or_runtime",
            "activationReadiness", approved && providerReady ? "ready-for-activation" : "activation-route-disabled",
            "rollbackReadiness", activated ? "rollback-available" : "rollback-route-disabled",
            "nextSafeRecoverySurface", "surface-agent-admin-detail"),
        "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(),
        "allowedActions", allowedActionMaps,
        "disabledActions", disabledActionMaps,
        "safeRedactionSummary", mapOf("rawPromptText", "omitted", "skillReferenceBodies", "omitted", "providerCredentials", "omitted", "rawProviderModelResponses", "omitted", "rawToolInputsOutputs", "omitted", "hiddenScopes", "omitted", "rawTraceEvidence", "role-gated", "loaderToolInputs", "omitted", "bearerTokens", "omitted", "internalStackTraces", "omitted", "hiddenTenantCustomerIdentifiers", "omitted", "privilegedPolicyDiagnostics", "role-gated"),
        "diagnostics", mapOf("managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "proposalId", selected == null ? null : selected.proposalId(), "sourceSurfaceActionId", selected == null ? "action-agent-admin-open-behavior-proposals" : selected.correlationId(), "sourceArtifactVersionIdentifiers", selected == null ? List.of() : List.of(selected.targetArtifactId()), "capabilityIds", List.of(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, AGENT_ADMIN_REVIEW_CAPABILITY, AGENT_ADMIN_REJECT_CAPABILITY, AGENT_ADMIN_CANCEL_CAPABILITY, AGENT_ADMIN_ACTIVATE_CAPABILITY, AGENT_ADMIN_ROLLBACK_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY), "providerModelAliases", List.of("redacted-provider-alias"), "redactedTraceIds", traceRefs, "correlationId", correlationId, "idempotencyKey", "client-generated", "rowContextHash", stableSuffix((selected == null ? AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID : selected.proposalId()) + correlationId), "redactionProfile", "browser-safe-behavior-proposal"),
        "states", List.of("loading", "ready", "empty-no-pending-proposal", "submitting", "approving", "rejecting", "deferring", "decision-submitted", "decision-approved", "decision-rejected", "decision-deferred", "ready-for-activation", "rollback-available", "approval-required", "acknowledgement-required", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "tool-boundary-blocked", "activation-route-disabled", "rollback-route-disabled", "cancel/no-mutation", "no-op", "failure"),
        "proposals", proposalMaps,
        "noFakeSuccess", true);
    return envelope("surface-agent-behavior-proposal", "decision-card", "Behavior proposal decision card", actor, correlationId, data, actions);
  }

  private List<BehaviorChangeProposal> visibleBehaviorProposals(AuthContextResolver.ResolvedMe actor) {
    return agentRuntimeService.proposals().stream()
        .filter(proposal -> actor.selectedContext().tenantId().equals(proposal.tenantId()))
        .filter(proposal -> AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID.equals(proposal.agentDefinitionId()))
        .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
        .toList();
  }

  private Map<String, Object> behaviorProposalMap(BehaviorChangeProposal proposal) {
    return mapOf(
        "proposalId", proposal.proposalId(),
        "targetArtifact", proposal.targetArtifact().name().toLowerCase(Locale.ROOT),
        "targetArtifactId", proposal.targetArtifactId(),
        "status", proposal.status().name().toLowerCase(Locale.ROOT),
        "riskClassification", proposal.riskClassification(),
        "requestedByAccountId", proposal.requestedByAccountId(),
        "reviewedByAccountId", proposal.reviewedByAccountId(),
        "reviewReason", proposal.reviewReason(),
        "createdAt", proposal.createdAt().toString(),
        "reviewedAt", proposal.reviewedAt() == null ? null : proposal.reviewedAt().toString(),
        "redaction", "raw prompt, skill, reference, provider, and tool evidence omitted");
  }

  private BehaviorChangeProposal behaviorProposalInput(AuthContextResolver.ResolvedMe actor, Object input) {
    var requestedProposalId = stringInput(input, "proposalId", stringInput(input, "behaviorProposalId", ""));
    return visibleBehaviorProposals(actor).stream()
        .filter(proposal -> requestedProposalId.isBlank() || requestedProposalId.equals(proposal.proposalId()))
        .findFirst()
        .orElse(null);
  }


  private SurfaceEnvelope agentLifecycleConfirmationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, String lifecycleAction, boolean changed) {
    authContextResolver.requireCapability(actor.selectedContext(), AGENT_DEFINITIONS_MANAGE_CAPABILITY);
    var activation = "activate".equals(lifecycleAction) || "activated".equals(lifecycleAction);
    var normalizedAction = activation ? "activate" : "deactivate";
    var surfaceId = activation ? "surface-agent-activation-confirmation" : "surface-agent-deactivation-confirmation";
    var contract = activation ? "agent_admin.activation_confirmation.v1" : "agent_admin.deactivation_confirmation.v1";
    var agent = agentBehaviorRepository.agentDefinition(actor.selectedContext().tenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElse(null);
    var currentStatus = agent == null ? (activation ? "inactive_or_pending" : "active_or_pending") : agent.status().name().toLowerCase(Locale.ROOT);
    var proposedStatus = activation ? "active" : "deactivated";
    var traceRefs = List.of("trace-agent-definition-" + normalizedAction + "-confirmation-" + stableSuffix(correlationId));
    var disabledReason = changed ? null : (activation
        ? "Activation is disabled until a backend-visible approved proposal/version, provider/runtime readiness, policy, tool-boundary, acknowledgement, and idempotency prerequisites are all satisfied."
        : ("disabled".equals(currentStatus)
            ? "This managed agent is already deactivated; repeat confirmation is idempotent and performs no source-artifact deletion."
            : "Confirming requires the explicit DEACTIVATE acknowledgement, a browser-safe admin reason, backend capability authorization, idempotency, and trace evidence."));
    var data = mapOf(
        "surfaceContract", contract,
        "recordId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "recordLabel", agent == null ? "Agent Admin Agent" : agent.displayName(),
        "lifecycleAction", normalizedAction,
        "currentStatus", currentStatus,
        "proposedStatus", proposedStatus,
        "impactSummary", activation ? "Activation would enable runtime invocation only after backend approval, provider readiness, rollback metadata, idempotency, and trace checks pass." : "Deactivation would disable runtime invocation and governed loader access only after backend authorization, idempotency, and trace checks pass.",
        "approvalState", activation ? "approval-required-provider-fail-closed" : "approval required before mutation",
        "policyBasis", "managed-agent-governance lifecycle policy; no model output or frontend state may commit this directly",
        "idempotencyKeyHint", "client-generated lifecycle key bound to AgentDefinition and proposal context",
        "disabledReason", disabledReason,
        "evidenceRefs", List.of("AgentDefinition", "BehaviorChangeProposal", "PromptRiskReview", "ToolPermissionBoundary", "ModelConfigRef", "AgentWorkTrace"),
        "traceRefs", traceRefs,
        "actionContext", mapOf("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "proposalId", "proposal-agent-admin-activation-required", "approvedVersionLabel", "no-approved-version"),
        "noDirectMutation", true,
        "noFakeSuccess", true,
        "redaction", "Provider secrets, raw prompts, raw skills, raw references, and hidden authority remain omitted.",
        activation ? "activationSummary" : "deactivationSummary", mapOf("surfaceId", surfaceId, "title", activation ? "Confirm agent behavior activation" : "Confirm agent deactivation", "type", "lifecycle-confirmation", "contract", contract, "selectedManagedAgentDisplayName", agent == null ? "Agent Admin Agent" : agent.displayName(), "currentLifecycleState", currentStatus, "proposedLifecycleState", proposedStatus, "reasonCategory", activation ? null : "admin-supplied", "activeBehaviorVersionLabel", activation ? null : "current_active_version", "approvedBehaviorProposalLabel", activation ? "No approved provider-ready proposal selected" : "Separate deactivation confirmation required", "approvedVersionLabel", activation ? "blocked_until_approved_version" : "current_active_version", "readinessState", activation ? "provider-fail-closed" : ("disabled".equals(currentStatus) ? "already-deactivated/no-op" : "ready"), "lastApprovedAt", null, "lastReviewedAt", agent == null || agent.updatedAt() == null ? null : agent.updatedAt().toString(), "lastRefreshedAt", Instant.now().toString(), "finalConfirmationNotice", "Final lifecycle mutation is backend-authorized only; browser controls are advisory."),
        "scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantDisplayName", actor.selectedContext().tenantId(), "organizationDisplayName", actor.selectedContext().tenantId(), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true, "visibilityDecision", "visible"),
        activation ? "approvalSummary" : "policyAndApprovalSummary", mapOf("humanApprovalStatus", activation ? "missing-approved-candidate" : ("disabled".equals(currentStatus) ? "already-deactivated/no-op" : "deactivation-allowed-with-confirmation"), "requiredApproverCategory", "tenant-or-organization-admin-with-managed-agent-governance", "approvingActorLabel", null, "outstandingBlockers", activation ? List.of("approved_behavior_proposal_required", "provider_runtime_readiness_required", "tool_boundary_evidence_required", "explicit_acknowledgement_required") : List.of("explicit_acknowledgement_required", "admin_reason_required", "active_task_check_required"), "promptRiskReadiness", activation ? "review-required-before-activation" : "not-applicable-for-deactivation", "testReadiness", activation ? "no-side-effect-test-evidence-required" : "not-applicable-no-runtime-test-executed", "toolBoundaryReadiness", "backend ToolPermissionBoundary checks required", "modelReferenceReadiness", activation ? "provider-fail-closed" : "not_applicable_no_provider_call", "providerRuntimeReadiness", activation ? "blocked_provider_or_runtime" : "not_applicable_no_provider_success_claim", "lifecycleReadiness", "disabled".equals(currentStatus) ? "already-deactivated/no-op" : "active-candidate", "activeTaskImpact", "No active task ids are exposed to the browser; backend policy remains authoritative.", "noFakeSuccess", true),
        "safeEvidenceSummary", List.of(
            mapOf("evidenceId", "proposal-review", "label", "Behavior proposal review", "status", activation ? "missing-approved-candidate" : "not-selected", "redactionNote", "proposal ids only; raw prompt and evidence bodies omitted"),
            mapOf("evidenceId", "runtime-provider", "label", "Provider/runtime readiness", "status", activation ? "blocked_provider_or_runtime" : "not_applicable_no_provider_call", "redactionNote", "provider credentials and raw provider errors omitted"),
            mapOf("evidenceId", "tool-boundary", "label", "Tool-boundary checks", "status", "required", "redactionNote", "raw tool inputs/outputs omitted"),
            mapOf("evidenceId", "trace", "label", activation ? "Activation trace" : "Deactivation trace", "status", "recorded-on-read-and-attempt", "traceRefs", traceRefs)),
        "confirmationState", mapOf("allowedActions", activation ? List.of("refresh", "cancel", "open-proposal", "open-trace") : List.of("refresh", "confirm", "cancel", "open-proposal", "open-trace"), "disabledActions", activation ? List.of("confirm") : ("disabled".equals(currentStatus) ? List.of("confirm") : List.of()), "requiredAcknowledgementText", activation ? "ACTIVATE" : "DEACTIVATE", "acknowledgementRequired", true, "reasonRequired", !activation, "idempotencyState", "client-generated key required for confirmation attempts", "conflictState", "none", "providerFailClosedState", activation ? "blocked_provider_or_runtime" : "not_applicable", "nextSafeRecoverySurfaceId", activation ? "surface-agent-behavior-proposal" : "surface-agent-admin-detail", "noDirectMutation", true),
        "safeRedactionSummary", mapOf("rawPromptText", "omitted", "rawSkillReferenceBodies", "omitted", "providerCredentials", "omitted", "modelInternals", "omitted", "hiddenScopes", "omitted", "rawTraceEvidence", "role-gated", "loaderToolInputs", "omitted", "bearerTokens", "omitted", "internalStackTraces", "omitted", "privilegedPolicyDiagnostics", "role-gated"),
        "diagnostics", mapOf("managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "proposalId", "proposal-agent-admin-activation-required", "approvedArtifactVersionIds", List.of(), "capabilityIds", activation ? List.of(AGENT_DEFINITIONS_MANAGE_CAPABILITY, AGENT_ADMIN_ACTIVATE_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY) : List.of(AGENT_DEFINITIONS_MANAGE_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY), "providerModelAliases", List.of("redacted-provider-alias"), "redactedTraceIds", traceRefs, "correlationId", correlationId, "idempotencyKey", "client-generated", "rowContextHash", stableSuffix(AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "redactionProfile", "browser-safe-lifecycle-confirmation"),
        "states", activation
            ? List.of("loading", "ready", "submitting/confirming", "activation-complete", "approval-required", "acknowledgement-required", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "no-op", "cancel/no-mutation", "failure")
            : List.of("loading", "ready", "submitting/confirming", "deactivation-complete", "already-deactivated/no-op", "approval-required", "acknowledgement-required", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "tool-boundary-blocked", "no-op", "cancel/no-mutation", "failure"));
    var actions = activation
        ? List.of(agentActivationRefreshAction(), agentActivationConfirmAction(), agentActivationCancelAction(), agentActivationOpenProposalAction(), agentActivationOpenTraceAction())
        : List.of(agentDeactivationRefreshAction(), agentDeactivationConfirmAction("disabled".equals(currentStatus)), agentDeactivationCancelAction(), agentDeactivationOpenProposalAction(), agentDeactivationOpenTraceAction());
    return envelope(surfaceId, "lifecycle-confirmation", activation ? "Confirm agent activation" : "Confirm agent deactivation", actor, correlationId, data, actions);
  }

  private SurfaceEnvelope agentRollbackConfirmationSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return agentRollbackConfirmationSurface(actor, null, correlationId);
  }

  private String rollbackProposalId(AuthContextResolver.ResolvedMe actor, Object input) {
    var explicit = stringInput(input, "proposalId", stringInput(input, "activatedProposalId", stringInput(input, "rollbackProposalId", "")));
    if (!explicit.isBlank()) return explicit;
    var candidate = rollbackCandidate(actor, input);
    return candidate == null ? "" : candidate.proposalId();
  }

  private BehaviorChangeProposal rollbackCandidate(AuthContextResolver.ResolvedMe actor, Object input) {
    var requestedAgentId = stringInput(input, "agentDefinitionId", "");
    var explicit = stringInput(input, "proposalId", stringInput(input, "activatedProposalId", stringInput(input, "rollbackProposalId", "")));
    var tenantId = actor.selectedContext().tenantId();
    var proposals = agentRuntimeService.proposals().stream()
        .filter(proposal -> tenantId.equals(proposal.tenantId()))
        .filter(proposal -> explicit.isBlank() || explicit.equals(proposal.proposalId()))
        .filter(proposal -> requestedAgentId.isBlank() || requestedAgentId.equals(proposal.agentDefinitionId()))
        .filter(proposal -> proposal.status() == BehaviorChangeProposal.Status.ACTIVATED || proposal.status() == BehaviorChangeProposal.Status.ROLLED_BACK)
        .toList();
    return proposals.stream()
        .filter(proposal -> proposal.status() == BehaviorChangeProposal.Status.ACTIVATED)
        .findFirst()
        .orElse(proposals.stream().findFirst().orElse(null));
  }

  private SurfaceEnvelope agentRollbackConfirmationSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), AGENT_ADMIN_ROLLBACK_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, AGENT_ADMIN_ROLLBACK_CAPABILITY, "agent_admin.rollback_confirmation.v1", correlationId);
    var requestedAgentId = stringInput(input, "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID);
    var candidate = rollbackCandidate(actor, input);
    var proposalId = candidate == null ? "" : candidate.proposalId();
    var agentId = candidate == null ? requestedAgentId : candidate.agentDefinitionId();
    var agent = agentBehaviorRepository.agentDefinition(actor.selectedContext().tenantId(), agentId).orElse(null);
    var traceRefs = List.of("trace-agent-definition-rollback-confirmation-" + stableSuffix(correlationId));
    var hasCandidate = candidate != null && candidate.status() == BehaviorChangeProposal.Status.ACTIVATED;
    var alreadyRolledBack = candidate != null && candidate.status() == BehaviorChangeProposal.Status.ROLLED_BACK;
    var status = alreadyRolledBack ? "already-rolled-back/no-op" : (hasCandidate ? "ready" : "approval-required");
    var disabledReason = hasCandidate ? null : (alreadyRolledBack ? "Rollback has already been recorded for this activated proposal; repeated confirmation is idempotent and performs no source-artifact deletion." : "Rollback requires backend-visible activated proposal metadata, target-version validity, and backend command authority.");
    return envelope("surface-agent-rollback-confirmation", "lifecycle-confirmation", "Confirm agent behavior rollback", actor, correlationId,
        mapOf(
            "surfaceContract", "agent_admin.rollback_confirmation.v1",
            "recordId", agentId,
            "recordLabel", agent == null ? "Managed agent" : agent.displayName(),
            "lifecycleAction", "rollback",
            "currentStatus", hasCandidate ? "activated_proposal" : status,
            "proposedStatus", "previous_active_version",
            "impactSummary", "Rollback restores a prior active behavior snapshot only after backend metadata, authorization, idempotency, target-version, active-task, and trace checks pass; no source artifacts or tenant overrides are deleted.",
            "approvalState", hasCandidate ? "rollback allowed with confirmation" : "rollback metadata required",
            "policyBasis", "managed-agent-governance rollback policy",
            "idempotencyKeyHint", "client-generated key required for confirmation attempts",
            "disabledReason", disabledReason,
            "rollbackSummary", mapOf("surfaceId", "surface-agent-rollback-confirmation", "title", "Confirm agent behavior rollback", "type", "lifecycle-confirmation", "contract", "agent_admin.rollback_confirmation.v1", "selectedManagedAgentDisplayName", agent == null ? "Managed agent" : agent.displayName(), "currentLifecycleState", hasCandidate ? "activated" : status, "activeBehaviorVersionLabel", candidate == null ? "no activated proposal selected" : candidate.targetArtifact().name().toLowerCase(Locale.ROOT) + ":activated", "proposedRollbackTargetLabel", candidate == null ? "prior approved version unavailable" : "prior approved " + candidate.targetArtifact().name().toLowerCase(Locale.ROOT) + " snapshot", "rollbackReasonCategory", "admin-supplied", "lastActivatedAt", candidate == null || candidate.activatedAt() == null ? null : candidate.activatedAt().toString(), "lastReviewedAt", candidate == null || candidate.reviewedAt() == null ? null : candidate.reviewedAt().toString(), "lastRefreshedAt", Instant.now().toString(), "finalConfirmationNotice", "Final rollback mutation is backend-authorized only; browser controls are advisory."),
            "scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantDisplayName", actor.selectedContext().tenantId(), "organizationDisplayName", actor.selectedContext().tenantId(), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true, "visibilityDecision", hasCandidate || alreadyRolledBack ? "visible" : "no-activated-proposal"),
            "targetVersionSummary", mapOf("currentVersionLabel", candidate == null ? "unavailable" : candidate.targetArtifact().name().toLowerCase(Locale.ROOT) + ":current", "rollbackTargetLabel", candidate == null ? "unavailable" : candidate.targetArtifact().name().toLowerCase(Locale.ROOT) + ":previous", "approvalStatus", candidate == null ? "missing-activated-proposal" : candidate.status().name().toLowerCase(Locale.ROOT), "providerModelReadiness", "not_applicable_no_provider_call", "toolBoundaryImplications", "backend ToolPermissionBoundary checks remain authoritative", "rollbackAvailability", hasCandidate ? "available-after-acknowledgement-and-reason" : status, "rawArtifactBodiesOmitted", true),
            "policyAndApprovalSummary", mapOf("humanApprovalStatus", hasCandidate ? "activated-proposal-visible" : status, "requiredApproverCategory", "tenant-or-organization-admin-with-managed-agent-governance", "outstandingBlockers", hasCandidate ? List.of("explicit_acknowledgement_required", "admin_reason_required", "active_task_check_required") : List.of("activated_proposal_metadata_required", "valid_rollback_snapshot_required"), "lifecycleReadiness", status, "activatedProposalRequirement", candidate == null ? "missing" : candidate.status().name().toLowerCase(Locale.ROOT), "targetVersionValidity", hasCandidate ? "backend rollback snapshot recorded" : "not available", "providerRuntimeReadiness", "not_applicable_no_provider_success_claim", "toolBoundaryReadiness", "backend ToolPermissionBoundary checks required", "noFakeSuccess", true),
            "safeEvidenceSummary", List.of(
                mapOf("evidenceId", "activated-proposal", "label", "Activated behavior proposal", "status", candidate == null ? "missing" : candidate.status().name().toLowerCase(Locale.ROOT), "redactionNote", "proposal ids and target category only; raw prompt and evidence bodies omitted"),
                mapOf("evidenceId", "rollback-snapshot", "label", "Rollback snapshot", "status", hasCandidate ? "recorded-before-activation" : "missing-or-unavailable", "redactionNote", "artifact bodies and provider responses omitted"),
                mapOf("evidenceId", "trace", "label", "Rollback trace", "status", "recorded-on-read-and-attempt", "traceRefs", traceRefs)),
            "evidenceRefs", List.of("activated behavior proposal", "rollback snapshot", "activation audit event", "AgentRuntimeTrace"),
            "traceRefs", traceRefs,
            "actionContext", mapOf("agentDefinitionId", agentId, "proposalId", proposalId, "activatedProposalId", proposalId),
            "confirmationState", mapOf("allowedActions", List.of("refresh", "confirm", "cancel", "open-proposal", "open-trace"), "disabledActions", hasCandidate ? List.of() : List.of("confirm"), "requiredAcknowledgementText", "ROLLBACK", "acknowledgementRequired", true, "reasonRequired", true, "idempotencyState", "client-generated key required for confirmation attempts", "conflictState", hasCandidate ? "none" : "missing activated proposal or rollback target", "providerFailClosedState", "not_applicable_no_provider_success_claim", "nextSafeRecoverySurfaceId", "surface-agent-admin-detail", "noDirectMutation", true),
            "safeRedactionSummary", mapOf("rawPromptText", "omitted", "rawSkillReferenceBodies", "omitted", "providerCredentials", "omitted", "modelInternals", "omitted", "hiddenScopes", "omitted", "rawTraceEvidence", "role-gated", "loaderToolInputs", "omitted", "bearerTokens", "omitted", "internalStackTraces", "omitted", "privilegedPolicyDiagnostics", "role-gated"),
            "diagnostics", mapOf("managedAgentId", agentId, "activatedProposalId", proposalId, "activeAndRollbackArtifactVersionIds", candidate == null ? List.of() : List.of(candidate.targetArtifactId()), "capabilityIds", List.of(AGENT_ADMIN_ROLLBACK_CAPABILITY, AUDIT_TRACE_READ_CAPABILITY), "providerModelAliases", List.of("redacted-provider-alias"), "redactedTraceIds", traceRefs, "correlationId", correlationId, "idempotencyKey", "client-generated", "rowContextHash", stableSuffix(agentId + proposalId), "redactionProfile", "browser-safe-rollback-confirmation"),
            "states", List.of("loading", "ready", "submitting/confirming", "rollback-complete", "already-rolled-back/no-op", "approval-required", "acknowledgement-required", "validation-error", "conflict", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "tool-boundary-blocked", "cancel/no-mutation", "no-op", "failure"),
            "noDirectMutation", true,
            "noFakeSuccess", true,
            "redaction", "Provider secrets, raw prompts, raw skills, raw references, trace evidence, bearer tokens, and hidden authority remain omitted."),
        List.of(agentRollbackRefreshAction(), agentRollbackConfirmAction(!hasCandidate), agentRollbackCancelAction(), agentRollbackOpenProposalAction(), agentRollbackOpenTraceAction()));
  }

  private SurfaceEnvelope agentSeedImportConfirmationSurface(AuthContextResolver.ResolvedMe actor, String correlationId, int createdCount, int skippedCount) {
    return envelope("surface-agent-seed-import-confirmation", "lifecycle-confirmation", "Agent seed import confirmation", actor, correlationId,
        mapOf("surfaceContract", "agent_admin.seed_import_confirmation.v1", "status", createdCount == 0 ? "no-op" : "success", "summary", "Seed import preserved tenant customizations and only created missing starter defaults.", "createdCount", createdCount, "skippedCount", skippedCount, "safeReason", "Raw prompt, skill, reference, provider credential, and hidden override content are never browser-visible.", "traceRefs", List.of("trace-agent-seed-import-" + stableSuffix(correlationId)), "targetSurfaceId", "surface-agent-seed-material"),
        List.of(listAgentSeedMaterialAction(), openAgentTraceAction()));
  }

  private SurfaceEnvelope agentPromptRiskReviewEmptySurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), AgentAdminPromptRiskReviewService.READ_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, AgentAdminPromptRiskReviewService.READ_CAPABILITY, "agent_admin.prompt_risk_review.v1 empty/status read", correlationId);
    var traceIds = List.of("trace-agent-admin-prompt-risk-status-" + stableSuffix(correlationId));
    return envelope("surface-agent-admin-prompt-risk-review", "workflow-status", "Prompt-risk review status", actor, correlationId,
        promptRiskReviewData(actor, null, "empty", "No prompt-risk review task is selected. Start a governed review from Agent Admin before accepting or rejecting advisory findings.", traceIds, correlationId),
        promptRiskReviewActions());
  }

  private CapabilityActionResult promptRiskReviewActionResult(PromptRiskReviewTask task, String status, String message, String correlationId, AuthContextResolver.ResolvedMe actor) {
    return new CapabilityActionResult(status, message, correlationId, task.traceIds(), promptRiskReviewSurface(actor, task, correlationId));
  }

  private SurfaceEnvelope promptRiskReviewSurface(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask task, String correlationId) {
    return envelope("surface-agent-admin-prompt-risk-review", "workflow-status", "Prompt-risk review status", actor, correlationId,
        promptRiskReviewData(actor, task, task.status().name().toLowerCase(Locale.ROOT), task.summary(), task.traceIds(), correlationId),
        promptRiskReviewActions());
  }

  private Map<String, Object> promptRiskReviewData(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask task, String status, String summary, List<String> traceIds, String correlationId) {
    var blocked = task != null && task.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME;
    var completed = task != null && task.status() == PromptRiskReviewTask.Status.COMPLETED_REVIEW_REQUIRED;
    var blockerCode = task == null ? "blocked_provider_or_runtime" : firstNonBlank(task.blockerCode(), blocked ? "blocked_provider_or_runtime" : null);
    var evidenceRefs = task == null ? List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace") : task.evidenceRefs();
    var findingRefs = completed ? task.findingRefs() : List.<String>of();
    var taskStatus = task == null ? status : task.status().name().toLowerCase(Locale.ROOT);
    return mapOf(
        "surfaceContract", "agent_admin.prompt_risk_review.v1",
        "taskContract", "agent_admin.prompt_risk_review_task.v1",
        "workflowId", "agent-admin-prompt-risk-review",
        "taskId", task == null ? null : task.taskId(),
        "autonomousAgentTaskId", task == null ? null : task.autonomousAgentTaskId(),
        "status", taskStatus,
        "summary", summary,
        "reviewSummary", mapOf("surfaceId", "surface-agent-admin-prompt-risk-review", "title", "Prompt-risk review status", "type", "workflow-status", "contract", "agent_admin.prompt_risk_review.v1", "selectedManagedAgentDisplayName", "Agent Admin", "promptProposalLabel", task == null ? "No selected review" : task.proposalId(), "reviewTaskId", task == null ? null : task.taskId(), "taskStatus", taskStatus, "currentPhase", promptRiskCurrentPhase(task), "riskClass", completed ? "medium" : "not_available", "readinessCategory", blocked ? "provider-fail-closed" : (completed ? "review-completed" : "not-started-or-running"), "providerRuntimeStatus", blocked ? "blocked_provider_or_runtime" : "backend-governed", "startedAt", task == null ? null : task.createdAt().toString(), "completedAt", completed ? task.updatedAt().toString() : null, "lastRefreshedAt", Instant.now().toString(), "advisoryOnly", true, "noDirectMutation", true),
        "scopeSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", actor.selectedContext().capabilities().contains(AgentAdminPromptRiskReviewService.READ_CAPABILITY), "safeReason", "Backend resolves JWT plus selected AuthContext and redacts hidden/cross-scope review context."),
        "readinessBlocker", blockerCode == null ? null : mapOf("code", blockerCode, "category", "provider/runtime", "providerRuntimeSummary", summary, "retryEligible", true, "recoveryRoutes", List.of("surface-agent-model-refs", "surface-agent-admin-detail"), "requiredHumanReviewReason", "Do not trust or activate prompt changes without a real model-backed review or explicit human decision.", "noFakeSuccess", true),
        "progress", mapOf("percent", task == null ? 0 : task.progressPercent(), "summary", summary),
        "progressEvents", List.of(mapOf("phase", promptRiskCurrentPhase(task), "status", taskStatus, "timestamp", task == null ? Instant.now().toString() : task.updatedAt().toString(), "safeEvidenceRefs", evidenceRefs, "omittedCategoryCount", 6, "explanation", summary)),
        "riskFindings", findingRefs.stream().map(ref -> mapOf("severity", "medium", "riskArea", "prompt-risk", "affectedBehaviorCategory", "managed-agent-behavior", "safeEvidenceRef", ref, "confidence", "model-backed-when-present", "requiredHumanReviewReason", "Activation requires separate human behavior proposal review.", "recommendedNextDecisionSurface", "surface-agent-behavior-proposal")).toList(),
        "findingRefs", findingRefs,
        "recommendations", List.of(mapOf("recommendationId", "prompt-risk-human-review", "advisoryRecommendation", completed ? "human_review_required" : "wait_or_resolve_blocker", "requiredFollowUp", "surface-agent-behavior-proposal", "sourceSurfaceReturnTarget", "surface-agent-prompt-governance", "humanBehaviorProposalRequired", true, "summary", "Accepting advisory findings creates review evidence only; activation remains a separate governed behavior-change decision.")),
        "decisionState", mapOf("resultReviewStates", List.of("pending_worker_result", "completed_review_required", "result_accepted", "result_rejected", "cancelled"), "resultReviewState", task == null ? "pending_worker_result" : promptRiskResultReviewState(task), "allowedDecisions", completed ? List.of("accept", "reject") : List.of(), "disabledReasons", completed ? List.of() : List.of(firstNonBlank(blockerCode, "review-not-completed")), "acknowledgementRequired", completed, "rejectionReasonRequired", true, "idempotency", "terminal decisions are idempotent/no-op on repeated terminal state", "nextRequiredSurface", completed ? "surface-agent-behavior-proposal" : "surface-agent-admin-prompt-risk-review"),
        "noDirectMutation", true,
        "activationBlockedUntilHumanDecision", true,
        "providerFailures", blockerCode == null ? List.of() : List.of(blockerCode),
        "blockers", blockerCode == null ? List.of() : List.of(mapOf("code", blockerCode, "message", summary)),
        "requiredCapabilityId", AgentAdminPromptRiskReviewService.READ_CAPABILITY,
        "evidenceRefs", evidenceRefs,
        "artifactDeltas", task == null ? List.of() : task.proposedDeltas().stream().map(delta -> mapOf("artifactKind", delta.artifactKind().name().toLowerCase(Locale.ROOT), "artifactId", delta.artifactId(), "fromVersion", delta.fromVersion(), "toVersion", delta.toVersion(), "changeSummary", delta.changeSummary(), "redactedDiffRef", delta.redactedDiffRef())).toList(),
        "authorizedActions", promptRiskReviewActions().stream().map(SurfaceAction::actionId).toList(),
        "safeRedactionSummary", mapOf("omittedCategories", List.of("raw prompt text", "skill/reference bodies", "provider credentials", "raw provider requests/responses", "hidden scopes", "JWT/session material", "privileged policy diagnostics"), "browserSafe", true),
        "diagnostics", mapOf("capabilityIds", List.of(AgentAdminPromptRiskReviewService.START_CAPABILITY, AgentAdminPromptRiskReviewService.READ_CAPABILITY, AgentAdminPromptRiskReviewService.CANCEL_CAPABILITY, AgentAdminPromptRiskReviewService.ACCEPT_RESULT_CAPABILITY, AgentAdminPromptRiskReviewService.REJECT_RESULT_CAPABILITY), "traceIds", traceIds, "correlationId", correlationId, "redactionProfile", "agent-admin-prompt-risk-browser-safe"),
        "traceIds", traceIds,
        "traceLinks", traceIds.stream().map(traceId -> mapOf("traceId", traceId, "targetSurfaceId", "surface-agent-admin-trace", "label", "Prompt-risk trace", "summary", "Role-gated runtime evidence")).toList());
  }

  private String promptRiskCurrentPhase(PromptRiskReviewTask task) {
    if (task == null) return "not-started";
    return switch (task.status()) {
      case QUEUED -> "queued";
      case RUNNING -> "running";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "blocked-provider-or-runtime";
      case COMPLETED_REVIEW_REQUIRED -> "completed";
      case CANCELLED -> "cancelled";
      case ACCEPTED -> "accepted";
      case REJECTED -> "rejected";
    };
  }

  private List<SurfaceAction> promptRiskReviewActions() {
    return List.of(startPromptRiskReviewAction(), readPromptRiskReviewAction(), cancelPromptRiskReviewAction(), acceptPromptRiskReviewAction(), rejectPromptRiskReviewAction(), promptRiskReviewOpenSourceAction(), promptRiskReviewOpenTraceAction());
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
    authContextResolver.requireCapability(actor.selectedContext(), AUDIT_TRACE_READ_CAPABILITY);
    if (actor.selectedContext().scopeType() == ScopeType.CUSTOMER) {
      throw new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN");
    }
    authContextResolver.appendProtectedReadTrace(actor, AUDIT_TRACE_READ_CAPABILITY, "agent_admin.trace.v1", correlationId);
    var now = Instant.now();
    var traceSuffix = stableSuffix(correlationId);
    var traceIds = List.of(
        "trace-agent-admin-read-" + traceSuffix,
        "trace-agent-admin-provider-readiness-" + traceSuffix,
        "trace-agent-admin-tool-boundary-denial-" + traceSuffix,
        "trace-agent-admin-proposal-decision-" + traceSuffix,
        "trace-agent-admin-prompt-risk-" + traceSuffix);
    var events = List.of(
        mapOf("eventId", "agent-admin-trace-read", "occurredAt", now.minus(5, ChronoUnit.MINUTES).toString(), "actor", "Tenant Admin", "actorCategory", "authorized-admin", "sourceSurfaceId", "surface-agent-admin-dashboard", "sourceActionId", "action-agent-admin-open-trace", "sourceSurfaceLabel", "Agent Admin command center", "sourceActionLabel", "Open Agent Admin trace", "managedAgentLabel", "Agent Admin", "eventCategory", "read", "authorizationOutcome", "allowed", "reason", "Selected AuthContext includes audit.trace.read for Agent Admin browser-safe trace refs.", "resultStatus", "ready", "safeEvidenceRefs", List.of("AgentWorkTrace", "surface-action-envelope"), "redactionOmissionCount", 9, "traceId", traceIds.get(0), "correlationId", correlationId, "drillDownActionId", "action-agent-admin-trace-drill-down", "action", "Agent Admin trace timeline opened with raw evidence redacted."),
        mapOf("eventId", "agent-admin-provider-readiness", "occurredAt", now.minus(4, ChronoUnit.MINUTES).toString(), "actor", "Managed agent runtime", "actorCategory", "backend-service", "sourceSurfaceId", "surface-agent-model-refs", "sourceActionId", "action-agent-model-refs-refresh", "sourceSurfaceLabel", "Model references", "sourceActionLabel", "Refresh model-reference review", "managedAgentLabel", "Agent Admin", "eventCategory", "provider-readiness", "authorizationOutcome", "allowed", "reason", "Provider/model status is summarized without provider secrets or raw API errors.", "resultStatus", "provider-fail-closed", "safeEvidenceRefs", List.of("ModelConfigRef", "ProviderReadinessCheck"), "redactionOmissionCount", 8, "traceId", traceIds.get(1), "correlationId", correlationId, "drillDownActionId", "action-agent-admin-trace-drill-down", "action", "Provider/runtime readiness stayed fail-closed; no model-less success was claimed."),
        mapOf("eventId", "agent-admin-tool-boundary-denial", "occurredAt", now.minus(3, ChronoUnit.MINUTES).toString(), "actor", "ToolPermissionBoundary", "actorCategory", "policy-enforcer", "sourceSurfaceId", "surface-agent-tool-boundary-diff", "sourceActionId", "action-agent-tool-boundary-simulate", "sourceSurfaceLabel", "Tool-boundary review", "sourceActionLabel", "Simulate tool-boundary change", "managedAgentLabel", "Agent Admin", "eventCategory", "tool-boundary", "authorizationOutcome", "denied", "reason", "Side-effecting authority expansion requires separate review and approval.", "resultStatus", "approval-required", "safeEvidenceRefs", List.of("ToolPermissionBoundary", "GovernedLoaderDecision", "SkillLoadTrace"), "redactionOmissionCount", 10, "traceId", traceIds.get(2), "correlationId", correlationId, "drillDownActionId", "action-agent-admin-trace-drill-down", "action", "Tool-boundary expansion was denied and recorded without raw tool inputs or outputs."),
        mapOf("eventId", "agent-admin-proposal-decision", "occurredAt", now.minus(2, ChronoUnit.MINUTES).toString(), "actor", "Behavior proposal service", "actorCategory", "backend-service", "sourceSurfaceId", "surface-agent-behavior-proposal", "sourceActionId", "action-agent-behavior-proposal-open-trace", "sourceSurfaceLabel", "Behavior proposal", "sourceActionLabel", "Open proposal trace", "managedAgentLabel", "Agent Admin", "eventCategory", "proposal-decision", "authorizationOutcome", "allowed", "reason", "Human behavior proposal evidence is visible only as browser-safe summaries.", "resultStatus", "approval-required", "safeEvidenceRefs", List.of("BehaviorProposal", "PromptAssemblyTrace"), "redactionOmissionCount", 11, "traceId", traceIds.get(3), "correlationId", correlationId, "drillDownActionId", "action-agent-admin-trace-drill-down", "action", "Proposal evidence remained review-only; no lifecycle mutation occurred."),
        mapOf("eventId", "agent-admin-prompt-risk", "occurredAt", now.minus(1, ChronoUnit.MINUTES).toString(), "actor", "Prompt-risk review workflow", "actorCategory", "workflow", "sourceSurfaceId", "surface-agent-admin-prompt-risk-review", "sourceActionId", "action-agent-prompt-risk-review-open-trace", "sourceSurfaceLabel", "Prompt-risk review", "sourceActionLabel", "Open prompt-risk trace", "managedAgentLabel", "Agent Admin", "eventCategory", "prompt-risk-task", "authorizationOutcome", "allowed", "reason", "Prompt-risk state is exposed as advisory workflow evidence with provider/runtime blockers preserved.", "resultStatus", "blocked_provider_or_runtime", "safeEvidenceRefs", List.of("PromptRiskReviewTask", "AgentWorkTrace"), "redactionOmissionCount", 9, "traceId", traceIds.get(4), "correlationId", correlationId, "drillDownActionId", "action-agent-admin-trace-drill-down", "action", "Prompt-risk review evidence was summarized without raw prompts, provider payloads, or hidden-scope ids."));
    var actions = agentAdminTraceActions();
    return envelope("surface-agent-admin-trace", "audit-timeline", "Agent Admin traces", actor, correlationId,
        mapOf("surfaceContract", "agent_admin.trace.v1",
            "traceSummary", mapOf("surfaceId", "surface-agent-admin-trace", "title", "Agent Admin traces", "type", "audit-timeline", "contract", "agent_admin.trace.v1", "sourceSurfaceLabel", "Agent Admin trace link", "sourceActionLabel", "Open redacted trace", "selectedScopeLabel", contextLabel(actor.selectedContext().tenantId(), actor.selectedContext().customerId()), "timeRange", mapOf("from", now.minus(5, ChronoUnit.MINUTES).toString(), "to", now.toString()), "traceStatus", "ready", "resultOutcomeCategory", "redacted-agent-admin-evidence", "authorizationDecisionSummary", "Allowed by audit.trace.read for the selected AuthContext; hidden or cross-scope evidence is not enumerated.", "providerRuntimeReadinessCategory", "provider-fail-closed", "redactionProfileLabel", "agent-admin-trace-browser-safe", "eventCount", events.size(), "lastRefreshedAt", now.toString(), "timelineCompleteness", "partial-redacted"),
            "scopeSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "tenantOrganizationDisplayName", contextLabel(actor.selectedContext().tenantId(), null), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "traceReadAuthorized", true, "safeReason", "Backend resolves JWT plus selected AuthContext; cross-scope or privileged-only trace refs return no-enumeration denial instead of trace existence."),
            "timelineEvents", events,
            "events", events,
            "authorizationNarrative", "The backend rechecked audit.trace.read for this selected AuthContext, exposes only Agent Admin trace categories visible in the selected tenant/organization scope, and withholds raw event documents, hidden identifiers, provider payloads, prompt/skill/reference bodies, loader inputs, JWT/session material, and privileged policy diagnostics.",
            "redactionAndOmissions", mapOf("rawPrompts", "omitted", "skillReferenceBodies", "omitted", "providerCredentialsResponses", "omitted", "hiddenScopes", "omitted", "rawTraceDocuments", "role-gated", "loaderInputs", "omitted", "toolPayloads", "omitted", "jwtSessionMaterial", "omitted", "internalStackTraces", "omitted", "privilegedPolicyDiagnostics", "role-gated", "crossTenantCustomerIdentifiers", "omitted"),
            "redactionSummary", "Raw prompts, skill/reference bodies, provider credentials/responses, hidden scopes, raw trace documents, loader inputs, tool payloads, JWT/session material, internal stack traces, privileged policy diagnostics, and cross-tenant/customer identifiers are omitted or role-gated.",
            "omittedCategories", List.of("raw prompts", "skill/reference bodies", "provider credentials/responses", "hidden scopes", "raw trace documents", "loader inputs", "tool payloads", "JWT/session material", "internal stack traces", "privileged policy diagnostics", "cross-tenant/customer identifiers"),
            "exportState", mapOf("availability", "disabled", "disabledReason", "Redacted export requires the Audit/Trace export-request surface and acknowledgement; this Agent Admin drill-in does not return raw trace dumps.", "redactedExportProfile", "agent-admin-trace-browser-safe", "timeRangeLimit", "selected visible events only", "acknowledgementRequired", true, "targetSurfaceId", "surface-audit-trace-export-request"),
            "escalationState", mapOf("availability", "disabled", "disabledReason", "Escalation requires an Audit/Trace investigation note or escalation workflow with a human-readable reason; no managed-agent behavior is mutated from this trace surface.", "allowedTargets", List.of("Audit/Trace investigation", "Governance/Policy review"), "reasonRequired", true, "noManagedAgentMutation", true),
            "authorizedActions", actions.stream().map(this::authorizedActionMap).toList(),
            "diagnostics", mapOf("rawTraceIds", traceIds, "correlationId", correlationId, "idempotencyKey", "client-generated-for-export-or-escalation", "sourceSurfaceActionId", "backend-issued-agent-admin-trace-link", "capabilityPolicyLabels", List.of(AUDIT_TRACE_READ_CAPABILITY, "agent-admin-trace-visibility"), "actorSourceCategory", "authorized-admin-or-auditor", "managedAgentId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "redactionProfile", "agent-admin-trace-browser-safe", "omittedCategoryCount", 11, "exportEscalationRequestIds", List.of(), "actionRouting", actions.stream().map(SurfaceAction::actionId).toList()),
            "states", List.of("loading", "ready", "empty-no-visible-events", "submitting/exporting/escalating", "export-ready", "escalation-recorded", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "conflict", "validation-error", "no-op", "failure"),
            "partial", true,
            "correlationId", correlationId,
            "traceIds", traceIds,
            "noDirectMutation", true),
        actions);
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
      case "surface-my-account-personal-attention-digest-result" -> personalAttentionDigestEmptyResultSurface(actor, correlationId);
      case "surface-my-account-personal-attention-digest-blocked" -> personalAttentionDigestBlockedSurface(actor, null, correlationId);
      case "surface-my-account-open-denied" -> myAccountOpenDeniedSurface(actor, new MyAccountService.OpenWorkstreamDecision("denied", "The requested destination is unavailable or redacted for this selected context.", "surface-my-account-open-denied", MY_ACCOUNT_AGENT_ID, "Destination unavailable", null, correlationId, List.of("trace-my-account-open-denied-" + stableSuffix(correlationId)), "not_available_in_selected_context"), correlationId);
      case "surface-user-admin-dashboard", "surface-user-admin-saas-owner-dashboard", "surface-user-admin-tenant-dashboard", "surface-user-admin-customer-dashboard" -> dashboardSurface(actor, correlationId);
      case "surface-user-admin-system-message" -> {
        authContextResolver.appendProtectedReadTrace(actor, USERADMIN_VIEW_OVERVIEW, "user_admin.system_message.v1 direct recovery load", correlationId);
        yield userAdminSystemMessageSurface(actor, "direct_recovery", "User Admin recovery is available for this selected context. Choose an authorized return, refresh, or audit action; hidden targets and provider/model/tool details stay redacted.", null, correlationId);
      }
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
      case "surface-user-admin-invitation-detail" -> invitationDetailSurface(actor, (Object) null, correlationId);
      case "surface-user-admin-invitation-resend-confirmation" -> invitationResendConfirmationSurface(actor, null, correlationId);
      case "surface-user-admin-invitation-revoke-confirmation" -> invitationRevokeConfirmationSurface(actor, null, correlationId);
      case "surface-user-admin-membership-status-confirmation" -> membershipStatusConfirmationSurface(actor, null, correlationId);
      case "surface-user-admin-role-change-preview" -> roleChangePreviewSurface(actor, null, correlationId);
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
      case "surface-audit-trace-summary-review" -> auditTraceSummaryReviewNotReadySurface(actor, correlationId);
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

  private CapabilityActionResult invitationActionResult(String status, String message, String correlationId, Invitation invite, AuthContextResolver.ResolvedMe actor) {
    var traceId = "trace-useradmin-invitation-" + stableSuffix(invite.invitationId() + ":" + correlationId);
    var detail = invitationDetailSurface(actor, invite, correlationId);
    var resultStatus = Objects.equals(detail.data().get("status"), "blocked_provider_or_runtime") ? "blocked-runtime" : status;
    var resultMessage = resultStatus.equals(status) ? message : message + " Delivery is blocked by provider/outbox readiness; review the typed invitation detail surface for safe recovery.";
    return new CapabilityActionResult(resultStatus, resultMessage, correlationId, List.of(traceId), detail);
  }

  private CapabilityActionResult organizationAdminInvitationActionResult(String status, String message, String correlationId, Invitation invite, AuthContextResolver.ResolvedMe actor, ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail organizationDetail) {
    var traceId = "trace-organization-admin-invitation-" + stableSuffix(invite.invitationId() + ":" + correlationId);
    var detail = invitationDetailSurface(actor, invite, correlationId);
    detail.data().put("branchNavigation", organizationBranchNavigation(correlationId));
    detail.data().put("branchRootSurfaceId", "surface-user-admin-organization-directory");
    detail.data().put("branchReturnActionId", "action-user-admin-show-organization-admins");
    detail.data().put("branchReturnLabel", "Back to Organization Admins");
    detail.data().put("organizationId", organizationDetail.organization().organizationId());
    detail.data().put("organizationName", organizationDetail.organization().organizationName());
    detail.data().put("recordKind", "organization-admin-invitation");
    detail.data().put("targetScope", mapOf("scopeType", ScopeType.TENANT.name(), "tenantId", organizationDetail.organization().organizationId(), "organizationId", organizationDetail.organization().organizationId(), "organizationName", organizationDetail.organization().organizationName(), "source", "backend-authored-organization-admin-invitation-submit", "correlationId", correlationId, "traceRefs", organizationDetail.traceRefs()));
    detail.data().put("redaction", List.of("invitation-token-redacted", "provider-payload-redacted", "email-body-redacted", "raw-jwt-redacted", "tenant-app-data-redacted", "hidden-organization-admin-counts-redacted"));
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
      case "action-submit-organization-create", "action-organization-create" -> service.createOrganization(actor, stringInput(input, "organizationName", ""), idempotencyKey, stringInput(input, "reason", "organization-created"), correlationId);
      case "action-submit-organization-rename", "action-organization-rename" -> service.renameOrganization(actor, stringInput(input, "organizationId", stringInput(input, "recordId", "")), stringInput(input, "organizationName", ""), idempotencyKey, stringInput(input, "reason", "organization-renamed"), correlationId);
      case "action-organization-suspend" -> service.suspendOrganization(actor, stringInput(input, "organizationId", stringInput(input, "recordId", "")), rawStringInput(input, "reason", ""), stringInput(input, "confirmationPhrase", stringInput(input, "confirmation", "")), idempotencyKey, correlationId);
      case "action-organization-reactivate" -> service.reactivateOrganization(actor, stringInput(input, "organizationId", stringInput(input, "recordId", "")), rawStringInput(input, "reason", ""), stringInput(input, "confirmationPhrase", stringInput(input, "confirmation", "")), idempotencyKey, correlationId);
      default -> throw new AuthorizationException(404, "target-not-found-or-forbidden");
    };
  }

  private void requireOrganizationLifecycleAction(ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail, String action, String correlationId) {
    if (!detail.visibleActions().contains(action)) {
      throw new AuthorizationException(409, "organization-lifecycle-action-unavailable:" + action + ":" + correlationId);
    }
  }

  private Map<String, Object> organizationSummaryMap(ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationSummary organization) {
    return mapOf("organizationId", organization.organizationId(), "organizationName", organization.organizationName(), "status", organization.status(), "safeLifecycleSummary", organization.status().equals("active") ? "Active Tenant boundary" : "Suspended Tenant boundary", "attentionBadges", List.of(), "visibleTenantAdminCount", null, "pendingAdminInvitationCount", null, "actionAvailability", organization.status().equals("active") ? List.of("open-detail", "rename", "suspend") : List.of("open-detail", "rename", "reactivate"), "targetSurfaceId", "surface-user-admin-organization-detail", "targetSurfaceType", "show-inspection", "targetActionId", "action-organization-read", "openActionId", "action-organization-read", "traceRefs", organization.traceRefs(), "redactionState", "tenant-app-data-redacted");
  }

  private Map<String, Object> organizationDetailMap(ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService.OrganizationDetail detail) {
    return mapOf("organizationId", detail.organization().organizationId(), "organizationName", detail.organization().organizationName(), "status", detail.organization().status(), "safeBoundaryNotice", detail.safeBoundaryNotice(), "visibleActions", detail.visibleActions(), "recentAuditEvents", List.of(), "traceRefs", detail.traceRefs(), "correlationId", detail.correlationId());
  }

  private Map<String, Object> surfaceActionSummary(SurfaceAction action) {
    return mapOf("actionId", action.actionId(), "label", action.label(), "purpose", action.intent(), "governedCapability", action.capabilityId(), "governedToolId", action.governedToolId(), "targetSurfaceId", action.resultSurface() == null ? null : action.resultSurface().updateSurfaceId(), "openPlacement", action.resultSurface() == null ? null : action.resultSurface().openPlacement(), "enabled", action.disabled() == null, "disabledReason", action.disabled() == null ? null : action.disabled().message(), "requiresConfirmation", action.requiresConfirmation(), "requiresApproval", action.requiresApproval(), "idempotency", action.idempotency() == null ? null : action.idempotency().keySource());
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
      case "action-audit-trace-start-summary-task", "action-audit-trace-summary-task-start", "action-audit-trace-summary-task-read" -> auditTraceSummaryTaskBlockedSurface(actor, correlationId);
      case "action-audit-trace-summary-review", "action-audit-trace-summary-accept", "action-audit-trace-summary-reject" -> auditTraceSummaryReviewNotReadySurface(actor, correlationId);
      case "action-show-my-account-dashboard" -> myAccountDashboardSurface(actor, correlationId);
      case "action-show-my-profile", "action-update-my-profile" -> myProfileSurface(actor, correlationId);
      case "action-show-my-settings", "action-update-my-settings" -> mySettingsSurface(actor, correlationId);
      case "action-show-my-context", "action-select-my-context" -> myContextSurface(actor, correlationId);
      case "action-show-my-account-notification-center", "action-notification-mark-read", "action-notification-dismiss", "action-notification-archive", "action-notification-snooze", "action-notification-update-preferences" -> myAccountNotificationCenterSurface(actor, correlationId);
      case "action-sign-out" -> myAccountDashboardSurface(actor, correlationId);
      case "action-start-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-read-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-cancel-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-accept-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-reject-my-account-personal-attention-digest" -> personalAttentionDigestEmptyProgressSurface(actor, correlationId);
      case "action-open-user-admin", "action-user-admin-return-dashboard" -> dashboardSurface(actor, correlationId);
      case "action-open-agent-admin", "action-display-agent-admin-dashboard", "action-agent-admin-refresh-dashboard" -> agentAdminDashboardSurface(actor, correlationId);
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
      case "action-display-agent-catalog", "action-agent-admin-open-catalog", "action-agent-admin-refresh-catalog", "action-agent-admin-search-catalog", "action-agent-admin-reset-catalog-filters", "action-agent-detail-back-to-catalog" -> agentAdminCatalogSurface(actor, correlationId);
      case "action-open-agent-detail", "action-agent-detail-refresh", "action-agent-prompt-governance-back-to-detail", "action-agent-skill-manifest-back-to-detail", "action-agent-tool-boundary-back-to-detail", "action-agent-model-refs-back-to-detail", "action-agent-test-console-back-to-detail" -> agentAdminDetailSurface(actor, correlationId);
      case "action-agent-admin-catalog-open-trace", "action-agent-detail-open-trace" -> agentAdminTraceSurface(actor, correlationId);
      case "action-activate-agent-definition", "action-agent-detail-open-activation", "action-agent-activation-refresh", "action-agent-activation-confirm" -> agentLifecycleConfirmationSurface(actor, correlationId, "activate", false);
      case "action-agent-activation-cancel" -> agentAdminDetailSurface(actor, correlationId);
      case "action-agent-activation-open-proposal" -> agentBehaviorProposalSurface(actor, correlationId);
      case "action-agent-activation-open-trace" -> agentAdminTraceSurface(actor, correlationId);
      case "action-deactivate-agent-definition", "action-agent-detail-open-deactivation", "action-agent-deactivation-refresh" -> agentLifecycleConfirmationSurface(actor, correlationId, "deactivate", false);
      case "action-agent-deactivation-confirm", "action-agent-deactivation-cancel" -> agentAdminDetailSurface(actor, correlationId);
      case "action-agent-deactivation-open-proposal" -> agentBehaviorProposalSurface(actor, correlationId);
      case "action-agent-deactivation-open-trace" -> agentAdminTraceSurface(actor, correlationId);
      case "action-agent-detail-open-rollback", "action-agent-rollback-refresh" -> agentRollbackConfirmationSurface(actor, correlationId);
      case "action-agent-rollback-confirm", "action-agent-rollback-cancel" -> agentAdminDetailSurface(actor, correlationId);
      case "action-agent-rollback-open-proposal" -> agentBehaviorProposalSurface(actor, correlationId);
      case "action-agent-rollback-open-trace" -> agentAdminTraceSurface(actor, correlationId);
      case "action-import-agent-seed-defaults" -> agentSeedImportConfirmationSurface(actor, correlationId, 0, 0);
      case "action-agent-seed-material-refresh", "action-agent-seed-material-search", "action-agent-seed-material-reset-filters", "action-agent-seed-material-open-provenance", "action-agent-seed-material-prepare-import", "action-agent-seed-material-start-import", "action-agent-seed-material-cancel-import" -> agentSeedMaterialSurface(actor, correlationId);
      case "action-agent-seed-material-open-agent-detail" -> agentAdminDetailSurface(actor, correlationId);
      case "action-propose-prompt-diff", "action-agent-detail-open-prompt-governance", "action-agent-prompt-governance-refresh", "action-agent-model-refs-open-prompt-governance" -> agentPromptGovernanceSurface(actor, correlationId);
      case "action-test-agent-prompt", "action-agent-detail-run-test", "action-agent-prompt-governance-simulate", "action-agent-skill-manifest-simulate", "action-agent-tool-boundary-simulate", "action-agent-model-refs-run-test", "action-agent-test-console-refresh", "action-agent-test-console-run", "action-agent-test-console-read-result", "action-agent-test-console-open-proposal" -> agentTestConsoleSurface(actor, correlationId);
      case "action-approve-skill-manifest", "action-agent-admin-open-manifest-drift", "action-agent-detail-open-skill-manifest", "action-agent-skill-manifest-refresh" -> agentSkillManifestSurface(actor, correlationId);
      case "action-simulate-tool-boundary", "action-agent-admin-open-tool-boundary", "action-agent-detail-open-tool-boundary", "action-agent-skill-manifest-open-tool-boundary", "action-agent-tool-boundary-refresh", "action-agent-model-refs-open-tool-boundary" -> agentToolBoundarySurface(actor, correlationId);
      case "action-manage-model-ref", "action-agent-admin-open-model-refs", "action-agent-detail-open-model-refs", "action-agent-skill-manifest-open-model-refs", "action-agent-tool-boundary-open-model-refs", "action-agent-model-refs-refresh" -> agentModelRefsSurface(actor, correlationId);
      case "action-list-agent-seed-material", "action-agent-admin-open-seed-material" -> agentSeedMaterialSurface(actor, correlationId);
      case "action-submit-behavior-change", "action-agent-prompt-governance-submit-review", "action-agent-prompt-governance-approve", "action-agent-prompt-governance-reject", "action-agent-skill-manifest-submit-review", "action-agent-skill-manifest-approve", "action-agent-skill-manifest-reject", "action-agent-tool-boundary-submit-review", "action-agent-tool-boundary-approve", "action-agent-tool-boundary-reject", "action-agent-model-refs-submit-review", "action-agent-model-refs-approve", "action-agent-model-refs-reject", "action-agent-admin-open-behavior-proposals", "action-agent-behavior-proposal-refresh", "action-agent-behavior-proposal-submit", "action-agent-behavior-proposal-approve", "action-agent-behavior-proposal-reject", "action-agent-behavior-proposal-defer" -> agentBehaviorProposalSurface(actor, correlationId);
      case "action-agent-behavior-proposal-cancel" -> agentAdminDetailSurface(actor, correlationId);
      case "action-agent-behavior-proposal-open-activation" -> agentLifecycleConfirmationSurface(actor, correlationId, "activate", false);
      case "action-agent-behavior-proposal-open-rollback" -> agentRollbackConfirmationSurface(actor, correlationId);
      case "action-agent-behavior-proposal-open-source" -> agentPromptGovernanceSurface(actor, correlationId);
      case "action-agent-admin-open-trace", "action-agent-prompt-governance-open-trace", "action-agent-skill-manifest-open-trace", "action-agent-tool-boundary-open-trace", "action-agent-model-refs-open-trace", "action-agent-test-console-open-trace", "action-agent-behavior-proposal-open-trace", "action-agent-prompt-risk-review-open-trace", "action-agent-seed-material-open-trace", "action-agent-admin-trace-refresh", "action-agent-admin-trace-drill-down", "action-agent-admin-trace-export", "action-agent-admin-trace-escalate" -> agentAdminTraceSurface(actor, correlationId);
      case "action-agent-admin-trace-back-to-source", "action-agent-prompt-risk-review-open-source" -> agentPromptGovernanceSurface(actor, correlationId);
      case "action-agent-seed-material-back-to-source" -> agentAdminCatalogSurface(actor, correlationId);
      case "action-agent-prompt-risk-review-start", "action-agent-prompt-risk-review-read", "action-agent-prompt-risk-review-cancel", "action-agent-prompt-risk-review-accept", "action-agent-prompt-risk-review-reject", "action-agentadmin-start-prompt-risk-review", "action-agentadmin-read-prompt-risk-review", "action-agentadmin-cancel-prompt-risk-review", "action-agentadmin-accept-prompt-risk-review-result", "action-agentadmin-reject-prompt-risk-review-result", "action-agent-admin-open-prompt-risk-review", "action-agent-detail-open-prompt-risk-review", "action-agent-prompt-governance-open-risk-review" -> agentPromptRiskReviewEmptySurface(actor, correlationId);
      case "action-user-admin-show-saas-owner-admins" -> saasOwnerAdminsSurface(actor, correlationId);
      case "action-open-saas-owner-admin-invitation-create" -> saasOwnerAdminInvitationCreateSurface(actor, correlationId);
      case "action-submit-saas-owner-admin-invitation" -> invitationDetailSurface(actor, null, correlationId);
      case "action-display-organization-admin", "action-user-admin-show-organizations" -> organizationAdminSurface(actor, correlationId);
      case "action-open-organization-admin-invitation-create" -> organizationAdminInvitationCreateSurface(actor, correlationId);
      case "action-open-organization-admin-detail", "action-open-organization-admin-invitation-detail" -> organizationAdminDetailSurface(actor, correlationId);
      case "action-user-admin-show-customers" -> customerDirectorySurface(actor, correlationId);
      case "action-customer-read" -> customerDetailSurface(actor, correlationId);
      case "action-open-customer-create" -> customerCreateSurface(actor, correlationId);
      case "action-open-customer-rename" -> customerRenameSurface(actor, correlationId);
      case "action-open-customer-suspend", "action-open-customer-suspend-confirmation" -> customerSuspendSurface(actor, correlationId);
      case "action-open-customer-reactivate", "action-open-customer-reactivate-confirmation" -> customerReactivateSurface(actor, correlationId);
      case "action-user-admin-show-customer-admins" -> customerAdminsSurface(actor, correlationId);
      case "action-open-customer-admin-invitation-create" -> customerAdminInvitationCreateSurface(actor, correlationId);
      case "action-open-customer-admin-detail", "action-open-customer-admin-invitation-detail" -> customerAdminDetailSurface(actor, correlationId);
      case "action-customer-admin-invite" -> invitationDetailSurface(actor, (Object) null, correlationId);
      case "action-submit-customer-create", "action-customer-create", "action-submit-customer-rename", "action-customer-rename", "action-customer-suspend", "action-customer-reactivate" -> customerDetailSurface(actor, correlationId);
      case "action-submit-organization-create", "action-organization-create" -> organizationDetailSurface(actor, correlationId);
      case "action-open-organization-create" -> organizationCreateSurface(actor, correlationId);
      case "action-open-organization-rename" -> organizationRenameSurface(actor, correlationId);
      case "action-open-organization-suspend" -> organizationSuspendSurface(actor, correlationId);
      case "action-open-organization-reactivate" -> organizationReactivateSurface(actor, correlationId);
      case "action-display-user-detail", "action-replace-membership-role", "action-useradmin-preview-role-change", "action-useradmin-change-member-roles", "action-useradmin-disable-account", "action-useradmin-reactivate-account" -> detailSurface(actor, correlationId);
      case "action-open-useradmin-invitation-create" -> invitationCreateSurface(actor, null, correlationId);
      case "action-display-invitation-detail", "action-invite-user" -> invitationDetailSurface(actor, (Object) null, correlationId);
      case "action-open-useradmin-invitation-resend-confirmation" -> invitationResendConfirmationSurface(actor, null, correlationId);
      case "action-open-useradmin-invitation-revoke-confirmation" -> invitationRevokeConfirmationSurface(actor, null, correlationId);
      case "action-useradmin-resend-invitation", "action-useradmin-revoke-invitation" -> invitationDetailSurface(actor, (Object) null, correlationId);
      case "action-open-useradmin-membership-status-confirmation" -> membershipStatusConfirmationSurface(actor, null, correlationId);
      case "action-open-useradmin-support-access-grant" -> supportAccessGrantSurface(actor, null, correlationId);
      case "action-open-useradmin-support-access-revoke-confirmation", "action-open-user-admin-support-access-revoke-confirmation" -> supportAccessRevokeConfirmationSurface(actor, null, correlationId);
      case "action-open-useradmin-identity-exception-review", "action-useradmin-request-identity-relink", "action-useradmin-read-identity-relink", "action-useradmin-approve-identity-relink", "action-useradmin-deny-identity-relink", "action-useradmin-complete-identity-relink" -> identityExceptionReviewSurface(actor, null, correlationId);
      case "action-useradmin-disable-member", "action-useradmin-reactivate-member", "action-useradmin-permanently-remove-user", "action-useradmin-read-support-access", "action-useradmin-grant-support-access", "action-useradmin-revoke-support-access", "action-useradmin-extend-support-access", "action-display-user-list", "action-user-admin-show-users" -> listSurface(actor, correlationId);
      case "action-useradmin-start-access-review", "action-useradmin-read-access-review", "action-useradmin-cancel-access-review", "action-useradmin-accept-access-review-result", "action-useradmin-reject-access-review-result" -> accessReviewBlockedSurface(actor, correlationId);
      default -> dashboardSurface(actor, correlationId);
    };
  }

  private SurfaceAction actionById(String actionId) {
    return List.of(showDashboardAction(), showNotificationCenterAction(), markNotificationReadAction(), dismissNotificationAction(), archiveNotificationAction(), snoozeNotificationAction(), updateNotificationPreferencesAction(), showProfileAction(), showSettingsAction(), showContextAction(), selectContextAction(), updateProfileAction(), updateSettingsAction(), signOutAction(), startPersonalAttentionDigestAction(), readPersonalAttentionDigestAction(), cancelPersonalAttentionDigestAction(), acceptPersonalAttentionDigestAction(), rejectPersonalAttentionDigestAction(), openUserAdminAction(), userAdminReturnDashboardAction(), openAgentAdminAction(), openGovernancePolicyAction(), showSaasOwnerAdminsAction(), openSaasOwnerAdminInvitationCreateAction(), submitSaasOwnerAdminInvitationAction(), displayOrganizationAdminAction(), showOrganizationsAction(), openOrganizationCreateAction(), openOrganizationRenameAction(), openOrganizationSuspendAction(), openOrganizationReactivateAction(), organizationListAction(), organizationReadAction(), submitOrganizationCreateAction(), organizationCreateAction(), submitOrganizationRenameAction(), organizationRenameAction(), organizationSuspendAction(), organizationReactivateAction(), showOrganizationAdminsAction(), openOrganizationAdminDetailAction(), openOrganizationAdminInvitationDetailAction(), openOrganizationAdminInvitationCreateAction(), organizationAdminInviteAction(), showCustomersAction(), customerReadAction(), openCustomerCreateAction(), openCustomerRenameAction(), openCustomerSuspendAction(), openCustomerSuspendConfirmationAction(), openCustomerReactivateAction(), openCustomerReactivateConfirmationAction(), submitCustomerCreateAction(), customerCreateAction(), submitCustomerRenameAction(), customerRenameAction(), customerSuspendAction(), customerReactivateAction(), showCustomerAdminsAction(), openCustomerAdminDetailAction(), openCustomerAdminInvitationDetailAction(), openCustomerAdminInvitationCreateAction(), customerAdminInviteAction(), displayListAction(), showUsersAction(), displayDetailAction(), displayInvitationDetailAction(), openInvitationCreateAction(), openUserAdminInvitationCreateAction(), openInvitationResendConfirmationAction(), openInvitationRevokeConfirmationAction(), openUserAdminInvitationRevokeConfirmationAction(), openMembershipStatusConfirmationAction(), openUserAdminMembershipStatusConfirmationAction(), confirmMembershipStatusChangeAction(), openSupportAccessGrantAction(), openUserAdminSupportAccessGrantAction(), openSupportAccessRevokeConfirmationAction(), openUserAdminSupportAccessRevokeConfirmationAction(), openIdentityExceptionReviewAction(), requestIdentityRelinkAction(), readIdentityRelinkAction(), approveIdentityRelinkAction(), denyIdentityRelinkAction(), completeIdentityRelinkAction(), inviteAction(), resendInvitationAction(), revokeInvitationAction(), confirmUserAdminInvitationRevokeAction(), updateMemberStatusAction(), reactivateMemberStatusAction(), permanentlyRemoveUserAction(), disableAccountAction(), reactivateAccountAction(), readSupportAccessAction(), validateSupportAccessGrantAction(), submitSupportAccessGrantAction(), grantSupportAccessAction(), revokeSupportAccessAction(), confirmUserAdminSupportAccessRevokeAction(), extendSupportAccessAction(), previewRoleChangeAction(), openUserAdminRoleChangePreviewAction(), reviseUserAdminRoleChangeAction(), commitUserAdminRoleChangeAction(), changeMemberRolesAction(), startAccessReviewAction(), readAccessReviewAction(), cancelAccessReviewAction(), acceptAccessReviewResultAction(), rejectAccessReviewResultAction(), deniedReplaceRoleAction(), submitUserAdminInvitationAction(), traceAction(), openAuditAction(), auditTraceDashboardAction(), auditTraceSearchAction(), auditTraceDetailAction(), auditTraceTimelineAction(), auditTraceFailureEvidenceAction(), auditTraceInvestigationGuideAction(), auditTraceExportRequestAction(), auditTraceAppendInvestigationNoteAction(), auditTraceSummaryTaskBlockedAction(), auditTraceSummaryTaskReadAction(), auditTraceSummaryReviewAction(), auditTraceSummaryAcceptAction(), auditTraceSummaryRejectAction(), governanceDashboardAction(), governanceListPoliciesAction(), governanceReadPolicyAction(), governanceDraftProposalAction(), governanceSubmitProposalAction(), governanceSimulateProposalAction(), governanceDecideProposalAction(), governanceActivateProposalAction(), governanceRollbackPolicyAction(), governanceOutcomeNoteAction(), governanceStartImpactAnalysisAction(), governanceReadImpactAnalysisAction(), governanceCancelImpactAnalysisAction(), governanceAcceptImpactResultAction(), governanceRejectImpactResultAction(), governanceRequestImpactChangesAction(), simulatePolicyAction(), commitPolicyAction(), agentAdminRefreshDashboardAction(), agentAdminOpenCatalogAction(), agentAdminRefreshCatalogAction(), agentAdminSearchCatalogAction(), agentAdminResetCatalogFiltersAction(), agentAdminCatalogOpenTraceAction(), agentAdminOpenBehaviorProposalsAction(), agentAdminOpenPromptRiskReviewAction(), agentAdminOpenSeedMaterialAction(), agentAdminOpenManifestDriftAction(), agentAdminOpenToolBoundaryAction(), agentAdminOpenModelRefsAction(), agentAdminOpenTraceAction(), displayAgentAdminDashboardAction(), displayAgentCatalogAction(), openAgentDetailAction(), agentDetailRefreshAction(), agentDetailOpenPromptGovernanceAction(), agentDetailOpenSkillManifestAction(), agentDetailOpenToolBoundaryAction(), agentDetailOpenModelRefsAction(), agentDetailRunTestAction(), agentDetailOpenPromptRiskReviewAction(), agentDetailOpenActivationAction(), agentDetailOpenDeactivationAction(), agentDetailOpenRollbackAction(), agentDetailOpenTraceAction(), agentDetailBackToCatalogAction(), activateAgentDefinitionAction(), agentActivationRefreshAction(), agentActivationConfirmAction(), agentActivationCancelAction(), agentActivationOpenProposalAction(), agentActivationOpenTraceAction(), deactivateAgentDefinitionAction(), agentDeactivationRefreshAction(), agentDeactivationConfirmAction(false), agentDeactivationCancelAction(), agentDeactivationOpenProposalAction(), agentDeactivationOpenTraceAction(), agentRollbackRefreshAction(), agentRollbackConfirmAction(false), agentRollbackCancelAction(), agentRollbackOpenProposalAction(), agentRollbackOpenTraceAction(), importAgentSeedDefaultsAction(), agentSeedMaterialRefreshAction(), agentSeedMaterialSearchAction(), agentSeedMaterialResetFiltersAction(), agentSeedMaterialOpenProvenanceAction(), agentSeedMaterialPrepareImportAction(), agentSeedMaterialStartImportAction(), agentSeedMaterialCancelImportAction(), agentSeedMaterialOpenAgentDetailAction(), agentSeedMaterialOpenTraceAction(), agentSeedMaterialBackToSourceAction(), proposePromptDiffAction(), testPromptAction(), agentTestConsoleRefreshAction(), agentTestConsoleRunAction(), agentTestConsoleReadResultAction(), agentTestConsoleOpenProposalAction(false), agentTestConsoleOpenTraceAction(), agentTestConsoleBackToDetailAction(), agentPromptGovernanceRefreshAction(), agentPromptGovernanceSimulateAction(), agentPromptGovernanceSubmitReviewAction(), agentPromptGovernanceApproveAction(), agentPromptGovernanceRejectAction(), agentPromptGovernanceOpenRiskReviewAction(), agentPromptGovernanceOpenTraceAction(), agentPromptGovernanceBackToDetailAction(), agentSkillManifestRefreshAction(), agentSkillManifestSimulateAction(), agentSkillManifestSubmitReviewAction(), agentSkillManifestApproveAction(), agentSkillManifestRejectAction(), agentSkillManifestOpenToolBoundaryAction(), agentSkillManifestOpenModelRefsAction(), agentSkillManifestOpenTraceAction(), agentSkillManifestBackToDetailAction(), agentToolBoundaryRefreshAction(), agentToolBoundarySimulateAction(), agentToolBoundarySubmitReviewAction(), agentToolBoundaryApproveAction(), agentToolBoundaryRejectAction(), agentToolBoundaryOpenModelRefsAction(), agentToolBoundaryOpenTraceAction(), agentToolBoundaryBackToDetailAction(), agentModelRefsRefreshAction(), agentModelRefsRunTestAction(true), agentModelRefsSubmitReviewAction(), agentModelRefsApproveAction(), agentModelRefsRejectAction(), agentModelRefsOpenPromptGovernanceAction(), agentModelRefsOpenToolBoundaryAction(), agentModelRefsOpenTraceAction(), agentModelRefsBackToDetailAction(), agentBehaviorProposalRefreshAction(), agentBehaviorProposalSubmitAction(false), agentBehaviorProposalApproveAction(false), agentBehaviorProposalRejectAction(false), agentBehaviorProposalDeferAction(false), agentBehaviorProposalCancelAction(false), agentBehaviorProposalOpenActivationAction(false), agentBehaviorProposalOpenRollbackAction(false), agentBehaviorProposalOpenSourceAction(), agentBehaviorProposalOpenTraceAction(), approveSkillManifestAction(), submitBehaviorChangeAction(), rejectBehaviorChangeAction(), activateBehaviorChangeAction(), cancelBehaviorChangeAction(), rollbackBehaviorChangeAction(), simulateToolBoundaryAction(), manageModelRefAction(), listAgentSeedMaterialAction(), startPromptRiskReviewAction(), readPromptRiskReviewAction(), cancelPromptRiskReviewAction(), acceptPromptRiskReviewAction(), rejectPromptRiskReviewAction(), promptRiskReviewOpenSourceAction(), promptRiskReviewOpenTraceAction(), agentAdminTraceRefreshAction(), agentAdminTraceDrillDownAction(), agentAdminTraceExportAction(), agentAdminTraceEscalateAction(), agentAdminTraceBackToSourceAction(), legacyPromptRiskReviewAction("action-agentadmin-start-prompt-risk-review", startPromptRiskReviewAction()), legacyPromptRiskReviewAction("action-agentadmin-read-prompt-risk-review", readPromptRiskReviewAction()), legacyPromptRiskReviewAction("action-agentadmin-cancel-prompt-risk-review", cancelPromptRiskReviewAction()), legacyPromptRiskReviewAction("action-agentadmin-accept-prompt-risk-review-result", acceptPromptRiskReviewAction()), legacyPromptRiskReviewAction("action-agentadmin-reject-prompt-risk-review-result", rejectPromptRiskReviewAction()), openAgentTraceAction()).stream().filter(action -> actionId.equals(action.actionId())).findFirst().orElse(null);
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
  private SurfaceAction userAdminReturnDashboardAction() { return new SurfaceAction("action-user-admin-return-dashboard", "Return to User Admin dashboard", "surface-request", "user-admin.return-dashboard", "search-user-directory", USER_ADMIN_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-dashboard", "inline"), new Audit("UserAdminDashboardReturned", true)); }
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
  private SurfaceAction selectContextAction() { return new SurfaceAction("action-select-my-context", "Select authorized context", "surface-request", browserToolId("action-select-my-context"), governedToolId(CORE_ACCESS_CONTEXT_SELECT_CAPABILITY), CORE_ACCESS_CONTEXT_SELECT_CAPABILITY, "schema.core.access.context.select.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-my-context", "inline"), new Audit("AuthContextSelectRequested", true)); }
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
        .filter(key -> !List.of("displayName", "preferredThemeId", "locale", "timeZone").contains(key))
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
    var locale = input.get("locale") instanceof String value ? value : null;
    var timeZone = input.get("timeZone") instanceof String value ? value : null;
    try {
      return myAccountService.updateProfileSettings(actor, displayName, themeId, locale, timeZone, request.idempotencyKey(), request.correlationId());
    } catch (IllegalArgumentException invalidPreference) {
      throw new AuthorizationException(400, "MY_ACCOUNT_INVALID_PREFERENCE:" + invalidPreference.getMessage());
    }
  }

  private CapabilityActionResult validateSupportAccessGrant(AuthContextResolver.ResolvedMe actor, CapabilityActionRequest request) {
    var validation = new ArrayList<String>();
    var purpose = stringInput(request.input(), "purpose", stringInput(request.input(), "reason", ""));
    if (purpose.isBlank()) validation.add("Purpose is required for support-access grant or extension.");
    var expiryHours = supportAccessExpiryHours(request.input());
    if (expiryHours <= 0 || expiryHours > 8) validation.add("Expiry must be one of the backend-authorized support-access durations and no more than 8 hours.");
    var surface = supportAccessGrantSurface(actor, request.input(), request.correlationId());
    surface.data().put("validationMessages", validation);
    surface.data().put("lastResult", mapOf("status", validation.isEmpty() ? "validated" : "validation-error", "message", validation.isEmpty() ? "Support-access grant request is valid for backend submission." : "Support-access grant request needs correction before submission.", "traceRefs", List.of("trace-useradmin-support-access-grant-validation-" + stableSuffix(request.correlationId()))));
    return new CapabilityActionResult(validation.isEmpty() ? "accepted" : "validation-error", validation.isEmpty() ? "Support-access grant request is valid for backend submission." : "Support-access grant request needs correction before submission.", request.correlationId(), List.of("trace-useradmin-support-access-grant-validation-" + stableSuffix(request.correlationId())), surface);
  }

  private CapabilityActionResult updateSupportAccess(AuthContextResolver.ResolvedMe actor, CapabilityActionRequest request) {
    var enabled = !("action-useradmin-revoke-support-access".equals(request.actionId()) || "action-confirm-user-admin-support-access-revoke".equals(request.actionId()));
    var purpose = stringInput(request.input(), "purpose", stringInput(request.input(), "reason", ""));
    if (enabled && purpose.isBlank()) {
      var surface = supportAccessGrantSurface(actor, request.input(), request.correlationId());
      surface.data().put("validationMessages", List.of("Purpose is required for support-access grant or extension."));
      surface.data().put("lastResult", mapOf("status", "validation-error", "message", "Purpose is required before support access can be granted.", "traceRefs", List.of("trace-useradmin-support-access-validation-denied-" + stableSuffix(request.correlationId()))));
      return new CapabilityActionResult("validation-error", "Purpose is required before support access can be granted.", request.correlationId(), List.of("trace-useradmin-support-access-validation-denied-" + stableSuffix(request.correlationId())), surface);
    }
    var expiresAt = enabled ? Instant.now().plus(supportAccessExpiryHours(request.input()), ChronoUnit.HOURS) : null;
    var changed = userAdminService.updateSupportAccess(actor, stringInput(request.input(), "membershipId", actor.selectedContext().membershipId()), enabled, expiresAt, enabled ? purpose : stringInput(request.input(), "reason", "workstream support access revoke"), request.idempotencyKey(), request.correlationId());
    var detail = detailSurface(actor, request.input(), request.correlationId());
    detail.data().put("lastResult", mapOf("status", "accepted", "message", enabled ? "Support access granted or extended by backend-authoritative User Admin capability." : "Support access revoked by backend-authoritative User Admin capability.", "membershipId", changed.membershipId(), "supportAccess", changed.supportAccess(), "expiresAt", changed.expiresAt() == null ? null : changed.expiresAt().toString(), "traceRefs", List.of("trace-useradmin-support-access-" + stableSuffix(request.idempotencyKey()))));
    return new CapabilityActionResult("accepted", enabled ? "Support access granted or extended by backend-authoritative User Admin capability." : "Support access revoked by backend-authoritative User Admin capability.", request.correlationId(), List.of("trace-useradmin-support-access-" + stableSuffix(request.idempotencyKey())), detail);
  }

  private long supportAccessExpiryHours(Object input) {
    var raw = stringInput(input, "expiryHours", stringInput(input, "expiry", "8"));
    try {
      var parsed = Long.parseLong(raw.replaceAll("[^0-9]", ""));
      return parsed <= 0 ? 8 : Math.min(parsed, 8);
    } catch (NumberFormatException invalid) {
      return 8;
    }
  }

  private SurfaceAction showSaasOwnerAdminsAction() { return new SurfaceAction("action-user-admin-show-saas-owner-admins", "Show SaaS Owner Admins", "surface-request", "user-admin.show-saas-owner-admins", "manage-saas-owner-admins", SAAS_OWNER_ADMIN_LIST_CAPABILITY, "schema.saas-owner-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-saas-owner-admins", "inline"), new Audit("SaasOwnerAdminsDisplayed", true)); }
  private SurfaceAction openSaasOwnerAdminInvitationCreateAction() { return new SurfaceAction("action-open-saas-owner-admin-invitation-create", "Invite SaaS Owner Admin", "surface-request", "user-admin.open-saas-owner-admin-invite", "manage-saas-owner-admins", SAAS_OWNER_ADMIN_INVITE_CAPABILITY, "schema.saas-owner-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-saas-owner-admin-invitation-create", "inline"), new Audit("SaasOwnerAdminInvitationCreateDisplayed", true)); }
  private SurfaceAction submitSaasOwnerAdminInvitationAction() { return new SurfaceAction("action-submit-saas-owner-admin-invitation", "Invite SaaS Owner Admin", "command", "user-admin.submit-saas-owner-admin-invite", "manage-saas-owner-admins", SAAS_OWNER_ADMIN_INVITE_CAPABILITY, "schema.saas-owner-admin.invitation-create.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("SaasOwnerAdminInvitationRequested", true)); }
  private SurfaceAction displayOrganizationAdminAction() { return new SurfaceAction("action-display-organization-admin", "Show organizations", "surface-request", browserToolId("action-display-organization-admin"), governedToolId(SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY), SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, "schema.organization-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-directory", "inline"), new Audit("OrganizationDirectoryDisplayed", true)); }
  private SurfaceAction showOrganizationsAction() { return new SurfaceAction("action-user-admin-show-organizations", "Show organizations", "surface-request", "user-admin.show-organizations", "manage-organizations", SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, "schema.organization-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-directory", "inline"), new Audit("OrganizationDirectoryDisplayed", true)); }
  private SurfaceAction openOrganizationCreateAction() { return new SurfaceAction("action-open-organization-create", "Open Organization create form", "surface-request", browserToolId("action-open-organization-create"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-create", "inline"), new Audit("OrganizationCreateFormDisplayed", true)); }
  private SurfaceAction openOrganizationRenameAction() { return new SurfaceAction("action-open-organization-rename", "Open Organization rename form", "surface-request", browserToolId("action-open-organization-rename"), "manage-organizations", SAAS_OWNER_ORGANIZATION_RENAME_CAPABILITY, "schema.organization-admin.rename.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-rename", "inline"), new Audit("OrganizationRenameFormDisplayed", true)); }
  private SurfaceAction openOrganizationSuspendAction() { return new SurfaceAction("action-open-organization-suspend", "Open Organization suspend confirmation", "surface-request", browserToolId("action-open-organization-suspend"), "manage-organizations", SAAS_OWNER_ORGANIZATION_SUSPEND_CAPABILITY, "schema.organization-admin.suspend.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-suspend-confirmation", "inline"), new Audit("OrganizationSuspendConfirmationDisplayed", true)); }
  private SurfaceAction openOrganizationReactivateAction() { return new SurfaceAction("action-open-organization-reactivate", "Open Organization reactivate confirmation", "surface-request", browserToolId("action-open-organization-reactivate"), "manage-organizations", SAAS_OWNER_ORGANIZATION_REACTIVATE_CAPABILITY, "schema.organization-admin.reactivate.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-reactivate-confirmation", "inline"), new Audit("OrganizationReactivateConfirmationDisplayed", true)); }
  private SurfaceAction organizationListAction() { return new SurfaceAction("action-organization-list", "Refresh Organizations", "read", browserToolId("action-organization-list"), governedToolId(SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY), SAAS_OWNER_ORGANIZATION_LIST_CAPABILITY, "schema.organization-admin.list.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-directory", "inline"), new Audit("OrganizationListRequested", true)); }
  private SurfaceAction organizationReadAction() { return new SurfaceAction("action-organization-read", "Read Organization", "read", browserToolId("action-organization-read"), governedToolId(SAAS_OWNER_ORGANIZATION_READ_CAPABILITY), SAAS_OWNER_ORGANIZATION_READ_CAPABILITY, "schema.organization-admin.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationReadRequested", true)); }
  private SurfaceAction submitOrganizationCreateAction() { return new SurfaceAction("action-submit-organization-create", "Create Organization", "command", "user-admin.submit-organization-create", "manage-organizations", SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.create.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationCreateRequested", true)); }
  private SurfaceAction organizationCreateAction() { return new SurfaceAction("action-organization-create", "Create Organization", "command", browserToolId("action-organization-create"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationCreateRequested", true)); }
  private SurfaceAction submitOrganizationRenameAction() { return new SurfaceAction("action-submit-organization-rename", "Rename Organization", "command", "user-admin.submit-organization-rename", "manage-organizations", SAAS_OWNER_ORGANIZATION_RENAME_CAPABILITY, "schema.organization-admin.rename.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationRenameRequested", true)); }
  private SurfaceAction organizationRenameAction() { return new SurfaceAction("action-organization-rename", "Rename Organization", "command", browserToolId("action-organization-rename"), governedToolId(SAAS_OWNER_TENANT_MANAGE_CAPABILITY), SAAS_OWNER_TENANT_MANAGE_CAPABILITY, "schema.organization-admin.rename.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationRenameRequested", true)); }
  private SurfaceAction organizationSuspendAction() { return new SurfaceAction("action-organization-suspend", "Suspend Organization", "command", browserToolId("action-organization-suspend"), "manage-organizations", SAAS_OWNER_ORGANIZATION_SUSPEND_CAPABILITY, "schema.organization-admin.suspend.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationSuspendRequested", true)); }
  private SurfaceAction organizationReactivateAction() { return new SurfaceAction("action-organization-reactivate", "Reactivate Organization", "command", browserToolId("action-organization-reactivate"), "manage-organizations", SAAS_OWNER_ORGANIZATION_REACTIVATE_CAPABILITY, "schema.organization-admin.reactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-organization-detail", "inline"), new Audit("OrganizationReactivateRequested", true)); }
  private SurfaceAction showOrganizationAdminsAction() { return new SurfaceAction("action-user-admin-show-organization-admins", "Show Organization Admins", "surface-request", "user-admin.show-organization-admins", "manage-organization-admins", SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY, "schema.organization-admin.admins.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-admins", "inline"), new Audit("OrganizationAdminsDisplayed", true)); }
  private SurfaceAction openOrganizationAdminDetailAction() { return new SurfaceAction("action-open-organization-admin-detail", "View Organization Admin", "read", "user-admin.open-organization-admin-detail", "manage-organization-admins", SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY, "schema.organization-admin.detail.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-admin-detail", "inline"), new Audit("OrganizationAdminDetailDisplayed", true)); }
  private SurfaceAction openOrganizationAdminInvitationDetailAction() { return new SurfaceAction("action-open-organization-admin-invitation-detail", "View Organization Admin invitation", "read", "user-admin.open-organization-admin-invitation-detail", "manage-organization-admins", SAAS_OWNER_ORGANIZATION_ADMIN_LIST_CAPABILITY, "schema.organization-admin.invitation-detail.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-admin-detail", "inline"), new Audit("OrganizationAdminInvitationDetailDisplayed", true)); }
  private SurfaceAction openOrganizationAdminInvitationCreateAction() { return new SurfaceAction("action-open-organization-admin-invitation-create", "Invite Organization Admin", "surface-request", "user-admin.open-organization-admin-invite", "manage-organization-admins", SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY, "schema.organization-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-organization-admin-invitation-create", "inline"), new Audit("OrganizationAdminInvitationCreateDisplayed", true)); }
  private SurfaceAction organizationAdminInviteAction() { return new SurfaceAction("action-submit-organization-admin-invitation", "Create Organization Admin invitation", "command", "user-admin.invite-organization-admin", "manage-organization-admins", SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY, "schema.organization-admin.invitation-create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("OrganizationAdminInvitationRequested", true)); }
  private SurfaceAction showCustomersAction() { return new SurfaceAction("action-user-admin-show-customers", "Show customers", "surface-request", "user-admin.show-customers", "manage-customers", TENANT_CUSTOMER_LIST_CAPABILITY, "schema.customer-admin.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-directory", "inline"), new Audit("CustomerDirectoryDisplayed", true)); }
  private SurfaceAction customerReadAction() { return new SurfaceAction("action-customer-read", "Read Customer", "read", "user-admin.read-customer", "manage-customers", TENANT_CUSTOMER_READ_CAPABILITY, "schema.customer-admin.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerReadRequested", true)); }
  private SurfaceAction openCustomerCreateAction() { return new SurfaceAction("action-open-customer-create", "Create Customer", "surface-request", "user-admin.open-customer-create", "manage-customers", TENANT_CUSTOMER_CREATE_CAPABILITY, "schema.customer-admin.create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-create", "inline"), new Audit("CustomerCreateFormDisplayed", true)); }
  private SurfaceAction submitCustomerCreateAction() { return new SurfaceAction("action-submit-customer-create", "Create Customer", "command", "user-admin.create-customer", "manage-customers", TENANT_CUSTOMER_CREATE_CAPABILITY, "schema.customer-admin.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerCreateRequested", true)); }
  private SurfaceAction openCustomerRenameAction() { return new SurfaceAction("action-open-customer-rename", "Rename Customer", "surface-request", "user-admin.open-customer-rename", "manage-customers", TENANT_CUSTOMER_RENAME_CAPABILITY, "schema.customer-admin.rename.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-rename", "inline"), new Audit("CustomerRenameFormDisplayed", true)); }
  private SurfaceAction submitCustomerRenameAction() { return new SurfaceAction("action-submit-customer-rename", "Rename Customer", "command", "user-admin.submit-customer-rename", "manage-customers", TENANT_CUSTOMER_RENAME_CAPABILITY, "schema.customer-admin.rename.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerRenameRequested", true)); }
  private SurfaceAction openCustomerSuspendAction() { return new SurfaceAction("action-open-customer-suspend", "Open Customer suspend confirmation", "surface-request", "user-admin.open-customer-suspend", "manage-customers", TENANT_CUSTOMER_SUSPEND_CAPABILITY, "schema.customer-admin.suspend.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-suspend-confirmation", "inline"), new Audit("CustomerSuspendConfirmationDisplayed", true)); }
  private SurfaceAction openCustomerSuspendConfirmationAction() { return new SurfaceAction("action-open-customer-suspend-confirmation", "Open Customer suspend confirmation (compatibility)", "surface-request", "user-admin.open-customer-suspend-confirmation", "manage-customers", TENANT_CUSTOMER_SUSPEND_CAPABILITY, "schema.customer-admin.suspend.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-suspend-confirmation", "inline"), new Audit("CustomerSuspendConfirmationDisplayed", true)); }
  private SurfaceAction openCustomerReactivateAction() { return new SurfaceAction("action-open-customer-reactivate", "Open Customer reactivate confirmation", "surface-request", "user-admin.open-customer-reactivate", "manage-customers", TENANT_CUSTOMER_REACTIVATE_CAPABILITY, "schema.customer-admin.reactivate.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-reactivate-confirmation", "inline"), new Audit("CustomerReactivateConfirmationDisplayed", true)); }
  private SurfaceAction openCustomerReactivateConfirmationAction() { return new SurfaceAction("action-open-customer-reactivate-confirmation", "Open Customer reactivate confirmation (compatibility)", "surface-request", "user-admin.open-customer-reactivate-confirmation", "manage-customers", TENANT_CUSTOMER_REACTIVATE_CAPABILITY, "schema.customer-admin.reactivate.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-reactivate-confirmation", "inline"), new Audit("CustomerReactivateConfirmationDisplayed", true)); }
  private SurfaceAction customerCreateAction() { return new SurfaceAction("action-customer-create", "Create Customer (compatibility)", "command", "user-admin.create-customer", "manage-customers", TENANT_CUSTOMER_CREATE_CAPABILITY, "schema.customer-admin.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerCreateRequested", true)); }
  private SurfaceAction customerRenameAction() { return new SurfaceAction("action-customer-rename", "Rename Customer (compatibility)", "command", "user-admin.rename-customer", "manage-customers", TENANT_CUSTOMER_RENAME_CAPABILITY, "schema.customer-admin.rename.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerRenameRequested", true)); }
  private SurfaceAction customerSuspendAction() { return new SurfaceAction("action-customer-suspend", "Suspend Customer", "command", "user-admin.suspend-customer", "manage-customers", TENANT_CUSTOMER_SUSPEND_CAPABILITY, "schema.customer-admin.suspend.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerSuspendRequested", true)); }
  private SurfaceAction customerReactivateAction() { return new SurfaceAction("action-customer-reactivate", "Reactivate Customer", "command", "user-admin.reactivate-customer", "manage-customers", TENANT_CUSTOMER_REACTIVATE_CAPABILITY, "schema.customer-admin.reactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-customer-detail", "inline"), new Audit("CustomerReactivateRequested", true)); }
  private SurfaceAction showCustomerAdminsAction() { return new SurfaceAction("action-user-admin-show-customer-admins", "Show Customer Admins", "surface-request", "user-admin.show-customer-admins", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_LIST_CAPABILITY, "schema.customer-admins.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-admins", "inline"), new Audit("CustomerAdminsDisplayed", true)); }
  private SurfaceAction openCustomerAdminDetailAction() { return new SurfaceAction("action-open-customer-admin-detail", "View Customer Admin", "read", "user-admin.open-customer-admin-detail", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_LIST_CAPABILITY, "schema.customer-admin.detail.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-admin-detail", "inline"), new Audit("CustomerAdminDetailDisplayed", true)); }
  private SurfaceAction openCustomerAdminInvitationDetailAction() { return new SurfaceAction("action-open-customer-admin-invitation-detail", "View Customer Admin invitation", "read", "user-admin.open-customer-admin-invitation-detail", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_LIST_CAPABILITY, "schema.customer-admin.invitation-detail.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-admin-detail", "inline"), new Audit("CustomerAdminInvitationDetailDisplayed", true)); }
  private SurfaceAction openCustomerAdminInvitationCreateAction() { return new SurfaceAction("action-open-customer-admin-invitation-create", "Invite Customer Admin", "surface-request", "user-admin.open-customer-admin-invite", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_INVITE_CAPABILITY, "schema.customer-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-customer-admin-invitation-create", "inline"), new Audit("CustomerAdminInvitationCreateDisplayed", true)); }
  private SurfaceAction customerAdminInviteAction() { return new SurfaceAction("action-customer-admin-invite", "Create Customer Admin invitation", "command", "user-admin.invite-customer-admin", "manage-customer-admins", TENANT_CUSTOMER_ADMIN_INVITE_CAPABILITY, "schema.customer-admin.invitation-create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("CustomerAdminInvitationRequested", true)); }
  private SurfaceAction displayListAction() { return new SurfaceAction("action-display-user-list", "Show users", "read", browserToolId("action-display-user-list"), governedToolId(USERADMIN_LIST_MEMBERS), USERADMIN_LIST_MEMBERS, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminListDisplayed", true)); }
  private SurfaceAction showUsersAction() { return new SurfaceAction("action-user-admin-show-users", "Show users", "read", "user-admin.show-users", "search-user-directory", USERADMIN_LIST_MEMBERS, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminBranchReturnDisplayed", true)); }
  private SurfaceAction displayDetailAction() { return new SurfaceAction("action-display-user-detail", "View user", "read", browserToolId("action-display-user-detail"), governedToolId(USERADMIN_LIST_MEMBERS), USERADMIN_LIST_MEMBERS, "schema.user-admin.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminDetailDisplayed", true)); }
  private SurfaceAction displayInvitationDetailAction() { return new SurfaceAction("action-display-invitation-detail", "View invitation", "read", browserToolId("action-display-invitation-detail"), governedToolId(USERADMIN_LIST_INVITATIONS), USERADMIN_LIST_INVITATIONS, "schema.user-admin.invitation-detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationDetailDisplayed", true)); }
  private SurfaceAction openInvitationCreateAction() { return new SurfaceAction("action-open-useradmin-invitation-create", "Invite user", "surface-request", browserToolId("action-open-useradmin-invitation-create"), governedToolId(USERADMIN_SEND_INVITATION), USERADMIN_SEND_INVITATION, "schema.user-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-create", "inline"), new Audit("InvitationCreateFormDisplayed", true)); }
  private SurfaceAction openUserAdminInvitationCreateAction() { return new SurfaceAction("action-open-user-admin-invitation-create", "Invite user", "surface-request", browserToolId("action-open-user-admin-invitation-create"), governedToolId(USERADMIN_SEND_INVITATION), USERADMIN_SEND_INVITATION, "schema.user-admin.invitation-create.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-create", "inline"), new Audit("InvitationCreateFormDisplayed", true)); }
  private SurfaceAction openInvitationResendConfirmationAction() { return new SurfaceAction("action-open-useradmin-invitation-resend-confirmation", "Open resend confirmation", "surface-request", browserToolId("action-open-useradmin-invitation-resend-confirmation"), governedToolId(USERADMIN_RESEND_INVITATION), USERADMIN_RESEND_INVITATION, "schema.user-admin.invitation-resend.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-resend-confirmation", "inline"), new Audit("InvitationResendConfirmationDisplayed", true)); }
  private SurfaceAction openInvitationRevokeConfirmationAction() { return new SurfaceAction("action-open-useradmin-invitation-revoke-confirmation", "Open revoke confirmation", "surface-request", browserToolId("action-open-useradmin-invitation-revoke-confirmation"), governedToolId(USERADMIN_REVOKE_INVITATION), USERADMIN_REVOKE_INVITATION, "schema.user-admin.invitation-revoke.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-revoke-confirmation", "inline"), new Audit("InvitationRevokeConfirmationDisplayed", true)); }
  private SurfaceAction openUserAdminInvitationRevokeConfirmationAction() { return new SurfaceAction("action-open-user-admin-invitation-revoke-confirmation", "Open revoke confirmation", "surface-request", browserToolId("action-open-user-admin-invitation-revoke-confirmation"), governedToolId(USERADMIN_REVOKE_INVITATION), USERADMIN_REVOKE_INVITATION, "schema.user-admin.invitation-revoke.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-invitation-revoke-confirmation", "inline"), new Audit("InvitationRevokeConfirmationDisplayed", true)); }
  private SurfaceAction openMembershipStatusConfirmationAction() { return new SurfaceAction("action-open-useradmin-membership-status-confirmation", "Open status confirmation", "surface-request", browserToolId("action-open-useradmin-membership-status-confirmation"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.membership-status.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-membership-status-confirmation", "inline"), new Audit("MembershipStatusConfirmationDisplayed", true)); }
  private SurfaceAction openUserAdminMembershipStatusConfirmationAction() { return new SurfaceAction("action-open-user-admin-membership-status-confirmation", "Open status confirmation", "surface-request", browserToolId("action-open-user-admin-membership-status-confirmation"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.membership-status.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-membership-status-confirmation", "inline"), new Audit("MembershipStatusConfirmationDisplayed", true)); }
  private SurfaceAction confirmMembershipStatusChangeAction() { return new SurfaceAction("action-confirm-user-admin-membership-status-change", "Confirm status change", "command", browserToolId("action-confirm-user-admin-membership-status-change"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.membership-status.confirm.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberStatusChanged", true)); }
  private SurfaceAction openSupportAccessGrantAction() { return new SurfaceAction("action-open-useradmin-support-access-grant", "Open support access grant", "surface-request", browserToolId("action-open-useradmin-support-access-grant"), governedToolId(USERADMIN_SUPPORT_ACCESS_GRANT), USERADMIN_SUPPORT_ACCESS_GRANT, "schema.user-admin.support-access-grant.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-support-access-grant", "inline"), new Audit("SupportAccessGrantFormDisplayed", true)); }
  private SurfaceAction openUserAdminSupportAccessGrantAction() { return new SurfaceAction("action-open-user-admin-support-access-grant", "Open support access grant", "surface-request", browserToolId("action-open-user-admin-support-access-grant"), governedToolId(USERADMIN_SUPPORT_ACCESS_GRANT), USERADMIN_SUPPORT_ACCESS_GRANT, "schema.user-admin.support-access-grant.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-support-access-grant", "inline"), new Audit("SupportAccessGrantFormDisplayed", true)); }
  private SurfaceAction openSupportAccessRevokeConfirmationAction() { return new SurfaceAction("action-open-useradmin-support-access-revoke-confirmation", "Open support access revoke confirmation", "surface-request", browserToolId("action-open-useradmin-support-access-revoke-confirmation"), governedToolId(USERADMIN_SUPPORT_ACCESS_REVOKE), USERADMIN_SUPPORT_ACCESS_REVOKE, "schema.user-admin.support-access-revoke.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-support-access-revoke-confirmation", "inline"), new Audit("SupportAccessRevokeConfirmationDisplayed", true)); }
  private SurfaceAction openUserAdminSupportAccessRevokeConfirmationAction() { return new SurfaceAction("action-open-user-admin-support-access-revoke-confirmation", "Open support access revoke confirmation", "surface-request", browserToolId("action-open-user-admin-support-access-revoke-confirmation"), governedToolId(USERADMIN_SUPPORT_ACCESS_REVOKE), USERADMIN_SUPPORT_ACCESS_REVOKE, "schema.user-admin.support-access-revoke.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-support-access-revoke-confirmation", "inline"), new Audit("SupportAccessRevokeConfirmationDisplayed", true)); }
  private SurfaceAction openIdentityExceptionReviewAction() { return new SurfaceAction("action-open-useradmin-identity-exception-review", "Open identity exception review", "surface-request", browserToolId("action-open-useradmin-identity-exception-review"), governedToolId(USERADMIN_IDENTITY_RELINK_READ), USERADMIN_IDENTITY_RELINK_READ, "schema.user-admin.identity-exception.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityExceptionReviewDisplayed", true)); }
  private SurfaceAction requestIdentityRelinkAction() { return new SurfaceAction("action-useradmin-request-identity-relink", "Request identity recovery", "command", browserToolId("action-useradmin-request-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_REQUEST), USERADMIN_IDENTITY_RELINK_REQUEST, "schema.user-admin.identity-relink.request.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkRequested", true)); }
  private SurfaceAction readIdentityRelinkAction() { return new SurfaceAction("action-useradmin-read-identity-relink", "Refresh identity recovery", "read", browserToolId("action-useradmin-read-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_READ), USERADMIN_IDENTITY_RELINK_READ, "schema.user-admin.identity-relink.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkRead", true)); }
  private SurfaceAction approveIdentityRelinkAction() { return new SurfaceAction("action-useradmin-approve-identity-relink", "Approve identity recovery", "approval", browserToolId("action-useradmin-approve-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_APPROVE), USERADMIN_IDENTITY_RELINK_APPROVE, "schema.user-admin.identity-relink.approve.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkApproved", true)); }
  private SurfaceAction denyIdentityRelinkAction() { return new SurfaceAction("action-useradmin-deny-identity-relink", "Deny identity recovery", "approval", browserToolId("action-useradmin-deny-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_DENY), USERADMIN_IDENTITY_RELINK_DENY, "schema.user-admin.identity-relink.deny.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkDenied", true)); }
  private SurfaceAction completeIdentityRelinkAction() { return new SurfaceAction("action-useradmin-complete-identity-relink", "Complete identity recovery", "command", browserToolId("action-useradmin-complete-identity-relink"), governedToolId(USERADMIN_IDENTITY_RELINK_COMPLETE), USERADMIN_IDENTITY_RELINK_COMPLETE, "schema.user-admin.identity-relink.complete.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-identity-exception-review", "inline"), new Audit("IdentityRelinkCompleted", true)); }
  private SurfaceAction inviteAction() { return new SurfaceAction("action-invite-user", "Invite user", "command", browserToolId("action-invite-user"), governedToolId(USERADMIN_SEND_INVITATION), USERADMIN_SEND_INVITATION, "schema.invitation.create.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationRequested", true)); }
  private SurfaceAction submitUserAdminInvitationAction() { return new SurfaceAction("action-submit-user-admin-invitation", "Create invitation", "command", browserToolId("action-submit-user-admin-invitation"), governedToolId(USERADMIN_SEND_INVITATION), USERADMIN_SEND_INVITATION, "schema.user-admin.invitation-create.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationRequested", true)); }
  private SurfaceAction resendInvitationAction() { return new SurfaceAction("action-useradmin-resend-invitation", "Resend invitation", "command", browserToolId("action-useradmin-resend-invitation"), governedToolId(USERADMIN_RESEND_INVITATION), USERADMIN_RESEND_INVITATION, "schema.invitation.resend.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationResendRequested", true)); }
  private SurfaceAction revokeInvitationAction() { return new SurfaceAction("action-useradmin-revoke-invitation", "Revoke invitation", "command", browserToolId("action-useradmin-revoke-invitation"), governedToolId(USERADMIN_REVOKE_INVITATION), USERADMIN_REVOKE_INVITATION, "schema.invitation.revoke.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationRevokeRequested", true)); }
  private SurfaceAction confirmUserAdminInvitationRevokeAction() { return new SurfaceAction("action-confirm-user-admin-invitation-revoke", "Revoke invitation", "command", browserToolId("action-confirm-user-admin-invitation-revoke"), governedToolId(USERADMIN_REVOKE_INVITATION), USERADMIN_REVOKE_INVITATION, "schema.user-admin.invitation-revoke.confirm.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-invitation-detail", "inline"), new Audit("InvitationRevokeRequested", true)); }
  private SurfaceAction updateMemberStatusAction() { return new SurfaceAction("action-useradmin-disable-member", "Deactivate user", "command", browserToolId("action-useradmin-disable-member"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.member-status.update.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberStatusChanged", true)); }
  private SurfaceAction reactivateMemberStatusAction() { return new SurfaceAction("action-useradmin-reactivate-member", "Reactivate user", "command", browserToolId("action-useradmin-reactivate-member"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.member-status.update.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberStatusChanged", true)); }
  private SurfaceAction permanentlyRemoveUserAction() { return new SurfaceAction("action-useradmin-permanently-remove-user", "Permanently remove user", "command", browserToolId("action-useradmin-permanently-remove-user"), governedToolId(USERADMIN_UPDATE_MEMBER_STATUS), USERADMIN_UPDATE_MEMBER_STATUS, "schema.user-admin.user-remove.permanent.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminUserPermanentlyRemoved", true)); }
  private SurfaceAction disableAccountAction() { return new SurfaceAction("action-useradmin-disable-account", "Disable account", "command", browserToolId("action-useradmin-disable-account"), governedToolId(USERADMIN_DISABLE_ACCOUNT), USERADMIN_DISABLE_ACCOUNT, "schema.user-admin.account.disable.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminAccountDisabled", true)); }
  private SurfaceAction reactivateAccountAction() { return new SurfaceAction("action-useradmin-reactivate-account", "Reactivate account", "command", browserToolId("action-useradmin-reactivate-account"), governedToolId(USERADMIN_REACTIVATE_ACCOUNT), USERADMIN_REACTIVATE_ACCOUNT, "schema.user-admin.account.reactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminAccountReactivated", true)); }
  private SurfaceAction readSupportAccessAction() { return new SurfaceAction("action-useradmin-read-support-access", "Read support access", "read", browserToolId("action-useradmin-read-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_READ), USERADMIN_SUPPORT_ACCESS_READ, "schema.user-admin.support-access.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-users", "inline"), new Audit("UserAdminSupportAccessRead", true)); }
  private SurfaceAction validateSupportAccessGrantAction() { return new SurfaceAction("action-validate-user-admin-support-access-grant", "Validate support access request", "read", browserToolId("action-validate-user-admin-support-access-grant"), governedToolId(USERADMIN_SUPPORT_ACCESS_GRANT), USERADMIN_SUPPORT_ACCESS_GRANT, "schema.user-admin.support-access-grant.validate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-support-access-grant", "inline"), new Audit("UserAdminSupportAccessGrantValidated", true)); }
  private SurfaceAction submitSupportAccessGrantAction() { return new SurfaceAction("action-submit-user-admin-support-access-grant", "Submit support access grant", "command", browserToolId("action-submit-user-admin-support-access-grant"), governedToolId(USERADMIN_SUPPORT_ACCESS_GRANT), USERADMIN_SUPPORT_ACCESS_GRANT, "schema.user-admin.support-access-grant.submit.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessGranted", true)); }
  private SurfaceAction grantSupportAccessAction() { return new SurfaceAction("action-useradmin-grant-support-access", "Grant support access", "command", browserToolId("action-useradmin-grant-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_GRANT), USERADMIN_SUPPORT_ACCESS_GRANT, "schema.user-admin.support-access.grant.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessGranted", true)); }
  private SurfaceAction revokeSupportAccessAction() { return new SurfaceAction("action-useradmin-revoke-support-access", "Revoke support access", "command", browserToolId("action-useradmin-revoke-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_REVOKE), USERADMIN_SUPPORT_ACCESS_REVOKE, "schema.user-admin.support-access.revoke.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessRevoked", true)); }
  private SurfaceAction confirmUserAdminSupportAccessRevokeAction() { return new SurfaceAction("action-confirm-user-admin-support-access-revoke", "Revoke support access", "command", browserToolId("action-confirm-user-admin-support-access-revoke"), governedToolId(USERADMIN_SUPPORT_ACCESS_REVOKE), USERADMIN_SUPPORT_ACCESS_REVOKE, "schema.user-admin.support-access-revoke.confirm.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessRevoked", true)); }
  private SurfaceAction extendSupportAccessAction() { return new SurfaceAction("action-useradmin-extend-support-access", "Extend support access", "command", browserToolId("action-useradmin-extend-support-access"), governedToolId(USERADMIN_SUPPORT_ACCESS_EXTEND), USERADMIN_SUPPORT_ACCESS_EXTEND, "schema.user-admin.support-access.extend.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminSupportAccessExtended", true)); }
  private SurfaceAction previewRoleChangeAction() { return new SurfaceAction("action-useradmin-preview-role-change", "Preview role change", "proposal", browserToolId("action-useradmin-preview-role-change"), governedToolId(USERADMIN_PREVIEW_ROLE_CHANGE), USERADMIN_PREVIEW_ROLE_CHANGE, "schema.user-admin.role-change.preview.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-role-change-preview", "inline"), new Audit("UserAdminRoleChangePreviewed", true)); }
  private SurfaceAction openUserAdminRoleChangePreviewAction() { return new SurfaceAction("action-open-user-admin-role-change-preview", "Refresh preview", "surface-request", browserToolId("action-open-user-admin-role-change-preview"), governedToolId(USERADMIN_PREVIEW_ROLE_CHANGE), USERADMIN_PREVIEW_ROLE_CHANGE, "schema.user-admin.role-change.preview.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-role-change-preview", "inline"), new Audit("UserAdminRoleChangePreviewed", true)); }
  private SurfaceAction reviseUserAdminRoleChangeAction() { return new SurfaceAction("action-revise-user-admin-role-change", "Revise or cancel", "surface-request", browserToolId("action-revise-user-admin-role-change"), governedToolId(USERADMIN_PREVIEW_ROLE_CHANGE), USERADMIN_PREVIEW_ROLE_CHANGE, "schema.user-admin.role-change.revise.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminRoleChangeRevisionRequested", true)); }
  private SurfaceAction commitUserAdminRoleChangeAction() { return new SurfaceAction("action-commit-user-admin-role-change", "Commit role change", "command", browserToolId("action-commit-user-admin-role-change"), governedToolId(USERADMIN_CHANGE_MEMBER_ROLES), USERADMIN_CHANGE_MEMBER_ROLES, "schema.user-admin.role-change.apply.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberRolesChanged", true)); }
  private SurfaceAction changeMemberRolesAction() { return new SurfaceAction("action-useradmin-change-member-roles", "Change role", "command", browserToolId("action-useradmin-change-member-roles"), governedToolId(USERADMIN_CHANGE_MEMBER_ROLES), USERADMIN_CHANGE_MEMBER_ROLES, "schema.user-admin.role-change.apply.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("UserAdminMemberRolesChanged", true)); }
  private SurfaceAction startAccessReviewAction() { return new SurfaceAction("action-useradmin-start-access-review", "Start access review", "workflow", browserToolId("action-useradmin-start-access-review"), governedToolId(USERADMIN_ACCESS_REVIEW_START), USERADMIN_ACCESS_REVIEW_START, "schema.user-admin.access-review.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewStarted", true)); }
  private SurfaceAction readAccessReviewAction() { return new SurfaceAction("action-useradmin-read-access-review", "Read access review", "read", browserToolId("action-useradmin-read-access-review"), governedToolId(USERADMIN_ACCESS_REVIEW_READ), USERADMIN_ACCESS_REVIEW_READ, "schema.user-admin.access-review.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewRead", true)); }
  private SurfaceAction cancelAccessReviewAction() { return new SurfaceAction("action-useradmin-cancel-access-review", "Cancel access review", "command", browserToolId("action-useradmin-cancel-access-review"), governedToolId(USERADMIN_ACCESS_REVIEW_CANCEL), USERADMIN_ACCESS_REVIEW_CANCEL, "schema.user-admin.access-review.cancel.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewCancelled", true)); }
  private SurfaceAction acceptAccessReviewResultAction() { return new SurfaceAction("action-useradmin-accept-access-review-result", "Accept access review result", "approval", browserToolId("action-useradmin-accept-access-review-result"), governedToolId(USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT), USERADMIN_ACCESS_REVIEW_ACCEPT_RESULT, "schema.user-admin.access-review.accept-result.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewResultAccepted", true)); }
  private SurfaceAction rejectAccessReviewResultAction() { return new SurfaceAction("action-useradmin-reject-access-review-result", "Reject access review result", "approval", browserToolId("action-useradmin-reject-access-review-result"), governedToolId(USERADMIN_ACCESS_REVIEW_REJECT_RESULT), USERADMIN_ACCESS_REVIEW_REJECT_RESULT, "schema.user-admin.access-review.reject-result.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-user-admin-access-review-task", "inline"), new Audit("UserAdminAccessReviewResultRejected", true)); }
  private SurfaceAction deniedReplaceRoleAction() { return new SurfaceAction("action-replace-membership-role", "Replace membership role", "command", browserToolId("action-replace-membership-role"), governedToolId(USER_ADMIN_CAPABILITY), USER_ADMIN_CAPABILITY, "schema.membership.role.replace.v1", true, false, new DisabledReason("LAST_ADMIN_DENIED", "Backend authorization denied this action: cannot remove the last tenant admin without an approved replacement."), new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-user-admin-user-detail", "inline"), new Audit("MembershipRoleReplacementDenied", true)); }
  private SurfaceAction traceAction() { return new SurfaceAction("action-open-trace", "Open trace", "trace", browserToolId("action-open-trace"), governedToolId("my_account.view_own_trace_refs"), "my_account.view_own_trace_refs", null, false, false, null, new Idempotency(false, null), null, new Audit("TraceOpened", true)); }
  private SurfaceAction openAuditAction() { return new SurfaceAction("action-open-audit-trace", "Open audit timeline", "trace", browserToolId("action-open-audit-trace"), governedToolId("my_account.view_own_trace_refs"), "my_account.view_own_trace_refs", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-dashboard", "inline"), new Audit("AuditTimelineOpened", true)); }
  private SurfaceAction auditTraceDashboardAction() { return new SurfaceAction("action-audit-trace-dashboard", "Refresh Audit/Trace dashboard", "read", browserToolId("action-audit-trace-dashboard"), governedToolId(AUDIT_TRACE_DASHBOARD_CAPABILITY), AUDIT_TRACE_DASHBOARD_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-dashboard", "inline"), new Audit("AuditTraceDashboardRead", true)); }
  private SurfaceAction auditTraceSearchAction() { return new SurfaceAction("action-audit-trace-search", "Search traces", "read", browserToolId("action-audit-trace-search"), governedToolId(AUDIT_TRACE_SEARCH_CAPABILITY), AUDIT_TRACE_SEARCH_CAPABILITY, "schema.audit-trace.search.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-search", "inline"), new Audit("AuditTraceSearchRequested", true)); }
  private SurfaceAction auditTraceDetailAction() { return new SurfaceAction("action-audit-trace-detail", "Open trace detail", "read", browserToolId("action-audit-trace-detail"), governedToolId(AUDIT_TRACE_DETAIL_CAPABILITY), AUDIT_TRACE_DETAIL_CAPABILITY, "schema.audit-trace.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-detail", "inline"), new Audit("AuditTraceDetailRequested", true)); }
  private SurfaceAction auditTraceTimelineAction() { return new SurfaceAction("action-audit-trace-timeline", "Open correlation timeline", "read", browserToolId("action-audit-trace-timeline"), governedToolId(AUDIT_TRACE_TIMELINE_CAPABILITY), AUDIT_TRACE_TIMELINE_CAPABILITY, "schema.audit-trace.timeline.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-timeline", "inline"), new Audit("AuditTraceTimelineRequested", true)); }
  private SurfaceAction auditTraceFailureEvidenceAction() { return new SurfaceAction("action-audit-trace-failure-evidence", "Open failure evidence", "read", browserToolId("action-audit-trace-failure-evidence"), governedToolId(AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY), AUDIT_TRACE_FAILURE_EVIDENCE_CAPABILITY, "schema.audit-trace.failure-evidence.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-failure-evidence", "inline"), new Audit("AuditTraceFailureEvidenceRequested", true)); }
  private SurfaceAction auditTraceInvestigationGuideAction() { return new SurfaceAction("action-audit-trace-investigation-guide", "Show investigation guidance", "read", browserToolId("action-audit-trace-investigation-guide"), governedToolId(AUDIT_TRACE_GUIDE_CAPABILITY), AUDIT_TRACE_GUIDE_CAPABILITY, "schema.audit-trace.investigation-guide.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-audit-trace-investigation-guide", "inline"), new Audit("AuditTraceInvestigationGuideRequested", true)); }
  private SurfaceAction auditTraceExportRequestAction() { return new SurfaceAction("action-audit-trace-request-redacted-export", "Request redacted export", "approval", browserToolId("action-audit-trace-request-redacted-export"), governedToolId(AUDIT_TRACE_EXPORT_REQUEST_CAPABILITY), AUDIT_TRACE_EXPORT_REQUEST_CAPABILITY, "schema.audit-trace.export-request.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface("decision-card", "surface-audit-trace-export-request", "inline"), new Audit("AuditTraceRedactedExportRequested", true)); }
  private SurfaceAction auditTraceAppendInvestigationNoteAction() { return new SurfaceAction("action-audit-trace-append-investigation-note", "Append investigation note", "command", browserToolId("action-audit-trace-append-investigation-note"), governedToolId(AUDIT_TRACE_INVESTIGATION_NOTE_CAPABILITY), AUDIT_TRACE_INVESTIGATION_NOTE_CAPABILITY, "schema.audit-trace.investigation-note.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface("system-message", "surface-audit-trace-investigation-note", "inline"), new Audit("AuditTraceInvestigationNoteAppended", true)); }
  private SurfaceAction auditTraceSummaryTaskBlockedAction() { return new SurfaceAction("action-audit-trace-summary-task-start", "Start audit summary task", "workflow", browserToolId("action-audit-trace-summary-task-start"), governedToolId("audit.trace.summaryTask.start"), AUDIT_TRACE_SUMMARY_TASK_START_CAPABILITY, "schema.audit-trace.summary-task.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface("workflow-status", "surface-audit-trace-summary-progress", "inline"), new Audit("AuditTraceSummaryTaskStartedOrBlocked", true)); }
  private SurfaceAction auditTraceSummaryTaskReadAction() { return new SurfaceAction("action-audit-trace-summary-task-read", "Refresh summary task", "read", browserToolId("action-audit-trace-summary-task-read"), governedToolId(AuditTraceSummaryService.READ_CAPABILITY), AuditTraceSummaryService.READ_CAPABILITY, "schema.audit-trace.summary-task.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface("workflow-status", "surface-audit-trace-summary-progress", "inline"), new Audit("AuditTraceSummaryTaskRead", true)); }
  private SurfaceAction auditTraceSummaryReviewAction() { return new SurfaceAction("action-audit-trace-summary-review", "Open summary review", "read", browserToolId("action-audit-trace-summary-review"), governedToolId(AUDIT_TRACE_SUMMARY_TASK_REVIEW_CAPABILITY), AUDIT_TRACE_SUMMARY_TASK_REVIEW_CAPABILITY, "schema.audit-trace.summary-review.open.v1", false, false, null, new Idempotency(false, null), new ResultSurface("decision-card", "surface-audit-trace-summary-review", "inline"), new Audit("AuditTraceSummaryReviewRead", true)); }
  private SurfaceAction auditTraceSummaryAcceptAction() { return new SurfaceAction("action-audit-trace-summary-accept", "Accept advisory summary", "approval", browserToolId("action-audit-trace-summary-accept"), governedToolId(AUDIT_TRACE_SUMMARY_TASK_ACCEPT_CAPABILITY), AUDIT_TRACE_SUMMARY_TASK_ACCEPT_CAPABILITY, "schema.audit-trace.summary-review.accept.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface("decision-card", "surface-audit-trace-summary-review", "inline"), new Audit("AuditTraceSummaryAccepted", true)); }
  private SurfaceAction auditTraceSummaryRejectAction() { return new SurfaceAction("action-audit-trace-summary-reject", "Reject advisory summary", "approval", browserToolId("action-audit-trace-summary-reject"), governedToolId(AUDIT_TRACE_SUMMARY_TASK_REJECT_CAPABILITY), AUDIT_TRACE_SUMMARY_TASK_REJECT_CAPABILITY, "schema.audit-trace.summary-review.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface("decision-card", "surface-audit-trace-summary-review", "inline"), new Audit("AuditTraceSummaryRejected", true)); }
  private SurfaceAction governanceDashboardAction() { return new SurfaceAction("action-governance-policy-dashboard", "Open governance dashboard", "read", browserToolId("action-governance-policy-dashboard"), governedToolId(GOVERNANCE_POLICY_READ_CAPABILITY), GOVERNANCE_POLICY_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-dashboard", "inline"), new Audit("GovernancePolicyDashboardDisplayed", true)); }
  private SurfaceAction governanceListPoliciesAction() { return new SurfaceAction("action-governance-policy-list", "List policy inventory", "read", browserToolId("action-governance-policy-list"), governedToolId(GOVERNANCE_POLICY_READ_CAPABILITY), GOVERNANCE_POLICY_READ_CAPABILITY, "schema.governance-policy.inventory.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-inventory", "inline"), new Audit("GovernancePolicyInventoryListed", true)); }
  private SurfaceAction governanceReadPolicyAction() { return new SurfaceAction("action-governance-policy-read", "Read policy evidence", "read", browserToolId("action-governance-policy-read"), governedToolId(GOVERNANCE_POLICY_READ_CAPABILITY), GOVERNANCE_POLICY_READ_CAPABILITY, "schema.governance-policy.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-detail", "inline"), new Audit("GovernancePolicyDetailRead", true)); }
  private SurfaceAction governanceDraftProposalAction() { return new SurfaceAction("action-governance-policy-draft-proposal", "Draft policy proposal", "proposal", browserToolId("action-governance-policy-draft-proposal"), governedToolId(GOVERNANCE_POLICY_PROPOSE_CAPABILITY), GOVERNANCE_POLICY_PROPOSE_CAPABILITY, "schema.governance-policy.proposal.draft.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-proposal", "inline"), new Audit("GovernancePolicyProposalDrafted", true)); }
  private SurfaceAction governanceSubmitProposalAction() { return new SurfaceAction("action-governance-policy-submit-proposal", "Submit proposal for review", "proposal", browserToolId("action-governance-policy-submit-proposal"), governedToolId(GOVERNANCE_POLICY_PROPOSE_CAPABILITY), GOVERNANCE_POLICY_PROPOSE_CAPABILITY, "schema.governance-policy.proposal.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-proposal", "inline"), new Audit("GovernancePolicyProposalSubmitted", true)); }
  private SurfaceAction governanceSimulateProposalAction() { return new SurfaceAction("action-governance-policy-simulate", "Simulate policy impact", "governance", browserToolId("action-governance-policy-simulate"), governedToolId(GOVERNANCE_POLICY_SIMULATE_CAPABILITY), GOVERNANCE_POLICY_SIMULATE_CAPABILITY, "schema.governance-policy.simulate.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-governance-policy-simulation", "inline"), new Audit("GovernancePolicySimulationRequested", true)); }
  private SurfaceAction governanceDecideProposalAction() { return new SurfaceAction("action-governance-policy-decide", "Approve, reject, or request changes", "approval", browserToolId("action-governance-policy-decide"), governedToolId(GOVERNANCE_PROPOSALS_REVIEW_CAPABILITY), GOVERNANCE_POLICY_APPROVE_CAPABILITY, "schema.governance-policy.proposal.decide.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-decision", "inline"), new Audit("GovernancePolicyDecisionRecorded", true)); }
  private SurfaceAction governanceActivateProposalAction() { return new SurfaceAction("action-governance-policy-activate", "Activate approved policy", "command", browserToolId("action-governance-policy-activate"), governedToolId(GOVERNANCE_PROPOSALS_ACTIVATE_CAPABILITY), GOVERNANCE_POLICY_ACTIVATE_CAPABILITY, "schema.governance-policy.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-decision", "inline"), new Audit("GovernancePolicyActivationRequested", true)); }
  private SurfaceAction governanceRollbackPolicyAction() { return new SurfaceAction("action-governance-policy-rollback", "Roll back policy change", "command", browserToolId("action-governance-policy-rollback"), governedToolId(GOVERNANCE_PROPOSALS_ACTIVATE_CAPABILITY), GOVERNANCE_POLICY_ROLLBACK_CAPABILITY, "schema.governance-policy.rollback.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-governance-policy-decision", "inline"), new Audit("GovernancePolicyRollbackRequested", true)); }
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

  private SurfaceAction agentAdminRefreshDashboardAction() { return new SurfaceAction("action-agent-admin-refresh-dashboard", "Refresh Agent Admin dashboard", "read", browserToolId("action-agent-admin-refresh-dashboard"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-dashboard", "inline"), new Audit("AgentAdminDashboardDisplayed", true)); }
  private SurfaceAction agentAdminOpenCatalogAction() { return new SurfaceAction("action-agent-admin-open-catalog", "Open managed agent catalog", "read", browserToolId("action-agent-admin-open-catalog"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction agentAdminRefreshCatalogAction() { return new SurfaceAction("action-agent-admin-refresh-catalog", "Refresh managed agent catalog", "read", browserToolId("action-agent-admin-refresh-catalog"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction agentAdminSearchCatalogAction() { return new SurfaceAction("action-agent-admin-search-catalog", "Search managed agent catalog", "read", browserToolId("action-agent-admin-search-catalog"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, "schema.agent-admin.catalog-filter.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogSearched", true)); }
  private SurfaceAction agentAdminResetCatalogFiltersAction() { return new SurfaceAction("action-agent-admin-reset-catalog-filters", "Reset catalog filters", "read", browserToolId("action-agent-admin-reset-catalog-filters"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogFiltersReset", true)); }
  private SurfaceAction agentAdminCatalogOpenTraceAction() { return new SurfaceAction("action-agent-admin-catalog-open-trace", "Open catalog trace", "trace", browserToolId("action-agent-admin-catalog-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentCatalogTraceOpened", true)); }
  private SurfaceAction agentAdminOpenBehaviorProposalsAction() { return new SurfaceAction("action-agent-admin-open-behavior-proposals", "Open behavior proposals", "read", browserToolId("action-agent-admin-open-behavior-proposals"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorProposalQueueOpened", true)); }
  private SurfaceAction agentAdminOpenPromptRiskReviewAction() { return new SurfaceAction("action-agent-admin-open-prompt-risk-review", "Open prompt-risk review", "read", browserToolId("action-agent-admin-open-prompt-risk-review"), governedToolId(AgentAdminPromptRiskReviewService.READ_CAPABILITY), AgentAdminPromptRiskReviewService.READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewRead", true)); }
  private SurfaceAction agentAdminOpenSeedMaterialAction() { return new SurfaceAction("action-agent-admin-open-seed-material", "Open seed material", "read", browserToolId("action-agent-admin-open-seed-material"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialListed", true)); }
  private SurfaceAction agentAdminOpenManifestDriftAction() { return new SurfaceAction("action-agent-admin-open-manifest-drift", "Open manifest drift review", "read", browserToolId("action-agent-admin-open-manifest-drift"), governedToolId(AGENT_ADMIN_GET_MANIFEST_CAPABILITY), AGENT_ADMIN_GET_MANIFEST_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-skill-manifest-diff", "inline"), new Audit("AgentSkillManifestDisplayed", true)); }
  private SurfaceAction agentAdminOpenToolBoundaryAction() { return new SurfaceAction("action-agent-admin-open-tool-boundary", "Open tool-boundary review", "read", browserToolId("action-agent-admin-open-tool-boundary"), governedToolId(AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("AgentToolBoundaryDisplayed", true)); }
  private SurfaceAction agentAdminOpenModelRefsAction() { return new SurfaceAction("action-agent-admin-open-model-refs", "Open model references", "read", browserToolId("action-agent-admin-open-model-refs"), governedToolId(AGENT_ADMIN_GET_MODEL_REF_CAPABILITY), AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-model-refs", "inline"), new Audit("AgentModelRefsDisplayed", true)); }
  private SurfaceAction agentAdminOpenTraceAction() { return new SurfaceAction("action-agent-admin-open-trace", "Open Agent Admin trace", "trace", browserToolId("action-agent-admin-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentWorkTraceOpened", true)); }
  private SurfaceAction displayAgentAdminDashboardAction() { return new SurfaceAction("action-display-agent-admin-dashboard", "Open Agent Admin dashboard", "read", browserToolId("action-display-agent-admin-dashboard"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-dashboard", "inline"), new Audit("AgentAdminDashboardDisplayed", true)); }
  private SurfaceAction displayAgentCatalogAction() { return new SurfaceAction("action-display-agent-catalog", "Display agent catalog", "read", browserToolId("action-display-agent-catalog"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction openAgentDetailAction() { return new SurfaceAction("action-open-agent-detail", "Open agent readiness detail", "read", browserToolId("action-open-agent-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, "schema.agent-definition.detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction agentDetailRefreshAction() { return new SurfaceAction("action-agent-detail-refresh", "Refresh readiness detail", "read", browserToolId("action-agent-detail-refresh"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, "schema.agent-detail.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailRefreshed", true)); }
  private SurfaceAction agentDetailOpenPromptGovernanceAction() { return new SurfaceAction("action-agent-detail-open-prompt-governance", "Open prompt governance", "read", browserToolId("action-agent-detail-open-prompt-governance"), governedToolId(AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY), AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-prompt-governance", "inline"), new Audit("AgentPromptGovernanceOpened", true)); }
  private SurfaceAction agentDetailOpenSkillManifestAction() { return new SurfaceAction("action-agent-detail-open-skill-manifest", "Open skill manifest review", "read", browserToolId("action-agent-detail-open-skill-manifest"), governedToolId(AGENT_ADMIN_GET_MANIFEST_CAPABILITY), AGENT_ADMIN_GET_MANIFEST_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-skill-manifest-diff", "inline"), new Audit("AgentSkillManifestDisplayed", true)); }
  private SurfaceAction agentDetailOpenToolBoundaryAction() { return new SurfaceAction("action-agent-detail-open-tool-boundary", "Open tool-boundary review", "read", browserToolId("action-agent-detail-open-tool-boundary"), governedToolId(AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("AgentToolBoundaryDisplayed", true)); }
  private SurfaceAction agentDetailOpenModelRefsAction() { return new SurfaceAction("action-agent-detail-open-model-refs", "Open model references", "read", browserToolId("action-agent-detail-open-model-refs"), governedToolId(AGENT_ADMIN_GET_MODEL_REF_CAPABILITY), AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-model-refs", "inline"), new Audit("AgentModelRefsDisplayed", true)); }
  private SurfaceAction agentDetailRunTestAction() { return new SurfaceAction("action-agent-detail-run-test", "Run no-side-effect test", "workflow", browserToolId("action-agent-detail-run-test"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-runtime.test.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentRuntimeTestRequested", true)); }
  private SurfaceAction agentDetailOpenPromptRiskReviewAction() { return new SurfaceAction("action-agent-detail-open-prompt-risk-review", "Open prompt-risk review", "read", browserToolId("action-agent-detail-open-prompt-risk-review"), governedToolId(AgentAdminPromptRiskReviewService.READ_CAPABILITY), AgentAdminPromptRiskReviewService.READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewRead", true)); }
  private SurfaceAction agentDetailOpenActivationAction() { return new SurfaceAction("action-agent-detail-open-activation", "Open activation confirmation", "surface-request", browserToolId("action-agent-detail-open-activation"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-activation-confirmation", "inline"), new Audit("AgentDefinitionActivationConfirmationDisplayed", true)); }
  private SurfaceAction agentDetailOpenDeactivationAction() { return new SurfaceAction("action-agent-detail-open-deactivation", "Open deactivation confirmation", "surface-request", browserToolId("action-agent-detail-open-deactivation"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.deactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-deactivation-confirmation", "inline"), new Audit("AgentDefinitionDeactivationConfirmationDisplayed", true)); }
  private SurfaceAction agentDetailOpenRollbackAction() { return new SurfaceAction("action-agent-detail-open-rollback", "Open rollback confirmation", "surface-request", browserToolId("action-agent-detail-open-rollback"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.behavior-change.rollback.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-rollback-confirmation", "inline"), new Audit("AgentDefinitionRollbackConfirmationDisplayed", true)); }
  private SurfaceAction agentDetailOpenTraceAction() { return new SurfaceAction("action-agent-detail-open-trace", "Open detail trace", "trace", browserToolId("action-agent-detail-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentDefinitionDetailTraceOpened", true)); }
  private SurfaceAction agentDetailBackToCatalogAction() { return new SurfaceAction("action-agent-detail-back-to-catalog", "Back to catalog", "read", browserToolId("action-agent-detail-back-to-catalog"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentCatalogDisplayed", true)); }
  private SurfaceAction activateAgentDefinitionAction() { return new SurfaceAction("action-activate-agent-definition", "Review AgentDefinition activation", "surface-request", browserToolId("action-activate-agent-definition"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-activation-confirmation", "inline"), new Audit("AgentDefinitionActivationConfirmationDisplayed", true)); }
  private SurfaceAction agentActivationRefreshAction() { return new SurfaceAction("action-agent-activation-refresh", "Refresh activation confirmation", "read", browserToolId("action-agent-activation-refresh"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-admin.activation.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-activation-confirmation", "inline"), new Audit("AgentActivationConfirmationRefreshed", true)); }
  private SurfaceAction agentActivationConfirmAction() { return new SurfaceAction("action-agent-activation-confirm", "Confirm activation", "command", browserToolId("action-agent-activation-confirm"), governedToolId(AGENT_ADMIN_ACTIVATE_CAPABILITY), AGENT_ADMIN_ACTIVATE_CAPABILITY, "schema.agent-admin.activation.confirm.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeActivationAttempted", true)); }
  private SurfaceAction agentActivationCancelAction() { return new SurfaceAction("action-agent-activation-cancel", "Cancel activation", "command", browserToolId("action-agent-activation-cancel"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-admin.activation.cancel.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentActivationConfirmationCancelled", true)); }
  private SurfaceAction agentActivationOpenProposalAction() { return new SurfaceAction("action-agent-activation-open-proposal", "Open approved proposal", "read", browserToolId("action-agent-activation-open-proposal"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-admin.activation.open-proposal.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentActivationProposalOpened", true)); }
  private SurfaceAction agentActivationOpenTraceAction() { return new SurfaceAction("action-agent-activation-open-trace", "Open activation trace", "trace", browserToolId("action-agent-activation-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentActivationTraceOpened", true)); }
  private SurfaceAction deactivateAgentDefinitionAction() { return new SurfaceAction("action-deactivate-agent-definition", "Review AgentDefinition deactivation", "surface-request", browserToolId("action-deactivate-agent-definition"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-definition.lifecycle.deactivate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-deactivation-confirmation", "inline"), new Audit("AgentDefinitionDeactivationConfirmationDisplayed", true)); }
  private SurfaceAction agentDeactivationRefreshAction() { return new SurfaceAction("action-agent-deactivation-refresh", "Refresh deactivation confirmation", "read", browserToolId("action-agent-deactivation-refresh"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-admin.deactivation.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-deactivation-confirmation", "inline"), new Audit("AgentDeactivationConfirmationRefreshed", true)); }
  private SurfaceAction agentDeactivationConfirmAction(boolean disabled) { return new SurfaceAction("action-agent-deactivation-confirm", "Confirm deactivation", "command", browserToolId("action-agent-deactivation-confirm"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-admin.deactivation.confirm.v1", true, true, disabled ? new DisabledReason("already-deactivated", "This managed agent is already deactivated; repeat confirmation is idempotent and performs no source-artifact deletion.") : null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDeactivationAttempted", true)); }
  private SurfaceAction agentDeactivationCancelAction() { return new SurfaceAction("action-agent-deactivation-cancel", "Cancel deactivation", "command", browserToolId("action-agent-deactivation-cancel"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-admin.deactivation.cancel.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDeactivationConfirmationCancelled", true)); }
  private SurfaceAction agentDeactivationOpenProposalAction() { return new SurfaceAction("action-agent-deactivation-open-proposal", "Open lifecycle proposal", "read", browserToolId("action-agent-deactivation-open-proposal"), governedToolId(AGENT_DEFINITIONS_MANAGE_CAPABILITY), AGENT_DEFINITIONS_MANAGE_CAPABILITY, "schema.agent-admin.deactivation.open-proposal.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentDeactivationProposalOpened", true)); }
  private SurfaceAction agentDeactivationOpenTraceAction() { return new SurfaceAction("action-agent-deactivation-open-trace", "Open deactivation trace", "trace", browserToolId("action-agent-deactivation-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentDeactivationTraceOpened", true)); }
  private SurfaceAction agentRollbackRefreshAction() { return new SurfaceAction("action-agent-rollback-refresh", "Refresh rollback confirmation", "read", browserToolId("action-agent-rollback-refresh"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.rollback.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-rollback-confirmation", "inline"), new Audit("AgentRollbackConfirmationRefreshed", true)); }
  private SurfaceAction agentRollbackConfirmAction(boolean disabled) { return new SurfaceAction("action-agent-rollback-confirm", "Confirm rollback", "command", browserToolId("action-agent-rollback-confirm"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.rollback.confirm.v1", true, true, disabled ? new DisabledReason("rollback-metadata-required", "Rollback requires an activated behavior proposal with stored rollback metadata; this starter fails closed instead of fabricating rollback state.") : null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentBehaviorRollbackAttempted", true)); }
  private SurfaceAction agentRollbackCancelAction() { return new SurfaceAction("action-agent-rollback-cancel", "Cancel rollback", "command", browserToolId("action-agent-rollback-cancel"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.rollback.cancel.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentRollbackConfirmationCancelled", true)); }
  private SurfaceAction agentRollbackOpenProposalAction() { return new SurfaceAction("action-agent-rollback-open-proposal", "Open activated proposal", "read", browserToolId("action-agent-rollback-open-proposal"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.rollback.open-proposal.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentRollbackProposalOpened", true)); }
  private SurfaceAction agentRollbackOpenTraceAction() { return new SurfaceAction("action-agent-rollback-open-trace", "Open rollback trace", "trace", browserToolId("action-agent-rollback-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentRollbackTraceOpened", true)); }
  private SurfaceAction importAgentSeedDefaultsAction() { return new SurfaceAction("action-import-agent-seed-defaults", "Import missing seed defaults", "workflow", browserToolId("action-import-agent-seed-defaults"), governedToolId(AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "schema.agent-seed.import-defaults.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedDefaultsImported", true)); }
  private SurfaceAction agentSeedMaterialRefreshAction() { return new SurfaceAction("action-agent-seed-material-refresh", "Refresh seed material", "read", browserToolId("action-agent-seed-material-refresh"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, "schema.agent-admin.seed-material.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialListed", true)); }
  private SurfaceAction agentSeedMaterialSearchAction() { return new SurfaceAction("action-agent-seed-material-search", "Search seed material", "read", browserToolId("action-agent-seed-material-search"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, "schema.agent-admin.seed-material.search.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialSearched", true)); }
  private SurfaceAction agentSeedMaterialResetFiltersAction() { return new SurfaceAction("action-agent-seed-material-reset-filters", "Reset seed filters", "read", browserToolId("action-agent-seed-material-reset-filters"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, "schema.agent-admin.seed-material.reset-filters.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialFiltersReset", true)); }
  private SurfaceAction agentSeedMaterialOpenProvenanceAction() { return new SurfaceAction("action-agent-seed-material-open-provenance", "Open seed provenance", "read", browserToolId("action-agent-seed-material-open-provenance"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, "schema.agent-admin.seed-material.open-provenance.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialProvenanceOpened", true)); }
  private SurfaceAction agentSeedMaterialPrepareImportAction() { return new SurfaceAction("action-agent-seed-material-prepare-import", "Prepare import", "workflow", browserToolId("action-agent-seed-material-prepare-import"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, "schema.agent-admin.seed-material.prepare-import.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedImportPrepared", true)); }
  private SurfaceAction agentSeedMaterialStartImportAction() { return new SurfaceAction("action-agent-seed-material-start-import", "Start acknowledged import", "workflow", browserToolId("action-agent-seed-material-start-import"), governedToolId(AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "schema.agent-admin.seed-material.start-import.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedDefaultsImported", true)); }
  private SurfaceAction agentSeedMaterialCancelImportAction() { return new SurfaceAction("action-agent-seed-material-cancel-import", "Cancel import", "command", browserToolId("action-agent-seed-material-cancel-import"), governedToolId(AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY), AGENT_ADMIN_RESEED_DEFAULTS_CAPABILITY, "schema.agent-admin.seed-material.cancel-import.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedImportCancelled", true)); }
  private SurfaceAction agentSeedMaterialOpenAgentDetailAction() { return new SurfaceAction("action-agent-seed-material-open-agent-detail", "Open target agent detail", "read", browserToolId("action-agent-seed-material-open-agent-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, "schema.agent-admin.seed-material.open-agent-detail.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction agentSeedMaterialOpenTraceAction() { return new SurfaceAction("action-agent-seed-material-open-trace", "Open seed trace", "trace", browserToolId("action-agent-seed-material-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentSeedMaterialTraceOpened", true)); }
  private SurfaceAction agentSeedMaterialBackToSourceAction() { return new SurfaceAction("action-agent-seed-material-back-to-source", "Back to source", "read", browserToolId("action-agent-seed-material-back-to-source"), governedToolId(AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY), AGENT_ADMIN_LIST_DEFINITIONS_CAPABILITY, "schema.agent-admin.seed-material.back-to-source.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-catalog", "inline"), new Audit("AgentSeedMaterialSourceReturned", true)); }
  private SurfaceAction proposePromptDiffAction() { return new SurfaceAction("action-propose-prompt-diff", "Propose prompt diff", "proposal", browserToolId("action-propose-prompt-diff"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.prompt-version.proposal.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-prompt-governance", "side-panel"), new Audit("PromptVersionDraftProposed", true)); }
  private SurfaceAction testPromptAction() { return new SurfaceAction("action-test-agent-prompt", "Run no-side-effect prompt test", "workflow", browserToolId("action-test-agent-prompt"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-runtime.test.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface("workflow-status", null, "inline"), new Audit("AgentRuntimeTestRequested", true)); }
  private SurfaceAction agentTestConsoleRefreshAction() { return new SurfaceAction("action-agent-test-console-refresh", "Refresh test console", "read", browserToolId("action-agent-test-console-refresh"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-admin.test-console.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentTestConsoleRefreshed", true)); }
  private SurfaceAction agentTestConsoleRunAction() { return new SurfaceAction("action-agent-test-console-run", "Run no-side-effect runtime test", "workflow", browserToolId("action-agent-test-console-run"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-admin.test-console.run.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentRuntimeTestRequested", true)); }
  private SurfaceAction agentTestConsoleReadResultAction() { return new SurfaceAction("action-agent-test-console-read-result", "Read test result", "read", browserToolId("action-agent-test-console-read-result"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-admin.test-console.read-result.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentRuntimeTestRead", true)); }
  private SurfaceAction agentTestConsoleOpenProposalAction(boolean enabled) { return new SurfaceAction("action-agent-test-console-open-proposal", "Open behavior proposal", "proposal", browserToolId("action-agent-test-console-open-proposal"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.test-console.open-proposal.v1", true, false, enabled ? null : new DisabledReason("blocked_provider_or_runtime", "Behavior proposal routing is disabled until provider/runtime evidence exists; advisory output cannot activate artifacts directly."), new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentTestConsoleProposalOpened", true)); }
  private SurfaceAction agentTestConsoleOpenTraceAction() { return new SurfaceAction("action-agent-test-console-open-trace", "Open test trace", "trace", browserToolId("action-agent-test-console-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentTestConsoleTraceOpened", true)); }
  private SurfaceAction agentTestConsoleBackToDetailAction() { return new SurfaceAction("action-agent-test-console-back-to-detail", "Back to agent detail", "read", browserToolId("action-agent-test-console-back-to-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction agentPromptGovernanceRefreshAction() { return new SurfaceAction("action-agent-prompt-governance-refresh", "Refresh prompt governance", "read", browserToolId("action-agent-prompt-governance-refresh"), governedToolId(AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY), AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY, "schema.agent-admin.prompt-governance.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-prompt-governance", "inline"), new Audit("AgentPromptGovernanceRefreshed", true)); }
  private SurfaceAction agentPromptGovernanceSimulateAction() { return new SurfaceAction("action-agent-prompt-governance-simulate", "Run no-side-effect prompt simulation", "workflow", browserToolId("action-agent-prompt-governance-simulate"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-admin.prompt-governance.simulate.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentPromptGovernanceSimulationRequested", true)); }
  private SurfaceAction agentPromptGovernanceSubmitReviewAction() { return new SurfaceAction("action-agent-prompt-governance-submit-review", "Submit prompt change for review", "proposal", browserToolId("action-agent-prompt-governance-submit-review"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.prompt-governance.submit-review.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentPromptGovernanceReviewSubmitted", true)); }
  private SurfaceAction agentPromptGovernanceApproveAction() { return new SurfaceAction("action-agent-prompt-governance-approve", "Approve prompt governance review", "approval", browserToolId("action-agent-prompt-governance-approve"), governedToolId(AGENT_ADMIN_REVIEW_CAPABILITY), AGENT_ADMIN_REVIEW_CAPABILITY, "schema.agent-admin.prompt-governance.approve.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentPromptGovernanceApproved", true)); }
  private SurfaceAction agentPromptGovernanceRejectAction() { return new SurfaceAction("action-agent-prompt-governance-reject", "Reject prompt governance review", "approval", browserToolId("action-agent-prompt-governance-reject"), governedToolId(AGENT_ADMIN_REJECT_CAPABILITY), AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.prompt-governance.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentPromptGovernanceRejected", true)); }
  private SurfaceAction agentPromptGovernanceOpenRiskReviewAction() { return new SurfaceAction("action-agent-prompt-governance-open-risk-review", "Open prompt-risk review", "read", browserToolId("action-agent-prompt-governance-open-risk-review"), governedToolId(AgentAdminPromptRiskReviewService.READ_CAPABILITY), AgentAdminPromptRiskReviewService.READ_CAPABILITY, "schema.agent-admin.prompt-governance.open-risk-review.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewRead", true)); }
  private SurfaceAction agentPromptGovernanceOpenTraceAction() { return new SurfaceAction("action-agent-prompt-governance-open-trace", "Open prompt governance trace", "trace", browserToolId("action-agent-prompt-governance-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentPromptGovernanceTraceOpened", true)); }
  private SurfaceAction agentPromptGovernanceBackToDetailAction() { return new SurfaceAction("action-agent-prompt-governance-back-to-detail", "Back to agent detail", "read", browserToolId("action-agent-prompt-governance-back-to-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction agentSkillManifestRefreshAction() { return new SurfaceAction("action-agent-skill-manifest-refresh", "Refresh skill manifest review", "read", browserToolId("action-agent-skill-manifest-refresh"), governedToolId(AGENT_ADMIN_GET_MANIFEST_CAPABILITY), AGENT_ADMIN_GET_MANIFEST_CAPABILITY, "schema.agent-admin.skill-manifest.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-skill-manifest-diff", "inline"), new Audit("AgentSkillManifestRefreshed", true)); }
  private SurfaceAction agentSkillManifestSimulateAction() { return new SurfaceAction("action-agent-skill-manifest-simulate", "Run no-side-effect manifest simulation", "workflow", browserToolId("action-agent-skill-manifest-simulate"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-admin.skill-manifest.simulate.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentSkillManifestSimulationRequested", true)); }
  private SurfaceAction agentSkillManifestSubmitReviewAction() { return new SurfaceAction("action-agent-skill-manifest-submit-review", "Submit manifest change for review", "proposal", browserToolId("action-agent-skill-manifest-submit-review"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.skill-manifest.submit-review.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentSkillManifestReviewSubmitted", true)); }
  private SurfaceAction agentSkillManifestApproveAction() { return new SurfaceAction("action-agent-skill-manifest-approve", "Approve manifest review", "approval", browserToolId("action-agent-skill-manifest-approve"), governedToolId(AGENT_ADMIN_REVIEW_CAPABILITY), AGENT_ADMIN_REVIEW_CAPABILITY, "schema.agent-admin.skill-manifest.approve.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentSkillManifestApproved", true)); }
  private SurfaceAction agentSkillManifestRejectAction() { return new SurfaceAction("action-agent-skill-manifest-reject", "Reject manifest review", "approval", browserToolId("action-agent-skill-manifest-reject"), governedToolId(AGENT_ADMIN_REJECT_CAPABILITY), AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.skill-manifest.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentSkillManifestRejected", true)); }
  private SurfaceAction agentSkillManifestOpenToolBoundaryAction() { return new SurfaceAction("action-agent-skill-manifest-open-tool-boundary", "Open related tool-boundary review", "read", browserToolId("action-agent-skill-manifest-open-tool-boundary"), governedToolId(AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, "schema.agent-admin.skill-manifest.open-tool-boundary.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("AgentToolBoundaryDisplayed", true)); }
  private SurfaceAction agentSkillManifestOpenModelRefsAction() { return new SurfaceAction("action-agent-skill-manifest-open-model-refs", "Open related model references", "read", browserToolId("action-agent-skill-manifest-open-model-refs"), governedToolId(AGENT_ADMIN_GET_MODEL_REF_CAPABILITY), AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, "schema.agent-admin.skill-manifest.open-model-refs.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-model-refs", "inline"), new Audit("AgentModelRefsDisplayed", true)); }
  private SurfaceAction agentSkillManifestOpenTraceAction() { return new SurfaceAction("action-agent-skill-manifest-open-trace", "Open manifest review trace", "trace", browserToolId("action-agent-skill-manifest-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentSkillManifestTraceOpened", true)); }
  private SurfaceAction agentSkillManifestBackToDetailAction() { return new SurfaceAction("action-agent-skill-manifest-back-to-detail", "Back to agent detail", "read", browserToolId("action-agent-skill-manifest-back-to-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction agentToolBoundaryRefreshAction() { return new SurfaceAction("action-agent-tool-boundary-refresh", "Refresh tool-boundary review", "read", browserToolId("action-agent-tool-boundary-refresh"), governedToolId(AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, "schema.agent-admin.tool-boundary.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("AgentToolBoundaryRefreshed", true)); }
  private SurfaceAction agentToolBoundarySimulateAction() { return new SurfaceAction("action-agent-tool-boundary-simulate", "Run no-side-effect tool-boundary simulation", "workflow", browserToolId("action-agent-tool-boundary-simulate"), governedToolId(AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, "schema.agent-admin.tool-boundary.simulate.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentToolBoundarySimulationRequested", true)); }
  private SurfaceAction agentToolBoundarySubmitReviewAction() { return new SurfaceAction("action-agent-tool-boundary-submit-review", "Submit tool-boundary change for review", "proposal", browserToolId("action-agent-tool-boundary-submit-review"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.tool-boundary.submit-review.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentToolBoundaryReviewSubmitted", true)); }
  private SurfaceAction agentToolBoundaryApproveAction() { return new SurfaceAction("action-agent-tool-boundary-approve", "Approve tool-boundary review", "approval", browserToolId("action-agent-tool-boundary-approve"), governedToolId(AGENT_ADMIN_REVIEW_CAPABILITY), AGENT_ADMIN_REVIEW_CAPABILITY, "schema.agent-admin.tool-boundary.approve.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentToolBoundaryApproved", true)); }
  private SurfaceAction agentToolBoundaryRejectAction() { return new SurfaceAction("action-agent-tool-boundary-reject", "Reject tool-boundary review", "approval", browserToolId("action-agent-tool-boundary-reject"), governedToolId(AGENT_ADMIN_REJECT_CAPABILITY), AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.tool-boundary.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentToolBoundaryRejected", true)); }
  private SurfaceAction agentToolBoundaryOpenModelRefsAction() { return new SurfaceAction("action-agent-tool-boundary-open-model-refs", "Open related model references", "read", browserToolId("action-agent-tool-boundary-open-model-refs"), governedToolId(AGENT_ADMIN_GET_MODEL_REF_CAPABILITY), AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, "schema.agent-admin.tool-boundary.open-model-refs.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-model-refs", "inline"), new Audit("AgentModelRefsDisplayed", true)); }
  private SurfaceAction agentToolBoundaryOpenTraceAction() { return new SurfaceAction("action-agent-tool-boundary-open-trace", "Open tool-boundary review trace", "trace", browserToolId("action-agent-tool-boundary-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentToolBoundaryTraceOpened", true)); }
  private SurfaceAction agentToolBoundaryBackToDetailAction() { return new SurfaceAction("action-agent-tool-boundary-back-to-detail", "Back to agent detail", "read", browserToolId("action-agent-tool-boundary-back-to-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction agentModelRefsRefreshAction() { return new SurfaceAction("action-agent-model-refs-refresh", "Refresh model-reference review", "read", browserToolId("action-agent-model-refs-refresh"), governedToolId(AGENT_ADMIN_GET_MODEL_REF_CAPABILITY), AGENT_ADMIN_GET_MODEL_REF_CAPABILITY, "schema.agent-admin.model-refs.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-model-refs", "inline"), new Audit("AgentModelRefsRefreshed", true)); }
  private SurfaceAction agentModelRefsRunTestAction(boolean providerReady) { return new SurfaceAction("action-agent-model-refs-run-test", "Run no-side-effect model readiness test", "workflow", browserToolId("action-agent-model-refs-run-test"), governedToolId(AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY, "schema.agent-admin.model-refs.run-test.v1", false, false, providerReady ? null : new DisabledReason("blocked_provider_or_runtime", "Provider/runtime readiness is not configured for a real model smoke; no fixture or model-less success is counted."), new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-test-console", "inline"), new Audit("AgentModelRefsReadinessTestRequested", true)); }
  private SurfaceAction agentModelRefsSubmitReviewAction() { return new SurfaceAction("action-agent-model-refs-submit-review", "Submit model-reference change for review", "proposal", browserToolId("action-agent-model-refs-submit-review"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.model-refs.submit-review.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentModelRefsReviewSubmitted", true)); }
  private SurfaceAction agentModelRefsApproveAction() { return new SurfaceAction("action-agent-model-refs-approve", "Approve model-reference review", "approval", browserToolId("action-agent-model-refs-approve"), governedToolId(AGENT_ADMIN_REVIEW_CAPABILITY), AGENT_ADMIN_REVIEW_CAPABILITY, "schema.agent-admin.model-refs.approve.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentModelRefsApproved", true)); }
  private SurfaceAction agentModelRefsRejectAction() { return new SurfaceAction("action-agent-model-refs-reject", "Reject model-reference review", "approval", browserToolId("action-agent-model-refs-reject"), governedToolId(AGENT_ADMIN_REJECT_CAPABILITY), AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.model-refs.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentModelRefsRejected", true)); }
  private SurfaceAction agentModelRefsOpenPromptGovernanceAction() { return new SurfaceAction("action-agent-model-refs-open-prompt-governance", "Open related prompt governance", "read", browserToolId("action-agent-model-refs-open-prompt-governance"), governedToolId(AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY), AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY, "schema.agent-admin.model-refs.open-prompt-governance.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-prompt-governance", "inline"), new Audit("AgentPromptGovernanceOpened", true)); }
  private SurfaceAction agentModelRefsOpenToolBoundaryAction() { return new SurfaceAction("action-agent-model-refs-open-tool-boundary", "Open related tool-boundary review", "read", browserToolId("action-agent-model-refs-open-tool-boundary"), governedToolId(AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_GET_TOOL_BOUNDARY_CAPABILITY, "schema.agent-admin.model-refs.open-tool-boundary.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("AgentToolBoundaryDisplayed", true)); }
  private SurfaceAction agentModelRefsOpenTraceAction() { return new SurfaceAction("action-agent-model-refs-open-trace", "Open model-reference trace", "trace", browserToolId("action-agent-model-refs-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentModelRefsTraceOpened", true)); }
  private SurfaceAction agentModelRefsBackToDetailAction() { return new SurfaceAction("action-agent-model-refs-back-to-detail", "Back to agent detail", "read", browserToolId("action-agent-model-refs-back-to-detail"), governedToolId(AGENT_ADMIN_GET_DEFINITION_CAPABILITY), AGENT_ADMIN_GET_DEFINITION_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentDefinitionDetailDisplayed", true)); }
  private SurfaceAction agentBehaviorProposalRefreshAction() { return new SurfaceAction("action-agent-behavior-proposal-refresh", "Refresh behavior proposal", "read", browserToolId("action-agent-behavior-proposal-refresh"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.behavior-proposal.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorProposalRefreshed", true)); }
  private SurfaceAction agentBehaviorProposalSubmitAction(boolean disabled) { return new SurfaceAction("action-agent-behavior-proposal-submit", "Submit proposal for review", "proposal", browserToolId("action-agent-behavior-proposal-submit"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.behavior-proposal.submit.v1", true, false, disabled ? new DisabledReason("proposal-not-submittable", "Submit requires a visible draft/proposed behavior proposal in this selected AuthContext.") : null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorProposalSubmitted", true)); }
  private SurfaceAction agentBehaviorProposalApproveAction(boolean disabled) { return new SurfaceAction("action-agent-behavior-proposal-approve", "Approve proposal", "approval", browserToolId("action-agent-behavior-proposal-approve"), governedToolId(AGENT_ADMIN_REVIEW_CAPABILITY), AGENT_ADMIN_REVIEW_CAPABILITY, "schema.agent-admin.behavior-proposal.approve.v1", true, true, disabled ? new DisabledReason("blocked_provider_or_runtime", "Approval is disabled until a visible in-review proposal has required evidence and provider/runtime readiness; no fixture/model-less success is accepted.") : null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorProposalApproved", true)); }
  private SurfaceAction agentBehaviorProposalRejectAction(boolean disabled) { return new SurfaceAction("action-agent-behavior-proposal-reject", "Reject proposal", "approval", browserToolId("action-agent-behavior-proposal-reject"), governedToolId(AGENT_ADMIN_REJECT_CAPABILITY), AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.behavior-proposal.reject.v1", true, true, disabled ? new DisabledReason("proposal-not-rejectable", "Reject requires a visible in-review or approved proposal and a browser-safe reason.") : null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorProposalRejected", true)); }
  private SurfaceAction agentBehaviorProposalDeferAction(boolean disabled) { return new SurfaceAction("action-agent-behavior-proposal-defer", "Defer proposal", "approval", browserToolId("action-agent-behavior-proposal-defer"), governedToolId(AGENT_ADMIN_CANCEL_CAPABILITY), AGENT_ADMIN_CANCEL_CAPABILITY, "schema.agent-admin.behavior-proposal.defer.v1", true, true, disabled ? new DisabledReason("proposal-not-deferrable", "Defer requires a visible proposal and a reason/follow-up category; it never approves or activates behavior.") : null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorProposalDeferred", true)); }
  private SurfaceAction agentBehaviorProposalCancelAction(boolean disabled) { return new SurfaceAction("action-agent-behavior-proposal-cancel", "Cancel proposal interaction", "command", browserToolId("action-agent-behavior-proposal-cancel"), governedToolId(AGENT_ADMIN_CANCEL_CAPABILITY), AGENT_ADMIN_CANCEL_CAPABILITY, "schema.agent-admin.behavior-proposal.cancel.v1", true, false, disabled ? new DisabledReason("proposal-not-cancellable", "Cancel requires a visible policy-allowed proposal and never deletes source artifacts.") : null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-detail", "inline"), new Audit("AgentBehaviorProposalCancelled", true)); }
  private SurfaceAction agentBehaviorProposalOpenActivationAction(boolean disabled) { return new SurfaceAction("action-agent-behavior-proposal-open-activation", "Open activation confirmation", "surface-request", browserToolId("action-agent-behavior-proposal-open-activation"), governedToolId(AGENT_ADMIN_ACTIVATE_CAPABILITY), AGENT_ADMIN_ACTIVATE_CAPABILITY, "schema.agent-admin.behavior-proposal.open-activation.v1", false, false, disabled ? new DisabledReason("activation-route-disabled", "Activation requires an approved proposal plus provider/runtime, policy, version, tool-boundary, scope, and confirmation prerequisites.") : null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-activation-confirmation", "inline"), new Audit("AgentBehaviorProposalActivationOpened", true)); }
  private SurfaceAction agentBehaviorProposalOpenRollbackAction(boolean disabled) { return new SurfaceAction("action-agent-behavior-proposal-open-rollback", "Open rollback confirmation", "surface-request", browserToolId("action-agent-behavior-proposal-open-rollback"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.behavior-proposal.open-rollback.v1", false, false, disabled ? new DisabledReason("rollback-route-disabled", "Rollback requires a visible previously activated proposal with stored rollback metadata and target version.") : null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-rollback-confirmation", "inline"), new Audit("AgentBehaviorProposalRollbackOpened", true)); }
  private SurfaceAction agentBehaviorProposalOpenSourceAction() { return new SurfaceAction("action-agent-behavior-proposal-open-source", "Open source review", "read", browserToolId("action-agent-behavior-proposal-open-source"), governedToolId(AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY), AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY, "schema.agent-admin.behavior-proposal.open-source.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-prompt-governance", "inline"), new Audit("AgentBehaviorProposalSourceOpened", true)); }
  private SurfaceAction agentBehaviorProposalOpenTraceAction() { return new SurfaceAction("action-agent-behavior-proposal-open-trace", "Open proposal trace", "trace", browserToolId("action-agent-behavior-proposal-open-trace"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentBehaviorProposalTraceOpened", true)); }
  private SurfaceAction approveSkillManifestAction() { return new SurfaceAction("action-approve-skill-manifest", "Approve manifest review", "approval", browserToolId("action-approve-skill-manifest"), governedToolId(AGENT_ADMIN_REVIEW_CAPABILITY), AGENT_ADMIN_REVIEW_CAPABILITY, null, true, true, null, new Idempotency(true, "surface-item"), new ResultSurface(null, "surface-agent-skill-manifest-diff", "inline"), new Audit("AgentSkillManifestApproved", true)); }
  private SurfaceAction submitBehaviorChangeAction() { return new SurfaceAction("action-submit-behavior-change", "Submit behavior change for review", "proposal", browserToolId("action-submit-behavior-change"), governedToolId(AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY), AGENT_ADMIN_SUBMIT_REVIEW_CAPABILITY, "schema.agent-admin.behavior-change.submit.v1", true, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeSubmitted", true)); }
  private SurfaceAction rejectBehaviorChangeAction() { return new SurfaceAction("action-reject-behavior-change", "Reject behavior change", "approval", browserToolId("action-reject-behavior-change"), governedToolId(AGENT_ADMIN_REJECT_CAPABILITY), AGENT_ADMIN_REJECT_CAPABILITY, "schema.agent-admin.behavior-change.reject.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeRejected", true)); }
  private SurfaceAction activateBehaviorChangeAction() { return new SurfaceAction("action-activate-behavior-change", "Activate approved behavior change", "command", browserToolId("action-activate-behavior-change"), governedToolId(AGENT_ADMIN_ACTIVATE_CAPABILITY), AGENT_ADMIN_ACTIVATE_CAPABILITY, "schema.agent-admin.behavior-change.activate.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeActivated", true)); }
  private SurfaceAction cancelBehaviorChangeAction() { return new SurfaceAction("action-cancel-behavior-change", "Cancel behavior change", "command", browserToolId("action-cancel-behavior-change"), governedToolId(AGENT_ADMIN_CANCEL_CAPABILITY), AGENT_ADMIN_CANCEL_CAPABILITY, "schema.agent-admin.behavior-change.cancel.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeCancelled", true)); }
  private SurfaceAction rollbackBehaviorChangeAction() { return new SurfaceAction("action-rollback-behavior-change", "Rollback activated behavior change", "command", browserToolId("action-rollback-behavior-change"), governedToolId(AGENT_ADMIN_ROLLBACK_CAPABILITY), AGENT_ADMIN_ROLLBACK_CAPABILITY, "schema.agent-admin.behavior-change.rollback.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentBehaviorChangeRolledBack", true)); }
  private SurfaceAction simulateToolBoundaryAction() { return new SurfaceAction("action-simulate-tool-boundary", "Simulate tool boundary change", "governance", browserToolId("action-simulate-tool-boundary"), governedToolId(AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY), AGENT_ADMIN_SIMULATE_TOOL_BOUNDARY_CAPABILITY, "schema.tool-boundary.simulation.v1", false, false, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-tool-boundary-diff", "inline"), new Audit("ToolBoundarySimulationRequested", true)); }
  private SurfaceAction manageModelRefAction() { return new SurfaceAction("action-manage-model-ref", "Request model ref change", "proposal", browserToolId("action-manage-model-ref"), governedToolId(AGENT_ADMIN_ACTIVATE_CAPABILITY), AGENT_ADMIN_ACTIVATE_CAPABILITY, null, false, false, new DisabledReason("MODEL_POLICY_DENIED", "This starter denies switching to a disabled provider alias; provider secrets remain redacted."), new Idempotency(true, "client-generated"), new ResultSurface("decision", null, "inline"), new Audit("AgentModelRefChangeDenied", true)); }
  private SurfaceAction listAgentSeedMaterialAction() { return new SurfaceAction("action-list-agent-seed-material", "List seed material", "read", browserToolId("action-list-agent-seed-material"), governedToolId(AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY), AGENT_ADMIN_LIST_SEED_MATERIAL_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-seed-material", "inline"), new Audit("AgentSeedMaterialListed", true)); }
  private SurfaceAction startPromptRiskReviewAction() { return new SurfaceAction("action-agent-prompt-risk-review-start", "Start prompt-risk review", "workflow", browserToolId("action-agent-prompt-risk-review-start"), governedToolId(AgentAdminPromptRiskReviewService.START_CAPABILITY), AgentAdminPromptRiskReviewService.START_CAPABILITY, "schema.agent-admin.prompt-risk-review.start.v1", true, true, null, new Idempotency(true, "client-generated"), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewStarted", true)); }
  private SurfaceAction readPromptRiskReviewAction() { return new SurfaceAction("action-agent-prompt-risk-review-read", "Read prompt-risk review", "read", browserToolId("action-agent-prompt-risk-review-read"), governedToolId(AgentAdminPromptRiskReviewService.READ_CAPABILITY), AgentAdminPromptRiskReviewService.READ_CAPABILITY, "schema.agent-admin.prompt-risk-review.read.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewRead", true)); }
  private SurfaceAction cancelPromptRiskReviewAction() { return new SurfaceAction("action-agent-prompt-risk-review-cancel", "Cancel prompt-risk review", "command", browserToolId("action-agent-prompt-risk-review-cancel"), governedToolId(AgentAdminPromptRiskReviewService.CANCEL_CAPABILITY), AgentAdminPromptRiskReviewService.CANCEL_CAPABILITY, "schema.agent-admin.prompt-risk-review.cancel.v1", true, true, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-prompt-risk-review", "inline"), new Audit("AgentAdminPromptRiskReviewCancelled", true)); }
  private SurfaceAction acceptPromptRiskReviewAction() { return new SurfaceAction("action-agent-prompt-risk-review-accept", "Accept prompt-risk review result", "approval", browserToolId("action-agent-prompt-risk-review-accept"), governedToolId(AgentAdminPromptRiskReviewService.ACCEPT_RESULT_CAPABILITY), AgentAdminPromptRiskReviewService.ACCEPT_RESULT_CAPABILITY, "schema.agent-admin.prompt-risk-review.accept.v1", true, true, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentAdminPromptRiskReviewAccepted", true)); }
  private SurfaceAction rejectPromptRiskReviewAction() { return new SurfaceAction("action-agent-prompt-risk-review-reject", "Reject prompt-risk review result", "approval", browserToolId("action-agent-prompt-risk-review-reject"), governedToolId(AgentAdminPromptRiskReviewService.REJECT_RESULT_CAPABILITY), AgentAdminPromptRiskReviewService.REJECT_RESULT_CAPABILITY, "schema.agent-admin.prompt-risk-review.reject.v1", true, true, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-behavior-proposal", "inline"), new Audit("AgentAdminPromptRiskReviewRejected", true)); }
  private SurfaceAction promptRiskReviewOpenSourceAction() { return new SurfaceAction("action-agent-prompt-risk-review-open-source", "Open source review", "read", browserToolId("action-agent-prompt-risk-review-open-source"), governedToolId(AgentAdminPromptRiskReviewService.READ_CAPABILITY), AgentAdminPromptRiskReviewService.READ_CAPABILITY, "schema.agent-admin.prompt-risk-review.open-source.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-prompt-governance", "inline"), new Audit("AgentAdminPromptRiskReviewSourceOpened", true)); }
  private SurfaceAction promptRiskReviewOpenTraceAction() { return new SurfaceAction("action-agent-prompt-risk-review-open-trace", "Open prompt-risk trace", "trace", browserToolId("action-agent-prompt-risk-review-open-trace"), governedToolId("audit.trace.read"), "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentAdminPromptRiskReviewTraceOpened", true)); }
  private SurfaceAction legacyPromptRiskReviewAction(String actionId, SurfaceAction canonical) { return new SurfaceAction(actionId, canonical.label(), canonical.intent(), browserToolId(actionId), canonical.governedToolId(), canonical.capabilityId(), canonical.inputSchemaRef(), canonical.requiresConfirmation(), canonical.requiresApproval(), canonical.disabled(), canonical.idempotency(), canonical.resultSurface(), canonical.audit()); }
  private SurfaceAction openAgentTraceAction() { return new SurfaceAction("action-open-agent-trace", "Open agent work trace", "trace", browserToolId("action-open-agent-trace"), governedToolId("audit.trace.read"), "audit.trace.read", null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "deep-link"), new Audit("AgentWorkTraceOpened", true)); }
  private List<SurfaceAction> agentAdminTraceActions() { return List.of(agentAdminTraceRefreshAction(), agentAdminTraceDrillDownAction(), agentAdminTraceExportAction(), agentAdminTraceEscalateAction(), agentAdminTraceBackToSourceAction()); }
  private SurfaceAction agentAdminTraceRefreshAction() { return new SurfaceAction("action-agent-admin-trace-refresh", "Refresh Agent Admin trace", "read", browserToolId("action-agent-admin-trace-refresh"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, "schema.agent-admin.trace.refresh.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "inline"), new Audit("AgentAdminTraceRefreshed", true)); }
  private SurfaceAction agentAdminTraceDrillDownAction() { return new SurfaceAction("action-agent-admin-trace-drill-down", "Open event detail", "trace", browserToolId("action-agent-admin-trace-drill-down"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, "schema.agent-admin.trace.drill-down.v1", false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "inline"), new Audit("AgentAdminTraceEventDrilledDown", true)); }
  private SurfaceAction agentAdminTraceExportAction() { return new SurfaceAction("action-agent-admin-trace-export", "Request redacted export", "approval", browserToolId("action-agent-admin-trace-export"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, "schema.agent-admin.trace.export.v1", true, true, new DisabledReason("audit-trace-export-surface-required", "Redacted export requires the Audit/Trace export-request surface and acknowledgement; raw Agent Admin trace dumps are never returned to the browser."), new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "inline"), new Audit("AgentAdminTraceExportRequestBlocked", true)); }
  private SurfaceAction agentAdminTraceEscalateAction() { return new SurfaceAction("action-agent-admin-trace-escalate", "Escalate trace evidence", "command", browserToolId("action-agent-admin-trace-escalate"), governedToolId(AUDIT_TRACE_READ_CAPABILITY), AUDIT_TRACE_READ_CAPABILITY, "schema.agent-admin.trace.escalate.v1", true, false, new DisabledReason("audit-trace-escalation-workflow-required", "Escalation requires an Audit/Trace investigation workflow with a human-readable reason; this trace surface performs no managed-agent mutation."), new Idempotency(false, null), new ResultSurface(null, "surface-agent-admin-trace", "inline"), new Audit("AgentAdminTraceEscalationBlocked", true)); }
  private SurfaceAction agentAdminTraceBackToSourceAction() { return new SurfaceAction("action-agent-admin-trace-back-to-source", "Back to source review", "read", browserToolId("action-agent-admin-trace-back-to-source"), governedToolId(AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY), AGENT_ADMIN_GET_PROMPT_VERSION_CAPABILITY, null, false, false, null, new Idempotency(false, null), new ResultSurface(null, "surface-agent-prompt-governance", "inline"), new Audit("AgentAdminTraceSourceReturned", true)); }

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
    if (SAAS_OWNER_ORGANIZATION_RENAME_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_SUSPEND_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_REACTIVATE_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_MANAGE_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_ADMIN_INVITE_CAPABILITY.equals(capabilityId) || SAAS_OWNER_ORGANIZATION_ADMIN_MANAGE_CAPABILITY.equals(capabilityId)) return actor.selectedContext().capabilities().contains(SAAS_OWNER_TENANT_MANAGE_CAPABILITY);
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
  private String rolePreviewMembershipId(AuthContextResolver.ResolvedMe actor, Object input) {
    return stringInput(input, "membershipId", actor.selectedContext().membershipId());
  }

  private List<FoundationRole> rolePreviewRoles(AuthContextResolver.ResolvedMe actor, Object input, String membershipId) {
    if (input instanceof Map<?, ?> map && (map.containsKey("roles") || map.containsKey("role"))) return rolesInput(input);
    return userAdminService.listUsers(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(row -> row.membershipId().equals(membershipId))
        .findFirst()
        .map(UserAdminService.UserDirectoryRow::roles)
        .orElse(actor.selectedContext().roles());
  }

  private static Map<String, Object> inputMap(Object input) {
    if (!(input instanceof Map<?, ?> map)) return Map.of();
    var copy = new LinkedHashMap<String, Object>();
    map.forEach((key, value) -> copy.put(String.valueOf(key), value));
    return copy;
  }

  private static Map<String, Object> mergeInput(Object input, Map<String, Object> additions) {
    var merged = new LinkedHashMap<String, Object>();
    if (input instanceof Map<?, ?> map) map.forEach((key, value) -> merged.put(String.valueOf(key), value));
    merged.putAll(additions);
    return merged;
  }

  private static String stringInput(Object input, String key, String fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return value; return fallback; }
  private static Instant instantInput(Object input, String key) {
    var value = stringInput(input, key, null);
    if (value == null) return null;
    try {
      return Instant.parse(value);
    } catch (DateTimeParseException invalid) {
      throw new AuthorizationException(400, key + "-invalid-instant");
    }
  }
  private static String rawStringInput(Object input, String key, String fallback) { if (input instanceof Map<?, ?> map && map.get(key) instanceof String value) return value; return fallback; }
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
      if (map.get("roles") instanceof List<?> roles && !roles.isEmpty()) return roles.stream().map(String::valueOf).map(WorkstreamService::customerAdminRoleValue).toList();
      if (map.get("roles") instanceof String role && !role.isBlank()) return List.of(customerAdminRoleValue(role));
      if (map.get("role") instanceof String role && !role.isBlank()) return List.of(customerAdminRoleValue(role));
    }
    return List.of(FoundationRole.CUSTOMER_ADMIN);
  }

  private static FoundationRole customerAdminRoleValue(String role) {
    try {
      return FoundationRole.valueOf(role);
    } catch (IllegalArgumentException error) {
      throw new AuthorizationException(400, "unsupported-role");
    }
  }
  private static MembershipStatus membershipStatusInput(String value) {
    var normalized = Objects.requireNonNullElse(value, "removed").trim().toUpperCase(Locale.ROOT).replace('-', '_');
    return switch (normalized) {
      case "ACTIVE", "REACTIVATE", "REACTIVATED" -> MembershipStatus.ACTIVE;
      case "SUSPEND", "SUSPENDED", "DISABLE", "DISABLED" -> MembershipStatus.SUSPENDED;
      case "REMOVE", "REMOVED", "DEACTIVATED", "DELETED", "INACTIVE" -> MembershipStatus.REMOVED;
      default -> MembershipStatus.valueOf(normalized);
    };
  }

  private static String membershipActionDefaultStatus(String actionId) {
    return "action-useradmin-reactivate-member".equals(actionId) ? "active" : "removed";
  }

  private static String requestedMembershipOperation(String proposedStatus, MembershipStatus currentStatus) {
    var target = membershipStatusInput(proposedStatus);
    if (target == MembershipStatus.ACTIVE) return currentStatus == MembershipStatus.ACTIVE ? "reactivate_membership_noop" : "reactivate_membership";
    if (target == MembershipStatus.SUSPENDED) return "suspend_membership";
    return "remove_membership";
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
  private static Map<String, Object> mapValue(Object value) { return value instanceof Map<?, ?> map ? map.entrySet().stream().collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue, (left, right) -> right, LinkedHashMap::new)) : new LinkedHashMap<>(); }

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
