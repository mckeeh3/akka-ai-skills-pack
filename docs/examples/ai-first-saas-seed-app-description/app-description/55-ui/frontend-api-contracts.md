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

Admin APIs are one shared scope-aware User Admin route group backed by `UserDirectoryView`, `MembershipView`, `InvitationView`, `AdminAuditView`, and `AccessReviewQueueView`. The same contracts serve SaaS Owner Admin, Tenant Admin, and Customer Admin variants; backend authorization and redaction decide which fields/actions are visible for the selected `AuthContext`.

### Shared User Admin DTOs

```ts
type AdminScopeType = "saas_owner" | "tenant" | "customer";
type AdminActorVariant = "SaaS Owner Admin" | "Tenant Admin" | "Customer Admin" | "Auditor" | "Support Access";
type UserAdminSurfaceId = "user-admin-dashboard" | "user-admin-user-list" | "user-admin-user-account";
type UserAdminSurfaceState = "ready" | "loading" | "empty" | "error" | "forbidden" | "stale";
type AdminAccountStatus = "invited" | "active" | "disabled" | "removed";
type AdminMembershipStatus = "invited" | "active" | "suspended" | "removed";
type AdminInvitationStatus = "pending" | "sent" | "delivery_failed" | "accepted" | "expired" | "revoked";
type AdminRiskLevel = "none" | "low" | "medium" | "high" | "critical";
type AdminDenialReason =
  | "disabled_actor"
  | "inactive_membership"
  | "missing_capability"
  | "cross_tenant"
  | "cross_customer"
  | "customer_admin_tenant_action"
  | "saas_owner_support_access_required"
  | "role_escalation"
  | "last_admin_loss"
  | "policy_denied"
  | "target_not_visible";

type AdminScopeContextDto = {
  scopeType: AdminScopeType;
  scopeLabel: string;
  actorVariant: AdminActorVariant;
  tenantId?: string;
  customerId?: string;
  selectedMembershipId: string;
  browserCapabilities: string[];
  supportAccessGrantId?: string;
};

type UserAdminTraceLinkDto = {
  traceId: string;
  label: string;
  href: string;
  kind: "surface" | "audit" | "agent_work" | "decision" | "data_access";
};

type UserAdminRedactionDto = {
  field: string;
  reason: "out_of_scope" | "policy" | "sensitive" | "support_access_required";
  replacementLabel: string;
};

type UserAdminActionAvailabilityDto = {
  actionId: string;
  label: string;
  capabilityId: string;
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  href?: string;
  enabled: boolean;
  denialReason?: AdminDenialReason;
  requiresIdempotencyKey?: boolean;
  requiresDecisionCard?: boolean;
  decisionCardId?: string;
  traceId?: string;
};

type UserAdminSurfaceEnvelope<T> = {
  surfaceId: UserAdminSurfaceId;
  surfaceVersion: "v1";
  state: UserAdminSurfaceState;
  scope: AdminScopeContextDto;
  payload?: T;
  correlationId: string;
  surfaceTraceId: string;
  generatedAt: string;
  staleSince?: string;
  error?: { code: string; safeMessage: string; retryable: boolean };
  forbidden?: { denialReason: AdminDenialReason; contextSwitchHint?: string };
  redactions: UserAdminRedactionDto[];
  traceLinks: UserAdminTraceLinkDto[];
};

type UserAdminMutationRequest = {
  reason?: string;
  expectedVersion?: number;
  idempotencyKey: string;
};

type UserAdminMutationResponse = {
  status: "applied" | "queued_for_decision" | "denied" | "no_op" | "conflict";
  targetId: string;
  targetVersion?: number;
  auditTraceId: string;
  decisionCardId?: string;
  denialReason?: AdminDenialReason;
  correlationId: string;
};
```

### `GET /api/admin/users/dashboard`

Loads the canonical `user-admin-dashboard` surface. Query parameters: `tenantId?`, `customerId?`, `includeQueues?=true|false`.

Response:

```ts
type UserAdminMetricCardDto = {
  cardId: string;
  label: string;
  value: number | "redacted";
  trend?: "up" | "down" | "flat";
  riskLevel: AdminRiskLevel;
  filterForList?: UserAdminUserListFilterDto;
  traceId: string;
  redactions?: UserAdminRedactionDto[];
};

type UserAdminQueueItemDto = {
  itemId: string;
  queueType: "invitation_delivery_failure" | "stale_invitation" | "dormant_admin" | "support_access_expiry" | "last_admin_risk" | "agent_recommendation" | "recent_denial";
  title: string;
  summary: string;
  targetAccountId?: string;
  targetMembershipId?: string;
  targetInvitationId?: string;
  riskLevel: AdminRiskLevel;
  dueAt?: string;
  actions: UserAdminActionAvailabilityDto[];
  traceId: string;
};

type UserAdminDashboardPayload = {
  cards: UserAdminMetricCardDto[];
  queues: UserAdminQueueItemDto[];
  primaryActions: UserAdminActionAvailabilityDto[];
  listNavigation: Array<{ label: string; filter: UserAdminUserListFilterDto; capabilityId: "admin.users.search" }>;
};

type UserAdminDashboardResponse = UserAdminSurfaceEnvelope<UserAdminDashboardPayload>;
```

