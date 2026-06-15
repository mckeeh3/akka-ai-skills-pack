import type { ApiClient } from '../../../api/ApiClient';
import type {
  AdminUser,
  ApiError,
  ApiResult,
  DecisionDetailResponse,
  DecisionSummary,
  GoalDetailResponse,
  GoalSummary,
  MeResponse,
  OrganizationDetailPayload,
  OrganizationSummary,
  PolicySummary,
  TraceDetailResponse,
  TraceSummary
} from '../../../api/types';

const tenantId = 'seed-tenant';

export class FixtureApiClient implements ApiClient {
  private me: MeResponse = {
    user: { id: 'seed-user-1', email: 'supervisor@example.test', displayName: 'Seed Supervisor' },
    memberships: [{ tenantId, tenantName: 'Seed tenant', status: 'active', roles: ['supervisor', 'reviewer'] }],
    activeTenantId: tenantId,
    preferences: { themeId: 'aurora-light' }
  };

  private users: AdminUser[] = [
    { userId: 'seed-user-1', email: 'supervisor@example.test', displayName: 'Seed Supervisor', membershipStatus: 'active', roles: ['supervisor', 'reviewer'], version: 1 },
    { userId: 'seed-user-2', email: 'auditor@example.test', displayName: 'Seed Auditor', membershipStatus: 'invited', roles: ['auditor'], version: 1 }
  ];

  private organizations: OrganizationSummary[] = [
    { organizationId: 'tenant-starter', organizationName: 'Starter Organization', status: 'active', traceRefs: ['trace-organization-tenant-starter'] },
    { organizationId: 'tenant-suspended', organizationName: 'Suspended Organization', status: 'suspended', traceRefs: ['trace-organization-tenant-suspended'] }
  ];

  private goalRows: GoalSummary[] = [
    { goalId: 'goal-1', objective: 'Stabilize onboarding exception review', ownerDisplayName: 'Seed Supervisor', priority: 'high', status: 'waiting_for_human', activePlanId: 'plan-1', pendingDecisionCount: 2, updatedAt: new Date().toISOString() },
    { goalId: 'goal-2', objective: 'Improve policy proposal quality', ownerDisplayName: 'Seed Supervisor', priority: 'normal', status: 'active', activePlanId: 'plan-2', pendingDecisionCount: 0, updatedAt: new Date().toISOString() }
  ];

  private decisionRows: DecisionSummary[] = [
    { decisionId: 'decision-1', type: 'recommendation_approval', title: 'Approve bounded outreach plan', originatingAgent: 'Planning Agent', linkedGoalId: 'goal-1', priority: 'high', status: 'open', riskScore: 72, confidenceScore: 84, impactEstimate: 'Moderate customer impact', policyTriggers: ['approval-gate.high-impact'], dueAt: new Date(Date.now() + 3600_000).toISOString(), version: 1 },
    { decisionId: 'decision-2', type: 'exception_resolution', title: 'Resolve missing evidence exception', originatingAgent: 'Evidence Agent', linkedGoalId: 'goal-1', priority: 'normal', status: 'open', riskScore: 48, confidenceScore: 61, impactEstimate: 'Low operational impact', policyTriggers: ['evidence.required'], version: 1 }
  ];

  private policies: PolicySummary[] = [
    { policyId: 'policy-1', name: 'High-impact action approval', activeVersion: 'v1', ownerRole: 'policy-owner', approvalGateCount: 3, openProposalCount: 1 },
    { policyId: 'policy-2', name: 'Agent evidence requirements', activeVersion: 'v1', ownerRole: 'policy-owner', approvalGateCount: 2, openProposalCount: 0 }
  ];

  private traces: TraceSummary[] = [
    { traceId: 'trace-1', category: 'decision', timestamp: new Date().toISOString(), actorLabel: 'Planning Agent', action: 'created decision card', targetLabel: 'decision-1', authorizationBasis: 'policy approval-gate.high-impact', correlationId: 'corr-fixture-1' },
    { traceId: 'trace-2', category: 'work', timestamp: new Date().toISOString(), actorLabel: 'Seed Supervisor', action: 'created goal', targetLabel: 'goal-1', authorizationBasis: 'role supervisor', correlationId: 'corr-fixture-2' }
  ];

