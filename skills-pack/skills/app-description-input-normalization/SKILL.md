---
name: app-description-input-normalization
description: Normalize flexible user input into a structured app-description delta envelope covering intent, capability, behavior, tests, auth/security, observability, review requests, and realization requests before routing or maintenance.
---

# App Description Input Normalization

Use this skill when the harness needs to turn flexible user language into a consistent internal change envelope before routing, maintenance, impact analysis, or generation decisions.

This skill does not primarily decide the final next step.
Its job is to convert messy input into a stable normalized representation that later skills can use reliably.

## Goal

Produce a normalized input result that:
- preserves the user's intent in a structured form
- separates description-change intent from generation and review intent
- extracts candidate workstream counts/boundaries, attention needs, role-specific dashboards, human surface graph nodes/actions, internal workstream agent graph candidates, governed capabilities, governed-tool/browser-tool/agent-tool/internal-tool candidates, autonomous task candidates, events/notifications/traces, behavior, tests, auth/security, UI, and observability
- distinguishes confirmed statements from inferred assumptions
- records ambiguity explicitly instead of hiding it
- gives downstream skills a stable basis for routing and maintenance

## Required reading

Read these first if present:
- `../../../AGENTS.md`
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/requirements-to-workstream-development-process.md` for the canonical input/PRD → workstreams → attention → dashboards → surfaces/actions → capabilities → Akka substrate process
- `../docs/minimum-ai-first-saas-app.md` for minimum/core app/basic/chatbot-like generated SaaS scope: five core workstream core app domain with `markdown_response`, not a generic chatbot or single-workstream slice
- `../docs/agent-workstream-application-architecture.md` for functional-agent workstream semantics
- `../docs/structured-surface-contracts.md` for surface/action contracts
- `../docs/capability-first-backend-architecture.md` for capability contract fields and exposure-surface semantics
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../app-descriptions/SKILL.md`
- `../app-description-bootstrap/SKILL.md`
- `../app-description-intake-router/SKILL.md`

Prefer these current generated-SaaS references when present:
- the target project `app-description/README.md` plus `../docs/core-ai-first-saas-foundation.md`
- target project `app-description/12-workstreams/functional-agents.md`
- target project `app-description/12-workstreams/surfaces-index.md`
- target project `app-description/10-capabilities/01-secure-tenant-user-foundation.md`
- target project `app-description/55-ui/ui-index.md`

Use current target-project app-description files and core app templates for normalization or cross-linking mechanics; do not depend on removed historical domain examples.

## Use this skill when

The task sounds like:
- "interpret this request first"
- "normalize this input before updating the description"
- "extract the real deltas from this user prompt"
- "separate behavior, test, security, and generation intent"
- "turn this revision request into a structured maintenance input"

Use it especially when the input is:
- broad
- mixed across multiple concerns
- partly ambiguous
- combining revision, review, and realization intent
- likely to affect more than one description layer

## Core operating rule

Normalize the user's meaning without overcommitting beyond what they actually said.

A good normalized result:
- captures explicit requests faithfully
- identifies probable implied deltas carefully
- separates confirmed facts from assumptions
- leaves open questions visible

## What this skill must extract