### `GET /api/admin/users`

Loads the canonical `user-admin-user-list` surface. Query parameters: `q?`, `tenantId?`, `customerId?`, `accountStatus?`, `membershipStatus?`, `role?`, `invitationStatus?`, `supportAccess?=any|active|expiring|none`, `reviewType?`, `riskLevel?`, `dashboardQueueId?`, `sort?`, `pageSize?`, `pageToken?`.

Response:

```ts
type UserAdminUserListFilterDto = {
  q?: string;
  accountStatus?: AdminAccountStatus;
  membershipStatus?: AdminMembershipStatus;
  role?: string;
  invitationStatus?: AdminInvitationStatus;
  supportAccess?: "any" | "active" | "expiring" | "none";
  reviewType?: "dormant_admin" | "last_admin_risk" | "role_escalation" | "support_access_expiry";
  riskLevel?: AdminRiskLevel;
  dashboardQueueId?: string;
};

type UserAdminPaginationDto = {
  pageToken?: string;
  nextPageToken?: string;
  pageSize: number;
  sort: "name" | "email" | "lastActiveAt" | "risk" | "createdAt";
  queryLabel: string;
};

type UserAdminUserRowDto = {
  accountId: string;
  displayEmail: string;
  displayName?: string;
  accountStatus: AdminAccountStatus;
  identityLinkState: "linked" | "unlinked" | "relink_pending" | "relink_blocked";
  lastActiveAt?: string;
  memberships: Array<{ membershipId: string; scopeLabel: string; status: AdminMembershipStatus; roles: string[]; supportAccessExpiresAt?: string }>;
  invitationBadges: Array<{ invitationId: string; status: AdminInvitationStatus; deliveryStatus: "pending" | "sent" | "delivery_failed"; expiresAt: string }>;
  accessReviewBadges: Array<{ itemId: string; label: string; riskLevel: AdminRiskLevel }>;
  riskFlags: string[];
  actions: UserAdminActionAvailabilityDto[];
  redactions: UserAdminRedactionDto[];
  traceId: string;
};

type UserAdminUserListPayload = {
  filters: UserAdminUserListFilterDto;
  pagination: UserAdminPaginationDto;
  rows: UserAdminUserRowDto[];
  emptyReason?: "no_users_in_scope" | "no_search_matches" | "no_queue_items" | "redacted_result_set";
  createInvitationAction?: UserAdminActionAvailabilityDto;
};

type UserAdminUserListResponse = UserAdminSurfaceEnvelope<UserAdminUserListPayload>;
```

### `GET /api/admin/users/{accountId}`

Loads the canonical `user-admin-user-account` detail surface for a scoped backend-visible account id. Query parameters: `tenantId?`, `customerId?`, `returnToPageToken?`.

Response:

```ts
type UserAdminAccountSummaryDto = {
  accountId: string;
  displayEmail: string;
  displayName?: string;
  accountStatus: AdminAccountStatus;
  identityLinkState: "linked" | "unlinked" | "relink_pending" | "relink_blocked";
  createdAt: string;
  activatedAt?: string;
  disabledAt?: string;
  lastLoginAt?: string;
  lastActivityAt?: string;
  redactions: UserAdminRedactionDto[];
};

type UserAdminMembershipDetailDto = {
  membershipId: string;
  scopeType: AdminScopeType;
  scopeLabel: string;
  tenantId?: string;
  customerId?: string;
  roles: string[];
  capabilitiesSummary: string[];
  status: AdminMembershipStatus;
  membershipKind: "standard" | "support_access" | "bootstrap_admin";
  supportAccessGrantId?: string;
  supportAccessExpiresAt?: string;
  invitationId?: string;
  lastAdminRisk?: boolean;
  version: number;
  actions: UserAdminActionAvailabilityDto[];
  auditTraceIds: string[];
};

type UserAdminInvitationHistoryDto = {
  invitationId: string;
  targetEmail: string;
  status: AdminInvitationStatus;
  deliveryStatus: "pending" | "sent" | "delivery_failed";
  deliveryAttempts: number;
  expiresAt: string;
  roles: string[];
  actions: UserAdminActionAvailabilityDto[];
  auditTraceId: string;
};

type UserAdminSupportAccessDto = {
  grantId: string;
  membershipId?: string;
  purpose: string;
  status: "requested" | "active" | "revoked" | "expired" | "denied";
  startsAt?: string;
  expiresAt?: string;
  actions: UserAdminActionAvailabilityDto[];
  auditTraceId: string;
};

type UserAdminAccessReviewItemDto = {
  itemId: string;
  reviewType: string;
  summary: string;
  riskLevel: AdminRiskLevel;
  recommendation?: string;
  actions: UserAdminActionAvailabilityDto[];
  traceId: string;
};

type UserAdminAuditExcerptDto = {
  auditEventId: string;
  occurredAt: string;
  actorLabel: string;
  actionType: string;
  result: "allowed" | "denied" | "no_op" | "failed";
  evidenceSummary: string;
  traceId: string;
};

type UserAdminUserAccountPayload = {
  account: UserAdminAccountSummaryDto;
  profileVisibility: "self" | "admin_visible" | "redacted";
  settingsVisibility: "self" | "policy_allowed" | "hidden";
  memberships: UserAdminMembershipDetailDto[];
  invitations: UserAdminInvitationHistoryDto[];
  supportAccess: UserAdminSupportAccessDto[];
  accessReviewItems: UserAdminAccessReviewItemDto[];
  recentAudit: UserAdminAuditExcerptDto[];
  actions: UserAdminActionAvailabilityDto[];
  returnToList?: { filter: UserAdminUserListFilterDto; pageToken?: string };
};

type UserAdminUserAccountResponse = UserAdminSurfaceEnvelope<UserAdminUserAccountPayload>;
```

