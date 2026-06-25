# AI-First SaaS SaaS Foundation App Domain PRD

## PRD role

This is the machine-consumable domain PRD for the reusable SaaS Foundation App domain. It is source input for skills-pack consumption, app-description generation, SaaS Foundation App realization, backlog planning, and implementation review.

The document intentionally prioritizes deterministic decomposition over human narrative.

## Domain identity

- **Domain id:** `ai_first_saas_core_app`
- **Domain name:** AI-First SaaS SaaS Foundation App Domain
- **Domain type:** mandatory generated-app foundation domain
- **Applies to:** every generated full-stack secure AI-first SaaS app unless explicitly out of scope
- **Primary architecture:** workstream-first authenticated application shell
- **Frontend substrate:** React/Vite/TypeScript workstream shell, left rail, persistent composer, structured surfaces
- **Backend substrate:** Akka Java SDK components selected from capability contracts

## Shared UI style and theme contract

Core workstream surfaces inherit the canonical style contract from `../../web-ui-style-guide.md`:

- **Selected style:** `ai-first-workstream-enterprise` — a calm enterprise workstream interface for delegated agent work, evidence, decisions, governance, audit, and outcomes.
- **Theme model:** named-theme selection, not mode-first `light`/`dark`/`system` preferences.
- **Initial named themes:** `aurora-light`, `cobalt-light`, `obsidian-dark`, `midnight-dark`, and `dark-night`.
- **My Account settings:** expose available theme names and persist/apply the selected theme id as `preferredThemeId`; theme choice is not authorization.

Surface PRDs should keep capability/action semantics authoritative and add only surface-specific appearance expectations. Generated UI should render dashboards, lists, detail cards, decision cards, diff reviews, audit timelines, governance controls, and system-message surfaces as enterprise workstream surfaces rather than generic dashboard or CRUD mockups.

## Domain invariant

```text
Human/product intent
→ core SaaS domain requirements
→ workstreams as root app units
→ exactly one backing functional/context-area agent per workstream
→ workstream-agent prompt/skills/expertise
→ structured surfaces and typed system-message surfaces
→ surface actions and surface-request actions
→ governed backend capabilities
→ APIs/tools/workflows/views/entities/timers/consumers
→ audit/work traces and tests
```

Rules:

- Workstream is the root authenticated application abstraction.
- Every workstream is backed by exactly one functional/context-area agent.
- Surfaces are the only renderable workstream artifacts.
- System messages are typed surfaces.
- Every surface action, including read/query and surface-request actions, maps to a governed backend capability.
- Workstream agents can guide, explain, request surfaces, and invoke allowed capabilities, but backend capabilities enforce authority.
- Frontend visibility, prompt text, route names, and disabled buttons are not authorization controls.

## Required core workstreams

| Workstream id | Directory | Backing functional agent | Required by | Purpose |
|---|---|---|---|---|
| `my_account` | `my-account-workstream/` | `functional_agent.my_account` | all generated apps | signed-in user's own account, context, profile, settings, sign out, and capability visibility |
| `user_admin` | `user-admin-workstream/` | `functional_agent.user_admin` | all SaaS Foundation App/downstream apps; SaaS Foundation App may begin here as SaaS Foundation App scope | users, invitations, memberships, roles/capabilities, disabled access, access review, support-access visibility |
| `agent_admin` | `agent-admin-workstream/` | `functional_agent.agent_admin` | all AI-first generated apps with governed agents | agent definitions, prompts, skills, references, manifests, tool boundaries, behavior proposals, approvals, traces |
| `audit_trace` | `audit-trace-workstream/` | `functional_agent.audit_trace` | all SaaS Foundation App/downstream apps | searchable audit/work traces for identity, authz, data access, tool use, decisions, workflows, governance, outcomes |
| `governance_policy` | `governance-policy-workstream/` | `functional_agent.governance_policy` | generated apps with policies, prompts, thresholds, approval gates, or behavior changes | policies, approval rules, simulations, proposals, learning, governed activation/rollback |

## Shared actors and authority

| Actor/role | Scope | Baseline authority |
|---|---|---|
| `signed_in_user` | own account and selected AuthContext | access My Account and authorized workstreams |
| `tenant_admin` | tenant | manage users, memberships, roles within granted capabilities; manage agents/policies when granted |
| `customer_admin` | customer within tenant | manage customer-scoped users/data where granted |
| `auditor` | tenant/customer or support-authorized scope | read scoped audit/trace evidence; no side effects by default |
| `support_operator` | SaaS owner/support context plus explicit support-access grant | investigate tenant issues under audit; no silent tenant data access |
| `saas_owner_admin` | SaaS owner/platform | bootstrap tenants, support-access controls, billing/platform administration where implemented |
| `workstream_agent` | selected workstream + selected AuthContext | guide user, request/read surfaces, call allowed read/proposal tools; side effects only via explicit governed capability boundary |
| `internal_agent` | service/workflow authority basis | bounded classification, summarization, evaluation, proposal, simulation, enrichment, or routing; no UI rail presence |

## Shared security foundation

Required objects/concepts:

- WorkOS/AuthKit browser authentication.
- Local authorization state: `Account`, `UserProfile`, `UserSettings`, `Membership`, `Role`, `Permission`/`Capability`, selected `AuthContext`.
- Organization model: `SaaS Owner`, `Tenant`, `Customer`.
- `/api/me` with browser-safe account, memberships, selected/default context, roles/capabilities, profile, settings, visible workstreams, and support-access state.
- Invitation lifecycle with Resend production email and captured local/dev/test outbox.
- Backend authorization on every protected route, component command, view query, stream, agent tool, workflow action, consumer side effect, timer action, and surface action.
- Tenant/customer isolation on reads, writes, events, and surface payloads.
- Audit/work traces for identity, authz, policy, approval, data access, tool use, decisions, workflow progress, and outcomes.
- Governed runtime agent artifacts: `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`.

## Shared surface type baseline

These surface types inherit the shared UI style and named-theme contract above. Surface files may use generic type names such as `dashboard` or `data_table`, but their generated appearance should follow the AI-first enterprise workstream patterns in `../../web-ui-style-guide.md`.

Every core workstream may use:

- `dashboard` / attention summary
- `markdown_response`
- `system_message`
- `data_table` / search results
- `detail_card`
- `form`
- `decision_card` / `approval_card` / `exception_card`
- `diff_review`
- `audit_timeline`
- `workflow_status`
- `policy_or_version_card`
- `metric_panel` / outcome review where applicable

Surface files in workstream PRDs must define payload, actions, capability ids, authority, states, traces, and tests. Static mockups are not sufficient.

## Shared capability inventory and exposure channel contract

Every capability referenced by a core workstream is a governed backend contract. APIs, tools, surface actions, confirmed human chat tool plans, AI-backed agent-tool calls, workflow steps, timers, consumers, views, and internal methods are exposure or realization channels over that contract.

Every capability must define:

- stable capability id;
- capability class: read/evidence, command, proposal, approval, workflow, governance, trace/audit, scheduled, reactive;
- actors/callers;
- AuthContext and tenant/customer scope;
- input/output DTOs and redaction;
- validation and idempotency/no-op behavior;
- data access and side effects;
- approval/escalation policy;
- audit/work-trace fields;
- exposure channels/actor adapters: `surface_action`/browser API, `human_chat_tool_plan`, `agent_tool_call`/workstream-agent tool, internal-agent tool, workflow step, timer, consumer, MCP tool/resource, view, internal method;
- tests.

Exposure-channel rules:

- Browser APIs are frontend exposures of capabilities.
- Confirmed `human_chat_tool_plan` adapters are conversational human-backed exposures: the workstream agent proposes a concrete plan, binds explicit confirmation to it, and execution reuses the governed capability/tool contract.
- AI-backed workstream-agent tools (`agent_tool_call`) are model-facing exposures governed by the active workstream tool catalog and `ToolPermissionBoundary`.
- Internal-agent tools are backend AI-worker exposures of capabilities.
- Surface actions reference capabilities and usually invoke browser APIs.
- Chat plans and agent tools may invoke the same capabilities as surface actions when shared governed-tool ids, authority, required inputs, confirmation/approval, idempotency, audit/work traces, and result or partial-failure surfaces are preserved.
- Side-effecting tools default to surface action, plan-bound human confirmation, draft/proposal, or approval flows unless a bounded autonomous policy explicitly allows execution.

## Shared Akka realization expectations

Select components from capability semantics:

- Event Sourced Entities: audit-grade decisions, approvals, policies, prompts/skills lifecycle, invitations, traces where event history matters.
- Key Value Entities: current account/profile/settings/configuration state.
- Workflows: invitation onboarding, approvals, behavior-change lifecycle, export requests, long-running agent work.
- Views: `/api/me` projections, directories, dashboards, queues, audit search, trace search, policy lists.
- Agents: workstream agents and internal agents for guidance, summarization, proposal, evaluation, simulation.
- Consumers: audit enrichment, email delivery, projection reactions, notification/outbox processing.
- Timed Actions: invite expiry/reminders, access-review reminders, retention/export expiry, periodic summaries.
- HTTP endpoints: browser APIs for surface payloads/actions, streaming, frontend hosting.
- MCP endpoints: optional selected governed tool/resource exposure.

## Domain readiness

Ready when:

- all five workstream PRDs exist and declare required surfaces, actions, capabilities, traces, and tests;
- app-description generation can map each workstream to `12-workstreams/`, `10-capabilities/`, `40-auth-security/`, `50-observability/`, `55-ui/`, and `30-tests/`;
- implementation planning can produce Akka components and frontend surface work from capability contracts;
- no core surface remains a non-functional mockup;
- tests cover authz, tenant isolation, denied access, disabled users, stale data, audit, system-message redaction, and frontend secret boundaries.

Not ready if:

- a core workstream is represented only as pages or CRUD screens;
- workstream agent expertise is missing for required user intents;
- surface actions do not reference capability ids;
- protected reads or navigation happen without backend capabilities;
- system messages are ad hoc strings;
- role/scope checks are frontend-only;
- audit/work traces are absent or untested.

## Workstream PRDs

- `my-account-workstream/README.md`
- `user-admin-workstream/README.md`
- `agent-admin-workstream/README.md`
- `audit-trace-workstream/README.md`
- `governance-policy-workstream/README.md`