  session = {
    getMe: () => delayedOk(this.me),
    updatePreferences: (request: { themeId: MeResponse['preferences']['themeId'] }) => {
      this.me = { ...this.me, preferences: { themeId: request.themeId } };
      return delayedOk({ preferences: this.me.preferences, correlationId: correlationId() });
    }
  };

  admin = {
    listUsers: () => delayedOk({ items: this.users }),
    inviteUser: (request: { email: string; roleIds: string[]; idempotencyKey: string; displayName?: string }) => {
      if (!request.email.includes('@')) return delayedError('validation_error', 'Enter a valid email address.', { email: ['Email must contain @.'] });
      if (!request.roleIds.length) return delayedError('validation_error', 'Select at least one role.', { roleIds: ['Role is required.'] });
      const existing = this.users.find((user) => user.email === request.email);
      if (existing?.membershipStatus === 'invited') return delayedOk({ invitationId: `invite-${existing.userId}`, status: 'resent' as const, invitedEmail: request.email, correlationId: correlationId() });
      const userId = `seed-user-${this.users.length + 1}`;
      this.users.push({ userId, email: request.email, displayName: request.displayName, membershipStatus: 'invited', roles: request.roleIds, version: 1 });
      return delayedOk({ invitationId: `invite-${userId}`, status: 'pending' as const, invitedEmail: request.email, correlationId: correlationId() });
    },
    updateRoles: (userId: string, request: { roleIds: string[]; reason?: string; expectedVersion: number }) => {
      const user = this.users.find((candidate) => candidate.userId === userId);
      if (!user) return delayedError('not_found', 'User is not visible in this tenant.');
      if (request.expectedVersion !== user.version) return delayedError('conflict', 'This role assignment changed. Refresh and try again.');
      if (request.roleIds.some((role) => ['tenant-admin', 'policy-owner'].includes(role)) && !request.reason) return delayedError('validation_error', 'Reason is required for elevated role grants.', { reason: ['Reason is required.'] });
      user.roles = request.roleIds;
      user.version += 1;
      return delayedOk({ userId, roleIds: user.roles, version: user.version, auditTraceId: 'trace-role-update', correlationId: correlationId() });
    },
    listOrganizations: (query?: Record<string, string | undefined>) => {
      const search = query?.query?.toLowerCase();
      const status = query?.status?.toLowerCase();
      const organizations = this.organizations.filter((organization) => (!search || organization.organizationName.toLowerCase().includes(search)) && (!status || organization.status.toLowerCase() === status));
      return delayedOk({ organizations, safeBoundaryNotice: organizationBoundaryNotice, traceRefs: ['trace-organization-list'], correlationId: correlationId(), redactions: organizationRedactions });
    },
    getOrganization: (organizationId: string) => {
      const organization = this.organizations.find((candidate) => candidate.organizationId === organizationId);
      if (!organization) return delayedError('not_found', 'Organization is not visible for this SaaS Owner context.');
      return delayedOk(organizationDetail(organization));
    },
    createOrganization: (request: { organizationName: string; idempotencyKey: string; reason?: string }) => {
      if (!request.organizationName.trim()) return delayedError('validation_error', 'Organization name is required.', { organizationName: ['Organization name is required.'] });
      if (!request.idempotencyKey.trim()) return delayedError('validation_error', 'Idempotency key is required.', { idempotencyKey: ['Required for Organization mutations.'] });
      const existing = this.organizations.find((candidate) => candidate.organizationName === request.organizationName);
      if (existing) return delayedOk({ status: 'no-op', message: 'Organization create replay returned the existing browser-safe Organization.', organization: organizationDetail(existing), traceRefs: ['trace-organization-create-no-op'], correlationId: correlationId() });
      const organization = { organizationId: `tenant-${this.organizations.length + 1}`, organizationName: request.organizationName, status: 'active', traceRefs: ['trace-organization-create'] };
      this.organizations.push(organization);
      return delayedOk({ status: 'accepted', message: 'Organization created as an active Tenant lifecycle boundary.', organization: organizationDetail(organization), traceRefs: ['trace-organization-create'], correlationId: correlationId() });
    },
    renameOrganization: (organizationId: string, request: { organizationName: string; idempotencyKey: string; reason?: string }) => {
      const organization = this.organizations.find((candidate) => candidate.organizationId === organizationId);
      if (!organization) return delayedError('not_found', 'Organization is not visible for this SaaS Owner context.');
      if (!request.organizationName.trim()) return delayedError('validation_error', 'Organization name is required.', { organizationName: ['Organization name is required.'] });
      if (organization.organizationName === request.organizationName) return delayedOk({ status: 'no-op', message: 'Requested Organization name already matches current state.', organization: organizationDetail(organization), traceRefs: ['trace-organization-rename-no-op'], correlationId: correlationId() });
      organization.organizationName = request.organizationName;
      return delayedOk({ status: 'accepted', message: 'Organization display name updated without changing Tenant isolation or support access.', organization: organizationDetail(organization), traceRefs: ['trace-organization-rename'], correlationId: correlationId() });
    },
    suspendOrganization: (organizationId: string, request: { reason: string; idempotencyKey: string }) => organizationLifecycle(this.organizations, organizationId, request, 'suspended'),
    reactivateOrganization: (organizationId: string, request: { reason: string; idempotencyKey: string }) => organizationLifecycle(this.organizations, organizationId, request, 'active'),
    listSaasOwnerAdmins: () => delayedOk({ admins: [], invitations: [], traceRefs: ['trace-saas-owner-admins'], correlationId: correlationId(), redaction: ['tenant-customer-data-redacted'] }),
    listOrganizationAdmins: (organizationId: string) => {
      const organization = this.organizations.find((candidate) => candidate.organizationId === organizationId) ?? this.organizations[0];
      return delayedOk({ organization, admins: [], invitations: [], traceRefs: ['trace-organization-admins'], correlationId: correlationId(), redaction: ['tenant-app-data-redacted'] });
    },
    listCustomers: () => delayedOk({ customers: [], safeBoundaryNotice: customerBoundaryNotice, traceRefs: ['trace-customer-list'], correlationId: correlationId(), redaction: customerRedactions }),
    getCustomer: (customerId: string) => delayedOk(customerDetail({ customerId, customerName: `Customer ${customerId}`, status: 'active', traceRefs: ['trace-customer-detail'] })),
    createCustomer: (request: { customerName: string; idempotencyKey: string; reason?: string }) => delayedOk({ status: 'accepted', message: 'Customer create routed through backend authority.', customer: customerDetail({ customerId: `customer-${Date.now()}`, customerName: request.customerName, status: 'active', traceRefs: ['trace-customer-create'] }), traceRefs: ['trace-customer-create'], correlationId: correlationId() }),
    renameCustomer: (customerId: string, request: { customerName: string; idempotencyKey: string; reason?: string }) => delayedOk({ status: 'accepted', message: 'Customer renamed through backend authority.', customer: customerDetail({ customerId, customerName: request.customerName, status: 'active', traceRefs: ['trace-customer-rename'] }), traceRefs: ['trace-customer-rename'], correlationId: correlationId() }),
    suspendCustomer: (customerId: string, _request: { reason: string; idempotencyKey: string }) => delayedOk({ status: 'accepted', message: 'Customer suspended through backend authority.', customer: customerDetail({ customerId, customerName: `Customer ${customerId}`, status: 'suspended', traceRefs: ['trace-customer-suspend'] }), traceRefs: ['trace-customer-suspend'], correlationId: correlationId() }),
    reactivateCustomer: (customerId: string, _request: { reason: string; idempotencyKey: string }) => delayedOk({ status: 'accepted', message: 'Customer reactivated through backend authority.', customer: customerDetail({ customerId, customerName: `Customer ${customerId}`, status: 'active', traceRefs: ['trace-customer-reactivate'] }), traceRefs: ['trace-customer-reactivate'], correlationId: correlationId() }),
    listCustomerAdmins: (customerId: string) => delayedOk({ customer: { customerId, customerName: `Customer ${customerId}`, status: 'active', traceRefs: ['trace-customer-admins'] }, admins: [], invitations: [], traceRefs: ['trace-customer-admins'], correlationId: correlationId(), redaction: ['sibling-customers-redacted'] })
  };