From user input, derive as applicable:
- primary intent
- secondary intents
- functional-agent/workstream candidates, including count/boundary changes, owner roles, tenant/customer scope, and core-foundation vs domain-specific classification
- attention needs per workstream: what needs my attention, target audience, severity/lifecycle, source/freshness expectations, and whether the item contributes to My Account or left-rail counts
- role-specific dashboard candidates: actor-specific summary cards, attention item surfaces, blocked/overdue/risky/failed/waiting states, participant visibility, and next authorized actions
- human surface graph candidates, including dashboard trunk, surface nodes, surface actions/edges, system-message surfaces, surface states, reusable placement, and surface-request actions such as `open_workstream`, `open_attention_item`, approval, retry, acknowledge, dismiss, escalate, or investigation start
- capability or scope delta, including candidate actors/callers, AuthContext, schemas, side effects, idempotency, approval/policy, audit/trace, and exposure surfaces
- governed-tool candidates inside capability/surface-action maps, including semantic operation ids plus browser-tool, agent-tool, and internal-tool exposure candidates
- internal workstream agent graph candidates, including virtual dashboard agent attention, worker delegations, escalation/result/proposal surfaces, expertise skill/reference updates, and denial/help semantics
- autonomous task candidates for durable internal/background model-driven work, including why Akka `AutonomousAgent` may fit typed lifecycle, snapshots/results, notifications, dependencies, failure/cancellation, delegation, handoff, teams, or moderation
- event/notification/projection/trace implications, including attention projection and audit/work trace candidates
- behavior delta
- test delta
- auth/security delta
- UI delta
- observability delta
- change-impact or readiness interest
- generation request details
- review/explanation request details
- explicit user constraints or preferences
- open questions created by ambiguity

## Normalized envelope

Use this normalized shape internally:

```md
# Normalized App Description Input

## Raw input summary
- ...

## Primary intent
- bootstrap | description-change | generate-app | review | mixed

## Secondary intents
- ...

## Confirmed deltas
- workstreams / functional agents:
  - workstreamId/name/responsibility:
  - count/boundary change:
  - owner functional agent:
  - authorized actors/roles/scope:
  - core-foundation vs domain-specific:
- attention / role-specific dashboard:
  - attention categories and target audience:
  - severity/lifecycle/source/freshness:
  - My Account / left-rail contribution:
  - default dashboard summary and attention surfaces:
- human surface graph / surface actions:
  - dashboard trunk and surface nodes:
  - surface ids/types/states:
  - surface-request actions:
  - action-to-capability/governed-tool candidates:
- capabilities / governed-tools:
  - scope/outcome:
  - actors/callers:
  - AuthContext/scope:
  - inputs/outputs:
  - side effects/idempotency:
  - approval/policy/audit/exposure surfaces:
  - governed-tool ids and browser-tool/agent-tool/internal-tool exposure:
- internal workstream agent graph:
  - virtual dashboard agent attention:
  - worker delegations and escalation/result/proposal surfaces:
  - expertise skill/reference updates and denial/help semantics:
- autonomous task candidates:
  - candidate background work:
  - AutonomousAgent fit signals:
  - lifecycle/notification/dependency needs:
- events / notifications / projections / traces:
- behavior:
- tests:
- auth/security:
- UI:
- observability:

## Candidate inferred deltas
- workstream boundary/count gaps:
- attention/role-specific dashboard gaps:
- surface graph/action gaps:
- capability/governed-tool contract gaps:
- internal workstream agent graph or expertise gaps:
- autonomous task fit or non-fit:
- event/notification/projection/trace impacts:
- linked behavior/tests/auth-security/UI/observability impacts:
- ...

## Realization request
- none | generate | run-tests | run-app | mixed

## Review request
- none | what-changed | readiness | impact | other

## Constraints and preferences
- ...

## Open questions
- ...
```

## Normalization rules

### 1. Separate intent from content
A user may ask for generation while also changing the app description.
Record both instead of forcing one to erase the other.

### 2. Separate confirmed from inferred
Use `confirmed` for what the user clearly requested.
Use `candidate inferred deltas` for plausible implications that still need confirmation or later skill validation.

### 3. Preserve the requirements-to-workstream chain
For broad generated-SaaS input, normalize in this order before code or component concepts:
1. secure SaaS/AuthContext assumptions, including the five core workstream core app domain when the prompt says minimum, core app, basic, smallest useful app, or chatbot-like SaaS
2. workstream count/boundary and functional-agent candidates
3. attention needs and role-specific dashboard implications
4. human surface graph nodes, states, system-message surfaces, and surface actions
5. governed capability/API candidates and governed-tool candidates in capability/surface-action maps
6. browser-tool, agent-tool, and internal-tool exposure candidates for each governed-tool
7. internal workstream agent graph candidates, worker delegations, escalation/result surfaces, and expertise skill/reference updates
8. request-based workstream Agent turns for immediate user-facing messages
9. autonomous task candidates for durable internal/background model-driven work
10. events, notifications, projections, and audit/work traces
11. behavior, tests, auth/security, UI, observability, realization, and review impacts

