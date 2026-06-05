---
name: app-description-test-specification
description: Update the authoritative test layer of the app description by turning intended behavior into explicit acceptance, regression, edge-case, negative, idempotency, security, and observability expectations without generating code.
---

# App Description Test Specification

Use this skill when intended behavior needs to be made more precise through tests, examples, and verification expectations.

This skill treats tests as part of the **authoritative application description**, not as a post-implementation afterthought.

## Goal

Create or update the test layer of the app description so that behavior becomes more unambiguous through explicit verification expectations.

The output should:
- define acceptance behavior clearly
- capture regressions and bug-fix expectations
- include edge cases and negative cases
- make no-op and idempotency behavior testable
- capture mandatory secure SaaS foundation verification expectations, including tenant isolation, forbidden access, disabled users, role/scope denial, `/api/me`, audit, and frontend secret-boundary tests
- capture relevant security and observability verification expectations
- preserve AI-first verification needs for generated SaaS delegated work, agent judgment, approvals, policies, traces, evaluations, and outcomes
- identify remaining ambiguity that still needs clarification

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md` for capability-level success, validation, forbidden, tenant-isolation, idempotency, audit, approval, and exposure-surface test expectations
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS foundation verification expectations
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`

## Use this skill when

The input sounds like:
- "add tests for this behavior"
- "make this unambiguous"
- "capture the regression"
- "what tests should define this feature?"
- "clarify the edge cases"
- "express this as acceptance criteria"

Use it for:
- acceptance cases
- regression cases
- boundary cases
- invalid input handling
- no-op behavior
- idempotency behavior
- authorization/security expectations
- observability verification expectations
- agent output, evaluator, guardrail, policy, approval, trace, and outcome-loop verification expectations for generated AI-first SaaS semantics

## Core operating rule

Do not think of tests as only code-level tests.
Think of them as **behavioral declarations with evidence intent**.

The purpose of this skill is to specify what must later be provable about the app.

## What this skill must capture

For each behavior, capability, or change, specify as applicable:
- linked capability id/class and exposure surfaces under test
- acceptance scenarios
- regression scenarios
- negative scenarios
- boundary or edge scenarios
- no-op scenarios
- idempotency scenarios
- failure-path scenarios
- baseline secure SaaS foundation scenarios for Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, Invitation, AuthContext, `/api/me`, backend authorization, support-access, billing boundary, AdminAuditEvent, and tenant/customer-scoped commands and queries
- auth/security verification scenarios
- observability verification scenarios
- capability contract verification: authorized AuthContext success, validation failure, forbidden/tenant isolation, duplicate/idempotent calls, audit/work-trace emission, approval/escalation behavior, and surface-specific UI/API/tool/MCP/workflow/timer/consumer behavior

## Test specification categories

### 1. Acceptance
The normal successful behavior the user expects.

### 2. Regression
The previously broken or ambiguous behavior that must now remain correct.

### 3. Negative
Invalid or forbidden requests that must fail, reject, or be ignored correctly.

### 4. No-op and idempotency
Repeated or obsolete requests that should not create unintended side effects.

### 5. Security
Authentication, authorization, and restricted-access expectations.

For generated SaaS apps this category is mandatory baseline coverage, not a conditional add-on. Include:
- tenant isolation for commands, queries, views, streams, tools, workflow actions, consumers, timers, and generated UI access
- forbidden access and resource-hiding behavior where applicable
- disabled-user and inactive-membership denial
- role/scope/permission denial for every protected operation family
- `/api/me` payload, selected AuthContext, context switching, and browser-safe capability display
- invitation, admin bootstrap, Membership/role assignment, support-access grant/revoke/expiry, and billing-boundary cases
- AdminAuditEvent emission for identity, membership, role, support-access, billing-boundary, policy, approval, data-access, and consequential AI/tool activity
- frontend secret-boundary tests proving provider/server secrets are not emitted to browser assets or client-visible API payloads

### 6. Observability
What evidence should exist for diagnosis, audit, alerts, or operational visibility.

### 7. AI-first evaluation and governance
When delegated work or agentic judgment is in scope, define how to prove:
- agent responsibilities and authority boundaries are respected
- policies, permissions, thresholds, guardrails, and approval gates are enforced mechanically
- recommendations include required evidence, confidence, risk, impact, alternatives, and rationale
- human approval, rejection, override, escalation, exception, and feedback paths behave correctly
- work traces, decision traces, policy invocations, tool calls, data access, and outcome links are emitted
- evaluator or regression checks cover quality, safety, drift, and learning-loop behavior where relevant

## Standard output shape

Use this structure when updating or summarizing the test layer:

```md
# Test Specification Update

## Behavior or capability under test
- linked capability id/class:
- exposure surfaces:

## Acceptance cases
- ...

## Regression cases
- ...

## Negative cases
- ...

## No-op / idempotency cases
- ...

## Security verification cases
- ...

## Observability / trace verification cases
- ...

## AI-first evaluation and outcome cases
- ...

## Open questions and assumptions
- ...
```