  goals = {
    listGoals: () => delayedOk({ items: this.goalRows }),
    getGoal: (goalId: string) => {
      const goal = this.goalRows.find((candidate) => candidate.goalId === goalId);
      if (!goal) return delayedError('not_found', 'Goal is not visible in this tenant.');
      return delayedOk(goalDetail(goal));
    },
    createGoal: (request: { objective: string; successCriteria: string[]; priority: GoalSummary['priority']; idempotencyKey: string }) => {
      if (request.objective.length < 10) return delayedError('validation_error', 'Objective must be at least 10 characters.', { objective: ['Describe the intended outcome.'] });
      if (!request.successCriteria.length) return delayedError('validation_error', 'At least one success criterion is required.', { successCriteria: ['Add a success criterion.'] });
      const goalId = `goal-${this.goalRows.length + 1}`;
      this.goalRows.push({ goalId, objective: request.objective, ownerDisplayName: this.me.user.displayName, priority: request.priority, status: 'draft', pendingDecisionCount: 0, updatedAt: new Date().toISOString() });
      return delayedOk({ goalId, status: 'draft' as const, version: 1, nextAction: 'draft_plan' as const, correlationId: correlationId() });
    },
    draftPlan: (goalId: string) => this.goalRows.some((goal) => goal.goalId === goalId) ? delayedOk({ planJobId: `plan-job-${goalId}`, status: 'queued' as const, correlationId: correlationId() }) : delayedError('not_found', 'Goal is not visible in this tenant.'),
    launchGoal: (goalId: string, request: { acknowledgement: boolean }) => {
      if (!request.acknowledgement) return delayedError('validation_error', 'Acknowledge approval gates before launch.', { acknowledgement: ['Required before launch.'] });
      const goal = this.goalRows.find((candidate) => candidate.goalId === goalId);
      if (!goal) return delayedError('not_found', 'Goal is not visible in this tenant.');
      goal.status = 'active';
      return delayedOk({ goalId, status: 'active' as const, traceId: `trace-launch-${goalId}`, correlationId: correlationId() });
    }
  };

