# Five-Core v0 Workstream Dependency Map

## Purpose

This map coordinates the five sibling implementation mini-projects so they can proceed one at a time without re-planning the shared foundation. It complements `shared-five-core-v0-contract.md`.

## Recommended execution order

1. `specs/my-account-workstream-v0/`
2. `specs/user-admin-workstream-v0/`
3. `specs/agent-admin-workstream-v0/`
4. `specs/audit-trace-workstream-v0/`
5. `specs/governance-policy-workstream-v0/`

This order favors user/context visibility first, then administration, then behavior governance, then trace investigation, then policy/governance expansion. A later task may reorder only with an explicit queue update and dependency rationale.

## Shared prerequisites for all five queues

Every sibling queue should reference and inherit:

- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`
- the runtime completion doctrine from `AGENTS.md`, `skills/README.md`, `docs/minimum-ai-first-saas-app.md`, and `templates/ai-first-saas-starter/README.md`

Each queue's first implementation task should be bounded by:

- selected `AuthContext` and backend role/capability checks;
- governed capability contracts before exposure through UI/API/tool/workflow/timer/consumer paths;
- request/response Akka Agent runtime for user-facing model-backed turns;
- `ToolPermissionBoundary` enforcement and trace emission for managed-agent tools;
- fail-closed provider behavior instead of deterministic normal-runtime fallback;
- local runtime validation appropriate to the named scope.

## Dependency graph

```text
shared five-core v0 contract
  ├─ My Account v0
  │   ├─ establishes signed-in user tile behavior, selected AuthContext visibility,
  │   │  browser-safe account/context surfaces, and aggregate next-step links
  │   └─ feeds User Admin, Audit/Trace, and Governance/Policy with user-visible
  │      context/authority patterns
  ├─ User Admin v0
  │   ├─ depends on My Account context/authority display patterns
  │   ├─ establishes account/member/role/capability administration surfaces
  │   └─ feeds Agent Admin and Governance/Policy with role/capability management
  ├─ Agent Admin v0
  │   ├─ depends on User Admin capability/role semantics
  │   ├─ establishes governed managed-agent definitions, prompts, skills,
  │   │  references, manifests, model refs, ToolPermissionBoundary, and seeds
  │   └─ feeds Audit/Trace and Governance/Policy with behavior-change and
  │      model/tool trace semantics
  ├─ Audit/Trace v0
  │   ├─ depends on trace emission from My Account, User Admin, and Agent Admin
  │   ├─ establishes trace search/investigation/correlation surfaces
  │   └─ feeds Governance/Policy with evidence and replay inputs
  └─ Governance/Policy v0
      ├─ depends on User Admin permissions, Agent Admin behavior artifacts,
      │  and Audit/Trace evidence
      └─ establishes policy/approval/governance controls for later full-core work
```

## Workstream dependency details

### My Account

Primary upstream dependencies:

- shared five-core v0 contract;
- existing starter `/api/me`, selected context, workstream launcher, and runtime-agent baseline.

Provides downstream:

- signed-in user tile as the only My Account launcher;
- account/profile/settings/current-context display pattern;
- safe self-service next-step and denial recovery patterns;
- aggregate links/actions that open other authorized workstreams without granting authority.

Readiness gates:

- browser-safe `/api/me` fields and capability-driven UI;
- no duplicate top-rail My Account launcher;
- trace/correlation references for self-service and denial paths;
- runtime validation through UI/API path.

### User Admin

Primary upstream dependencies:

- My Account context/authority display and selected `AuthContext` semantics;
- shared role/capability and tenant/customer authorization rules.

Provides downstream:

- account/member/role/capability administration patterns;
- invitation/readiness next-step visibility;
- capability basis for Agent Admin and Governance/Policy authority checks;
- audit events for administrative access changes.

Readiness gates:

- backend checks for tenant/customer, membership, role/scope/capability, disabled/missing authority, and idempotent no-op cases;
- UI states for forbidden, validation, pending invitation/readiness, and trace links;
- deterministic non-AI services for authorization and validation, not prompt-only rules.

### Agent Admin

Primary upstream dependencies:

- User Admin role/capability authority;
- shared request/response Akka Agent runtime and managed-agent doctrine.

Provides downstream:

- governed managed-agent `AgentDefinition` records;
- prompt, skill, reference, manifest, model-ref, and `ToolPermissionBoundary` lifecycle surfaces;
- seed/default behavior material visibility;
- behavior-change proposal/review patterns where included;
- trace semantics for prompt assembly, skill/reference loads, tool denials, and model-provider failures.

Readiness gates:

- normal workstream turns still use request/response Akka Agent, not `AutonomousAgent` by default;
- missing model/provider config fails closed with actionable surface;
- static prompts, ad hoc tools, or direct provider calls do not count as managed runtime completion;
- provider secrets remain backend-only.

### Audit/Trace

Primary upstream dependencies:

- trace emission from My Account, User Admin, and Agent Admin;
- shared workstream log and correlation-id conventions.

Provides downstream:

- trace search/list/detail and investigation patterns;
- denial/model/tool/capability/audit event visibility;
- evidence and replay inputs for Governance/Policy decisions;
- trace-link conventions reused by all later domain-specific workstreams.

Readiness gates:

- reads are scoped and redacted by `AuthContext`;
- cross-tenant/customer trace access is denied and traced;
- UI handles empty, forbidden, partial, redacted, and error states;
- trace surfaces identify request/response, AutonomousAgent task, deterministic service, policy, and capability events accurately.

### Governance/Policy

Primary upstream dependencies:

- User Admin permission/capability semantics;
- Agent Admin governed behavior artifacts and tool boundaries;
- Audit/Trace evidence and correlation surfaces.

Provides downstream:

- policy/permission concept surfaces;
- approval/governance boundary patterns;
- behavior-change activation controls where in v0 scope;
- rules for later full-core and domain-specific workstreams to request or prove authority.

Readiness gates:

- policy text does not grant backend authority;
- activation/approval capabilities are explicit, authorized, audited, and idempotent;
- high-impact or authority-expanding changes require human-governed approval unless a narrow accepted autonomous boundary exists;
- UI exposes evidence, risk/impact, decision status, and trace links.

## Cross-workstream validation gates

Before moving from one sibling mini-project to the next, the completed workstream should prove:

- it references `shared-five-core-v0-contract.md` or has equivalent inherited language in README/backlog/task briefs;
- first runnable task and terminal verification task remain present in that queue;
- required checks ran or blockers were recorded;
- no normal runtime path was satisfied by deterministic/demo/mock/simulated/model-less substitutes;
- new capabilities, traces, and UI surfaces do not weaken existing AuthContext, ToolPermissionBoundary, provider, secret-boundary, or tenant-isolation rules.

## Terminal verification expectations

The final verification task in this coordination mini-project should confirm:

- this contract and dependency map exist and cover runtime, auth, capability, trace, UI, model-provider, request/response, AutonomousAgent, and deterministic-service rules;
- all five sibling queues reference or inherit the shared contract;
- each sibling queue has a runnable first task and terminal verification task;
- any gaps are converted into bounded follow-up tasks before a new terminal verification task.
