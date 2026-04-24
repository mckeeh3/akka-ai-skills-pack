# App Description End-to-End Workflow Example

## Purpose

This document shows a concrete end-to-end example of how a single user revision request should flow through the description-first skill stack.

It is a **process example**, not just a file-structure example.
It demonstrates how the harness should:
- interpret flexible user input
- normalize it
- route it
- update the right description layers
- analyze change impact
- reassess readiness
- optionally answer review questions without generating code

Use this together with:
- `docs/description-first-application-doctrine.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- `docs/examples/purchase-request-app-description/README.md`

## Example context

Assume the repository already contains the example app-description tree under:

```text
docs/examples/purchase-request-app-description/app-description/
```

The current example already includes:
- purchase-request capability definition
- submission and approval behavior
- acceptance, regression, negative, and operational verification
- initial auth/security
- initial observability
- a readiness status of `ready-with-assumptions`

## Example user prompt

> tighten security so only managers can approve purchase requests, add audit visibility for approvals, and once that is updated tell me if the description is ready to generate

This is a good example because it is **mixed**:
- it changes production constraints
- it implies verification changes
- it asks for a readiness review
- it does **not** explicitly ask for generation yet

## Step 1. `app-description-input-normalization`

First, normalize the prompt into a stable envelope.

Expected normalized result shape:

```md
# Normalized App Description Input

## Raw input summary
- tighten approval security, add approval audit visibility, and assess readiness afterward

## Primary intent
- mixed

## Secondary intents
- description-change
- review

## Confirmed deltas
- capabilities:
  - none explicitly changed
- behavior:
  - approval action is restricted to managers
- tests:
  - approval authorization and audit-related verification are implied
- auth/security:
  - only managers may approve purchase requests
- observability:
  - approval actions require audit visibility

## Candidate inferred deltas
- negative verification should deny non-manager approval attempts
- readiness may need reassessment because production constraints changed

## Realization request
- none

## Review request
- readiness

## Constraints and preferences
- perform readiness review after description updates