  decisions = {
    listDecisions: () => delayedOk({ items: this.decisionRows }),
    getDecision: (decisionId: string) => {
      const decision = this.decisionRows.find((candidate) => candidate.decisionId === decisionId);
      if (!decision) return delayedError('not_found', 'Decision is not visible in this tenant.');
      return delayedOk(decisionDetail(decision));
    },
    actOnDecision: (decisionId: string, action: 'approve' | 'reject' | 'request_changes' | 'escalate' | 'counter' | 'convert_to_policy_proposal', request: { expectedVersion: number; acknowledgement?: boolean }) => {
      const decision = this.decisionRows.find((candidate) => candidate.decisionId === decisionId);
      if (!decision) return delayedError('not_found', 'Decision is not visible in this tenant.');
      if (request.expectedVersion !== decision.version) return delayedError('conflict', 'This decision changed while you were reviewing it.');
      if (action === 'approve' && decision.priority === 'high' && !request.acknowledgement) return delayedError('validation_error', 'Acknowledge evidence, risk, policy trigger, and impact before approval.', { acknowledgement: ['Required for high-impact approval.'] });
      decision.status = action === 'escalate' ? 'escalated' : 'resolved';
      decision.version += 1;
      return delayedOk({ decisionId, status: action === 'escalate' ? 'escalated' as const : 'resolved' as const, action, traceId: `trace-${decisionId}-${action}`, correlationId: correlationId() });
    }
  };

  governance = {
    listPolicies: () => delayedOk({ items: this.policies }),
    createPolicyProposal: () => delayedOk({ proposalId: 'proposal-fixture-1', status: 'submitted' as const, correlationId: correlationId() }),
    simulatePolicyProposal: (proposalId: string) => delayedOk({ simulationJobId: `simulation-${proposalId}`, status: 'queued' as const, correlationId: correlationId() })
  };

  audit = {
    searchTraces: () => delayedOk({ items: this.traces }),
    getTrace: (traceId: string) => {
      const trace = this.traces.find((candidate) => candidate.traceId === traceId);
      if (!trace) return delayedError('not_found', 'Trace is not visible in this tenant.');
      const detail: TraceDetailResponse = { ...trace, tenantId, policyReferences: [{ policyId: 'policy-1', version: 'v1', clauseId: 'approval-gate.high-impact' }], relatedLinks: [], safeDetails: { fixture: true } };
      return delayedOk(detail);
    }
  };
}

const organizationBoundaryNotice = 'Organization administration manages the Tenant lifecycle boundary only; it does not grant tenant/customer application-data access, support access, provider secret access, or billing-derived authority.';
const organizationRedactions = ['tenant-app-data-redacted', 'provider-secrets-redacted', 'billing-authority-redacted', 'support-access-internals-redacted', 'hidden-counts-redacted'];
const customerBoundaryNotice = 'Customer administration is scoped to the selected Organization/Tenant; sibling-customer facts and tenant application data are omitted.';
const customerRedactions = ['sibling-customers-redacted', 'tenant-app-data-redacted', 'provider-secrets-redacted'];

