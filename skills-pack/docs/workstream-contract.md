# Workstream Contract

## Status and scope

This is the compact canonical contract for workstreams in generated secure AI-first SaaS applications. Use it as the schema-style source below `./agent-workstream-application-architecture.md` and alongside `./requirements-to-workstream-development-process.md`, `./structured-surface-contracts.md`, `./workstream-attention-contracts.md`, and `./workstream-expertise-model.md`. Use `./workstream-manifest-schema.md` for the machine-readable app-description index and `./minimum-implementable-workstream-slice.md` for one-slice implementation tasks.

A **workstream definition** is the design-time product vertical for authenticated consequential work. A **workstream instance** is the durable runtime timeline/log for one workstream definition in a selected tenant/customer/AuthContext scope. A **workstream view/session** is the browser's current rendering of an instance. Keep these terms separate when writing app descriptions, specs, APIs, and tests.

## Core invariant

```text
workstream definition
→ exactly one backing functional/context-area agent
→ role-specific dashboard and attention model
→ human surface graph with typed surfaces and system-message surfaces
→ capability-backed surface actions and governed-tools
→ optional internal workstream agent graph
→ workstream expertise bundle when LLM-backed
→ Akka/API/UI realization with auth, traces, retention, and tests
```

A workstream is not a page, route, CRUD module, chat session, Akka component, prompt, or generic assistant. Routes, endpoints, views, agents, workflows, timers, consumers, frontend components, and MCP/gRPC/HTTP APIs realize or expose the workstream after the contract is clear.

## Required workstream definition fields

| Field | Required content |
|---|---|
| `workstreamId` | Stable id, usually matching the functional area, for example `user-admin` or `sales-pipeline`. |
| Display name and responsibility | User-facing name plus the durable business responsibility this workstream owns. |
| Classification | `foundation` for SaaS Foundation App workstreams or `domain-specific` for business extensions. |
| Owning functional/context-area agent | Exactly one user-facing functional agent id; internal agents may support but do not own the workstream. |
| Managed agent definition id | Required tenant-governed managed-agent behavior record id for the owning functional agent; it may match `functionalAgentId` until separately named. |
| Workstream icon metadata | Stable icon id, visual hint, accent token, tooltip, accessible label, and optional approved asset ref. |
| Instance scope | Runtime instance key semantics such as `tenantId + selectedContextId + functionalAgentId`; optional customer/subthread keys only when explicitly created by capability contracts. |
| Authorized actors | Roles, capability ids, membership status, support-access rules, service actors if any, and hidden/denied/disabled states. |
| Selected AuthContext assumptions | Tenant/customer scope, account/member identity, role/capability snapshot, and support-access/redaction behavior. |
| Default surface | Initial dashboard, attention, briefing, or explicit deferred/system-message surface. |
| Attention model | Workstream-local manifest category ids mapped to canonical `AttentionItem.category` values, severities, lifecycle, producers, idempotency, left-rail count effect, My Account aggregation effect, and tests; see `./workstream-attention-contracts.md`. |
| Role-specific dashboards | Dashboard purpose and variants by role/AuthContext; the dashboard is the human surface graph trunk. |
| Human surface graph | Nodes, edges, result surfaces, deferred typed surfaces if any, system-message surfaces, deep-link/surface-request behavior, stale/reconnect handling, and graph tests. Recommended placement: `12-workstreams/surface-graph.md` plus `deferred-typed-surfaces.md` when first-slice fallbacks exist. |
| Surface contracts | Stable surface ids/types/versions, exactly one owner functional agent, explicit reusable-by agents/workstreams, compact or full payload schemas, states, actions, auth, redaction, traces, and tests under `12-workstreams/surface-contracts/**`. |
| Capability/governed-tool map | Every read/query/mutation/surface request/agent tool/internal action maps to a capability id, governed-tool id, exposure channel, schema, idempotency, policy, audit, and tests. The manifest carries lightweight `surfaceActionMappings` with surface id, action id, capability id, governed-tool id, exposure channel, auth basis, idempotency summary, result/system-message surface, and trace requirement; this mapping is required at `capability-ready` and above. |
| Workstream expertise bundle | Required for LLM-backed functional agents: prompt intent, model binding, skills, references, manifests, loader tools, tool boundary, traces, governance owner, and tests. |
| Internal workstream agent graph | Virtual dashboard agent, worker agents/AutonomousAgent tasks, delegation edges, progress/result/failure surfaces, escalation, authority basis, tool boundaries, traces, and tests when delegated/background model work exists. Manifest `internalWorkers` entries are structured when present; omit or use `[]` when no internal/background worker behavior is claimed. |
| Runtime realization | Selected Akka substrate and participants, HTTP/gRPC/MCP/API/frontend/realtime paths, provider fail-closed behavior, and local validation. At `runtime-ready` and `production-ready`, manifest `readinessEvidence` must name local commands, an API/UI smoke path, provider/security fail-closed check, and trace evidence from the real governed runtime path. |
| Retention and redaction | Durable workstream log, summaries, audit-grade trace retention, actor labeling, deleted/disabled-account handling, frontend-safe and agent-safe redaction. |
| Observability | AdminAuditEvent, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, DecisionTrace, PolicyInvocation, ToolInvocation, DataAccessEvent, correlation/causation ids. |
| Tests/readiness | Authorization, tenant/customer isolation, surface rendering, action-to-capability behavior, tool-boundary denial, attention lifecycle, traces, provider fail-closed, API/UI runtime validation, and readiness level. |

## Runtime instance fields

Use explicit runtime fields when persisting or returning workstream items:

