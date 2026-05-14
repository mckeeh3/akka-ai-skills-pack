# Frontend API Contracts

## API style

- typed TypeScript client generated or maintained from backend contracts where feasible
- all API calls include auth/session context and selected tenant context
- browser-facing DTOs are intentional and must not leak internal domain objects accidentally
- all API routes use `/api/...`
- all mutating commands return safe user-facing status plus correlation id

## Common envelopes

```ts
type ApiError = {
  code: string;
  message: string;
  correlationId: string;
  fieldErrors?: Record<string, string[]>;
};

type Page<T> = {
  items: T[];
  nextPageToken?: string;
};

type Status = "draft" | "planning" | "active" | "waiting_for_human" | "blocked" | "completed" | "failed" | "cancelled";
```

Common HTTP error handling:

- `400` validation or malformed request
- `401` unauthenticated or expired session
- `403` forbidden for role/tenant/policy reason
- `404` not found or not visible in tenant scope
- `409` stale version/conflict/idempotency conflict
- `500` safe server failure with correlation id

## Session and tenant

### `GET /api/me`

Response:

```ts
type MeResponse = {
  user: {
    id: string;
    email: string;
    displayName: string;
  };
  memberships: Array<{
    tenantId: string;
    tenantName: string;
    status: "active" | "invited" | "suspended";
    roles: string[];
  }>;
  activeTenantId?: string;
  preferences: {
    mode: "light" | "dark" | "system";
  };
};
```

### `PUT /api/me/preferences`

Request:

```ts
type UpdatePreferencesRequest = {
  mode: "light" | "dark" | "system";
};
```

Response:

```ts
type UpdatePreferencesResponse = {
  preferences: { mode: "light" | "dark" | "system" };
  correlationId: string;
};
```

## Admin users and invitations

Admin APIs are backed by UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView and enforce backend authorization, redaction, pagination, tenant/customer scope, and audit for every query/action.

### `GET /api/admin/users`

Query parameters:

- `q?`
- `tenantId?`
- `customerId?`
- `accountStatus?`
- `membershipStatus?`
- `role?`
- `identityLinkState?`
- `pageToken?`

Response:

```ts
type AdminUsersResponse = Page<{
  userId: string;
  email: string;
  displayName?: string;
  accountStatus: "active" | "disabled";
  membershipStatus: "active" | "invited" | "suspended" | "removed";
  roles: string[];
  identityLinkState: "linked" | "unlinked" | "relink_pending";
  lastActiveAt?: string;
  lastAdminRisk?: boolean;
}>;
```

### `POST /api/admin/users/invitations`

Request:

```ts
type InviteUserRequest = {
  email: string;
  displayName?: string;
  roleIds: string[];
  message?: string;
  idempotencyKey: string;
};
```

Response:

```ts
type InviteUserResponse = {
  invitationId: string;
  status: "pending" | "resent";
  invitedEmail: string;
  deliveryStatus: "pending" | "sent" | "delivery_failed";
  correlationId: string;
};
```

### Invitation lifecycle routes

Routes:

- `GET /api/admin/invitations`
- `POST /api/admin/invitations/{invitationId}/resend`
- `POST /api/admin/invitations/{invitationId}/revoke`
- `POST /api/invitations/accept`

Invitation query filters:

- `status?=pending|sent|delivery_failed|accepted|expired|revoked`
- `deliveryStatus?=pending|sent|delivery_failed`
- `targetEmail?`
- `inviterId?`
- `tenantId?`
- `customerId?`
- `expiresBefore?`
- `pageToken?`

```ts
type InvitationSummary = {
  invitationId: string;
  targetEmail: string;
  status: "pending" | "sent" | "delivery_failed" | "accepted" | "expired" | "revoked";
  deliveryStatus: "pending" | "sent" | "delivery_failed";
  deliveryAttempts: number;
  expiresAt: string;
  roles: string[];
  canResend: boolean;
  canRevoke: boolean;
  auditTraceId: string;
};

type AcceptInvitationRequest = {
  acceptanceContext: string;
  idempotencyKey: string;
};
```

### `PUT /api/admin/users/{userId}/roles`

Request:

```ts
type UpdateRolesRequest = {
  roleIds: string[];
  reason?: string;
  expectedVersion: number;
};
```