function organizationDetail(organization: OrganizationSummary): OrganizationDetailPayload {
  return {
    organization,
    safeBoundaryNotice: organizationBoundaryNotice,
    visibleActions: organization.status === 'suspended' ? ['rename', 'reactivate'] : ['rename', 'suspend'],
    recentAuditEvents: [],
    traceRefs: organization.traceRefs,
    correlationId: correlationId(),
    redactions: organizationRedactions
  };
}

function customerDetail(customer: { customerId: string; customerName: string; status: string; traceRefs: string[] }) {
  return { customer, safeBoundaryNotice: customerBoundaryNotice, visibleActions: customer.status === 'suspended' ? ['rename', 'reactivate'] : ['rename', 'suspend'], recentAuditEvents: [], traceRefs: customer.traceRefs, correlationId: correlationId(), redaction: customerRedactions };
}

function organizationLifecycle(organizations: OrganizationSummary[], organizationId: string, request: { reason: string; idempotencyKey: string }, nextStatus: 'active' | 'suspended') {
  const organization = organizations.find((candidate) => candidate.organizationId === organizationId);
  if (!organization) return delayedError('not_found', 'Organization is not visible for this SaaS Owner context.');
  if (!request.reason?.trim()) return delayedError('validation_error', 'Reason is required for Organization lifecycle changes.', { reason: ['Reason is required.'] });
  if (!request.idempotencyKey?.trim()) return delayedError('validation_error', 'Idempotency key is required.', { idempotencyKey: ['Required for Organization mutations.'] });
  if (organization.status === nextStatus) return delayedOk({ status: 'no-op', message: `Organization is already ${nextStatus}; idempotency preserved.`, organization: organizationDetail(organization), traceRefs: [`trace-organization-${nextStatus}-no-op`], correlationId: correlationId() });
  organization.status = nextStatus;
  return delayedOk({ status: 'accepted', message: `Organization ${nextStatus === 'active' ? 'reactivated' : 'suspended'} at the Tenant lifecycle boundary.`, organization: organizationDetail(organization), traceRefs: [`trace-organization-${nextStatus}`], correlationId: correlationId() });
}

function goalDetail(goal: GoalSummary): GoalDetailResponse {
  return {
    goal: { ...goal, successCriteria: ['Human reviewer can inspect and approve the plan.'], constraints: ['Do not execute high-impact actions without approval.'], version: 1 },
    plan: { planId: goal.activePlanId ?? 'plan-draft', version: 1, status: 'draft', steps: [{ stepId: 'step-1', title: 'Review evidence and draft recommendation', assignedAgent: 'Planning Agent', expectedSideEffects: [], requiredApprovalGateIds: ['gate-1'] }] },
    approvalGates: [{ gateId: 'gate-1', name: 'High-impact action approval', status: 'required', policyClauseId: 'approval-gate.high-impact' }],
    linkedDecisions: [],
    traceLinks: [{ traceId: 'trace-2', label: 'Goal created', href: '#audit' }]
  };
}

function decisionDetail(decision: DecisionSummary): DecisionDetailResponse {
  return {
    ...decision,
    recommendation: 'Approve bounded work after reviewing evidence and policy trigger.',
    evidenceItems: [{ evidenceId: 'evidence-1', label: 'Trace summary', summary: 'Agent produced a recommendation within configured authority boundaries.', sourceType: 'trace', href: '#audit' }],
    alternativesConsidered: ['Defer until more evidence is attached.', 'Escalate to policy owner.'],
    allowedActions: ['approve', 'reject', 'request_changes', 'escalate', 'counter'],
    traceLinks: [{ traceId: 'trace-1', label: 'Decision trace', href: '#audit' }]
  };
}

function delayedOk<T>(value: T): Promise<ApiResult<T>> {
  return new Promise((resolve) => setTimeout(() => resolve({ ok: true, value }), 40));
}

function delayedError(code: string, message: string, fieldErrors?: Record<string, string[]>): Promise<ApiResult<never>> {
  const error: ApiError = { code, message, correlationId: correlationId(), fieldErrors };
  return new Promise((resolve) => setTimeout(() => resolve({ ok: false, error }), 40));
}

function correlationId() {
  return `corr-${Math.random().toString(16).slice(2)}`;
}