```ts
type WorkstreamInstanceRef = {
  workstreamId: string;              // definition/type id
  functionalAgentId: string;         // exactly one owner for the definition
  workstreamInstanceId: string;      // stable runtime timeline/log id
  tenantId: string;
  selectedContextId: string;
  customerId?: string;
  accountId: string;
  membershipId: string;
  roleIds: string[];
  capabilityIds: string[];
  supportAccess?: { active: boolean; reason?: string; expiresAt?: string };
  createdAt: string;
  lastActivityAt?: string;
  retentionClass: "routine" | "consequential" | "audit-grade";
};
```

A product vertical such as `user-admin` may have many runtime instances, one per authorized tenant/customer/AuthContext scope or explicitly modeled subthread. Do not use route names or frontend session ids as durable workstream ids.

## Ownership and reuse rules

- Each workstream definition has exactly one owning functional agent.
- A surface may be reusable across workstreams, but it has exactly one owner functional agent and explicit `reusableByFunctionalAgentIds`/reusable-by workstreams.
- My Account may aggregate authorized attention across accessible workstreams, but it does not own the source workstreams or their source items.
- Internal agents support a workstream through an internal graph; they are not left-rail workstreams unless they become user-facing responsibility boundaries.
- Cross-workstream navigation is a governed surface-request edge such as `open_workstream` or `open_attention_item`; it does not create shared authority.
- Prompt text, expertise text, rail visibility, hidden UI state, and tool descriptions cannot grant permissions or expand scope.

## Readiness levels

Use these labels in app descriptions, specs, backlogs, and review summaries instead of a vague "ready":

| Level | Meaning |
|---|---|
| `identified` | Workstream id, responsibility, and owning functional agent are named. |
| `described` | Required contract fields are captured or explicitly deferred with scope impact. |
| `surface-ready` | Dashboard, attention categories, surface graph, and surface contracts are defined. |
| `capability-ready` | Surface actions/tools map to governed capabilities/governed-tools with auth, idempotency, policy, audit, and tests. |
| `expertise-ready` | LLM-backed agent has governed model binding, prompt, skills, references, manifests, loader tools, tool boundary, traces, and tests. |
| `runtime-ready` | Real local Akka/API/UI path works for the stated scope, including provider/security fail-closed behavior. |
| `production-ready` | Runtime path plus retention, redaction, observability, accessibility, resilience, and regression coverage are complete for production expectations. |

Do not claim `runtime-ready` from static fixtures, mock-only responses, deterministic provider bypasses, frontend-only badges, or service-only model calls outside the governed Akka Agent path.

## ID taxonomy

Do not silently substitute one id family for another. Map ids explicitly when implementation examples, frontend adapters, governed managed-agent records, or Akka component names differ.

| ID | Example | Meaning |
|---|---|---|
| `workstreamId` | `user-admin` | Product vertical/workstream definition id. |
| `functionalAgentId` | `user-admin-agent` | Exactly-one user-facing workstream owner. |
| `managedAgentDefinitionId` | `user-admin-agent` or `agent.user-admin` | Tenant-governed managed-agent behavior record id. |
| Akka component class/name | `WorkstreamRuntimeAgent` | Java runtime implementation detail. |
| `surfaceId` | `user-admin-dashboard` | Structured surface contract id. |
| `capabilityId` | `secure-tenant-user-foundation` | Backend authority family. |
| `governedToolId` | `useradmin.invitation.create` | Executable semantic operation inside a capability. |

## Minimum app-description placement

```text
app-description/12-workstreams/
  workstream-manifest.json          # machine-readable workstream/agent/surface/capability index
  functional-agents.md              # workstream catalog and exactly-one functional-agent ownership
  workstreams-and-retention.md      # instance semantics, retention, redaction, durable log fields
  attention-and-dashboards.md       # attention categories, dashboard variants, My Account/rail aggregation
  internal-agents.md                # supporting internal agent graph candidates
  surfaces-index.md                 # surface inventory and shared/deferred/domain sections
  surface-graph.md                  # explicit surface nodes and edges
  deferred-typed-surfaces.md        # first-slice fallback/deferred result surfaces when present
  foundation-workstream-completeness.md # readiness/evidence/gap matrix for foundation workstreams
  surface-contracts/*.md            # structured surface contracts
  workstream-expertise/*.md         # one bundle per LLM-backed functional agent
app-description/10-capabilities/**  # capability and governed-tool definitions
app-description/55-ui/**            # browser realization, routes/deep links, shell rendering
app-description/70-traceability/**  # workstream/agent/surface/capability/test/trace maps
```

## Validation checklist

A workstream contract is incomplete if any non-deferred item is missing:

- [ ] workstream definition fields are present;
- [ ] type vs runtime instance semantics are clear;
- [ ] exactly one owning functional agent is named;
- [ ] role/AuthContext/tenant/customer authorization and hidden/denied states are specified;
- [ ] attention categories and dashboard variants are specified;
- [ ] surface graph nodes and edges map to structured surface contracts, with exactly one owner per surface and explicit deferred typed surfaces where fallbacks remain;
- [ ] every protected edge/action maps to a capability and governed-tool exposure channel, with manifest `surfaceActionMappings` required from `capability-ready` upward;
- [ ] LLM-backed behavior has an expertise bundle and governed runtime path;
- [ ] internal/background agent work has structured worker entries, graph, task lifecycle, authority, traces, and progress/result/failure surfaces;
- [ ] retention, redaction, audit/work traces, and tests are linked;
- [ ] readiness level is honest; local validation is stated, and `runtime-ready`/`production-ready` claims include explicit runtime evidence rather than planned checks.