Response:

```ts
type UpdateRolesResponse = {
  userId: string;
  roleIds: string[];
  version: number;
  auditTraceId: string;
  decisionCardId?: string;
  lastAdminProtection?: boolean;
  correlationId: string;
};
```

### Access review and admin-agent routes

Routes:

- `GET /api/admin/access-review`
- `POST /api/admin/access-review/{itemId}/resolve`
- `POST /api/admin/agents/access-review/run`
- `POST /api/admin/agents/risk-score`
- `POST /api/admin/agents/role-recommendation`
- `POST /api/admin/agents/audit-summary`

Responses include evidence links to UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, risk/confidence, recommendation source agent, decision-card id when required, and correlation id.

## Goals and plans

### `GET /api/goals`

Query parameters:

- `status?`
- `owner?`
- `priority?`
- `pageToken?`

Response:

```ts
type GoalSummary = {
  goalId: string;
  objective: string;
  ownerDisplayName: string;
  priority: "low" | "normal" | "high" | "urgent";
  status: Status;
  activePlanId?: string;
  pendingDecisionCount: number;
  updatedAt: string;
};

type GoalsResponse = Page<GoalSummary>;
```

### `POST /api/goals`

Request:

```ts
type CreateGoalRequest = {
  objective: string;
  priority: "low" | "normal" | "high" | "urgent";
  targetDate?: string;
  successCriteria: string[];
  constraints?: string[];
  requestedToolIds?: string[];
  idempotencyKey: string;
};
```

Response:

```ts
type CreateGoalResponse = {
  goalId: string;
  status: "draft";
  version: number;
  nextAction: "draft_plan";
  correlationId: string;
};
```

### `GET /api/goals/{goalId}`

Response:

```ts
type GoalDetailResponse = {
  goal: GoalSummary & {
    successCriteria: string[];
    constraints: string[];
    targetDate?: string;
    version: number;
  };
  plan?: ExecutionPlanDto;
  approvalGates: ApprovalGateDto[];
  linkedDecisions: DecisionSummary[];
  traceLinks: TraceLinkDto[];
};

type ExecutionPlanDto = {
  planId: string;
  version: number;
  status: "draft" | "under_review" | "approved" | "active" | "superseded";
  steps: Array<{
    stepId: string;
    title: string;
    assignedAgent?: string;
    expectedSideEffects: string[];
    requiredApprovalGateIds: string[];
  }>;
};

type ApprovalGateDto = {
  gateId: string;
  name: string;
  status: "not_required" | "required" | "satisfied" | "blocked";
  policyClauseId?: string;
};

type TraceLinkDto = {
  traceId: string;
  label: string;
  href: string;
};
```

### `POST /api/goals/{goalId}/draft-plan`

Request:

```ts
type DraftPlanRequest = {
  guidance?: string;
  expectedGoalVersion: number;
  idempotencyKey: string;
};
```

Response:

```ts
type DraftPlanResponse = {
  planJobId: string;
  status: "queued" | "running";
  correlationId: string;
};
```

### `POST /api/goals/{goalId}/launch`

Request:

```ts
type LaunchGoalRequest = {
  planId: string;
  expectedGoalVersion: number;
  acknowledgement: boolean;
  idempotencyKey: string;
};
```

Response:

```ts
type LaunchGoalResponse = {
  goalId: string;
  status: "active";
  traceId: string;
  correlationId: string;
};
```

## Decisions

### `GET /api/decisions`

Query parameters:

- `status?=open|resolved|escalated`
- `priority?`
- `policyTrigger?`
- `agentId?`
- `pageToken?`

Response:

```ts
type DecisionSummary = {
  decisionId: string;
  type: "recommendation_approval" | "exception_resolution" | "deviation_review" | "policy_change_proposal";
  title: string;
  originatingAgent?: string;
  linkedGoalId?: string;
  priority: "low" | "normal" | "high" | "urgent";
  status: "open" | "resolved" | "escalated" | "superseded";
  riskScore?: number;
  confidenceScore?: number;
  impactEstimate?: string;
  policyTriggers: string[];
  dueAt?: string;
  version: number;
};

type DecisionsResponse = Page<DecisionSummary>;
```

### `GET /api/decisions/{decisionId}`

