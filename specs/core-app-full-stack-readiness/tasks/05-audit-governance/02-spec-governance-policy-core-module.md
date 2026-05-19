# TASK-CORE-05-002: Specify Governance/Policy core module

## Purpose

Create concrete contracts for governance proposals, evaluation findings, approvals, activation, rollback, authority-expansion denial, and decision-card surfaces.

## Required reads

- `docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md`
- `skills/ai-first-saas-policy-governance/SKILL.md`
- `skills/ai-first-saas-decision-cards/SKILL.md`
- `skills/akka-agent-closed-loop-improvement/SKILL.md`
- `specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md`
- `specs/core-app-full-stack-readiness/audit-trace-core-module-slice.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/governance-policy-core-module-slice.md`

## Required checks

- Slice connects evaluator findings and behavior-edit proposals to review, approval/rejection, activation, rollback, trace evidence, and workstream decision cards.
- `git diff --check`

## Done criteria

- Governance/Policy is generation-ready as a core module.
- Queue status and changes are committed.
