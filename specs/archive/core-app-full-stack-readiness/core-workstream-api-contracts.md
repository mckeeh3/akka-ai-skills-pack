# Core Workstream API Contracts

## Purpose

Align the canonical workstream UI reference with the full-core secure AI-first SaaS foundation. This is the implementation contract for frontend fixtures, API clients, and later React surfaces under `frontend/src/workstream/**`.

Sources:

- `docs/workstream-ui-reference-architecture.md`
- `docs/structured-surface-contracts.md`
- `specs/core-app-full-stack-readiness/user-admin-reference-slice.md`
- `specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md`

## Contract rules

- Browser bootstrap starts from `GET /api/me`, selected `AuthContext`, and browser-safe capability ids.
- Functional agents are work areas in the rail, not routes.
- Every consequential surface action maps to a governed backend `capabilityId`; frontend visibility is advisory only.
- Backend authorization owns disabled-user, tenant/customer isolation, scope denial, role/capability denial, approval-required, and redaction behavior.
- Surface payloads use `SurfaceEnvelope<TData>` with `surfaceId`, `surfaceType`, `surfaceVersion`, `ownerFunctionalAgentId`, `authContext`, `correlationId`, `traceIds`, `redaction`, `data`, `actions`, and optional links/stale markers.
- Safe denial states must be representable as disabled/denied actions, forbidden surface state, denied action result, stale marker, or redacted payload; no fixture may rely on hidden buttons as the security boundary.

## API client families

The frontend workstream client should expose these browser-safe calls:

```ts
bootstrap(): Promise<ApiResult<WorkstreamBootstrapResponse>>;
getMe(): Promise<ApiResult<MeResponse>>;
listFunctionalAgents(): Promise<ApiResult<FunctionalAgentSummary[]>>;
listWorkstreamItems(functionalAgentId?: string): Promise<ApiResult<WorkstreamItem[]>>;
getSurface(surfaceId: string): Promise<ApiResult<SurfaceEnvelope<unknown>>>;
runCapabilityAction(request: CapabilityActionRequest): Promise<ApiResult<CapabilityActionResult>>;
```

Backend route families that feed those calls:

| Family | Routes | Notes |
|---|---|---|
| Bootstrap/self | `GET /api/me`, profile/settings/context routes | Returns selected context, available contexts, visible capabilities, rail agents, disabled/no-membership states. |
| Workstream | `GET /api/workstream/items`, `GET /api/workstream/surfaces/{surfaceId}`, `POST /api/workstream/actions` | Stable generic shell contract; may delegate to module-specific APIs. |
| User Admin | `/api/admin/users`, `/api/admin/invitations`, `/api/admin/support-access`, `/api/admin/audit`, `/api/admin/access-review`, Tenant/Customer settings routes | Consumes the User Administration slice. |
| Agent Admin | `/api/agent-admin/**` | Consumes the Agent Admin component/API slice. |
| Audit/Trace | `/api/audit/**` or `/api/work-traces/**` | Durable search/detail follows the later Audit/Trace module; current UI contract uses trace link summaries. |
| Governance/Policy | `/api/governance/**`, decision/proposal routes | Durable policy and decision-card implementation follows the later Governance/Policy module. |
| Realtime | SSE workstream/surface event stream keyed by selected context | Duplicate, out-of-order, malformed, cross-context, stale, and reconnect cases are safe no-ops or stale markers. |

## Functional agents

| Functional agent | Default surface | Required capabilities | Primary APIs | Denial/redaction states |
|---|---|---|---|---|
| Access/Profile | `detail-edit` | `profile.read`, `profile.update` when editing | `/api/me`, `/api/me/profile`, `/api/me/settings`, `/api/me/context` | Disabled account, no membership, invalid selected context, self-only editable fields, support-access marker redaction. |
| User Admin | `dashboard` | `core.user_admin.read`, `core.user_admin.manage` or mapped admin capabilities | `/api/admin/users`, invitations, memberships, support access, audit, access review, settings | Last-admin denied, role escalation denied, disabled actor, Customer/Tenant boundary denial, resource hiding, raw token/provider/Resend-secret redaction. |
| Agent Admin | `governance-diff` or `dashboard` | `agent.definitions.manage`, `agent.prompts.govern`, `agent.skills.govern`, `agent.tool_boundaries.manage`, `agent.models.read/manage`, `agent.runtime.test` as scoped | `/api/agent-admin/**` | Cross-tenant artifact denial, missing approval for authority/tool/model expansion, inactive prompt/skill/manifest/tool/model refs, provider-secret redaction, draft content access denial. |
| Audit/Trace | `audit-timeline` | `audit.trace.read`, module-specific audit read capabilities | `/api/admin/audit`, later `/api/audit/**` and `/api/work-traces/**` | Scoped audit search, redacted evidence, support/auditor-only fields, hidden not-found for cross-scope trace ids. |
| Governance/Policy | `governance-diff`, `decision`, `outcome` | `governance.policy.read`, `governance.policy.propose`, decision approval capabilities | `/api/governance/**`, decision/proposal routes | Approval-required, policy-blocked, simulation-only, authority expansion denied, redacted policy evidence, no-op proposal states. |