Response:

```ts
type DecisionDetailResponse = DecisionSummary & {
  recommendation: string;
  evidenceItems: EvidenceItemDto[];
  alternativesConsidered: string[];
  allowedActions: DecisionAction[];
  traceLinks: TraceLinkDto[];
};

type EvidenceItemDto = {
  evidenceId: string;
  label: string;
  summary: string;
  sourceType: "trace" | "document" | "metric" | "tool" | "user_input";
  href?: string;
};

type DecisionAction = "approve" | "reject" | "request_changes" | "escalate" | "counter" | "convert_to_policy_proposal";
```

### Decision action endpoints

Routes:

- `POST /api/decisions/{decisionId}/approve`
- `POST /api/decisions/{decisionId}/reject`
- `POST /api/decisions/{decisionId}/request-changes`
- `POST /api/decisions/{decisionId}/escalate`
- `POST /api/decisions/{decisionId}/counter`

Request:

```ts
type DecisionActionRequest = {
  expectedVersion: number;
  comment?: string;
  counterproposal?: string;
  acknowledgement?: boolean;
  idempotencyKey: string;
};
```

Response:

```ts
type DecisionActionResponse = {
  decisionId: string;
  status: "resolved" | "escalated" | "waiting_for_changes";
  action: DecisionAction;
  traceId: string;
  correlationId: string;
};
```

## Governance

### `GET /api/governance/policies`

Response:

```ts
type PolicySummary = {
  policyId: string;
  name: string;
  activeVersion: string;
  ownerRole: string;
  approvalGateCount: number;
  openProposalCount: number;
};

type PoliciesResponse = Page<PolicySummary>;
```

### `POST /api/governance/policy-proposals`

Request:

```ts
type CreatePolicyProposalRequest = {
  policyId: string;
  title: string;
  proposedChange: string;
  rationale: string;
  expectedImpact: string;
  simulationScope?: string;
  examples?: string[];
  idempotencyKey: string;
};
```

Response:

```ts
type CreatePolicyProposalResponse = {
  proposalId: string;
  status: "draft" | "submitted";
  correlationId: string;
};
```

### `POST /api/governance/policy-proposals/{proposalId}/simulate`

Response:

```ts
type PolicySimulationResponse = {
  simulationJobId: string;
  status: "queued" | "running";
  correlationId: string;
};
```

## Audit

### `GET /api/audit/traces`

Query parameters:

- `q?`
- `goalId?`
- `decisionId?`
- `agentId?`
- `policyId?`
- `toolName?`
- `actorId?`
- `from?`
- `to?`
- `pageToken?`

Response:

```ts
type TraceSummary = {
  traceId: string;
  category: "work" | "decision" | "policy_invocation" | "tool_invocation" | "data_access" | "approval" | "outcome";
  timestamp: string;
  actorLabel: string;
  action: string;
  targetLabel: string;
  authorizationBasis: string;
  correlationId: string;
};

type TraceSearchResponse = Page<TraceSummary>;
```

### `GET /api/audit/traces/{traceId}`

Response:

```ts
type TraceDetailResponse = TraceSummary & {
  tenantId: string;
  policyReferences: Array<{ policyId: string; version: string; clauseId?: string }>;
  relatedLinks: TraceLinkDto[];
  safeDetails: Record<string, string | number | boolean | null>;
};
```

## Realtime

### `GET /api/realtime/stream`

Transport: SSE.

Query parameters:

- `topics=mission-control,goals,decisions,governance,notifications`

Event envelope:

```ts
type RealtimeEvent = {
  eventId: string;
  tenantId: string;
  topic: "mission-control" | "goals" | "decisions" | "governance" | "notifications";
  type: string;
  subjectId: string;
  version?: number;
  occurredAt: string;
  payload: Record<string, unknown>;
};
```

Required event behavior:

- events are tenant and permission scoped
- client merges by `subjectId` and `version` where available
- duplicate or replayed events do not create duplicate rows
- malformed events are ignored and surfaced as non-fatal stream warnings

Initial event types:

- `goal.updated`
- `plan.updated`
- `decision.created`
- `decision.updated`
- `decision.resolved`
- `agent-activity.created`
- `policy-proposal.updated`
- `notification.created`
