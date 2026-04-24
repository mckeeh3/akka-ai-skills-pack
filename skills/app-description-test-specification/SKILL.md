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
- capture relevant security and observability verification expectations
- identify remaining ambiguity that still needs clarification

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
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

## Core operating rule

Do not think of tests as only code-level tests.
Think of them as **behavioral declarations with evidence intent**.

The purpose of this skill is to specify what must later be provable about the app.

## What this skill must capture

For each behavior or change, specify as applicable:
- acceptance scenarios
- regression scenarios
- negative scenarios
- boundary or edge scenarios
- no-op scenarios
- idempotency scenarios
- failure-path scenarios
- auth/security verification scenarios
- observability verification scenarios

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

### 6. Observability
What evidence should exist for diagnosis, audit, alerts, or operational visibility.

## Standard output shape

Use this structure when updating or summarizing the test layer:

```md
# Test Specification Update

## Behavior under test
- ...

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

## Observability verification cases
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

### 5. Include security and observability when first-class
If access control or operational evidence is part of the requested behavior, define it here explicitly.

### 6. Leave ambiguity visible
If expected behavior is still uncertain, record the ambiguity instead of inventing a test that overcommits.

## Handoff rules

Route onward as needed:
- back to `app-description-behavior-specification` if the test work reveals missing behavioral semantics
- to `app-description-auth-security` if security expectations need first-class refinement
- to `app-description-observability` if logging, metrics, traces, auditability, or alerts need explicit definition
- to `app-description-readiness-assessment` when the user is asking whether the description is complete enough to realize

## Example scenario patterns

Good patterns include:
- Given a valid request in the initial state, when the user submits it, then the system accepts it and records the new state.
- Given the request has already been applied, when the same request is repeated, then the system performs no duplicate side effect.
- Given the caller lacks permission, when they attempt the action, then the system rejects it and exposes no protected data.
- Given the downstream dependency fails, when the action is triggered, then the system preserves a diagnosable failure outcome and emits the required operational evidence.

## Anti-patterns

Avoid:
- listing only happy-path tests
- describing tests in vague prose with no scenario shape
- binding tests too tightly to likely implementation details
- forgetting regression coverage for behavior changes triggered by bugs
- omitting security or observability verification when they are part of the requested behavior
- pretending ambiguous behavior is well-defined when it is not

## Final review checklist

Before finishing, verify:
- every important behavior has at least one acceptance case
- bug-fix changes have regression cases
- invalid or forbidden paths have negative cases
- repeated-request behavior is covered where relevant
- security verification is included when relevant
- observability verification is included when relevant
- open questions are recorded instead of guessed
- no code-generation step was assumed

## Response style

When answering:
- start with the behavior under test
- list scenario groups clearly
- prefer short given/when/then style statements
- separate confirmed expectations from open questions
- keep the result useful as an authoritative input to later generation
