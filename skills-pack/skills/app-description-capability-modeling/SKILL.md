---
name: app-description-capability-modeling
description: Update the authoritative capability layer of the app description by defining business capabilities, scope boundaries, actors, outcomes, and links to behavior, tests, security, and observability artifacts.
---

# App Description Capability Modeling

Use this skill when the harness needs to define or revise the **capability layer** of the app description.

This skill maintains `10-capabilities/` as the inventory of what the app is for, what governed backend operations and queries exist, which governed-tools sit inside each capability, what user-visible outcomes they support, and what is in or out of scope.
It does not generate code.

## Goal

Create or update capability-oriented app-description artifacts that:
- define the app's business or user-visible capabilities clearly
- treat backend operations and queries as governed capability contracts and governed-tools before implementation or exposure choices
- separate in-scope from out-of-scope outcomes
- identify primary actors or callers
- preserve AI-first operating semantics when a capability delegates work, shapes human authority, or requires outcome accountability
- make capability boundaries stable enough for downstream operating-model, behavior, tests, security, and observability work
- link each capability to the artifacts that realize and verify it

## Required reading

Read these first if present:
- `../../../AGENTS.md`
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` for workstream attention/dashboard/surface-action/autonomous task provenance before component selection
- `../docs/capability-first-backend-architecture.md`
- `../app-descriptions/SKILL.md`
- `../app-description-input-normalization/SKILL.md`
- `../app-description-bootstrap/SKILL.md`
- `../app-description-functional-agent-modeling/SKILL.md`
- `../app-description-surface-modeling/SKILL.md`
- `../app-description-ui/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`

Prefer these AI-first SaaS seed capability references when present:
- target project `app-description/10-capabilities/capabilities-index.md`
- target project `app-description/10-capabilities/01-secure-tenant-user-foundation.md`
- target project `app-description/10-capabilities/02-ai-first-work-management.md`
- target project `app-description/10-capabilities/03-governance-decisions-and-audit.md`
- target project `app-description/10-capabilities/05-managed-agent-foundation.md`

For capability cross-linking mechanics, use current target-project app-description files and starter templates; do not depend on removed historical domain examples.

## Use this skill when

The task sounds like:
- "what capabilities does this app need?"
- "add a new business capability"
- "split this into capability areas"
- "what is in scope vs out of scope?"
- "model the capability layer before behavior details"

Use it for:
- new capability definition
- scope boundary clarification
- actor and outcome identification
- delegated-work, supervision, decision, governance, trace, or outcome-loop capability boundaries
- capability splitting or consolidation
- maintaining the `10-capabilities/` layer and its links

## Core operating rule

Capabilities describe **what the app must enable**, not how it is implemented.

A capability should be:
- business-meaningful
- stable enough to survive internal implementation changes
- narrow enough to link clearly to behavior and verification
- explicit about what it excludes

## Generated SaaS exposure rule

For generated full-stack SaaS, a user-facing capability change must record the source functional agent, attention category or role-specific dashboard context, workstream action, structured surface, surface graph edge, surface action, and governed-tool that expose or consume the capability. If no human workstream or browser surface should expose it, state `internal-only` explicitly and name the internal caller class. This prevents capability modeling from bypassing `12-workstreams/`, `55-ui`, and surface-to-capability traceability.

For durable internal/background model-driven work, record the internal workstream agent graph exposure explicitly: virtual dashboard agent, worker-agent/delegation node, start/query/cancel/result-read/external-complete/external-fail capabilities, task lifecycle state, snapshot/result schemas, notification/projection updates, governed-tools available to each worker, result/proposal/system-message surfaces, escalation rules, and whether Akka `AutonomousAgent` is the intended substrate. AutonomousAgent tasks do not grant authority; every lifecycle action and governed-tool call still inherits this capability contract.

## What this skill must capture

For each capability, identify and describe as applicable:
- stable capability id/name in product language
- capability class: `read/evidence`, `command`, `proposal`, `approval`, `workflow`, `policy/governance`, `trace/audit`, `scheduled`, or `reactive`
- business goal and purpose
- primary actors or caller classes, including humans, request-based workstream Agents, AutonomousAgent tasks, workflows, services, timers, consumers, support roles, or internal callers
- source workstream, attention category, role-specific dashboard/surface context, surface graph node/edge, and whether the capability opens, resolves, dismisses, escalates, or updates an attention item
- AuthContext, tenant/customer scope, role, permission, scope, and named capability grants
- typed input schema, validation, safe defaults, idempotency key, and correlation id expectations
- typed output schema, redaction rules, user/agent-safe fields, safe denial/error shape, and evidence boundaries
- data access boundaries, tenant/customer filters, PII/secret handling, and raw-state exposure limitations
- side effects such as state changes, external calls, publications, timers, emails, notifications, workflow starts, attention projection changes, AutonomousAgent task starts/results, or no side effects for read-only capabilities
- governed-tools inside the capability: stable governed-tool id, operation/query class, actors/callers, AuthContext, schemas, side effects, idempotency, approval/policy, audit/work trace, implementation mapping, and exposure as `browser-tool`, `agent-tool`, `internal-tool`, `workflow-tool`, `timer-tool`, `consumer-tool`, `MCP-tool`, API, view/query, or non-exposed backend method
- autonomous task lifecycle contract when applicable: task type, start criteria, progress/result surface, notification stream, dependency/timeout/failure/cancellation semantics, governed-tools used by the task, and result authority
- delegated operational work, if any
- retained human authority, approvals, exceptions, or supervision needs, if any
- policy, permission, risk/confidence threshold, evidence, trace, learning, or outcome-accountability needs, if any
- source functional agents, workstream actions, structured surfaces, surface actions, and action-to-capability map entries for user-facing exposure, or an explicit `internal-only` declaration
- selected exposure surfaces: workstream surface action, browser UI/API action (`browser-tool`), HTTP/gRPC API, request-based Agent tool (`agent-tool`), AutonomousAgent tool/task action, MCP-tool/resource/prompt, workflow-tool, view/query, timer-tool, consumer-tool, or internal-tool method
- notification and projection outputs for dashboards, My Account aggregate attention, left rail summaries, surface stale/reconnect behavior, and audit/work traces
- in-scope outcomes
- out-of-scope outcomes
- major constraints or assumptions
- linked operating-model artifacts under the required `15-operating-model/` for generated AI-first SaaS semantics
- linked behavior artifacts
- linked test artifacts, including success, validation, forbidden, tenant-isolation, idempotency, audit, approval, and surface-specific cases
- linked auth/security artifacts
- linked observability artifacts
- linked UI and traceability artifacts when relevant

## Standard capability output shape

Use this response shape when updating or summarizing capability work:

```md
# Capability Modeling Update

