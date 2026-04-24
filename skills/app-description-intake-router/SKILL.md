---
name: app-description-intake-router
description: Classify flexible user input into description-maintenance or generation intent, extract candidate app-description deltas, and route to the smallest next app-description skill without forcing the user to know internal taxonomy.
---

# App Description Intake Router

Use this as the front door when the user provides natural-language input about an application and the harness must decide how to respond.

This skill exists for a **description-first operating model** where the application description is the source of truth and generated code is a downstream projection.

## Goal

Interpret flexible user input and produce a routing decision that:
- defaults to maintaining the app description unless generation is explicitly requested
- detects when the user wants to change only the app description
- detects when the user wants to generate the app or run it
- consumes a normalized input envelope when available
- extracts candidate behavior, test, security, and observability deltas when normalization has not yet happened
- identifies the smallest next focused skill to load
- asks only the minimum clarification needed to avoid an incorrect next step

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-input-normalization/SKILL.md`

## Default routing rule

Prefer to use `app-description-input-normalization` first when the input is broad, mixed, or ambiguous.
If normalization has not yet occurred, this skill may perform lightweight extraction itself.

If the user does **not** explicitly ask to generate code, run the app, execute tests, or otherwise realize outputs, treat the input as:
- **change only the app description**

Generation is opt-in unless the harness is only recommending it as a possible next step.

## Primary input modes

### 1. Change only the app description
Route here when the user is:
- adding or revising capabilities
- changing rules or workflows
- clarifying behavior
- reporting a bug in expected behavior
- refining tests or edge cases
- tightening auth/security
- refining observability expectations
- asking conceptual questions about what the app should do

### 2. Generate the app
Route here only when the user explicitly asks to realize outputs, such as:
- generate the code
- run the app
- execute tests
- prepare the app for manual evaluation
- regenerate affected outputs from the current description

Example:
- "ok, now generate the code and run the app"

## Intent signals

### Strong description-maintenance signals
Examples:
- "change the behavior so..."
- "add support for..."
- "the app should..."
- "for security, require..."
- "clarify what happens when..."
- "fix the bug where..."
- "update the description to include..."

### Strong generation signals
Examples:
- "generate the app"
- "generate the code"
- "run the app"
- "run the tests"
- "build it now"
- "regenerate from the current description"

### Mixed signals
If the input contains both revision and generation requests:
1. extract the requested description changes first
2. note that generation is also requested
3. route through the relevant description-maintenance skills before generation
4. only then hand off to generation or readiness assessment

## What this skill must extract

From the user input or normalized input envelope, identify candidate deltas in these categories:
- capability or scope
- behavior and invariants
- test and example expectations
- auth/security
- observability
- generation request
- review or explanation request

Do not require perfect certainty before routing.
Use provisional extraction plus targeted clarification when needed.

## Routing rules

### If the input has not yet been normalized and is broad, mixed, or ambiguous
Load next:
- `app-description-input-normalization`
- then continue routing based on the normalized envelope

### If the input is primarily about capability scope, actors, or user-visible outcomes
Load next:
- `app-description-capability-modeling`

### If the input is primarily behavior change
Load next:
- `app-description-behavior-specification`

### If the input is primarily about examples, edge cases, or verification
Load next:
- `app-description-test-specification`

### If the input directly changes access rules, trust boundaries, or sensitive-data handling
Load next:
- `app-description-auth-security`

### If the input directly changes logs, metrics, traces, auditability, or diagnosability
Load next:
- `app-description-observability`

### If the user explicitly requests realization from the current description
Load next:
- `app-description-readiness-assessment`
- then `app-generate-app` if ready or accepted with assumptions

### If the user asks what changed or whether the description is ready
Load next as applicable:
- `app-description-change-summary`
- `app-description-readiness-summary`

## Clarification policy

Ask questions only when a wrong routing choice would create the wrong internal description update or trigger premature generation.

Good clarification questions are:
- narrow
- binary or small-choice when possible
- about missing behavior, not implementation trivia
- just enough to select the next skill safely

Examples:
- "Do you want this request to change only the app description, or should I also generate the app afterward?"
- "Is this new rule part of normal behavior, or are you mainly specifying a test/regression case?"
- "Is this requirement about access control, or is it just a general business rule?"
- "Do you want me to update observability expectations, or only user-visible behavior?"

## Output contract

Produce a routing result with these sections:
1. Input summary
2. Primary intent
3. Candidate description deltas
4. Next skill or skill sequence
5. Clarifications needed, if any
6. Generation requested now: yes/no

## Standard output template

Use this response shape internally or in structured notes:

```md
# App Description Routing

## Input summary
- ...

## Primary intent
- description-change | generate-app | mixed | review

## Candidate description deltas
- behavior:
- tests:
- auth/security:
- observability:

## Next skill or skill sequence
1. ...
2. ...

## Clarifications needed
- none
```

## Anti-patterns

Avoid:
- jumping straight to code generation on a vague prompt
- forcing the user to name a skill or internal artifact type
- treating all ambiguity as a reason to stop instead of routing provisionally
- confusing behavior rules with test-only examples when the user is clearly changing the app
- collapsing security or observability into generic behavior when they are explicitly called out
- asking broad discovery questions when a narrow route is already clear

## Final review checklist

Before finishing, verify:
- the primary intent is explicit
- generation is not assumed unless the user asked for it
- candidate behavior, test, security, and observability deltas are separated
- the next skill is the smallest focused skill that matches the request
- clarification questions are minimal and justified

## Response style

When answering:
- keep the routing summary short
- state the inferred intent explicitly
- name the next skill or skill sequence clearly
- keep the user interaction natural rather than taxonomy-driven