### 4. Preserve cross-layer separation
Do not collapse these into one bucket:
- workstreams / functional agents
- attention / dashboard
- structured surfaces / surface actions
- capability contract
- autonomous task candidate
- events / notifications / projections / traces
- behavior
- tests
- auth/security
- UI
- observability
- realization
- review

When a surface action or capability delta changes authority, side effects, approval, audit, exposure surfaces, attention counts, or autonomous task lifecycle, record the likely linked layer impacts instead of treating it as an isolated scope change.

### 5. Normalize to app-description language, not code language
Prefer terms like:
- capability
- behavior rule
- invariant
- acceptance case
- authorization rule
- audit requirement
instead of jumping to classes, endpoints, or frameworks.

### 6. Surface ambiguity explicitly
If the input leaves unclear whether something is a behavior change, test clarification, or security rule, record the ambiguity instead of guessing silently.

### 7. Preserve user priority signals
If the user says things like "for now", "must", "optional", "later", or "just for evaluation", carry those into constraints and preferences.

## Intent classification guide

### `bootstrap`
Use when the user is starting a new app description or no usable internal root exists.

### `description-change`
Use when the user is primarily changing capabilities, behavior, tests, security, or observability.

### `generate-app`
Use when the user explicitly asks to realize outputs now.

### `review`
Use when the user is mainly asking for explanation, readiness, or change summary.

### `mixed`
Use when more than one of the above clearly applies in one prompt.

## Handoff rules

Route onward as needed:
- to `app-description-bootstrap` when the normalized intent is `bootstrap`
- to `app-description-intake-router` when routing is still needed after normalization
- to `app-description-functional-agent-modeling` and `app-description-surface-modeling` before direct capability/UI routing when broad generated-SaaS input names work areas, dashboards, queues, command centers, approvals, decisions, audit timelines, workflow status, forms, tables, actions, or agent/chat areas; then route UI realization impacts to `app-description-ui`/`55-ui` only after surface/action meaning and capability backing are clear
- to `app-description-capability-modeling` when capability scope, actors, AuthContext, schemas, side effects, idempotency, approval, audit, exposure surfaces, or intended outcomes are the dominant delta after workstream/surface context is preserved
- to focused maintenance skills when normalization already isolates the dominant delta clearly, while preserving any linked workstream, attention, dashboard, surface action, autonomous task, event/notification/projection/trace, behavior, tests, auth/security, UI, observability, or readiness impacts
- to `app-description-change-impact` when the input is explicitly asking about affected areas or realization scope
- to `app-description-readiness-assessment` when the input explicitly asks whether generation is appropriate

## Clarification policy

Ask only the smallest questions needed to reduce material ambiguity in the normalized envelope.

Examples:
- "Is your main goal to revise the description only, or to revise it and then generate the app now?"
- "Is this primarily a new behavior rule, or are you mainly defining how it should be tested?"
- "Should I treat this as a security requirement, or as a general business rule that applies to all users?"

## Anti-patterns

Avoid:
- collapsing mixed intent into a single simplistic label
- converting uncertain implications into confirmed requirements
- normalizing directly into code-level tasks
- losing user preference signals like phased scope or evaluation-only intent
- treating review questions as hidden generation requests

## Final review checklist

Before finishing, verify:
- primary intent is explicit
- secondary intents are preserved when present
- confirmed vs inferred deltas are separated
- workstream, attention/dashboard, surface/action, capability, autonomous task, event/notification/projection/trace, behavior, tests, security, UI, and observability are separated
- realization and review requests are separated
- ambiguity is recorded as open questions instead of guessed away

## Response style

When answering:
- summarize the input briefly
- show the normalized intent and deltas clearly
- keep the structure compact but explicit
- make it usable as an immediate handoff to routing or maintenance skills