## Core surface inventory

### Access/Profile surfaces

- **Self context card** (`detail-edit`): account status, profile, settings, selected tenant/customer, membership, role/capability basis, support-access state.
- **Context switcher** (`list-search` or `detail-edit`): available contexts, unavailable contexts, selected/default context, no-membership recovery.
- **Authority basis** (`audit-timeline` or embedded detail section): recent context/audit refs for high-privilege changes.

Actions:

| Action | Capability id | Result behavior |
|---|---|---|
| Update profile | `core.user_admin.self_context` | update current detail surface or validation-error with preserved fields. |
| Update settings | `core.user_admin.self_context` | no-op or accepted result, refresh `/api/me` state. |
| Switch context | `core.user_admin.self_context` | refresh bootstrap, append action-feedback, emit audit when high privilege. |
| Open trace | `audit.trace.read` | append/open audit timeline if authorized; denied otherwise. |

### User Admin surfaces

- **User Admin command center** (`dashboard`): pending invitations, active users, access-review items, support grants, delivery failures, audit excerpts.
- **Users/invitations/memberships list** (`list-search`): tenant/customer scoped rows for users, invitation queue, membership rows, support grants, admin audit excerpts.
- **User detail/edit** (`detail-edit`): account/profile/settings summary, memberships, invitations, support access, audit links, action availability.
- **Support access** (`detail-edit` and `workflow-status`): grant/revoke/extend/expiry.
- **Access review** (`decision`, `list-search`): stale invitation, delivery failure, dormant admin, risky role, last-admin risk, support-access expiry.
- **Admin audit** (`audit-timeline`): filtered admin events with actor/target/action/result/policy refs.

Required action capability ids:

| Action family | Capability id | Required frontend result states |
|---|---|---|
| Search/list/detail | `core.user_admin.read` | ready, empty, forbidden, partial-data, stale. |
| Invite/resend/revoke | `core.user_admin.manage` plus invitation capability mapping | accepted, validation-error, denied, no-op, failed, workflow-status result. |
| Replace/remove roles | `core.user_admin.manage` | denied `LAST_ADMIN_DENIED`, denied `ROLE_ESCALATION_DENIED`, approval-required, conflict, accepted. |
| Suspend/reactivate/remove membership | `core.user_admin.manage` | confirmation, idempotency, audit trace, conflict, last-admin denial. |
| Grant/revoke/extend support access | `core.user_admin.manage` | approval-required for high risk/long duration, expiry stale markers, audit timeline links. |
| Resolve access review/create decision | `core.user_admin.manage` and governance decision capability | append decision surface or accepted/no-op. |

### Agent Admin surfaces

- **Agent catalog** (`list-search` or `dashboard`): agent definitions by status, placement, authority, steward, model ref, readiness.
- **Agent detail/readiness** (`detail-edit`): active refs, prompt/manifest/tool/model readiness, runtime binding, trace links.
- **Prompt governance** (`detail-edit`, `governance-diff`): prompt draft/detail/history/diff/review/activate/test.
- **Skill governance** (`detail-edit`, `governance-diff`): skill catalog/detail/history/diff/review/assignment.
- **Manifest management** (`governance-diff`): compact manifest preview, assigned skills, activation readiness.
- **Tool boundary management** (`governance-diff`): registry grants, side-effect policy, simulation results, approval-required changes.
- **Model refs** (`list-search`, `detail-edit`): safe provider aliases, allowed modes/authority, disabled/denied states, no secrets.
- **Behavior proposals** (`decision`, `governance-diff`): proposed diffs, authority expansion, risk flags, review/apply.
- **Test console** (`workflow-status` or `detail-edit`): deterministic test-mode invocation, no-side-effect banner, trace refs, denied tool/skill cases.

Required action capability ids:

| Action family | Capability id | Required frontend result states |
|---|---|---|
| Create/update/activate/disable/archive AgentDefinition | `agent.definitions.manage` | validation-error for missing refs, approval-required for authority expansion, denied for cross tenant/disabled actor, accepted/no-op. |
| Prompt lifecycle/test | `agent.prompts.govern`, `agent.runtime.test` | secret-like content denied, draft access denied, activation errors, test-console trace result. |
| Skill lifecycle/readSkill test | `agent.skills.govern`, `agent.runtime.test` | unassigned/inactive/cross-tenant denied, safe SkillLoadTrace links. |
| Manifest edit/review/activate | `agent.skills.govern` | approval-required for behavior/data expansion, conflict on stale version. |
| Tool-boundary edit/simulate/review/activate | `agent.tool_boundaries.manage` | approval-required for side effects, denied unknown/free-form tool ids, simulation result surface. |
| Model selection/policy | `agent.models.read`, `agent.models.manage` | disabled model denied, provider secret redacted, unsupported mode forbidden. |
| Proposal review/apply | governance decision capability plus target capability | decision-card result, applied/no-op/conflict/denied states. |