## Open questions
- should rejection be restricted to managers under the same rule?
- what exact audit fields are required for approval visibility?
```

Why this matters:
- the user did not ask for code generation
- the prompt mixes security, observability, testing, and review intent
- normalization prevents the harness from collapsing everything into a vague behavior edit

## Step 2. `app-description-intake-router`

Next, route from the normalized envelope.

Expected routing result:
- primary intent: `mixed`
- next skill sequence:
  1. `app-description-auth-security`
  2. `app-description-observability`
  3. `app-description-test-specification`
  4. `app-description-change-impact`
  5. `app-description-readiness-assessment`
  6. `app-description-readiness-summary`

Why this route:
- capability scope is not changing
- the main semantic change is auth/security
- observability is explicitly requested
- tests are implied by the requested change
- readiness review is explicitly requested
- generation is not requested

## Step 3. `app-description-auth-security`

Update the auth/security layer first because the user explicitly tightened approval permissions.

Likely affected artifact:
- `docs/examples/purchase-request-app-description/app-description/40-auth-security/identity-and-authorization.md`

Current relevant rule already says:
- manager may approve or reject submitted requests within their authorization scope

A refined update could make the rule more explicit, for example:
- only authenticated managers may approve submitted purchase requests
- approval denial behavior must be explicit for non-manager callers
- approval access must not expose protected request details beyond allowed denial semantics

The skill should also record open questions if needed, such as:
- does the same manager-only rule apply to rejection?
- are there narrower approval scopes than simply “manager”?

## Step 4. `app-description-observability`

Update observability next because the user explicitly asked for audit visibility for approvals.

Likely affected artifact:
- `docs/examples/purchase-request-app-description/app-description/50-observability/logs-metrics-traces-and-alerts.md`

Current relevant rules already say:
- emit auditable decision records for manager actions
- log approval or rejection outcome

A refined update could make approval audit visibility more explicit, for example:
- approval audit records must include request id, approver identity, decision outcome, and decision time
- failed unauthorized approval attempts may need security-audit visibility depending on later policy

This skill should avoid overcommitting if the user did not specify exact audit fields.
Those remain open questions or reasonable follow-up candidates.

## Step 5. `app-description-test-specification`

Update the test layer to make the security and observability changes explicit and verifiable.

Likely affected artifacts:
- `docs/examples/purchase-request-app-description/app-description/30-tests/negative/01-forbidden-actions.md`
- `docs/examples/purchase-request-app-description/app-description/30-tests/operational/01-audit-and-diagnosability.md`

Expected additions or refinements:

### Negative verification
- given a non-manager caller
- when they attempt to approve a submitted request
- then the action is denied

### Operational verification
- given an authorized approval decision
- when a manager approves a request
- then an audit record exists with the required approval visibility fields

This step turns the requested policy change into explicit verification expectations rather than leaving it as prose.

## Step 6. `app-description-change-impact`

Now analyze what else must move.

Expected impact result:

### Impacted authoritative layers
- behavior: approval authorization semantics are sharpened
- tests: negative and operational verification must reflect the new rule
- auth/security: approval access rules are explicitly narrowed
- observability: approval audit visibility is more specific

### Impacted derived layers
- `70-traceability/behavior-to-tests-map.md`
- `70-traceability/change-impact-map.md`
- `00-system/readiness-status.md`
- optionally `80-review/latest-change-summary.md`

### Likely affected generated outputs
- approval-facing access-control surfaces
- denial-path tests
- audit/log emission behavior
- possibly endpoint authorization behavior depending on realized architecture

### Regeneration recommendation
- likely `localized`
- because the semantic change is narrow and centered on approval authorization and approval audit visibility

Why this step matters:
- even a small policy change affects more than one layer
- without this step, stale tests or stale regeneration maps may survive

## Step 7. `app-description-readiness-assessment`

Now reassess readiness.

Possible outcome:
- `ready-with-assumptions`

Why not necessarily `ready`?
- exact audit record shape may still be underspecified
- the rejection path may still need clarification if the same manager-only rule must apply there

Why not `not-ready`?
- core behavior is already mature in the example
- the new change is localized
- the description may still be good enough for useful realization, as long as assumptions are surfaced

A good readiness result here would say:
- behavior completeness: acceptable
- test completeness: acceptable if negative and operational cases were updated
- auth/security completeness: mostly acceptable, with any role-scope assumptions surfaced
- observability completeness: acceptable for evaluation if exact audit schema is not yet critical

## Step 8. `app-description-readiness-summary`

Because the user asked whether the description is ready to generate, the harness should answer directly in prompt/response form.

Example summary shape:

```md
# App Description Readiness Summary

## Current state
- ready-with-assumptions

## Why
- the purchase-request description is already structurally complete
- the new revision tightened approval authorization and approval audit visibility without changing the broader capability set
- linked negative and operational verification can be updated locally

## Key gaps or assumptions
- approval audit fields may still need one more pass if strict audit schema is required
- confirm whether manager-only restriction applies equally to rejection

## Recommendation
- continue description work only if you want stricter approval-audit specificity
- otherwise the description is sufficient for a useful generated evaluation build

## Suggested next step
- if you want, explicitly ask to generate the app from the current description
```

## Optional Step 9. User now asks for generation

If the user then says:

> ok, generate the app

then the next path is:
1. `app-description-readiness-assessment` confirm current state
2. `app-generate-app`
3. optional run/test/evaluation steps
4. generation summary

Important rule:
- the harness should not treat the original prompt as a hidden generation request
- generation begins only after explicit user request or accepted harness recommendation

## What this example proves

This workflow shows that a natural user revision can be handled without:
- direct code editing
- direct internal-doc editing by humans
- premature generation
- collapsing all changes into one vague “update the spec” step

It also shows the intended value of the skill stack:
- normalization gives structure
- routing chooses the smallest next steps
- layer-specific skills update the right internal artifacts
- change-impact prevents stale links and stale outputs
- readiness produces a clear go/no-go judgment
- review remains prompt/response oriented

## Minimal checklist for future workflow examples

When creating additional workflow examples, show:
1. the raw user prompt
2. the normalized envelope
3. the routing decision
4. the layer-specific updates
5. the change-impact result
6. the readiness result
7. the final review response

That is the minimum path needed to validate the description-first operating model in practice.