## Requested change
- ...

## Capability definition
- id / name:
- class:
- business goal:
- actors / callers:
- source workstream / attention / role-specific dashboard context:
- source surface graph node / edge:

## Authority and contract
- AuthContext / scope:
- permissions / named capability grants:
- inputs / validation / idempotency:
- outputs / redaction / denial shape:
- data access:
- side effects:
- governed-tools:
  - id:
  - class:
  - exposure (`browser-tool`, `agent-tool`, `internal-tool`, workflow/timer/consumer/MCP/API/view):
  - authority / schemas / side effects / idempotency / audit:
- source functional agents / workstream actions:
- source surfaces / surface actions / graph edges:
- exposure surfaces:
- internal workstream agent graph / autonomous task lifecycle / AutonomousAgent fit, if applicable:
- notification / dashboard / attention projection outputs:
- internal-only declaration, if applicable:

## AI-first operating semantics
- delegated work:
- retained human authority:
- governance / policy boundary:
- decision, exception, or supervision needs:
- trace / learning / outcome needs:

## In-scope outcomes
- ...

## Out-of-scope outcomes
- ...

## Major assumptions or constraints
- ...

## Linked layers
- operating model:
- behavior:
- tests:
- auth/security:
- observability:
- UI:
- traceability:
```

## Capability modeling rules

### 1. Model capability before implementation structure
Use business outcomes, governed operation/query contracts, governed-tools, and user-visible purpose, not classes, services, endpoints, or agent tools, as the primary definition.

### 2. Keep scope boundaries explicit
Every important capability should make clear what it does not include.
This reduces later ambiguity and uncontrolled scope creep.

### 3. Keep capabilities stable but bounded
Do not create one giant capability for the whole app.
Do not create tiny pseudo-capabilities that are really just implementation details.

### 4. Link forward deliberately
A capability is incomplete if it cannot be linked to behavior, verification, security, observability, UI when exposed to humans, traceability artifacts, governed-tool exposure channels, and any attention/dashboard/autonomous task notification projections it changes.

### 5. Separate capability change from behavior detail
A new capability may require later behavior work, but capability modeling should first establish the business boundary, authority model, contract shape, side-effect boundaries, audit/approval obligations, and intended outcomes.

### 6. Preserve AI-first semantics before component or CRUD framing
When the capability involves delegated operational work, autonomous or semi-autonomous judgment, human approval, policy controls, exceptions, auditability, or outcome accountability, define those as capability semantics.
Do not flatten them into CRUD records, dashboards, or a chatbot feature.
Link the capability to `15-operating-model/` artifacts for goals, agent authority, policies, decisions, traces, and outcomes as applicable.

### 7. Keep human governance visible
If automation can affect consequential state, money, commitments, permissions, customer communication, compliance, or operational outcomes, record what humans delegate and what humans retain.
If the boundary is unknown, ask or flag it before downstream behavior or generation work.

### 8. Preserve user language where useful
When the user names a business concept clearly, preserve that concept in the capability name or description rather than replacing it with framework language.

## Handoff rules

Route onward as needed:
- to `ai-first-saas-object-model`, `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, or `ai-first-saas-outcomes-metrics` when the capability needs focused operating-model semantics
- to `app-description-functional-agent-modeling` when the capability changes which user-facing functional agents can call it or which prompt/skill/tool boundaries apply
- to `app-description-surface-modeling` when the capability changes a structured surface payload, action, allowed state, or surface-to-capability map
- to `app-description-behavior-specification` when the capability needs concrete flows, rules, states, or invariants
- to `app-description-test-specification` when acceptance, evaluation, or scope-verification scenarios need to be defined
- to `app-description-auth-security` when the capability introduces differentiated actors, ownership, protected actions, or enforceable agent/human permissions
- to `app-description-observability` when the capability introduces auditable, measurable, traceable, or diagnostically important flows
- to `app-description-ui` when the capability adds, removes, or changes a human exposure surface, navigation/action availability, decision/supervision surface, frontend API contract, or capability-gated UI behavior
- to `app-description-readiness-assessment` when missing capability contract fields would force generation to invent actors, AuthContext, schemas, side effects, idempotency, approval, audit, exposure surfaces, or tests
- to `app-description-change-impact` when a capability change likely alters existing linked layers, realization scope, specs/backlogs, or pending tasks