### Audit/Trace surfaces

- **Audit search** (`list-search`): scoped query by time, actor, target, action type, result, policy/decision.
- **Trace timeline** (`audit-timeline`): AdminAuditEvent, PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace, tool/data-access, approval, denial, and outcome refs.
- **Evidence detail** (`detail-edit`): redacted evidence summaries and data classification.

Actions:

| Action | Capability id | Result behavior |
|---|---|---|
| Search audit | `audit.trace.read` or scoped module audit read | partial-data when evidence redacted; forbidden for missing auditor/admin scope. |
| Open trace detail | `audit.trace.read` | audit timeline surface with redaction profile; hidden not-found for cross-scope trace. |
| Export/request evidence | future governance/audit capability | approval-required or denied unless explicitly in scope. |

### Governance/Policy surfaces

- **Decision card** (`decision`): recommendation, evidence, risk/confidence/impact, alternatives, approve/reject/defer/escalate.
- **Policy proposal diff** (`governance-diff`): before/after, simulations, affected agents/tools/surfaces, approval boundary.
- **Policy simulation** (`workflow-status` or `outcome`): simulation progress/result and no-side-effect state.
- **Outcome review** (`outcome`): outcome metrics linked to decisions, traces, and rollback/improvement proposals.

Actions:

| Action | Capability id | Result behavior |
|---|---|---|
| Propose policy | `governance.policy.propose` | append governance diff, approval-needed when authority expands. |
| Run simulation | `governance.policy.simulate` | workflow/progress surface then outcome/result. |
| Approve/reject/defer/escalate decision | decision approval capability | accepted/no-op/approval-required/denied with trace. |
| Apply governed commit | target governed capability plus approval basis | conflict for stale proposal, denied for missing approval, audit trace required. |

## Action and denial contract

Every `CapabilityActionRequest` includes:

- `actionId`, `capabilityId`, `input`, `selectedContextId`, `correlationId`;
- `surfaceId` when launched from a surface;
- `idempotencyKey` when required by the `SurfaceAction`.

Every `CapabilityActionResult` returns:

- one of `accepted`, `denied`, `validation-error`, `approval-required`, `conflict`, `no-op`, `failed`;
- safe user-facing `message`;
- `correlationId`, `traceIds`;
- optional `resultSurface` for appended/updated detail, decision, workflow, audit, governance, or outcome surfaces.

Required denial/redaction reason codes for fixtures and implementations:

- `ACCOUNT_DISABLED`
- `CONTEXT_REQUIRED`
- `CONTEXT_FORBIDDEN`
- `MEMBERSHIP_NOT_ACTIVE`
- `TARGET_NOT_FOUND_OR_FORBIDDEN`
- `ROLE_ESCALATION_DENIED`
- `LAST_ADMIN_DENIED`
- `APPROVAL_REQUIRED`
- `AUTHORITY_EXPANSION_REQUIRES_APPROVAL`
- `AGENT_ARTIFACT_INACTIVE`
- `TOOL_BOUNDARY_DENIED`
- `MODEL_POLICY_DENIED`
- `TRACE_REDACTED`
- `CROSS_CONTEXT_EVENT_IGNORED`

## Fixture alignment checklist

Existing or future fixtures under `frontend/src/workstream/fixtures/**` should include:

- `/api/me` variants: tenant admin, regular member, auditor/support, disabled account, no-membership/forbidden.
- functional agents: Access/Profile, User Admin, Agent Admin, Audit/Trace, Governance/Policy, plus hidden/disabled examples.
- surface envelopes for all five core functional agents, not only generic dashboard cards.
- action fixtures where every consequential action has a capability id, idempotency rule, audit event type, trace requirement, and denial/approval path.
- result fixtures for all statuses: accepted, denied, validation-error, approval-required, conflict, no-op, failed.
- realtime fixtures for create/update/action accepted/action denied/workflow progressed/stale/reconnected/duplicate/out-of-order/malformed/cross-context-denied.
- frontend secret-boundary strings absent from fixtures: provider secrets, API keys, raw JWTs, raw invitation tokens, token hashes, Resend secrets, hidden platform instructions.

## Test expectations

Contract tests should assert:

- workstream client exposes bootstrap, `/api/me`, agents, items, surfaces, and action calls;
- rail authorization handles visible, denied, hidden, disabled, and attention states;
- Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy have realistic surfaces and capability-backed actions;
- denied/forbidden actions preserve user-safe reason codes and do not rely on frontend hiding;
- redaction markers and trace/correlation ids survive rendering;
- event merge helpers treat duplicate, out-of-order, malformed, forbidden, and cross-context events as safe no-ops or stale markers;
- fixtures and browser DTOs contain no secrets or raw tokens.
