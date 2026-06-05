# App Description End-to-End Workflow Example

## Purpose

This document shows how a natural user revision should flow through the description-first skill stack for a generated secure AI-first SaaS app.

It is a **process example**. It demonstrates how the harness should:
- interpret flexible user input;
- normalize it without forcing skill names on the user;
- route to the smallest relevant description layers;
- update workstreams, surfaces, capabilities, tests, security, and observability in the right order;
- analyze change impact;
- reassess readiness;
- answer review questions without prematurely generating code.

Use this together with:
- `docs/description-first-application-doctrine.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- the target project `app-description/README.md` plus `docs/core-ai-first-saas-foundation.md`
- `docs/requirements-to-workstream-development-process.md`

Historical domain-specific app-description examples have been removed; use current target-project app-description files and core app templates for cross-linking mechanics.

## Example context

Assume the maintained `app-description/` already follows the core app core app-description shape and includes the five core workstream core app domain:

- `my_account` — current user's profile, context, settings, and cross-workstream attention.
- `user_admin` — users, memberships, invitations, access review, support access, and admin audit.
- `agent_admin` — managed AgentDefinition, prompts, skills, references, manifests, tool boundaries, and model/runtime policy.
- `audit_trace` — audit/work trace search, evidence views, and investigation support.
- `governance_policy` — policy, approvals, exceptions, decision cards, and authority review.

The description already has first-class layers for:
- `12-workstreams/functional-agents.md`
- `12-workstreams/surfaces-index.md`
- `surface-contracts/**`
- capability contracts
- behavior
- tests
- auth/security
- observability
- readiness and traceability
- `55-ui/` browser realization guidance

## Example user prompt

> Add an access review flow where tenant admins can ask the User Admin Agent to investigate risky memberships, show a decision card for proposed fixes, require human approval before disabling access, and then tell me if the description is ready to generate.

This is a good example because it is **mixed**:
- it changes a functional-agent workstream;
- it adds or refines structured surfaces and actions;
- it introduces a governed capability and approval gate;
- it may require durable internal/background agent work;
- it changes tests, security, observability, and readiness;
- it asks for a review, not immediate code generation.

## Step 1. Normalize input

Load `app-description-input-normalization` and convert the prompt into a stable envelope.

Expected normalized result shape:

```md
# Normalized App Description Input

## Raw input summary
- add tenant-admin access review investigation, decision card, approval-gated remediation, and readiness review

## Primary intent
- mixed

## Secondary intents
- description-change
- review

## Confirmed deltas
- workstreams:
  - User Admin workstream gains an access-review investigation path
- functional agents:
  - User Admin Agent can guide the review and explain proposed fixes
- surfaces:
  - access-risk investigation result surface
  - decision card for proposed disable-access fixes
- capabilities:
  - start access review investigation
  - approve proposed access fix
  - apply approved access fix
- behavior:
  - risky membership investigation proposes fixes but cannot disable access without human approval
- tests:
  - approval-required, forbidden, audit/trace, and local runtime/UI checks are implied
- auth/security:
  - tenant admin authority is required; cross-tenant and disabled-user paths deny
- observability:
  - investigation, proposal, approval, and applied fix require audit/work traces

## Candidate inferred deltas
- durable investigation may be an AutonomousAgent task candidate
- User Admin Agent normal user-facing turns remain request-based Akka Agent calls through the governed runtime path
- My Account and left rail attention may show pending access-review decisions

## Realization request
- none

## Review request
- readiness

## Open questions
- which risk signals are in scope for the first review version?
- should disabling access affect all memberships or only the selected tenant/customer context?
```

Why this matters:
- the prompt is not a hidden generation request;
- the harness keeps workstream, surface, capability, security, and runtime implications separate;
- unresolved risk-signal scope becomes a bounded question instead of an invented implementation detail.

## Step 2. Route the normalized input

Load `app-description-intake-router` and select the smallest safe sequence.

Expected routing result:

1. `app-description-functional-agent-modeling`
2. `app-description-surface-modeling`
3. `app-description-capability-modeling`
4. `app-description-behavior-specification`
5. `app-description-auth-security`
6. `app-description-observability`
7. `app-description-test-specification`
8. `app-description-ui` if browser realization contracts must change
9. `app-description-change-impact`
10. `app-description-readiness-assessment`
11. `app-description-readiness-summary`

Why this route:
- functional-agent responsibility and workstream behavior changed;
- structured surfaces/actions must exist before UI realization details;
- protected actions must map to capabilities before code or endpoints;
- security, traces, tests, and readiness are affected;
- generation is not requested.

## Step 3. Update functional-agent and workstream model

Update `12-workstreams/functional-agents.md` and related workstream index material.

Expected additions:
- User Admin Agent responsibility includes access-review risk investigation guidance.
- Durable risk investigations are marked as internal/background agent work candidates when typed lifecycle, snapshots, result review, cancellation, or handoff are needed.
- The User Admin workstream attention model includes pending access-review decisions, risky membership findings, blocked investigations, and failed remediation attempts.
- Workstream icon metadata and shell placement remain stable unless the user requested visual changes.

Important rule:
- request-based Akka `Agent` remains the default for immediate User Admin Agent turns;
- durable investigation work may route to `AutonomousAgent`, but it does not bypass capability authority or approval gates.

## Step 4. Update structured surfaces and actions

Update `12-workstreams/surfaces-index.md` and `surface-contracts/**`.

Expected surface contracts:

| Surface/action | Meaning |
|---|---|
| `surface.user_admin.access_review.dashboard.v1` | summary of active reviews, risky findings, pending decisions, and failed actions |
| `surface.user_admin.access_review.finding.v1` | evidence view for one risky membership or risk cluster |
| `surface.user_admin.access_review.decision_card.v1` | proposed fix with evidence, risk, confidence, authority basis, and approval actions |
| `action.user_admin.start_access_review` | request investigation for selected tenant/customer scope |
| `action.user_admin.approve_access_fix` | approve a proposed remediation |
| `action.user_admin.apply_approved_access_fix` | apply only after approval and policy checks |

Each action must declare loading, empty, forbidden, stale, and error states where relevant. Actions are not frontend-only jumps; they map to capabilities.

## Step 5. Update capability contracts

Update the capability layer before choosing components.

Expected capability shapes:

- `user_admin.access_review.start` (`command` or `task lifecycle action`)
  - caller: tenant admin in selected tenant/customer context
  - side effects: creates review record or task, emits audit/work trace, updates attention projections
  - substrate candidate: Workflow and/or AutonomousAgent task depending on lifecycle needs

- `user_admin.access_review.propose_fix` (`internal tool/result`)
  - caller: governed internal worker or request-based User Admin Agent through permitted tools
  - side effects: records proposal evidence and trace; does not disable access
  - approval: human approval required before any protected mutation

- `user_admin.access_review.approve_fix` (`approval`)
  - caller: authorized tenant admin, with last-admin and separation-of-duty checks
  - side effects: approval decision trace and pending application action

- `user_admin.membership.disable_approved` (`command`)
  - caller: workflow step or protected backend operation after approval
  - side effects: membership state change, audit event, attention update, notification/projection update

Capability contracts must include AuthContext, tenant/customer isolation, idempotency, policy/approval, audit/work trace, exposure surfaces, and tests.

## Step 6. Update behavior, security, observability, and tests

Behavior should state that investigation can recommend but not autonomously disable access.

Auth/security should specify:
- tenant admin authority required;
- disabled users denied;
- cross-tenant targets denied;
- last-admin and support-access constraints enforced;
- backend checks required even if the UI hides actions.

Observability should specify:
- AdminAuditEvent for protected access changes;
- AgentWorkTrace for model-backed investigation and recommendation;
- PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and ToolPermissionBoundary decisions when governed runtime agents are involved;
- trace links from decision card to evidence and final action.

Tests should add or update:
- authorized start review;
- forbidden cross-tenant or disabled-user attempt;
- proposal cannot directly disable access;
- approval required before mutation;
- denied approval path;
- audit/work trace linkage;
- UI decision-card action smoke;
- local runtime/API/UI validation for the named scope.

## Step 7. Update UI realization only after contracts exist

If the browser realization changes, update `55-ui/` after workstream, surface, capability, security, observability, and test meaning is clear.

Expected UI guidance:
- User Admin rail/workstream entry shows access-review attention counts from backend projections.
- The main workstream renders structured surfaces and decision cards, not ad hoc pages.
- Composer suggestions can request investigation but protected actions still call backend capabilities.
- Forbidden and stale states are explicit.
- No frontend secret or authorization shortcut is introduced.

## Step 8. Analyze change impact

Load `app-description-change-impact`.

Expected impact result:

### Impacted authoritative layers
- `12-workstreams/functional-agents.md`
- `12-workstreams/surfaces-index.md`
- `surface-contracts/**`
- capabilities
- behavior
- auth/security
- observability
- tests
- `55-ui/` if browser realization changed

### Impacted derived layers
- traceability maps
- readiness status
- latest change summary
- generated outputs for User Admin workstream, capabilities, backend components, API, UI, tests, and traces

### Regeneration recommendation
- localized to the User Admin access-review vertical if all foundation layers already exist;
- not ready for generation if risk signals, approval policy, or runtime validation requirements remain blocking.

## Step 9. Reassess readiness

Load `app-description-readiness-assessment`.

Possible result:
- `not-ready` if risk-signal scope, approval policy, surface contracts, capability contracts, managed-agent runtime path, security tests, or local validation are underspecified.
- `ready-with-assumptions` only if remaining assumptions are explicit and do not undermine the named runtime behavior.
- `ready` only when the selected generation scope can be implemented and validated through the intended local runtime/API/UI path.

Readiness must fail if the normal user-facing workstream turn would bypass the governed Akka `Agent` runtime, if an AutonomousAgent task is used without capability authority, or if approval-gated mutations can occur through deterministic/demo/model-less shortcuts.

## Step 10. Answer the user with a readiness summary

Load `app-description-readiness-summary` and answer directly.

Example summary shape:

```md
# App Description Readiness Summary

## Current state
- not-ready for unrestricted generation
- ready only for a narrowed design pass if risk-signal scope remains open

## Why
- the User Admin workstream, surfaces, capabilities, security, observability, and tests were updated for access review
- protected remediation is approval-gated and traceable
- generation still depends on confirmed risk signals and selected local validation scope

## Key gaps or assumptions
- confirm first-version risk signals
- confirm tenant/customer scope of disabled access
- confirm whether durable investigation requires AutonomousAgent task lifecycle or a bounded request-based Agent turn is enough

## Recommendation
- answer the blocking questions or narrow the first implementation slice
- generate only after readiness confirms workstream UI, governed agent runtime, capabilities, security, traces, and local runtime/API/UI validation are complete for the selected scope
```

## Optional Step 11. User later asks for generation

If the user then says:

> ok, generate it

then the next path is:
1. reassess readiness for the selected scope;
2. run `app-generate-app` only if readiness is sufficient or the user accepts a narrowed generation scope;
3. materialize specs/backlog/pending tasks as needed;
4. implement and validate through the intended local runtime/API/UI path.

The original revision prompt must not be treated as an implicit generation request.

## What this example proves

This workflow shows that a natural user revision can be handled without:
- direct code editing;
- page-first or CRUD-first decomposition;
- hidden generation;
- bypassing governed capabilities;
- treating agent recommendations as authority to mutate protected state;
- losing audit/work trace or runtime validation obligations.

It also shows the intended value of the skill stack:
- normalization gives structure;
- routing chooses minimal next skills;
- workstream, surface, capability, and UI layers stay separate;
- change-impact prevents stale maps and generated-output assumptions;
- readiness gives a clear go/no-go/narrowing judgment.

## Minimal checklist for future workflow examples

When creating additional workflow examples, show:
1. raw user prompt;
2. normalized envelope;
3. routing decision;
4. functional-agent/workstream impact;
5. surface/action impact;
6. capability impact;
7. security, observability, and test impact;
8. UI realization impact, if any;
9. change-impact result;
10. readiness result;
11. final review response.
