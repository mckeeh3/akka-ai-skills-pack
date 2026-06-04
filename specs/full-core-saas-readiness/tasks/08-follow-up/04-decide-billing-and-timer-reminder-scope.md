# TASK-FCSR-08-004: Decide billing and timer-reminder follow-up scope

## Objective

Decide whether billing implementation and invitation timer-backed reminders remain deferred or become required implementation work for the current full-core target.

## Required reads

- `AGENTS.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `specs/full-core-saas-readiness/full-core-readiness-gap-contract.md`
- `app-description/00-system/readiness-status.md`
- `app-description/80-review/latest-readiness-summary.md`
- `specs/full-core-saas-readiness/pending-tasks.md`

## Skills

- `ai-first-saas-policy-governance`
- `akka-change-request-to-spec-update`

## In scope

- Decide and record whether billing lifecycle, entitlements, payment failure behavior, and billing UI are in scope now.
- Decide and record whether timer-backed invitation reminders are in scope now.
- If either is in scope, append bounded implementation tasks with required reads/skills/checks.
- If either remains deferred, record the accepted readiness impact.

## Out of scope

- Implementing billing or timer reminder behavior in this decision task.

## Expected outputs

- Updated queue and readiness docs if scope changes.
- Optional follow-up decision artifact.

## Required checks

- `git diff --check`
- Focused `rg` evidence for billing-boundary and timer-reminder scope/status.

## Done criteria

- Billing and timer-reminder scope are explicitly accepted, deferred, or queued as implementation tasks.
- Changes and queue update are committed.