## Test authoring rules

### 1. Tie tests to behavior, not implementation files
Describe what must be proven, not which class or helper probably implements it.

### 2. Prefer explicit scenario language
A good test statement says:
- given what context
- when what action happens
- then what must be true

### 3. Cover forbidden and repeated behavior
Do not stop at happy-path acceptance.
Include rejected behavior, repeated commands, obsolete callbacks, and retry situations where relevant.

### 4. Add regression tests for every bug-fix semantic change
If a bug triggered the change, capture the old failure mode as a regression expectation.

### 5. Include secure foundation, security, and observability baseline tests
For generated SaaS apps, secure foundation tests are mandatory even when the feature request is app-specific. Do not treat tenant isolation, forbidden access, disabled users, role/scope denial, `/api/me`, audit, or frontend secret-boundary checks as polish.

If additional access control or operational evidence is part of the requested behavior, define it here explicitly.

### 6. Test delegated authority, not just generated answers
For AI-first behavior, include tests that prove what the agent/system may do, what it must only recommend, what requires approval, and what must be denied or escalated.
Do not accept prompt wording as the only safety evidence.

### 7. Include evaluation and outcome loops where meaningful
When recommendations, classifications, summaries, policy proposals, or autonomous actions affect outcomes, specify evaluator, regression, feedback, replay/simulation, and later-outcome verification expectations.

### 8. Leave ambiguity visible
If expected behavior is still uncertain, record the ambiguity instead of inventing a test that overcommits.

## Handoff rules

Route onward as needed:
- back to `app-description-capability-modeling` if the test work reveals missing capability actors, AuthContext, schemas, side effects, idempotency, approval, audit, or exposure surfaces
- back to `app-description-behavior-specification` if the test work reveals missing behavioral semantics
- to `app-description-auth-security` if security expectations need first-class refinement
- to `app-description-ui` if browser action, supervision, decision, governance, digest, audit, realtime, accessibility, or frontend API behavior must be specified for verification
- to `app-description-observability` if logging, metrics, work traces, decision traces, auditability, or alerts need explicit definition
- to AI-first companion skills when tests reveal missing agent authority, policy, decision-card, audit-trace, UI-surface, evaluation, or outcome-loop semantics
- to `app-description-readiness-assessment` when the user is asking whether the description is complete enough to realize

## Example scenario patterns

Good patterns include:
- Given a disabled user with a formerly valid Membership, when they call any protected route, view query, stream, agent tool, workflow action, or generated UI action, then the system denies access and emits the required AdminAuditEvent without exposing protected data.
- Given a user with Membership in tenant A only, when they attempt to read or mutate tenant B or customer B data, then the system rejects the request mechanically and no cross-tenant data appears in responses, views, streams, traces, or logs.
- Given the browser requests `/api/me`, when the user is authenticated, then the response contains only browser-safe Account/Profile/Settings, Membership, selected AuthContext, and capabilities and never exposes backend secrets or unauthorized scopes.
- Given a valid request in the initial state, when the user submits it, then the system accepts it and records the new state.
- Given the request has already been applied, when the same request is repeated, then the system performs no duplicate side effect.
- Given the caller lacks permission, when they attempt the action, then the system rejects it and exposes no protected data.
- Given the downstream dependency fails, when the action is triggered, then the system preserves a diagnosable failure outcome and emits the required operational evidence.
- Given an agent recommendation exceeds the configured risk threshold, when the recommendation is produced, then the system creates an approval or exception case rather than committing the action autonomously.
- Given a human overrides an agent recommendation, when the decision is recorded, then the trace links the evidence, policy clause, reviewer, rationale, and later outcome measurement.

## Anti-patterns

Avoid:
- listing only happy-path tests
- describing tests in vague prose with no scenario shape
- binding tests too tightly to likely implementation details
- forgetting regression coverage for behavior changes triggered by bugs
- omitting mandatory secure SaaS foundation verification for generated apps
- omitting security or observability verification when they are part of the requested behavior
- testing only LLM text quality while ignoring authority boundaries, policy enforcement, trace emission, and outcome links
- pretending ambiguous behavior is well-defined when it is not

## Final review checklist

Before finishing, verify:
- every important behavior has at least one acceptance case
- bug-fix changes have regression cases
- invalid or forbidden paths have negative cases
- repeated-request behavior is covered where relevant
- mandatory secure SaaS foundation tests are present for generated SaaS apps, including tenant isolation, forbidden access, disabled user, role/scope denial, `/api/me`, audit, and frontend secret-boundary tests
- security verification is included when relevant
- observability verification is included when relevant
- AI-first evaluation, policy, permission, approval, trace, and outcome cases are included when relevant
- open questions are recorded instead of guessed
- no code-generation step was assumed

## Response style

When answering:
- start with the behavior under test
- list scenario groups clearly
- prefer short given/when/then style statements
- separate confirmed expectations from open questions
- keep the result useful as an authoritative input to later generation
