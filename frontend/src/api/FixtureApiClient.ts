import type { ApiClient } from './ApiClient';
import type {
  AdminUser,
  ApiError,
  ApiResult,
  DecisionDetailResponse,
  DecisionSummary,
  GoalDetailResponse,
  GoalSummary,
  MeResponse,
  PolicySummary,
  TraceDetailResponse,
  TraceSummary
} from './types';

const tenantId = 'seed-tenant';

export class FixtureApiClient implements ApiClient {
  private me: MeResponse = {
    user: { id: 'seed-user-1', email: 'supervisor@example.test', displayName: 'Seed Supervisor' },
    memberships: [{ tenantId, tenantName: 'Seed tenant', status: 'active', roles: ['supervisor', 'reviewer'] }],
    activeTenantId: tenantId,
    preferences: { mode: 'system' }
  };

  private users: AdminUser[] = [
    { userId: 'seed-user-1', email: 'supervisor@example.test', displayName: 'Seed Supervisor', membershipStatus: 'active', roles: ['supervisor', 'reviewer'], version: 1 },
    { userId: 'seed-user-2', email: 'auditor@example.test', displayName: 'Seed Auditor', membershipStatus: 'invited', roles: ['auditor'], version: 1 }
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
    updatePreferences: (request: { mode: MeResponse['preferences']['mode'] }) => {
      this.me = { ...this.me, preferences: { mode: request.mode } };
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
    }
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
