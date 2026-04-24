---
name: app-description-behavior-specification
description: Update the authoritative app-description behavior layer from flexible user input by capturing capabilities, rules, invariants, state transitions, edge cases, and forbidden behavior without generating code.
---

# App Description Behavior Specification

Use this skill when the user is defining or revising what the application should do.

This skill maintains the **behavior layer** of the internal application description.
It does not generate code.
It turns input into authoritative behavioral meaning that downstream generation can later realize.

## Goal

Create or update behavior-oriented app-description artifacts that:
- capture capabilities and scope clearly
- express user-visible and system-visible behavior
- define state changes, transitions, and invariants
- make forbidden behavior explicit
- separate confirmed facts from assumptions
- identify the linked test, security, and observability implications

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`

## Use this skill when

The input sounds like:
- "the app should now support..."
- "change the workflow so that..."
- "fix the behavior where..."
- "after approval, the system must..."
- "users must not be able to..."
- "when X happens, Y should happen"
- "this should be a no-op if..."

Use this skill for:
- new capabilities
- behavior revisions
- bug-fix semantics
- workflow and process rules
- invariants
- edge-case behavior
- no-op and idempotency semantics
- forbidden states and forbidden transitions

Do **not** use this skill as the primary handler when the user is only asking to generate the app.

## Core operating rule

Prefer **behavioral precision** over implementation speculation.

The job of this skill is to define:
- what must happen
- what may happen
- what must never happen
- what remains unknown

It should avoid prematurely locking in code structure unless the behavior itself requires a structural distinction.

## What this skill must capture

For each requested change, identify and describe as applicable:
- capability or business purpose
- actors or callers
- triggers, commands, or requests
- stateful concepts and lifecycle states
- valid transitions
- invalid transitions
- invariants
- success outcomes
- failure outcomes
- no-op or idempotent behavior
- ordering or timing assumptions
- dependencies on security or observability layers

## Output contract

The updated behavior specification should make it possible for a later harness step to answer:
- what changed in the app's meaning?
- what behaviors are newly required?
- what behaviors are newly forbidden?
- what tests must exist to make this unambiguous?
- which other app-description layers are now affected?

## Standard behavioral output shape

Use this response shape when updating or summarizing behavior changes:

```md
# Behavior Specification Update

## Requested change
- ...

## Capability or scope change
- ...

## Behavioral rules
- ...

## State and transitions
- ...

## Invariants
- ...

## Forbidden behavior
- ...

## No-op / idempotent behavior
- ...

## Open questions and assumptions
- ...

## Affected linked layers
- tests:
- auth/security:
- observability:
```

## Behavior modeling rules

### 1. State intent before implementation
Describe behavior in terms of outcomes, transitions, and invariants before discussing code structure.

### 2. Separate confirmed facts from assumptions
Never present an inferred behavior as confirmed if the user did not actually specify it.

### 3. Pull hidden expectations upward
If the request implies missing semantics such as idempotency, ordering, approval boundaries, or failure handling, make them explicit as assumptions or questions.

### 4. Make no-op behavior explicit
For update and retry scenarios, describe what should happen when the requested change is already true or no longer applicable.

### 5. Make forbidden behavior explicit
Do not stop at positive happy-path rules.
Capture what the system must refuse, reject, or ignore.

### 6. Flag cross-layer impact
If a behavior change implies new tests, tighter security, or more observability, say so explicitly.

## Clarification policy

Ask only the smallest questions needed to resolve material ambiguity in behavior.

Good questions include:
- "Should repeated requests be treated as idempotent success or as an error?"
- "After approval, is this field immutable or still editable?"
- "If the downstream action fails, should the request remain pending, fail permanently, or retry later?"
- "Is this rule user-visible behavior or only an internal operational expectation?"

## Handoff to other skills

When the behavior update is established, route onward as needed:
- to `app-description-test-specification` when the behavior needs concrete acceptance, regression, or edge-case tests
- to `app-description-auth-security` when the change affects authentication, authorization, trust boundaries, or sensitive-data rules
- to `app-description-observability` when the change affects logs, metrics, traces, audit events, or diagnosability
- to `app-description-readiness-assessment` only when the user is asking whether the description is ready to realize

## Anti-patterns

Avoid:
- paraphrasing the user request without turning it into actual behavioral rules
- silently choosing implementation structure as if it were behavioral truth
- omitting forbidden behavior and failure behavior
- forgetting no-op or idempotency semantics for retries and repeated requests
- treating a bug fix as only a patch instead of a corrected behavioral rule
- mixing security or observability policy into behavior without identifying the cross-layer impact

## Final review checklist

Before finishing, verify:
- the requested behavior change is restated accurately
- new or changed rules are explicit
- invariants and forbidden behavior are included when relevant
- no-op or idempotent behavior is included when relevant
- assumptions are separated from confirmed facts
- linked test, security, and observability impacts are called out
- no code-generation step was assumed

## Response style

When answering:
- summarize the behavior change first
- list rules in concise declarative form
- keep implementation speculation minimal
- call out open questions separately from confirmed behavior
- make the downstream test implications explicit