## Clarification policy

Ask only the smallest questions needed to avoid creating the wrong capability boundary.

Examples:
- "Is this a new top-level capability, or a refinement of an existing one?"
- "What outcome is the user trying to achieve through this capability?"
- "What should explicitly remain out of scope for this capability right now?"
- "Does this capability apply to all users, or only a specific actor type?"
- "What AuthContext, tenant/customer scope, role, permission, or named capability grant is required?"
- "Is this read-only, side-effecting, approval-gated, scheduled, reactive, or internal-only?"
- "Which surfaces should expose it: browser UI, HTTP/gRPC, agent tool, MCP, workflow, timer, consumer, view/query, or none?"
- "What work is delegated to the system or agents, and what authority remains with a human?"
- "Which decisions require approval, exception handling, evidence, or audit trace?"

## Anti-patterns

Avoid:
- naming capabilities after technical components
- collapsing multiple unrelated outcomes into one vague capability
- omitting out-of-scope boundaries
- treating implementation tasks as capabilities
- treating endpoints, agent tools, workflows, autonomous task methods, notifications, browser-tools, internal-tools, governed-tools, or entities as the capability inventory root
- treating delegated operational work as generic CRUD plus a chatbot
- hiding approval, policy, exception, or audit needs inside later implementation
- leaving capability files unlinked to downstream behavior and tests

## Final review checklist

Before finishing, verify:
- the capability is named clearly
- business goal and actors are explicit
- AuthContext/scope, input/output shape, side effects, idempotency, policy/approval, audit/trace, governed-tools, exposure surfaces, and tests are explicit enough for downstream work
- linked behavior, auth/security, tests, UI, observability, traceability, and readiness impacts are named whenever the capability contract changes
- in-scope and out-of-scope outcomes are explicit
- major assumptions are recorded when relevant
- delegated work and retained human governance are explicit for generated AI-first SaaS semantics
- linked downstream layers are called out
- the result strengthens `10-capabilities/` rather than bypassing it

## Response style

When answering:
- summarize the capability change first
- state the capability boundary clearly
- list in-scope and out-of-scope outcomes explicitly
- avoid implementation terminology unless needed for clarification
- call out the next linked description layers to update
- mention operating-model links before behavior/test/security/observability links for generated AI-first SaaS semantics