### Invitation lifecycle and User Admin mutation routes

Invitation query routes return the same browser-safe invitation DTOs embedded in dashboard, list, and account payloads; raw invitation tokens/token hashes and full email bodies are never returned. All mutation requests include `idempotencyKey`; all responses include `correlationId`, `auditTraceId`, and optional `decisionCardId`. Safe no-op, denial, and conflict responses are explicit and auditable.

Routes:

- `GET /api/admin/invitations` (`admin.invitations.search`)
- `POST /api/admin/users/invitations` (`admin.invitations.create`)
- `POST /api/admin/invitations/{invitationId}/resend` (`admin.invitations.resend`)
- `POST /api/admin/invitations/{invitationId}/revoke` (`admin.invitations.revoke`)
- `POST /api/invitations/accept`
- `POST /api/admin/users/{accountId}/memberships` (`admin.memberships.add`)
- `POST /api/admin/memberships/{membershipId}/suspend` (`admin.memberships.suspend`)
- `POST /api/admin/memberships/{membershipId}/reactivate` (`admin.memberships.reactivate`)
- `POST /api/admin/memberships/{membershipId}/remove` (`admin.memberships.remove`)
- `PUT /api/admin/memberships/{membershipId}/roles` (`admin.roles.replace`)
- `DELETE /api/admin/memberships/{membershipId}/roles/{roleId}` (`admin.roles.remove`)
- `POST /api/admin/users/{accountId}/disable` (`admin.users.disable`)
- `POST /api/admin/users/{accountId}/reactivate` (`admin.users.reactivate`)
- `PATCH /api/admin/users/{accountId}/profile` (`admin.users.profile.patch`)
- `POST /api/admin/users/{accountId}/identity-relink/request` (`admin.users.identity_relink.request`)
- `POST /api/admin/users/{accountId}/identity-relink/complete` (`admin.users.identity_relink.complete`)
- `POST /api/admin/support-access/grants` (`admin.support_access.grant`)
- `POST /api/admin/support-access/grants/{grantId}/revoke` (`admin.support_access.revoke`)
- `POST /api/admin/support-access/grants/{grantId}/extend` (`admin.support_access.extend`)
- `GET /api/admin/access-review` (`admin.access_review.read`)
- `POST /api/admin/access-review/{itemId}/resolve` (`admin.access_review.resolve`)
- `GET /api/admin/audit` (`admin.audit.read`)

Selected request/response DTOs:

```ts
type InviteUserRequest = UserAdminMutationRequest & {
  email: string;
  displayName?: string;
  roleIds: string[];
  tenantId?: string;
  customerId?: string;
  message?: string;
};

type InviteUserResponse = UserAdminMutationResponse & {
  invitationId: string;
  invitedEmail: string;
  deliveryStatus: "pending" | "sent" | "delivery_failed";
};

type UpdateMembershipRolesRequest = UserAdminMutationRequest & {
  roleIds: string[];
};

type UpdateMembershipRolesResponse = UserAdminMutationResponse & {
  membershipId: string;
  roleIds: string[];
  lastAdminProtection?: boolean;
};

type AccessReviewResolveRequest = UserAdminMutationRequest & {
  resolution: "accepted_recommendation" | "dismissed" | "remediated" | "converted_to_decision";
};

type AcceptInvitationRequest = {
  acceptanceContext: string;
  idempotencyKey: string;
};
```

### Agent helper routes

Routes:

- `POST /api/admin/agents/access-review/run`
- `POST /api/admin/agents/risk-score`
- `POST /api/admin/agents/role-recommendation`
- `POST /api/admin/agents/audit-summary`

Responses include evidence links to the three User Admin surfaces, risk/confidence, recommendation source agent, `AgentWorkTrace` id, decision-card id when required, and `correlationId`.

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
